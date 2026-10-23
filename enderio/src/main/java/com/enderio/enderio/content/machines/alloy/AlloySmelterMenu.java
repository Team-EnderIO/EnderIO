package com.enderio.enderio.content.machines.alloy;

import com.enderio.core.common.network.menu.EnumSyncSlot;
import com.enderio.core.common.network.menu.FloatSyncSlot;
import com.enderio.enderio.content.enchanter.EnchanterMenu;
import com.enderio.enderio.content.enchanter.EnchanterRecipe;
import com.enderio.enderio.foundation.menu.MachineSlot;
import com.enderio.enderio.foundation.menu.PoweredMachineMenu;
import com.enderio.enderio.foundation.task.CraftingMachineTask;
import com.enderio.enderio.init.EIOBlockEntities;
import com.enderio.enderio.init.EIOMenus;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Prediction;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class AlloySmelterMenu extends PoweredMachineMenu<AlloySmelterBlockEntity> {
    public static final int INPUTS_INDEX = 1;
    public static final int INPUT_COUNT = 3;
    public static final int LAST_INDEX = 4;

    private final FloatSyncSlot progressSlot;
    private final EnumSyncSlot<AlloySmelterMode> modeSlot;

    // Server constructor
    public AlloySmelterMenu(int containerId, Inventory inventory, AlloySmelterBlockEntity blockEntity) {
        super(EIOMenus.ALLOY_SMELTER.get(), containerId, inventory, blockEntity);
        addSlots();

        progressSlot = addSyncSlot(FloatSyncSlot.readOnly(blockEntity::getCraftingProgress));
        modeSlot = addUpdatableSyncSlot(
                EnumSyncSlot.simple(AlloySmelterMode.class, blockEntity::getMode, blockEntity::setMode));
    }

    // Client constructor
    public AlloySmelterMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
        super(EIOMenus.ALLOY_SMELTER.get(), containerId, playerInventory, buf,
            EIOBlockEntities.ALLOY_SMELTER.get());
        addSlots();

        progressSlot = addSyncSlot(FloatSyncSlot.standalone());
        modeSlot = addUpdatableSyncSlot(EnumSyncSlot.standalone(AlloySmelterMode.class));
    }

    private void addSlots() {
        // Capacitor slot
        addCapacitorSlot(8, 89);

        addSlot(new MachineSlot(getMachineInventory(), AlloySmelterBlockEntity.INPUTS.slot(0), 55, 38));
        addSlot(new MachineSlot(getMachineInventory(), AlloySmelterBlockEntity.INPUTS.slot(1), 80, 28));
        addSlot(new MachineSlot(getMachineInventory(), AlloySmelterBlockEntity.INPUTS.slot(2), 104, 38));
        addSlot(new MachineSlot(getMachineInventory(), AlloySmelterBlockEntity.OUTPUT, 80, 79));

        addPlayerInventorySlots(8, 126);
    }

    public float getCraftingProgress() {
        return progressSlot.get();
    }

    public AlloySmelterMode getMode() {
        return modeSlot.get();
    }

    public void setMode(AlloySmelterMode mode) {
        modeSlot.set(mode);
        updateSlot(modeSlot);
    }

    @Override
    public RecipeBookType getRecipeBookType() {
        return RecipeBookType.valueOf("ENDERIO_ALLOY_SMELTING");
    }

    @Override
    public void fillCraftSlotsStackedContents(StackedItemContents stackedContents) {
        stackedContents.accountSimpleStack(getSlot(1).getItem());
        stackedContents.accountSimpleStack(getSlot(2).getItem());
        stackedContents.accountSimpleStack(getSlot(3).getItem());
    }

    @Override
    public PostPlaceAction handlePlacement(boolean useMaxItems, boolean allowDroppingItemsToClear, RecipeHolder<?> recipe, ServerLevel level,
        Inventory inventory) {
        return handlePlacement(useMaxItems, allowDroppingItemsToClear, recipe, level, inventory, INPUTS_INDEX, LAST_INDEX);
    }

    @Nullable
    protected Recipe<?> getRecipe() {
        var currentTask = this.getBlockEntity().craftingTaskHost.getCurrentTask();
        if (currentTask == null) {
            return null;
        }
        return currentTask.getRecipe();
    }
}
