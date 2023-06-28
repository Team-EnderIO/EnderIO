package com.enderio.machines.common.blockentity;

import com.enderio.api.capacitor.CapacitorModifier;
import com.enderio.api.capacitor.QuadraticScalable;
import com.enderio.base.common.advancement.PaintingTrigger;
import com.enderio.base.common.block.painted.IPaintedBlock;
import com.enderio.core.common.recipes.OutputStack;
import com.enderio.machines.common.blockentity.base.PoweredCraftingMachine;
import com.enderio.machines.common.blockentity.task.PoweredCraftingTask;
import com.enderio.machines.common.init.MachineRecipes;
import com.enderio.machines.common.io.item.MachineInventory;
import com.enderio.machines.common.io.item.MachineInventoryLayout;
import com.enderio.machines.common.io.item.SingleSlotAccess;
import com.enderio.machines.common.menu.PaintingMachineMenu;
import com.enderio.machines.common.recipe.PaintingRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class PaintingMachineBlockEntity extends PoweredCraftingMachine<PaintingRecipe, PaintingRecipe.Container> {

    public static final SingleSlotAccess INPUT = new SingleSlotAccess();
    public static final SingleSlotAccess PAINT = new SingleSlotAccess();
    public static final SingleSlotAccess OUTPUT = new SingleSlotAccess();
    private final PaintingRecipe.Container container;
    public static final QuadraticScalable CAPACITY = new QuadraticScalable(CapacitorModifier.ENERGY_CAPACITY, () -> 100000f);
    public static final QuadraticScalable USAGE = new QuadraticScalable(CapacitorModifier.ENERGY_USE, () -> 30f);

    private final AABB area;

    public PaintingMachineBlockEntity(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState) {
        super(MachineRecipes.PAINTING.type().get(), CAPACITY, USAGE, type, worldPosition, blockState);

        container = new PaintingRecipe.Container(getInventory(), this);
        area = AABB.ofSize(worldPosition.getCenter(), 10, 10, 10);
    }

    @Override
    protected PoweredCraftingTask<PaintingRecipe, PaintingRecipe.Container> createTask(@Nullable PaintingRecipe recipe) {
        return new PoweredCraftingTask<>(this, getContainer(), OUTPUT, recipe) {
            @Override
            protected void takeInputs(PaintingRecipe recipe) {
                MachineInventory inv = getInventory();
                INPUT.getItemStack(inv).shrink(1);
            }

            @Override
            protected boolean placeOutputs(List<OutputStack> outputs, boolean simulate) {
                if (level == null || level.isClientSide)
                    return super.placeOutputs(outputs, simulate);

                Optional<String> s = outputs
                    .stream()
                    .findFirst()
                    .map(OutputStack::getItem)
                    .flatMap(item -> Optional.ofNullable(item.getTag()))
                    .filter(nbt -> nbt.contains("BlockEntityTag", Tag.TAG_COMPOUND))
                    .map(nbt -> nbt.getCompound("BlockEntityTag"))
                    .filter(nbt -> nbt.contains("paint", Tag.TAG_STRING))
                    .map(nbt -> nbt.getString("paint"));
                if (s.isPresent()) {
                    Block paint = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(s.get()));
                    for(Player player : level.players()) {
                        if (player instanceof ServerPlayer serverPlayer && area.contains(player.getX(), player.getY(), player.getZ())) {
                            PaintingTrigger.PAINTING_TRIGGER.trigger(serverPlayer, paint);
                        }
                    }
                }
                return super.placeOutputs(outputs, simulate);
            }
        };
    }


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
        if (level == null)
            return false;
        return level.getRecipeManager().getAllRecipesFor(MachineRecipes.PAINTING.type().get()).stream().map(PaintingRecipe::getInput).anyMatch(ingredient -> ingredient.test(stack));
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
    protected PaintingRecipe.Container getContainer() {
        return container;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
        return new PaintingMachineMenu(this, pInventory, pContainerId);
    }
}
