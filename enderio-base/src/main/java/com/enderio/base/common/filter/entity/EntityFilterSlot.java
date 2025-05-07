package com.enderio.base.common.filter.entity;

import com.enderio.base.api.attachment.StoredEntityData;
import com.enderio.base.common.init.EIODataComponents;
import com.enderio.base.common.filter.FilterSlot;
import com.enderio.base.common.tag.EIOTags;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;

public class EntityFilterSlot extends FilterSlot<StoredEntityData> {

    public EntityFilterSlot(Supplier<StoredEntityData> getter, Consumer<StoredEntityData> setter, int pSlot, int pX, int pY) {
        super(getter, setter, pSlot, pX, pY);
    }

    @Override
    protected StoredEntityData emptyResource() {
        return StoredEntityData.EMPTY;
    }

    @Override
    public Optional<StoredEntityData> getResourceFrom(ItemStack itemStack) {
        if (itemStack.is(EIOTags.Items.ENTITY_STORAGE)) {
            StoredEntityData ghost = itemStack.getOrDefault(EIODataComponents.STORED_ENTITY, StoredEntityData.EMPTY);
            return Optional.of(ghost);
        } else if (itemStack.getItem() instanceof SpawnEggItem spawnEggItem) {
            Entity entity = spawnEggItem.getType(itemStack).create(Minecraft.getInstance().level);
            if (entity instanceof LivingEntity livingEntity) {
                StoredEntityData ghost = new StoredEntityData(
                        livingEntity.serializeNBT(Minecraft.getInstance().level.registryAccess()),
                        livingEntity.getMaxHealth());
                return Optional.of(ghost);
            }
        }

        return Optional.empty();
    }
}
