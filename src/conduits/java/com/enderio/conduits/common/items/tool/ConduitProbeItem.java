package com.enderio.conduits.common.items.tool;

import com.enderio.api.conduit.ConduitType;
import com.enderio.api.misc.ColorControl;
import com.enderio.api.misc.RedstoneControl;
import com.enderio.conduits.common.conduit.ConduitBundle;
import com.enderio.conduits.common.conduit.block.ConduitBlockEntity;
import com.enderio.conduits.common.conduit.connection.ConnectionState;
import com.enderio.conduits.common.conduit.connection.DynamicConnectionState;
import com.enderio.conduits.common.network.C2SSetConduitConnectionState;
import com.enderio.conduits.common.util.InteractionUtil;
import com.enderio.core.common.network.CoreNetwork;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.Objects;

public class ConduitProbeItem extends Item implements INBTSerializable<CompoundTag> {
    public static final String STATE_FIELD = "STATE";
    public static final String IS_INSERT = "IS_INSERT";
    public static final String IS_EXTRACT = "IS_EXTRACT";
    public static final String INSERT_CHANNEL = "INSERT_CHANNEL";
    public static final String EXTRACT_CHANNEL = "EXTRACT_CHANNEL";
    public static final String REDSTONE_CONTROL = "REDSTONE_CONTROL";
    public static final String REDSTONE_CHANNEL = "REDSTONE_CHANNEL";
    
    private State state;
    
    public ConduitProbeItem(Properties properties) {
        super(properties);
        state = State.PROBE;
    }
    
    public State getState() {
        return state;
    }
    
    public void switchState() {
        this.state = State.values()[(this.state.ordinal() + 1) % State.values().length];
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        BlockEntity block = context.getLevel().getBlockEntity(context.getClickedPos());
        if (block instanceof ConduitBlockEntity conduit) {
            if (state.equals(State.COPY_PASTE)) {
                if (context.isSecondaryUseActive()) {
                    handleCopy(conduit, 
                        InteractionUtil.fromClickLocation(context.getClickLocation(), context.getClickedPos().getCenter()), 
                        stack);
                }
                else {
                    handlePaste(conduit, 
                        InteractionUtil.fromClickLocation(context.getClickLocation(), context.getClickedPos().getCenter()), 
                        stack);
                }
            }
            return InteractionResult.SUCCESS;
        }
        return super.onItemUseFirst(stack, context);
    }

    private void handleCopy(ConduitBlockEntity conduitBlock, Direction face, ItemStack itemStack) {
        CompoundTag tag = itemStack.getOrCreateTag();
        tag.getAllKeys().clear();
        ConduitBundle bundle = conduitBlock.getBundle();
        if (bundle.getConnectedTypes(face).isEmpty()) return;
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
            }
            else {
                typeTag.putBoolean(IS_INSERT, false);
                typeTag.putBoolean(IS_EXTRACT, false);
            }
            tag.put(Objects.requireNonNull(ConduitType.getKey(conduitType)).toString(), typeTag);
        });
    }
    
    public void handlePaste(ConduitBlockEntity conduitBlock, Direction face, ItemStack itemStack) {
        CompoundTag tag = itemStack.getTag();
        if (tag == null) return;
        ConduitBundle bundle = conduitBlock.getBundle();
        bundle.getTypes().forEach(conduitType -> {
            CompoundTag typeTag = tag.getCompound(Objects.requireNonNull(ConduitType.getKey(conduitType)).toString());
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
            CoreNetwork.sendToServer(new C2SSetConduitConnectionState(conduitBlock.getBlockPos(), face, conduitType, newState));
        });
        conduitBlock.updateEmptyDynConnection();
        bundle.onChanged();
        conduitBlock.setChanged();
        conduitBlock.updateShape();
        conduitBlock.updateClient();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt(STATE_FIELD, state.ordinal());
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        int stateOrdinal = tag.getInt(STATE_FIELD);
        if (stateOrdinal >= 0 && stateOrdinal < State.values().length) {
            state = State.values()[stateOrdinal];
        }
    }

    public enum State {
        PROBE,
        COPY_PASTE
    }
}
