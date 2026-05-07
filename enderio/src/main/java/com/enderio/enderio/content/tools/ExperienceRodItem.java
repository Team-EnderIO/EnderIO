package com.enderio.enderio.content.tools;

import com.enderio.core.common.network.EmitParticlePacket;
import com.enderio.enderio.foundation.lang.EIOCommonLang;
import com.enderio.enderio.foundation.util.ExperienceUtil;
import com.enderio.enderio.init.EIOFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

public class ExperienceRodItem extends Item {
    public ExperienceRodItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        Level level = context.getLevel();
        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.SUCCESS;
        }

        Player player = context.getPlayer();
        BlockPos pos = context.getClickedPos();
        Direction side = context.getClickedFace();

        boolean wasSuccess;
        if (player.isShiftKeyDown()) {
            wasSuccess = transferFromPlayerToBlock(player, level, pos, side);
        } else {
            wasSuccess = transferFromBlockToPlayer(player, level, pos, side);
        }

        if (wasSuccess) {
            var particle = ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, 0.27450980f, 0.88627451f,
                0.29411765f);

            PacketDistributor.sendToPlayersTrackingChunk(serverLevel, ChunkPos.containing(pos),
                new EmitParticlePacket(particle, pos, 0.2, 0.8, 0.2));

            level.playSound(null, pos, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.1f,
                0.5F * ((level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.7F + 1.8F));
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    private static boolean transferFromBlockToPlayer(Player player, Level level, BlockPos pos, Direction side) {
        try {
            var fluidHandler = level.getCapability(Capabilities.Fluid.BLOCK, pos, side);
            if (fluidHandler != null) {
                FluidResource availableFluid = fluidHandler.getResource(0);
                if (availableFluid.is(Tags.Fluids.EXPERIENCE) && fluidHandler.getAmountAsInt(0) > 0) {
                    try (Transaction transaction = Transaction.openRoot()) {
                        int requiredXp = player.getXpNeededForNextLevel();
                        int fluidVolume = requiredXp * ExperienceUtil.EXP_TO_FLUID;

                        int drained = fluidHandler.extract(availableFluid, fluidVolume, transaction);

                        if (drained > 0) {
                            player.giveExperiencePoints(drained / ExperienceUtil.EXP_TO_FLUID);
                            transaction.commit();
                            return true;
                        }
                    }
                }

                return false;
            }
        } catch (ArithmeticException ex) {
            player.sendOverlayMessage(EIOCommonLang.TOO_MANY_LEVELS);
        }

        return false;
    }

    private static boolean transferFromPlayerToBlock(Player player, Level level, BlockPos pos, Direction direction) {
        try {
            if (player.experienceLevel <= 0 && player.experienceProgress <= 0.0f) {
                return false;
            }

            var fluidHandler = level.getCapability(Capabilities.Fluid.BLOCK, pos, direction);
            if (fluidHandler != null) {
                try (Transaction transaction = Transaction.openRoot()) {
                    long fluidVolume = ExperienceUtil.getPlayerTotalXp(player) * ExperienceUtil.EXP_TO_FLUID;
                    int cappedVolume = (int) Math.min(Integer.MAX_VALUE, fluidVolume);
                    int takenVolume = fluidHandler.insert(FluidResource.of(EIOFluids.XP_JUICE.source()), cappedVolume, transaction);
                    if (takenVolume > 0) {
                        transaction.commit();
                        player.giveExperiencePoints(-takenVolume / ExperienceUtil.EXP_TO_FLUID);
                        return true;
                    }

                    return false;
                }
            }
        } catch (ArithmeticException ex) {
            player.sendOverlayMessage(EIOCommonLang.TOO_MANY_LEVELS);
        }

        return false;
    }
}
