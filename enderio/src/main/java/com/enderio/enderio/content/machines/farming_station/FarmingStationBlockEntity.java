package com.enderio.enderio.content.machines.farming_station;

import com.enderio.core.annotations.UseOnly;
import com.enderio.enderio.api.capacitor.CapacitorModifier;
import com.enderio.enderio.api.capacitor.QuadraticScalable;
import com.enderio.enderio.api.farm.FarmInteraction;
import com.enderio.enderio.api.farm.FarmTask;
import com.enderio.enderio.api.farm.FarmTaskManager;
import com.enderio.enderio.api.farm.FarmingMachine;
import com.enderio.enderio.api.io.energy.EnergyIOMode;
import com.enderio.enderio.api.soul.Soul;
import com.enderio.enderio.api.soul.binding.SoulBindable;
import com.enderio.enderio.config.machines.MachinesConfig;
import com.enderio.enderio.foundation.MachineNBTKeys;
import com.enderio.enderio.foundation.attachment.ActionRange;
import com.enderio.enderio.foundation.attachment.RangedActor;
import com.enderio.enderio.foundation.block.entity.PoweredMachineBlockEntity;
import com.enderio.enderio.foundation.block.entity.flags.CapacitorSupport;
import com.enderio.enderio.foundation.inventory.MachineInventoryLayout;
import com.enderio.enderio.foundation.inventory.MultiSlotAccess;
import com.enderio.enderio.foundation.inventory.SingleSlotAccess;
import com.enderio.enderio.foundation.souldata.FarmSoul;
import com.enderio.enderio.foundation.state.MachineState;
import com.enderio.enderio.init.EIOBlockEntities;
import com.enderio.enderio.init.EIODataComponents;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.common.SpecialPlantable;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FarmingStationBlockEntity extends PoweredMachineBlockEntity implements RangedActor, FarmingMachine, SoulBindable {
    private static final QuadraticScalable ENERGY_CAPACITY = new QuadraticScalable(CapacitorModifier.ENERGY_CAPACITY,
            MachinesConfig.COMMON.ENERGY.FARM_CAPACITY);
    private static final QuadraticScalable ENERGY_USAGE = new QuadraticScalable(CapacitorModifier.ENERGY_USE,
            MachinesConfig.COMMON.ENERGY.FARM_USAGE);

    private static final ActionRange DEFAULT_RANGE = new ActionRange(5, false);

    public static final MultiSlotAccess TOOLS = new MultiSlotAccess(); // Order - Axe, Hoe, Shears
    public static final MultiSlotAccess AREAS = new MultiSlotAccess(); // Order - NE, SE, SW, NW
    public static final MultiSlotAccess BONEMEAL = new MultiSlotAccess();
    public static final MultiSlotAccess OUTPUT = new MultiSlotAccess();

    private List<BlockPos> positions;
    private int currentIndex = 0;

    @Nullable
    private FarmTask currentTask = null;

    private Soul boundSoul = Soul.EMPTY;
    private FarmSoul.SoulData soulData;
    private static boolean reload = false;
    private boolean reloadCache = !reload;

    private ActionRange actionRange = DEFAULT_RANGE;

    @UseOnly(LogicalSide.SERVER)
    private FakePlayer farmPlayer;

    public FarmingStationBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(EIOBlockEntities.FARMING_STATION.get(), worldPosition, blockState, true, CapacitorSupport.REQUIRED,
                EnergyIOMode.Input, ENERGY_CAPACITY, ENERGY_USAGE);
    }

    @Override
    public ActionRange getActionRange() {
        return actionRange;
    }

    @Override
    public void setActionRange(ActionRange actionRange) {
        this.actionRange = actionRange.clamp(0, getMaxRange());
        updateLocations();
        setChanged();

        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    @Override
    public int getMaxRange() {
        return 5;
    }

    @Override
    protected @Nullable MachineInventoryLayout createInventoryLayout() {
        return MachineInventoryLayout.builder()
                .capacitor()
                .inputSlot(3, this::validToolForSlot)
                .slotAccess(TOOLS)
                .inputSlot(4)
                .slotAccess(AREAS)
                .inputSlot(2, (integer, stack) -> stack.is(Tags.Items.FERTILIZERS))
                .slotAccess(BONEMEAL)
                .outputSlot(6)
                .slotAccess(OUTPUT)
                .build();
    }

    @Override
    public void serverTick() {
        if (reloadCache != reload && boundSoul.hasEntity()) {
            Optional<FarmSoul.SoulData> op = FarmSoul.RELOAD_LISTENER.matches(boundSoul.entityType());
            op.ifPresent(data -> soulData = data);
            reloadCache = reload;
        }
        // TODO: this is quite icky. need abstractions between tick time and power consumption
        if (canAct(10) && getEnergyStorage().getAmountAsInt() >= getMaxEnergyUse() * 10) {
            processFarmTask();
            getEnergyStorage().consume(getMaxEnergyUse() * 10, null);
        }

        super.serverTick();
    }

    @Override
    public void clientTick() {
        if (level.isClientSide() && level instanceof ClientLevel clientLevel) {
            getActionRange().addClientParticle(clientLevel, getParticleLocation(),
                    MachinesConfig.CLIENT.BLOCKS.FARM_RANGE_COLOR.get());
        }

        super.clientTick();
    }

    private void processFarmTask() {
        int stop = Math.min(currentIndex + getRange(), positions.size());
        while (currentIndex < stop) {
            BlockPos soil = positions.get(currentIndex);
            if (currentTask != null) {
                // try process current task
                if (currentTask.process(soil, this) == FarmInteraction.IGNORED) {
                    currentTask = null; // Task is done or no longer valid
                }
            }
            // Look for a new task
            if(currentTask == null) {
                for (FarmTask task : FarmTaskManager.getTasks()) {
                    if (task.process(soil, this) != FarmInteraction.IGNORED) { // new task found
                        currentTask = task;
                        break;
                    }
                }
            }
            currentIndex++;
        }

        // All positions have been checked, restart
        if (stop == positions.size()) {
            currentIndex = 0;
        }
    }

    // FIXME: multislot access filters take global slot index and not local
    private boolean validToolForSlot(int index, ItemResource itemResource) {
        return switch(index) { // Order - Axes, Hoes, Shears - Check TOOLS slot access
            case 1 -> itemResource.is(ItemTags.AXES);
            case 2 -> itemResource.is(ItemTags.HOES);
            case 3 -> itemResource.is(Tags.Items.TOOLS_SHEAR);
            default -> false;
        };
    }

    public SingleSlotAccess getSeedForPos(BlockPos soil) {
        if (soil.getX() >= getBlockPos().getX() && soil.getZ() <= getBlockPos().getZ()) {
            return AREAS.get(0); //NE
        }
        if (soil.getX() >= getBlockPos().getX() && soil.getZ() > getBlockPos().getZ()) {
            return AREAS.get(1); //SE
        }
        if (soil.getX() < getBlockPos().getX() && soil.getZ() > getBlockPos().getZ()) {
            return AREAS.get(2); //SW
        }
        if (soil.getX() < getBlockPos().getX() && soil.getZ() <= getBlockPos().getZ()) {
            return AREAS.get(3); //NW
        }
        return AREAS.get(3);//NW
    }

    @Override
    public boolean isActive() {
        if (!canAct()) {
            return false;
        }
        // TODO Check tool
        return currentTask != null;
    }

    public BlockPos getParticleLocation() {
        return worldPosition.below();
    }

    private void updateLocations() {
        positions = new ArrayList<>();
        currentIndex = 0;
        for (BlockPos pos : BlockPos.betweenClosed(worldPosition.offset(-getRange(), -1, -getRange()),
                worldPosition.offset(getRange(), -1, getRange()))) {
            positions.add(pos.immutable()); // Need to make it immutable
        }
    }

    public boolean handleDrops(BlockState plant, BlockPos pos, BlockPos soil, BlockEntity blockEntity,
            ItemStack stack) {
        ItemStack dummy = stack.copy();
        if (soulData != null) {
            var enchantmentsRecipe = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
            var fortuneEnchantment = enchantmentsRecipe.getOrThrow(Enchantments.FORTUNE);
            dummy.enchant(fortuneEnchantment, dummy.getEnchantmentLevel(fortuneEnchantment) + soulData.seeds());
        }
        List<ItemStack> drops = Block.getDrops(plant, (ServerLevel) this.level, pos, blockEntity, getPlayer(), dummy);
        return collectDrops(drops, soil);
    }

    // TODO handle inv full
    public boolean collectDrops(List<ItemStack> drops, @Nullable BlockPos soil) {
        try (Transaction transaction = Transaction.openRoot()) {
            for (ItemStack drop : drops) {
                if (soil != null) {
                    SingleSlotAccess seedForPos = getSeedForPos(soil);
                    ItemStack seeds = seedForPos.getItemStack(this);
                    if (seeds.isEmpty()) {
                        if (drop.getItem() instanceof BlockItem || drop.getItem() instanceof SpecialPlantable) {
                            // Collect potential seeds
                            int amount = seedForPos.insert(this, ItemResource.of(drop), drop.getCount(), transaction);
                            drop.shrink(amount);
                            continue;
                        }
                    } else if (ItemStack.isSameItem(drop, seeds)) {
                        int amount = seedForPos.insert(this, ItemResource.of(drop), drop.getCount(), transaction);
                        drop.shrink(amount);
                    }
                }

                for (int i = 0; i < 6; i++) {
                    if (drop.isEmpty()) {
                        continue;
                    }

                    int amount = OUTPUT.get(i).insert(this, ItemResource.of(drop), drop.getCount(), transaction);
                    drop.shrink(amount);
                }

                if (!drop.isEmpty()) {
                    updateMachineState(MachineState.FULL_OUTPUT, true);
                    return false;
                }
            }

            transaction.commit();
            return true;
        }
    }

    public boolean consumeBonemeal() {
        boolean consumed = false;
        for (int i = 0; i < 2; i++) {
            ItemStack itemStack = BONEMEAL.get(i).getItemStack(this);
            if (!itemStack.isEmpty()) {
                if (soulData == null || level.getRandom().nextFloat() < soulData.bonemeal()) {
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
        var stack = getSeedForPos(pos).getItemStack(this);
        if (stack.getCount() > 1) // leave one item in the slot
            return stack;
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack getAxe() {
        return TOOLS.get(0).getItemStack(this);
    }

    @Override
    public ItemStack getHoe() {
        return TOOLS.get(1).getItemStack(this);
    }

    @Override
    public ItemStack getShears() {
        return TOOLS.get(2).getItemStack(this);
    }

    @UseOnly(LogicalSide.SERVER)
    @Override
    public FakePlayer getPlayer() {
        if (!(level instanceof ServerLevel serverLevel)) {
            throw new IllegalStateException("Level is null");
        }

        if (farmPlayer == null) {
            farmPlayer = new FakePlayer(serverLevel, new GameProfile(getMachineOwnerOrRandom(), "enderio:farm:" + worldPosition));
            farmPlayer.setPos(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ());
        }

        return farmPlayer;
    }

    @Override
    public BlockPos getPosition() {
        return getBlockPos();
    }

    @Override
    public int getFarmingRange() {
        return getRange();
    }

    @Nullable
    public EntityType<?> getEntityType() {
        return boundSoul.hasEntity() ? boundSoul.entityType() : null;
    }

    @Override
    public Soul getBoundSoul() {
        return boundSoul;
    }

    @Override
    public boolean canBind() {
        return true;
    }

    @Override
    public boolean isSoulValid(Soul soul) {
        return FarmSoul.RELOAD_LISTENER.matches(soul.entityTypeId()).isPresent();
    }

    @Override
    public void bindSoul(Soul newSoul) {
        this.boundSoul = newSoul;
        this.soulData = FarmSoul.RELOAD_LISTENER.matches(newSoul.entityTypeId()).get();
    }

    @SubscribeEvent
    static void onReload(OnDatapackSyncEvent event) {
        reload = !reload;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new FarmingStationMenu(containerId, playerInventory, this);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        updateLocations();
    }

    @Override
    protected void saveAdditionalSynced(ValueOutput output) {
        super.saveAdditionalSynced(output);

        if (!actionRange.equals(DEFAULT_RANGE)) {
            output.store(MachineNBTKeys.ACTION_RANGE, ActionRange.CODEC, this.actionRange);
        }

        output.store(MachineNBTKeys.ENTITY_STORAGE, Soul.OPTIONAL_CODEC, boundSoul);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);

        actionRange = input.read(MachineNBTKeys.ACTION_RANGE, ActionRange.CODEC)
            .orElse(DEFAULT_RANGE);

        boundSoul = input.read(MachineNBTKeys.ENTITY_STORAGE, Soul.OPTIONAL_CODEC).orElse(Soul.EMPTY);
    }

    @Override
    protected void applyImplicitComponents(DataComponentGetter components) {
        super.applyImplicitComponents(components);

        var actionRange = components.get(EIODataComponents.ACTION_RANGE);
        if (actionRange != null) {
            this.actionRange = actionRange;
        }

        boundSoul = components.getOrDefault(EIODataComponents.SOUL, Soul.EMPTY);
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);

        // Only if unchanged.
        if (!actionRange.equals(DEFAULT_RANGE)) {
            components.set(EIODataComponents.ACTION_RANGE, actionRange);
        }

        if (boundSoul.hasEntity()) {
            components.set(EIODataComponents.SOUL, boundSoul);
        }
    }
}
