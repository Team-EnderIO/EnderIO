package com.enderio.machines.common.blocks.painting;

import com.enderio.base.api.capacitor.CapacitorModifier;
import com.enderio.base.api.capacitor.QuadraticScalable;
import com.enderio.base.api.io.energy.EnergyIOMode;
import com.enderio.base.common.init.EIOCriterions;
import com.enderio.base.common.init.EIODataComponents;
import com.enderio.base.common.paint.BlockPaintData;
import com.enderio.base.common.paint.block.PaintedBlock;
import com.enderio.core.common.recipes.OutputStack;
import com.enderio.machines.common.blocks.base.blockentity.PoweredMachineBlockEntity;
import com.enderio.machines.common.blocks.base.blockentity.flags.CapacitorSupport;
import com.enderio.machines.common.blocks.base.inventory.MachineInventoryLayout;
import com.enderio.machines.common.blocks.base.inventory.SingleSlotAccess;
import com.enderio.machines.common.blocks.base.task.PoweredCraftingMachineTask;
import com.enderio.machines.common.blocks.base.task.host.CraftingMachineTaskHost;
import com.enderio.machines.common.config.MachinesConfig;
import com.enderio.machines.common.init.MachineBlockEntities;
import com.enderio.machines.common.init.MachineRecipes;
import com.enderio.machines.common.recipe.RecipeCaches;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.Shapes;
import org.jetbrains.annotations.Nullable;

public class PaintingMachineBlockEntity extends PoweredMachineBlockEntity {

    public static final SingleSlotAccess INPUT = new SingleSlotAccess();
    public static final SingleSlotAccess PAINT = new SingleSlotAccess();
    public static final SingleSlotAccess OUTPUT = new SingleSlotAccess();
    public static final QuadraticScalable CAPACITY = new QuadraticScalable(CapacitorModifier.ENERGY_CAPACITY,
            MachinesConfig.COMMON.ENERGY.PAINTING_MACHINE_CAPACITY);
    public static final QuadraticScalable USAGE = new QuadraticScalable(CapacitorModifier.ENERGY_USE,
            MachinesConfig.COMMON.ENERGY.PAINTING_MACHINE_USAGE);

    private final AABB area;

    private final CraftingMachineTaskHost<PaintingRecipe, PaintingRecipe.Input> craftingTaskHost;

    public PaintingMachineBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(MachineBlockEntities.PAINTING_MACHINE.get(), worldPosition, blockState, true, CapacitorSupport.REQUIRED,
                EnergyIOMode.Input, CAPACITY, USAGE);

        area = AABB.ofSize(worldPosition.getCenter(), 10, 10, 10);

        craftingTaskHost = new CraftingMachineTaskHost<>(this, this::hasEnergy, MachineRecipes.PAINTING.type().get(),
                this::createTask, this::createRecipeInput);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
        return new PaintingMachineMenu(pInventory, pContainerId, this);
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

    // region Inventory

    @Override
    public MachineInventoryLayout createInventoryLayout() {
        return MachineInventoryLayout.builder()
                .capacitor()
                .inputSlot(this::isValidInput)
                .slotAccess(INPUT)
                .inputSlot(this::isValidPaint)
                .slotAccess(PAINT)
                .outputSlot()
                .slotAccess(OUTPUT)
                .build();
    }

    private boolean isValidInput(int index, ItemStack stack) {
        return RecipeCaches.PAINTING.hasRecipe(List.of(stack));
    }

    private boolean isValidPaint(int index, ItemStack stack) {
        if (stack.getItem() instanceof BlockItem blockItem) {
            Block block = blockItem.getBlock();
            if (block instanceof PaintedBlock) {
                return false;
            }

            return block.defaultBlockState().getOcclusionShape(level, getBlockPos()) == Shapes.block();
        }
        return false;
    }

    @Override
    protected void onInventoryContentsChanged(int slot) {
        super.onInventoryContentsChanged(slot);
        craftingTaskHost.newTaskAvailable();
    }

    private PaintingRecipe.Input createRecipeInput() {
        return new PaintingRecipe.Input(INPUT.getItemStack(getInventory()), PAINT.getItemStack(getInventory()));
    }

    // endregion

    // region Crafting Task

    public float getCraftingProgress() {
        return craftingTaskHost.getProgress();
    }

    @Override
    public boolean isActive() {
        return canAct() && hasEnergy() && craftingTaskHost.hasTask();
    }

    protected PoweredCraftingMachineTask<PaintingRecipe, PaintingRecipe.Input> createTask(Level level,
            PaintingRecipe.Input recipeInput, @Nullable RecipeHolder<PaintingRecipe> recipe) {
        return new PoweredCraftingMachineTask<>(level, getInventory(), getEnergyStorage(), recipeInput, OUTPUT,
                recipe) {
            @Override
            protected void consumeInputs(PaintingRecipe recipe) {
                INPUT.getItemStack(getInventory()).shrink(1);
            }

            @Override
            protected boolean placeOutputs(List<OutputStack> outputs, boolean simulate) {
                if (getLevel() == null || getLevel().isClientSide) {
                    return super.placeOutputs(outputs, simulate);
                }

                Optional<BlockPaintData> s = outputs.stream()
                        .findFirst()
                        .map(OutputStack::getItem)
                        .flatMap(item -> Optional.ofNullable(item.get(EIODataComponents.BLOCK_PAINT)));

                s.ifPresent(paintData -> {
                    for (Player player : getLevel().players()) {
                        if (player instanceof ServerPlayer serverPlayer
                                && area.contains(player.getX(), player.getY(), player.getZ())) {
                            EIOCriterions.PAINTING_TRIGGER.get().trigger(serverPlayer, paintData.paint());
                        }
                    }
                });

                return super.placeOutputs(outputs, simulate);
            }
        };
    }

    // endregion

    // region Serialization

    @Override
    public void saveAdditional(CompoundTag pTag, HolderLookup.Provider lookupProvider) {
        super.saveAdditional(pTag, lookupProvider);
        craftingTaskHost.save(lookupProvider, pTag);
    }

    @Override
    public void loadAdditional(CompoundTag pTag, HolderLookup.Provider lookupProvider) {
        super.loadAdditional(pTag, lookupProvider);
        craftingTaskHost.load(lookupProvider, pTag);
    }

    // endregion
}
