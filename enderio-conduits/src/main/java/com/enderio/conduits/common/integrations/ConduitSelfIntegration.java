package com.enderio.conduits.common.integrations;

import com.enderio.base.api.integration.Integration;
import com.enderio.base.common.init.EIODataComponents;
import com.enderio.base.common.paint.item.PaintedSlabBlockItem;
import com.enderio.conduits.api.FacadeItem;
import java.util.Optional;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class ConduitSelfIntegration implements Integration {

    @Override
    public Optional<Block> getFacadeOf(ItemStack stack) {
        if (stack.getItem() instanceof FacadeItem facadeItem) {
            return Optional.of(facadeItem.getTexture(stack));
        }

        // TODO: Temporary
        if (stack.getItem() instanceof PaintedSlabBlockItem) {
            var paintData = stack.get(EIODataComponents.BLOCK_PAINT);
            return Optional.of(paintData.paint());
        }

        return Optional.empty();
    }
}
