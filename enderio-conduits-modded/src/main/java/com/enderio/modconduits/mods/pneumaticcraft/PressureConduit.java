package com.enderio.modconduits.mods.pneumaticcraft;

import com.enderio.conduits.api.Conduit;
import com.enderio.conduits.api.ConduitMenuData;
import com.enderio.conduits.api.ConduitNode;
import com.enderio.conduits.api.ConduitType;
import com.enderio.conduits.api.ticker.ConduitTicker;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.desht.pneumaticcraft.api.PNCCapabilities;
import me.desht.pneumaticcraft.api.pressure.PressureTier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record PressureConduit(
    ResourceLocation texture,
    Component description,
    PressureTier tier
) implements Conduit<PressureConduit> {

    public static Codec<PressureTier> TIER_CODEC = RecordCodecBuilder.create(
        builder -> builder.group(
            Codec.FLOAT.fieldOf("danger").forGetter(PressureTier::getDangerPressure),
            Codec.FLOAT.fieldOf("critical").forGetter(PressureTier::getCriticalPressure)
        ).apply(builder, (danger, critical) -> new PressureTier() {

            @Override
            public float getDangerPressure() {
                return danger;
            }

            @Override
            public float getCriticalPressure() {
                return critical;
            }
        })
    );

    public static MapCodec<PressureConduit> CODEC = RecordCodecBuilder.mapCodec(
        builder -> builder.group(
            ResourceLocation.CODEC.fieldOf("texture").forGetter(PressureConduit::texture),
            ComponentSerialization.CODEC.fieldOf("description").forGetter(PressureConduit::description),
            TIER_CODEC.fieldOf("pressure_tier").forGetter(PressureConduit::tier)
        ).apply(builder, PressureConduit::new)
    );

    private static final PressureTicker TICKER = new PressureTicker();
    private static final ConduitMenuData MENU_DATA = new ConduitMenuData.Simple(false, false, false, false, false, true);

    @Override
    public ConduitType<PressureConduit> type() {
        return PneumaticModule.Types.PRESSURE.get();
    }

    @Override
    public ConduitTicker<PressureConduit> getTicker() {
        return TICKER;
    }

    @Override
    public ConduitMenuData getMenuData() {
        return MENU_DATA;
    }

    @Override
    public boolean canBeInSameBundle(Holder<Conduit<?>> otherConduit) {
        if (otherConduit.value().type() != type()) {
            return true;
        }

        return false;
    }

    @Override
    public boolean canBeReplacedBy(Holder<Conduit<?>> otherConduit) {
        if (otherConduit.value().type() != type()) {
            return false;
        }

        if (otherConduit.value() instanceof PressureConduit other) {

            return tier().getCriticalPressure() <= other.tier().getCriticalPressure();
        }

        return false;
    }

    @Override
    public <TCapability, TContext> @Nullable TCapability proxyCapability(BlockCapability<TCapability, TContext> capability, ConduitNode node, Level level,
        BlockPos pos, @Nullable TContext tContext) {
        if (capability == PNCCapabilities.AIR_HANDLER_MACHINE) {

            //noinspection unchecked
            return (TCapability) new PressureConduitStorage(tier(), node);
        }
        return null;
    }

    @Override
    public int compareTo(@NotNull PressureConduit o) {
        if (tier().getCriticalPressure() < o.tier().getCriticalPressure()) {
            return -1;
        } else if (tier().getCriticalPressure() > o.tier().getCriticalPressure()) {
            return 1;
        }

        return 0;
    }
}
