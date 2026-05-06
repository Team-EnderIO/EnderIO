package com.enderio.modded_conduits.common.modules.appeng;

import appeng.api.networking.GridFlags;
import appeng.api.networking.GridHelper;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IInWorldGridNodeHost;
import appeng.api.networking.IManagedGridNode;
import appeng.api.util.AECableType;
import com.enderio.core.common.serialization.ValueIOSerializableHolder;
import com.enderio.enderio.api.EnderIORegistries;
import com.enderio.enderio.api.conduits.Conduit;
import com.enderio.enderio.api.conduits.network.node.NodeData;
import com.enderio.enderio.api.conduits.network.node.NodeDataType;
import com.enderio.enderio.content.conduits.ConduitBlockItem;
import com.enderio.modded_conduits.config.ModdedConduitsConfig;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

import java.util.Set;

public final class MEConduitNodeData implements NodeData, IInWorldGridNodeHost {

    public static final MapCodec<MEConduitNodeData> CODEC = RecordCodecBuilder.mapCodec(inst -> inst
        .group(ValueIOSerializableHolder.<IManagedGridNode>codec().fieldOf("main_node").forGetter(data -> data.mainNodeHolder))
        .apply(inst, MEConduitNodeData::new));

    public static final NodeDataType<MEConduitNodeData> TYPE = new NodeDataType<>(CODEC, MEConduitNodeData::new);

    private static final Logger LOGGER = LogUtils.getLogger();

    private @Nullable ValueIOSerializableHolder<IManagedGridNode> mainNodeHolder;
    private boolean isNodeDestroyed;

    private AECableType cableType = AECableType.SMART;

    public MEConduitNodeData() {
    }

    private MEConduitNodeData(ValueIOSerializableHolder<IManagedGridNode> mainNodeHolder) {
        this.mainNodeHolder = mainNodeHolder;
    }

    public boolean isMainNodeInitialized() {
        return !isNodeDestroyed && mainNodeHolder != null && mainNodeHolder.isPresent();
    }

    public void init(MEConduit conduit, Level level, BlockPos pos, @Nullable Player player) {
        if (isMainNodeInitialized() && mainNodeHolder.get().isReady()) {
            return;
        }

        // Init node if it isn't already.
        if (!isMainNodeInitialized()) {
            Holder<Conduit<?, ?>> asHolder = level.registryAccess()
                .lookupOrThrow(EnderIORegistries.Keys.CONDUIT)
                .wrapAsHolder(conduit);

            var mainNode = GridHelper.createManagedNode(this, GridNodeListener.INSTANCE)
                .setVisualRepresentation(ConduitBlockItem.getStackFor(asHolder, 1))
                .setInWorldNode(true)
                .setTagName("conduit")
                .setGridColor(conduit.color());

            mainNode.setIdlePowerUsage(conduit.isDense()
                ? ModdedConduitsConfig.COMMON.AE2.DENSE_ME_POWER_USAGE_PER_TICK.get()
                : ModdedConduitsConfig.COMMON.AE2.NORMAL_ME_POWER_USAGE_PER_TICK.get());

            if (conduit.isDense()) {
                mainNode.setFlags(GridFlags.DENSE_CAPACITY);
            }

            this.cableType = conduit.isDense() ? AECableType.DENSE_SMART : AECableType.SMART;

            // Load any data (or store the fresh node if we have no data)
            try (ProblemReporter.ScopedCollector reporter = new ProblemReporter.ScopedCollector(() -> "me-conduit@" + pos, LOGGER)) {
                if (mainNodeHolder == null) {
                    mainNodeHolder = new ValueIOSerializableHolder<>(mainNode);
                } else {
                    mainNodeHolder.inflate(mainNode, level.registryAccess(), reporter);
                }
            }
        }

        if (player != null) {
            mainNodeHolder.get().setOwningPlayer(player);
        }

        GridHelper.onFirstTick(level.getBlockEntity(pos), _ -> {
            if (!mainNodeHolder.get().isReady()) {
                mainNodeHolder.get().create(level, pos);
            }
        });
    }

    public void destroy() {
        if (!isMainNodeInitialized() || isNodeDestroyed) {
            return;
        }

        mainNodeHolder.get().destroy();
        isNodeDestroyed = true;
    }

    public void setExposedSides(Set<Direction> connectedSides) {
        if (isMainNodeInitialized()) {
            mainNodeHolder.get().setExposedOnSides(connectedSides);
        }
    }

    @Override
    public NodeDataType<?> type() {
        return TYPE;
    }

    @Override
    public @Nullable IGridNode getGridNode(Direction dir) {
        return mainNodeHolder != null ? mainNodeHolder.get().getNode() : null;
    }

    @Override
    public AECableType getCableConnectionType(Direction dir) {
        return cableType;
    }
}
