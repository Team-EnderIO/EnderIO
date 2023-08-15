package com.enderio.machines.common.blockentity;

import com.enderio.api.capacitor.CapacitorModifier;
import com.enderio.api.capacitor.QuadraticScalable;
import com.enderio.api.io.energy.EnergyIOMode;
import com.enderio.machines.common.blockentity.base.PoweredMachineBlockEntity;
import com.enderio.machines.common.config.MachinesConfig;
import com.enderio.machines.common.io.item.MachineInventoryLayout;
import com.enderio.machines.common.io.item.MultiSlotAccess;
import com.enderio.machines.common.io.item.SingleSlotAccess;
import com.enderio.machines.common.menu.FarmingStationMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.IPlantable;
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
    public static final int TICK_PER_OPERATION = 5;
    public static int RANGE = 4;

    public static final int GET_WATER_PER_OPERATION = 1000/ RANGE*RANGE;

    private int completedTicks = 0;
    private int counter = 0;

    private List<BlockPos> blocksInRange;


    public FarmingStationBlockEntity(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState) {
        super(EnergyIOMode.Input, CAPACITY, USAGE, type, worldPosition, blockState);
        blocksInRange = getBlocksInRange();
        setRange(RANGE);
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
                    counter++;
                } else {
                    counter = 0;
                }

            }
        } else {
            completedTicks = 0;
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

    public boolean acceptInput(ItemStack stack) {
        return stack.getItem() instanceof BlockItem && ((BlockItem) stack.getItem()).getBlock() instanceof IPlantable;
    }


    //Randomly choose a crop and apply bone meal on it
    public void boneMealBlock() {
        ItemStack boneMealItemStack = getFirstNonEmptyItemStack(FERTILIZERS);
        Minecraft mc = Minecraft.getInstance();
        if (!boneMealItemStack.equals(ItemStack.EMPTY)) {
            Random random = new Random();
            int chosenCrop = random.nextInt(blocksInRange.size());
            BoneMealItem.applyBonemeal(boneMealItemStack, level, blocksInRange.get(chosenCrop), mc.player);
        }
    }

    //Randomly makes farmland wet
    public void waterSoil() {
        Random random = new Random();
        int chosenBlock = random.nextInt(blocksInRange.size());

        BlockPos pos = getBlocksInRange().get(chosenBlock);
        BlockState blockState = level.getBlockState(pos.below());

        if (blockState.getBlock().equals(Blocks.FARMLAND) && GET_WATER_PER_OPERATION >= getFluidTank().getFluidAmount()) {
            this.level.setBlockAndUpdate(pos.below(), blockState.setValue(FarmBlock.MOISTURE, 7));
            getFluidTank().drain(GET_WATER_PER_OPERATION, IFluidHandler.FluidAction.EXECUTE);
        }
    }


    public void till(BlockPos pos) {
        ItemStack hoe = getInventory().getStackInSlot(HOE.getIndex());

        if (!hoe.isEmpty()) {
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
                if (this.level.getBlockState(pos.below()).canSustainPlant(level, pos.below(), Direction.UP, (IPlantable) block)) {

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
            if (!slotItemStack.equals(ItemStack.EMPTY) && slotItemStack.getCount() != 0) {
                return slot.get(i).getItemStack(getInventory());
            }
        }
        return ItemStack.EMPTY;
    }
}