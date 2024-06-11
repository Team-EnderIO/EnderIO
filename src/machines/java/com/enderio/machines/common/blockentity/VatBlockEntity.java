package com.enderio.machines.common.blockentity;

import com.enderio.EnderIO;
import com.enderio.core.common.network.NetworkDataSlot;
import com.enderio.core.common.recipes.OutputStack;
import com.enderio.machines.common.attachment.FluidTankUser;
import com.enderio.machines.common.blockentity.base.MachineBlockEntity;
import com.enderio.machines.common.blockentity.task.CraftingMachineTask;
import com.enderio.machines.common.blockentity.task.host.CraftingMachineTaskHost;
import com.enderio.machines.common.init.MachineBlockEntities;
import com.enderio.machines.common.init.MachineRecipes;
import com.enderio.machines.common.io.fluid.FluidItemInteractive;
import com.enderio.machines.common.io.fluid.MachineFluidHandler;
import com.enderio.machines.common.io.fluid.MachineFluidTank;
import com.enderio.machines.common.io.fluid.MachineTankLayout;
import com.enderio.machines.common.io.fluid.TankAccess;
import com.enderio.machines.common.io.item.MachineInventory;
import com.enderio.machines.common.io.item.MachineInventoryLayout;
import com.enderio.machines.common.io.item.MultiSlotAccess;
import com.enderio.machines.common.menu.VatMenu;
import com.enderio.machines.common.recipe.FermentingRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class VatBlockEntity extends MachineBlockEntity implements FluidTankUser, FluidItemInteractive {

    public static final int TANK_CAPACITY = 8 * FluidType.BUCKET_VOLUME;
    private static final TankAccess INPUT_TANK = new TankAccess();
    private static final TankAccess OUTPUT_TANK = new TankAccess();
    public static final MultiSlotAccess REAGENTS = new MultiSlotAccess();
    private static final ResourceLocation EMPTY = EnderIO.loc("");

    private final MachineFluidHandler fluidHandler;
    private final CraftingMachineTaskHost<FermentingRecipe, FermentingRecipe.Container> craftingTaskHost;

    private ResourceLocation recipeId;

    public VatBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(MachineBlockEntities.VAT.get(), worldPosition, blockState);
        fluidHandler = createFluidHandler();

        // Sync fluid_stacks and active recipe.
        addDataSlot(NetworkDataSlot.FLUID_STACK.create(() -> INPUT_TANK.getFluid(this), stack -> INPUT_TANK.setFluid(this, stack)));
        addDataSlot(NetworkDataSlot.FLUID_STACK.create(() -> OUTPUT_TANK.getFluid(this), stack -> OUTPUT_TANK.setFluid(this, stack)));

        addDataSlot(NetworkDataSlot.RESOURCE_LOCATION.create(this::getRecipeId, this::setRecipeId));

        craftingTaskHost = new CraftingMachineTaskHost<>(this, () -> true, MachineRecipes.VAT_FERMENTING.type().get(),
            new FermentingRecipe.Container(getInventoryNN(), getInputTank()), this::createTask);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new VatMenu(this, playerInventory, containerId);
    }

    @Override
    public void serverTick() {
        super.serverTick();

        if (canAct()) {
            craftingTaskHost.tick();
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        craftingTaskHost.onLevelReady();
    }

    @Override
    public ItemInteractionResult onBlockEntityUsed(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack stack = player.getItemInHand(hand);
        if (!stack.isEmpty()) {
            if (handleFluidItemInteraction(player, hand, stack, this, INPUT_TANK) || handleFluidItemInteraction(player, hand, stack, this, OUTPUT_TANK)) {
                player.getInventory().setChanged();
                return ItemInteractionResult.sidedSuccess(level.isClientSide());
            }
        }
        return super.onBlockEntityUsed(state, level, pos, player, hand, hit);
    }

    protected VatBlockEntity.VatCraftingMachineTask createTask(Level level, FermentingRecipe.Container container,
        @Nullable RecipeHolder<FermentingRecipe> recipe) {
        return new VatBlockEntity.VatCraftingMachineTask(level, getInventoryNN(), getFluidHandler(), container, recipe);
    }

    @Override
    public @Nullable MachineInventoryLayout getInventoryLayout() {
        return MachineInventoryLayout.builder().inputSlot(2).slotAccess(REAGENTS).build();
    }

    @Override
    protected void onInventoryContentsChanged(int slot) {
        super.onInventoryContentsChanged(slot);
        craftingTaskHost.newTaskAvailable();
    }

    @Override
    public MachineTankLayout getTankLayout() {
        return MachineTankLayout
            .builder()
            .tank(INPUT_TANK, TANK_CAPACITY, true, false, (stack) -> true)
            .tank(OUTPUT_TANK, TANK_CAPACITY, false, true, (stack) -> true)
            .build();
    }

    @Override
    public MachineFluidHandler getFluidHandler() {
        return fluidHandler;
    }

    @Override
    public MachineFluidHandler createFluidHandler() {
        return new MachineFluidHandler(this, getTankLayout()) {
            @Override
            protected void onContentsChanged(int slot) {
                super.onContentsChanged(slot);
                craftingTaskHost.newTaskAvailable();
                setChanged();
            }
        };
    }

    public MachineFluidTank getInputTank() {
        return INPUT_TANK.getTank(this);
    }

    public MachineFluidTank getOutputTank() {
        return OUTPUT_TANK.getTank(this);
    }

    public float getProgress() {
        return craftingTaskHost.getProgress();
    }

    public ResourceLocation getRecipeId() {
        if (level.isClientSide()) {
            return recipeId;
        }

        if (craftingTaskHost.getCurrentTask() != null) {
            ResourceLocation id = craftingTaskHost.getCurrentTask().getRecipeId();
            return id != null ? id : EMPTY;
        }
        return EMPTY;
    }

    public void setRecipeId(ResourceLocation recipeId) {
        this.recipeId = recipeId;
    }

    public CraftingMachineTaskHost<FermentingRecipe, FermentingRecipe.Container> getCraftingHost() {
        return craftingTaskHost;
    }

    protected static class VatCraftingMachineTask extends CraftingMachineTask<FermentingRecipe, FermentingRecipe.Container> {

        public VatCraftingMachineTask(@NotNull Level level, MachineInventory inventory, MachineFluidHandler fluidHandler, FermentingRecipe.Container container,
            @Nullable RecipeHolder<FermentingRecipe> recipe) {
            super(level, inventory, fluidHandler, container, recipe);
        }

        @Override
        protected void consumeInputs(FermentingRecipe recipe) {
            REAGENTS.get(0).getItemStack(inventory).shrink(1);
            REAGENTS.get(1).getItemStack(inventory).shrink(1);

            INPUT_TANK.getTank(fluidHandler).drain(recipe.input().amount(), IFluidHandler.FluidAction.EXECUTE);
        }

        @Override
        protected boolean placeOutputs(List<OutputStack> outputs, boolean simulate) {
            var action = simulate ? IFluidHandler.FluidAction.SIMULATE : IFluidHandler.FluidAction.EXECUTE;
            FluidStack output = outputs.getFirst().getFluid();
            int filled = OUTPUT_TANK.getTank(fluidHandler).fill(output, action);
            return filled == output.getAmount();
        }

        @Override
        protected int makeProgress(int remainingProgress) {
            return 1; // do nothing. VAT doesn't consume power
        }

        @Override
        protected int getProgressRequired(FermentingRecipe recipe) {
            return recipe.ticks();
        }

    }

    @Override
    public void saveAdditional(CompoundTag pTag, HolderLookup.Provider lookupProvider) {
        super.saveAdditional(pTag, lookupProvider);
        saveTank(lookupProvider, pTag);
        craftingTaskHost.save(lookupProvider, pTag);
    }

    @Override
    public void loadAdditional(CompoundTag pTag, HolderLookup.Provider lookupProvider) {
        super.loadAdditional(pTag, lookupProvider);
        loadTank(lookupProvider, pTag);
        craftingTaskHost.load(lookupProvider, pTag);
    }
}
