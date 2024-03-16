package com.enderio.machines.common.blockentity;

import com.enderio.api.attachment.StoredEntityData;
import com.enderio.api.capacitor.CapacitorModifier;
import com.enderio.api.capacitor.QuadraticScalable;
import com.enderio.api.farm.FarmInteraction;
import com.enderio.api.farm.IFarmingStation;
import com.enderio.api.io.energy.EnergyIOMode;
import com.enderio.core.common.network.slot.CodecNetworkDataSlot;
import com.enderio.core.common.network.slot.FluidStackNetworkDataSlot;
import com.enderio.core.common.network.slot.ResourceLocationNetworkDataSlot;
import com.enderio.machines.common.attachment.ActionRange;
import com.enderio.machines.common.attachment.IFluidTankUser;
import com.enderio.machines.common.attachment.IRangedActor;
import com.enderio.machines.common.blockentity.base.PoweredMachineBlockEntity;
import com.enderio.api.farm.FarmTask;
import com.enderio.machines.common.config.MachinesConfig;
import com.enderio.machines.common.init.MachineAttachments;
import com.enderio.machines.common.init.MachineBlockEntities;
import com.enderio.machines.common.io.fluid.MachineFluidHandler;
import com.enderio.machines.common.io.fluid.MachineTankLayout;
import com.enderio.machines.common.io.fluid.TankAccess;
import com.enderio.machines.common.io.item.MachineInventoryLayout;
import com.enderio.machines.common.io.item.MultiSlotAccess;
import com.enderio.machines.common.io.item.SingleSlotAccess;
import com.enderio.machines.common.menu.FarmMenu;
import com.enderio.machines.common.souldata.EngineSoul;
import com.enderio.machines.common.souldata.FarmSoul;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.functions.LootingEnchantFunction;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RecipesUpdatedEvent;
import net.neoforged.neoforge.common.FarmlandWaterManager;
import net.neoforged.neoforge.common.IPlantable;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.ticket.AABBTicket;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.enderio.machines.common.blockentity.PoweredSpawnerBlockEntity.NO_MOB;

public class FarmBlockEntity extends PoweredMachineBlockEntity implements IRangedActor, IFarmingStation, IFluidTankUser {
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
    //TODO One fake player for all? Or one for each machine?
    public static final FakePlayer FARM_PLAYER = new FakePlayer(
        ServerLifecycleHooks.getCurrentServer().overworld(), new GameProfile(UUID.fromString("7b2621b4-83fb-11ee-b962-0242ac120002"), "enderio:farm"));
    private List<BlockPos> positions;
    private int currentIndex = 0;
    private int consumed = 0;
    @Nullable
    private FarmTask currentTask = null;
    private final CodecNetworkDataSlot<ActionRange> actionRangeDataSlot;
    private final MachineFluidHandler fluidHandler;
    private static final TankAccess TANK = new TankAccess();
    private static final int CAPACITY = 1000;
    @Nullable
    private AABBTicket ticket;

    private StoredEntityData entityData = StoredEntityData.empty();
    @Nullable
    private FarmSoul.SoulData soulData;
    private static boolean reload = false;
    private boolean reloadCache = !reload;


    public FarmBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(EnergyIOMode.Input, ENERGY_CAPACITY, ENERGY_USAGE, MachineBlockEntities.FARMING_STATION.get(), worldPosition, blockState);
        fluidHandler = createFluidHandler();

        // TODO: rubbish way of having a default. use an interface instead?
        if (!hasData(MachineAttachments.ACTION_RANGE)) {
            setData(MachineAttachments.ACTION_RANGE, new ActionRange(5, false));
        }

        addDataSlot(new ResourceLocationNetworkDataSlot(() -> this.getEntityType().orElse(NO_MOB),this::setEntityType));
        addDataSlot(new FluidStackNetworkDataSlot(() -> TANK.getFluid(this), f -> TANK.setFluid(this, f)));
        actionRangeDataSlot = addDataSlot(new CodecNetworkDataSlot<>(this::getActionRange, this::internalSetActionRange, ActionRange.CODEC));
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
        if (reloadCache != reload && entityData != StoredEntityData.empty() && entityData.getEntityType().isPresent()) {
            Optional<FarmSoul.SoulData> op = FarmSoul.FARM.matches(entityData.getEntityType().get());
            op.ifPresent(data -> soulData = data);
            reloadCache = reload;
        }
        if (isActive()) {
            doFarmTask();
        }

