package com.enderio.machines.common.blockentity;

import com.enderio.base.common.tag.EIOTags;
import com.enderio.base.common.util.ExperienceUtil;
import com.enderio.core.common.network.NetworkDataSlot;
import com.enderio.machines.common.attachment.FluidTankUser;
import com.enderio.machines.common.blockentity.base.MachineBlockEntity;
import com.enderio.machines.common.init.MachineBlockEntities;
import com.enderio.machines.common.init.MachineRecipes;
import com.enderio.machines.common.io.fluid.FluidItemInteractive;
import com.enderio.machines.common.io.fluid.MachineFluidHandler;
import com.enderio.machines.common.io.fluid.MachineFluidTank;
import com.enderio.machines.common.io.fluid.MachineTankLayout;
import com.enderio.machines.common.io.fluid.TankAccess;
import com.enderio.machines.common.io.item.MachineInventoryLayout;
import com.enderio.machines.common.io.item.SingleSlotAccess;
import com.enderio.machines.common.menu.FluidTankMenu;
import com.enderio.machines.common.recipe.TankRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

// TODO: Rewrite this with tasks?
//       Could implement a task for each thing it currently has in the If's
public abstract class FluidTankBlockEntity extends MachineBlockEntity implements FluidItemInteractive, FluidTankUser {

    public static class Standard extends FluidTankBlockEntity {
        public static final int CAPACITY = 16 * FluidType.BUCKET_VOLUME;

        public Standard(BlockPos worldPosition, BlockState blockState) {
            super(MachineBlockEntities.FLUID_TANK.get(), worldPosition, blockState);
        }

        @Override
        public MachineTankLayout getTankLayout() {
            return new MachineTankLayout.Builder().tank(TANK, CAPACITY).build();
        }
    }

    public static class Enhanced extends FluidTankBlockEntity {
        public static final int CAPACITY = 32 * FluidType.BUCKET_VOLUME;

        public Enhanced(BlockPos worldPosition, BlockState blockState) {
            super(MachineBlockEntities.PRESSURIZED_FLUID_TANK.get(), worldPosition, blockState);
        }

        @Override
        public MachineTankLayout getTankLayout() {
            return new MachineTankLayout.Builder().tank(TANK, CAPACITY).build();
        }

    }

    private final TankRecipe.Container container;
    private final MachineFluidHandler fluidHandler;
    private static final TankAccess TANK = new TankAccess();

    // TODO: Swap from optional to nullable?
    private Optional<RecipeHolder<TankRecipe>> currentRecipe = Optional.empty();

    public static final SingleSlotAccess FLUID_FILL_INPUT = new SingleSlotAccess();
    public static final SingleSlotAccess FLUID_FILL_OUTPUT = new SingleSlotAccess();
    public static final SingleSlotAccess FLUID_DRAIN_INPUT = new SingleSlotAccess();
    public static final SingleSlotAccess FLUID_DRAIN_OUTPUT = new SingleSlotAccess();

    public FluidTankBlockEntity(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState) {
        super(type, worldPosition, blockState);
        fluidHandler = createFluidHandler();

        // Sync fluid for model
        addDataSlot(NetworkDataSlot.FLUID_STACK.create(() -> TANK.getFluid(this), f -> TANK.setFluid(this, f)));

        // Wrap container for fluid recipes
        container = new TankRecipe.Container(getInventoryNN(), TANK.getTank(this));
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
        IFluidHandlerItem fluidHandlerCap = item.getCapability(Capabilities.FluidHandler.ITEM);
        if (fluidHandlerCap != null) {
            return true;
        }

        // fill recipes
        if (level != null) {
            List<RecipeHolder<TankRecipe>> allRecipes = level.getRecipeManager().getAllRecipesFor(MachineRecipes.TANK.type().get());
            if (allRecipes.stream().anyMatch((recipe) -> recipe.value().mode() == TankRecipe.Mode.EMPTY && recipe.value().input().test(item))) {
                return true;
            }
        }

        return false;
    }

    public boolean acceptItemDrain(ItemStack item) {
        // bucket types
        IFluidHandlerItem fluidHandlerCap = item.getCapability(Capabilities.FluidHandler.ITEM);
        if (fluidHandlerCap != null) {
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
            List<RecipeHolder<TankRecipe>> allRecipes = level.getRecipeManager().getAllRecipesFor(MachineRecipes.TANK.type().get());
            if (allRecipes.stream().anyMatch((recipe) -> recipe.value().mode() == TankRecipe.Mode.FILL && recipe.value().input().test(item))) {
                return true;
            }
        }

        return false;
    }

