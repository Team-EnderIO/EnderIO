package com.enderio.machines.common.blockentity;

import com.enderio.api.capability.StoredEntityData;
import com.enderio.api.capacitor.CapacitorModifier;
import com.enderio.api.capacitor.FixedScalable;
import com.enderio.api.capacitor.LinearScalable;
import com.enderio.api.capacitor.QuadraticScalable;
import com.enderio.api.io.energy.EnergyIOMode;
import com.enderio.core.common.network.slot.FluidStackNetworkDataSlot;
import com.enderio.core.common.network.slot.ResourceLocationNetworkDataSlot;
import com.enderio.machines.common.MachineNBTKeys;
import com.enderio.machines.common.blockentity.base.PoweredMachineBlockEntity;
import com.enderio.machines.common.config.MachinesConfig;
import com.enderio.machines.common.io.energy.MachineEnergyStorage;
import com.enderio.machines.common.io.fluid.MachineFluidTank;
import com.enderio.machines.common.io.item.MachineInventoryLayout;
import com.enderio.machines.common.menu.SoulEngineMenu;
import com.enderio.machines.common.souldata.EngineSoul;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static com.enderio.machines.common.blockentity.PoweredSpawnerBlockEntity.NO_MOB;

@Mod.EventBusSubscriber
public class SoulEngineBlockEntity extends PoweredMachineBlockEntity {

    private static final QuadraticScalable CAPACITY = new QuadraticScalable(CapacitorModifier.ENERGY_CAPACITY, MachinesConfig.COMMON.ENERGY.SOUL_ENGINE_CAPACITY);
    public static final LinearScalable BURN_SPEED = new LinearScalable(CapacitorModifier.FIXED, MachinesConfig.COMMON.ENERGY.SOUL_ENGINE_BURN_SPEED);
    //TODO capacitor increase efficiency
    public static final LinearScalable GENERATION_SPEED = new LinearScalable(CapacitorModifier.FIXED, () -> 1);

    private static final String BURNED_TICKS = "BurnedTicks";
    private StoredEntityData entityData = StoredEntityData.empty();
    public static final int FLUID_CAPACITY = 2 * FluidType.BUCKET_VOLUME;
    @Nullable
    private EngineSoul.SoulData soulData;
    private int burnedTicks = 0;
    private static boolean reload = false;
    private boolean reloadCache = !reload;

    public SoulEngineBlockEntity(BlockEntityType<?> type,
        BlockPos worldPosition, BlockState blockState) {
        super(EnergyIOMode.Output, CAPACITY, FixedScalable.ZERO, type, worldPosition, blockState);
        addDataSlot(new ResourceLocationNetworkDataSlot(() -> this.getEntityType().orElse(NO_MOB),this::setEntityType));
        addDataSlot(new FluidStackNetworkDataSlot(getFluidTankNN()::getFluid, getFluidTankNN()::setFluid));
    }

    @Override
    public MachineInventoryLayout getInventoryLayout() {
        return MachineInventoryLayout.builder()
            .capacitor()
            .build();
    }

    @Override
    public void serverTick() {
        if (reloadCache != reload && entityData != StoredEntityData.empty() && entityData.getEntityType().isPresent()) {
            Optional<EngineSoul.SoulData> op = EngineSoul.ENGINE.matches(entityData.getEntityType().get());
            op.ifPresent(data -> soulData = data);
            reloadCache = reload;
        }
        if (soulData != null && isActive()) {
            producePower();
        }

        updateMachineState(MachineState.NOT_SOULBOUND, soulData == null || entityData.getEntityType().isEmpty());
        super.serverTick();
    }

    public Optional<ResourceLocation> getEntityType() {
        return entityData.getEntityType();
    }

    public void setEntityType(ResourceLocation entityType) {
        entityData = StoredEntityData.of(entityType);
    }

    @Override
    protected boolean isActive() {
        return canAct() && getFluidTankNN().getFluidAmount() > 0;
    }

    public void producePower() {
        if (burnedTicks >= soulData.tickpermb()) {
            int energy = (int) (soulData.powerpermb() * getGenerationRate());
            if (!getFluidTankNN().isEmpty() && getEnergyStorage().addEnergy(energy, true) == energy) {
                getFluidTankNN().drain(1, IFluidHandler.FluidAction.EXECUTE);
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
    protected @Nullable FluidTank createFluidTank() {
        return new MachineFluidTank(FLUID_CAPACITY, isFluidValid(), this) {
            @Override
            protected void onContentsChanged() {
                super.onContentsChanged();
                updateMachineState(MachineState.EMPTY_TANK, getFluidAmount() <= 0);
                setChanged();
            }

            @Override
            public int fill(FluidStack resource, FluidAction action) {
                // Convert into tagged fluid
                if (this.isFluidValid(resource)) {
                    var currentFluid = this.getFluid().getFluid();
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

    private Predicate<FluidStack> isFluidValid() {
        return fluidStack -> {
            if (soulData == null)  {
                return false;
            }
            String fluid = soulData.fluid();
            if (fluid.startsWith("#")) { //We have a fluid tag instead
                TagKey<Fluid> tag = TagKey.create(Registries.FLUID, new ResourceLocation(fluid.substring(1)));
                Optional<Fluid> optional = ForgeRegistries.FLUIDS.tags().getTag(tag).stream().findFirst();
                if (optional.isPresent()) {
                    return fluidStack.getFluid().isSame(optional.get());
                }
            } else {
                Optional<Holder.Reference<Fluid>> delegate = ForgeRegistries.FLUIDS.getDelegate(new ResourceLocation(fluid));
                if (delegate.isPresent()) {
                    return fluidStack.getFluid().isSame(delegate.get().get());
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
                updateMachineState(MachineState.FULL_POWER, (getEnergyStorage().getEnergyStored() >= getEnergyStorage().getMaxEnergyStored()) && isCapacitorInstalled());
            }
        };
    }

    @Override
    public void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.putInt(BURNED_TICKS, burnedTicks);
        pTag.put(MachineNBTKeys.ENTITY_STORAGE, entityData.serializeNBT());
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        burnedTicks = pTag.getInt(BURNED_TICKS);
        entityData.deserializeNBT(pTag.getCompound(MachineNBTKeys.ENTITY_STORAGE));

        updateMachineState(MachineState.NO_POWER, false);
        updateMachineState(MachineState.FULL_POWER, (getEnergyStorage().getEnergyStored() >= getEnergyStorage().getMaxEnergyStored()) && isCapacitorInstalled());
    }

    @Override
    public void setLevel(Level level) {
        super.setLevel(level);

        updateMachineState(MachineState.NO_POWER, false);
        updateMachineState(MachineState.FULL_POWER, (getEnergyStorage().getEnergyStored() >= getEnergyStorage().getMaxEnergyStored()) && isCapacitorInstalled());
    }

    @SubscribeEvent
    static void onReload(RecipesUpdatedEvent event) {
        reload = !reload;
    }
}
