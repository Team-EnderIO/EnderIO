package com.enderio.enderio.api.armory.upgrades.hooks.tools;

import com.enderio.enderio.api.armory.upgrades.DarkSteelUpgradeContainer;
import com.enderio.enderio.api.armory.upgrades.hooks.DarkSteelHookContext;
import com.enderio.enderio.api.armory.upgrades.hooks.DarkSteelUpgradeHookTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Called when the item is used to mine a block, after the vanilla behaviour (i.e. durability loss) and before the block is removed.
 */
public interface MineBlockHook {
    void mineBlock(DarkSteelHookContext context, Level level, BlockState state, BlockPos pos, LivingEntity miningEntity);

    static void mineBlock(DarkSteelUpgradeContainer container, DarkSteelHookContext context, Level level, BlockState state, BlockPos pos,
        LivingEntity miningEntity) {
        for (MineBlockHook hook : container.getHooks(DarkSteelUpgradeHookTypes.MINE_BLOCK)) {
            hook.mineBlock(context, level, state, pos, miningEntity);
        }
    }
}
