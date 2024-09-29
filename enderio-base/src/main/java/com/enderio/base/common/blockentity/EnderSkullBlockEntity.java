package com.enderio.base.common.blockentity;

import com.enderio.base.common.init.EIOBlockEntities;
import me.liliandev.ensure.ensures.EnsureSide;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class EnderSkullBlockEntity extends BlockEntity {
    private float animationticks = 0;

    public EnderSkullBlockEntity(BlockPos pos, BlockState blockState) {
        super(EIOBlockEntities.ENDER_SKULL.get(), pos, blockState);
    }


    @EnsureSide(EnsureSide.Side.CLIENT)
    public float getAnimation(float partialTick) {
        return animationticks;
    }


    @EnsureSide(EnsureSide.Side.CLIENT)
    public void setAnimation(float ticks) {
        animationticks = ticks;
    }


    @EnsureSide(EnsureSide.Side.CLIENT)
    public static void animation(Level level, BlockPos blockPos, BlockState state, EnderSkullBlockEntity enderSkull) {
        if (enderSkull.animationticks > 0) {
            enderSkull.animationticks--;
        }
    }
}
