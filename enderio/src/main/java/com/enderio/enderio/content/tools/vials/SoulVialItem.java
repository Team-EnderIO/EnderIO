package com.enderio.enderio.content.tools.vials;

import com.enderio.core.client.item.AdvancedTooltipProvider;
import com.enderio.core.common.util.TooltipUtil;
import com.enderio.enderio.EnderIO;
import com.enderio.enderio.api.soul.Soul;
import com.enderio.enderio.api.soul.SoulBoundUtils;
import com.enderio.enderio.content.tools.ToolsLang;
import com.enderio.enderio.foundation.util.EntityCaptureUtils;
import com.enderio.enderio.init.EIODataComponents;
import com.enderio.enderio.init.EIOItems;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.wanderingtrader.WanderingTrader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.RegisterConditionalItemModelPropertyEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

@EventBusSubscriber
public class SoulVialItem extends Item implements AdvancedTooltipProvider {
    /**
     * Should match key from {@link LivingEntity#addAdditionalSaveData(ValueOutput)} )}
     */
    public static final String KEY_HEALTH = "Health";

    public SoulVialItem(Properties properties) {
        super(properties);
    }

    public static ItemStack forSoul(Soul soul) {
        ItemStack soulVial = new ItemStack(EIOItems.SOUL_VIAL.get());
        soulVial.set(EIODataComponents.SOUL, soul);
        return soulVial;
    }

    // Item appearance and description

    @Override
    public boolean isFoil(ItemStack stack) {
        return stack.getOrDefault(EIODataComponents.SOUL, Soul.EMPTY).hasEntity();
    }

    @Override
    public void addCommonTooltips(ItemStack itemStack, @Nullable Player player, List<Component> tooltips) {
        // Add entity name
        var soul = itemStack.getOrDefault(EIODataComponents.SOUL, Soul.EMPTY);
        if (!soul.isEmpty()) {
            tooltips.add(TooltipUtil.style(Component.translatable(soul.entityType().getDescriptionId())));
        }
    }

    @Override
    public void addDetailedTooltips(ItemStack itemStack, @Nullable Player player, List<Component> tooltips) {
        var soul = itemStack.getOrDefault(EIODataComponents.SOUL, Soul.EMPTY);
        if (soul.isEmpty()) {
            return;
        }

        // Try add entity health
        float maxHealth = itemStack.getOrDefault(EIODataComponents.ENTITY_MAX_HEALTH, 0f);
        if (maxHealth <= 0) {
            return;
        }

        var entityTag = soul.entityTag();
        if (entityTag.contains(KEY_HEALTH)) {
            float health = entityTag.getFloatOr(KEY_HEALTH, 0f);
            tooltips.add(TooltipUtil.styledWithArgs(ToolsLang.SOUL_VIAL_TOOLTIP_HEALTH, health, maxHealth));
        }
    }

    // endregion

