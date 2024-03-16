package com.enderio.api.farm;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.FakePlayer;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public interface IFarmingStation {

    ItemStack getSeedsForPos(BlockPos pos);

    int getConsumedPower();

    void addConsumedPower(int i);

    int consumeEnergy(int energy, boolean simulate);

    ItemStack getHoe();

    ItemStack getAxe();

    ItemStack getShears();

    FakePlayer getPlayer();

    boolean consumeBonemeal();

    Level getLevel();

    void collectDrops(List<ItemStack> drops, @Nullable BlockPos soil);

    Optional<ResourceLocation> getEntityType();

    default InteractionResult useStack(BlockPos soil, ItemStack stack) {
        getPlayer().setItemInHand(InteractionHand.MAIN_HAND, stack);
        UseOnContext context = new UseOnContext(getPlayer(), InteractionHand.MAIN_HAND, new BlockHitResult(Vec3.atBottomCenterOf(soil), Direction.UP, soil, false));
        InteractionResult result = stack.useOn(context);
        getPlayer().setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        return result;
    }
}
