package tpiskorski.machinator.flow.quartz.vm.job;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import tpiskorski.machinator.flow.command.BaseCommand;
import tpiskorski.machinator.flow.command.CommandFactory;
import tpiskorski.machinator.flow.command.CommandResult;
import tpiskorski.machinator.flow.executor.CommandExecutor;
import tpiskorski.machinator.flow.executor.ExecutionContext;
import tpiskorski.machinator.flow.executor.poll.PollExecutor;
import tpiskorski.machinator.flow.parser.ProgressCommandsInterpreter;
import tpiskorski.machinator.flow.parser.ShowVmStateParser;
import tpiskorski.machinator.flow.parser.SimpleVmParser;
import tpiskorski.machinator.model.vm.VirtualMachine;
import tpiskorski.machinator.model.vm.VirtualMachineService;
import tpiskorski.machinator.model.vm.VirtualMachineState;

import java.io.IOException;
import java.util.List;

@Component
public class VmDeleteJob extends QuartzJobBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(VmDeleteJob.class);

    private final CommandExecutor commandExecutor;
    private final CommandFactory commandFactory;

    @Autowired private VirtualMachineService virtualMachineService;

    private ProgressCommandsInterpreter progressCommandsInterpreter = new ProgressCommandsInterpreter();
    private PollExecutor pollExecutor = new PollExecutor();
    private ShowVmStateParser showVmStateParser = new ShowVmStateParser();

    private SimpleVmParser simpleVmParser = new SimpleVmParser();

    @Autowired
    public VmDeleteJob(CommandExecutor commandExecutor, CommandFactory commandFactory) {
        this.commandExecutor = commandExecutor;
        this.commandFactory = commandFactory;
    }

    @Override protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        JobDataMap mergedJobDataMap = context.getMergedJobDataMap();
        VirtualMachine vm = (VirtualMachine) mergedJobDataMap.get("vm");
        LOGGER.info("Started for {}-{}", vm.getServerAddress(), vm.getVmName());

        vm.lock();
        try {
            powerOffIfRunning(vm);
            deleteVm(vm);
            checkIfDeleted(vm);
        } catch (IOException | InterruptedException e) {
            LOGGER.error("VmDeleteJob job failed", e);
            throw new JobExecutionException(e);
        } finally {
            vm.unlock();
        }
    }

    private void deleteVm(VirtualMachine vm) throws IOException, InterruptedException, JobExecutionException {
        ExecutionContext deleteVm = ExecutionContext.builder()
            .executeOn(vm.getServer())
            .command(commandFactory.makeWithArgs(BaseCommand.DELETE_VM, vm.getVmName()))
            .build();

        ExecutionContext infoVm = ExecutionContext.builder()
            .executeOn(vm.getServer())
            .command(commandFactory.makeWithArgs(BaseCommand.SHOW_VM_INFO, vm.getId()))
            .build();

        CommandResult result = commandExecutor.execute(deleteVm);
        if (!progressCommandsInterpreter.isSuccess(result)) {
            vm.setState(showVmStateParser.parse(commandExecutor.execute(infoVm)));
            throw new JobExecutionException(result.getError());
        }
    }

    private void powerOffIfRunning(VirtualMachine vm) throws IOException, InterruptedException, JobExecutionException {
        ExecutionContext turnOff = ExecutionContext.builder()
            .executeOn(vm.getServer())
            .command(commandFactory.makeWithArgs(BaseCommand.TURN_OFF, vm.getVmName()))
            .build();

        ExecutionContext infoVm = ExecutionContext.builder()
            .executeOn(vm.getServer())
            .command(commandFactory.makeWithArgs(BaseCommand.SHOW_VM_INFO, vm.getId()))
            .build();

        if (showVmStateParser.parse(commandExecutor.execute(infoVm)) == VirtualMachineState.RUNNING) {
            CommandResult result = commandExecutor.execute(turnOff);

            if (!progressCommandsInterpreter.isSuccess(result)) {
                throw new JobExecutionException(result.getError());
            }

            pollExecutor.pollExecute(() -> showVmStateParser.parse(commandExecutor.execute(infoVm)) == VirtualMachineState.POWEROFF);
        }
    }

    private void checkIfDeleted(VirtualMachine vm) throws IOException, InterruptedException, JobExecutionException {
        ExecutionContext listVms = ExecutionContext.builder()
            .executeOn(vm.getServer())
            .command(commandFactory.makeWithArgs(BaseCommand.LIST_ALL_VMS, vm.getId()))
            .build();

        CommandResult result = commandExecutor.execute(listVms);
        List<VirtualMachine> vms = simpleVmParser.parse(result);
        if (!vms.contains(vm)) {
            virtualMachineService.remove(vm);
        } else {
            throw new JobExecutionException("Could not delete vm");
        }
    }
}