package com.enderio.modded_conduits.common.modules.cc_tweaked;

import com.enderio.enderio.api.conduits.network.node.NodeData;
import com.enderio.enderio.api.conduits.network.node.NodeDataType;
import dan200.computercraft.api.network.wired.WiredNode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

/**
 * Node data for the CC: Tweaked network conduit.
 * Holds the WiredElement and WiredNode for this conduit instance.
 *
 * Note: The CC:Tweaked wired network graph is rebuilt dynamically on chunk load,
 * so the WiredNode itself does not need to be persisted to NBT.
 */
public class CCConduitNodeData implements NodeData {

    public static final NodeDataType<CCConduitNodeData> TYPE = new NodeDataType<>(null, CCConduitNodeData::new);

    @Nullable
    private Level level;
    @Nullable
    private BlockPos pos;

    @Nullable
    private CCConduitElement element;

    public void initialize(Level level, BlockPos pos) {
        this.level = level;
        this.pos = pos;
    }

    public boolean isInitialized() {
        return level != null && pos != null;
    }

    public WiredNode getNode() {
        return getElement().getNode();
    }

    public CCConduitElement getElement() {
        if (element == null) {
            if (level == null || pos == null) {
                throw new IllegalStateException("CCConduitNodeData not initialized with level and pos");
            }
            element = new CCConduitElement(level, pos);
        }
        return element;
    }

    @Override
    public NodeDataType<?> type() {
        return TYPE;
    }
}
