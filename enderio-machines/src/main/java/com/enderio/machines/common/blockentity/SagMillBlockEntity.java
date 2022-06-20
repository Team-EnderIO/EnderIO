package com.enderio.machines.common.blockentity;

import com.enderio.api.capacitor.CapacitorKey;
import com.enderio.api.grindingball.IGrindingBallData;
import com.enderio.base.common.blockentity.sync.IntegerDataSlot;
import com.enderio.base.common.blockentity.sync.ResourceLocationDataSlot;
import com.enderio.base.common.blockentity.sync.SyncMode;
import com.enderio.base.common.util.GrindingBallManager;
import com.enderio.machines.common.MachineTier;
import com.enderio.machines.common.blockentity.base.PoweredCraftingMachine;
import com.enderio.machines.common.blockentity.task.PoweredCraftingTask;
import com.enderio.machines.common.init.MachineCapacitorKeys;
import com.enderio.machines.common.init.MachineRecipes;
import com.enderio.machines.common.io.item.MachineInventory;
import com.enderio.machines.common.io.item.MachineInventoryLayout;
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

public abstract class SagMillBlockEntity extends PoweredCraftingMachine<SagMillingRecipe, SagMillingRecipe.Container> {

    // region Tiers

    public static class Simple extends SagMillBlockEntity {

        public Simple(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState) {
            super(MachineCapacitorKeys.SIMPLE_SAG_MILL_ENERGY_CAPACITY.get(),
                MachineCapacitorKeys.SIMPLE_SAG_MILL_ENERGY_TRANSFER.get(),
                MachineCapacitorKeys.SIMPLE_SAG_MILL_ENERGY_CONSUME.get(),
                type, worldPosition, blockState);
        }

        @Override
        public MachineTier getTier() {
            return MachineTier.SIMPLE;
        }

        @Override
        public int getEnergyLeakPerSecond() {
            return 1; // TODO: Config
        }
    }

    public static class Standard extends SagMillBlockEntity {

        public Standard(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState) {
            super(MachineCapacitorKeys.SAG_MILL_ENERGY_CAPACITY.get(),
                MachineCapacitorKeys.SAG_MILL_ENERGY_TRANSFER.get(),
                MachineCapacitorKeys.SAG_MILL_ENERGY_CONSUME.get(),
                type, worldPosition, blockState);
        }

        @Override
        public MachineTier getTier() {
            return MachineTier.STANDARD;
        }
    }

    public static class Enhanced extends SagMillBlockEntity {

        public Enhanced(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState) {
            super(MachineCapacitorKeys.ENHANCED_SAG_MILL_ENERGY_CAPACITY.get(),
                MachineCapacitorKeys.ENHANCED_SAG_MILL_ENERGY_TRANSFER.get(),
                MachineCapacitorKeys.ENHANCED_SAG_MILL_ENERGY_CONSUME.get(),
                type, worldPosition, blockState);
        }

        @Override
        public MachineTier getTier() {
            return MachineTier.ENHANCED;
        }
    }

    // endregion

    private IGrindingBallData grindingBallData = IGrindingBallData.IDENTITY;

    private @Nullable ResourceLocation pendingGrindingBallId;

    private int grindingBallDamage;

    private final SagMillingRecipe.Container container;


    public SagMillBlockEntity(CapacitorKey capacityKey, CapacitorKey transferKey, CapacitorKey energyUseKey, BlockEntityType<?> type, BlockPos worldPosition,
        BlockState blockState) {
        super(MachineRecipes.Types.SAGMILLING, capacityKey, transferKey, energyUseKey, type, worldPosition, blockState);
        container = new SagMillingRecipe.Container(getInventory(), this::getGrindingBallData);

        addDataSlot(new IntegerDataSlot(() -> grindingBallDamage, dmg -> grindingBallDamage = dmg, SyncMode.GUI));
        addDataSlot(new ResourceLocationDataSlot(() -> grindingBallData.getId(), gId -> grindingBallData = GrindingBallManager.getData(gId), SyncMode.GUI));
    }

    public @Nullable IGrindingBallData getGrindingBallData() {
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
        return MachineInventoryLayout.builder(getTier() != MachineTier.SIMPLE)
            .inputSlot()
            .outputSlot(4)
            .inputSlot((slot, stack) -> GrindingBallManager.isGrindingBall(stack))
            .build();
    }

    @Override
    protected PoweredCraftingTask<SagMillingRecipe, SagMillingRecipe.Container> createTask(@Nullable SagMillingRecipe recipe) {
        return new PoweredCraftingTask<>(this, container, 1, 4, recipe) {
            @Override
            protected void takeInputs(SagMillingRecipe recipe) {
                MachineInventory inv = getInventory();
                inv.getStackInSlot(0).shrink(1);

                // Claim any available grinding balls.
                if (grindingBallData == IGrindingBallData.IDENTITY) {
                    ItemStack ball = inv.getStackInSlot(5);
                    if (!ball.isEmpty()) {
                        IGrindingBallData data = GrindingBallManager.getData(ball);
                        if (data != null) {
                            setGrindingBallData(data);
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
