package com.enderio.base.common.block;

import com.enderio.base.common.config.BaseConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class ReinforcedObsidianBlock extends Block {

    private static final int[] COLS = { 0x3c3056, 0x241e31, 0x1e182b, 0x0e0e15, 0x07070b };

    public ReinforcedObsidianBlock(Properties props) {
        super(props);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource randomSource) {
        if (BaseConfig.CLIENT.MACHINE_PARTICLES.get() && randomSource.nextFloat() < .25f) {
            Direction face = Direction.getRandom(randomSource);
            BlockPos spawnPos = pos.relative(face, 1);
            if (!level.getBlockState(spawnPos).isSolidRender(level, spawnPos)) {

                double xd = face.getStepX() == 0 ? randomSource.nextDouble() : face.getStepX() < 0 ? -0.05 : 1.05;
                double yd = face.getStepY() == 0 ? randomSource.nextDouble() : face.getStepY() < 0 ? -0.05 : 1.05;
                double zd = face.getStepZ() == 0 ? randomSource.nextDouble() : face.getStepZ() < 0 ? -0.05 : 1.05;

                double x = pos.getX() + xd;
                double y = pos.getY() + yd;
                double z = pos.getZ() + zd;

                int col = COLS[randomSource.nextInt(COLS.length)];
                level.addParticle(new DustParticleOptions(Vec3.fromRGB24(col).toVector3f(), 1.0F), x, y, z, 0.0D, 0.0D, 0.0D);
            }
        }
    }

    @Override
    public boolean canDropFromExplosion(BlockState state, BlockGetter world, BlockPos pos, Explosion explosion) {
        return false;
    }

}
