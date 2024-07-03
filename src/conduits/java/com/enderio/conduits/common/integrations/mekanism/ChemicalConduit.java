package com.enderio.conduits.common.integrations.mekanism;

import com.enderio.api.conduit.Conduit;
import com.enderio.api.conduit.ConduitMenuData;
import com.enderio.api.conduit.ConduitNetwork;
import com.enderio.api.conduit.ConduitNetworkContext;
import com.enderio.api.conduit.ConduitType;
import com.enderio.conduits.common.init.ConduitLang;
import com.enderio.core.common.util.TooltipUtil;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public record ChemicalConduit(
    ResourceLocation texture,
    Component description,
    int transferRate,
    boolean isMultiChemical
) implements Conduit<ChemicalConduit, ConduitNetworkContext.Dummy, ChemicalConduitData> {

    public static MapCodec<ChemicalConduit> CODEC = RecordCodecBuilder.mapCodec(
        builder -> builder.group(
            ResourceLocation.CODEC.fieldOf("texture").forGetter(ChemicalConduit::texture),
            ComponentSerialization.CODEC.fieldOf("description").forGetter(ChemicalConduit::description),
            Codec.INT.fieldOf("transfer_rate").forGetter(ChemicalConduit::transferRate),
            Codec.BOOL.fieldOf("is_multi_chemical").forGetter(ChemicalConduit::isMultiChemical)
        ).apply(builder, ChemicalConduit::new)
    );

    private static final ChemicalTicker TICKER = new ChemicalTicker(MekanismIntegration.Capabilities.GAS, MekanismIntegration.Capabilities.SLURRY,
        MekanismIntegration.Capabilities.INFUSION, MekanismIntegration.Capabilities.PIGMENT);

    private static final ConduitMenuData MENU_DATA = new ConduitMenuData.Simple(false, false, false, false, false, true);
    private static final ConduitMenuData MULTI_MENU_DATA = new ConduitMenuData.Simple(false, false, false, true, true, true);

    @Override
    public ConduitType<ChemicalConduit> type() {
        return null;
    }

    @Override
    public ChemicalTicker getTicker() {
        return TICKER;
    }

    @Override
    public ConduitMenuData getMenuData() {
        return isMultiChemical() ? MULTI_MENU_DATA : MENU_DATA;
    }

    @Override
    public @Nullable ConduitNetworkContext.Dummy createNetworkContext(ConduitNetwork<ConduitNetworkContext.Dummy, ChemicalConduitData> network) {
        return null;
    }

    @Override
    public ChemicalConduitData createConduitData(Level level, BlockPos pos) {
        return new ChemicalConduitData(isMultiChemical());
    }

    @Override
    public boolean canBeInSameBundle(Holder<Conduit<?, ?, ?>> otherConduit) {
        if (otherConduit.value().type() != type()) {
            return true;
        }

        return false;
    }

    @Override
    public boolean canBeReplacedBy(Holder<Conduit<?, ?, ?>> otherConduit) {
        if (otherConduit.value().type() != type()) {
            return false;
        }

        if (otherConduit.value() instanceof ChemicalConduit otherChemicalConduit) {
            // Replacement must support multi fluid if the current does.
            if (isMultiChemical() && !otherChemicalConduit.isMultiChemical()) {
                return false;
            }

            return transferRate() <= otherChemicalConduit.transferRate();
        }

        return false;
    }

    @Override
    public void addToTooltip(Item.TooltipContext pContext, Consumer<Component> pTooltipAdder, TooltipFlag pTooltipFlag) {
        // Get transfer rate, adjusted for the ticker rate.
        String transferLimitFormatted = String.format("%,d", transferRate() * (20 / getTicker().getTickRate()));
        pTooltipAdder.accept(TooltipUtil.styledWithArgs(ConduitLang.FLUID_RATE_TOOLTIP, transferLimitFormatted));

        if (isMultiChemical()) {
            pTooltipAdder.accept(MekanismIntegration.LANG_MULTI_CHEMICAL_TOOLTIP);
        }
    }

    @Override
    public int compareTo(@NotNull ChemicalConduit o) {
        if (isMultiChemical() && !o.isMultiChemical()) {
            return 1;
        }

        if (transferRate() < o.transferRate()) {
            return -1;
        } else if (transferRate() > o.transferRate()) {
            return 1;
        }

        return 0;
    }

    // TODO: Support for extract upgrades
}