    @Override
    public MachineInventoryLayout getInventoryLayout() {
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

    @Override
    public MachineFluidHandler getFluidHandler() {
        return this.fluidHandler;
    }

    @Override
    public MachineFluidHandler createFluidHandler() {
        return new MachineFluidHandler(this, getTankLayout()) {
            @Override
            protected void onContentsChanged(int slot) {
                onTankContentsChanged();
                setChanged();
                super.onContentsChanged(slot);
                updateMachineState(MachineState.EMPTY_TANK, TANK.getFluidAmount(this) <= 0);
            }
        };
    }

    public MachineFluidTank getFluidTank() {
        return TANK.getTank(this);
    }

    //TODO: enable fluid tanks to receive stackable fluid containers
    private void fillInternal() {
        ItemStack inputItem = FLUID_FILL_INPUT.getItemStack(this);
        ItemStack outputItem = FLUID_FILL_OUTPUT.getItemStack(this);
        if (!inputItem.isEmpty()) {
            if (inputItem.getItem() instanceof BucketItem filledBucket) {
                if (outputItem.isEmpty() || (outputItem.getItem() == Items.BUCKET && outputItem.getCount() < outputItem.getMaxStackSize())) {
                    int filled = TANK.fill(this, new FluidStack(filledBucket.content, FluidType.BUCKET_VOLUME), IFluidHandler.FluidAction.SIMULATE);
                    if (filled == FluidType.BUCKET_VOLUME) {
                        TANK.fill(this, new FluidStack(filledBucket.content, FluidType.BUCKET_VOLUME), IFluidHandler.FluidAction.EXECUTE);
                        inputItem.shrink(1);
                        FLUID_FILL_OUTPUT.insertItem(this, Items.BUCKET.getDefaultInstance(), false);
                    }
                }
            } else {
                IFluidHandlerItem fluidHandlerItem = inputItem.getCapability(Capabilities.FluidHandler.ITEM);
                if (fluidHandlerItem != null && outputItem.isEmpty()) {
                    int filled = FluidUtil.tryFluidTransfer(getFluidHandler(), fluidHandlerItem, TANK.getFluidAmount(this), true).getAmount();
                    if (filled > 0) {
                        FLUID_FILL_OUTPUT.setStackInSlot(this, fluidHandlerItem.getContainer());
                        FLUID_FILL_INPUT.setStackInSlot(this, ItemStack.EMPTY);
                    }
                }
            }
        }
    }

    // endregion

    @Override
    public ItemInteractionResult onBlockEntityUsed(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack stack = player.getItemInHand(hand);
        if (!stack.isEmpty() && handleFluidItemInteraction(player, hand, stack, this, TANK)) {
            player.getInventory().setChanged();
            return ItemInteractionResult.CONSUME;
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
                IFluidHandlerItem fluidHandlerItem = inputItem.getCapability(Capabilities.FluidHandler.ITEM);
                if (fluidHandlerItem != null && outputItem.isEmpty()) {
                    int filled = FluidUtil.tryFluidTransfer(fluidHandlerItem, getFluidHandler(), TANK.getFluidAmount(this), true).getAmount();
                    if (filled > 0) {
                        FLUID_DRAIN_OUTPUT.setStackInSlot(this, fluidHandlerItem.getContainer());
                        FLUID_DRAIN_INPUT.setStackInSlot(this, ItemStack.EMPTY);
                    }
                }
            }
        }
    }

    private void tryTankRecipe() {
        currentRecipe.ifPresent(recipe -> {
            switch (recipe.value().mode()) {
            case FILL -> {
                ItemStack outputStack = FLUID_FILL_OUTPUT.getItemStack(this);

                if (outputStack.isEmpty() || (outputStack.is(recipe.value().output()) && outputStack.getCount() < outputStack.getMaxStackSize())) {
                    FLUID_FILL_INPUT.getItemStack(this).shrink(1);

                    TANK.fill(this, recipe.value().fluid(), IFluidHandler.FluidAction.EXECUTE);

                    if (outputStack.isEmpty()) {
                        FLUID_FILL_OUTPUT.setStackInSlot(this, new ItemStack(recipe.value().output(), 1));
                    } else {
                        FLUID_FILL_OUTPUT.getItemStack(this).grow(1);
                    }
                }
            }
            case EMPTY -> {
                ItemStack outputStack = FLUID_DRAIN_OUTPUT.getItemStack(this);

                if (outputStack.isEmpty() || (outputStack.is(recipe.value().output()) && outputStack.getCount() < outputStack.getMaxStackSize())) {
                    FLUID_DRAIN_INPUT.getItemStack(this).shrink(1);

                    TANK.drain(this, recipe.value().fluid(), IFluidHandler.FluidAction.EXECUTE);

                    if (outputStack.isEmpty()) {
                        FLUID_DRAIN_OUTPUT.setStackInSlot(this, new ItemStack(recipe.value().output(), 1));
                    } else {
                        FLUID_DRAIN_OUTPUT.getItemStack(this).grow(1);
                    }
                }
            }
            default -> throw new NotImplementedException();
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
    public void saveAdditional(CompoundTag pTag, HolderLookup.Provider lookupProvider) {
        super.saveAdditional(pTag, lookupProvider);
        saveTank(lookupProvider, pTag);
    }

    @Override
    public void loadAdditional(CompoundTag pTag, HolderLookup.Provider lookupProvider) {
        super.loadAdditional(pTag, lookupProvider);
        loadTank(lookupProvider, pTag);
    }

    // endregion
}
