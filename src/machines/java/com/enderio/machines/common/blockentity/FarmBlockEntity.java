package com.enderio.machines.common.blockentity;

import com.enderio.api.capacitor.CapacitorModifier;
import com.enderio.api.capacitor.QuadraticScalable;
import com.enderio.api.io.energy.EnergyIOMode;
import com.enderio.core.common.network.slot.BooleanNetworkDataSlot;
import com.enderio.core.common.network.slot.IntegerNetworkDataSlot;
import com.enderio.machines.common.blockentity.base.PoweredMachineBlockEntity;
import com.enderio.machines.common.blockentity.task.FarmTask;
import com.enderio.machines.common.config.MachinesConfig;
import com.enderio.machines.common.io.item.MachineInventoryLayout;
import com.enderio.machines.common.io.item.MultiSlotAccess;
import com.enderio.machines.common.io.item.SingleSlotAccess;
import com.enderio.machines.common.menu.FarmMenu;
import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FarmBlockEntity extends PoweredMachineBlockEntity {
    public static final String CONSUMED = "Consumed";
    private static final QuadraticScalable ENERGY_CAPACITY = new QuadraticScalable(CapacitorModifier.ENERGY_CAPACITY, MachinesConfig.COMMON.ENERGY.FARM_CAPACITY);
    private static final QuadraticScalable ENERGY_USAGE = new QuadraticScalable(CapacitorModifier.ENERGY_USE, MachinesConfig.COMMON.ENERGY.FARM_USAGE);
    public static final SingleSlotAccess AXE = new SingleSlotAccess();
    public static final SingleSlotAccess HOE = new SingleSlotAccess();
    public static final SingleSlotAccess SHEAR = new SingleSlotAccess();
    public static final SingleSlotAccess NE = new SingleSlotAccess();
    public static final SingleSlotAccess SE = new SingleSlotAccess();
    public static final SingleSlotAccess SW = new SingleSlotAccess();
    public static final SingleSlotAccess NW = new SingleSlotAccess();
    public static final MultiSlotAccess BONEMEAL = new MultiSlotAccess();
    public static final MultiSlotAccess OUTPUT = new MultiSlotAccess();
    //TODO Move cause this isn't a good place imo
    public static final FakePlayer FARM_PLAYER = new FakePlayer(ServerLifecycleHooks.getCurrentServer().overworld(), new GameProfile(UUID.fromString("7b2621b4-83fb-11ee-b962-0242ac120002"), "enderio:farm"));
    private List<BlockPos> positions;
    private int currentIndex = 0;
    private int consumed = 0;
    @Nullable
    private FarmTask currentTask = null;

    public FarmBlockEntity(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState) {
        super(EnergyIOMode.Input, ENERGY_CAPACITY, ENERGY_USAGE, type, worldPosition, blockState);
        this.range = 5;

        rangeDataSlot = new IntegerNetworkDataSlot(this::getRange, r -> this.range = r) {
            @Override
            public void updateServerCallback() {
                updateLocations();
            }
        };
        addDataSlot(rangeDataSlot);

        rangeVisibleDataSlot = new BooleanNetworkDataSlot(this::isRangeVisible, b -> this.rangeVisible = b);
        addDataSlot(rangeVisibleDataSlot);
    }

    @Override
    public @Nullable MachineInventoryLayout getInventoryLayout() {
        return MachineInventoryLayout.builder()
            .capacitor()
            .inputSlot((i,s) -> s.is(ItemTags.AXES))
            .slotAccess(AXE)
            .inputSlot((i,s) -> s.is(ItemTags.HOES))
            .slotAccess(HOE)
            .inputSlot((i,s) -> s.is(Tags.Items.SHEARS))
            .slotAccess(SHEAR)
            .inputSlot()
            .slotAccess(NE)
            .inputSlot()
            .slotAccess(SE)
            .inputSlot()
            .slotAccess(SW)
            .inputSlot()
            .slotAccess(NW)
            .inputSlot(2)
            .slotAccess(BONEMEAL)
            .outputSlot(6)
            .slotAccess(OUTPUT)
            .build();
    }

    @Override
    public void serverTick() {
        if (isActive()) {
            doFarmTask();
        }

        super.serverTick();
    }

    private void doFarmTask() {
        int stop = Math.min(currentIndex + range, positions.size());
        while (currentIndex < stop) {
            BlockPos soil = positions.get(currentIndex);
            if (currentTask != null) {
                if (currentTask.farm(soil, this) != FarmInteraction.POWERED) {
                    currentTask = null; //Task is done or no longer valid
                }
                break;
            }
            //Look for a new task
            for (FarmTask task: FarmTask.TASKS) {
                FarmInteraction interaction = task.farm(soil, this);
                if (interaction == FarmInteraction.POWERED) { //new task found
                    currentTask = task;
                    break;
                }
                if (interaction == FarmInteraction.FINISHED) {//Task found and already done
                    currentTask = null;
                    break;
                }
            }
            //task found
            if (currentTask != null) {
                break;
            }
            currentIndex++;
        }
        //All positions have been checked, restart
        if (stop == positions.size()) {
            currentIndex = 0;
        }
    }

    //TODO check if the coords actually are these direction
    public SingleSlotAccess getSeedForPos(BlockPos soil) {
        if (soil.getX() >= getBlockPos().getX() && soil.getZ() > getBlockPos().getZ()){
            return NW;
        }
        if (soil.getX() > getBlockPos().getX() && soil.getZ() <= getBlockPos().getZ()){
            return SW;
        }
        if (soil.getX() <= getBlockPos().getX() && soil.getZ() < getBlockPos().getZ()){
            return SE;
        }
        if (soil.getX() < getBlockPos().getX() && soil.getZ() >= getBlockPos().getZ()){
            return NE;
        }
        return NW;
    }

    @Override
    protected boolean isActive() {
        if (!canAct()) {
            return false;
        }
        //Check tool
        return true;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new FarmMenu(this, playerInventory, containerId);
    }

    @Override
    public void setRange(int range) {
        super.setRange(range);
        updateLocations();
    }

    @Override
    public BlockPos getParticleLocation() {
        return worldPosition.below();
    }

    @Override
    public void onLoad() {
        super.onLoad();
        updateLocations();
    }

    private void updateLocations() {
        positions = new ArrayList<>();
        currentIndex = 0;
        for (BlockPos pos : BlockPos.betweenClosed(worldPosition.offset(-range,-1, -range), worldPosition.offset(range,-1,range))) {
            positions.add(pos.immutable()); //Need to make it immutable
        }
    }

    //TODO handle inv full
    public void collectDrops(List<ItemStack> drops, @Nullable BlockPos soil) {
        for (ItemStack drop : drops) {
            if (soil != null) {
                ItemStack seeds = getSeedForPos(soil).getItemStack(this);
                if (seeds.isEmpty()) {
                    if (drop.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof IPlantable) {
                        getSeedForPos(soil).setStackInSlot(this, drop);
                        continue;
                    }
                }
                if (ItemStack.isSameItem(drop, seeds)) {
                    int leftOver = seeds.getMaxStackSize() - seeds.getCount();
                    if (drop.getCount() > leftOver) {
                        seeds.setCount(seeds.getMaxStackSize());
                        drop.shrink(leftOver);
                    } else {
                        seeds.setCount(seeds.getCount() + drop.getCount());
                        drop.setCount(0);
                        continue;
                    }
                }
            }
            for (int i = 0; i < 6; i++) {
                ItemStack leftOver = OUTPUT.get(i).insertItem(this, drop.copy(), false);
                if (leftOver.isEmpty()) {
                    drop.setCount(0);
                    break;
                } else {
                    drop.setCount(leftOver.getCount());
                }
            }
        }
    }

    public int getConsumedPower() {
        return consumed;
    }

    public int setConsumedPower(int power) {
        return consumed = power;
    }

    @Override
    public void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.putInt(CONSUMED, consumed);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        consumed = pTag.getInt(CONSUMED);
    }

    public boolean consumeBonemeal() {
        boolean consumed = false;
        for (int i = 0; i < 2; i++) {
            ItemStack itemStack = BONEMEAL.get(i).getItemStack(this);
            if (!itemStack.isEmpty()) {
                itemStack.shrink(1);
                consumed = true;
                break;
            }
        }
        return consumed;
    }

    public enum FarmInteraction {
        FINISHED,
        POWERED,
        BLOCKED,
        IGNORED;
    }
}
