package com.enderio.enderio.content.machines.sag_mill;

import com.enderio.core.common.storage.ItemStorage;
import com.enderio.core.common.storage.layout.ItemStorageLayout;
import com.enderio.core.common.storage.layout.SlotTemplates;
import com.enderio.core.common.storage.slot.MultiResourceSlotKey;
import com.enderio.core.common.storage.slot.SingleResourceSlotKey;
import com.enderio.enderio.api.capacitor.CapacitorModifier;
import com.enderio.enderio.api.capacitor.QuadraticScalable;
import com.enderio.enderio.api.components.GrindingBallData;
import com.enderio.enderio.api.io.energy.EnergyIOMode;
import com.enderio.enderio.config.machines.MachinesConfig;
import com.enderio.enderio.foundation.MachineNBTKeys;
import com.enderio.enderio.foundation.block.entity.PoweredMachineBlockEntity;
import com.enderio.enderio.foundation.block.entity.flags.CapacitorSupport;
import com.enderio.enderio.foundation.inventory.MachineSlotTemplates;
import com.enderio.enderio.foundation.recipe.MachineRecipeCaches;
import com.enderio.enderio.foundation.task.PoweredCraftingMachineTask;
import com.enderio.enderio.foundation.task.host.CraftingMachineTaskHost;
import com.enderio.enderio.init.EIOBlockEntities;
import com.enderio.enderio.init.EIODataComponents;
import com.enderio.enderio.init.EIORecipeTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class SagMillBlockEntity extends PoweredMachineBlockEntity {
    public static final QuadraticScalable CAPACITY = new QuadraticScalable(CapacitorModifier.ENERGY_CAPACITY,
            MachinesConfig.COMMON.ENERGY.SAG_MILL_CAPACITY);
    public static final QuadraticScalable USAGE = new QuadraticScalable(CapacitorModifier.ENERGY_USE,
            MachinesConfig.COMMON.ENERGY.SAG_MILL_USAGE);

    public static final SingleResourceSlotKey<ItemResource> INPUT = new SingleResourceSlotKey<>();
    public static final SingleResourceSlotKey<ItemResource> GRINDING_BALL = new SingleResourceSlotKey<>();
    public static final MultiResourceSlotKey<ItemResource> OUTPUT = new MultiResourceSlotKey<>(4);
    public static final SingleResourceSlotKey<ItemResource> CAPACITOR = new SingleResourceSlotKey<>();

    private GrindingBallData grindingBallData = GrindingBallData.IDENTITY;
    private int grindingBallDamage;

    private final CraftingMachineTaskHost<SagMillingRecipe, SagMillingRecipe.Input> craftingTaskHost;

    public SagMillBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(EIOBlockEntities.SAG_MILL.get(), worldPosition, blockState, true, CapacitorSupport.REQUIRED, CAPACITOR,
                EnergyIOMode.Input, CAPACITY, USAGE);

        craftingTaskHost = new CraftingMachineTaskHost<>(this, this::hasEnergy, EIORecipeTypes.SAG_MILLING.get(),
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
    public ItemStorageLayout createInventoryLayout() {
        return ItemStorageLayout.builder()
            .add(INPUT, SlotTemplates.input(), b -> b
                .filter(this::isValidInput))
            .add(OUTPUT, SlotTemplates.output())
            .add(GRINDING_BALL, SlotTemplates.input(), b -> b
                .filter((_, itemResource) -> itemResource.typeHolder().getData(GrindingBallData.DATA_MAP_TYPE) != null))
            .add(CAPACITOR, MachineSlotTemplates.capacitor())
            .build();
    }

    private boolean isValidInput(int index, ItemResource stack) {
        return MachineRecipeCaches.SAG_MILLING.hasRecipe(List.of(stack.toStack()));
    }

    @Override
    protected void onInventoryContentsChanged(int slot) {
        super.onInventoryContentsChanged(slot);
        craftingTaskHost.newTaskAvailable();
    }

    private SagMillingRecipe.Input createRecipeInput() {
        return new SagMillingRecipe.Input(getInventory().getStack(INPUT), getGrindingBallData());
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
        return new PoweredCraftingMachineTask<>(level, this, getInventory(), getEnergyStorage(), container, OUTPUT, recipe) {
            @Override
            protected void consumeInputs(SagMillingRecipe recipe) {
                ItemStorage inv = getInventory();
                inv.getStack(INPUT).shrink(1);

                // Claim any available grinding balls.
                if (recipe.bonusType().useGrindingBall() && grindingBallData.isIdentity()) {
                    ItemStack ball = inv.getStack(GRINDING_BALL);
                    if (!ball.isEmpty()) {
                        GrindingBallData data = ball.typeHolder().getData(GrindingBallData.DATA_MAP_TYPE);
                        if (data == null) {
                            data = GrindingBallData.IDENTITY;
                        }
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
    public void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);

        output.putChild(MachineNBTKeys.CRAFTING_TASK, craftingTaskHost);

        if (!grindingBallData.isIdentity()) {
            output.store(KEY_GRINDING_BALL, GrindingBallData.CODEC, grindingBallData);
            output.putInt(KEY_GRINDING_BALL_DAMAGE, grindingBallDamage);
        }
    }

    @Override
    public void loadAdditional(ValueInput input) {
        super.loadAdditional(input);

        input.child(MachineNBTKeys.CRAFTING_TASK).ifPresent(craftingTaskHost::deserialize);

        grindingBallData = input.read(KEY_GRINDING_BALL, GrindingBallData.CODEC)
                .orElse(GrindingBallData.IDENTITY);

        grindingBallDamage = input.getIntOr(KEY_GRINDING_BALL_DAMAGE, 0);
    }

    @Override
    protected void applyImplicitComponents(DataComponentGetter components) {
        super.applyImplicitComponents(components);

        grindingBallData = components.getOrDefault(EIODataComponents.SAG_MILL_GRINDING_BALL,
                GrindingBallData.IDENTITY);
        grindingBallDamage = components.getOrDefault(EIODataComponents.SAG_MILL_GRINDING_BALL_DAMAGE, 0);
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);

        if (getGrindingBallDamage() > 0) {
            components.set(EIODataComponents.SAG_MILL_GRINDING_BALL, grindingBallData);
            components.set(EIODataComponents.SAG_MILL_GRINDING_BALL_DAMAGE, grindingBallDamage);
        }
    }

    @Override
    public void removeComponentsFromTag(ValueOutput output) {
        super.removeComponentsFromTag(output);
        output.discard(KEY_GRINDING_BALL);
        output.discard(KEY_GRINDING_BALL_DAMAGE);
    }

    // endregion
}