        super.serverTick();
    }

    @Override
    public void clientTick() {
        if (level.isClientSide && level instanceof ClientLevel clientLevel) {
            getActionRange().addClientParticle(clientLevel, getParticleLocation(), MachinesConfig.CLIENT.BLOCKS.DRAIN_RANGE_COLOR.get());
        }

        super.clientTick();
    }

    private void doFarmTask() {
        int stop = Math.min(currentIndex + getRange(), positions.size());
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
    public int getMaxRange() {
        return 5;
    }

    @Override
    public ActionRange getActionRange() {
        return getData(MachineAttachments.ACTION_RANGE);
    }

    @Override
    public void setActionRange(ActionRange actionRange) {
        if (level != null && level.isClientSide) {
            clientUpdateSlot(actionRangeDataSlot, actionRange);
        } else {
            internalSetActionRange(actionRange);
        }
    }

    private void internalSetActionRange(ActionRange actionRange) {
        setData(MachineAttachments.ACTION_RANGE, actionRange);
        updateLocations();
        setChanged();
    }

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
        for (BlockPos pos : BlockPos.betweenClosed(worldPosition.offset(-getRange(),-1, -getRange()), worldPosition.offset(getRange(),-1,getRange()))) {
            positions.add(pos.immutable()); //Need to make it immutable
        }
    }

    //TODO handle inv full
    public void collectDrops(List<ItemStack> drops, @Nullable BlockPos soil) {
        for (ItemStack drop : drops) {
            if (soulData != null) { //TODO do this properly, how do we want this to work?
                if (level.random.nextFloat() > soulData.seeds()) {
                    drop.grow(2);
                }
            }
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

    @Override
    public void addConsumedPower(int power) {
        if (power > 0) {
            power = soulData == null ? power : (int) (power * soulData.power());
        }
        consumed += power;
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
                if (soulData == null || level.random.nextFloat() < soulData.bonemeal()) {
                    itemStack.shrink(1);
                }
                consumed = true;
                break;
            }
        }
        return consumed;
    }

    @Override
    public ItemStack getSeedsForPos(BlockPos pos) {
        return getSeedForPos(pos).getItemStack(this);
    }

    @Override
    public ItemStack getAxe() {
        return AXE.getItemStack(this);
    }

    @Override
    public ItemStack getHoe() {
        return HOE.getItemStack(this);
    }

    @Override
    public ItemStack getShears() {
        return SHEAR.getItemStack(this);
    }

    @Override
    public FakePlayer getPlayer() {
        return FARM_PLAYER;
    }

    @Override
    public int consumeEnergy(int energy, boolean simulate) {
        return getEnergyStorage().consumeEnergy(energy, simulate);
    }

    @Override
    public MachineTankLayout getTankLayout() {
        return new MachineTankLayout.Builder().tank(TANK, CAPACITY, f -> f.is(FluidTags.WATER)).build();
    }

    @Override
    public MachineFluidHandler getFluidHandler() {
        return fluidHandler;
    }

    @Override
    public MachineFluidHandler createFluidHandler() {
        return new MachineFluidHandler(getIOConfig(), getTankLayout()) {
            @Override
            protected void onContentsChanged(int slot) {
                onTankContentsChanged();
                setChanged();
                super.onContentsChanged(slot);
                updateMachineState(MachineState.EMPTY_TANK, TANK.getFluidAmount(this) <= 0);
            }
        };
    }

    private void onTankContentsChanged() {
        if (TANK.getTank(this).getFluidAmount() == TANK.getTank(this).getCapacity()) {
            if (ticket != null) {
                ticket.invalidate();
            }
            this.ticket = FarmlandWaterManager.addAABBTicket(this.level, new AABB(this.worldPosition).inflate(getRange()));
        } else {
            if (ticket != null) {
                ticket.invalidate();
                ticket = null;
            }
        }
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        if (ticket != null) {
            ticket.invalidate();
            ticket = null;
        }
    }

    public Optional<ResourceLocation> getEntityType() {
        return entityData.getEntityType();
    }

    public void setEntityType(ResourceLocation entityType) {
        entityData = StoredEntityData.of(entityType);
    }

    @SubscribeEvent
    static void onReload(RecipesUpdatedEvent event) {
        reload = !reload;
    }
}
