package com.enderio.machines.common.blockentity;

import com.enderio.api.capability.StoredEntityData;
import com.enderio.api.capacitor.CapacitorModifier;
import com.enderio.api.capacitor.FixedScalable;
import com.enderio.api.capacitor.QuadraticScalable;
import com.enderio.api.io.energy.EnergyIOMode;
import com.enderio.core.common.network.slot.FluidStackNetworkDataSlot;
import com.enderio.core.common.network.slot.ResourceLocationNetworkDataSlot;
import com.enderio.machines.common.MachineNBTKeys;
import com.enderio.machines.common.blockentity.base.PoweredMachineBlockEntity;
import com.enderio.machines.common.config.MachinesConfig;
import com.enderio.machines.common.io.fluid.MachineFluidTank;
import com.enderio.machines.common.io.item.MachineInventoryLayout;
import com.enderio.machines.common.menu.MobGeneratorMenu;
import com.enderio.machines.common.souldata.GeneratorSoul;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Predicate;

import static com.enderio.machines.common.blockentity.PoweredSpawnerBlockEntity.NO_MOB;

public class MobGeneratorBlockEntity extends PoweredMachineBlockEntity {

    private static final QuadraticScalable CAPACITY = new QuadraticScalable(CapacitorModifier.ENERGY_CAPACITY, MachinesConfig.COMMON.ENERGY.MOB_GENERATOR_CAPACITY);
    private static final String BURNED_TICKS = "burnedTicks";
    private StoredEntityData entityData = StoredEntityData.empty();
    private static final int FLUID_CAPACITY = 2 * FluidType.BUCKET_VOLUME;
    private GeneratorSoul.SoulData soulData;
    private int burnedTicks = 0;

    public MobGeneratorBlockEntity(BlockEntityType<?> type,
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
        if (entityData != StoredEntityData.empty() && entityData.getEntityType().isPresent()) {
            Optional<GeneratorSoul.SoulData> op = GeneratorSoul.GENERATOR.matches(entityData.getEntityType().get());
            op.ifPresent(data -> {
                soulData = data;
                if (isActive()) {
                    producePower();
                }
            });
        }

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
        if (burnedTicks == soulData.tickpermb()) {
            if (!getFluidTankNN().isEmpty() && getEnergyStorage().addEnergy(soulData.powerpermb(), true) == soulData.powerpermb()) {
                getFluidTankNN().drain(1, IFluidHandler.FluidAction.EXECUTE);
                getEnergyStorage().addEnergy(soulData.powerpermb());
                burnedTicks = 0;
            }
        } else {
            burnedTicks ++;
        }
    }

    @Override
    protected @Nullable FluidTank createFluidTank() {
        return new MachineFluidTank(FLUID_CAPACITY, isFluidValid(), this) {
            @Override
            protected void onContentsChanged() {
                super.onContentsChanged();
            }
        };
    }

    //TODO add tag support
    private Predicate<FluidStack> isFluidValid() {
        return fluidStack -> {
            if (soulData == null)  {
                return false;
            }
            ResourceLocation fluid = soulData.fluid();
            Optional<Holder.Reference<Fluid>> delegate = ForgeRegistries.FLUIDS.getDelegate(fluid);
            if (delegate.isPresent()) {
                return fluidStack.getFluid().isSame(delegate.get().get());
            }
            return false;
        };

    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new MobGeneratorMenu(this, playerInventory, containerId);
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
    }
}
