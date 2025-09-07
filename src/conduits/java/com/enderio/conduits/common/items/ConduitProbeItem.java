package com.enderio.conduits.common.items;

import com.enderio.api.conduit.ConduitType;
import com.enderio.api.misc.ColorControl;
import com.enderio.api.misc.RedstoneControl;
import com.enderio.conduits.common.conduit.ConduitBundle;
import com.enderio.conduits.common.conduit.block.ConduitBlockEntity;
import com.enderio.conduits.common.conduit.connection.ConnectionState;
import com.enderio.conduits.common.conduit.connection.DynamicConnectionState;
import com.enderio.conduits.common.conduit.type.item.ItemConduitData.ItemSidedData;
import com.enderio.conduits.common.conduit.type.item.ItemConduitType;
import com.enderio.conduits.common.init.ConduitLang;
import com.enderio.conduits.common.network.C2SSyncProbeState;
import com.enderio.conduits.common.util.InteractionUtil;
import com.enderio.core.common.network.CoreNetwork;
import com.enderio.core.common.util.TooltipUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import com.enderio.base.common.lang.EIOLang;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class ConduitProbeItem extends Item {
    public static final String STATE_FIELD = "STATE";
    public static final String CONDUIT_DATA = "CONDUIT_DATA";
    public static final String IS_INSERT = "IS_INSERT";
    public static final String IS_EXTRACT = "IS_EXTRACT";
    public static final String INSERT_CHANNEL = "INSERT_CHANNEL";
    public static final String EXTRACT_CHANNEL = "EXTRACT_CHANNEL";
    public static final String REDSTONE_CONTROL = "REDSTONE_CONTROL";
    public static final String REDSTONE_CHANNEL = "REDSTONE_CHANNEL";
    public static final String ROUND_ROBIN = "ROUND_ROBIN";
    public static final String SELF_FEED = "SELF_FEED";
    public static final String PRIORITY = "PRIORITY";

    public static final Set<String> BOOL_TAG = Set.of(IS_INSERT, IS_EXTRACT, ROUND_ROBIN, SELF_FEED);
    public static final Set<String> COLOR_CONTROL_TAG = Set.of(INSERT_CHANNEL, EXTRACT_CHANNEL, REDSTONE_CHANNEL);

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
        Player player = context.getPlayer();
        if (player == null) {
            return InteractionResult.FAIL;
        }
        if (context.getLevel().isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        if (!(block instanceof ConduitBlockEntity conduit)) {
            return super.onItemUseFirst(stack, context);
        }

        switch (getState(stack)) {
            case COPY_PASTE -> {
                if (context.isSecondaryUseActive()) {
                    handleCopy(conduit,
                        InteractionUtil.fromClickLocation(context.getClickLocation(), context.getClickedPos().getCenter()),
                        stack,
                        player);
                } else {
                    handlePaste(conduit,
                        InteractionUtil.fromClickLocation(context.getClickLocation(), context.getClickedPos().getCenter()),
                        stack,
                        player);
                }
            }
            case PROBE -> {
                player.sendSystemMessage(Component.literal("This feature isn't implemented yet.").withStyle(ChatFormatting.RED));
            }
        }
        return InteractionResult.SUCCESS;
    }

    private void handleCopy(ConduitBlockEntity conduitBlock, Direction face, ItemStack itemStack, Player player) {
        CompoundTag tag = itemStack.getOrCreateTag();
        CompoundTag conduitData = new CompoundTag();
        tag.put(CONDUIT_DATA, conduitData);
        ConduitBundle bundle = conduitBlock.getBundle();
        if (bundle.getConnectedTypes(face).isEmpty()) {
            return;
        }
        MutableComponent message = Component.empty();
        bundle.getTypes().forEach(conduit -> {
            String conduitKey = ConduitType.getKey(conduit).toString();
            ConnectionState connectionState = bundle.getConnectionState(face, conduit);
            CompoundTag typeTag = new CompoundTag();
            if (connectionState instanceof DynamicConnectionState dynamic) {
                typeTag.putBoolean(IS_INSERT, dynamic.isInsert());
                typeTag.putBoolean(IS_EXTRACT, dynamic.isExtract());
                typeTag.putInt(INSERT_CHANNEL, dynamic.insertChannel().ordinal());
                typeTag.putInt(EXTRACT_CHANNEL, dynamic.extractChannel().ordinal());
                typeTag.putInt(REDSTONE_CONTROL, dynamic.control().ordinal());
                typeTag.putInt(REDSTONE_CHANNEL, dynamic.redstoneChannel().ordinal());

                if (conduit instanceof ItemConduitType itemConduitType) {
                    ItemSidedData sidedData = bundle.getNodeFor(itemConduitType).getConduitData().get(face);
                    typeTag.putBoolean(ROUND_ROBIN, sidedData.isRoundRobin);
                    typeTag.putBoolean(SELF_FEED, sidedData.isSelfFeed);
                    typeTag.putInt(PRIORITY, sidedData.getPriority());
                }
            } else {
                typeTag.putBoolean(IS_INSERT, false);
                typeTag.putBoolean(IS_EXTRACT, false);
            }
            conduitData.put(conduitKey, typeTag);

            message.append(Component.literal("\n" + conduitKeyToDisplayName(conduitKey).getString() + ":\n").withStyle(ChatFormatting.UNDERLINE));
            typeTag.getAllKeys().forEach(key -> {
                StringBuilder sb = new StringBuilder();
                sb.append(" - " + key + ": ");
                if (BOOL_TAG.contains(key)) {
                     sb.append(typeTag.getBoolean(key));
                } else if (COLOR_CONTROL_TAG.contains(key)) {
                    sb.append(ColorControl.values()[typeTag.getInt(key)]);
                } else if (key.equals(REDSTONE_CONTROL)) {
                    sb.append(RedstoneControl.values()[typeTag.getInt(key)]);
                } else {
                    sb.append(typeTag.get(key));
                }
                sb.append("\n");
                message.append(Component.literal(sb.toString()).withStyle(ChatFormatting.GRAY));
            });
        });
        player.sendSystemMessage(TooltipUtil.withArgs(EIOLang.CONDUIT_PROBE_MESSAGE_COPIED, message));
    }

    public void handlePaste(ConduitBlockEntity conduitBlock, Direction face, ItemStack itemStack, Player player) {
        CompoundTag tag = itemStack.getTag();
        if (tag == null || !(tag.get(CONDUIT_DATA) instanceof CompoundTag conduitData)) {
            return;
        }

        List<String> pastedConduits = new ArrayList<String>();
        ConduitBundle bundle = conduitBlock.getBundle();
        bundle.getTypes().forEach(conduit -> {
            String conduitKey = ConduitType.getKey(conduit).toString();
            CompoundTag typeTag = conduitData.getCompound(conduitKey);
            if (typeTag == null || typeTag.isEmpty()) {
                return;
            }

            ConnectionState prevConnectionState = bundle.getConnectionState(face, conduit);
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
            conduitBlock.handleConnectionStateUpdate(face, conduit, newState);

            if (conduit instanceof ItemConduitType itemConduitType && typeTag.contains(ROUND_ROBIN)) {
                ItemSidedData sidedData = bundle.getNodeFor(itemConduitType).getConduitData().compute(face);
                sidedData.isRoundRobin = typeTag.getBoolean(ROUND_ROBIN);
                sidedData.isSelfFeed = typeTag.getBoolean(SELF_FEED);
                sidedData.setPriority(typeTag.getInt(PRIORITY));
            }
            pastedConduits.add(conduitKeyToDisplayName(conduitKey).getString());
        });

        if (!pastedConduits.isEmpty()) {
            String pastedConduitsString = String.join(", ", pastedConduits);
            player.sendSystemMessage(TooltipUtil.withArgs(EIOLang.CONDUIT_PROBE_MESSAGE_PASTED, pastedConduitsString));
        }
        conduitBlock.setChanged();
        conduitBlock.updateClient();
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        if (!(stack.getItem() instanceof ConduitProbeItem)) {
            return;
        }

        tooltipComponents.add(TooltipUtil.styledWithArgs(EIOLang.CONDUIT_PROBE_MODE, ConduitProbeItem.getState(stack).getStateText()));

        CompoundTag tag = stack.getTag();
        if (tag != null && (tag.get(CONDUIT_DATA) instanceof CompoundTag conduitData) && !conduitData.getAllKeys().isEmpty()) {
            tooltipComponents.add(EIOLang.CONDUIT_PROBE_CONTAINS_COPIED.withStyle(ChatFormatting.GRAY));
            conduitData.getAllKeys().forEach(conduitKey -> {
                tooltipComponents.add(Component.literal("- " + conduitKeyToDisplayName(conduitKey).getString()).withStyle(ChatFormatting.DARK_GRAY));
            });
        }

        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
    }

    // Can't use ResourceLocation because of backwards compatibility of conduitData
    private Component conduitKeyToDisplayName(String conduitKey) {
        String translationKey = "item." + conduitKey.replace(":", ".");
        return Component.translatable(translationKey);
    }

    public enum State {
        PROBE,
        COPY_PASTE;

        public Component getStateText() {
            return switch (this) {
                case PROBE -> EIOLang.CONDUIT_PROBE_PROBE;
                case COPY_PASTE -> EIOLang.CONDUIT_PROBE_COPY_PASTE;
            };
        }
    }
}
