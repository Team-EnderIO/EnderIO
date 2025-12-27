package com.enderio.enderio.content.paint;

import com.enderio.enderio.content.paint.block.PaintedBlock;
import com.enderio.enderio.init.EIODataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class PaintUtils {
    public static Block getBlockFromRL(Identifier rl) {
        return BuiltInRegistries.BLOCK.getValue(rl);
    }

    public static Block getBlockFromString(String rl) {
        return BuiltInRegistries.BLOCK.getValue(Identifier.parse(rl));
    }

    public static Optional<SoundEvent> getPlaceSound(BlockState state, Level level, BlockPos pos, Player player, Class<? extends BlockItem> blockItemClass) {
        if (level.isClientSide()) {
            return Optional.of(List.of(InteractionHand.values()).stream()
                .map(player::getItemInHand)
                .filter(itemStack -> blockItemClass.isInstance(itemStack.getItem()))
                .map(stack -> stack.get(EIODataComponents.BLOCK_PAINT))
                .filter(Objects::nonNull)
                .map(BlockPaintData::paint)
                .map(block -> block.getSoundType(block.defaultBlockState(), level, pos, player))
                .map(SoundType::getPlaceSound)
                .findFirst()
                .orElse(SoundType.WOOD.getPlaceSound()));
        }
        if (state.getBlock() instanceof PaintedBlock paintedBlock) {
            return Optional.of(paintedBlock.getSoundType(state, level, pos, player).getPlaceSound());
        } else {
            return Optional.empty();
        }
    }
}
