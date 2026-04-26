package com.enderio.enderio.content.machines.sag_mill;

import com.enderio.core.annotations.UseOnly;
import com.enderio.enderio.api.capacitor.CapacitorModifier;
import com.enderio.enderio.api.capacitor.QuadraticScalable;
import com.enderio.enderio.api.components.GrindingBallData;
import com.enderio.enderio.api.io.energy.EnergyIOMode;
import com.enderio.enderio.client.SoundHandler;
import com.enderio.enderio.config.machines.MachinesConfig;
import com.enderio.enderio.foundation.block.ProgressMachineBlock;
import com.enderio.enderio.foundation.block.entity.PoweredMachineBlockEntity;
import com.enderio.enderio.foundation.block.entity.flags.CapacitorSupport;
import com.enderio.enderio.foundation.inventory.MachineInventory;
import com.enderio.enderio.foundation.inventory.MachineInventoryLayout;
import com.enderio.enderio.foundation.inventory.MultiSlotAccess;
import com.enderio.enderio.foundation.inventory.SingleSlotAccess;
import com.enderio.enderio.foundation.recipe.MachineRecipeCaches;
import com.enderio.enderio.foundation.task.PoweredCraftingMachineTask;
import com.enderio.enderio.foundation.task.host.CraftingMachineTaskHost;
import com.enderio.enderio.init.EIOBlockEntities;
import com.enderio.enderio.init.EIODataComponents;
import com.enderio.enderio.init.EIORecipes;
import com.enderio.enderio.init.EIOSounds;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.LogicalSide;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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
        super(EIOBlockEntities.SAG_MILL.get(), worldPosition, blockState, true, CapacitorSupport.REQUIRED,
                EnergyIOMode.Input, CAPACITY, USAGE);

        craftingTaskHost = new CraftingMachineTaskHost<>(this, this::hasEnergy, EIORecipes.SAG_MILLING.type().get(),
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
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (state.getValue(ProgressMachineBlock.POWERED)) {
            double x = pos.getX() + 0.5;
            double y = pos.getY();
            double z = pos.getZ() + 0.5;

            SoundHandler.playSound(pos, EIOSounds.SAG_MILL.get(), SoundSource.BLOCKS, MachinesConfig.CLIENT.MACHINE_VOLUME.get(), 1.0f, random, x, y, z);

            Direction direction = state.getValue(ProgressMachineBlock.FACING);
            Direction.Axis axis = direction.getAxis();
            double r = 0.7;
            double ss = random.nextDouble() * 0.6 - 0.3;
            double dx = axis == Direction.Axis.X ? direction.getStepX() * r : ss;
            double dy = random.nextDouble();
            double dz = axis == Direction.Axis.Z ? direction.getStepZ() * r : ss;
            level.addParticle(ParticleTypes.DUST_PLUME, x + dx, y + dy, z + dz, 0.0, 0.0, 0.0);
            ItemStack input = INPUT.getItemStack(this);
            if (input.getItem() instanceof BlockItem blockItem) {
                level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, blockItem.getBlock().defaultBlockState()), x + dx, y + dy, z + dz, 0.0, 0.0, 0.0);
            } else {
                level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.GRAVEL.defaultBlockState()), x + dx, y + dy, z + dz, 0.0, 0.0, 0.0);
            }
        } else {
            SoundHandler.stopSound(pos);
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
                .inputSlot((slot, stack) -> stack.getItemHolder().getData(GrindingBallData.DATA_MAP_TYPE) != null)
                .slotAccess(GRINDING_BALL)
                .capacitor()
                .build();
    }

    private boolean isValidInput(int index, ItemStack stack) {
        return MachineRecipeCaches.SAG_MILLING.hasRecipe(List.of(stack));
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
                        GrindingBallData data = ball.getItemHolder().getData(GrindingBallData.DATA_MAP_TYPE);
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
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        super.saveAdditional(tag, lookupProvider);
        craftingTaskHost.save(lookupProvider, tag);

        if (!grindingBallData.isIdentity()) {
            tag.put(KEY_GRINDING_BALL, grindingBallData.save(lookupProvider));
            tag.putInt(KEY_GRINDING_BALL_DAMAGE, grindingBallDamage);
        }
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        super.loadAdditional(tag, lookupProvider);
        craftingTaskHost.load(lookupProvider, tag);

        if (tag.contains(KEY_GRINDING_BALL)) {
            grindingBallData = GrindingBallData.parseOptional(lookupProvider, tag.getCompound((KEY_GRINDING_BALL)));
        }

        if (tag.contains(KEY_GRINDING_BALL_DAMAGE)) {
            grindingBallDamage = tag.getInt(KEY_GRINDING_BALL_DAMAGE);
        }
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput components) {
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
    public void removeComponentsFromTag(CompoundTag tag) {
        super.removeComponentsFromTag(tag);
        tag.remove(KEY_GRINDING_BALL);
        tag.remove(KEY_GRINDING_BALL_DAMAGE);
    }

    // endregion
}
