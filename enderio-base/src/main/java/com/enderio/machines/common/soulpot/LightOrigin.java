package com.enderio.machines.common.soulpot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.LightLayer;

public record LightOrigin(boolean isMin, int level) implements Origin<LightOrigin> {
    public static final MapCodec<LightOrigin> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Codec.BOOL.fieldOf("isMin").forGetter(LightOrigin::isMin),
            Codec.INT.fieldOf("level").forGetter(LightOrigin::level))
        .apply(inst, LightOrigin::new));

    @Override
    public OriginType<LightOrigin> type() {
        return OriginType.LIGHT;
    }

    @Override
    public boolean matches(OriginContext ctx) {
        int brightness = ctx.level().getBrightness(LightLayer.BLOCK, ctx.pos());
        if (isMin) {
            return level >= brightness;
        } else {
            return level <= brightness;
        }
    }
}
