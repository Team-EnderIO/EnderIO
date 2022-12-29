package com.enderio.machines.common.io.item;

import java.util.ArrayList;
import java.util.List;

public class MultiSlotAccess {
    private List<SingleSlotAccess> accesses = null;

    void init(int index, int size) {
        List<SingleSlotAccess> accesses = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            SingleSlotAccess access = new SingleSlotAccess();
            access.init(i + index);
            accesses.add(access);
        }
        this.accesses = accesses;
    }

    public int size() {
        return accesses.size();
    }

    public SingleSlotAccess get(int index) {
        return accesses.get(index);
    }

    public int getStartIndex() {
        return accesses.get(0).getIndex();
    }
}
