package com.enderio.base.common.menu;

import com.enderio.api.capability.IEntityStorage;
import com.enderio.api.capability.StoredEntityData;
import com.enderio.base.common.init.EIOCapabilities;
import com.enderio.core.common.menu.FilterSlot;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraftforge.common.util.LazyOptional;

import java.util.Optional;
import java.util.function.Consumer;

public class EntityFilterSlot extends FilterSlot<StoredEntityData> {

    public EntityFilterSlot(Consumer<StoredEntityData> consumer, int pSlot, int pX, int pY) {
        super(consumer, pSlot, pX, pY);
    }

    @Override
    public Optional<StoredEntityData> getResourceFrom(ItemStack itemStack) {

        LazyOptional<IEntityStorage> entityStorageOptional = itemStack.getCapability(EIOCapabilities.ENTITY_STORAGE);
        if (entityStorageOptional.isPresent()) {
            IEntityStorage storage = entityStorageOptional.resolve().get();
            return Optional.of(storage.getStoredEntityData());
        }

        if (itemStack.getItem() instanceof SpawnEggItem spawnEggItem) {
            Entity entity = spawnEggItem.getType(itemStack.getTag()).create(Minecraft.getInstance().level);
            if (entity instanceof LivingEntity livingEntity) {
                StoredEntityData ghost = new StoredEntityData(
                    livingEntity.serializeNBT(),
                    livingEntity.getMaxHealth());
                return Optional.of(ghost);
            }
        }

        return Optional.empty();
    }
}
