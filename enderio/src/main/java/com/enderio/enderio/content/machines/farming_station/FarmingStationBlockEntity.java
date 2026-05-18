package com.enderio.enderio.content.machines.farming_station;

import com.enderio.core.annotations.UseOnly;
import com.enderio.core.common.storage.layout.ItemStorageLayout;
import com.enderio.core.common.storage.layout.SlotTemplates;
import com.enderio.core.common.storage.slot.MultiResourceSlotKey;
import com.enderio.core.common.storage.slot.ResourceSlotId;
import com.enderio.core.common.storage.slot.SingleResourceSlotKey;
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
import com.enderio.enderio.foundation.inventory.MachineSlotTemplates;
import com.enderio.enderio.foundation.souldata.FarmSoul;
import com.enderio.enderio.foundation.state.MachineState;
import com.enderio.enderio.init.EIOBlockEntities;
import com.enderio.enderio.init.EIODataComponents;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
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

    public static final MultiResourceSlotKey<ItemResource> TOOLS = new MultiResourceSlotKey<>(3); // Order - Axe, Hoe, Shears
    public static final MultiResourceSlotKey<ItemResource> AREAS = new MultiResourceSlotKey<>(4); // Order - NE, SE, SW, NW
    public static final MultiResourceSlotKey<ItemResource> BONEMEAL = new MultiResourceSlotKey<>(2);
    public static final MultiResourceSlotKey<ItemResource> OUTPUT = new MultiResourceSlotKey<>(6);
    public static final SingleResourceSlotKey<ItemResource> CAPACITOR = new SingleResourceSlotKey<>();

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
        super(EIOBlockEntities.FARMING_STATION.get(), worldPosition, blockState, true, CapacitorSupport.REQUIRED, CAPACITOR,
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
    protected @Nullable ItemStorageLayout createInventoryLayout() {
        return ItemStorageLayout.builder()
            .add(CAPACITOR, MachineSlotTemplates.capacitor())
            .add(TOOLS, SlotTemplates.input(64), b -> b
                .filter(this::validToolForSlot))
            .add(AREAS, SlotTemplates.input(64))
            .add(BONEMEAL, SlotTemplates.input(64), b -> b
                .filter((_, itemResource) -> itemResource.is(Tags.Items.FERTILIZERS)))
            .add(OUTPUT, SlotTemplates.output(64))
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
        if (canAct(10) && hasEnergy() && getEnergyStorage().getAmountAsInt() >= getMaxEnergyUse() * 10) {
            processFarmTask();
            getEnergyStorage().consume(getMaxEnergyUse() * 10, null);
        }

        super.serverTick();
    }

    @Override
    public void clientTick() {
        if (level.isClientSide() && level instanceof ClientLevel clientLevel) {
            getActionRange().addClientParticle(clientLevel, getParticleLocation(),
                    MachinesConfig.CLIENT.BLOCKS.DRAIN_RANGE_COLOR.get());
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

    public ResourceSlotId<ItemResource> getSeedForPos(BlockPos soil) {
        if (soil.getX() >= getBlockPos().getX() && soil.getZ() <= getBlockPos().getZ()) {
            return AREAS.slot(0); //NE
        }
        if (soil.getX() >= getBlockPos().getX() && soil.getZ() > getBlockPos().getZ()) {
            return AREAS.slot(1); //SE
        }
        if (soil.getX() < getBlockPos().getX() && soil.getZ() > getBlockPos().getZ()) {
            return AREAS.slot(2); //SW
        }
        if (soil.getX() < getBlockPos().getX() && soil.getZ() <= getBlockPos().getZ()) {
            return AREAS.slot(3); //NW
        }
        return AREAS.slot(3);//NW
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

    public boolean handleDrops(BlockState plant, BlockPos pos, BlockPos soil, BlockEntity blockEntity, ItemResource resource) {
        ItemStack dummy = resource.toStack();
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
        var inventory = getInventory();
        try (Transaction transaction = Transaction.openRoot()) {
            for (ItemStack drop : drops) {
                if (soil != null) {
                    var seedSlot = getSeedForPos(soil);
                    ItemStack seeds = inventory.getStack(seedSlot);
                    if (seeds.isEmpty()) {
                        if (drop.getItem() instanceof BlockItem || drop.getItem() instanceof SpecialPlantable) {
                            // Collect potential seeds
                            int amount = inventory.insert(seedSlot, ItemResource.of(drop), drop.getCount(), transaction);
                            drop.shrink(amount);
                            continue;
                        }
                    } else if (ItemStack.isSameItem(drop, seeds)) {
                        int amount = inventory.insert(seedSlot, ItemResource.of(drop), drop.getCount(), transaction);
                        drop.shrink(amount);
                    }
                }

                for (var outputSlot : OUTPUT) {
                    if (drop.isEmpty()) {
                        continue;
                    }

                    int amount = inventory.insert(outputSlot, ItemResource.of(drop), drop.getCount(), transaction);
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
        try (Transaction transaction = Transaction.openRoot()) {
            for (var boneMealSlot : BONEMEAL) {
                var resource = getInventory().getResource(boneMealSlot);
                if (resource.isEmpty()) {
                    continue;
                }

                int extract = getInventory().extract(boneMealSlot, resource, 1, transaction);
                if (extract != 1) {
                    continue;
                }

                // Only commit to consumption if we were supposed to consume it
                if (soulData == null || level.getRandom().nextFloat() < soulData.bonemeal()) {
                    transaction.commit();
                }

                return true;
            }
        }

        return false;
    }

    @Override
    public ResourceSlotId<ItemResource> seeds(BlockPos pos) {
        return getSeedForPos(pos);
    }

    @Override
    public ResourceSlotId<ItemResource> axe() {
        return TOOLS.slot(0);
    }

    @Override
    public ResourceSlotId<ItemResource> hoe() {
        return TOOLS.slot(1);
    }

    @Override
    public ResourceSlotId<ItemResource> shears() {
        return TOOLS.slot(2);
    }

    @Override
    public ItemResource getResource(ResourceSlotId<ItemResource> slot) {
        return getInventory().getResource(slot);
    }

    @Override
    public InteractionResult useStack(BlockPos soil, ItemResource resource, ResourceSlotId<ItemResource> slot) {
        ItemStack stack = resource.toStack();
        getPlayer().setItemInHand(InteractionHand.MAIN_HAND, stack);
        UseOnContext context = new UseOnContext(getPlayer(), InteractionHand.MAIN_HAND,
            new BlockHitResult(Vec3.atBottomCenterOf(soil), Direction.UP, soil, false));
        InteractionResult result = stack.useOn(context);
        getPlayer().setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);

        ItemResource damagedTool = ItemResource.of(stack);
        try (Transaction transaction = Transaction.openRoot()) {
            int extracted = getInventory().extract(slot, getResource(slot), 1, transaction);
            if (extracted != 1) {
                return InteractionResult.FAIL;
            }

            if (!damagedTool.isEmpty()) {
                int inserted = getInventory().insert(slot, damagedTool, 1, transaction);
                if (inserted != 1) {
                    return InteractionResult.FAIL;
                }
            }
            transaction.commit();
        }
        return result;
    }

    @Override
    public void mineBlock(ResourceSlotId<ItemResource> slot, BlockState state, BlockPos pos) {
        ItemStack tool = getResource(slot).toStack();
        if (tool.isEmpty()) {
            return;
        }

        tool.mineBlock(level, state, pos, getPlayer());
        ItemResource damagedTool = ItemResource.of(tool);
        try (Transaction transaction = Transaction.openRoot()) {
            int extracted = getInventory().extract(slot, getResource(slot), 1, transaction);
            if (extracted != 1) {
                return;
            }

            if (!damagedTool.isEmpty()) {
                int inserted = getInventory().insert(slot, damagedTool, 1, transaction);
                if (inserted != 1) {
                    return;
                }
            }
            transaction.commit();
        }
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
        reloadCache = !reload;
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

        boundSoul = input.read(MachineNBTKeys.ENTITY_STORAGE, Soul.OPTIONAL_CODEC)
            .orElse(Soul.EMPTY);
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
