package tpiskorski.machinator.flow.quartz.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tpiskorski.machinator.config.ConfigService;
import tpiskorski.machinator.flow.executor.ExecutionException;
import tpiskorski.machinator.model.backup.BackupDefinition;
import tpiskorski.machinator.model.vm.VirtualMachine;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Optional;

@Service
public class BackupService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BackupService.class);

    private final ConfigService configService;

    @Autowired
    public BackupService(ConfigService configService) {
        this.configService = configService;
    }

    public long getBackupCount(BackupDefinition backupDefinition) {
        return getBackupCount(backupDefinition.getVm());
    }

    public long getBackupCount(VirtualMachine vm) {
        try {
            File location = getBackupLocation(vm);
            if (!location.isDirectory() && !location.mkdirs()) {
                throw new ExecutionException(String.format("Could not create directory %s", location));
            }

            return Files.list(location.toPath()).count();
        } catch (IOException e) {
            LOGGER.error("Backup job failed", e);
            throw new ExecutionException(e);
        }
    }

    private File getBackupLocation(VirtualMachine vm) {
        return new File(configService.getConfig().getBackupLocation() + "/"
            + vm.getServer().getAddress() + "/"
            + vm.getVmName());
    }

    public void assertBackupCount(BackupDefinition backupDefinition) {
        long count = getBackupCount(backupDefinition);

        if (count >= backupDefinition.getFileLimit()) {
            LOGGER.error("Backup job failed. File limit exceeded");
            throw new ExecutionException("File limit exceeded");
        }
    }

    public File getBackupLocation(BackupDefinition backupDefinition) {
        return new File(configService.getConfig().getBackupLocation() + "/"
            + backupDefinition.getServer().getAddress() + "/"
            + backupDefinition.getVm().getVmName());
    }

    public String getBackupPath(BackupDefinition backupDefinition) {
        return getBackupLocation(backupDefinition) + "/" + getNextBackupName(backupDefinition);
    }

    public String getNextBackupName(BackupDefinition backupDefinition) {
        long count = getBackupCount(backupDefinition);
        return "backup_" + (count + 1) + ".ova";
    }

    public String getRemoteTemporaryFilePath(VirtualMachine vm) {
        String vmName = vm.getVmName();
        return "~/temp_" + vmName + ".ova";
    }

    public String getLocalTemporaryFilePath(VirtualMachine vm) {
        String vmName = vm.getVmName();
        return System.getProperty("user.home") + File.separator + "temp_" + vmName + ".ova";
    }

    public String findLatestBackup(VirtualMachine vm) throws IOException {
        File location = getBackupLocation(vm);

        Optional<Path> latestBackup = Files.list(location.toPath())
            .filter(Files::isRegularFile)
            .max(Comparator.comparingLong(f -> f.toFile().lastModified()));

        return latestBackup.get().toString();
    }
}
