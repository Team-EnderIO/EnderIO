package com.enderio.machines.common.blockentity;

import com.enderio.api.capacitor.CapacitorModifier;
import com.enderio.api.capacitor.QuadraticScalable;
import com.enderio.api.grindingball.IGrindingBallData;
import com.enderio.base.common.util.GrindingBallManager;
import com.enderio.core.common.sync.IntegerDataSlot;
import com.enderio.core.common.sync.ResourceLocationDataSlot;
import com.enderio.core.common.sync.SyncMode;
import com.enderio.machines.common.blockentity.base.PoweredCraftingMachine;
import com.enderio.machines.common.blockentity.task.PoweredCraftingTask;
import com.enderio.machines.common.init.MachineRecipes;
import com.enderio.machines.common.io.item.MachineInventory;
import com.enderio.machines.common.io.item.MachineInventoryLayout;
import com.enderio.machines.common.io.item.MultiSlotAccess;
import com.enderio.machines.common.io.item.SingleSlotAccess;
import com.enderio.machines.common.menu.SagMillMenu;
import com.enderio.machines.common.recipe.SagMillingRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class SagMillBlockEntity extends PoweredCraftingMachine<SagMillingRecipe, SagMillingRecipe.Container> {
    public static final QuadraticScalable CAPACITY = new QuadraticScalable(CapacitorModifier.ENERGY_CAPACITY, () -> 100000f);
    public static final QuadraticScalable TRANSFER = new QuadraticScalable(CapacitorModifier.ENERGY_TRANSFER, () -> 120f);
    public static final QuadraticScalable USAGE = new QuadraticScalable(CapacitorModifier.ENERGY_USE, () -> 30f);

    private IGrindingBallData grindingBallData = IGrindingBallData.IDENTITY;

    @Nullable
    private ResourceLocation pendingGrindingBallId;

    private int grindingBallDamage;

    private final SagMillingRecipe.Container container;

    private static final SingleSlotAccess inputSlotAccess = new SingleSlotAccess();
    private static final SingleSlotAccess grindingBallSlotAccess = new SingleSlotAccess();
    private static final MultiSlotAccess OUTPUT = new MultiSlotAccess();


    public SagMillBlockEntity(BlockEntityType<?> type, BlockPos worldPosition,
        BlockState blockState) {
        super(MachineRecipes.SAGMILLING.type().get(), CAPACITY, TRANSFER, USAGE, type, worldPosition, blockState);
        container = new SagMillingRecipe.Container(getInventory(), this::getGrindingBallData);

        addDataSlot(new IntegerDataSlot(() -> grindingBallDamage, dmg -> grindingBallDamage = dmg, SyncMode.GUI));
        addDataSlot(new ResourceLocationDataSlot(() -> grindingBallData.getId(), gId -> grindingBallData = GrindingBallManager.getData(gId), SyncMode.GUI));
    }

    @Nullable
    public IGrindingBallData getGrindingBallData() {
        return grindingBallData;
    }

    public void setGrindingBallData(IGrindingBallData data) {
        grindingBallDamage = 0;
        grindingBallData = data;
    }

    public float getGrindingBallDamage() {
        if (grindingBallData.getDurability() <= 0)
            return 0.0f;
        return 1.0f - (grindingBallDamage / (float) grindingBallData.getDurability());
    }

    @Override
    public void onLoad() {
        super.onLoad();

        // Load a pending grinding ball.
        if (pendingGrindingBallId != null) {
            grindingBallData = GrindingBallManager.getData(pendingGrindingBallId);
        }
    }

    @Override
    public MachineInventoryLayout getInventoryLayout() {
        return MachineInventoryLayout.builder()
            .inputSlot()
            .slotAccess(inputSlotAccess)
            .outputSlot(4)
            .slotAccess(OUTPUT)
            .inputSlot((slot, stack) -> GrindingBallManager.isGrindingBall(stack))
            .slotAccess(grindingBallSlotAccess)
            .capacitor()
            .build();
    }

    @Override
    protected PoweredCraftingTask<SagMillingRecipe, SagMillingRecipe.Container> createTask(@Nullable SagMillingRecipe recipe) {
        return new PoweredCraftingTask<>(this, container, OUTPUT, recipe) {
            @Override
            protected void takeInputs(SagMillingRecipe recipe) {
                MachineInventory inv = getInventory();
                inputSlotAccess.getItemStack(inv).shrink(1);

                // Claim any available grinding balls.
                if (grindingBallData == IGrindingBallData.IDENTITY) {
                    ItemStack ball = grindingBallSlotAccess.getItemStack(inv);
                    if (!ball.isEmpty()) {
                        IGrindingBallData data = GrindingBallManager.getData(ball);
                        setGrindingBallData(data);
                        if (data != IGrindingBallData.IDENTITY) {
                            ball.shrink(1);
                        }
                    }
                }
            }

            @Override
            protected int consumeEnergy(int maxConsume) {
                int energyConsumed = super.consumeEnergy(maxConsume);

                if (getRecipe().getBonusType().useGrindingBall()) {
                    // Damage the grinding ball by how much micro infinity was consumed.
                    grindingBallDamage += energyConsumed;

                    // If its broken, go back to identity.
                    if (grindingBallDamage >= grindingBallData.getDurability()) {
                        setGrindingBallData(IGrindingBallData.IDENTITY);
                    }
                }

                return energyConsumed;
            }
        };
    }

    @Override
    protected SagMillingRecipe.Container getContainer() {
        return container;
    }

    @Override
    public void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        if (grindingBallData != IGrindingBallData.IDENTITY) {
            pTag.putString("GrindingBall", grindingBallData.getId().toString());
            pTag.putInt("GrindingBallDamage", grindingBallDamage);
        }
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        if (pTag.contains("GrindingBall")) {
            pendingGrindingBallId = new ResourceLocation(pTag.getString("GrindingBall"));
        }

        if (pTag.contains("GrindingBallDamage")) {
            grindingBallDamage = pTag.getInt("GrindingBallDamage");
        }
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new SagMillMenu(this, inventory, containerId);
    }
}
