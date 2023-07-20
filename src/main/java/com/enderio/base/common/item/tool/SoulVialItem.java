package com.enderio.base.common.item.tool;

import com.enderio.EnderIO;
import com.enderio.api.capability.IEntityStorage;
import com.enderio.api.capability.IMultiCapabilityItem;
import com.enderio.api.capability.MultiCapabilityProvider;
import com.enderio.api.capability.StoredEntityData;
import com.enderio.base.common.capability.EntityStorageItemStack;
import com.enderio.base.common.init.EIOCapabilities;
import com.enderio.base.common.init.EIOItems;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.base.common.util.EntityCaptureUtils;
import com.enderio.core.client.item.IAdvancedTooltipProvider;
import com.enderio.core.common.util.EntityUtil;
import com.enderio.core.common.util.TooltipUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

@Mod.EventBusSubscriber(modid = EnderIO.MODID)
public class SoulVialItem extends Item implements IMultiCapabilityItem, IAdvancedTooltipProvider {
    public SoulVialItem(Properties pProperties) {
        super(pProperties);
    }

    // Item appearance and description

    @Override
    public boolean isFoil(ItemStack pStack) {
        return pStack.getCapability(EIOCapabilities.ENTITY_STORAGE).map(IEntityStorage::hasStoredEntity).orElse(false);
    }

    @Override
    public void addCommonTooltips(ItemStack itemStack, @Nullable Player player, List<Component> tooltips) {
        itemStack
            .getCapability(EIOCapabilities.ENTITY_STORAGE)
            .ifPresent(entityStorage -> entityStorage
                .getStoredEntityData()
                .getEntityType()
                .ifPresent(entityType -> tooltips.add(TooltipUtil.style(Component.translatable(EntityUtil.getEntityDescriptionId(entityType))))));
    }

    @Override
    public void addDetailedTooltips(ItemStack itemStack, @Nullable Player player, List<Component> tooltips) {
        itemStack
            .getCapability(EIOCapabilities.ENTITY_STORAGE)
            .ifPresent(entityStorage -> entityStorage
                .getStoredEntityData()
                .getHealthState()
                .ifPresent(health -> tooltips.add(TooltipUtil.styledWithArgs(EIOLang.SOUL_VIAL_TOOLTIP_HEALTH, health.getA(), health.getB()))));
    }

    // endregion

    // region Interactions

    @Override
    public InteractionResult interactLivingEntity(ItemStack pStack, Player pPlayer, LivingEntity pInteractionTarget, InteractionHand pUsedHand) {
        if (pPlayer.level().isClientSide) {
            return InteractionResult.FAIL;
        }
        return catchEntity(pStack, pInteractionTarget, filledVial -> {
            ItemStack hand = pPlayer.getItemInHand(pUsedHand);
            if (hand.isEmpty()) {
                hand.setCount(1); // Forge will fire the destroyItemEvent and vanilla replaces it to ItemStack.EMPTY if this isn't done
                pPlayer.setItemInHand(pUsedHand, filledVial);
            } else {
                if (!pPlayer.addItem(filledVial)) {
                    pPlayer.drop(filledVial, false);
                }
            }
        }, component -> pPlayer.displayClientMessage(component, true));
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        if (pContext.getLevel().isClientSide) {
            return InteractionResult.FAIL;
        }

        Player player = pContext.getPlayer();

        // Only players may use the soul vial
        if (player == null) {
            return InteractionResult.FAIL;
        }
        return releaseEntity(pContext.getLevel(), pContext.getItemInHand(), pContext.getClickedFace(), pContext.getClickedPos(),
            emptyVial -> player.setItemInHand(pContext.getHand(), emptyVial));
    }

    private static InteractionResult catchEntity(ItemStack soulVial, LivingEntity entity, Consumer<ItemStack> filledVialInsertion,
        Consumer<Component> displayCallback) {
        return soulVial.getCapability(EIOCapabilities.ENTITY_STORAGE).map(entityStorage -> {
            if (!entityStorage.hasStoredEntity()) {

                if (entity instanceof Player) {
                    displayCallback.accept(EIOLang.SOUL_VIAL_ERROR_PLAYER);
                    return InteractionResult.FAIL;
                }

                // Get the entity type and verify it is allowed to be captured
                // We ignore the unchecked cast, as entity is LivingEntity
                // noinspection unchecked
                EntityCaptureUtils.CapturableStatus status = EntityCaptureUtils.getCapturableStatus((EntityType<? extends LivingEntity>) entity.getType(),
                    entity);
                if (status != EntityCaptureUtils.CapturableStatus.CAPTURABLE) {
                    displayCallback.accept(status.errorMessage());
                    return InteractionResult.FAIL;
                }

                if (!entity.isAlive()) {
                    displayCallback.accept(EIOLang.SOUL_VIAL_ERROR_DEAD);
                    return InteractionResult.FAIL;
                }

                soulVial.shrink(1);

                // Create a filled vial and put the entity's NBT inside.
                if (entity instanceof Mob mob && mob.getLeashHolder() != null) {
                    mob.dropLeash(true, true);
                }
                ItemStack filledVial = EIOItems.FILLED_SOUL_VIAL.get().getDefaultInstance();
                setEntityData(filledVial, entity);

                // give back the filled vial
                filledVialInsertion.accept(filledVial);

                // Remove the captured mob.
                entity.discard();
            }
            return InteractionResult.SUCCESS;
        }).orElse(InteractionResult.SUCCESS);
    }

