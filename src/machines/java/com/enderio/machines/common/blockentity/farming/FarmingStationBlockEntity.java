package com.enderio.machines.common.blockentity.farming;

import com.enderio.api.capacitor.CapacitorModifier;
import com.enderio.api.capacitor.QuadraticScalable;
import com.enderio.api.io.energy.EnergyIOMode;
import com.enderio.core.common.network.slot.BooleanNetworkDataSlot;
import com.enderio.core.common.network.slot.IntegerNetworkDataSlot;
import com.enderio.machines.common.blockentity.base.PoweredMachineBlockEntity;
import com.enderio.machines.common.blockentity.farming.farmers.IFarmer;
import com.enderio.machines.common.blockentity.farming.farmers.PlantFarmer;
import com.enderio.machines.common.blockentity.farming.farmers.PumpkinFarmer;
import com.enderio.machines.common.config.MachinesConfig;
import com.enderio.machines.common.io.item.MachineInventoryLayout;
import com.enderio.machines.common.io.item.MultiSlotAccess;
import com.enderio.machines.common.io.item.SingleSlotAccess;
import com.enderio.machines.common.menu.FarmingStationMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FarmingStationBlockEntity extends PoweredMachineBlockEntity {

    public static final QuadraticScalable CAPACITY = new QuadraticScalable(CapacitorModifier.ENERGY_CAPACITY, MachinesConfig.COMMON.ENERGY.WIRED_CHARGER_CAPACITY);

    public static final QuadraticScalable USAGE = new QuadraticScalable(CapacitorModifier.ENERGY_USE, MachinesConfig.COMMON.ENERGY.WIRED_CHARGER_USAGE);

    public static final SingleSlotAccess HOE = new SingleSlotAccess();

    public static final SingleSlotAccess AXE = new SingleSlotAccess();

    public static final SingleSlotAccess SHEARS = new SingleSlotAccess();

    public static final MultiSlotAccess FERTILIZERS = new MultiSlotAccess();

    public static final MultiSlotAccess INPUTS = new MultiSlotAccess();

    public static final MultiSlotAccess OUTPUT = new MultiSlotAccess();

    //Should be dynamically increased with a better capacitor
    public static final int TICK_PER_OPERATION = 1;

    public static final int GET_WATER_PER_OPERATION = 20;

    private int completedTicks = 0;
    private int counter = 0;

    private List<BlockPos> blocksInRange;


    public FarmingStationBlockEntity(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState) {
        super(EnergyIOMode.Input, CAPACITY, USAGE, type, worldPosition, blockState);
        setRange(getRange());
        blocksInRange = getBlocksInRange();

        rangeDataSlot = new IntegerNetworkDataSlot(this::getRange, r -> this.range = r);
        addDataSlot(rangeDataSlot);

        rangeVisibleDataSlot = new BooleanNetworkDataSlot(this::isRangeVisible, b -> this.rangeVisible = b);
        addDataSlot(rangeVisibleDataSlot);
    }


    @Override
    public @Nullable MachineInventoryLayout getInventoryLayout() {
        return MachineInventoryLayout.builder()
            .inputSlot(4, (slot, stack) -> acceptInput(stack))
            .slotAccess(INPUTS)
            .setStackLimit(1) // Reset stack limit
            .inputSlot((slot, stack) -> stack.getItem() instanceof HoeItem)
            .slotAccess(HOE)
            .inputSlot((slot, stack) -> stack.getItem() instanceof AxeItem)
            .slotAccess(AXE)
            .inputSlot((slot, stack) -> stack.getItem() instanceof ShearsItem)
            .slotAccess(SHEARS)
            .setStackLimit(64) // Reset stack limit
            .inputSlot(2, (slot, stack) -> stack.getItem() instanceof BoneMealItem)
            .slotAccess(FERTILIZERS)
            .outputSlot(6)
            .slotAccess(OUTPUT)
            .capacitor()
            .build();
    }

    @Override
    protected boolean isActive() {
        return false;
    }

    @Override
    public void serverTick() {
        super.serverTick();
        if (canAct()) {

            completedTicks = (completedTicks + 1) % TICK_PER_OPERATION;
            if (completedTicks == 0) {
                if (counter < blocksInRange.size()) {
                    till(blocksInRange.get(counter).below());
                    plant(blocksInRange.get(counter));
                    boneMealBlock();
                    waterSoil();
                    collect(blocksInRange.get(counter));
                    increasePointer();
                }
            }
        } else {
            completedTicks = 0;
            resetCounter();
        }

    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player player) {
        return new FarmingStationMenu(this, pPlayerInventory, pContainerId);
    }

    @Override
    protected @Nullable FluidTank createFluidTank() {
        return new FluidTank(2000, f -> f.getFluid().is(FluidTags.WATER));
    }



    //region operations

    //Randomly choose a crop and apply bone meal on it
    public void boneMealBlock() {
        ItemStack boneMealItemStack = getFirstNonEmptyItemStack(FERTILIZERS);
        if (!boneMealItemStack.equals(ItemStack.EMPTY) && getEnergyStorage().getEnergyStored() >= getEnergyStorage().getMaxEnergyUse()) {
            Random random = new Random();
            int chosenCrop = random.nextInt(blocksInRange.size());
            BoneMealItem.applyBonemeal(boneMealItemStack, level, blocksInRange.get(chosenCrop), FakePlayerFactory.getMinecraft((net.minecraft.server.level.ServerLevel) level));
        }
    }

    //Randomly makes farmland wet
    public void waterSoil() {
        Random random = new Random();
        int chosenBlock = random.nextInt(blocksInRange.size());

        BlockPos pos = getBlocksInRange().get(chosenBlock);
        BlockState blockState = level.getBlockState(pos.below());

        if (blockState.getBlock().equals(Blocks.FARMLAND)
            && GET_WATER_PER_OPERATION <= getFluidTank().getFluidAmount()
            && getEnergyStorage().getEnergyStored() >= getEnergyStorage().getMaxEnergyUse()
        ) {
            this.level.setBlockAndUpdate(pos.below(), blockState.setValue(FarmBlock.MOISTURE, 7));
            getFluidTank().drain(GET_WATER_PER_OPERATION, IFluidHandler.FluidAction.EXECUTE);
        }
    }


    public void till(BlockPos pos) {
        ItemStack hoe = getInventory().getStackInSlot(HOE.getIndex());

        if (!hoe.isEmpty() && getEnergyStorage().getEnergyStored() >= getEnergyStorage().getMaxEnergyUse()) {
            BlockState blockState = level.getBlockState(pos);
            if (blockState.getBlock().equals(Blocks.DIRT) || blockState.getBlock().equals(Blocks.GRASS_BLOCK)) {
                this.level.setBlockAndUpdate(pos, Blocks.FARMLAND.defaultBlockState());
                level.playSound(null, pos, SoundEvents.HOE_TILL, SoundSource.BLOCKS, 1.0f, 1.0f);
                hoe.hurt(1, level.getRandom(), null);
                getEnergyStorage().consumeEnergy(getEnergyStorage().getMaxEnergyUse(), false);
            }
        }
    }

    public void plant(BlockPos pos) {
        ItemStack inputItemStack = getFirstNonEmptyItemStack(INPUTS);

        if (!inputItemStack.equals(ItemStack.EMPTY)) {

            //If an item is a crop or everything that can grow
            if (inputItemStack.getItem() instanceof BlockItem && ((BlockItem) inputItemStack.getItem()).getBlock() instanceof IPlantable) {
                Block block = ((BlockItem) inputItemStack.getItem()).getBlock();

                //If the seed / sapling can be planted on the block
                if (this.level.getBlockState(pos.below()).canSustainPlant(level, pos.below(), Direction.UP, (IPlantable) block)
                    && block.canSurvive(this.level.getBlockState(pos), this.getLevel(), pos)) {

                    //If the block isn't already planted
                    if (this.level.getBlockState(pos).getBlock().equals(Blocks.AIR)) {

                        //Plant it and update corresponding machine slots
                        if (this.level.setBlockAndUpdate(pos, ((IPlantable) block).getPlant(level, pos))) {
                            inputItemStack.shrink(1);
                            getEnergyStorage().consumeEnergy(getEnergyStorage().getMaxEnergyUse(), false);
                        }
                    }
                }
            }
        }
    }

    public void collect(BlockPos pos) {
        Block block = getLevel().getBlockState(pos).getBlock();
        IFarmer farmer = null;

        //We look for the good operation to do
        if (block instanceof CropBlock)
            farmer = new PlantFarmer();
        else if (block instanceof PumpkinBlock || block instanceof MelonBlock)
            farmer = new PumpkinFarmer();


        //If a block can be harvested, do the operation
        if (farmer != null) {
            List<ItemStack> items = farmer.doOperation(getLevel(), pos, this.level.getBlockState(pos), true);
            getEnergyStorage().consumeEnergy(farmer.getCostPerOperation(), false);

            boolean canOutput = canOutput(items);

            if (canOutput) {
                farmer.doOperation(getLevel(), pos, this.level.getBlockState(pos), false);
                for (ItemStack loot : items) {

                    for (SingleSlotAccess outputSlot : OUTPUT.getAccesses()) {

                        ItemStack simulated = outputSlot.insertItem(getInventory(), loot, true);
                        if (simulated.isEmpty()) {
                            outputSlot.insertItem(getInventory(), loot, false);
                            break;
                        }
                    }
                }
            }
        }
    }


    public void increasePointer() {
        this.counter = (counter + 1) % blocksInRange.size();
    }

    public void resetCounter() {
        this.counter = 0;
    }

    //endregion

    //region inventory update

    //Get all blocks that are around the farming station at the same y level (sometimes need to get pos.below)
    List<BlockPos> getBlocksInRange() {
        BlockPos farmerPos = getBlockPos();

        Stream<BlockPos> blocks = BlockPos.betweenClosedStream(farmerPos.getX() - getRange(), farmerPos.getY(), farmerPos.getZ() - getRange(),
            farmerPos.getX() + getRange(), farmerPos.getY(), farmerPos.getZ() + getRange());

        return blocks
            .map(BlockPos::immutable)
            .distinct()
            .collect(Collectors.toList());
    }

    public ItemStack getFirstNonEmptyItemStack(MultiSlotAccess slot) {
        for (int i = 0; i < slot.size(); i++) {
            ItemStack slotItemStack = slot.get(i).getItemStack(getInventory());
            if (slotItemStack.isEmpty()) {
                return slot.get(i).getItemStack(getInventory());
            }
        }
        return ItemStack.EMPTY;
    }


    @Override
    public int getRange() {
        if (requiresCapacitor() && isCapacitorInstalled()) {
            if (getCapacitorData().getBase() <= 1)
                return 4;
            else if (getCapacitorData().getBase() <= 2)
                return 6;
            else if (getCapacitorData().getBase() <= 3)
                return 10;
        }
        return 0;
    }

    @Override
    public int getMaxRange() {
        return 10;
    }

    @Override
    protected void onInventoryContentsChanged(int slot) {
        super.onInventoryContentsChanged(slot);
        setRange(getRange());
        blocksInRange = getBlocksInRange();
    }

    public boolean acceptInput(ItemStack stack) {
        return stack.getItem() instanceof BlockItem && ((BlockItem) stack.getItem()).getBlock() instanceof IPlantable;
    }

    public boolean canOutput(List<ItemStack> items) {
        boolean canOutput = true;

        for (ItemStack loot : items) {
            canOutput = false;

            for (SingleSlotAccess outputSlot : OUTPUT.getAccesses()) {

                ItemStack simulated = outputSlot.insertItem(getInventory(), loot, true);
                if (simulated.isEmpty()) {
                    canOutput = true;
                    break;
                }
            }

            if (!canOutput)
                break;
        }

        return canOutput;
    }
    //endregion
}