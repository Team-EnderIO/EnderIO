package com.enderio.enderio.content.machines.soul_engine;

import com.enderio.core.common.storage.FluidStorage;
import com.enderio.core.common.storage.layout.FluidStorageLayout;
import com.enderio.core.common.storage.layout.ItemStorageLayout;
import com.enderio.core.common.storage.layout.SlotTemplates;
import com.enderio.core.common.storage.slot.SingleResourceSlotKey;
import com.enderio.enderio.api.capacitor.CapacitorModifier;
import com.enderio.enderio.api.capacitor.scaling.FixedIntScalable;
import com.enderio.enderio.api.capacitor.scaling.LinearIntScalable;
import com.enderio.enderio.api.capacitor.scaling.QuadraticIntScalable;
import com.enderio.enderio.api.io.energy.EnergyIOMode;
import com.enderio.enderio.api.soul.Soul;
import com.enderio.enderio.api.soul.binding.SoulBindable;
import com.enderio.enderio.config.machines.MachinesConfig;
import com.enderio.enderio.foundation.MachineNBTKeys;
import com.enderio.enderio.foundation.block.ProgressMachineBlock;
import com.enderio.enderio.foundation.block.entity.PoweredMachineBlockEntity;
import com.enderio.enderio.foundation.block.entity.flags.CapacitorSupport;
import com.enderio.enderio.foundation.inventory.MachineSlotTemplates;
import com.enderio.enderio.foundation.souldata.EngineSoul;
import com.enderio.enderio.foundation.state.MachineState;
import com.enderio.enderio.foundation.storage.SidedResourceHandler;
import com.enderio.enderio.init.EIOBlockEntities;
import com.enderio.enderio.init.EIODataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.energy.EnergyHandlerUtil;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

@EventBusSubscriber
public class SoulEngineBlockEntity extends PoweredMachineBlockEntity implements SoulBindable {

    public static final ICapabilityProvider<SoulEngineBlockEntity, Direction, ResourceHandler<FluidResource>> FLUID_HANDLER_PROVIDER = (be,
        side) -> be.fluidStorage != null ? SidedResourceHandler.of(be.fluidStorage, side, be) : null;

    private static final QuadraticIntScalable CAPACITY = new QuadraticIntScalable(CapacitorModifier.ENERGY_CAPACITY,
            MachinesConfig.COMMON.ENERGY.SOUL_ENGINE_CAPACITY);
    public static final LinearIntScalable BURN_SPEED = new LinearIntScalable(CapacitorModifier.FIXED,
            MachinesConfig.COMMON.ENERGY.SOUL_ENGINE_BURN_SPEED);
    // TODO capacitor increase efficiency
    public static final LinearIntScalable GENERATION_SPEED = new LinearIntScalable(CapacitorModifier.FIXED, () -> 1);

    private static final String BURNED_TICKS = "BurnedTicks";

    public static final SingleResourceSlotKey<ItemResource> CAPACITOR = new SingleResourceSlotKey<>();

    private Soul boundSoul = Soul.EMPTY;
    public static final int FLUID_CAPACITY = 2 * FluidType.BUCKET_VOLUME;

    public static final SingleResourceSlotKey<FluidResource> TANK = new SingleResourceSlotKey<>();

    private final FluidStorage fluidStorage;

    private EngineSoul.SoulData soulData;
    private int burnedTicks = 0;
    private static boolean reload = false;
    private boolean reloadCache = !reload;

    public SoulEngineBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(EIOBlockEntities.SOUL_ENGINE.get(), worldPosition, blockState, true, CapacitorSupport.REQUIRED, CAPACITOR,
                EnergyIOMode.Output, CAPACITY, FixedIntScalable.ZERO);

        var fluidStorageLayout = FluidStorageLayout.builder()
            .add(TANK, SlotTemplates.storage(), slot -> slot
                .capacity(FLUID_CAPACITY)
                .filter((_, resource) -> isFluidValid(resource.toStack(1))))
            .build();

