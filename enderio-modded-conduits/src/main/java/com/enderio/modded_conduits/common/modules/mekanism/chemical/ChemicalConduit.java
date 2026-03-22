package com.enderio.modded_conduits.common.modules.mekanism.chemical;

import com.enderio.core.common.util.TooltipUtil;
import com.enderio.enderio.api.conduits.Conduit;
import com.enderio.enderio.api.conduits.ConduitType;
import com.enderio.enderio.api.conduits.bundle.SlotType;
import com.enderio.enderio.api.conduits.connection.path.ConnectionPathProperty;
import com.enderio.enderio.api.conduits.connection.path.ConnectionPathPropertyConsumer;
import com.enderio.enderio.api.conduits.network.node.ConduitNode;
import com.enderio.enderio.api.io.RedstoneControl;
import com.enderio.enderio.content.conduits.ConduitLang;
import com.enderio.modded_conduits.common.modules.mekanism.MekanismModule;
import com.enderio.modded_conduits.config.ModdedConduitsConfig;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.joml.Vector2i;

import java.util.function.Consumer;

public record ChemicalConduit(Identifier texture, Component description, long transferRatePerTick)
    implements Conduit<ChemicalConduit, ChemicalConduitConnectionConfig> {

    public static final int EXTRACT_FILTER_SLOT = 0;
    public static final int INSERT_FILTER_SLOT = 1;

    public static final MapCodec<ChemicalConduit> CODEC = RecordCodecBuilder
        .mapCodec(
            builder -> builder
                .group(Identifier.CODEC.fieldOf("texture").forGetter(ChemicalConduit::texture),
                    ComponentSerialization.CODEC.fieldOf("description")
                        .forGetter(ChemicalConduit::description),
                    Codec.LONG.fieldOf("transfer_rate").forGetter(ChemicalConduit::transferRatePerTick))
                .apply(builder, ChemicalConduit::new));

    public static final ConnectionPathProperty<Long> PATH_MAX_TRANSFER_RATE = ConnectionPathProperty.minLong(0);

    @Override
    public ConduitType<ChemicalConduit, ChemicalConduitConnectionConfig> type() {
        return MekanismModule.TYPE_CHEMICAL.get();
    }

    @Override
    public boolean hasMenu() {
        return true;
    }

    @Override
    public boolean canReplaceConduit(ChemicalConduit otherConduit) {
        return compareTo(otherConduit) > 0;
    }

    @Override
    public void collectNodePathProperties(ConduitNode node, ConnectionPathPropertyConsumer consumer) {
        consumer.accept(PATH_MAX_TRANSFER_RATE, transferRatePerTick());
    }

    @Override
    public boolean canConnectToBlock(Level level, BlockPos conduitPos, Direction direction) {
        return level.getCapability(MekanismModule.Capabilities.CHEMICAL, conduitPos.relative(direction),
                direction.getOpposite()) != null;
    }

    @Override
    public boolean canConnectToConduit(ChemicalConduit other) {
        return ModdedConduitsConfig.COMMON.MEKANISM.CAN_MIX_CHEMICAL_CONDUIT_TIERS.get();
    }

    @Override
    public ChemicalConduitConnectionConfig convertConnection(boolean isInsert, boolean isExtract, DyeColor inputChannel,
            DyeColor outputChannel, RedstoneControl redstoneControl, DyeColor redstoneChannel) {
        return new ChemicalConduitConnectionConfig(isInsert, inputChannel, isExtract, outputChannel, redstoneControl,
                redstoneChannel);
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
    public void addToTooltip(Item.TooltipContext context, Consumer<Component> tooltipAdder,
            TooltipFlag tooltipFlag) {
        String transferLimitFormatted = String.format("%,d", transferRatePerTick());
        tooltipAdder
                .accept(TooltipUtil.styledWithArgs(ConduitLang.FLUID_EFFECTIVE_RATE_TOOLTIP, transferLimitFormatted));

        if (tooltipFlag.hasShiftDown()) {
            String rawRateFormatted = String.format("%,d",
                    (int) Math.ceil(transferRatePerTick() * type().getTickRate(this)));
            tooltipAdder.accept(TooltipUtil.styledWithArgs(ConduitLang.FLUID_RAW_RATE_TOOLTIP, rawRateFormatted));
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
    public int compareTo(@NonNull ChemicalConduit o) {
        if (transferRatePerTick() < o.transferRatePerTick()) {
            return -1;
        } else if (transferRatePerTick() > o.transferRatePerTick()) {
            return 1;
        }

        return 0;
    }
}
