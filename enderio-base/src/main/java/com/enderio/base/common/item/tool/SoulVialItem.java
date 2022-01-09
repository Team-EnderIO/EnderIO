package com.enderio.base.common.item.tool;

import com.enderio.base.client.tooltip.IAdvancedTooltipProvider;
import com.enderio.base.common.init.EIOCapabilities;
import com.enderio.base.common.capability.IMultiCapabilityItem;
import com.enderio.base.common.capability.MultiCapabilityProvider;
import com.enderio.base.common.capability.entity.EntityStorage;
import com.enderio.base.common.capability.entity.IEntityStorage;
import com.enderio.base.common.capability.entity.StoredEntityData;
import com.enderio.base.common.item.EIOCreativeTabs;
import com.enderio.base.common.init.EIOItems;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.base.common.util.EntityCaptureUtils;
import com.enderio.base.common.util.EntityUtil;
import com.enderio.base.common.util.TooltipUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class SoulVialItem extends Item implements IMultiCapabilityItem, IAdvancedTooltipProvider {
    public SoulVialItem(Properties pProperties) {
        super(pProperties);
    }

    // Item appearance and description

    @Override
    public boolean isFoil(@Nonnull ItemStack pStack) {
        return pStack.getCapability(EIOCapabilities.ENTITY_STORAGE).map(IEntityStorage::hasStoredEntity).orElse(false);
    }

    @Override
    public void addCommonTooltips(ItemStack itemStack, @Nullable Player player, List<Component> tooltips) {
        itemStack.getCapability(EIOCapabilities.ENTITY_STORAGE).ifPresent(entityStorage -> {
            entityStorage.getStoredEntityData().getEntityType().ifPresent(entityType -> {
                tooltips.add(TooltipUtil.style(new TranslatableComponent(EntityUtil.getEntityDescriptionId(entityType))));
            });
        });
    }

    @Override
    public void addDetailedTooltips(ItemStack itemStack, @Nullable Player player, List<Component> tooltips) {
        itemStack.getCapability(EIOCapabilities.ENTITY_STORAGE).ifPresent(entityStorage -> {
            entityStorage.getStoredEntityData().getHealthState().ifPresent(health -> {
                tooltips.add(TooltipUtil.styledWithArgs(EIOLang.SOUL_VIAL_TOOLTIP_HEALTH, health.getA(), health.getB()));
            });
        });
    }

    // endregion

    // region Interactions

    // Capture logic
    @Nonnull
    @Override
    public InteractionResult interactLivingEntity(@Nonnull ItemStack pStack, @Nonnull Player pPlayer, @Nonnull LivingEntity pInteractionTarget,
        @Nonnull InteractionHand pUsedHand) {
        if (pPlayer.level.isClientSide) {
            return InteractionResult.FAIL;
        }

        return pStack.getCapability(EIOCapabilities.ENTITY_STORAGE).map(entityStorage -> {
            if (!entityStorage.hasStoredEntity()) {
                // Don't allow bottled player.
                if (pInteractionTarget instanceof Player) {
                    pPlayer.displayClientMessage(EIOLang.SOUL_VIAL_ERROR_PLAYER, true);
                    return InteractionResult.FAIL;
                }

                // Get the entity type and verify it isn't blacklisted
                switch (EntityCaptureUtils.getCapturableStatus(pInteractionTarget)) {
                case BOSS -> {
                    pPlayer.displayClientMessage(EIOLang.SOUL_VIAL_ERROR_BOSS, true);
                    return InteractionResult.FAIL;
                }
                case BLACKLISTED -> {
                    pPlayer.displayClientMessage(EIOLang.SOUL_VIAL_ERROR_BLACKLISTED, true);
                    return InteractionResult.FAIL;
                }
                case INCOMPATIBLE -> {
                    pPlayer.displayClientMessage(EIOLang.SOUL_VIAL_ERROR_FAILED, true);
                    return InteractionResult.FAIL;
                }
                }

                // No dead mobs.
                if (!pInteractionTarget.isAlive()) {
                    pPlayer.displayClientMessage(EIOLang.SOUL_VIAL_ERROR_DEAD, true);
                    return InteractionResult.FAIL;
                }

                // Create a filled vial and put the entity's NBT inside.
                ItemStack filledVial = new ItemStack(EIOItems.FILLED_SOUL_VIAL.get());
                setEntityData(filledVial, pInteractionTarget);

                // Consume a soul vial
                ItemStack hand = pPlayer.getItemInHand(pUsedHand);
                hand.shrink(1);

                // Give the player the filled vial
                if (hand.isEmpty()) {
                    pPlayer.setItemInHand(pUsedHand, filledVial);
                } else {
                    if (!pPlayer.addItem(filledVial)) {
                        pPlayer.drop(filledVial, false);
                    }
                }

                // Remove the captured mob.
                pInteractionTarget.discard();
            }

            return InteractionResult.SUCCESS;
        }).orElse(InteractionResult.SUCCESS);
    }

    // Release logic
    @Nonnull
    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        if (pContext.getLevel().isClientSide) {
            return InteractionResult.FAIL;
        }

        ItemStack itemStack = pContext.getItemInHand();
        Player player = pContext.getPlayer();

        // Only players may use the soul vial
        if (player == null) {
            return InteractionResult.FAIL;
        }

        itemStack.getCapability(EIOCapabilities.ENTITY_STORAGE).ifPresent(entityStorage -> {
            if (entityStorage.hasStoredEntity()) {
                StoredEntityData entityData = entityStorage.getStoredEntityData();

                // Get the face of the block we clicked and its position.
                Direction face = pContext.getClickedFace();
                BlockPos spawnPos = pContext.getClickedPos();

                // Get the spawn location for the mob.
                double spawnX = spawnPos.getX() + face.getStepX() + 0.5;
                double spawnY = spawnPos.getY() + face.getStepY();
                double spawnZ = spawnPos.getZ() + face.getStepZ() + 0.5;

                // Get a random rotation for the entity.
                float rotation = Mth.wrapDegrees(pContext
                    .getLevel()
                    .getRandom()
                    .nextFloat() * 360.0f);

                // Try to get the entity NBT from the item.
                Optional<Entity> entity = EntityType.create(entityData.getEntityTag(), pContext.getLevel());

                // Position the entity and add it.
                entity.ifPresent(ent -> {
                    ent.setPos(spawnX, spawnY, spawnZ);
                    ent.setYRot(rotation);
                    pContext
                        .getLevel()
                        .addFreshEntity(ent);
                });

                // Empty the soul vial.
                player.setItemInHand(pContext.getHand(), new ItemStack(EIOItems.EMPTY_SOUL_VIAL.get()));
            }
        });

        return InteractionResult.SUCCESS;
    }

    // endregion

    // region Creative tabs

    @Override
    public void fillItemCategory(@Nonnull CreativeModeTab pCategory, @Nonnull NonNullList<ItemStack> pItems) {
        if (pCategory == getItemCategory()) {
            pItems.add(new ItemStack(EIOItems.EMPTY_SOUL_VIAL.get()));
        } else if (pCategory == EIOCreativeTabs.SOULS) {
            // Register for every mob that can be captured.
            for (ResourceLocation entity : EntityCaptureUtils.getCapturableEntities()) {
                ItemStack is = new ItemStack(EIOItems.FILLED_SOUL_VIAL.get());
                setEntityType(is, entity);
                pItems.add(is);
            }
        }
    }

    @Override
    public Collection<CreativeModeTab> getCreativeTabs() {
        return Arrays.asList(getItemCategory(), EIOCreativeTabs.SOULS);
    }

    // endregion

    // region Entity Storage

    private static void setEntityType(ItemStack stack, ResourceLocation entityType) {
        stack
            .getCapability(EIOCapabilities.ENTITY_STORAGE)
            .ifPresent(storage -> {
                storage.setStoredEntityData(StoredEntityData.of(entityType));
            });
    }

    private static void setEntityData(ItemStack stack, LivingEntity entity) {
        stack
            .getCapability(EIOCapabilities.ENTITY_STORAGE)
            .ifPresent(storage -> {
                storage.setStoredEntityData(StoredEntityData.of(entity));
            });
    }

    @Nullable
    @Override
    public MultiCapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt, MultiCapabilityProvider provider) {
        provider.addSerialized(EIOCapabilities.ENTITY_STORAGE, LazyOptional.of(EntityStorage::new));
        return provider;
    }

    // endregion
}