        fluidStorage = new FluidStorage(fluidStorageLayout) {
            @Override
            protected void onContentsChanged(int index, FluidStack previousContents) {
                super.onContentsChanged(index, previousContents);
                updateMachineState(MachineState.EMPTY_TANK, fluidStorage.getAmountAsInt(TANK) <= 0);
                setChanged();
            }

            @Override
            public int insert(int index, FluidResource resource, int amount, net.neoforged.neoforge.transfer.transaction.TransactionContext transaction) {
                // Convert into tagged fluid - allow any valid fluid type to be inserted but normalize to the current fluid
                if (isValid(index, resource)) {
                    var currentFluid = getResource(index);
                    if (currentFluid.getFluid() == Fluids.EMPTY || resource.getFluid().isSame(currentFluid.getFluid())) {
                        return super.insert(index, resource, amount, transaction);
                    } else {
                        // Insert the same amount but as the current fluid type
                        return super.insert(index, currentFluid, amount, transaction);
                    }
                }

                // Non-tagged fluid.
                return 0;
            }
        };
    }

    @Override
    public ItemStorageLayout createInventoryLayout() {
        return ItemStorageLayout.builder()
            .add(CAPACITOR, MachineSlotTemplates.capacitor())
            .build();
    }

    @Override
    public void serverTick() {
        if (reloadCache != reload && boundSoul.hasEntity()) {
            Optional<EngineSoul.SoulData> op = EngineSoul.RELOAD_LISTENER.matches(boundSoul.entityType());
            op.ifPresent(data -> soulData = data);
            reloadCache = reload;
        }

        if (soulData != null && isActive()) {
            producePower();
        }

        if (canAct(20)) {
            updateMachineState(MachineState.FULL_POWER, EnergyHandlerUtil.isFull(getEnergyStorage()) && isCapacitorInstalled());
        }

        updateMachineState(MachineState.NOT_SOULBOUND, soulData == null || boundSoul.entityType() != null);
        super.serverTick();
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (state.getValue(ProgressMachineBlock.POWERED)) {
            double x = pos.getX() + 0.5;
            double y = pos.getY();
            double z = pos.getZ() + 0.5;

            Direction direction = state.getValue(ProgressMachineBlock.FACING);
            Direction.Axis axis = direction.getAxis();
            double r = 0.52;
            double ss = random.nextDouble() * 0.6 - 0.3;
            double dx = axis == Direction.Axis.X ? direction.getStepX() * r : ss;
            double dy = random.nextDouble() * 6.0 / 16.0;
            double dz = axis == Direction.Axis.Z ? direction.getStepZ() * r : ss;
            level.addParticle(ParticleTypes.SMOKE, x + dx, y + dy, z + dz, 0.0, 0.0, 0.0);
            level.addParticle(ParticleTypes.SOUL_FIRE_FLAME, x + dx, y + dy, z + dz, 0.0, 0.0, 0.0);
        }
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
        return EngineSoul.RELOAD_LISTENER.matches(soul.entityTypeId()).isPresent();
    }

    @Override
    public void bindSoul(Soul newSoul) {
        this.boundSoul = newSoul;
        this.soulData = EngineSoul.RELOAD_LISTENER.matches(newSoul.entityTypeId()).get();
    }

    @Override
    public boolean isActive() {
        return canAct() && fluidStorage.getAmountAsInt(TANK) > 0;
    }

    public void producePower() {
        if (burnedTicks >= soulData.tickpermb()) {
            int energy = (int) (soulData.powerpermb() * getGenerationRate());

            try (Transaction transaction = Transaction.openRoot()) {
                if (fluidStorage.getStack(TANK).isEmpty()) {
                    return;
                }

                if (getEnergyStorage().add(energy, transaction) != energy) {
                    return;
                }

                // Drain 1mb of fluid
                int tankIndex = fluidStorage.layout().indexOf(TANK);
                FluidStack currentFluid = fluidStorage.getStack(TANK);
                fluidStorage.extract(tankIndex, FluidResource.of(currentFluid), 1, transaction);

                transaction.commit();
                burnedTicks -= soulData.tickpermb();
            }
        } else {
            burnedTicks += getBurnRate();
        }
    }

    public int getBurnRate() {
        return BURN_SPEED.scaled(this::getCapacitorData).get();
    }

    public float getGenerationRate() {
        // TODO return GENERATION_SPEED.scaleF(this::getCapacitorData).get();
        return MachinesConfig.COMMON.ENERGY.SOUL_ENGINE_BURN_SPEED.get();
    }

    public FluidStack getStoredFluid() {
        return fluidStorage.getStack(TANK);
    }

    private boolean isFluidValid(FluidStack fluidStack) {
        if (soulData == null || level == null) {
            return false;
        }

        // TODO: Soul Data should be able to store holders.
        String fluid = soulData.fluid();
        if (fluid.startsWith("#")) { // We have a fluid tag instead
            TagKey<Fluid> tag = TagKey.create(Registries.FLUID, Identifier.parse(fluid.substring(1)));
            return fluidStack.is(tag);
        } else {
            Optional<Holder.Reference<Fluid>> delegate = level.registryAccess().lookupOrThrow(Registries.FLUID)
                    .get(ResourceKey.create(Registries.FLUID, Identifier.parse(fluid)));
            if (delegate.isPresent()) {
                return fluidStack.getFluid().isSame(delegate.get().value());
            }
        }
        return false;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new SoulEngineMenu(containerId, playerInventory, this);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt(BURNED_TICKS, burnedTicks);
        output.putChild("Fluid", fluidStorage);
    }

    @Override
    protected void saveAdditionalSynced(ValueOutput output) {
        super.saveAdditionalSynced(output);
        output.store(MachineNBTKeys.ENTITY_STORAGE, Soul.OPTIONAL_CODEC, boundSoul);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        burnedTicks = input.getIntOr(BURNED_TICKS, 0);
        var bound = input.read(MachineNBTKeys.ENTITY_STORAGE, Soul.OPTIONAL_CODEC);
        bound.ifPresent(s -> this.boundSoul = s);

        updateMachineState(MachineState.NO_POWER, false);
        updateMachineState(MachineState.FULL_POWER,EnergyHandlerUtil.isFull(getEnergyStorage()) && isCapacitorInstalled());

        input.child("Fluid").ifPresent(fluidStorage::deserialize);
    }

    @Override
    protected void applyImplicitComponents(DataComponentGetter components) {
        super.applyImplicitComponents(components);
        boundSoul = components.getOrDefault(EIODataComponents.SOUL, Soul.EMPTY);

        SimpleFluidContent storedFluid = components.get(EIODataComponents.ITEM_FLUID_CONTENT);
        if (storedFluid != null) {
            fluidStorage.setStack(TANK, storedFluid.copy());
        }
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);

        if (boundSoul.hasEntity()) {
            components.set(EIODataComponents.SOUL, boundSoul);
        }

        var tank = fluidStorage.getStack(TANK);
        if (!tank.isEmpty()) {
            components.set(EIODataComponents.ITEM_FLUID_CONTENT, SimpleFluidContent.copyOf(tank));
        }
    }

    @Override
    public void setLevel(Level level) {
        super.setLevel(level);

        updateMachineState(MachineState.NO_POWER, false);
        updateMachineState(MachineState.FULL_POWER,EnergyHandlerUtil.isFull(getEnergyStorage()) && isCapacitorInstalled());
    }

    @SubscribeEvent
    static void onReload(OnDatapackSyncEvent event) {
        reload = !reload;
    } //TODO verify this loads properly
}
