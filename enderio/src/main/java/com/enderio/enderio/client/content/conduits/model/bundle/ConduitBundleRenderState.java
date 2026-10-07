package com.enderio.enderio.client.content.conduits.model.bundle;

import com.enderio.core.annotations.UseOnly;
import com.enderio.enderio.api.conduits.Conduit;
import com.enderio.enderio.api.conduits.bundle.ConduitBundle;
import com.enderio.enderio.client.content.conduits.model.modifier.ConduitModelModifiers;
import com.enderio.enderio.content.conduits.OffsetHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConduitBundleRenderState {
    public static final ModelProperty<ConduitBundleRenderState> PROPERTY = new ModelProperty<>();

    private Direction.Axis mainAxis;
    private List<Holder<Conduit<?, ?>>> conduits;
    private Map<Holder<Conduit<?, ?>>, CompoundTag> extraWorldData;
    private Map<Direction, List<Holder<Conduit<?, ?>>>> conduitsByDirection;
    private Map<Direction, Map<Holder<Conduit<?, ?>>, ConduitConnectionRenderState>> conduitConnections;

    private boolean hasFacade;
    private BlockState facadeBlockstate;
    private boolean doesFacadeHideConduits;

    @UseOnly(LogicalSide.CLIENT)
    public static ConduitBundleRenderState of(ConduitBundle bundle) {
        var renderState = new ConduitBundleRenderState();

        renderState.mainAxis = OffsetHelper.findMainAxis(bundle);
        renderState.conduits = List.copyOf(bundle.getConduits());

        renderState.extraWorldData = new HashMap<>();
        for (var conduit : renderState.conduits) {
            var tag = bundle.getConduitExtraWorldData(conduit);
            if (tag != null) {
                renderState.extraWorldData.put(conduit, tag.copy());
            }
        }

        renderState.conduitsByDirection = new HashMap<>();
        for (var side : Direction.values()) {
            renderState.conduitsByDirection.put(side, bundle.getConnectedConduits(side));
        }

        renderState.conduitConnections = new HashMap<>();
        for (var side : Direction.values()) {
            HashMap<Holder<Conduit<?, ?>>, ConduitConnectionRenderState> conduits = new HashMap<>();
            for (var conduit : renderState.conduits) {
                if (bundle.getConnectionStatus(conduit, side).isEndpoint()) {
                    var connectionConfig = bundle.getConnectionConfig(conduit, side);
                    var connectionRenderState = ConduitConnectionRenderState.of(connectionConfig);
                    conduits.put(conduit, connectionRenderState);
                }
            }

            renderState.conduitConnections.put(side, conduits);
        }

        renderState.hasFacade = bundle.hasFacade();
        if (renderState.hasFacade) {
            renderState.facadeBlockstate = bundle.getFacadeBlock().defaultBlockState();
            // Facades that render as a full solid cube fully cover the conduits, so skip
            // rendering them to avoid z-fighting between the facade faces and conduit
            // connector plates (GH-1119). isSolidRender is used rather than canOcclude so
            // that partial blocks (slabs etc.) keep the conduits visible.
            renderState.doesFacadeHideConduits = bundle.getFacadeType().doesHideConduits()
                    || renderState.facadeBlockstate.isSolidRender(EmptyBlockGetter.INSTANCE, BlockPos.ZERO);
        } else {
            renderState.facadeBlockstate = Blocks.AIR.defaultBlockState();
            renderState.doesFacadeHideConduits = false;
        }

        return renderState;
    }

    public List<Holder<Conduit<?, ?>>> conduits() {
        return conduits;
    }

    @Nullable
    public CompoundTag getExtraWorldData(Holder<Conduit<?, ?>> conduit) {
        return extraWorldData.get(conduit);
    }

    public List<Holder<Conduit<?, ?>>> getConnectedConduits(Direction side) {
        return conduitsByDirection.getOrDefault(side, List.of());
    }

    public boolean isConnectionEndpoint(Direction side) {
        return !conduitConnections.get(side).isEmpty();
    }

    public ConduitConnectionRenderState getConnectionState(Direction side, Holder<Conduit<?, ?>> conduit) {
        return conduitConnections.get(side).get(conduit);
    }

    public Direction.Axis mainAxis() {
        return mainAxis;
    }

    public ResourceLocation getTexture(Holder<Conduit<?, ?>> conduit) {
        var modifier = ConduitModelModifiers.getModifier(conduit.value().type());
        if (modifier != null) {
            return modifier.getTexture(conduit, getExtraWorldData(conduit));
        } else {
            return conduit.value().texture();
        }
    }

    public boolean hasFacade() {
        return hasFacade;
    }

    public BlockState facade() {
        return facadeBlockstate;
    }

    public boolean doesFacadeHideConduits() {
        return doesFacadeHideConduits;
    }

}
