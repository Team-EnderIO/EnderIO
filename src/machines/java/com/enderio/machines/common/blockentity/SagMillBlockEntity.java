package com.enderio.machines.common.blockentity;

import com.enderio.api.capacitor.CapacitorModifier;
import com.enderio.api.capacitor.QuadraticScalable;
import com.enderio.api.grindingball.IGrindingBallData;
import com.enderio.api.io.energy.EnergyIOMode;
import com.enderio.base.common.util.GrindingBallManager;
import com.enderio.core.common.network.slot.IntegerNetworkDataSlot;
import com.enderio.core.common.network.slot.ResourceLocationNetworkDataSlot;
import com.enderio.machines.common.blockentity.base.PoweredMachineBlockEntity;
import com.enderio.machines.common.blockentity.task.PoweredCraftingMachineTask;
import com.enderio.machines.common.blockentity.task.host.CraftingMachineTaskHost;
import com.enderio.machines.common.config.MachinesConfig;
import com.enderio.machines.common.init.MachineRecipes;
import com.enderio.machines.common.io.item.MachineInventory;
import com.enderio.machines.common.io.item.MachineInventoryLayout;
import com.enderio.machines.common.io.item.MultiSlotAccess;
import com.enderio.machines.common.io.item.SingleSlotAccess;
import com.enderio.machines.common.menu.SagMillMenu;
import com.enderio.machines.common.recipe.RecipeCaches;
import com.enderio.machines.common.recipe.SagMillingRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SagMillBlockEntity extends PoweredMachineBlockEntity {
    public static final QuadraticScalable CAPACITY = new QuadraticScalable(CapacitorModifier.ENERGY_CAPACITY, MachinesConfig.COMMON.ENERGY.SAG_MILL_CAPACITY);
    public static final QuadraticScalable USAGE = new QuadraticScalable(CapacitorModifier.ENERGY_USE, MachinesConfig.COMMON.ENERGY.SAG_MILL_USAGE);

    public static final SingleSlotAccess INPUT = new SingleSlotAccess();
    public static final SingleSlotAccess GRINDING_BALL = new SingleSlotAccess();
    public static final MultiSlotAccess OUTPUT = new MultiSlotAccess();

    private IGrindingBallData grindingBallData = IGrindingBallData.IDENTITY;
    @Nullable
    private ResourceLocation pendingGrindingBallId;
    private int grindingBallDamage;

    private final CraftingMachineTaskHost<SagMillingRecipe, SagMillingRecipe.Container> craftingTaskHost;

    public SagMillBlockEntity(BlockEntityType<?> type, BlockPos worldPosition,
        BlockState blockState) {
        super(EnergyIOMode.Input, CAPACITY, USAGE, type, worldPosition, blockState);

        addDataSlot(new IntegerNetworkDataSlot(() -> grindingBallDamage, i -> grindingBallDamage = i));
        addDataSlot(new ResourceLocationNetworkDataSlot(() -> grindingBallData.getGrindingBallId(), gId -> grindingBallData = GrindingBallManager.getData(gId)));

        craftingTaskHost = new CraftingMachineTaskHost<>(this, this::hasEnergy, MachineRecipes.SAG_MILLING.type().get(),
            new SagMillingRecipe.Container(getInventoryNN(), this::getGrindingBallData), this::createTask);
    }

    public IGrindingBallData getGrindingBallData() {
        return grindingBallData;
    }

    public void setGrindingBallData(IGrindingBallData data) {
        grindingBallDamage = 0;
        grindingBallData = data;
    }

    public float getGrindingBallDamage() {
        if (grindingBallData.getDurability() <= 0) {
            return 0.0f;
        }

        return 1.0f - (grindingBallDamage / (float) grindingBallData.getDurability());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new SagMillMenu(this, inventory, containerId);
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

        // Load a pending grinding ball.
        if (pendingGrindingBallId != null) {
            grindingBallData = GrindingBallManager.getData(pendingGrindingBallId);
        }
    }

    @Override
    public MachineInventoryLayout getInventoryLayout() {
        return MachineInventoryLayout.builder()
            .inputSlot(this::isValidInput)
            .slotAccess(INPUT)
            .outputSlot(4)
            .slotAccess(OUTPUT)
            .inputSlot((slot, stack) -> GrindingBallManager.isGrindingBall(stack))
            .slotAccess(GRINDING_BALL)
            .capacitor()
            .build();
    }

    private boolean isValidInput(int index, ItemStack stack) {
        return RecipeCaches.SAG_MILLING.hasRecipe(List.of(stack));
    }

    @Override
    protected void onInventoryContentsChanged(int slot) {
        super.onInventoryContentsChanged(slot);
        craftingTaskHost.newTaskAvailable();
    }

    // region Crafting Task

    public float getCraftingProgress() {
        return craftingTaskHost.getProgress();
    }

    @Override
    protected boolean isActive() {
        return canAct() && hasEnergy() && craftingTaskHost.hasTask();
    }

    protected PoweredCraftingMachineTask<SagMillingRecipe, SagMillingRecipe.Container> createTask(Level level, SagMillingRecipe.Container container, @Nullable SagMillingRecipe recipe) {
        return new PoweredCraftingMachineTask<>(level, getInventoryNN(), getEnergyStorage(), container, OUTPUT, recipe) {
            @Override
            protected void consumeInputs(SagMillingRecipe recipe) {
                MachineInventory inv = getInventory();
                INPUT.getItemStack(inv).shrink(1);

                // Claim any available grinding balls.
                if (recipe.getBonusType().useGrindingBall() && grindingBallData == IGrindingBallData.IDENTITY) {
                    ItemStack ball = GRINDING_BALL.getItemStack(inv);
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
            protected int makeProgress(int remainingProgress) {
                int energyConsumed = super.makeProgress(remainingProgress);

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

    // endregion

    // region Serialization

    // region Serialization

    private static final String KEY_GRINDING_BALL_ID = "GrindingBallId";
    private static final String KEY_GRINDING_BALL_DAMAGE = "GrindingBallDamage";

    @Override
    public void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        craftingTaskHost.save(pTag);
        if (grindingBallData != IGrindingBallData.IDENTITY) {
            pTag.putString(KEY_GRINDING_BALL_ID, grindingBallData.getGrindingBallId().toString());
            pTag.putInt(KEY_GRINDING_BALL_DAMAGE, grindingBallDamage);
        }
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        craftingTaskHost.load(pTag);
        if (pTag.contains(KEY_GRINDING_BALL_ID)) {
            pendingGrindingBallId = new ResourceLocation(pTag.getString(KEY_GRINDING_BALL_ID));
        }

        if (pTag.contains(KEY_GRINDING_BALL_DAMAGE)) {
            grindingBallDamage = pTag.getInt(KEY_GRINDING_BALL_DAMAGE);
        }
    }

    // endregion
}
