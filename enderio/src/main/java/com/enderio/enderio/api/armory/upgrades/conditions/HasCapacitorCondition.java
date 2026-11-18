package com.enderio.enderio.api.armory.upgrades.conditions;

import com.enderio.enderio.api.EnderIOCapabilities;
import com.enderio.enderio.api.capacitor.CapacitorData;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public final class HasCapacitorCondition implements EffectCondition {

    public static final HasCapacitorCondition INSTANCE = new HasCapacitorCondition();

    public static final MapCodec<HasCapacitorCondition> CODEC = MapCodec.unit(INSTANCE);

    private HasCapacitorCondition() {
    }

    @Override
    public MapCodec<? extends EffectCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean isActive(ItemStack itemStack, @Nullable Level level, @Nullable Entity entity) {
        var capacitorSocket = itemStack.getCapability(EnderIOCapabilities.CAPACITOR_SOCKET);
        if (capacitorSocket == null) {
            return false;
        }

        return !capacitorSocket.getCapacitorData().equals(CapacitorData.NONE);
    }
}
