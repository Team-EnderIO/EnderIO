package com.enderio.machines.common.blockentity;

import com.enderio.api.capacitor.CapacitorKey;
import com.enderio.api.machines.recipes.IAlloySmeltingRecipe;
import com.enderio.api.recipe.CountedIngredient;
import com.enderio.machines.common.MachineTier;
import com.enderio.machines.common.blockentity.base.PoweredTaskMachineEntity;
import com.enderio.machines.common.blockentity.task.PoweredCraftingTask;
import com.enderio.machines.common.init.MachineCapacitorKeys;
import com.enderio.machines.common.init.MachineRecipes;
import com.enderio.machines.common.io.item.MachineInventory;
import com.enderio.machines.common.io.item.MachineInventoryLayout;
import com.enderio.machines.common.menu.AlloySmelterMenu;
import com.enderio.machines.common.recipe.AlloySmeltingRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class NewAlloySmelterBlockEntity extends PoweredTaskMachineEntity<PoweredCraftingTask<IAlloySmeltingRecipe, NewAlloySmelterBlockEntity.AlloySmelterContainer>> {
    public static class Standard extends NewAlloySmelterBlockEntity {

        public Standard(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
            super(AlloySmelterMode.ALL,
                MachineCapacitorKeys.ALLOY_SMELTER_ENERGY_CAPACITY.get(),
                MachineCapacitorKeys.ALLOY_SMELTER_ENERGY_TRANSFER.get(),
                MachineCapacitorKeys.ALLOY_SMELTER_ENERGY_CONSUME.get(),
                pType, pWorldPosition, pBlockState);
        }

        @Override
        public MachineTier getTier() {
            return MachineTier.STANDARD;
        }
    }

    private final AlloySmelterMode defaultMode;
    private AlloySmelterMode mode;
    private boolean inventoryChanged = true;

    private final AlloySmelterContainer container;

    public NewAlloySmelterBlockEntity(AlloySmelterMode mode, CapacitorKey capacityKey, CapacitorKey transferKey, CapacitorKey energyUseKey,
        BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
        super(capacityKey, transferKey, energyUseKey, pType, pWorldPosition, pBlockState);

        this.defaultMode = mode;
        this.mode = mode;

        this.container = new AlloySmelterContainer(getInventory());
    }

    public AlloySmelterMode getMode() {
        // Lock to default mode if this is a simple machine.
        return getTier() == MachineTier.SIMPLE ? defaultMode : mode;
    }

    public void setMode(AlloySmelterMode mode) {
        this.mode = mode;
    }

    @Override
    public MachineInventoryLayout getInventoryLayout() {
        // Setup item slots
        return MachineInventoryLayout.builder(getTier() != MachineTier.SIMPLE)
            .inputSlot(3, this::acceptSlotInput)
            .outputSlot()
            .build();
    }

    private boolean acceptSlotInput(int slot, ItemStack stack) {
        // Ensure we don't break automation by inserting items that'll break the current recipe.
        PoweredCraftingTask currentTask = getCurrentTask();
        if (currentTask != null) {
            MachineInventory inventory = getInventory();
            ItemStack currentContents = inventory.getStackInSlot(slot);
            inventory.setStackInSlot(slot, stack);

            boolean accept = currentTask.getRecipe().matches(container, level);

            inventory.setStackInSlot(slot, currentContents);
            return accept;
        }
        return true;
    }

    @Override
    protected void onInventoryContentsChanged(int slot) {
        inventoryChanged = true; // TODO: 28/05/2022 This kind of thing might be a good idea for a base crafter class?
        super.onInventoryContentsChanged(slot);
    }

    @Override
    protected boolean hasNextTask() {
        return inventoryChanged;
    }

    @Override
    protected @Nullable PoweredCraftingTask getNextTask() {
        if (level == null)
            return null;

        // If there's no items, don't waste tick time

        // Search for an alloy recipe.
        if (getMode().canAlloy()) {
            var task = level
                .getRecipeManager()
                .getRecipeFor(MachineRecipes.Types.ALLOY_SMELTING, container, level)
                .map(this::createTask)
                .orElse(null);

            if (task != null)
                return task;
        }

        // Search for a smelting recipe.
        if (getMode().canSmelt()) {
            var task = level
                .getRecipeManager()
                .getRecipeFor(RecipeType.SMELTING, getRecipeWrapper(), level)
                .map(recipe -> createTask(new WrappedSmeltingRecipe(recipe)))
                .orElse(null);

            if (task != null)
                return task;
        }

        return null;
    }

    @Override
    protected @Nullable PoweredCraftingTask<IAlloySmeltingRecipe, AlloySmelterContainer> loadTask(CompoundTag nbt) {
        PoweredCraftingTask<IAlloySmeltingRecipe, AlloySmelterContainer> task = createTask(null);
        task.deserializeNBT(nbt);
        return task;
    }

    private PoweredCraftingTask<IAlloySmeltingRecipe, AlloySmelterContainer> createTask(@Nullable IAlloySmeltingRecipe recipe) {
        return new PoweredCraftingTask<>(energyStorage, recipe, level, container) {

            @Override
            protected void takeInputs(IAlloySmeltingRecipe recipe) {
                if (recipe instanceof AlloySmeltingRecipe) {
                    // Track which ingredients have been consumed
                    MachineInventory inv = getInventory();
                    List<CountedIngredient> inputs = recipe.getInputs();
                    boolean[] consumed = new boolean[3];

                    // Iterate over the slots
                    for (int i = 0; i < 3; i++) {
                        ItemStack stack = inv.getStackInSlot(i);

                        // Iterate over the inputs
                        for (int j = 0; j < 3; j++) {

                            // If this ingredient has been matched already, continue
                            if (consumed[j])
                                continue;

                            if (j < inputs.size()) {
                                // If we expect an input, test we have a match for it.
                                CountedIngredient input = inputs.get(j);

                                if (input.test(stack)) {
                                    consumed[j] = true;
                                    stack.shrink(input.count());
                                }
                            } else if (stack.isEmpty()) {
                                // If we don't expect an input, make sure we have a blank for it.
                                consumed[j] = true;
                            }
                        }
                    }

                    // Only accepted *1* times inputs.
                    container.inputsTaken = 1;
                } else if (recipe instanceof WrappedSmeltingRecipe) {
                    MachineInventory inv = getInventory();
                    CountedIngredient input = recipe.getInputs().get(0);

                    // Iterate over the slots
                    int consumeCount = 0;
                    for (int i = 0; i < 3; i++) {
                        ItemStack stack = inv.getStackInSlot(i);

                        if (input.test(stack)) {
                            stack.shrink(input.count());
                            consumeCount++;
                        }
                    }

                    container.inputsTaken = consumeCount;
                }
            }

            @Override
            protected boolean takeOutputs(IAlloySmeltingRecipe recipe, AlloySmelterContainer container, boolean simulate) {
                ItemStack result = recipe.assemble(container);
                MachineInventory inv = getInventory();
                if (inv.insertItem(3, result, true).isEmpty()) {
                    if (!simulate)
                        inv.insertItem(3, result, false);
                    return true;
                }

                return false;
            }

            @Override
            protected @Nullable IAlloySmeltingRecipe loadRecipe(CompoundTag nbt) {
                ResourceLocation id = new ResourceLocation(nbt.getString("id"));
                return level.getRecipeManager().byKey(id).map(recipe -> {
                    if (recipe.getType() == MachineRecipes.Types.ALLOY_SMELTING) {
                        return (AlloySmeltingRecipe) recipe;
                    } else if (recipe.getType() == RecipeType.SMELTING) {
                        return new WrappedSmeltingRecipe((SmeltingRecipe) recipe);
                    }
                    return null;
                }).orElse(null);
            }
        };
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new AlloySmelterMenu(this, inventory, containerId);
    }

    @Override
    public void saveAdditional(CompoundTag pTag) {
        pTag.putInt("inputs_taken", container.inputsTaken);
        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        container.inputsTaken = pTag.getInt("inputs_taken");
        super.load(pTag);
    }

    public class AlloySmelterContainer extends RecipeWrapper {

        private int inputsTaken;

        public AlloySmelterContainer(IItemHandlerModifiable inv) {
            super(inv);
        }

        public int getInputsTaken() {
            return inputsTaken;
        }

    }

    private static class WrappedSmeltingRecipe implements IAlloySmeltingRecipe {
        private final SmeltingRecipe recipe;

        private WrappedSmeltingRecipe(SmeltingRecipe recipe) {
            this.recipe = recipe;
        }

        @Override
        public List<CountedIngredient> getInputs() {
            return List.of(CountedIngredient.of(recipe.getIngredients().get(0).getItems()));
        }

        @Override
        public float getExperience() {
            return recipe.getExperience();
        }

        public static final int RF_PER_ITEM = ForgeHooks.getBurnTime(new ItemStack(Items.COAL, 1), RecipeType.SMELTING) * 10 / 8;

        @Override
        public int getEnergyCost(AlloySmelterContainer container) {
            return container.inputsTaken * RF_PER_ITEM;
        }

        @Override
        public boolean matches(AlloySmelterContainer container, Level level) {
            return recipe.matches(container, level);
        }

        @Override
        public ItemStack assemble(AlloySmelterContainer container) {
            ItemStack result = recipe.assemble(container);
            result.setCount(result.getCount() * container.inputsTaken);
            return result;
        }

        @Override
        public ItemStack getResultItem() {
            return recipe.getResultItem();
        }

        @Override
        public ResourceLocation getId() {
            return recipe.getId();
        }

        @Override
        public RecipeSerializer<?> getSerializer() {
            return null;
        }

        @Override
        public RecipeType<?> getType() {
            return null;
        }
    }

}
