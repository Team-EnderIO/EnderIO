package com.enderio.base.common.menu;

import com.enderio.base.api.attachment.StoredEntityData;
import com.enderio.base.common.init.EIODataComponents;
import com.enderio.base.common.tag.EIOTags;
import net.minecraft.client.Minecraft;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;

import java.util.Optional;
import java.util.function.Consumer;

public class EntityFilterSlot extends FilterSlot<StoredEntityData> {

    public EntityFilterSlot(Consumer<StoredEntityData> consumer, int pSlot, int pX, int pY) {
        super(consumer, pSlot, pX, pY);
    }

    @Override
    protected Optional<StoredEntityData> getResourceFrom(ItemStack itemStack) {
        if (itemStack.is(EIOTags.Items.ENTITY_STORAGE)) {
            StoredEntityData ghost = itemStack.get(EIODataComponents.STORED_ENTITY);
            return Optional.of(ghost);
        } else if (itemStack.getItem() instanceof SpawnEggItem spawnEggItem) {
            Entity entity = spawnEggItem.getType(itemStack).create(Minecraft.getInstance().level);
            if (entity instanceof LivingEntity livingEntity) {
                StoredEntityData ghost = new StoredEntityData(livingEntity.serializeNBT(Minecraft.getInstance().level.registryAccess()),
                    livingEntity.getMaxHealth());
                return Optional.of(ghost);
            }
        }

        return Optional.empty();
    }
}
