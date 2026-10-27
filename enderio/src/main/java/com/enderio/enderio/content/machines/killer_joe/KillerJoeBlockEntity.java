package com.enderio.enderio.content.machines.killer_joe;

import com.enderio.core.annotations.UseOnly;
import com.enderio.core.common.storage.FluidStorage;
import com.enderio.core.common.storage.layout.FluidStorageLayout;
import com.enderio.core.common.storage.layout.ItemStorageLayout;
import com.enderio.core.common.storage.layout.SlotTemplates;
import com.enderio.core.common.storage.slot.SingleResourceSlotKey;
import com.enderio.core.common.util.EnderFakePlayer;
import com.enderio.enderio.config.machines.MachinesConfig;
import com.enderio.enderio.foundation.MachineNBTKeys;
import com.enderio.enderio.foundation.attachment.ActionRange;
import com.enderio.enderio.foundation.attachment.RangedActor;
import com.enderio.enderio.foundation.block.entity.MachineBlockEntity;
import com.enderio.enderio.foundation.state.MachineState;
import com.enderio.enderio.foundation.storage.SidedResourceHandler;
import com.enderio.enderio.init.EIOBlockEntities;
import com.enderio.enderio.init.EIODataComponents;
import com.enderio.enderio.init.EIOFluids;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class KillerJoeBlockEntity extends MachineBlockEntity implements RangedActor {

    public static final ICapabilityProvider<KillerJoeBlockEntity, Direction, ResourceHandler<FluidResource>> FLUID_HANDLER_PROVIDER = (be,
        side) -> be.fluidStorage != null ? SidedResourceHandler.of(be.fluidStorage, side, be) : null;

    /**
     * Block event fired whenever Joe attacks, used to animate the sword swing on the client.
     */
    public static final int ATTACK_EVENT = 1;

    private static final String WEAPON = "Weapon";

    // Fluid tank
    public static final SingleResourceSlotKey<FluidResource> TANK = new SingleResourceSlotKey<>();
    private final FluidStorage fluidStorage;

    // Inventory slot
    public static final SingleResourceSlotKey<ItemResource> WEAPON_SLOT = new SingleResourceSlotKey<>();

    // Kill area, a cube in front of the machine
    private static final ActionRange DEFAULT_RANGE = new ActionRange(2, false);
    private static final int MAX_RANGE = 4;
    private ActionRange actionRange = DEFAULT_RANGE;

    // Attack cooldown tracking
    private int attackCooldown = 0;

    // Fake player for attacks
    @UseOnly(LogicalSide.SERVER)
    @Nullable
    private EnderFakePlayer fakePlayer;

    // Synced weapon and last attack time, used for rendering.
    @UseOnly(LogicalSide.CLIENT)
    private ItemStack displayWeapon = ItemStack.EMPTY;

    @UseOnly(LogicalSide.CLIENT)
    private long lastAttackTime = Long.MIN_VALUE;

    public KillerJoeBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(EIOBlockEntities.KILLER_JOE.get(), worldPosition, blockState, false);

        var fluidStorageLayout = FluidStorageLayout.builder()
            .add(TANK, SlotTemplates.input(getFluidCapacity()), slot -> slot
                .filter((_, resource) -> resource.getFluid().isSame(EIOFluids.NUTRIENT_DISTILLATION.source().get())))
            .build();

        fluidStorage = new FluidStorage(fluidStorageLayout) {
            @Override
            protected void onContentsChanged(int index, FluidStack previousContents) {
                super.onContentsChanged(index, previousContents);
                updateMachineState(MachineState.EMPTY_TANK, getFluidAmount() < getFluidUsage());
                setChanged();
                sendBlockUpdate();
            }
        };
    }

    @Override
    public @Nullable ItemStorageLayout createInventoryLayout() {
        return ItemStorageLayout.builder()
            // TODO: Should we allow Axes too?
            .add(WEAPON_SLOT, SlotTemplates.input(1), b -> b
                .filter((_, resource) -> resource.is(ItemTags.SWORDS))) // Only accept swords
            .build();
    }

    @Override
    protected void onInventoryContentsChanged(int slot) {
        super.onInventoryContentsChanged(slot);

        // Weapon is rendered on the client
        sendBlockUpdate();
    }

    private void sendBlockUpdate() {
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    public FluidStorage getFluidStorage() {
        return fluidStorage;
    }

    @Override
    public void serverTick() {
        super.serverTick();

        EnderFakePlayer fakePlayer = getFakePlayer();
        if (fakePlayer == null) {
            return;
        }

        // Decrement cooldown
        if (attackCooldown > 0) {
            attackCooldown--;
        }

        // Update machine states
        updateMachineState(MachineState.NO_WEAPON, !hasValidWeapon());
        updateMachineState(MachineState.EMPTY_TANK, getFluidAmount() < getFluidUsage());

        // Try to attack if ready
        if (canAttack()) {
            performAttack(fakePlayer);
            attackCooldown = getAttackCooldown();
        }
    }

    @Override
    public void clientTick() {
        if (level instanceof ClientLevel clientLevel) {
            getActionRange().addClientParticle(clientLevel, getKillAreaCenter(),
                MachinesConfig.CLIENT.BLOCKS.KILLER_JOE_RANGE_COLOR.get());
        }

        super.clientTick();
    }

    private boolean hasValidWeapon() {
        ItemStack weapon = getInventory().getStack(WEAPON_SLOT);
        return !weapon.isEmpty() && weapon.is(ItemTags.SWORDS);
    }

    private boolean canAttack() {
        return
            canAct() &&
            hasValidWeapon() &&
            getFluidAmount() >= getFluidUsage() &&
            level instanceof ServerLevel &&
            attackCooldown <= 0;
    }

    private void performAttack(EnderFakePlayer attacker) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        // Get kill area based on facing
        AABB killArea = getKillArea();

        // Find all living entities in area (excluding players)
        List<LivingEntity> entitiesInRange = level.getEntities(
            EntityTypeTest.forClass(LivingEntity.class),
            killArea,
            entity -> !(entity instanceof Player)
        );

        if (entitiesInRange.isEmpty()) {
            return;
        }

        // Ensure we have a weapon
        ItemStack weapon = getInventory().getStack(WEAPON_SLOT);
        if (weapon.isEmpty()) {
            return;
        }

        // Get nearest target.
        var target = serverLevel.getNearestEntity(entitiesInRange, TargetingConditions.DEFAULT, attacker, attacker.getX(), attacker.getY(), attacker.getZ());
        if (target == null) {
            return;
        }

        // Give weapon to fake player
        attacker.setItemInHand(InteractionHand.MAIN_HAND, weapon.copy());

        // Ensure attacker has max strength so our cooldown is the only thing that applies
        attacker.setMaxAttackStrength();

        // Face forwards like the block, the sweep effect and knockback are based on the attacker's rotation.
        attacker.faceDirection(getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING));

        // Perform attack
        attacker.attack(target);

        // Let nearby clients know so they can swing the sword.
        level.blockEvent(worldPosition, getBlockState().getBlock(), ATTACK_EVENT, 0);

        // Consume fluid
        try (Transaction transaction = Transaction.openRoot()) {
            fluidStorage.extract(TANK, fluidStorage.getResource(TANK), getFluidUsage(), transaction);
            transaction.commit();
        }

        // Damage weapon
        getInventory().mutateStack(WEAPON_SLOT, stack -> stack.hurtAndBreak(1, serverLevel, null, item -> {
        }));
    }

    @Override
    public boolean triggerEvent(int id, int type) {
        if (id == ATTACK_EVENT) {
            if (level != null && level.isClientSide()) {
                lastAttackTime = level.getGameTime();
            }

            return true;
        }

        return super.triggerEvent(id, type);
    }

    @UseOnly(LogicalSide.SERVER)
    @Nullable
    private EnderFakePlayer getFakePlayer() {
        if (!(level instanceof ServerLevel serverLevel)) {
            return null;
        }

        if (fakePlayer == null) {
            // One fake player per killer joe to ensure we don't mess with mob pathing too badly.
            fakePlayer = new EnderFakePlayer(serverLevel, new GameProfile(getMachineOwnerOrRandom(), "enderio:killer_joe:" + worldPosition));

            // Move player into position
            fakePlayer.setPos(getBlockPos().getX() + 0.5, getBlockPos().getY() + 0.5, getBlockPos().getZ() + 0.5);
            fakePlayer.setOnGround(true);
        }

        return fakePlayer;
    }

    @Override
    public void setRemoved() {
        super.setRemoved();

        // Because we have fake players per block entity, we need to clean them up on removal.
        if (level != null && !level.isClientSide()) {
            if (fakePlayer != null) {
                fakePlayer.discard();
                fakePlayer = null;
            }
        }
    }

    // region Range

    @Override
    public ActionRange getActionRange() {
        return actionRange;
    }

    @Override
    @UseOnly(LogicalSide.SERVER)
    public void setActionRange(ActionRange actionRange) {
        this.actionRange = actionRange.clamp(1, getMaxRange());
        setChanged();
        sendBlockUpdate();
    }

    @Override
    public int getMaxRange() {
        return MAX_RANGE;
    }

    /**
     * The kill area is a cube sat in front of the machine, level with its base.
     */
    private BlockPos getKillAreaCenter() {
        Direction facing = getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
        int range = getRange();
        return worldPosition.relative(facing, range + 1).above(range);
    }

    private AABB getKillArea() {
        return new AABB(getKillAreaCenter()).inflate(getRange());
    }

    // endregion

    @Override
    public boolean isActive() {
        return attackCooldown > 0 && hasValidWeapon() && getFluidAmount() >= getFluidUsage();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new KillerJoeMenu(containerId, playerInventory, this);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("AttackCooldown", attackCooldown);
    }

    @Override
    protected void saveAdditionalSynced(ValueOutput output) {
        super.saveAdditionalSynced(output);
        output.putChild("Fluid", fluidStorage);

        if (!actionRange.equals(DEFAULT_RANGE)) {
            output.store(MachineNBTKeys.ACTION_RANGE, ActionRange.CODEC, actionRange);
        }

        ItemStack weapon = getInventory().getStack(WEAPON_SLOT);
        if (!weapon.isEmpty()) {
            output.store(WEAPON, ItemStack.CODEC, weapon);
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        input.child("Fluid").ifPresent(fluidStorage::deserialize);
        attackCooldown = input.getIntOr("AttackCooldown", 0);

        // Synced, not saved:
        actionRange = input.read(MachineNBTKeys.ACTION_RANGE, ActionRange.CODEC)
            .orElse(DEFAULT_RANGE);
        displayWeapon = input.read(WEAPON, ItemStack.CODEC)
            .orElse(ItemStack.EMPTY);
    }

    @Override
    protected void applyImplicitComponents(DataComponentGetter components) {
        super.applyImplicitComponents(components);

        var actionRange = components.get(EIODataComponents.ACTION_RANGE);
        if (actionRange != null) {
            this.actionRange = actionRange;
        }

        SimpleFluidContent storedFluid = components.get(EIODataComponents.ITEM_FLUID_CONTENT);
        if (storedFluid != null) {
            fluidStorage.setStack(TANK, storedFluid.copy());
        }
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);

        if (!actionRange.equals(DEFAULT_RANGE)) {
            components.set(EIODataComponents.ACTION_RANGE, actionRange);
        }

        var fluidStored = getStoredFluid();
        if (!fluidStored.isEmpty()) {
            components.set(EIODataComponents.ITEM_FLUID_CONTENT, SimpleFluidContent.copyOf(fluidStored));
        }
    }

    @Override
    public void removeComponentsFromTag(ValueOutput output) {
        super.removeComponentsFromTag(output);
        output.discard(MachineNBTKeys.ACTION_RANGE);
    }

    public int getAttackCooldown() {
        return MachinesConfig.COMMON.ENERGY.KILLER_JOE_ATTACK_COOLDOWN.get();
    }

    public int getFluidCapacity() {
        return MachinesConfig.COMMON.ENERGY.KILLER_JOE_FLUID_CAPACITY.get();
    }

    public int getFluidUsage() {
        return MachinesConfig.COMMON.ENERGY.KILLER_JOE_FLUID_USE.get();
    }

    public FluidStack getStoredFluid() {
        return fluidStorage.getStack(TANK);
    }

    public int getFluidAmount() {
        return fluidStorage.getAmountAsInt(TANK);
    }

    public int getCurrentCooldown() {
        return attackCooldown;
    }

    @UseOnly(LogicalSide.CLIENT)
    public ItemStack getDisplayWeapon() {
        return displayWeapon;
    }

    @UseOnly(LogicalSide.CLIENT)
    public long getLastAttackTime() {
        return lastAttackTime;
    }
}
