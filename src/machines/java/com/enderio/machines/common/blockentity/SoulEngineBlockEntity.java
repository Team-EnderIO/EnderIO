package com.enderio.machines.common.blockentity;

import com.enderio.api.attachment.StoredEntityData;
import com.enderio.api.capacitor.CapacitorModifier;
import com.enderio.api.capacitor.FixedScalable;
import com.enderio.api.capacitor.LinearScalable;
import com.enderio.api.capacitor.QuadraticScalable;
import com.enderio.api.io.energy.EnergyIOMode;
import com.enderio.core.common.network.slot.FluidStackNetworkDataSlot;
import com.enderio.core.common.network.slot.ResourceLocationNetworkDataSlot;
import com.enderio.machines.common.MachineNBTKeys;
import com.enderio.machines.common.attachment.IFluidTankUser;
import com.enderio.machines.common.blockentity.base.PoweredMachineBlockEntity;
import com.enderio.machines.common.config.MachinesConfig;
import com.enderio.machines.common.init.MachineBlockEntities;
import com.enderio.machines.common.io.energy.MachineEnergyStorage;
import com.enderio.machines.common.io.fluid.MachineFluidHandler;
import com.enderio.machines.common.io.fluid.MachineFluidTank;
import com.enderio.machines.common.io.fluid.MachineTankLayout;
import com.enderio.machines.common.io.fluid.TankAccess;
import com.enderio.machines.common.io.item.MachineInventoryLayout;
import com.enderio.machines.common.menu.SoulEngineMenu;
import com.enderio.machines.common.souldata.EngineSoul;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RecipesUpdatedEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static com.enderio.machines.common.blockentity.PoweredSpawnerBlockEntity.NO_MOB;

@EventBusSubscriber
public class SoulEngineBlockEntity extends PoweredMachineBlockEntity implements IFluidTankUser {

    private static final QuadraticScalable CAPACITY = new QuadraticScalable(CapacitorModifier.ENERGY_CAPACITY, MachinesConfig.COMMON.ENERGY.SOUL_ENGINE_CAPACITY);
    public static final LinearScalable BURN_SPEED = new LinearScalable(CapacitorModifier.FIXED, MachinesConfig.COMMON.ENERGY.SOUL_ENGINE_BURN_SPEED);
    //TODO capacitor increase efficiency
    public static final LinearScalable GENERATION_SPEED = new LinearScalable(CapacitorModifier.FIXED, () -> 1);

    private static final String BURNED_TICKS = "BurnedTicks";
    private StoredEntityData entityData = StoredEntityData.EMPTY;
    public static final int FLUID_CAPACITY = 2 * FluidType.BUCKET_VOLUME;
    private final MachineFluidHandler fluidHandler;
    private static final TankAccess TANK = new TankAccess();
    @Nullable
    private EngineSoul.SoulData soulData;
    private int burnedTicks = 0;
    private static boolean reload = false;
    private boolean reloadCache = !reload;

    public SoulEngineBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(EnergyIOMode.Output, CAPACITY, FixedScalable.ZERO, MachineBlockEntities.SOUL_ENGINE.get(), worldPosition, blockState);
        fluidHandler = createFluidHandler();

