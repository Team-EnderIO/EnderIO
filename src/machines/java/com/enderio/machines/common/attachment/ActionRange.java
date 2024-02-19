package com.enderio.machines.common.attachment;

import com.enderio.api.UseOnly;
import com.enderio.base.common.particle.RangeParticleData;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.neoforged.fml.LogicalSide;

public record ActionRange(int range, boolean isVisible) {
    public static final Codec<ActionRange> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            Codec.INT.fieldOf("range").forGetter(ActionRange::range),
            Codec.BOOL.fieldOf("isVisible").forGetter(ActionRange::isVisible)
        ).apply(instance, ActionRange::new));

    public ActionRange visible() {
        return new ActionRange(range, true);
    }

    public ActionRange invisible() {
        return new ActionRange(range, false);
    }

    public ActionRange increment() {
        return new ActionRange(range + 1, isVisible);
    }

    public ActionRange decrement() {
        return new ActionRange(range - 1, isVisible);
    }

    @UseOnly(LogicalSide.CLIENT)
    public void addClientParticle(ClientLevel level, BlockPos pos, String color) {
        if (!isVisible) {
            return;
        }

        if (level.isClientSide()) {
            level.addAlwaysVisibleParticle(new RangeParticleData(range, color), true, pos.getX(), pos.getY(), pos.getZ(), 0, 0, 0);
        }
    }
}
