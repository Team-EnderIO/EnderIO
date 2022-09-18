package com.enderio.conduits.common.integrations.ae2;

import appeng.api.config.SecurityPermissions;
import appeng.api.networking.*;
import appeng.api.util.AECableType;
import com.enderio.api.conduit.IConduitType;
import com.enderio.api.conduit.IExtendedConduitData;
import com.enderio.conduits.common.blockentity.ConduitBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class AE2InWorldConduitNodeHost implements IInWorldGridNodeHost, IExtendedConduitData<AE2InWorldConduitNodeHost> {

    private final Level level;
    private final BlockPos pos;
    private final AE2ConduitType type;
    private final IManagedGridNode mainNode;

    public AE2InWorldConduitNodeHost(Level level, BlockPos pos, AE2ConduitType type) {
        this.level = level;
        this.pos = pos;
        this.type = type;
        mainNode = GridHelper.createManagedNode(this, new GridNodeListener())
            .setVisualRepresentation(type.getConduitItem())
            .setInWorldNode(true)
            .setTagName("conduit");
        if (type.isDense()) {
            mainNode.setFlags(GridFlags.DENSE_CAPACITY);
        }
    }

    @Nullable
    @Override
    public IGridNode getGridNode(Direction dir) {
        return mainNode.getNode();
    }

    @Override
    public AECableType getCableConnectionType(Direction dir) {
        return AECableType.SMART;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        mainNode.saveToNBT(nbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        mainNode.loadFromNBT(nbt);
    }

    void onSecurityBreach() {
        if (level.getBlockEntity(pos) instanceof ConduitBlockEntity conduit) {
            conduit.removeType(type, true);
        }
    }

    @Override
    public void onCreated(IConduitType<?> type, Level level, BlockPos pos, @Nullable Player player) {
        if (player != null) {
            mainNode.setOwningPlayer(player);
        }
        mainNode.create(level, pos);
    }

    @Override
    public void updateConnection(IConduitType<?> type, Set<Direction> connectedSides) {
        mainNode.setExposedOnSides(connectedSides);
    }

    @Override
    public void onRemoved(IConduitType<?> type, Level level, BlockPos pos) {
        mainNode.destroy();
    }

    public boolean canPlayerModify(Player player) {
        return mainNode.isReady() && mainNode.getGrid().getSecurityService().hasPermission(player, SecurityPermissions.BUILD);
    }
}
