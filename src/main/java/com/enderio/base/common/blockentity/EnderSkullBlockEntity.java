package com.enderio.base.common.blockentity;

import com.enderio.api.UseOnly;
import com.enderio.base.common.init.EIOBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fml.LogicalSide;

public class EnderSkullBlockEntity extends BlockEntity {
    private float animationticks = 0;

    public EnderSkullBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(EIOBlockEntities.ENDER_SKULL.get(), pos, blockState);
    }

    @UseOnly(LogicalSide.CLIENT)
    public float getAnimation(float partialTick) {
        return animationticks;
    }

    @UseOnly(LogicalSide.CLIENT)
    public void setAnimation(float ticks) {
        animationticks = ticks;
    }

    @UseOnly(LogicalSide.CLIENT)
    public static void animation(Level level, BlockPos blockPos, BlockState state, EnderSkullBlockEntity enderSkull) {
        if (enderSkull.animationticks > 0) {
            enderSkull.animationticks--;
        }
    }
}
