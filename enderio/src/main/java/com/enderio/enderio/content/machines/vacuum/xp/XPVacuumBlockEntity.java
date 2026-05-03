package com.enderio.enderio.content.machines.vacuum.xp;

import com.enderio.core.common.storage.FluidStorage;
import com.enderio.core.common.storage.layout.FluidStorageLayout;
import com.enderio.core.common.storage.layout.SlotTemplates;
import com.enderio.core.common.storage.slot.SingleResourceSlotKey;
import com.enderio.enderio.config.machines.MachinesConfig;
import com.enderio.enderio.content.machines.vacuum.VacuumMachineBlockEntity;
import com.enderio.enderio.foundation.storage.SidedResourceHandler;
import com.enderio.enderio.init.EIOBlockEntities;
import com.enderio.enderio.init.EIODataComponents;
import com.enderio.enderio.init.EIOFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import static com.enderio.enderio.foundation.util.ExperienceUtil.EXP_TO_FLUID;

public class XPVacuumBlockEntity extends VacuumMachineBlockEntity<ExperienceOrb> {

    public static final ICapabilityProvider<XPVacuumBlockEntity, Direction, ResourceHandler<FluidResource>> FLUID_HANDLER_PROVIDER = (be,
        side) -> be.fluidStorage != null ? SidedResourceHandler.of(be.fluidStorage, side, be) : null;

    public static final SingleResourceSlotKey<FluidResource> TANK = new SingleResourceSlotKey<>();

    public static int CAPACITY = Integer.MAX_VALUE;

    public static final FluidStorageLayout FLUID_STORAGE_LAYOUT =
        FluidStorageLayout.builder()
            .add(TANK, SlotTemplates.storage(), slot -> slot.capacity(CAPACITY))
            .build();

    private final FluidStorage fluidStorage;

    public XPVacuumBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(EIOBlockEntities.XP_VACUUM.get(), worldPosition, blockState, ExperienceOrb.class);

        fluidStorage = new FluidStorage(FLUID_STORAGE_LAYOUT) {
            @Override
            protected void onContentsChanged(int index, FluidStack previousContents) {
                super.onContentsChanged(index, previousContents);
                setChanged();
            }
        };
    }

    @Override
    public String getColor() {
        return MachinesConfig.CLIENT.BLOCKS.XP_VACUUM_RANGE_COLOR.get();
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new XPVacuumMenu(containerId, inventory, this);
    }

    @Override
    public void handleEntity(ExperienceOrb xpe) {
        int amountToFill = xpe.getValue() * EXP_TO_FLUID;

        try (Transaction transaction = Transaction.openRoot()) {
            int tankIndex = fluidStorage.layout().indexOf(TANK);
            int filled = fluidStorage.insert(tankIndex, FluidResource.of(EIOFluids.XP_JUICE.source()), amountToFill, transaction);

            if (filled == amountToFill) {
                transaction.commit();
                xpe.discard();
            } else if (filled > 0) {
                transaction.commit();
                xpe.setValue(xpe.getValue() - Math.round(filled / ((float) EXP_TO_FLUID)));
            }
        }
    }

    public FluidStack getStoredFluid() {
        return fluidStorage.getStack(TANK);
    }

    // region Serialization

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

        FluidStack tank = fluidStorage.getStack(TANK);
        if (!tank.isEmpty()) {
            components.set(EIODataComponents.ITEM_FLUID_CONTENT, SimpleFluidContent.copyOf(tank));
        }
    }

    @Override
    public void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putChild("Fluid", fluidStorage);
    }

    @Override
    public void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        input.child("Fluid").ifPresent(fluidStorage::deserialize);
    }

    // endregion
}
