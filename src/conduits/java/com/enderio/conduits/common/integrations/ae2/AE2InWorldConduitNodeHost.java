package com.enderio.conduits.common.integrations.ae2;

import appeng.api.networking.*;
import appeng.api.util.AECableType;
import com.enderio.api.conduit.IConduitType;
import com.enderio.api.conduit.IExtendedConduitData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class AE2InWorldConduitNodeHost implements IInWorldGridNodeHost, IExtendedConduitData<AE2InWorldConduitNodeHost> {

    private final AE2ConduitType type;
    private final IManagedGridNode mainNode;

    final LazyOptional<AE2InWorldConduitNodeHost> selfCap = LazyOptional.of(() -> this);

    public AE2InWorldConduitNodeHost(AE2ConduitType type) {
        this.type = type;
        mainNode = GridHelper.createManagedNode(this, new GridNodeListener())
            .setVisualRepresentation(type.getConduitItem())
            .setInWorldNode(true)
            .setTagName("conduit");
        if (type.isDense()) {
            mainNode.setFlags(GridFlags.DENSE_CAPACITY);
        }
        mainNode.setIdlePowerUsage(type.isDense() ? 2 : 0.5d);
    }

    @Nullable
    @Override
    public IGridNode getGridNode(Direction dir) {
        return mainNode.getNode();
    }

    @Override
    public AECableType getCableConnectionType(Direction dir) {
        if (type.isDense())
            return AECableType.DENSE_SMART;
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

    @Override
    public void onCreated(IConduitType<?> type, Level level, BlockPos pos, @Nullable Player player) {
        if (!mainNode.isReady()) {
            if (player != null) {
                mainNode.setOwningPlayer(player);
            }
            mainNode.create(level, pos);
        }
    }

    @Override
    public void updateConnection(Set<Direction> connectedSides) {
        mainNode.setExposedOnSides(connectedSides);
    }

    @Override
    public void onRemoved(IConduitType<?> type, Level level, BlockPos pos) {
        mainNode.destroy();
        selfCap.invalidate();
    }

}
