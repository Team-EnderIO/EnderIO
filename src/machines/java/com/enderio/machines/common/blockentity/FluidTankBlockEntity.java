package com.enderio.machines.common.blockentity;

import com.enderio.base.common.tag.EIOTags;
import com.enderio.base.common.util.ExperienceUtil;
import com.enderio.core.common.network.slot.FluidStackNetworkDataSlot;
import com.enderio.machines.common.blockentity.base.MachineBlockEntity;
import com.enderio.machines.common.init.MachineRecipes;
import com.enderio.machines.common.io.fluid.IFluidItemInteractive;
import com.enderio.machines.common.io.fluid.MachineFluidHandler;
import com.enderio.machines.common.io.fluid.MachineFluidTank;
import com.enderio.machines.common.io.fluid.MachineTankLayout;
import com.enderio.machines.common.io.fluid.TankAccess;
import com.enderio.machines.common.io.item.MachineInventoryLayout;
import com.enderio.machines.common.io.item.SingleSlotAccess;
import com.enderio.machines.common.menu.FluidTankMenu;
import com.enderio.machines.common.recipe.TankRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

// TODO: Rewrite this with tasks?
//       Could implement a task for each thing it currently has in the If's
public abstract class FluidTankBlockEntity extends MachineBlockEntity implements IFluidItemInteractive {

    private static final TankAccess TANK = new TankAccess();

    public static class Standard extends FluidTankBlockEntity {
        public static final int CAPACITY = 16 * FluidType.BUCKET_VOLUME;

        public Standard(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState) {
            super(type, worldPosition, blockState);
        }

        @Override
        public @Nullable MachineTankLayout getTankLayout() {
            return new MachineTankLayout.Builder().tank(TANK, CAPACITY).build();
        }
    }

    public static class Enhanced extends FluidTankBlockEntity {
        public static final int CAPACITY = 32 * FluidType.BUCKET_VOLUME;

        public Enhanced(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState) {
            super(type, worldPosition, blockState);
        }

        @Override
        public @Nullable MachineTankLayout getTankLayout() {
            return new MachineTankLayout.Builder().tank(TANK, CAPACITY).build();
        }

    }

    private final TankRecipe.Container container;
    private Optional<TankRecipe> currentRecipe = Optional.empty();

    public static final SingleSlotAccess FLUID_FILL_INPUT = new SingleSlotAccess();
    public static final SingleSlotAccess FLUID_FILL_OUTPUT = new SingleSlotAccess();
    public static final SingleSlotAccess FLUID_DRAIN_INPUT = new SingleSlotAccess();
    public static final SingleSlotAccess FLUID_DRAIN_OUTPUT = new SingleSlotAccess();

    public FluidTankBlockEntity(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState) {
        super(type, worldPosition, blockState);

        // Sync fluid for model
        addDataSlot(new FluidStackNetworkDataSlot(() -> TANK.getFluid(this), f -> TANK.setFluid(this, f)));

        // Wrap container for fluid recipes
        container = new TankRecipe.Container(getInventoryNN(), () -> TANK.getTank(this));
    }

    @Override
    public void serverTick() {
        if (canActSlow()) {
            fillInternal();
            drainInternal();
            tryTankRecipe();
            tryMendTool();
        }

        super.serverTick();
    }

    // region Inventory

    public boolean acceptItemFill(ItemStack item) {
        // bucket types
        Optional<IFluidHandlerItem> fluidHandlerCap = item.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).resolve();

        if (fluidHandlerCap.isPresent()) {
            return true;
        }

        // fill recipes
        if (level != null) {
           List<TankRecipe> allRecipes = level.getRecipeManager().getAllRecipesFor(MachineRecipes.TANK.type().get());
           if (allRecipes.stream().anyMatch((recipe) -> recipe.isEmptying() && recipe.getInput().test(item))) {
            return true;
           }
        }

