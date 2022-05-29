package com.enderio.machines.common.blockentity;

import com.enderio.api.capacitor.CapacitorKey;
import com.enderio.api.recipe.AlloySmeltingRecipe;
import com.enderio.api.recipe.DataGenSerializer;
import com.enderio.api.recipe.IMachineRecipe;
import com.enderio.machines.common.MachineTier;
import com.enderio.machines.common.blockentity.base.PoweredTaskMachineEntity;
import com.enderio.machines.common.blockentity.task.NewPoweredCraftingTask;
import com.enderio.machines.common.blockentity.task.PoweredCraftingTask;
import com.enderio.machines.common.init.MachineCapacitorKeys;
import com.enderio.machines.common.init.MachineRecipes;
import com.enderio.machines.common.io.item.MachineInventory;
import com.enderio.machines.common.io.item.MachineInventoryLayout;
import com.enderio.machines.common.menu.AlloySmelterMenu;
import mezz.jei.api.constants.RecipeTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeHooks;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public abstract class NewAlloySmelterBlockEntity extends PoweredTaskMachineEntity<NewPoweredCraftingTask> {
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

    public NewAlloySmelterBlockEntity(AlloySmelterMode mode, CapacitorKey capacityKey, CapacitorKey transferKey, CapacitorKey energyUseKey,
        BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
        super(capacityKey, transferKey, energyUseKey, pType, pWorldPosition, pBlockState);

        this.defaultMode = mode;
        this.mode = mode;
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
        return MachineInventoryLayout.builder()
            .addInputs(3, this::acceptSlotInput)
            .addOutput()
            .capacitor(() -> getTier() != MachineTier.SIMPLE)
            .build();
    }

    private boolean acceptSlotInput(int slot, ItemStack stack) {
        // Ensure we don't break automation by inserting items that'll break the current recipe.
        NewPoweredCraftingTask currentTask = getCurrentTask();
        if (currentTask != null) {
            MachineInventory inventory = getInventory();
            ItemStack currentContents = inventory.getStackInSlot(slot);
            inventory.setStackInSlot(slot, stack);

            boolean accept = currentTask.getRecipe().matches(getRecipeWrapper(), level);

            inventory.setStackInSlot(slot, currentContents);
            return accept;
        }
        return true;
    }

    @Override
    protected void onInventoryContentsChanged(int slot) {
        inventoryChanged = true; // TODO: 28/05/2022 This kind of thing might be a good idea for a base crafter class?
    }

    @Override
    protected boolean hasNextTask() {
        return inventoryChanged;
    }

    @Override
    protected @Nullable NewPoweredCraftingTask getNextTask() {
        if (level == null)
            return null;

        // If there's no items, don't waste tick time

        // Search for an alloy recipe.
        if (getMode().canAlloy()) {
            NewPoweredCraftingTask task = level
                .getRecipeManager()
                .getRecipeFor(MachineRecipes.Types.ALLOY_SMELTING, getRecipeWrapper(), level)
                .map(this::createTask)
                .orElse(null);

            if (task != null)
                return task;
        }

        // Search for a smelting recipe.
        if (getMode().canSmelt()) {
            NewPoweredCraftingTask task = level
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
    protected @Nullable NewPoweredCraftingTask loadTask(CompoundTag nbt) {
        NewPoweredCraftingTask task = createTask(null);
        task.deserializeNBT(nbt);
        return task;
    }

    private NewPoweredCraftingTask createTask(@Nullable AlloySmeltingRecipe recipe) {
        return new NewPoweredCraftingTask(energyStorage, recipe, level, getRecipeWrapper()) {
            @Override
            protected boolean takeOutputs(List<ItemStack> outputs) {
                // Alloy smelting recipes only have a single output
                ItemStack out = outputs.get(0);

                MachineInventory inv = getInventory();

                if (inv.forceInsertItem(3, out, true).isEmpty()) {
                    inv.forceInsertItem(3, out, false);
                    return true;
                }

                return false;
            }

            @Override
            protected CompoundTag serializeRecipe(CompoundTag tag, IMachineRecipe<?, Container> recipe) {
                if (recipe instanceof WrappedSmeltingRecipe wrappedSmeltingRecipe) {
                    tag.putInt("multiplier", wrappedSmeltingRecipe.multiplier);
                }
                return super.serializeRecipe(tag, recipe);
            }

            @Override
            protected @Nullable IMachineRecipe<?, Container> loadRecipe(CompoundTag nbt) {
                ResourceLocation id = new ResourceLocation(nbt.getString("id"));
                return level.getRecipeManager().byKey(id).map(recipe -> {
                    if (recipe.getType() == MachineRecipes.Types.ALLOY_SMELTING) {
                        return (AlloySmeltingRecipe) recipe;
                    } else if (recipe.getType() == RecipeType.SMELTING) {
                        int multiplier = nbt.getInt("multiplier");
                        return new WrappedSmeltingRecipe((SmeltingRecipe) recipe, multiplier);
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

    // This is gross but it means we can support smelting recipes too, so pog!
    public static class WrappedSmeltingRecipe extends AlloySmeltingRecipe {
        private final SmeltingRecipe recipe;
        private int multiplier;

        public WrappedSmeltingRecipe(SmeltingRecipe recipe) {
            this(recipe, 0);
        }

        public WrappedSmeltingRecipe(SmeltingRecipe recipe, int multiplier) {
            super(recipe.getId(), List.of(), null, 0, 0);
            this.recipe = recipe;
        }

        public int getMultiplier() {
            return multiplier;
        }

        @Override
        public boolean matches(Container container, Level level) {
            return recipe.matches(container, level);
        }

        // This will never be serialized so we don't bother..
        @Override
        public DataGenSerializer<AlloySmeltingRecipe, Container> getSerializer() {
            return null;
        }

        @Override
        public RecipeType<?> getType() {
            return RecipeType.SMELTING;
        }

        @Override
        public void consumeInputs(Container container) {
            // Consume inputs and claim a multiplier
            multiplier = 0;
            for (int i = 0; i < 3; i++) {
                ItemStack stack = container.getItem(i);
                if (!stack.isEmpty()) {
                    stack.shrink(1);
                    multiplier++;
                }
            }
        }

        @Override
        public List<ItemStack> craft(Container container) {
            ItemStack result = recipe.assemble(container);
            result.setCount(Math.min(result.getCount() * multiplier, 64));
            return List.of(result);
        }

        @Override
        public int getOutputCount(Container container) {
            return 1;
        }

        @Override
        public List<List<ItemStack>> getAllInputs() {
            return List.of(Arrays.stream(recipe.getIngredients().get(0).getItems()).toList());
        }

        @Override
        public List<ItemStack> getAllOutputs() {
            return List.of(recipe.getResultItem());
        }

        // TODO: Ensure this is in line with the stirling generator.
        // TODO: Someplace to keep constants like this common.
        public static final int RF_PER_ITEM = ForgeHooks.getBurnTime(new ItemStack(Items.COAL, 1), RecipeType.SMELTING) * 10 / 8;

        @Override
        public int getEnergyCost() {
            return RF_PER_ITEM * multiplier;
        }
    }
}
