package tpiskorski.machinator.core.vm;

public enum VirtualMachineState {
    COMMAND_IN_PROGRESS,

    RUNNING,
    POWEROFF,
    UNREACHABLE;

    public static VirtualMachineState parse(String vmState) {
        if (vmState.equals("\"poweroff\"")) {
            return POWEROFF;
        } else if (vmState.equals("\"running\"")) {
            return RUNNING;
        } else {
            return UNREACHABLE;
        }
    }
}