        return false;
    }

    public boolean acceptItemDrain(ItemStack item) {
        // bucket types
        Optional<IFluidHandlerItem> fluidHandlerCap = item.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).resolve();

        if (fluidHandlerCap.isPresent()) {
            return true;
        }

        // Mending
        FluidStack fluid = TANK.getFluid(this);

        if (item.isDamageableItem() && !fluid.isEmpty() && fluid.getFluid().is(EIOTags.Fluids.EXPERIENCE)) {
            if (item.getEnchantmentLevel(Enchantments.MENDING) > 0) {
                return true;
            }
        }

        // drain recipes
        if (level != null) {
            List<TankRecipe> allRecipes = level.getRecipeManager().getAllRecipesFor(MachineRecipes.TANK.type().get());
            if (allRecipes.stream().anyMatch((recipe) -> !recipe.isEmptying() && recipe.getInput().test(item))) {
                return true;
            }
        }

        return false;
    }

    @Override
    public MachineInventoryLayout createInventoryLayout() {
        return MachineInventoryLayout
            .builder()
            .inputSlot((slot, stack) -> acceptItemFill(stack))
            .slotAccess(FLUID_FILL_INPUT)
            .outputSlot()
            .slotAccess(FLUID_FILL_OUTPUT)
            .inputSlot((slot, stack) -> acceptItemDrain(stack))
            .slotAccess(FLUID_DRAIN_INPUT)
            .outputSlot()
            .slotAccess(FLUID_DRAIN_OUTPUT)
            .build();
    }

    @Override
    protected void onInventoryContentsChanged(int slot) {
        super.onInventoryContentsChanged(slot);

        if (level != null) {
            if (!level.isClientSide()) {
                currentRecipe = level.getRecipeManager().getRecipeFor(MachineRecipes.TANK.type().get(), container, level);
            }
        }
    }

    // endregion

    // region Fluid Storage

    @Override
    protected @Nullable MachineFluidHandler createFluidHandler(MachineTankLayout layout) {
        return new MachineFluidHandler(getIOConfig(), getTankLayout()) {
            @Override
            protected void onContentsChanged(int slot) {
                onTankContentsChanged();
                updateMachineState(MachineState.EMPTY_TANK, TANK.getFluidAmount(this) <= 0);
                super.onContentsChanged(slot);
            }
        };
    }

    public MachineFluidTank getFluidTank() {
        return TANK.getTank(this);
    }

    // endregion

    //TODO: enable fluid tanks to receive stackable fluid containers
    private void fillInternal() {
        ItemStack inputItem = FLUID_FILL_INPUT.getItemStack(this);
        ItemStack outputItem = FLUID_FILL_OUTPUT.getItemStack(this);
        if (!inputItem.isEmpty()) {
            if (inputItem.getItem() instanceof BucketItem filledBucket) {
                if (outputItem.isEmpty() || (outputItem.getItem() == Items.BUCKET && outputItem.getCount() < outputItem.getMaxStackSize())) {
                    int filled = TANK.fill(this, new FluidStack(filledBucket.getFluid(), FluidType.BUCKET_VOLUME), IFluidHandler.FluidAction.SIMULATE);
                    if (filled == FluidType.BUCKET_VOLUME) {
                        TANK.fill(this, new FluidStack(filledBucket.getFluid(), FluidType.BUCKET_VOLUME), IFluidHandler.FluidAction.EXECUTE);
                        inputItem.shrink(1);
                        FLUID_FILL_OUTPUT.insertItem(this, Items.BUCKET.getDefaultInstance(), false);
                    }
                }
            } else {
                Optional<IFluidHandlerItem> fluidHandlerCap = inputItem.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).resolve();
                if (fluidHandlerCap.isPresent() && outputItem.isEmpty()) {
                    IFluidHandlerItem itemFluid = fluidHandlerCap.get();

                    int filled = moveFluids(itemFluid, getFluidHandler(), TANK.getCapacity(this));
                    if (filled > 0) {
                        FLUID_FILL_OUTPUT.setStackInSlot(this, itemFluid.getContainer());
                        FLUID_FILL_INPUT.setStackInSlot(this, ItemStack.EMPTY);
                    }
                }
            }
        }
    }

    @Override
    public InteractionResult onBlockEntityUsed(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack stack = player.getItemInHand(hand);
        if (!stack.isEmpty() && handleFluidItemInteraction(player, hand, stack, this, TANK)) {
            player.getInventory().setChanged();
            return InteractionResult.CONSUME;
        }
        return super.onBlockEntityUsed(state, level, pos, player, hand, hit);
    }

    //TODO: enable fluid tanks to receive stackable fluid containers
    private void drainInternal() {
        ItemStack inputItem = FLUID_DRAIN_INPUT.getItemStack(this);
        ItemStack outputItem = FLUID_DRAIN_OUTPUT.getItemStack(this);
        if (!inputItem.isEmpty()) {
            if (inputItem.getItem() == Items.BUCKET) {
                if (!TANK.getFluid(this).isEmpty()) {
                    FluidStack stack = TANK.drain(this, FluidType.BUCKET_VOLUME, IFluidHandler.FluidAction.SIMULATE);
                    if (stack.getAmount() == FluidType.BUCKET_VOLUME && (outputItem.isEmpty() || (outputItem.getItem() == stack.getFluid().getBucket()
                        && outputItem.getCount() < outputItem.getMaxStackSize()))) {
                        TANK.drain(this, FluidType.BUCKET_VOLUME, IFluidHandler.FluidAction.EXECUTE);
                        inputItem.shrink(1);
                        if (outputItem.isEmpty()) {
                            FLUID_DRAIN_OUTPUT.setStackInSlot(this, stack.getFluid().getBucket().getDefaultInstance());
                        } else {
                            outputItem.grow(1);
                        }
                    }
                }
            } else {
                Optional<IFluidHandlerItem> fluidHandlerCap = inputItem.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).resolve();
                if (fluidHandlerCap.isPresent() && outputItem.isEmpty()) {
                    IFluidHandlerItem itemFluid = fluidHandlerCap.get();
                    int filled = moveFluids(getFluidHandler(), itemFluid, TANK.getFluidAmount(this));
                    if (filled > 0) {
                        FLUID_DRAIN_OUTPUT.setStackInSlot(this, itemFluid.getContainer());
                        FLUID_DRAIN_INPUT.setStackInSlot(this, ItemStack.EMPTY);
                    }
                }
            }
        }
    }

    private void tryTankRecipe() {
        currentRecipe.ifPresent(recipe -> {
            ItemStack recipeOutput = recipe.getOutput().copy();

            if (recipe.isEmptying()) {
                ItemStack outputStack = FLUID_FILL_OUTPUT.getItemStack(this);

                if (outputStack.isEmpty() || (outputStack.is(recipeOutput.getItem()) && outputStack.getCount() < outputStack.getMaxStackSize())) {
                    FLUID_FILL_INPUT.getItemStack(this).shrink(1);

                    TANK.fill(this, recipe.getFluid(), IFluidHandler.FluidAction.EXECUTE);

                    if (outputStack.isEmpty()) {
                        FLUID_FILL_OUTPUT.setStackInSlot(this, recipeOutput);
                    } else {
                        FLUID_FILL_OUTPUT.getItemStack(this).grow(1);
                    }
                }
            } else {
                ItemStack outputStack = FLUID_DRAIN_OUTPUT.getItemStack(this);

                if (outputStack.isEmpty() || (outputStack.is(recipeOutput.getItem()) && outputStack.getCount() < outputStack.getMaxStackSize())) {
                    FLUID_DRAIN_INPUT.getItemStack(this).shrink(1);

                    TANK.drain(this, recipe.getFluid(), IFluidHandler.FluidAction.EXECUTE);

                    if (outputStack.isEmpty()) {
                        FLUID_DRAIN_OUTPUT.setStackInSlot(this, recipeOutput);
                    } else {
                        FLUID_DRAIN_OUTPUT.getItemStack(this).grow(1);
                    }
                }
            }
        });
    }

    private void tryMendTool() {
        FluidStack fluid = TANK.getFluid(this);

        if (!fluid.isEmpty() && fluid.getFluid().is(EIOTags.Fluids.EXPERIENCE)) {
            ItemStack tool = FLUID_DRAIN_INPUT.getItemStack(this);
            if (tool.isDamageableItem() && tool.getEnchantmentLevel(Enchantments.MENDING) > 0) {

                ItemStack repairedTool = tool.copy();

                int damage = tool.getDamageValue();
                int xpAmount = (int) Math.floor(damage / tool.getXpRepairRatio());
                int fluidAmount = xpAmount * ExperienceUtil.EXP_TO_FLUID;

                FluidStack drainedXp = TANK.drain(this, fluidAmount, IFluidHandler.FluidAction.EXECUTE);
                int repairAmount = (int) Math.floor(drainedXp.getAmount() * tool.getXpRepairRatio() / ExperienceUtil.EXP_TO_FLUID);
                repairedTool.setDamageValue(Math.max(0, damage - repairAmount));

                FLUID_DRAIN_INPUT.setStackInSlot(this, ItemStack.EMPTY);
                FLUID_DRAIN_OUTPUT.setStackInSlot(this, repairedTool);
            }
        }
    }

    private void onTankContentsChanged() {
        if (level != null) {
            if (!level.isClientSide()) {
                currentRecipe = level.getRecipeManager().getRecipeFor(MachineRecipes.TANK.type().get(), container, level);
            }
            level.getLightEngine().checkBlock(worldPosition);
        }
    }

    @Nullable
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
        return new FluidTankMenu(this, pInventory, pContainerId);
    }

    @Override
    public int getLightEmission() {
        return TANK.getFluid(this).getFluid().getFluidType().getLightLevel();
    }
    
    // region Serialization

    @Override
    public void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
    }

    // endregion
}
