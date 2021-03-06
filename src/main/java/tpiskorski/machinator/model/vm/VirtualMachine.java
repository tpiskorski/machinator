package tpiskorski.machinator.model.vm;

import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.util.Callback;
import tpiskorski.machinator.model.server.Server;

import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class VirtualMachine {

    private String id;
    private Server server;

    private StringProperty vmName = new SimpleStringProperty();
    private IntegerProperty cpuCores = new SimpleIntegerProperty();
    private IntegerProperty ramMemory = new SimpleIntegerProperty();
    private ObjectProperty<VirtualMachineState> state = new SimpleObjectProperty<>();
    private VirtualMachineType type = VirtualMachineType.REGULAR;
    private Lock lock = new ReentrantLock();

    public VirtualMachine() {
    }

    public VirtualMachine(Server server, String id) {
        setServer(server);
        setId(id);
        setState(VirtualMachineState.UNREACHABLE);
    }

    public static VirtualMachine placeholderFor(VirtualMachine vm, Server destination) {
        VirtualMachine placeholder = new VirtualMachine();
        placeholder.setServer(destination);
        placeholder.setVmName(vm.getVmName());
        placeholder.setCpuCores(vm.getCpuCores());
        placeholder.setRamMemory(vm.getRamMemory());
        placeholder.setType(VirtualMachineType.PLACEHOLDER);
        return placeholder;
    }

    static Callback<VirtualMachine, Observable[]> extractor() {
        return (VirtualMachine vm) -> new Observable[]{vm.vmNameProperty(), vm.cpuCoresProperty(), vm.ramMemoryProperty()};
    }

    public VirtualMachineType getType() {
        return type;
    }

    public void setType(VirtualMachineType type) {
        this.type = type;
    }

    public void lock() {
        lock.lock();
        setState(VirtualMachineState.COMMAND_IN_PROGRESS);
    }

    public boolean tryLockingForRefresh() {
        boolean locked = lock.tryLock();
        if (locked) {
            setState(VirtualMachineState.REFRESH_IN_PROGRESS);
        }
        return locked;
    }

    public void unlock() {
        if (getState() == VirtualMachineState.COMMAND_IN_PROGRESS || getState() == VirtualMachineState.REFRESH_IN_PROGRESS) {
            setState(VirtualMachineState.WAITING_FOR_REFRESH);
        }
        lock.unlock();
    }

    public String getServerAddress() {
        return server.getSimpleAddress();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public VirtualMachineState getState() {
        return state.get();
    }

    public void setState(VirtualMachineState state) {
        this.state.set(state);
    }

    public ObjectProperty<VirtualMachineState> stateProperty() {
        return state;
    }

    public String getVmName() {
        return vmName.get();
    }

    public void setVmName(String vmName) {
        this.vmName.set(vmName);
    }

    public StringProperty vmNameProperty() {
        return vmName;
    }

    public int getCpuCores() {
        return cpuCores.get();
    }

    public void setCpuCores(int cpuCores) {
        this.cpuCores.set(cpuCores);
    }

    public IntegerProperty cpuCoresProperty() {
        return cpuCores;
    }

    public int getRamMemory() {
        return ramMemory.get();
    }

    public void setRamMemory(int ramMemory) {
        this.ramMemory.set(ramMemory);
    }

    public IntegerProperty ramMemoryProperty() {
        return ramMemory;
    }

    @Override public int hashCode() {
        return Objects.hash(getServer(), getId());
    }

    @Override public boolean equals(Object obj) {
        if (!(obj instanceof VirtualMachine)) {
            return false;
        }
        VirtualMachine that = (VirtualMachine) obj;

        return Objects.equals(this.getServer(), that.getServer())
            && Objects.equals(this.getId(), that.getId());
    }

    public boolean deepEquals(VirtualMachine that) {
        return Objects.equals(this.getServer(), that.getServer())
            && Objects.equals(this.getId(), that.getId())
            && Objects.equals(this.getState(), that.getState())
            && Objects.equals(this.getCpuCores(), that.getCpuCores())
            && Objects.equals(this.getRamMemory(), that.getRamMemory());
    }

    @Override
    public String toString() {
        return String.format("vm[%s at %s]", getVmName(), getServer());
    }
}
