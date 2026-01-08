package com.enderio.enderio.content.machines.killer_joe;

import com.enderio.core.common.util.EnderFakePlayer;
import com.enderio.core.common.util.EnderFakePlayerFactory;
import com.enderio.enderio.api.UseOnly;
import com.enderio.enderio.config.machines.MachinesConfig;
import com.enderio.enderio.foundation.attachment.FluidTankUser;
import com.enderio.enderio.foundation.block.entity.MachineBlockEntity;
import com.enderio.enderio.foundation.io.fluid.FluidItemInteractive;
import com.enderio.enderio.foundation.io.fluid.MachineFluidHandler;
import com.enderio.enderio.foundation.io.fluid.MachineTankLayout;
import com.enderio.enderio.foundation.io.fluid.TankAccess;
import com.enderio.enderio.foundation.inventory.MachineInventoryLayout;
import com.enderio.enderio.foundation.inventory.SingleSlotAccess;
import com.enderio.enderio.foundation.state.MachineState;
import com.enderio.enderio.init.EIOBlockEntities;
import com.enderio.enderio.init.EIOFluids;
import com.mojang.authlib.GameProfile;
import me.liliandev.ensure.ensures.EnsureSide;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
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
import net.minecraft.world.phys.AABB;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class KillerJoeBlockEntity extends MachineBlockEntity implements FluidTankUser, FluidItemInteractive {

    // Fluid tank
    public static final TankAccess TANK = new TankAccess();
    private final MachineFluidHandler fluidHandler;

    // Inventory slot
    private static final SingleSlotAccess WEAPON_SLOT = new SingleSlotAccess();

    // Attack cooldown tracking
    private int attackCooldown = 0;

    // Fake player for attacks
    @UseOnly(LogicalSide.SERVER)
    @Nullable
    private EnderFakePlayer fakePlayer;

    public KillerJoeBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(EIOBlockEntities.KILLER_JOE.get(), worldPosition, blockState, false);
        fluidHandler = createFluidHandler();
    }

    @Override
    protected @Nullable MachineInventoryLayout createInventoryLayout() {
        return MachineInventoryLayout.builder()
            // TODO: Should we allow Axes too?
            .inputSlot((i, s) -> s.is(ItemTags.SWORDS)) // Only accept swords
            .slotAccess(WEAPON_SLOT)
            .build();
    }

    @Override
    public MachineTankLayout getTankLayout() {
        return MachineTankLayout.builder()
            .tank(TANK, getFluidCapacity(), stack -> stack.getFluid() == EIOFluids.NUTRIENT_DISTILLATION.source().get())
            .build();
    }

    @Override
    public MachineFluidHandler createFluidHandler() {
        return new MachineFluidHandler(this, getTankLayout()) {
            @Override
            protected void onContentsChanged(int slot) {
                super.onContentsChanged(slot);
                updateMachineState(MachineState.EMPTY_TANK, TANK.getFluidAmount(KillerJoeBlockEntity.this) < getFluidUsage());
                setChanged();
            }
        };
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
        updateMachineState(MachineState.EMPTY_TANK, TANK.getFluidAmount(this) < getFluidUsage());

        // Try to attack if ready
        if (canAttack()) {
            performAttack(fakePlayer);
            attackCooldown = getAttackCooldown();
        }
    }

    private boolean hasValidWeapon() {
        ItemStack weapon = WEAPON_SLOT.getItemStack(this);
        return !weapon.isEmpty() && weapon.is(ItemTags.SWORDS);
    }

    private boolean canAttack() {
        return
            hasValidWeapon() &&
            TANK.getFluidAmount(this) >= getFluidUsage() &&
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
        ItemStack weapon = WEAPON_SLOT.getItemStack(this);
        if (weapon.isEmpty()) {
            return;
        }

        // Get nearest target.
        var target = level.getNearestEntity(entitiesInRange, TargetingConditions.DEFAULT, attacker, attacker.getX(), attacker.getY(), attacker.getZ());
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
        TANK.drain(this, getFluidUsage(), IFluidHandler.FluidAction.EXECUTE);

        // Damage weapon
        weapon.hurtAndBreak(1, serverLevel, null, item -> {
        });
    }

    @EnsureSide(EnsureSide.Side.SERVER)
    @Nullable
    private EnderFakePlayer getFakePlayer() {
        if (!(level instanceof ServerLevel serverLevel)) {
            return null;
        }

        if (fakePlayer == null) {
            UUID ownerId = getMachineOwner();
            if (ownerId == null) {
                ownerId = UUID.randomUUID();
                setMachineOwner(ownerId);
            }

            // One fake player per killer joe to ensure we don't mess with mob pathing too badly.
            fakePlayer = new EnderFakePlayer(serverLevel, new GameProfile(ownerId, "enderio:killer_joe:" + worldPosition));

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
        if (level != null && !level.isClientSide) {
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
        return attackCooldown > 0 && hasValidWeapon() && TANK.getFluidAmount(this) >= getFluidUsage();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new KillerJoeMenu(containerId, playerInventory, this);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        super.saveAdditional(tag, lookupProvider);
        saveTank(lookupProvider, tag);
        tag.putInt("AttackCooldown", attackCooldown);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        super.loadAdditional(tag, lookupProvider);
        loadTank(lookupProvider, tag);
        attackCooldown = tag.getInt("AttackCooldown");
    }

    @Override
    public MachineFluidHandler getFluidHandler() {
        return fluidHandler;
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

    public int getFluidAmount() {
        return TANK.getFluidAmount(this);
    }

    public int getCurrentCooldown() {
        return attackCooldown;
    }
}
