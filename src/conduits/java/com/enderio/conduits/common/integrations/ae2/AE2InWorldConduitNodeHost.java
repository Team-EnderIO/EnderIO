package com.enderio.conduits.common.integrations.ae2;

import appeng.api.networking.GridFlags;
import appeng.api.networking.GridHelper;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IInWorldGridNodeHost;
import appeng.api.networking.IManagedGridNode;
import appeng.api.util.AECableType;
import com.enderio.api.conduit.ConduitType;
import com.enderio.api.conduit.ExtendedConduitData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class AE2InWorldConduitNodeHost implements IInWorldGridNodeHost, ExtendedConduitData<AE2InWorldConduitNodeHost> {

    private final AE2ConduitType type;
    @Nullable
    private IManagedGridNode mainNode = null;

    public AE2InWorldConduitNodeHost(AE2ConduitType type) {
        this.type = type;
        initMainNode();
    }

    private void initMainNode() {
        mainNode = GridHelper.createManagedNode(this, new GridNodeListener())
            .setVisualRepresentation(type.getConduitItem())
            .setInWorldNode(true)
            .setTagName("conduit");

        mainNode.setIdlePowerUsage(type.isDense() ? 0.4d : 0.1d);

        if (type.isDense()) {
            mainNode.setFlags(GridFlags.DENSE_CAPACITY);
        }
    }

    @Nullable
    @Override
    public IGridNode getGridNode(Direction dir) {
        if (mainNode == null) {
            initMainNode();
        }

        return mainNode.getNode();
    }

    @Override
    public AECableType getCableConnectionType(Direction dir) {
        if (type.isDense()) {
            return AECableType.DENSE_SMART;
        }

        return AECableType.SMART;
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider lookupProvider) {
        CompoundTag nbt = new CompoundTag();
        if (mainNode != null) {
            mainNode.saveToNBT(nbt);
        }
        return nbt;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider lookupProvider, CompoundTag nbt) {
        if (mainNode == null) {
            initMainNode();
        }

        mainNode.loadFromNBT(nbt);
    }

    @Override
    public void onCreated(ConduitType<?> type, Level level, BlockPos pos, @Nullable Player player) {
        if (mainNode == null) {
            // required because onCreated() can be called after onRemoved()
            initMainNode();
        }

        if (mainNode.isReady()) {
            return;
        }

        if (player != null) {
            mainNode.setOwningPlayer(player);
        }

        GridHelper.onFirstTick(level.getBlockEntity(pos), blockEntity -> mainNode.create(level, pos));
    }

    @Override
    public void updateConnection(Set<Direction> connectedSides) {
        if (mainNode == null) {
            return;
        }

        mainNode.setExposedOnSides(connectedSides);
    }

    @Override
    public void onRemoved(ConduitType<?> type, Level level, BlockPos pos) {
        if (mainNode != null) {
            mainNode.destroy();

            // required because onCreated() can be called after onRemoved()
            mainNode = null;
        }
        level.invalidateCapabilities(pos);
    }

}
