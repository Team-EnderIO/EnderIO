package com.enderio.base.common.filter.soul;

import com.enderio.base.api.attachment.Soul;
import com.enderio.base.common.init.EIOCapabilities;
import com.enderio.base.common.filter.FilterSlot;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;

public class SoulFilterSlot extends FilterSlot<Soul> {

    public SoulFilterSlot(Supplier<Soul> getter, Consumer<Soul> setter, int pSlot, int pX, int pY) {
        super(getter, setter, pSlot, pX, pY);
    }

    @Override
    protected Soul emptyResource() {
        return Soul.EMPTY;
    }

    @Override
    public Optional<Soul> getResourceFrom(ItemStack itemStack) {
        var soulStorage = itemStack.getCapability(EIOCapabilities.SingleSoulStorage.ITEM);
        if (soulStorage != null) {
            return Optional.of(soulStorage.getSoul());
        } else if (itemStack.getItem() instanceof SpawnEggItem spawnEggItem) {
            Entity entity = spawnEggItem.getType(itemStack).create(Minecraft.getInstance().level);
            if (entity instanceof LivingEntity livingEntity) {
                Soul ghost = new Soul(
                        livingEntity.serializeNBT(Minecraft.getInstance().level.registryAccess()),
                        livingEntity.getMaxHealth());
                return Optional.of(ghost);
            }
        }

        return Optional.empty();
    }
}
