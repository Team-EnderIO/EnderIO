package com.enderio.machines.common.blockentity;

import com.enderio.api.capacitor.CapacitorModifier;
import com.enderio.api.capacitor.QuadraticScalable;
import com.enderio.base.common.init.EIOItems;
import com.enderio.base.common.item.capacitors.FixedCapacitorItem;
import com.enderio.base.common.item.capacitors.LootCapacitorItem;
import com.enderio.core.common.recipes.OutputStack;
import com.enderio.core.common.sync.FluidStackDataSlot;
import com.enderio.core.common.sync.SyncMode;
import com.enderio.machines.common.blockentity.base.PoweredCraftingMachine;
import com.enderio.machines.common.blockentity.task.PoweredCraftingTask;
import com.enderio.machines.common.init.MachineRecipes;
import com.enderio.machines.common.io.fluid.MachineFluidHandler;
import com.enderio.machines.common.io.fluid.MachineFluidTank;
import com.enderio.machines.common.io.item.MachineInventory;
import com.enderio.machines.common.io.item.MachineInventoryLayout;
import com.enderio.machines.common.io.item.MultiSlotAccess;
import com.enderio.machines.common.io.item.SingleSlotAccess;
import com.enderio.machines.common.menu.VatMenu;
import com.enderio.machines.common.recipe.VatRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class VatBlockEntity extends PoweredCraftingMachine<VatRecipe, VatBlockEntity.VatContainer> {
    private final MachineFluidTank inputFluidTank;
    private final MachineFluidTank outputFluidTank;
    public static final SingleSlotAccess LEFT_SLOT = new SingleSlotAccess();
    public static final SingleSlotAccess RIGHT_SLOT = new SingleSlotAccess();
    private static final QuadraticScalable CAPACITY = new QuadraticScalable(CapacitorModifier.ENERGY_CAPACITY, () -> 100000f);
    private static final QuadraticScalable TRANSFER = new QuadraticScalable(CapacitorModifier.ENERGY_TRANSFER, () -> 120f);
    private static final QuadraticScalable USAGE = new QuadraticScalable(CapacitorModifier.ENERGY_USE, () -> 30f);
    private final VatContainer container;
    public VatBlockEntity(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState) {
        super(MachineRecipes.VATTING.type().get(), CAPACITY, TRANSFER, USAGE, type, worldPosition, blockState);

        // Create fluid tanks, set the filter for the input tank to only fluids that is used as inputs
        // in the machine. Since the output tank is later blocked from any input, no filter is needed
        this.inputFluidTank = new MachineFluidTank(8000, (stack) -> {
            assert level != null;
            return level.getRecipeManager().getAllRecipesFor(getRecipeType()).stream().anyMatch(vatRecipe -> vatRecipe.getInputFluid()==stack.getFluid());
        }, this);
        this.outputFluidTank = new MachineFluidTank(this);

        // Disallow input by API sources (pipes, buckets, etc.)
        this.inputFluidTank.allowOutput = false;
        this.outputFluidTank.allowInput = false;

        MachineFluidHandler fluidHandler = new MachineFluidHandler(getIOConfig(), inputFluidTank, outputFluidTank);
        // Recipe class needs access to the tanks through the container,
        // so just adding them as members in a subclass solves that problem
        this.container = new VatContainer(getInventory(), inputFluidTank);
        // Add capability provider
        addCapabilityProvider(fluidHandler);

        addDataSlot(new FluidStackDataSlot(inputFluidTank::getFluid, inputFluidTank::setFluid, SyncMode.WORLD));
        addDataSlot(new FluidStackDataSlot(outputFluidTank::getFluid, outputFluidTank::setFluid, SyncMode.WORLD));
    }

    //since recipes don't only depend on items' present, we need a custom solution
    @Override
    protected Optional<VatRecipe> findRecipe() {
        if (inputFluidTank.isEmpty())
            return Optional.empty();
        if (LEFT_SLOT.getItemStack(container).isEmpty()||RIGHT_SLOT.getItemStack(container).isEmpty()){
            return Optional.empty();
        }
        assert level != null;
        RecipeManager recipeManager = level.getRecipeManager();
        List<VatRecipe> possibleRecipes = recipeManager.getAllRecipesFor(recipeType);
        ItemStack leftContent = LEFT_SLOT.getItemStack(container);
        ItemStack rightContent = RIGHT_SLOT.getItemStack(container);

        for (VatRecipe recipe : possibleRecipes) {
            if(!recipe.matches(container, level))
                continue;//didn't match
            if(inputFluidTank.getFluid().getFluid()!=recipe.getInputFluid() ||
                inputFluidTank.getFluid().getAmount()<recipe.calcFluidConsumptionForItemCombo(leftContent.getItem(), rightContent.getItem()))
                continue;//didn't match
            if(!outputFluidTank.isEmpty() && recipe.getOutputFluid() != outputFluidTank.getFluid().getFluid())
                continue;//didn't match

            return Optional.of(recipe);
        }
        return Optional.empty();
    }

    @Override
    protected VatContainer getContainer() {
        return container;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new VatMenu(this, pPlayerInventory, pContainerId);
    }

    @Override
    public MachineInventoryLayout getInventoryLayout() {
        //TODO: make this functionality global for all crafting machines?? How would this work with the painting machine??
        return MachineInventoryLayout.builder()
            .inputSlot((integer, itemStack) -> //filter only stacks that are members of registered recipes
                level.getRecipeManager().getAllRecipesFor(getRecipeType()).stream()
                    .anyMatch(vatRecipe -> vatRecipe.matchesLeft(itemStack)))
            .slotAccess(LEFT_SLOT)

            .inputSlot((integer, itemStack) ->
                level.getRecipeManager().getAllRecipesFor(getRecipeType()).stream()
                    .anyMatch(vatRecipe -> vatRecipe.matchesRight(itemStack)))
            .slotAccess(RIGHT_SLOT)
            .capacitor()
            .build();
    }

    public FluidTank getInputTank() {
        return inputFluidTank;
    }

    public FluidTank getOutputTank() {
        return outputFluidTank;
    }

    @Override
    public InteractionResult onBlockEntityUsed(BlockState state, Level pLevel, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        InteractionResult inResult = inputFluidTank.onClickedWithPotentialFluidItem(player,hand);
        InteractionResult outResult = outputFluidTank.onClickedWithPotentialFluidItem(player,hand);
        return inResult == InteractionResult.CONSUME || outResult == InteractionResult.CONSUME ? InteractionResult.CONSUME : InteractionResult.SUCCESS;
    }

    @Override
    public void setChanged() {
        super.setChanged();
        newTaskAvailable();
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (!inputFluidTank.isEmpty()){
            CompoundTag inputTag = new CompoundTag();
            tag.put("inputFluid", inputTag);
            inputFluidTank.getFluid().writeToNBT(inputTag);
        }
        if (!outputFluidTank.isEmpty()){
            CompoundTag outputTag = new CompoundTag();
            tag.put("outputFluid", outputTag);
            inputFluidTank.getFluid().writeToNBT(outputTag);
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("inputFluid"))
            inputFluidTank.setFluid(FluidStack.loadFluidStackFromNBT(tag.getCompound("inputFluid")), true);
        if (tag.contains("outputFluid"))
            outputFluidTank.setFluid(FluidStack.loadFluidStackFromNBT(tag.getCompound("outputFluid")), true);
    }

    @Override
    protected PoweredCraftingTask<VatRecipe, VatContainer> createTask(@Nullable VatRecipe recipe) {
        return new PoweredCraftingTask<>(this, container, outputFluidTank, recipe) {
            @Override
            protected void takeInputs(VatRecipe recipe) {
                MachineInventory inv = getInventory();
                ItemStack leftItemStack = LEFT_SLOT.getItemStack(inv);
                ItemStack rightItemStack = RIGHT_SLOT.getItemStack(inv);
                container.getInputTank().drain(recipe.calcFluidConsumptionForItemCombo(leftItemStack.getItem(), rightItemStack.getItem()), IFluidHandler.FluidAction.EXECUTE, true);
                leftItemStack.shrink(1);
                rightItemStack.shrink(1);
            }
        };
    }
    public class VatContainer extends RecipeWrapper {
        private final MachineFluidTank inputTank;
        public VatContainer(IItemHandlerModifiable inv, MachineFluidTank inputTank) {
            super(inv);
            this.inputTank = inputTank;
        }
        public MachineFluidTank getInputTank() {
            return inputTank;
        }
    }
}
