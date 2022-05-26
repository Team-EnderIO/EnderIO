package com.enderio.machines.common.blockentity;

import com.enderio.api.UseOnly;
import com.enderio.api.capacitor.CapacitorKey;
import com.enderio.base.common.blockentity.sync.FloatDataSlot;
import com.enderio.base.common.blockentity.sync.SyncMode;
import com.enderio.base.common.capacitor.CapacitorUtil;
import com.enderio.machines.common.MachineTier;
import com.enderio.machines.common.block.ProgressMachineBlock;
import com.enderio.machines.common.blockentity.base.PowerGeneratingMachineEntity;
import com.enderio.machines.common.blockentity.data.sidecontrol.item.ItemHandlerMaster;
import com.enderio.machines.common.blockentity.data.sidecontrol.item.ItemSlotLayout;
import com.enderio.machines.common.energy.EnergyTransferMode;
import com.enderio.machines.common.init.MachineCapacitorKeys;
import com.enderio.machines.common.menu.AlloySmelterMenu;
import com.enderio.machines.common.menu.StirlingGeneratorMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.LogicalSide;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public abstract class StirlingGeneratorBlockEntity extends PowerGeneratingMachineEntity {
    // TODO: Add capacitor keys.
    public static class Simple extends StirlingGeneratorBlockEntity {
        public Simple(BlockEntityType<?> pType, BlockPos pWorldPosition,
            BlockState pBlockState) {
            super(MachineCapacitorKeys.DEV_ENERGY_CAPACITY.get(),
                MachineCapacitorKeys.DEV_ENERGY_TRANSFER.get(),
                MachineCapacitorKeys.DEV_ENERGY_CONSUME.get(),
                pType, pWorldPosition, pBlockState);
        }

        @Override
        public MachineTier getTier() {
            return MachineTier.Simple;
        }
    }

    public static class Standard extends StirlingGeneratorBlockEntity {
        public Standard(BlockEntityType<?> pType, BlockPos pWorldPosition,
            BlockState pBlockState) {
            super(MachineCapacitorKeys.DEV_ENERGY_CAPACITY.get(),
                MachineCapacitorKeys.DEV_ENERGY_TRANSFER.get(),
                MachineCapacitorKeys.DEV_ENERGY_CONSUME.get(),
                pType, pWorldPosition, pBlockState);
        }

        @Override
        public MachineTier getTier() {
            return MachineTier.Standard;
        }
    }

    private int burnTime;
    private int burnDuration;

    @UseOnly(LogicalSide.CLIENT)
    private float clientBurnProgress;

    public StirlingGeneratorBlockEntity(CapacitorKey capacityKey, CapacitorKey transferKey, CapacitorKey consumptionKey, BlockEntityType<?> pType, BlockPos pWorldPosition,
        BlockState pBlockState) {
        super(capacityKey, transferKey, consumptionKey, pType, pWorldPosition, pBlockState);

        addDataSlot(new FloatDataSlot(this::getBurnProgress, p -> clientBurnProgress = p, SyncMode.GUI));
    }

    @Override
    public Optional<ItemSlotLayout> getSlotLayout() {
        if (getTier() == MachineTier.Simple) {
            return Optional.of(ItemSlotLayout.basic(1, 0));
        }
        return Optional.of(ItemSlotLayout.withCapacitor(1, 0));
    }

    @Override
    protected ItemHandlerMaster createItemHandler(ItemSlotLayout layout) {
        // TODO: Really not a fan of how this works. Review this in my next PR... I kinda want to put slot validation into ItemSlotLayout maybe?
        return new ItemHandlerMaster(getIoConfig(), layout) {
            @NotNull
            @Override
            public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
                // Check its a capacitor.
                if (slot == 1 && !CapacitorUtil.isCapacitor(stack))
                    return stack;
                return super.insertItem(slot, stack, simulate);
            }

            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }
        };
    }

    @Override
    public void tick() {
        if (isServer()) {
            // Tick burn time even if redstone activation has stopped.
            if (isGenerating()) {
                burnTime--;
            }

            // Only continue burning if redstone is enabled.
            if (shouldAct() && !isGenerating()) {
                // Get the fuel
                ItemStack fuel = getItemHandler().getStackInSlot(0);

                if (!fuel.isEmpty()) {
                    // Get the burn time.
                    int burningTime = ForgeHooks.getBurnTime(fuel, RecipeType.SMELTING);

                    if (burningTime > 0) {
                        burnTime = burningTime;
                        burnDuration = burnTime;

                        // Remove the fuel
                        fuel.shrink(1);
                        getItemHandler().setStackInSlot(0, fuel);
                    }
                }
            }
        }

        super.tick();
    }

    @Override
    public boolean isGenerating() {
        return burnTime > 0;
    }

    public float getBurnProgress() {
        if (level.isClientSide)
            return clientBurnProgress;
        if (burnDuration == 0)
            return 0;
        return burnTime / (float) burnDuration;
    }

    @Override
    public int getGenerationRate() {
        // Stirling generator produces 10 RF per tick of burn time.
        // https://github.com/SleepyTrousers/EnderIO/blob/d6dfb9d3964946ceb9fd72a66a3cff197a51a1fe/enderio-base/src/main/java/crazypants/enderio/base/recipe/alloysmelter/VanillaSmeltingRecipe.java#L50
        // TODO: Should maybe have a constants class for energy conversions.
        // TODO: Implement efficiency!
        return 10;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
        return new StirlingGeneratorMenu(this, pInventory, pContainerId);
    }
}
