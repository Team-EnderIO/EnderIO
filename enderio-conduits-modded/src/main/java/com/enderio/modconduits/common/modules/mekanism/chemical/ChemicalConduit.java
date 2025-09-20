package com.enderio.modconduits.common.modules.mekanism.chemical;

import com.enderio.base.api.misc.RedstoneControl;
import com.enderio.conduits.api.Conduit;
import com.enderio.conduits.api.ConduitType;
import com.enderio.conduits.api.bundle.ConduitBundle;
import com.enderio.conduits.api.bundle.SlotType;
import com.enderio.conduits.api.connection.config.ConnectionConfig;
import com.enderio.conduits.api.connection.config.ConnectionConfigType;
import com.enderio.conduits.api.network.node.IConduitNode;
import com.enderio.conduits.api.network.node.legacy.ConduitDataAccessor;
import com.enderio.conduits.common.init.ConduitLang;
import com.enderio.core.common.util.TooltipUtil;
import com.enderio.modconduits.common.modules.mekanism.MekanismModule;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;

public record ChemicalConduit(ResourceLocation texture, Component description, long transferRatePerTick,
        boolean isMultiChemical) implements Conduit<ChemicalConduit, ChemicalConduitConnectionConfig> {

    public static final int EXTRACT_FILTER_SLOT = 0;
    public static final int INSERT_FILTER_SLOT = 1;

    public static final MapCodec<ChemicalConduit> CODEC = RecordCodecBuilder
            .mapCodec(
                    builder -> builder
                            .group(ResourceLocation.CODEC.fieldOf("texture").forGetter(ChemicalConduit::texture),
                                    ComponentSerialization.CODEC.fieldOf("description")
                                            .forGetter(ChemicalConduit::description),
                                    Codec.LONG.fieldOf("transfer_rate").forGetter(ChemicalConduit::transferRatePerTick),
                                    Codec.BOOL.fieldOf("is_multi_chemical").forGetter(ChemicalConduit::isMultiChemical))
                            .apply(builder, ChemicalConduit::new));

    private static final ChemicalTicker TICKER = new ChemicalTicker();

    @Override
    public ConduitType<ChemicalConduit> type() {
        return MekanismModule.TYPE_CHEMICAL.get();
    }

    @Override
    public ConnectionConfigType<ChemicalConduitConnectionConfig> connectionConfigType() {
        return ChemicalConduitConnectionConfig.TYPE;
    }

    @Override
    public ChemicalTicker ticker() {
        return TICKER;
    }

    @Override
    public boolean hasMenu() {
        return true;
    }

    @Override
    public boolean canBeInSameBundle(Holder<Conduit<?, ?>> otherConduit) {
        return otherConduit.value().type() != type();
    }

    @Override
    public boolean canBeReplacedBy(Holder<Conduit<?, ?>> otherConduit) {
        if (otherConduit.value().type() != type()) {
            return false;
        }

        if (!(otherConduit.value() instanceof ChemicalConduit otherChemicalConduit)) {
            return false;
        }

        return compareTo(otherChemicalConduit) < 0;
    }

    @Override
    public boolean hasServerConnectionChecks() {
        return !isMultiChemical();
    }

    @Override
    public boolean canConnectConduits(IConduitNode selfNode, IConduitNode otherNode) {
        if (isMultiChemical()) {
            return true;
        }

        // Ensure the networks are not locked to different fluids before connecting.
        var selfNetwork = selfNode.getNetwork();
        var otherNetwork = otherNode.getNetwork();

        // If one network does not yet exist, then we're good to connect.
        if (selfNetwork == null || otherNetwork == null) {
            return true;
        }

        var selfContext = selfNetwork.getContext(ChemicalConduitNetworkContext.TYPE);
        var otherContext = otherNetwork.getContext(ChemicalConduitNetworkContext.TYPE);

        if (selfContext == null || otherContext == null) {
            return true;
        }

        if (selfContext.lockedChemical().isEmptyType() || otherContext.lockedChemical().isEmptyType()) {
            return true;
        }

        return selfContext.lockedChemical().equals(otherContext.lockedChemical());
    }

    @Override
    public boolean canConnectToBlock(Level level, BlockPos conduitPos, Direction direction) {
        return level.getCapability(MekanismModule.Capabilities.CHEMICAL, conduitPos.relative(direction),
                direction.getOpposite()) != null;
    }

    @Override
    public ChemicalConduitConnectionConfig convertConnection(boolean isInsert, boolean isExtract, DyeColor inputChannel,
            DyeColor outputChannel, RedstoneControl redstoneControl, DyeColor redstoneChannel) {
        return new ChemicalConduitConnectionConfig(isInsert, inputChannel, isExtract, outputChannel, redstoneControl,
                redstoneChannel);
    }

    @Override
    public void copyLegacyData(IConduitNode node, ConduitDataAccessor legacyDataAccessor,
            BiConsumer<Direction, ConnectionConfig> connectionConfigSetter) {
        var legacyData = legacyDataAccessor.getData(MekanismModule.CHEMICAL_DATA_TYPE.get());
        if (legacyData == null) {
            return;
        }

        var context = Objects.requireNonNull(node.getNetwork()).getOrCreateContext(ChemicalConduitNetworkContext.TYPE);

        if (!context.lockedChemical().isEmptyType()) {
            return;
        }

        // Copy locked fluid from old data.
        context.setLockedChemical(legacyData.lockedChemical().getChemical());
    }

    @Override
    public int getInventorySize() {
        return 2;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return stack.getCapability(MekanismModule.Capabilities.CHEMICAL_FILTER) != null;
    }

    @Override
    public Vector2i getInventorySlotPosition(int slot) {
        return switch (slot) {
        case EXTRACT_FILTER_SLOT -> new Vector2i(113, 71);
        case INSERT_FILTER_SLOT -> new Vector2i(23, 71);
        default -> throw new IndexOutOfBoundsException();
        };
    }

    @Override
    public int getIndexForLegacySlot(SlotType slotType) {
        return switch (slotType) {
        case FILTER_EXTRACT -> EXTRACT_FILTER_SLOT;
        case FILTER_INSERT -> INSERT_FILTER_SLOT;
        default -> -1;
        };
    }

    @Override
    public @Nullable CompoundTag getExtraGuiData(ConduitBundle conduitBundle, IConduitNode node, Direction side) {
        return getExtraWorldData(conduitBundle, node);
    }

    @Override
    public @Nullable CompoundTag getExtraWorldData(ConduitBundle conduitBundle, IConduitNode node) {
        if (node.getNetwork() == null) {
            return null;
        }

        var context = node.getNetwork().getContext(ChemicalConduitNetworkContext.TYPE);
        if (context == null) {
            return null;
        }

        if (context.lockedChemical().isEmptyType()) {
            return null;
        }

        var tag = new CompoundTag();
        tag.putString("LockedChemical", context.lockedChemical().getRegistryName().toString());
        return tag;
    }

    @Override
    public void addToTooltip(Item.TooltipContext pContext, Consumer<Component> pTooltipAdder,
            TooltipFlag pTooltipFlag) {
        String transferLimitFormatted = String.format("%,d", transferRatePerTick());
        pTooltipAdder
                .accept(TooltipUtil.styledWithArgs(ConduitLang.FLUID_EFFECTIVE_RATE_TOOLTIP, transferLimitFormatted));

        if (isMultiChemical()) {
            pTooltipAdder.accept(MekanismModule.LANG_MULTI_CHEMICAL_TOOLTIP);
        }

        if (pTooltipFlag.hasShiftDown()) {
            String rawRateFormatted = String.format("%,d",
                    (int) Math.ceil(transferRatePerTick() * (20.0 / networkTickRate())));
            pTooltipAdder.accept(TooltipUtil.styledWithArgs(ConduitLang.FLUID_RAW_RATE_TOOLTIP, rawRateFormatted));
        }
    }

    @Override
    public boolean hasAdvancedTooltip() {
        return true;
    }

    @Override
    public boolean showDebugTooltip() {
        return true;
    }

    @Override
    public int compareTo(@NotNull ChemicalConduit o) {
        if (isMultiChemical() && !o.isMultiChemical()) {
            return 1;
        }

        if (transferRatePerTick() < o.transferRatePerTick()) {
            return -1;
        } else if (transferRatePerTick() > o.transferRatePerTick()) {
            return 1;
        }

        return 0;
    }
}