    // region Interactions

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity interactionTarget,
            InteractionHand usedHand) {
        if (player.level().isClientSide()) {
            return InteractionResult.FAIL;
        }

        Optional<ItemStack> itemStack = catchEntity(stack, interactionTarget, player::sendOverlayMessage);
        if (itemStack.isPresent()) {
            ItemStack filledVial = itemStack.get();
            ItemStack hand = player.getItemInHand(usedHand);
            if (hand.isEmpty()) {
                hand.setCount(1); // Forge will fire the destroyItemEvent and vanilla replaces it to
                                  // ItemStack.EMPTY if this isn't done
                player.setItemInHand(usedHand, filledVial);
            } else {
                if (!player.addItem(filledVial)) {
                    player.drop(filledVial, false);
                }
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getLevel().isClientSide()) {
            return InteractionResult.FAIL;
        }

        Player player = context.getPlayer();

        // Only players may use the soul vial
        if (player == null) {
            return InteractionResult.FAIL;
        }

        return releaseEntity(context.getLevel(), context.getItemInHand(), context.getClickedFace(), context.getClickedPos(), () -> {
            context.getItemInHand().shrink(1);
            var emptyVial = EIOItems.SOUL_VIAL.get().getDefaultInstance();
            if (!player.addItem(emptyVial)) {
                player.drop(emptyVial, false);
            }
        });
    }

    /**
     * @return the filled vial
     */
    private static Optional<ItemStack> catchEntity(ItemStack soulVial, LivingEntity entity,
            Consumer<Component> displayCallback) {

        // Soul Vial is filled, so it can't capture
        Soul soul = soulVial.getOrDefault(EIODataComponents.SOUL, Soul.EMPTY);
        if (soul.hasEntity()) {
            return Optional.empty();
        }

        if (entity instanceof Player) {
            displayCallback.accept(ToolsLang.SOUL_VIAL_ERROR_PLAYER);
            return Optional.empty();
        }

        if (!entity.isAlive()) {
            displayCallback.accept(ToolsLang.SOUL_VIAL_ERROR_DEAD);
            return Optional.empty();
        }

        //noinspection unchecked
        EntityCaptureUtils.CapturableStatus status = EntityCaptureUtils
                .getCapturableStatus((EntityType<? extends LivingEntity>) entity.getType());
        if (status != EntityCaptureUtils.CapturableStatus.CAPTURABLE) {
            displayCallback.accept(status.errorMessage());
            return Optional.empty();
        }

        // Create a filled vial and put the entity's NBT inside.
        if (entity instanceof Mob mob && mob.getLeashHolder() != null) {
            mob.dropLeash();
        }

        // Create the filled vial
        ItemStack filledVial = soulVial.copyWithCount(1);
        filledVial.set(EIODataComponents.SOUL, Soul.of(entity));
        filledVial.set(EIODataComponents.ENTITY_MAX_HEALTH, entity.getMaxHealth());

        // Consume the stack
        soulVial.shrink(1);

        // Remove the captured mob.
        entity.discard();
        return Optional.of(filledVial);
    }

    private static InteractionResult releaseEntity(Level level, ItemStack filledVial, Direction face, BlockPos pos,
            Runnable vialConsumed) {
        var storedSoul = filledVial.get(EIODataComponents.SOUL);

        if (storedSoul != null && storedSoul.hasEntity()) {
            // Get the spawn location for the mob.
            double spawnX = pos.getX() + face.getStepX() + 0.5;
            double spawnY = pos.getY() + face.getStepY();
            double spawnZ = pos.getZ() + face.getStepZ() + 0.5;

            // Get a random rotation for the entity.
            float rotation = Mth.wrapDegrees(level.getRandom().nextFloat() * 360.0f);

            // Try to get the entity NBT from the item.
            // TODO: 1.21.4: Do we need our own spawn reason? or is this fine?
            Optional<Entity> entity = EntityType.create(storedSoul.asInput(level.registryAccess()), level, EntitySpawnReason.SPAWN_ITEM_USE);

            // Position the entity and add it.
            entity.ifPresent(ent -> {
                ent.setPos(spawnX, spawnY, spawnZ);
                ent.setYRot(rotation);
                level.addFreshEntity(ent);
            });

            vialConsumed.run();
        }

        return InteractionResult.SUCCESS;
    }

    // endregion

    // region Utilities

    public static List<ItemStack> getAllFilled() {
        List<ItemStack> items = new ArrayList<>();
        for (var entityType : EntityCaptureUtils.getCapturableEntityTypes()) {
            items.add(forSoul(Soul.of(entityType)));
        }
        return items;
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

        // Use a copy of the stack if we're in creative.
        if (event.getEntity().getAbilities().instabuild) {
            stack = stack.copy();
        }

        if (stack.is(EIOItems.SOUL_VIAL)) {
            if (event.getTarget() instanceof AbstractHorse || event.getTarget() instanceof Villager
                    || event.getTarget() instanceof WanderingTrader || event.getTarget() instanceof Wolf) {
                stack.interactLivingEntity(event.getEntity(), (LivingEntity) event.getTarget(), event.getHand());
            }
        }
    }

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            DispenserBlock.registerBehavior(EIOItems.SOUL_VIAL.get(), new SoulVialDispenseBehavior());
        });
    }

    // endregion

    // region Dispenser

    private static class SoulVialDispenseBehavior extends OptionalDispenseItemBehavior {
        @Override
        protected ItemStack execute(BlockSource source, ItemStack stack) {
            if (SoulBoundUtils.isBound(stack)) {
                return release(source, stack);
            } else {
                return capture(source, stack);
            }
        }

        private ItemStack release(BlockSource source, ItemStack stack) {
            Direction dispenserDirection = source.state().getValue(DispenserBlock.FACING);
            AtomicReference<ItemStack> resultStack = new AtomicReference<>();
            releaseEntity(source.level(), stack, dispenserDirection, source.pos(),
                () -> resultStack.set(this.consumeWithRemainder(source, stack, EIOItems.SOUL_VIAL.get().getDefaultInstance())));

            if (resultStack.get() != null) {
                return resultStack.get();
            } else {
                this.setSuccess(false);
                return stack;
            }
        }

        private ItemStack capture(BlockSource source, ItemStack stack) {
            BlockPos blockpos = source.pos().relative(source.state().getValue(DispenserBlock.FACING));
            for (LivingEntity livingentity : source.level()
                .getEntitiesOfClass(LivingEntity.class, new AABB(blockpos),
                    living -> !(living instanceof Player))) {

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

    // endregion
}
