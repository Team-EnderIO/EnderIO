//package com.enderio.conduits.common.integrations.ae2;
//
//import appeng.api.networking.GridFlags;
//import appeng.api.networking.GridHelper;
//import appeng.api.networking.IGridNode;
//import appeng.api.networking.IInWorldGridNodeHost;
//import appeng.api.networking.IManagedGridNode;
//import appeng.api.util.AECableType;
//import com.enderio.api.conduit.IConduitType;
//import com.enderio.api.conduit.IExtendedConduitData;
//import net.minecraft.core.BlockPos;
//import net.minecraft.core.Direction;
//import net.minecraft.nbt.CompoundTag;
//import net.minecraft.world.entity.player.Player;
//import net.minecraft.world.level.Level;
//import net.neoforged.neoforge.common.util.LazyOptional;
//import org.jetbrains.annotations.Nullable;
//
//import java.util.Set;
//
//public class AE2InWorldConduitNodeHost implements IInWorldGridNodeHost, IExtendedConduitData<AE2InWorldConduitNodeHost> {
//
//    private final AE2ConduitType type;
//    @Nullable
//    private IManagedGridNode mainNode = null;
//
//    private LazyOptional<AE2InWorldConduitNodeHost> selfCap = LazyOptional.of(() -> this);
//
//    public AE2InWorldConduitNodeHost(AE2ConduitType type) {
//        this.type = type;
//        initMainNode();
//    }
//
//    private void initMainNode() {
//        mainNode = GridHelper.createManagedNode(this, new GridNodeListener())
//            .setVisualRepresentation(type.getConduitItem())
//            .setInWorldNode(true)
//            .setTagName("conduit");
//
//        mainNode.setIdlePowerUsage(type.isDense() ? 0.4d : 0.1d);
//
//        if (type.isDense()) {
//            mainNode.setFlags(GridFlags.DENSE_CAPACITY);
//        }
//    }
//
//    @Nullable
//    @Override
//    public IGridNode getGridNode(Direction dir) {
//        if (mainNode == null) {
//            initMainNode();
//        }
//        return mainNode.getNode();
//    }
//
//    public LazyOptional<AE2InWorldConduitNodeHost> getSelfCap() {
//        if (!selfCap.isPresent()) {
//            selfCap = LazyOptional.of(() -> this);
//        }
//        return selfCap;
//    }
//
//    @Override
//    public AECableType getCableConnectionType(Direction dir) {
//        if (type.isDense()) {
//            return AECableType.DENSE_SMART;
//        }
//
//        return AECableType.SMART;
//    }
//
//    @Override
//    public CompoundTag serializeNBT() {
//        CompoundTag nbt = new CompoundTag();
//        if (mainNode != null) {
//            mainNode.saveToNBT(nbt);
//        }
//        return nbt;
//    }
//
//    @Override
//    public void deserializeNBT(CompoundTag nbt) {
//        if (mainNode == null) {
//            initMainNode();
//        }
//
//        mainNode.loadFromNBT(nbt);
//    }
//
//    @Override
//    public void onCreated(IConduitType<?> type, Level level, BlockPos pos, @Nullable Player player) {
//        if (mainNode == null) {
//            // required because onCreated() can be called after onRemoved()
//            initMainNode();
//        }
//
//        if (mainNode.isReady()) {
//            return;
//        }
//
//        if (player != null) {
//            mainNode.setOwningPlayer(player);
//        }
//
//        GridHelper.onFirstTick(level.getBlockEntity(pos), blockEntity -> mainNode.create(level, pos));
//    }
//
//    @Override
//    public void updateConnection(Set<Direction> connectedSides) {
//        if (mainNode == null) {
//            return;
//        }
//
//        mainNode.setExposedOnSides(connectedSides);
//    }
//
//    @Override
//    public void onRemoved(IConduitType<?> type, Level level, BlockPos pos) {
//        if (mainNode != null) {
//            mainNode.destroy();
//
//            // required because onCreated() can be called after onRemoved()
//            mainNode = null;
//        }
//        selfCap.invalidate();
//    }
//
//}
