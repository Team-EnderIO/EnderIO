package com.enderio.enderio.content.machines.killer_joe;

import com.enderio.core.annotations.UseOnly;
import com.enderio.core.common.storage.FluidStorage;
import com.enderio.core.common.storage.layout.FluidStorageLayout;
import com.enderio.core.common.storage.layout.ItemStorageLayout;
import com.enderio.core.common.storage.layout.SlotTemplates;
import com.enderio.core.common.storage.slot.SingleResourceSlotKey;
import com.enderio.core.common.util.EnderFakePlayer;
import com.enderio.enderio.config.machines.MachinesConfig;
import com.enderio.enderio.foundation.block.entity.MachineBlockEntity;
import com.enderio.enderio.foundation.state.MachineState;
import com.enderio.enderio.foundation.storage.SidedResourceHandler;
import com.enderio.enderio.init.EIOBlockEntities;
import com.enderio.enderio.init.EIODataComponents;
import com.enderio.enderio.init.EIOFluids;
import com.mojang.authlib.GameProfile;
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

public class KillerJoeBlockEntity extends MachineBlockEntity {

    public static final ICapabilityProvider<KillerJoeBlockEntity, Direction, ResourceHandler<FluidResource>> FLUID_HANDLER_PROVIDER = (be,
        side) -> be.fluidStorage != null ? SidedResourceHandler.of(be.fluidStorage, side, be) : null;

    // Fluid tank
    public static final SingleResourceSlotKey<FluidResource> TANK = new SingleResourceSlotKey<>();
    private final FluidStorage fluidStorage;

    // Inventory slot
    public static final SingleResourceSlotKey<ItemResource> WEAPON_SLOT = new SingleResourceSlotKey<>();

    // Attack cooldown tracking
    private int attackCooldown = 0;

    // Fake player for attacks
    @UseOnly(LogicalSide.SERVER)
    @Nullable
    private EnderFakePlayer fakePlayer;

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

    private boolean hasValidWeapon() {
        ItemStack weapon = getInventory().getStack(WEAPON_SLOT);
        return !weapon.isEmpty() && weapon.is(ItemTags.SWORDS);
    }

    private boolean canAttack() {
        return
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

        // Perform attack
        attacker.attack(target);

        // Consume fluid
        try (Transaction transaction = Transaction.openRoot()) {
            fluidStorage.extract(TANK, fluidStorage.getResource(TANK), getFluidUsage(), transaction);
            transaction.commit();
        }

        // Damage weapon
        getInventory().mutateStack(WEAPON_SLOT, stack -> stack.hurtAndBreak(1, serverLevel, null, item -> {
        }));
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

    private AABB getKillArea() {
        BlockPos pos = getBlockPos();
        Direction facing = getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);

        // Area is 5x5x4 in front of the block
        // 5 wide (±2 from center), 5 tall (0 to +4), 4 deep (forward)
        return switch (facing) {
            case NORTH -> new AABB(pos.getX() - 2, pos.getY(), pos.getZ() - 4,
                                   pos.getX() + 3, pos.getY() + 5, pos.getZ());
            case SOUTH -> new AABB(pos.getX() - 2, pos.getY(), pos.getZ() + 1,
                                   pos.getX() + 3, pos.getY() + 5, pos.getZ() + 5);
            case WEST -> new AABB(pos.getX() - 4, pos.getY(), pos.getZ() - 2,
                                  pos.getX(), pos.getY() + 5, pos.getZ() + 3);
            case EAST -> new AABB(pos.getX() + 1, pos.getY(), pos.getZ() - 2,
                                  pos.getX() + 5, pos.getY() + 5, pos.getZ() + 3);
            default -> new AABB(pos.getX() - 2, pos.getY(), pos.getZ() - 2,
                                pos.getX() + 3, pos.getY() + 5, pos.getZ() + 3);
        };
    }

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
        output.putChild("Fluid", fluidStorage);
        output.putInt("AttackCooldown", attackCooldown);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        input.child("Fluid").ifPresent(fluidStorage::deserialize);
        attackCooldown = input.getIntOr("AttackCooldown", 0);
    }

    @Override
    protected void applyImplicitComponents(DataComponentGetter components) {
        super.applyImplicitComponents(components);

        SimpleFluidContent storedFluid = components.get(EIODataComponents.ITEM_FLUID_CONTENT);
        if (storedFluid != null) {
            fluidStorage.setStack(TANK, storedFluid.copy());
        }
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);

        var fluidStored = getStoredFluid();
        if (!fluidStored.isEmpty()) {
            components.set(EIODataComponents.ITEM_FLUID_CONTENT, SimpleFluidContent.copyOf(fluidStored));
        }
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
}
