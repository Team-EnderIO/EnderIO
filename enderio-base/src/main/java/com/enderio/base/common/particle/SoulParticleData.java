package com.enderio.base.common.particle;

import com.enderio.base.common.init.EIOParticles;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Math;
import org.joml.Vector3f;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

public record SoulParticleData(BlockPos potPos, BlockPos target) implements ParticleOptions {

    public static final int LIFETIME = 50;

    public static final MapCodec<SoulParticleData> CODEC = RecordCodecBuilder.mapCodec(instance ->
        instance.group(
            BlockPos.CODEC.fieldOf("potPos").forGetter(SoulParticleData::potPos),
            BlockPos.CODEC.fieldOf("targets").forGetter(SoulParticleData::target)
        )
        .apply(instance, SoulParticleData::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, SoulParticleData> STREAM_CODEC = StreamCodec.composite(
        BlockPos.STREAM_CODEC,
        SoulParticleData::potPos,
        BlockPos.STREAM_CODEC,
        SoulParticleData::target,
        SoulParticleData::new
    );

    @Override
    public ParticleType<?> getType() {
        return EIOParticles.SOUL_PARTICLE.get();
    }

    public record BezierCurve(Vec3 base, Vec3 baseAnchor, Vec3 endAnchor, Vec3 end) {

        public Vec3 calcPos(float progress) {
            Vec3 point0 = getPointOnLine(base, baseAnchor, progress);
            Vec3 point1 = getPointOnLine(baseAnchor, endAnchor, progress);
            Vec3 point2 = getPointOnLine(endAnchor, end, progress);

            Vec3 point01 = getPointOnLine(point0, point1, progress);
            Vec3 point12 = getPointOnLine(point1, point2, progress);
            return getPointOnLine(point01, point12, progress);
        }

        private static Vec3 getPointOnLine(Vec3 start, Vec3 end, float progress) {
            return start.add(end.subtract(start).scale(progress));
        }
    }

    public BezierCurve createPosFunction(Level level) {
        Vec3 pos0Orig = potPos.getBottomCenter().add(0, 1, 0);
        Vector3f pos0 = pos0Orig.toVector3f();
        Set<Direction> nonBlockedDirs = EnumSet.noneOf(Direction.class);
        for (Direction direction: Direction.values()) {
            if (!level.getBlockState(target.relative(direction)).isSolid()) {
                nonBlockedDirs.add(direction);
            }
        }
        Set<Direction> closestDirs = EnumSet.noneOf(Direction.class);
        for (Direction.Axis axis: Direction.Axis.values()) {
            Direction negDirection = Direction.fromAxisAndDirection(axis, Direction.AxisDirection.NEGATIVE);
            Direction posDirection = Direction.fromAxisAndDirection(axis, Direction.AxisDirection.POSITIVE);
            float distNegative = pos0.distanceSquared(target.getCenter().relative(negDirection, 1).toVector3f());
            float distPositive = pos0.distanceSquared(target.getCenter().relative(posDirection, 1).toVector3f());
            if (floatsAreEqual(distNegative, distPositive, 0.0001f)) {
                if (nonBlockedDirs.contains(posDirection)) {
                    closestDirs.add(posDirection);
                    continue;
                }
                if (nonBlockedDirs.contains(negDirection)) {
                    closestDirs.add(negDirection);
                    continue;
                }
                closestDirs.add(posDirection);
                continue;
            }
            if (Float.compare(distNegative, distPositive) <0) {
                closestDirs.add(negDirection);
            } else {
                closestDirs.add(posDirection);
            }
        }
        HashSet<Direction> intersectionSet = new HashSet<>(nonBlockedDirs);
        intersectionSet.retainAll(closestDirs);
        RandomSource r = level.getRandom();
        Direction endDirection;
        if (intersectionSet.isEmpty()) {
            endDirection = closestDirs.toArray(Direction[]::new)[r.nextInt(3)];
        } else {
            endDirection = intersectionSet.toArray(Direction[]::new)[r.nextInt(intersectionSet.size())];
        }
        Vec3 posEnd = target.getCenter().relative(endDirection, 0.5f);
        return new BezierCurve(posEnd, posEnd.relative(endDirection, 1), pos0Orig.relative(Direction.UP, 1),pos0Orig);
    }

    private static boolean floatsAreEqual(float value1, float value2, float delta) {
        return Math.abs(value1 - value2) <= delta;
    }
}
