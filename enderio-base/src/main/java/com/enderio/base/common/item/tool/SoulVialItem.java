package com.enderio.base.common.item.tool;

import com.enderio.EnderIOBase;
import com.enderio.base.api.attachment.StoredEntityData;
import com.enderio.base.common.init.EIODataComponents;
import com.enderio.base.common.init.EIOItems;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.base.common.util.EntityCaptureUtils;
import com.enderio.core.client.item.AdvancedTooltipProvider;
import com.enderio.core.common.util.TooltipUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

@EventBusSubscriber(modid = EnderIOBase.MODULE_MOD_ID)
public class SoulVialItem extends Item implements AdvancedTooltipProvider {

    public static final ICapabilityProvider<ItemStack, Void, StoredEntityData> STORED_ENTITY_PROVIDER
        = (stack, ctx) -> stack.get(EIODataComponents.STORED_ENTITY);

    public SoulVialItem(Properties pProperties) {
        super(pProperties);
    }

    // Item appearance and description

    @Override
    public boolean isFoil(ItemStack pStack) {
        return pStack.getOrDefault(EIODataComponents.STORED_ENTITY, StoredEntityData.EMPTY).hasEntity();
    }

    @Override
    public void addDetailedTooltips(ItemStack itemStack, @Nullable Player player, List<Component> tooltips) {
        itemStack
            .getOrDefault(EIODataComponents.STORED_ENTITY, StoredEntityData.EMPTY)
            .getHealthState()
            .ifPresent(health ->
                tooltips.add(TooltipUtil.styledWithArgs(EIOLang.SOUL_VIAL_TOOLTIP_HEALTH, health.getA(), health.getB())));
    }

    // endregion

    // region Interactions

    @Override
    public InteractionResult interactLivingEntity(ItemStack pStack, Player pPlayer, LivingEntity pInteractionTarget, InteractionHand pUsedHand) {
        if (pPlayer.level().isClientSide) {
            return InteractionResult.FAIL;
        }
        Optional<ItemStack> itemStack = catchEntity(pStack, pInteractionTarget, component -> pPlayer.displayClientMessage(component, true));
        if (itemStack.isPresent()) {
            ItemStack filledVial = itemStack.get();
            ItemStack hand = pPlayer.getItemInHand(pUsedHand);
            if (hand.isEmpty()) {
                hand.setCount(1); // Forge will fire the destroyItemEvent and vanilla replaces it to ItemStack.EMPTY if this isn't done
                pPlayer.setItemInHand(pUsedHand, filledVial);
            } else {
                if (!pPlayer.addItem(filledVial)) {
                    pPlayer.drop(filledVial, false);
                }
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
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

    /**
     * @return the filled vial
     */
    private static Optional<ItemStack> catchEntity(ItemStack soulVial, LivingEntity entity, Consumer<Component> displayCallback) {

        //Soul Vial is filled, so it can't capture
        Optional<StoredEntityData> entityData = getEntityData(soulVial);
        if (entityData.isPresent() && entityData.get().hasEntity()) {
            return Optional.empty();
        }
        
        if (entity instanceof Player) {
            displayCallback.accept(EIOLang.SOUL_VIAL_ERROR_PLAYER);
            return Optional.empty();
        }

        EntityCaptureUtils.CapturableStatus status = EntityCaptureUtils.getCapturableStatus((EntityType<? extends LivingEntity>) entity.getType(), entity);
        if (status != EntityCaptureUtils.CapturableStatus.CAPTURABLE) {
            displayCallback.accept(status.errorMessage());
            return Optional.empty();
        }

        if (!entity.isAlive()) {
            displayCallback.accept(EIOLang.SOUL_VIAL_ERROR_DEAD);
            return Optional.empty();
        }
        // Create a filled vial and put the entity's NBT inside.
        if (entity instanceof Mob mob && mob.getLeashHolder() != null) {
            mob.dropLeash(true, true);
        }
        soulVial.shrink(1);
        ItemStack filledVial = EIOItems.FILLED_SOUL_VIAL.get().getDefaultInstance();
        setEntityData(filledVial, entity);

        // Remove the captured mob.
        entity.discard();
        return Optional.of(filledVial);
    }

    private static InteractionResult releaseEntity(Level level, ItemStack filledVial, Direction face, BlockPos pos, Consumer<ItemStack> emptyVialSetter) {
        var storedEntity = filledVial.get(EIODataComponents.STORED_ENTITY);

        if (storedEntity != null && storedEntity.hasEntity()) {
            // Get the spawn location for the mob.
            double spawnX = pos.getX() + face.getStepX() + 0.5;
            double spawnY = pos.getY() + face.getStepY();
            double spawnZ = pos.getZ() + face.getStepZ() + 0.5;

            // Get a random rotation for the entity.
            float rotation = Mth.wrapDegrees(level.getRandom().nextFloat() * 360.0f);

            // Try to get the entity NBT from the item.
            Optional<Entity> entity = EntityType.create(storedEntity.getEntityTag(), level);

            // Position the entity and add it.
            entity.ifPresent(ent -> {
                ent.setPos(spawnX, spawnY, spawnZ);
                ent.setYRot(rotation);
                level.addFreshEntity(ent);
            });
            emptyVialSetter.accept(EIOItems.EMPTY_SOUL_VIAL.get().getDefaultInstance());
        }

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
        stack.set(EIODataComponents.STORED_ENTITY, StoredEntityData.of(entityType));
    }

    private static void setEntityData(ItemStack stack, LivingEntity entity) {
        stack.set(EIODataComponents.STORED_ENTITY, StoredEntityData.of(entity));
    }

    public static Optional<StoredEntityData> getEntityData(ItemStack stack) {
        return Optional.ofNullable(stack.get(EIODataComponents.STORED_ENTITY));
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
     * - Wandering Trader
     * - Wolves
     */
    @SubscribeEvent
    public static void onLivingInteract(PlayerInteractEvent.EntityInteractSpecific event) {
        ItemStack stack = event.getItemStack();
        if (stack.is(EIOItems.EMPTY_SOUL_VIAL.get())) {
            if (event.getTarget() instanceof AbstractChestedHorse || event.getTarget() instanceof Villager || event.getTarget() instanceof WanderingTrader || event.getTarget() instanceof Wolf) {
                stack.interactLivingEntity(event.getEntity(), (LivingEntity) event.getTarget(), event.getHand());
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
        @Override
        protected ItemStack execute(BlockSource source, ItemStack stack) {
            BlockPos blockpos = source.pos().relative(source.state().getValue(DispenserBlock.FACING));
            for (LivingEntity livingentity : source
                    .level()
                    .getEntitiesOfClass(LivingEntity.class, new AABB(blockpos), living -> !(living instanceof Player))) {

                // Copy, consumeWithRemainder will shrink the stack.
                Optional<ItemStack> filledVial = catchEntity(stack.copy(), livingentity, component -> {});
                if (filledVial.isPresent()) {
                    return this.consumeWithRemainder(source, stack, filledVial.get());
                }
            }

            this.setSuccess(false);
            return stack;
        }
    }

    private static class EmptySoulVialDispenseBehavior extends OptionalDispenseItemBehavior {
        protected ItemStack execute(BlockSource source, ItemStack stack) {
            Direction dispenserDirection = source.state().getValue(DispenserBlock.FACING);
            AtomicReference<ItemStack> emptyVial = new AtomicReference<>();
            releaseEntity(source.level(), stack, dispenserDirection, source.pos(), emptyVial::set);
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
