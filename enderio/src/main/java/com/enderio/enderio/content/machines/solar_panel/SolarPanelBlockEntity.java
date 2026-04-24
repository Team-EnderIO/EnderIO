package com.enderio.enderio.content.machines.solar_panel;

import com.enderio.enderio.api.soul.Soul;
import com.enderio.enderio.foundation.MachineNBTKeys;
import com.enderio.enderio.foundation.block.EIOBlockEntity;
import com.enderio.enderio.foundation.souldata.SolarSoul;
import com.enderio.enderio.foundation.tag.EIOTags;
import com.enderio.enderio.init.EIODataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.energy.IEnergyStorage;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class SolarPanelBlockEntity extends EIOBlockEntity {

    public static final ICapabilityProvider<SolarPanelBlockEntity, Direction, IEnergyStorage> ENERGY_STORAGE_PROVIDER = (
        be, side) -> side != Direction.UP ? be.energyStorage : null;
    
    private final ISolarPanelTier tier;
    private final SolarPanelNode node;
    private final SolarPanelEnergyStorage energyStorage;

    private final Map<Direction, BlockCapabilityCache<IEnergyStorage, Direction>> energyStorageCaches = new EnumMap<>(Direction.class);
    private final Set<IEnergyStorage> validPushTargetCache = new HashSet<>();
    private boolean isValidPushTargetCacheDirty = true;

    private Soul boundSoul = Soul.EMPTY;
    private SolarSoul.SoulData soulData;
    private static boolean reload = false;
    private boolean reloadCache = !reload;

    public SolarPanelBlockEntity(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState, ISolarPanelTier tier) {
        super(type, worldPosition, blockState);
        this.tier = tier;
        
        this.node = new SolarPanelNode(this);
        this.energyStorage = new SolarPanelEnergyStorage(this.node);
    }
    
    public ISolarPanelTier tier() {
        return tier;
    }

    // region Graph logic

    @Override
    public void setRemoved() {
        if (node.isValid()) {
            node.getNetwork().remove(node);
        }

        super.setRemoved();
    }

    @Override
    public void onLoad() {
        super.onLoad();

        // Create all energy caches
        if (level instanceof ServerLevel serverLevel) {
            for (Direction side : Direction.values()) {
                if (side == Direction.UP) {
                    continue;
                }

                energyStorageCaches.put(side, BlockCapabilityCache.create(
                    Capabilities.EnergyStorage.BLOCK,
                    serverLevel,
                    getBlockPos().relative(side),
                    side.getOpposite(),
                    () -> !isRemoved(),
                    () -> isValidPushTargetCacheDirty = true));
            }
        }

        for (Direction side : Direction.Plane.HORIZONTAL) {
            if (level.getBlockEntity(getBlockPos().relative(side)) instanceof SolarPanelBlockEntity panel) {
                node.getNetwork().connect(node, panel.node);
            }
        }
    }

    @Override
    public void neighborChanged(Block neighborBlock, BlockPos neighborPos) {
        super.neighborChanged(neighborBlock, neighborPos);

        if (!level.isClientSide()) {
            isValidPushTargetCacheDirty = true;
        }
    }

    Set<IEnergyStorage> getValidPushTargets() {
        if (isValidPushTargetCacheDirty) {
            validPushTargetCache.clear();
            for (Direction side : Direction.values()) {
                if (side == Direction.UP) {
                    continue;
                }

                var energyStorage = energyStorageCaches.get(side).getCapability();
                if (energyStorage != null && energyStorage.canReceive()) {
                    validPushTargetCache.add(energyStorage);
                }
            }
        }

        return validPushTargetCache;
    }

    // endregion

    // region Energy Generation

    @Override
    public void serverTick() {
        if (getGenerationRate() > 0) {
            node.addEnergyToNetwork(getGenerationRate());
        }

        if (reloadCache != reload && boundSoul.hasEntity()) {
            Optional<SolarSoul.SoulData> op = SolarSoul.SOLAR.matches(boundSoul.entityType());
            op.ifPresent(data -> soulData = data);
            reloadCache = reload;
        }

        // Push energy to non-panel neighbors
        if (node.isPrimaryNode()) {
            node.distributeEnergy();
        }

        super.serverTick();
    }

    /**
     * Calculates the generation rate for this solar panel.
     * Generates energy during day from 0 to  12_000 ticks (10 minute aka half a minecraft day)
     * Generates energy during day from 12_000 to  24_000 ticks (10 minute aka half a minecraft day)
     * Liquid Sunshine or/and Liquid Darkness make it always output at full power during the day/night.
     * Generation is scaled at the start and end of the day/night.
     *
     * @return this solar panels generation rate.
     * @see SolarPanelBlockEntity#hasLiquidSunshine()
     * @see SolarPanelBlockEntity#hasLiquidDarkness()
     */
    public int getGenerationRate() {
        if (level == null) {
            return 0;
        }

        //When should the panel make power
        boolean day = true;
        boolean night = false;
        if (soulData != null) {
            day = soulData.daytime();
            night = soulData.nighttime();
        }

        if ((day && night) || (day && hasLiquidSunshine()) || (night && hasLiquidDarkness())) {
            return tier.getProductionRate(); //Max power, ignore if it can see the sky
        }

        if (!this.level.dimensionType().hasSkyLight()) {
            if (soulData == null || soulData.level().isEmpty() || !soulData.level().get().equals(this.level.dimension())) {
                return 0; //No light in dimension and no soul to override it
            }
        }

        if (!this.level.canSeeSky(getBlockPos().above())) {
            return 0; //No sky, no power
        }

        float outputScale = 0;
        if (day) {
            outputScale = getOutputScale(level);
        } else if (night) {
            // A Day night cycle is 20 minutes, night is 10 minutes
            int dayTime = (int) (level.getDayTime() % GameTicks.DAY_IN_TICKS);

            // Over how long should generation scale up/down and the start/end of the night
            float rampTimeMinutes = 1.5f;
            float rampTimeTicks = GameTicks.MINUTE_IN_TICKS * rampTimeMinutes + GameTicks.MINUTE_IN_TICKS * 10;

            if (GameTicks.MINUTE_IN_TICKS * 10 > dayTime) {
                // During the day, no power
                outputScale = 0;
            } else if (GameTicks.MINUTE_IN_TICKS * 10 < dayTime && dayTime < rampTimeTicks) {
                // If in the ramp up period of the night, do a linear scale up
                outputScale = dayTime / rampTimeTicks;
            } else if (dayTime > (GameTicks.MINUTE_IN_TICKS * 20) - (GameTicks.MINUTE_IN_TICKS * rampTimeMinutes)) {
                // If in the ramp down period of the night, do a linear scale down
                int timeLeft = (GameTicks.MINUTE_IN_TICKS * 20) - dayTime;
                outputScale = timeLeft / rampTimeTicks;
            } else {
                // Rest of the night, full power
                outputScale = 1;
            }
        }
        return Mth.ceil(outputScale * tier.getProductionRate());
    }

    public static float getOutputScale(Level level) {

        float outputScale;
        // A Day night cycle is 20 minutes, daytime is 10 minutes
        int dayTime = (int) (level.getDayTime() % GameTicks.DAY_IN_TICKS);

        // Over how long should generation scale up/down and the start/end of the day
        float rampTimeMinutes = 1.5f;
        float rampTimeTicks = GameTicks.MINUTE_IN_TICKS * rampTimeMinutes;

        if (dayTime < rampTimeTicks) {
            // If in the ramp up period of the day, do a linear scale up
            outputScale = dayTime / rampTimeTicks;
        } else if (dayTime > (GameTicks.MINUTE_IN_TICKS * 10) - (GameTicks.MINUTE_IN_TICKS * rampTimeMinutes)) {
            // If in the ramp down period of the day, do a linear scale down
            int timeLeft = (GameTicks.MINUTE_IN_TICKS * 10) - dayTime;
            outputScale = timeLeft / rampTimeTicks;
        } else {
            // Rest of the day, full power
            outputScale = 1;
        }
        outputScale = adjustOutputForWeather(level, outputScale);

        return outputScale;
    }

    private static float adjustOutputForWeather(Level level, float outputScale) {
        if (level.isThundering()) {
            outputScale -= 0.7f;
        } else if (level.isRaining()) {
            outputScale -= 0.3f;
        }
        outputScale = Math.clamp(outputScale, 0, 1);
        return outputScale;
    }

    private boolean hasLiquidSunshine() {
        for (Direction direction : Direction.values()) {
            BlockState state = this.level.getBlockState(this.getBlockPos().relative(direction));
            if (state.getFluidState().is(EIOTags.Fluids.SOLAR_PANEL_LIGHT)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasLiquidDarkness() {
        for (Direction direction : Direction.values()) {
            BlockState state = this.level.getBlockState(this.getBlockPos().relative(direction));
            if (state.getFluidState().is(EIOTags.Fluids.SOLAR_PANEL_DARK)) {
                return true;
            }
        }
        return false;
    }

    private static final class GameTicks {
        static final int TICKS_PER_SECOND = 20;
        static final int MINUTE_IN_TICKS = TICKS_PER_SECOND * 60;
        static final int DAY_DURATION_MIN = 20;
        static final int DAY_IN_TICKS = DAY_DURATION_MIN * MINUTE_IN_TICKS;

        /**
         * Converts minutes to game ticks.
         * @see GameTicks#TICKS_PER_SECOND
         *
         * @param minutes to convert.
         * @return corresponding minutes in game ticks.
         */
        static int minutesToTicks(int minutes) {
            return minutes * 60 * TICKS_PER_SECOND;
        }
    }
    
    // endregion

    // region Serialization

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        boundSoul = Soul.parseOptional(lookupProvider, tag.getCompound(MachineNBTKeys.ENTITY_STORAGE));

        // Old serialization format
        if (tag.contains(MachineNBTKeys.ENERGY)) {
            var energyStorage = tag.getCompound(MachineNBTKeys.ENERGY);
            if (energyStorage.contains(MachineNBTKeys.ENERGY_STORED)) {
                this.node.setEnergyStored(energyStorage.getInt(MachineNBTKeys.ENERGY_STORED));
            }
        } else if (tag.contains(MachineNBTKeys.ENERGY_STORED)) {
            this.node.setEnergyStored(tag.getInt(MachineNBTKeys.ENERGY_STORED));
        }

        super.loadAdditional(tag, lookupProvider);
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        tag.put(MachineNBTKeys.ENTITY_STORAGE, boundSoul.saveOptional(lookupProvider));

        // TODO: 26.1 - serialize the node instead?
        tag.putInt(MachineNBTKeys.ENERGY_STORED, node.getEnergyStored());

        super.saveAdditional(tag, lookupProvider);
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput components) {
        super.applyImplicitComponents(components);
        boundSoul = components.getOrDefault(EIODataComponents.SOUL, Soul.EMPTY);
        node.setEnergyStored(components.getOrDefault(EIODataComponents.ENERGY, 0));
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);

        if (boundSoul.hasEntity()) {
            components.set(EIODataComponents.SOUL, boundSoul);
        }

        if (node.getEnergyStored() != 0) {
            components.set(EIODataComponents.ENERGY, node.getEnergyStored());
        }
    }

    // endregion
}
