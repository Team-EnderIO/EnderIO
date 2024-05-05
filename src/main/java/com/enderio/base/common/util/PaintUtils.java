package com.enderio.base.common.util;

import com.enderio.base.common.block.painted.IPaintedBlock;
import com.enderio.base.common.init.EIODataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class PaintUtils {
    public static Block getBlockFromRL(String rl) {
        return BuiltInRegistries.BLOCK.get(new ResourceLocation(rl));
    }

    /**
     * @deprecated Use stack.get(EIODataComponents.BLOCK_PAINT) directly.
     */
    @Deprecated(forRemoval = true, since = "6.1")
    @Nullable
    public static Block getPaint(ItemStack stack) {
        var paintData = stack.get(EIODataComponents.BLOCK_PAINT);
        if (paintData != null) {
            return paintData.paint();
        }

        return null;
    }

    public static Optional<SoundEvent> getPlaceSound(BlockState state, Level level, BlockPos pos, Player player, Class<? extends BlockItem> blockItemClass) {
        if (level.isClientSide()) {
            return Optional.of(List.of(InteractionHand.values()).stream()
                .map(player::getItemInHand)
                .filter(itemStack -> blockItemClass.isInstance(itemStack.getItem()))
                .map(PaintUtils::getPaint)
                .filter(Objects::nonNull)
                .map(block -> block.getSoundType(block.defaultBlockState(), level, pos, player))
                .map(SoundType::getPlaceSound)
                .findFirst()
                .orElse(SoundType.WOOD.getPlaceSound()));
        }
        if (state.getBlock() instanceof IPaintedBlock iPaintedBlock) {
            return Optional.of(iPaintedBlock.getSoundType(state, level, pos, player).getPlaceSound());
        } else {
            return Optional.empty();
        }
    }
}
