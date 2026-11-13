package com.enderio.enderio.api.armory.capacitor;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.MutableDataComponentHolder;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Experimental
public class ComponentCapacitorSocket implements CapacitorSocket {
    protected final MutableDataComponentHolder parent;
    protected final DataComponentType<SocketedCapacitor> capacitorComponent;

    public ComponentCapacitorSocket(MutableDataComponentHolder parent, DataComponentType<SocketedCapacitor> capacitorComponent) {
        this.parent = parent;
        this.capacitorComponent = capacitorComponent;
    }

    @Override
    public ItemStack getCapacitor() {
        return parent.getOrDefault(capacitorComponent, SocketedCapacitor.EMPTY).capacitor();
    }

    @Override
    public void setCapacitor(ItemStack capacitor) {
        if (capacitor.isEmpty()) {
            parent.remove(capacitorComponent);
        } else {
            parent.set(capacitorComponent, new SocketedCapacitor(capacitor));
        }
    }
}
