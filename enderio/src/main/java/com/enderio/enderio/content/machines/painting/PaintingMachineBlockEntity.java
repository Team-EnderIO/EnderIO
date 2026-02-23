package com.enderio.enderio.content.machines.painting;

import com.enderio.core.common.recipes.OutputStack;
import com.enderio.enderio.api.capacitor.CapacitorModifier;
import com.enderio.enderio.api.capacitor.QuadraticScalable;
import com.enderio.enderio.api.io.energy.EnergyIOMode;
import com.enderio.enderio.config.machines.MachinesConfig;
import com.enderio.enderio.content.paint.BlockPaintData;
import com.enderio.enderio.content.paint.block.PaintedBlock;
import com.enderio.enderio.foundation.MachineNBTKeys;
import com.enderio.enderio.foundation.block.entity.PoweredMachineBlockEntity;
import com.enderio.enderio.foundation.block.entity.flags.CapacitorSupport;
import com.enderio.enderio.foundation.inventory.MachineInventoryLayout;
import com.enderio.enderio.foundation.inventory.SingleSlotAccess;
import com.enderio.enderio.foundation.recipe.MachineRecipeCaches;
import com.enderio.enderio.foundation.task.PoweredCraftingMachineTask;
import com.enderio.enderio.foundation.task.host.CraftingMachineTaskHost;
import com.enderio.enderio.init.EIOBlockEntities;
import com.enderio.enderio.init.EIOCriterions;
import com.enderio.enderio.init.EIODataComponents;
import com.enderio.enderio.init.EIORecipeTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.EmptyBlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Optional;

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
        super(EIOBlockEntities.PAINTING_MACHINE.get(), worldPosition, blockState, true, CapacitorSupport.REQUIRED,
                EnergyIOMode.Input, CAPACITY, USAGE);

        area = AABB.ofSize(worldPosition.getCenter(), 10, 10, 10);

        craftingTaskHost = new CraftingMachineTaskHost<>(this, this::hasEnergy, EIORecipeTypes.PAINTING.get(),
                this::createTask, this::createRecipeInput);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new PaintingMachineMenu(inventory, containerId, this);
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

    private boolean isValidInput(int index, ItemResource stack) {
        return MachineRecipeCaches.PAINTING.hasRecipe(List.of(stack.toStack()));
    }

    private boolean isValidPaint(int index, ItemResource stack) {
        if (stack.getItem() instanceof BlockItem blockItem) {
            Block block = blockItem.getBlock();
            if (block instanceof PaintedBlock) {
                return false;
            }

            return block.defaultBlockState().isCollisionShapeFullBlock(EmptyBlockAndTintGetter.INSTANCE, BlockPos.ZERO);
        }
        return false;
    }

    @Override
    protected void onInventoryContentsChanged(int slot) {
        super.onInventoryContentsChanged(slot);
        craftingTaskHost.newTaskAvailable();
    }

    private PaintingRecipe.Input createRecipeInput() {
        return new PaintingRecipe.Input(INPUT.getStack(getInventory()), PAINT.getStack(getInventory()));
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
                INPUT.getStack(getInventory()).shrink(1);
            }

            @Override
            protected boolean placeOutputs(List<OutputStack> outputs, boolean simulate) {
                if (getLevel() == null || getLevel().isClientSide()) {
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
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putChild(MachineNBTKeys.CRAFTING_TASK, craftingTaskHost);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        var task = input.child(MachineNBTKeys.CRAFTING_TASK);
        task.ifPresent(craftingTaskHost::deserialize);
    }

    // endregion
}
