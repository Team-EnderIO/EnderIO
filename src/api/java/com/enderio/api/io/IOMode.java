package com.enderio.api.io;

public enum IOMode {
    /**
     * No specific configuration, allows external input and output but doesn't pull or push itself.
     */
    NONE(true, true, false, true),

    /**
     * Only pushes outputs allows both external pulling and the machine pushes itself.
     *
     * For example conduits can pull themselves, however putting a chest next to the machine will cause it to push items to the chest.
     *
     * @apiNote Each machine determines what this means this for energy. Some may ignore it.
     */
    PUSH(false, true, true, true),

    /**
     * Only pulls inputs, allowing both external pushing and the machine pulling itself.
     *
     * For example conduits can push into the machine themselves, but a chest next to the machine will also be pulled from.
     *
     * @apiNote Each machine determines what this means this for energy. Some may ignore it.
     */
    PULL(true, false, true, true),

    /**
     * Allow both pulling and pushing by both the machine and external blocks.
     *
     * @apiNote Each machine determines what this means this for energy. Some may ignore it.
     */
    BOTH(true, true, true, true),

    /**
     * Disallow any side access for all resources (including energy).
     *
     * @apiNote All machines will disallow power access for this side.
     */
    DISABLED(false, false, false, false);

    private final boolean input, output, force, canConnect;

    IOMode(boolean input, boolean output, boolean force, boolean canConnect) {
        this.input = input;
        this.output = output;
        this.force = force;
        this.canConnect = canConnect;
    }

    /**
     * Can resources be input via this side.
     */
    public boolean canInput() {
        return input;
    }

    /**
     * Can resources be output via this side.
     */
    public boolean canOutput() {
        return output;
    }

    /**
     * Whether or not this side can be connected to by external blocks.
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean canConnect() {
        return canConnect;
    }

    /**
     * Can resources be pushed out this side.
     *
     * @implNote This can be used by machines to determine if it should push resources out.
     */
    public boolean canPush() {
        return canOutput() && canForce();
    }

    /**
     * Can resources be pulled in this side.
     *
     * @implNote This can be used by machines to determine if it should pull resources in.
     */
    public boolean canPull() {
        return canInput() && canForce();
    }

    /**
     * Whether the machine can force resources in/out this side.
     */
    public boolean canForce() {
        return force;
    }
}
