package tpiskorski.machinator.model.watchdog;

import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.util.Callback;
import tpiskorski.machinator.model.server.Server;
import tpiskorski.machinator.model.vm.VirtualMachine;

import java.util.Objects;

public class Watchdog {

    private VirtualMachine virtualMachine;
    private Server watchdogServer;
    private BooleanProperty active = new SimpleBooleanProperty();

    public Watchdog(VirtualMachine virtualMachine, Server watchdogServer) {
        this.virtualMachine = virtualMachine;
        this.watchdogServer = watchdogServer;
        setActive(false);
    }

    static Callback<Watchdog, Observable[]> extractor() {
        return (Watchdog watchdog) -> new Observable[]{
            watchdog.activeProperty()
        };
    }

    public boolean isActive() {
        return active.get();
    }

    public void setActive(boolean active) {
        this.active.set(active);
    }

    public BooleanProperty activeProperty() {
        return active;
    }

    public VirtualMachine getVirtualMachine() {
        return virtualMachine;
    }

    public Server getWatchdogServer() {
        return watchdogServer;
    }

    public void setWatchdogServer(Server watchdogServer) {
        this.watchdogServer = watchdogServer;
    }

    @Override public int hashCode() {
        return Objects.hash(getVirtualMachine(), getWatchdogServer());
    }

    @Override public boolean equals(Object obj) {
        if (!(obj instanceof Watchdog)) {
            return false;
        }
        Watchdog that = (Watchdog) obj;

        return Objects.equals(this.getVirtualMachine(), that.getVirtualMachine())
            && Objects.equals(this.getWatchdogServer(), that.getWatchdogServer());
    }

    public String id() {
        if (watchdogServer != null) {
            return virtualMachine.toString() + "-" + watchdogServer.getAddress();
        }
        return virtualMachine.toString();
    }
}
