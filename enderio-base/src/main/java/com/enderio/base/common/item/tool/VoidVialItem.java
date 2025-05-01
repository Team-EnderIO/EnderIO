package com.enderio.base.common.item.tool;

import com.enderio.base.common.init.EIOFluids;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.base.common.tag.EIOTags;
import com.enderio.base.common.util.ExperienceUtil;
import com.enderio.core.common.network.EmitParticlePacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.network.PacketDistributor;

// TODO: Change behaviour to add xp tank.
public class VoidVialItem extends Item {
    public VoidVialItem(Properties pProperties) {
        super(pProperties.stacksTo(1));
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        Level level = context.getLevel();
        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.SUCCESS;
        }

        Player player = context.getPlayer();
        BlockPos pos = context.getClickedPos();

        boolean wasSuccess;
        if (player.isShiftKeyDown()) {
            wasSuccess = transferFromPlayerToBlock(player, level, pos);
        } else {
            wasSuccess = transferFromBlockToPlayer(player, level, pos);
        }

        if (wasSuccess) {
            var particle = ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, 0.27450980f, 0.88627451f,
                    0.29411765f);

            PacketDistributor.sendToPlayersTrackingChunk(serverLevel, new ChunkPos(pos),
                    new EmitParticlePacket(particle, pos, 0.2, 0.8, 0.2));

            level.playSound(null, pos, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.1f,
                    0.5F * ((level.random.nextFloat() - level.random.nextFloat()) * 0.7F + 1.8F));
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    private static boolean transferFromBlockToPlayer(Player player, Level level, BlockPos pos) {
        try {
            var fluidHandler = level.getCapability(Capabilities.FluidHandler.BLOCK, pos, null);
            if (fluidHandler != null) {
                FluidStack availableFluid = fluidHandler.getFluidInTank(0);
                if (availableFluid.is(EIOTags.Fluids.EXPERIENCE) && availableFluid.getAmount() > 0) {
                    int requiredXp = player.getXpNeededForNextLevel();
                    int fluidVolume = requiredXp * ExperienceUtil.EXP_TO_FLUID;

                    FluidStack drained = fluidHandler.drain(fluidVolume, IFluidHandler.FluidAction.EXECUTE);

                    if (!drained.isEmpty()) {
                        player.giveExperiencePoints(drained.getAmount() / ExperienceUtil.EXP_TO_FLUID);
                        return true;
                    }
                }

                return false;
            }
        } catch (ArithmeticException ex) {
            player.displayClientMessage(EIOLang.TOO_MANY_LEVELS, true);
        }

        return false;
    }

    private static boolean transferFromPlayerToBlock(Player player, Level level, BlockPos pos) {
        try {
            if (player.experienceLevel <= 0 && player.experienceProgress <= 0.0f) {
                return false;
            }

            var fluidHandler = level.getCapability(Capabilities.FluidHandler.BLOCK, pos, null);
            if (fluidHandler != null) {
                long fluidVolume = ExperienceUtil.getPlayerTotalXp(player) * ExperienceUtil.EXP_TO_FLUID;
                int cappedVolume = (int) Math.min(Integer.MAX_VALUE, fluidVolume);
                FluidStack fs = new FluidStack(EIOFluids.XP_JUICE.getSource(), cappedVolume);
                int takenVolume = fluidHandler.fill(fs, IFluidHandler.FluidAction.EXECUTE);
                if (takenVolume > 0) {
                    player.giveExperiencePoints(-takenVolume / ExperienceUtil.EXP_TO_FLUID);
                    return true;
                }

                return false;
            }
        } catch (ArithmeticException ex) {
            player.displayClientMessage(EIOLang.TOO_MANY_LEVELS, true);
        }

        return false;
    }
}
