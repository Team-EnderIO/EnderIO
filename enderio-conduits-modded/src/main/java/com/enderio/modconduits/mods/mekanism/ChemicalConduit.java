package com.enderio.modconduits.mods.mekanism;

import com.enderio.conduits.api.Conduit;
import com.enderio.conduits.api.ConduitMenuData;
import com.enderio.conduits.api.ConduitNode;
import com.enderio.conduits.api.ConduitType;
import com.enderio.conduits.common.init.ConduitLang;
import com.enderio.core.common.util.TooltipUtil;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public record ChemicalConduit(
    ResourceLocation texture,
    Component description,
    int transferRate,
    boolean isMultiChemical
) implements Conduit<ChemicalConduit> {

    public static MapCodec<ChemicalConduit> CODEC = RecordCodecBuilder.mapCodec(
        builder -> builder.group(
            ResourceLocation.CODEC.fieldOf("texture").forGetter(ChemicalConduit::texture),
            ComponentSerialization.CODEC.fieldOf("description").forGetter(ChemicalConduit::description),
            Codec.INT.fieldOf("transfer_rate").forGetter(ChemicalConduit::transferRate),
            Codec.BOOL.fieldOf("is_multi_chemical").forGetter(ChemicalConduit::isMultiChemical)
        ).apply(builder, ChemicalConduit::new)
    );

    private static final ChemicalTicker TICKER = new ChemicalTicker(MekanismModule.Capabilities.GAS, MekanismModule.Capabilities.SLURRY,
        MekanismModule.Capabilities.INFUSION, MekanismModule.Capabilities.PIGMENT);

    private static final ConduitMenuData MENU_DATA = new ConduitMenuData.Simple(false, false, false, false, false, true);
    private static final ConduitMenuData MULTI_MENU_DATA = new ConduitMenuData.Simple(false, false, false, true, true, true);

    @Override
    public ConduitType<ChemicalConduit> type() {
        return MekanismModule.Types.CHEMICAL.get();
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
    public boolean canConnectTo(ConduitNode selfNode, ConduitNode otherNode) {
        var selfData = selfNode.getOrCreateData(MekanismModule.CHEMICAL_DATA_TYPE.get());
        var otherData = otherNode.getOrCreateData(MekanismModule.CHEMICAL_DATA_TYPE.get());

        return selfData.lockedChemical.isEmpty() || otherData.lockedChemical.isEmpty() || selfData.lockedChemical.equals(otherData.lockedChemical);
    }

    @Override
    public void onConnectTo(ConduitNode selfNode, ConduitNode otherNode) {
        var selfData = selfNode.getOrCreateData(MekanismModule.CHEMICAL_DATA_TYPE.get());
        var otherData = otherNode.getOrCreateData(MekanismModule.CHEMICAL_DATA_TYPE.get());

        if (!selfData.lockedChemical.isEmpty()) {
//            if (!otherData.lockedChemical.isEmpty() && !selfData.lockedChemical.equals(otherData.lockedChemical)) {
//                //EnderIO.LOGGER.warn("incompatible chemical conduits merged");
//            }
            otherData.setlockedChemical(selfData.lockedChemical);
        } else if (!otherData.lockedChemical.isEmpty()) {
            selfData.setlockedChemical(otherData.lockedChemical);
        }
    }

    @Override
    public void addToTooltip(Item.TooltipContext pContext, Consumer<Component> pTooltipAdder, TooltipFlag pTooltipFlag) {
        // Get transfer rate, adjusted for the ticker rate.
        String transferLimitFormatted = String.format("%,d", transferRate() * (20 / getTicker().getTickRate()));
        pTooltipAdder.accept(TooltipUtil.styledWithArgs(ConduitLang.FLUID_RATE_TOOLTIP, transferLimitFormatted));

        if (isMultiChemical()) {
            pTooltipAdder.accept(MekanismModule.LANG_MULTI_CHEMICAL_TOOLTIP);
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
