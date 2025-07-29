package com.enderio.conduits.common.items;

import com.enderio.api.conduit.ConduitType;
import com.enderio.api.misc.ColorControl;
import com.enderio.api.misc.RedstoneControl;
import com.enderio.conduits.common.conduit.ConduitBundle;
import com.enderio.conduits.common.conduit.block.ConduitBlockEntity;
import com.enderio.conduits.common.conduit.connection.ConnectionState;
import com.enderio.conduits.common.conduit.connection.DynamicConnectionState;
import com.enderio.conduits.common.network.C2SSyncProbeState;
import com.enderio.conduits.common.util.InteractionUtil;
import com.enderio.core.common.network.CoreNetwork;
import com.enderio.core.common.util.TooltipUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class ConduitProbeItem extends Item {
    public static final String STATE_FIELD = "STATE";
    public static final String CONDUIT_DATA = "CONDUIT_DATA";
    public static final String IS_INSERT = "IS_INSERT";
    public static final String IS_EXTRACT = "IS_EXTRACT";
    public static final String INSERT_CHANNEL = "INSERT_CHANNEL";
    public static final String EXTRACT_CHANNEL = "EXTRACT_CHANNEL";
    public static final String REDSTONE_CONTROL = "REDSTONE_CONTROL";
    public static final String REDSTONE_CHANNEL = "REDSTONE_CHANNEL";
    
    public ConduitProbeItem(Properties properties) {
        super(properties);
    }
    
    public static State getState(ItemStack stack) {
        return State.values()[stack.getOrCreateTag().getInt(STATE_FIELD)];
    }
    
    public static void setState(ItemStack stack, State state, boolean syncToServer) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putInt(STATE_FIELD, state.ordinal());
        if (syncToServer) {
            CoreNetwork.sendToServer(new C2SSyncProbeState(state));
        }
    }
    
    public static void switchState(ItemStack stack, boolean syncToServer) {
        CompoundTag tag = stack.getOrCreateTag();
        int newState = (tag.getInt(STATE_FIELD) + 1) % State.values().length;
        setState(stack, State.values()[newState], syncToServer);
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        BlockEntity block = context.getLevel().getBlockEntity(context.getClickedPos());
        if (block instanceof ConduitBlockEntity conduit) {
            if (context.getLevel().isClientSide()) return InteractionResult.SUCCESS;
            switch (getState(stack)) {
            case COPY_PASTE -> {
                if (context.isSecondaryUseActive()) {
                    handleCopy(conduit,
                        InteractionUtil.fromClickLocation(context.getClickLocation(), context.getClickedPos().getCenter()),
                        stack);
                } else {
                    handlePaste(conduit,
                        InteractionUtil.fromClickLocation(context.getClickLocation(), context.getClickedPos().getCenter()),
                        stack);
                }
            }
            case PROBE -> {
                context.getPlayer().sendSystemMessage(Component.literal("This feature isn't implemented yet.").withStyle(ChatFormatting.RED));
            }
            }
            return InteractionResult.SUCCESS;
        }
        return super.onItemUseFirst(stack, context);
    }

    private void handleCopy(ConduitBlockEntity conduitBlock, Direction face, ItemStack itemStack) {
        CompoundTag tag = itemStack.getOrCreateTag();
        CompoundTag conduitData = new CompoundTag();
        tag.put(CONDUIT_DATA, conduitData);
        ConduitBundle bundle = conduitBlock.getBundle();
        if (bundle.getConnectedTypes(face).isEmpty()) {
            return;
        }
        bundle.getTypes().forEach(conduitType -> {
            ConnectionState connectionState = bundle.getConnectionState(face, conduitType);
            CompoundTag typeTag = new CompoundTag();
            if (connectionState.isConnection() && connectionState instanceof DynamicConnectionState dynamic) {
                typeTag.putBoolean(IS_INSERT, dynamic.isInsert());
                typeTag.putBoolean(IS_EXTRACT, dynamic.isExtract());
                typeTag.putInt(INSERT_CHANNEL, dynamic.insertChannel().ordinal());
                typeTag.putInt(EXTRACT_CHANNEL, dynamic.extractChannel().ordinal());
                typeTag.putInt(REDSTONE_CONTROL, dynamic.control().ordinal());
                typeTag.putInt(REDSTONE_CHANNEL, dynamic.redstoneChannel().ordinal());
            } else {
                typeTag.putBoolean(IS_INSERT, false);
                typeTag.putBoolean(IS_EXTRACT, false);
            }
            conduitData.put(Objects.requireNonNull(ConduitType.getKey(conduitType)).toString(), typeTag);
        });
    }
    
    public void handlePaste(ConduitBlockEntity conduitBlock, Direction face, ItemStack itemStack) {
        CompoundTag tag = itemStack.getTag();
        if (tag == null || !(tag.get(CONDUIT_DATA) instanceof CompoundTag conduitData)) {
            return;
        }
        ConduitBundle bundle = conduitBlock.getBundle();
        bundle.getTypes().forEach(conduitType -> {
            CompoundTag typeTag = conduitData.getCompound(Objects.requireNonNull(ConduitType.getKey(conduitType)).toString());
            ConnectionState prevConnectionState = bundle.getConnectionState(face, conduitType);
            DynamicConnectionState connectionState = null;
            if (prevConnectionState instanceof DynamicConnectionState) connectionState = (DynamicConnectionState) prevConnectionState;
            boolean wasConnected = connectionState != null;
            boolean isInsert = typeTag.getBoolean(IS_INSERT);
            boolean isExtract = typeTag.getBoolean(IS_EXTRACT);
            DynamicConnectionState newState = new DynamicConnectionState(
                isInsert, ColorControl.values()[typeTag.getInt(INSERT_CHANNEL)], isExtract,
                ColorControl.values()[typeTag.getInt(EXTRACT_CHANNEL)], RedstoneControl.values()[typeTag.getInt(REDSTONE_CONTROL)],
                ColorControl.values()[typeTag.getInt(REDSTONE_CHANNEL)], wasConnected ? connectionState.filterInsert() : ItemStack.EMPTY,
                wasConnected ? connectionState.filterExtract() : ItemStack.EMPTY, wasConnected ? connectionState.upgradeExtract() : ItemStack.EMPTY
            );
            conduitBlock.handleConnectionStateUpdate(face, conduitType, newState);
        });
        conduitBlock.setChanged();
        conduitBlock.updateClient();
        conduitBlock.updateEmptyDynConnection();
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        if (!(stack.getItem() instanceof ConduitProbeItem)) {
            return;
        }
        StringBuilder builder = new StringBuilder();
        for (String s : ConduitProbeItem.getState(stack).toString().toLowerCase().split("_")) {
            builder.append(StringUtils.capitalize(s));
            builder.append(" ");
        }
        builder.deleteCharAt(builder.length() - 1);
        tooltipComponents.add(TooltipUtil.style(Component.translatable("tooltip.enderio.conduit_probe.mode", builder.toString())));
        
        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
    }

    public enum State {
        PROBE,
        COPY_PASTE
    }
}
