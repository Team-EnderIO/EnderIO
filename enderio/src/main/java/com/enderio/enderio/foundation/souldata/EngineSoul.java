package com.enderio.enderio.foundation.souldata;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

public class EngineSoul {

    // TODO: 20.6: May be able to use FluidIngredient
    public record SoulData(Identifier entitytype, String fluid, int powerpermb, int tickpermb) implements com.enderio.enderio.foundation.souldata.SoulData {
        @Override
        public Identifier getKey() {
            return entitytype();
        }
    }

    public static final Codec<SoulData> CODEC = RecordCodecBuilder.create(soulDataInstance ->
       soulDataInstance.group(
           Identifier.CODEC.fieldOf("entity").forGetter(SoulData::entitytype),
           Codec.STRING.fieldOf("fluid").forGetter(SoulData::fluid),
           Codec.INT.fieldOf("power/mb").forGetter(SoulData::powerpermb),
           Codec.INT.fieldOf("tick/mb").forGetter(SoulData::tickpermb))
           .apply(soulDataInstance, SoulData::new));

    public static final StreamCodec<ByteBuf, SoulData> STREAM_CODEC = StreamCodec.composite(
        Identifier.STREAM_CODEC,
        SoulData::entitytype,
        ByteBufCodecs.STRING_UTF8,
        SoulData::fluid,
        ByteBufCodecs.INT,
        SoulData::powerpermb,
        ByteBufCodecs.INT,
        SoulData::tickpermb,
        SoulData::new
    );

    public static final String NAME = "engine";
    public static final SoulDataReloadListener<SoulData> RELOAD_LISTENER = new SoulDataReloadListener<>(CODEC, NAME);
}
