package com.enderio.enderio.content.tools;

import com.enderio.enderio.api.EnderIOCapabilities;
import com.enderio.enderio.api.io.SideConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class YetaWrenchItem extends Item {

    public YetaWrenchItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();

        // Check for side config capability
        SideConfig sideConfig = level.getCapability(EnderIOCapabilities.SIDE_CONFIG, pos, context.getClickedFace());
        if (sideConfig != null) {
            sideConfig.cycleMode();
            return InteractionResult.SUCCESS;
        }

        return super.onItemUseFirst(stack, context);
    }
}