    private static InteractionResult releaseEntity(Level level, ItemStack filledVial, Direction face, BlockPos pos, Consumer<ItemStack> emptyVialSetter) {
        filledVial.getCapability(EIOCapabilities.ENTITY_STORAGE).ifPresent(entityStorage -> {
            if (entityStorage.hasStoredEntity()) {
                StoredEntityData entityData = entityStorage.getStoredEntityData();

                // Get the spawn location for the mob.
                double spawnX = pos.getX() + face.getStepX() + 0.5;
                double spawnY = pos.getY() + face.getStepY();
                double spawnZ = pos.getZ() + face.getStepZ() + 0.5;

                // Get a random rotation for the entity.
                float rotation = Mth.wrapDegrees(level.getRandom().nextFloat() * 360.0f);

                // Try to get the entity NBT from the item.
                Optional<Entity> entity = EntityType.create(entityData.getEntityTag(), level);

                // Position the entity and add it.
                entity.ifPresent(ent -> {
                    ent.setPos(spawnX, spawnY, spawnZ);
                    ent.setYRot(rotation);
                    level.addFreshEntity(ent);
                });
                emptyVialSetter.accept(EIOItems.EMPTY_SOUL_VIAL.get().getDefaultInstance());
            }
        });
        return InteractionResult.SUCCESS;
    }

    // endregion

    // region Utilities

    public static List<ItemStack> getAllFilled() {
        List<ItemStack> items = new ArrayList<>();
        for (ResourceLocation entity : EntityCaptureUtils.getCapturableEntities()) {
            ItemStack is = EIOItems.FILLED_SOUL_VIAL.get().getDefaultInstance();
            setEntityType(is, entity);
            items.add(is);
        }
        return items;
    }

    // endregion

    // region Entity Storage

    public static void setEntityType(ItemStack stack, ResourceLocation entityType) {
        stack.getCapability(EIOCapabilities.ENTITY_STORAGE).ifPresent(storage -> storage.setStoredEntityData(StoredEntityData.of(entityType)));
    }

    private static void setEntityData(ItemStack stack, LivingEntity entity) {
        stack.getCapability(EIOCapabilities.ENTITY_STORAGE).ifPresent(storage -> storage.setStoredEntityData(StoredEntityData.of(entity)));
    }

    public static Optional<StoredEntityData> getEntityData(ItemStack stack) {
        return stack.getCapability(EIOCapabilities.ENTITY_STORAGE).map(IEntityStorage::getStoredEntityData);
    }

    @Nullable
    @Override
    public MultiCapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt, MultiCapabilityProvider provider) {
        provider.add(EIOCapabilities.ENTITY_STORAGE, LazyOptional.of(() -> new EntityStorageItemStack(stack)));
        return provider;
    }

    // endregion

    // region events

    /**
     * Fix for certain mobs that don't work with the soul vial.
     * So far the list includes:
     * - Donkey
     * - Mule
     * - Llama
     * - Villagers
     */
    @SubscribeEvent
    public static void onLivingInteract(PlayerInteractEvent.EntityInteractSpecific event) {
        ItemStack stack = event.getItemStack();
        if (stack.is(EIOItems.EMPTY_SOUL_VIAL.get())) {
            if (event.getTarget() instanceof AbstractChestedHorse || event.getTarget() instanceof Villager) {
                stack.getItem().interactLivingEntity(stack, event.getEntity(), (LivingEntity) event.getTarget(), event.getHand());
            }
        }
    }

    public static void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            DispenserBlock.registerBehavior(EIOItems.FILLED_SOUL_VIAL.get(), new EmptySoulVialDispenseBehavior());
            DispenserBlock.registerBehavior(EIOItems.EMPTY_SOUL_VIAL.get(), new FillSoulVialDispenseBehavior());
        });
    }

    // endregion

    // region Dispenser
    private static class FillSoulVialDispenseBehavior extends OptionalDispenseItemBehavior {
        protected ItemStack execute(BlockSource source, ItemStack stack) {
            BlockPos blockpos = source.getPos().relative(source.getBlockState().getValue(DispenserBlock.FACING));
            for (LivingEntity livingentity : source
                .getLevel()
                .getEntitiesOfClass(LivingEntity.class, new AABB(blockpos), living -> !(living instanceof Player))) {
                AtomicReference<ItemStack> filledVial = new AtomicReference<>();
                if (catchEntity(stack, livingentity, filledVial::set, component -> {}) == InteractionResult.SUCCESS && filledVial.get() != null) {
                    //push filledvial back into dispenser
                    source.getEntity().getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
                        for (int i = 0; i < handler.getSlots(); i++) {
                            if (handler.insertItem(i, filledVial.get(), true).isEmpty()) {
                                handler.insertItem(i, filledVial.get(), false);
                                break;
                            }
                        }
                    });
                    return stack;
                }
            }
            this.setSuccess(false);
            return stack;
        }
    }

    private static class EmptySoulVialDispenseBehavior extends OptionalDispenseItemBehavior {
        protected ItemStack execute(BlockSource source, ItemStack stack) {
            Direction dispenserDirection = source.getBlockState().getValue(DispenserBlock.FACING);
            AtomicReference<ItemStack> emptyVial = new AtomicReference<>();
            releaseEntity(source.getLevel(), stack, dispenserDirection, source.getPos(), emptyVial::set);
            if (emptyVial.get() != null) {
                return emptyVial.get();
            } else {
                this.setSuccess(false);
                return stack;
            }
        }
    }
    // endregion
}