        addDataSlot(new ResourceLocationNetworkDataSlot(() -> this.getEntityType().orElse(NO_MOB),this::setEntityType));
        addDataSlot(new FluidStackNetworkDataSlot(() -> TANK.getFluid(this), f -> TANK.setFluid(this, f)));

    }

    @Override
    public MachineInventoryLayout getInventoryLayout() {
        return MachineInventoryLayout.builder()
            .capacitor()
            .build();
    }

    @Override
    public void serverTick() {
        if (reloadCache != reload && entityData.hasEntity()) {
            Optional<EngineSoul.SoulData> op = EngineSoul.ENGINE.matches(entityData.entityType().get());
            op.ifPresent(data -> soulData = data);
            reloadCache = reload;
        }
        if (soulData != null && isActive()) {
            producePower();
        }

        updateMachineState(MachineState.NOT_SOULBOUND, soulData == null || entityData.entityType().isEmpty());
        super.serverTick();
    }

    public Optional<ResourceLocation> getEntityType() {
        return entityData.entityType();
    }

    public void setEntityType(ResourceLocation entityType) {
        entityData = StoredEntityData.of(entityType);
    }

    @Override
    protected boolean isActive() {
        return canAct() && TANK.getFluidAmount(this) > 0;
    }

    public void producePower() {
        if (burnedTicks >= soulData.tickpermb()) {
            int energy = (int) (soulData.powerpermb() * getGenerationRate());
            if (!TANK.getFluid(this).isEmpty() && getEnergyStorage().addEnergy(energy, true) == energy) {
                TANK.drain(this, 1, IFluidHandler.FluidAction.EXECUTE);
                getEnergyStorage().addEnergy(energy);
                burnedTicks -= soulData.tickpermb();
            }
        } else {
            burnedTicks += getBurnRate();
        }
    }

    public int getBurnRate() {
        return BURN_SPEED.scaleI(this::getCapacitorData).get();
    }

    public float getGenerationRate() {
        //TODO return GENERATION_SPEED.scaleF(this::getCapacitorData).get();
        return MachinesConfig.COMMON.ENERGY.SOUL_ENGINE_BURN_SPEED.get();
    }

    @Override
    public MachineTankLayout getTankLayout() {
        return MachineTankLayout.builder().tank(TANK, FLUID_CAPACITY, isFluidValid()).build();
    }

    public MachineFluidHandler createFluidHandler() {
        return new MachineFluidHandler(getIOConfig(), getTankLayout()) {
            @Override
            protected void onContentsChanged(int slot) {
                super.onContentsChanged(slot);
                updateMachineState(MachineState.EMPTY_TANK, TANK.getFluidAmount(this) <= 0);
                setChanged();
            }

            @Override
            public int fill(FluidStack resource, FluidAction action) {
                // Convert into tagged fluid
                if (TANK.isFluidValid(this, resource)) {
                    var currentFluid = TANK.getFluid(this).getFluid();
                    if (currentFluid == Fluids.EMPTY || resource.getFluid().isSame(currentFluid)) {
                        return super.fill(resource, action);
                    } else {
                        return super.fill(new FluidStack(currentFluid, resource.getAmount()), action);
                    }
                }

                // Non-tagged fluid.
                return 0;
            }
        };
    }

    public MachineFluidTank getFluidTank() {
        return TANK.getTank(this);
    }

    @Override
    public MachineFluidHandler getFluidHandler() {
        return fluidHandler;
    }

    private Predicate<FluidStack> isFluidValid() {
        return fluidStack -> {
            if (soulData == null)  {
                return false;
            }
            String fluid = soulData.fluid();
            if (fluid.startsWith("#")) { //We have a fluid tag instead
                TagKey<Fluid> tag = TagKey.create(Registries.FLUID, new ResourceLocation(fluid.substring(1)));

                // TODO: NEO-PORT: https://github.com/neoforged/NeoForge/pull/585 may be applicable
                var optionalTagContents = BuiltInRegistries.FLUID.getTag(tag);
                if (optionalTagContents.isPresent()) {
                    var optionalTag = optionalTagContents.get().stream().findFirst();
                    if (optionalTag.isPresent()) {
                        return fluidStack.getFluid().isSame(optionalTag.get().value());
                    }
                }
            } else {
                Optional<Holder.Reference<Fluid>> delegate = BuiltInRegistries.FLUID.getHolder(ResourceKey.create(Registries.FLUID, new ResourceLocation(fluid)));
                if (delegate.isPresent()) {
                    return fluidStack.getFluid().isSame(delegate.get().value());
                }
            }
            return false;
        };

    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new SoulEngineMenu(this, playerInventory, containerId);
    }

    protected MachineEnergyStorage createEnergyStorage(EnergyIOMode energyIOMode, Supplier<Integer> capacity, Supplier<Integer> usageRate) {
        return new MachineEnergyStorage(getIOConfig(), energyIOMode, capacity, usageRate) {
            @Override
            protected void onContentsChanged() {
                setChanged();
                updateMachineState(MachineState.FULL_POWER,
                    (getEnergyStorage().getEnergyStored() >= getEnergyStorage().getMaxEnergyStored()) && isCapacitorInstalled());
            }
        };
    }
    @Override
    public void saveAdditional(CompoundTag pTag, HolderLookup.Provider lookupProvider) {
        super.saveAdditional(pTag, lookupProvider);
        pTag.putInt(BURNED_TICKS, burnedTicks);
        pTag.put(MachineNBTKeys.ENTITY_STORAGE, entityData.saveOptional(lookupProvider));
        saveTank(lookupProvider, pTag);
    }

    @Override
    public void loadAdditional(CompoundTag pTag, HolderLookup.Provider lookupProvider) {
        super.loadAdditional(pTag, lookupProvider);
        burnedTicks = pTag.getInt(BURNED_TICKS);
        entityData = StoredEntityData.parseOptional(lookupProvider, pTag.getCompound(MachineNBTKeys.ENTITY_STORAGE));

        updateMachineState(MachineState.NO_POWER, false);
        updateMachineState(MachineState.FULL_POWER,
            (getEnergyStorage().getEnergyStored() >= getEnergyStorage().getMaxEnergyStored()) && isCapacitorInstalled());
        loadTank(lookupProvider, pTag);
    }

    @Override
    public void setLevel(Level level) {
        super.setLevel(level);

        updateMachineState(MachineState.NO_POWER, false);
        updateMachineState(MachineState.FULL_POWER,
            (getEnergyStorage().getEnergyStored() >= getEnergyStorage().getMaxEnergyStored()) && isCapacitorInstalled());
    }

    @SubscribeEvent
    static void onReload(RecipesUpdatedEvent event) {
        reload = !reload;
    }
}
