package com.enderio.conduits.common.integrations;

import com.enderio.conduits.api.FacadeItem;
import com.enderio.base.api.integration.Integration;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public class ConduitSelfIntegration implements Integration {

    @Override
    public Optional<BlockState> getFacadeOf(ItemStack stack) {
        if (stack.getItem() instanceof FacadeItem facadeItem) {
            return Optional.of(facadeItem.getTexture(stack));
        }
        return Optional.empty();
    }
}
