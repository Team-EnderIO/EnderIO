package com.enderio.base.common.item.tool;

import com.enderio.base.common.init.EIOFluids;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.base.common.tag.EIOTags;
import com.enderio.base.common.util.ExperienceUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class ExperienceRodItem extends Item {
    public ExperienceRodItem(Properties pProperties) {
        super(pProperties.stacksTo(1));
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        Level level = context.getLevel();
        if (level.isClientSide())
            return InteractionResult.SUCCESS;

        Player player = context.getPlayer();
        BlockPos pos = context.getClickedPos();
        Direction side = context.getClickedFace();

        boolean wasSuccess = false;
        if (player.isShiftKeyDown()) {
            wasSuccess = transferFromPlayerToBlock(player, level, pos, side);
        } else {
            wasSuccess = transferFromBlockToPlayer(player, level, pos, side);
        }

        if (wasSuccess) {
            // TODO: Particle and sound effect.
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    private static boolean transferFromBlockToPlayer(Player player, Level level, BlockPos pos, Direction side) {
        try {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity != null) {
                return blockEntity.getCapability(ForgeCapabilities.FLUID_HANDLER, side).map(fluidHandler -> {

                    FluidStack availableFluid = fluidHandler.getFluidInTank(0);
                    if (availableFluid.getFluid().is(EIOTags.Fluids.EXPERIENCE) && availableFluid.getAmount() > 0) {
                        int requiredXp = player.getXpNeededForNextLevel();
                        int fluidVolume = requiredXp * ExperienceUtil.EXPTOFLUID;

                        FluidStack drained = fluidHandler.drain(fluidVolume, IFluidHandler.FluidAction.EXECUTE);

                        if (!drained.isEmpty()) {
                            player.giveExperiencePoints(drained.getAmount() / ExperienceUtil.EXPTOFLUID);
                            return true;
                        }
                    }

                    return true;
                }).orElse(false);
            }
        } catch (ArithmeticException ex) {
            player.displayClientMessage(EIOLang.TOO_MANY_LEVELS, true);
        }

        return false;
    }

    private static boolean transferFromPlayerToBlock(Player player, Level level, BlockPos pos, Direction side) {
        try {
            if (player.experienceLevel <= 0 && player.experienceProgress <= 0.0f) {
                return false;
            }

            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity != null) {
                return blockEntity.getCapability(ForgeCapabilities.FLUID_HANDLER, side).map(fluidHandler -> {
                    int fluidVolume = ExperienceUtil.getPlayerTotalXp(player) * ExperienceUtil.EXPTOFLUID;
                    FluidStack fs = new FluidStack(EIOFluids.XP_JUICE.getSource(), fluidVolume);
                    int takenVolume = fluidHandler.fill(fs, IFluidHandler.FluidAction.EXECUTE);
                    if (takenVolume > 0) {
                        player.giveExperiencePoints(-takenVolume / ExperienceUtil.EXPTOFLUID);
                    }

                    return true;
                }).orElse(false);
            }
        } catch (ArithmeticException ex) {
            player.displayClientMessage(EIOLang.TOO_MANY_LEVELS, true);
        }

        return false;
    }
}
