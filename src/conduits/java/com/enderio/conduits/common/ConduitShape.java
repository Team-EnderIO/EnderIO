package com.enderio.conduits.common;

import com.enderio.api.conduit.IConduitType;
import com.enderio.conduits.common.blockentity.ConduitBundle;
import com.enderio.conduits.common.blockentity.OffsetHelper;
import com.enderio.conduits.common.blockentity.connection.DynamicConnectionState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ConduitShape {
    private final Map<IConduitType<?>, VoxelShape> conduitShapes = new HashMap<>();
    private final Map<Direction, VoxelShape> directionShapes = new HashMap<>();
    private static final VoxelShape connector = Block.box(2.5f, 2.5, 15f, 13.5f, 13.5f, 16f);
    private static final VoxelShape connection = Block.box(6.5f, 6.5f, 9.5, 9.5f, 9.5f, 16);
    private static final VoxelShape core = Block.box(6.5f, 6.5f, 6.5f, 9.5f, 9.5f, 9.5f);
    private VoxelShape totalShape = core;

    public ConduitShape() {

    }

    public void updateConduit(ConduitBundle bundle) {
        this.conduitShapes.clear();
        this.directionShapes.clear();
        for (IConduitType<?> type : bundle.getTypes()) {
            updateShapeForConduit(bundle, type);
        }
        updateTotalShape();
    }

    public VoxelShape getShapeFromHit(BlockPos pos, HitResult result) {
        return Optional.ofNullable(this.conduitShapes.get(getConduit(pos, result))).orElse(Shapes.empty());
    }

    @Nullable
    public IConduitType<?> getConduit(BlockPos pos, HitResult result) {
        return getLookUpValue(conduitShapes, pos, result);
    }

    @Nullable
    public Direction getDirection(BlockPos pos, HitResult result) {
        return getLookUpValue(directionShapes, pos, result);
    }

    @Nullable
    private <T> T getLookUpValue(Map<T, VoxelShape> shapes, BlockPos pos, HitResult result) {
        for (Map.Entry<T, VoxelShape> entry : shapes.entrySet()) {
            Vec3 vec3 = result.getLocation().subtract(pos.getX(), pos.getY(), pos.getZ());
            Optional<Vec3> point = entry.getValue().closestPointTo(vec3);
            if (point.isEmpty())
                continue;

            if (point.get().closerThan(vec3, Mth.EPSILON)) { // can't be 0 due to double
                return entry.getKey();
            }
        }
        return null;
    }

    private void updateTotalShape() {
        this.totalShape = Shapes.empty();
        this.conduitShapes.values().forEach(s -> this.totalShape = Shapes.join(this.totalShape, s, BooleanOp.OR));
        totalShape.optimize();
    }

    public VoxelShape getTotalShape() {
        return this.totalShape;
    }

    private void updateShapeForConduit(ConduitBundle conduitBundle, IConduitType<?> conduitType) {
        VoxelShape conduitShape = Shapes.empty();
        Direction.Axis axis = OffsetHelper.findMainAxis(conduitBundle);
        Map<IConduitType<?>, List<Vec3i>> offsets = new HashMap<>();
        for (Direction direction : Direction.values()) {
            VoxelShape directionShape = directionShapes.getOrDefault(direction, Shapes.empty());
            if (conduitBundle.getConnection(direction).getConnectionState(conduitType) instanceof DynamicConnectionState) {
                VoxelShape connectorShape = rotateVoxelShape(connector, direction);
                directionShape = Shapes.join(directionShape, connectorShape, BooleanOp.OR);
                conduitShape = Shapes.join(conduitShape, connectorShape, BooleanOp.OR);
            }
            var connectedTypes = conduitBundle.getConnection(direction).getConnectedTypes();
            if (connectedTypes.contains(conduitType)) {
                Vec3i offset = OffsetHelper.translationFor(direction.getAxis(),
                    OffsetHelper.offsetConduit(connectedTypes.indexOf(conduitType), connectedTypes.size()));
                offsets.computeIfAbsent(conduitType, ignored -> new ArrayList<>()).add(offset);
                VoxelShape connectionShape = rotateVoxelShape(connection, direction).move(offset.getX() * 3f / 16f, offset.getY() * 3f / 16f,
                    offset.getZ() * 3f / 16f);
                directionShape = Shapes.join(directionShape, connectionShape, BooleanOp.OR);
                conduitShape = Shapes.join(conduitShape, connectionShape, BooleanOp.OR);
            }
            directionShapes.put(direction, directionShape.optimize());
        }

        var allTypes = conduitBundle.getTypes();
        @Nullable Area box = null;
        Map<IConduitType<?>, Integer> notRendered = new HashMap<>();
        List<IConduitType<?>> rendered = new ArrayList<>();
        int i = allTypes.indexOf(conduitType);
        if (i == -1) {
            conduitShapes.put(conduitType, Shapes.block());
            return;
        }
        var type = allTypes.get(i);
        @Nullable List<Vec3i> offsetsForType = offsets.get(type);
        if (offsetsForType != null) {
            //all are pointing to the same xyz reference meaning that we can draw the core
            if (offsetsForType.stream().distinct().count() == 1) {
                rendered.add(type);
            } else {
                box = new Area(offsetsForType.toArray(new Vec3i[0]));
            }
        } else {
            notRendered.put(type, i);
        }

        Set<Vec3i> duplicateFinder = new HashSet<>();
        //rendered have only one distinct pos, so I can safely assume get(0) is valid
        List<Vec3i> duplicatePositions = rendered.stream().map(offsets::get).map(l -> l.get(0)).filter(n -> !duplicateFinder.add(n)).toList();
        for (Vec3i duplicatePosition : duplicatePositions) {
            if (box == null) {
                box = new Area(duplicatePosition);
            } else {
                box.makeContain(duplicatePosition);
            }
        }
        for (IConduitType<?> toRender : rendered) {
            if (box == null || !box.contains(offsetsForType.get(0)))
                conduitShape = Shapes.join(conduitShape,
                    core.move(offsetsForType.get(0).getX() * 3f / 16f, offsetsForType.get(0).getY() * 3f / 16f, offsetsForType.get(0).getZ() * 3f / 16f),
                    BooleanOp.OR);
        }

        if (box != null) {
            for (Map.Entry<IConduitType<?>, Integer> notRenderedEntry : notRendered.entrySet()) {
                Vec3i offset = OffsetHelper.translationFor(axis, OffsetHelper.offsetConduit(notRenderedEntry.getValue(), allTypes.size()));
                if (!box.contains(offset))
                    conduitShape = Shapes.join(conduitShape, core.move(offset.getX() * 3f / 16f, offset.getY() * 3f / 16f, offset.getZ() * 3f / 16f),
                        BooleanOp.OR);
            }
            conduitShape = Shapes.join(conduitShape, core.move(box.getMin().getX() * 3f / 16f, box.getMin().getY() * 3f / 16f, box.getMin().getZ() * 3f / 16f),
                BooleanOp.OR);
        } else {
            for (Map.Entry<IConduitType<?>, Integer> notRenderedEntry : notRendered.entrySet()) {
                Vec3i offset = OffsetHelper.translationFor(axis, OffsetHelper.offsetConduit(notRenderedEntry.getValue(), allTypes.size()));
                conduitShape = Shapes.join(conduitShape, core.move(offset.getX() * 3f / 16f, offset.getY() * 3f / 16f, offset.getZ() * 3f / 16f), BooleanOp.OR);
            }
        }
        conduitShapes.put(conduitType, conduitShape.optimize());
    }

    /**
     * Rotates a VoxelShape around the center to the specified Direction, Origin is SOUTH
     *
     * @param toRotate
     * @param direction
     * @return the rotated VoxelShape
     */
    public static VoxelShape rotateVoxelShape(VoxelShape toRotate, Direction direction) {
        VoxelShape[] buffer = new VoxelShape[] { toRotate, Shapes.empty() };
        if (direction.get2DDataValue() == -1) {
            if (direction == Direction.DOWN) {
                buffer[0].forAllBoxes(
                    (minX, minY, minZ, maxX, maxY, maxZ) -> buffer[1] = Shapes.or(buffer[1], Shapes.box(minX, 1 - maxZ, minY, maxX, 1 - minZ, maxY)));
            } else {
                buffer[0].forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> buffer[1] = Shapes.or(buffer[1], Shapes.box(minX, minZ, minY, maxX, maxZ, maxY)));
            }
            return buffer[1];
        }
        for (int i = 0; i < (direction.get2DDataValue()) % 4; i++) {
            buffer[0].forAllBoxes(
                (minX, minY, minZ, maxX, maxY, maxZ) -> buffer[1] = Shapes.or(buffer[1], Shapes.box(1 - maxZ, minY, minX, 1 - minZ, maxY, maxX)));
            buffer[0] = buffer[1];
            buffer[1] = Shapes.empty();
        }
        return buffer[0];
    }

}
