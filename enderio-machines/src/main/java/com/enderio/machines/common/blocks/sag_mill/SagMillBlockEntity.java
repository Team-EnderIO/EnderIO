package com.enderio.machines.common.blocks.sag_mill;

import com.enderio.base.api.capacitor.CapacitorModifier;
import com.enderio.base.api.capacitor.QuadraticScalable;
import com.enderio.base.api.grindingball.GrindingBallData;
import com.enderio.base.api.io.energy.EnergyIOMode;
import com.enderio.base.common.init.EIODataComponents;
import com.enderio.machines.common.blocks.base.blockentity.PoweredMachineBlockEntity;
import com.enderio.machines.common.blocks.base.blockentity.flags.CapacitorSupport;
import com.enderio.machines.common.blocks.base.inventory.MachineInventory;
import com.enderio.machines.common.blocks.base.inventory.MachineInventoryLayout;
import com.enderio.machines.common.blocks.base.inventory.MultiSlotAccess;
import com.enderio.machines.common.blocks.base.inventory.SingleSlotAccess;
import com.enderio.machines.common.blocks.base.task.PoweredCraftingMachineTask;
import com.enderio.machines.common.blocks.base.task.host.CraftingMachineTaskHost;
import com.enderio.machines.common.config.MachinesConfig;
import com.enderio.machines.common.init.MachineBlockEntities;
import com.enderio.machines.common.init.MachineDataComponents;
import com.enderio.machines.common.init.MachineRecipes;
import com.enderio.machines.common.recipe.RecipeCaches;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class SagMillBlockEntity extends PoweredMachineBlockEntity {
    public static final QuadraticScalable CAPACITY = new QuadraticScalable(CapacitorModifier.ENERGY_CAPACITY,
            MachinesConfig.COMMON.ENERGY.SAG_MILL_CAPACITY);
    public static final QuadraticScalable USAGE = new QuadraticScalable(CapacitorModifier.ENERGY_USE,
            MachinesConfig.COMMON.ENERGY.SAG_MILL_USAGE);

    public static final SingleSlotAccess INPUT = new SingleSlotAccess();
    public static final SingleSlotAccess GRINDING_BALL = new SingleSlotAccess();
    public static final MultiSlotAccess OUTPUT = new MultiSlotAccess();

    private GrindingBallData grindingBallData = GrindingBallData.IDENTITY;
    private int grindingBallDamage;

    private final CraftingMachineTaskHost<SagMillingRecipe, SagMillingRecipe.Input> craftingTaskHost;

    public SagMillBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(MachineBlockEntities.SAG_MILL.get(), worldPosition, blockState, true, CapacitorSupport.REQUIRED,
                EnergyIOMode.Input, CAPACITY, USAGE);

        craftingTaskHost = new CraftingMachineTaskHost<>(this, this::hasEnergy, MachineRecipes.SAG_MILLING.type().get(),
                this::createTask, this::createRecipeInput);
    }

    public GrindingBallData getGrindingBallData() {
        return grindingBallData;
    }

    public void setGrindingBallData(GrindingBallData data) {
        grindingBallDamage = 0;
        grindingBallData = data;
    }

    public float getGrindingBallDamage() {
        if (grindingBallData.durability() <= 0) {
            return 0.0f;
        }

        return 1.0f - (grindingBallDamage / (float) grindingBallData.durability());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new SagMillMenu(containerId, inventory, this);
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

    @Override
    public MachineInventoryLayout createInventoryLayout() {
        return MachineInventoryLayout.builder()
                .inputSlot(this::isValidInput)
                .slotAccess(INPUT)
                .outputSlot(4)
                .slotAccess(OUTPUT)
                .inputSlot((slot, stack) -> stack.has(EIODataComponents.GRINDING_BALL))
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

    private SagMillingRecipe.Input createRecipeInput() {
        return new SagMillingRecipe.Input(INPUT.getItemStack(getInventory()), getGrindingBallData());
    }

    // region Crafting Task

    public float getCraftingProgress() {
        return craftingTaskHost.getProgress();
    }

    @Override
    public boolean isActive() {
        return canAct() && hasEnergy() && craftingTaskHost.hasTask();
    }

    protected PoweredCraftingMachineTask<SagMillingRecipe, SagMillingRecipe.Input> createTask(Level level,
            SagMillingRecipe.Input container, @Nullable RecipeHolder<SagMillingRecipe> recipe) {
        return new PoweredCraftingMachineTask<>(level, getInventory(), getEnergyStorage(), container, OUTPUT, recipe) {
            @Override
            protected void consumeInputs(SagMillingRecipe recipe) {
                MachineInventory inv = getInventory();
                INPUT.getItemStack(inv).shrink(1);

                // Claim any available grinding balls.
                if (recipe.bonusType().useGrindingBall() && grindingBallData.isIdentity()) {
                    ItemStack ball = GRINDING_BALL.getItemStack(inv);
                    if (!ball.isEmpty()) {
                        var data = ball.getOrDefault(EIODataComponents.GRINDING_BALL, GrindingBallData.IDENTITY);
                        setGrindingBallData(data);
                        if (!data.isIdentity()) {
                            ball.shrink(1);
                        }
                    }
                }
            }

            @Override
            protected int makeProgress(int remainingProgress) {
                int energyConsumed = super.makeProgress(remainingProgress);

                if (getRecipe().bonusType().useGrindingBall()) {
                    // Damage the grinding ball by how much micro infinity was consumed.
                    grindingBallDamage += energyConsumed;

                    // If its broken, go back to identity.
                    if (grindingBallDamage >= grindingBallData.durability()) {
                        setGrindingBallData(GrindingBallData.IDENTITY);
                    }
                }

                return energyConsumed;
            }
        };
    }

    // endregion

    // region Serialization

    // region Serialization

    private static final String KEY_GRINDING_BALL = "GrindingBal";
    private static final String KEY_GRINDING_BALL_DAMAGE = "GrindingBallDamage";

    @Override
    public void saveAdditional(CompoundTag pTag, HolderLookup.Provider lookupProvider) {
        super.saveAdditional(pTag, lookupProvider);
        craftingTaskHost.save(lookupProvider, pTag);

        if (!grindingBallData.isIdentity()) {
            pTag.put(KEY_GRINDING_BALL, grindingBallData.save(lookupProvider));
            pTag.putInt(KEY_GRINDING_BALL_DAMAGE, grindingBallDamage);
        }
    }

    @Override
    public void loadAdditional(CompoundTag pTag, HolderLookup.Provider lookupProvider) {
        super.loadAdditional(pTag, lookupProvider);
        craftingTaskHost.load(lookupProvider, pTag);

        if (pTag.contains(KEY_GRINDING_BALL)) {
            grindingBallData = GrindingBallData.parseOptional(lookupProvider, pTag.getCompound((KEY_GRINDING_BALL)));
        }

        if (pTag.contains(KEY_GRINDING_BALL_DAMAGE)) {
            grindingBallDamage = pTag.getInt(KEY_GRINDING_BALL_DAMAGE);
        }
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput components) {
        super.applyImplicitComponents(components);

        grindingBallData = components.getOrDefault(MachineDataComponents.SAG_MILL_GRINDING_BALL,
                GrindingBallData.IDENTITY);
        grindingBallDamage = components.getOrDefault(MachineDataComponents.SAG_MILL_GRINDING_BALL_DAMAGE, 0);
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);

        if (getGrindingBallDamage() > 0) {
            components.set(MachineDataComponents.SAG_MILL_GRINDING_BALL, grindingBallData);
            components.set(MachineDataComponents.SAG_MILL_GRINDING_BALL_DAMAGE, grindingBallDamage);
        }
    }

    @Override
    public void removeComponentsFromTag(CompoundTag tag) {
        super.removeComponentsFromTag(tag);
        tag.remove(KEY_GRINDING_BALL);
        tag.remove(KEY_GRINDING_BALL_DAMAGE);
    }

    // endregion
}
