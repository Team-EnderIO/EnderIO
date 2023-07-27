package com.enderio.machines.common.blockentity;

import com.enderio.api.capacitor.CapacitorModifier;
import com.enderio.api.capacitor.QuadraticScalable;
import com.enderio.api.io.energy.EnergyIOMode;
import com.enderio.base.EIONBTKeys;
import com.enderio.base.common.advancement.PaintingTrigger;
import com.enderio.base.common.block.painted.IPaintedBlock;
import com.enderio.core.common.recipes.OutputStack;
import com.enderio.machines.common.blockentity.base.PoweredMachineBlockEntity;
import com.enderio.machines.common.blockentity.task.PoweredCraftingMachineTask;
import com.enderio.machines.common.blockentity.task.host.CraftingMachineTaskHost;
import com.enderio.machines.common.config.MachinesConfig;
import com.enderio.machines.common.init.MachineRecipes;
import com.enderio.machines.common.io.item.MachineInventoryLayout;
import com.enderio.machines.common.io.item.SingleSlotAccess;
import com.enderio.machines.common.menu.PaintingMachineMenu;
import com.enderio.machines.common.recipe.PaintingRecipe;
import com.enderio.machines.common.recipe.RecipeCaches;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class PaintingMachineBlockEntity extends PoweredMachineBlockEntity {

    public static final SingleSlotAccess INPUT = new SingleSlotAccess();
    public static final SingleSlotAccess PAINT = new SingleSlotAccess();
    public static final SingleSlotAccess OUTPUT = new SingleSlotAccess();
    public static final QuadraticScalable CAPACITY = new QuadraticScalable(CapacitorModifier.ENERGY_CAPACITY, MachinesConfig.COMMON.ENERGY.PAINTING_MACHINE_CAPACITY);
    public static final QuadraticScalable USAGE = new QuadraticScalable(CapacitorModifier.ENERGY_USE, MachinesConfig.COMMON.ENERGY.PAINTING_MACHINE_USAGE);

    private final AABB area;

    private final CraftingMachineTaskHost<PaintingRecipe, RecipeWrapper> craftingTaskHost;

    public PaintingMachineBlockEntity(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState) {
        super(EnergyIOMode.Input, CAPACITY, USAGE, type, worldPosition, blockState);

        area = AABB.ofSize(worldPosition.getCenter(), 10, 10, 10);

        craftingTaskHost = new CraftingMachineTaskHost<>(this, this::hasEnergy, MachineRecipes.PAINTING.type().get(),
            new RecipeWrapper(getInventoryNN()), this::createTask);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
        return new PaintingMachineMenu(this, pInventory, pContainerId);
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
    public MachineInventoryLayout getInventoryLayout() {
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
            if (block instanceof IPaintedBlock)
                return false;
            return block.defaultBlockState().getOcclusionShape(level, getBlockPos()) == Shapes.block();
        }
        return false;
    }

    @Override
    protected void onInventoryContentsChanged(int slot) {
        super.onInventoryContentsChanged(slot);
        craftingTaskHost.newTaskAvailable();
    }

    // endregion

    // region Crafting Task

    public float getCraftingProgress() {
        return craftingTaskHost.getProgress();
    }

    @Override
    protected boolean isActive() {
        return canAct() && hasEnergy() && craftingTaskHost.hasTask();
    }

    protected PoweredCraftingMachineTask<PaintingRecipe, RecipeWrapper> createTask(Level level, RecipeWrapper container, @Nullable PaintingRecipe recipe) {
        return new PoweredCraftingMachineTask<>(level, getInventoryNN(), getEnergyStorage(), container, OUTPUT, recipe) {
            @Override
            protected void consumeInputs(PaintingRecipe recipe) {
                INPUT.getItemStack(getInventory()).shrink(1);
            }

            @Override
            protected boolean placeOutputs(List<OutputStack> outputs, boolean simulate) {
                if (getLevel() == null || getLevel().isClientSide)
                    return super.placeOutputs(outputs, simulate);

                Optional<String> s = outputs
                    .stream()
                    .findFirst()
                    .map(OutputStack::getItem)
                    .flatMap(item -> Optional.ofNullable(item.getTag()))
                    .filter(nbt -> nbt.contains(BlockItem.BLOCK_ENTITY_TAG, Tag.TAG_COMPOUND))
                    .map(nbt -> nbt.getCompound(BlockItem.BLOCK_ENTITY_TAG))
                    .filter(nbt -> nbt.contains(EIONBTKeys.PAINT, Tag.TAG_STRING))
                    .map(nbt -> nbt.getString(EIONBTKeys.PAINT));
                if (s.isPresent()) {
                    Block paint = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(s.get()));
                    for (Player player : getLevel().players()) {
                        if (player instanceof ServerPlayer serverPlayer && area.contains(player.getX(), player.getY(), player.getZ())) {
                            PaintingTrigger.PAINTING_TRIGGER.trigger(serverPlayer, paint);
                        }
                    }
                }
                return super.placeOutputs(outputs, simulate);
            }
        };
    }

    // endregion

    // region Serialization

    @Override
    public void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        craftingTaskHost.save(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        craftingTaskHost.load(pTag);
    }

    // endregion
}
