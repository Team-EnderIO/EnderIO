package com.enderio.base.api.soul;

import com.enderio.base.api.attachment.StoredEntityData;
import java.util.function.Supplier;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;

public class SingleSoulStorageItemStack implements SingleSoulStorage {

    protected final Supplier<DataComponentType<StoredEntityData>> componentType;
    protected ItemStack container;

    public SingleSoulStorageItemStack(Supplier<DataComponentType<StoredEntityData>> componentType,
            ItemStack container) {
        this.componentType = componentType;
        this.container = container;
    }

    @Override
    public StoredEntityData getSoul() {
        return container.getOrDefault(componentType, StoredEntityData.EMPTY);
    }

    @Override
    public void setSoul(StoredEntityData soul) {
        if (soul.hasEntity()) {
            container.set(componentType, soul);
        } else {
            container.remove(componentType);
        }
    }
}
