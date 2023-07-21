package com.enderio.base.common.item.misc;

import com.enderio.base.client.renderer.item.EnderSkullIBEWLR;
import com.enderio.base.common.init.EIOBlocks;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.Direction;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.util.NonNullLazy;

import java.util.function.Consumer;

public class EnderSkullBlockItem extends StandingAndWallBlockItem {

    public EnderSkullBlockItem(Block block, Properties properties, Direction attachmentDirection) {
        super(block, EIOBlocks.WALL_ENDERMAN_SKULL.get(), properties, attachmentDirection);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {

            final NonNullLazy<BlockEntityWithoutLevelRenderer> renderer = NonNullLazy.of(() -> EnderSkullIBEWLR.INSTANCE);
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return renderer.get();
            }
        });
    }

    @Override
    public String getDescriptionId() {
        return super.getDescriptionId() + "_wall";
    }
}
