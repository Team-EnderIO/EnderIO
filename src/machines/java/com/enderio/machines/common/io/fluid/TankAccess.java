package com.enderio.machines.common.io.fluid;

public class TankAccess {

    private int index = Integer.MIN_VALUE;

    void init(int i) {
        if (index == Integer.MIN_VALUE) {
            index = i;
        } else if (index != i) {
            throw new IllegalArgumentException("TankLayout changed dynamically from " + index + " to " + i + ", don't do that");
        }
    }
}
