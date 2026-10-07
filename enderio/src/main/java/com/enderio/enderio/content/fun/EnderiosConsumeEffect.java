package com.enderio.enderio.content.fun;

import com.enderio.enderio.config.base.BaseConfig;
import com.enderio.enderio.init.EIOConsumableTypes;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.BlockUtil;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.fox.Fox;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.consume_effects.ConsumeEffect;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

public enum EnderiosConsumeEffect implements ConsumeEffect {
    INSTANCE;

    public static final MapCodec<EnderiosConsumeEffect> CODEC = MapCodec.unit(INSTANCE);
    public static final StreamCodec<RegistryFriendlyByteBuf, EnderiosConsumeEffect> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public Type<EnderiosConsumeEffect> getType() {
        return EIOConsumableTypes.ENDERIOS_CONSUME_EFFECT_TYPE.get();
    }

    @Override
    public boolean apply(Level level, ItemStack itemStack, LivingEntity livingEntity) {
        if (livingEntity.getRandom().nextFloat() < BaseConfig.COMMON.ITEMS.ENDERIOS_CHANCE.get()) {
            double range = BaseConfig.COMMON.ITEMS.ENDERIOS_RANGE.get();

            // Copied from TeleportRandomlyConsumeEffect.
            for (int attempt = 0; attempt < 16; attempt++) {
                double xx = livingEntity.getX() + (livingEntity.getRandom().nextDouble() - 0.5) * range;
                double yy = Mth.clamp(
                    livingEntity.getY() + (livingEntity.getRandom().nextDouble() - 0.5) * range,
                    level.getMinY(),
                    level.getMinY() + ((ServerLevel)level).getLogicalHeight() - 1
                );
                double zz = livingEntity.getZ() + (livingEntity.getRandom().nextDouble() - 0.5) * range;
                if (livingEntity.isPassenger()) {
                    livingEntity.stopRiding();
                }

                Vec3 oldPos = livingEntity.position();
                // Neo: Pass on the consumed stack to fire the appropriate sub-event of livingEntityTeleportEvent
                if (livingEntity.randomTeleport(xx, yy, zz, true, BlockTags.CONSUMABLE_DOES_NOT_TELEPORT_TO/*, stack*/)) {
                    level.gameEvent(GameEvent.TELEPORT, oldPos, GameEvent.Context.of(livingEntity));
                    SoundSource soundSource;
                    SoundEvent soundEvent;
                    if (livingEntity instanceof Fox) {
                        soundEvent = SoundEvents.FOX_TELEPORT;
                        soundSource = SoundSource.NEUTRAL;
                    } else {
                        soundEvent = SoundEvents.CHORUS_FRUIT_TELEPORT;
                        soundSource = SoundSource.PLAYERS;
                    }

                    level.playSound(null, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), soundEvent, soundSource);
//                if (this.directionalParticles) {
                    BlockPos origin = BlockPos.containing(oldPos);
                    BlockPos target = livingEntity.blockPosition();
                    level.levelEvent(2017, origin, BlockUtil.clampedPackDifferenceInPosition(origin, target, 127, 127, 127));
//                }

                    livingEntity.resetFallDistance();
                    livingEntity.resetCurrentImpulseContext();
                }
            }
            return true;
        }

        return false;
    }
}
