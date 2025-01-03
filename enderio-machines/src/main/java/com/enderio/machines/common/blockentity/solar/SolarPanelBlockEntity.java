package com.enderio.machines.common.blockentity.solar;

import static com.enderio.machines.common.blocks.powered_spawner.PoweredSpawnerBlockEntity.NO_MOB;

import com.enderio.base.api.attachment.StoredEntityData;
import com.enderio.base.api.capacitor.FixedScalable;
import com.enderio.base.api.io.IOMode;
import com.enderio.base.api.io.energy.EnergyIOMode;
import com.enderio.base.common.init.EIODataComponents;
import com.enderio.base.common.tag.EIOTags;
import com.enderio.core.common.network.NetworkDataSlot;
import com.enderio.machines.common.MachineNBTKeys;
import com.enderio.machines.common.blockentity.base.LegacyPoweredMachineBlockEntity;
import com.enderio.machines.common.blockentity.multienergy.MultiEnergyNode;
import com.enderio.machines.common.blockentity.multienergy.MultiEnergyStorageWrapper;
import com.enderio.machines.common.init.MachineBlockEntities;
import com.enderio.machines.common.io.IOConfig;
import com.enderio.machines.common.io.energy.MachineEnergyStorage;
import com.enderio.machines.common.souldata.SolarSoul;
import dev.gigaherz.graph3.Graph;
import dev.gigaherz.graph3.GraphObject;
import dev.gigaherz.graph3.Mergeable;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RecipesUpdatedEvent;
import org.jetbrains.annotations.Nullable;

public class SolarPanelBlockEntity extends LegacyPoweredMachineBlockEntity {

    private final ISolarPanelTier tier;

    private final MultiEnergyNode node;

    private StoredEntityData entityData = StoredEntityData.EMPTY;
    private SolarSoul.SoulData soulData;
    private static boolean reload = false;
    private boolean reloadCache = !reload;

    public SolarPanelBlockEntity(BlockPos worldPosition, BlockState blockState, SolarPanelTier tier) {
        super(EnergyIOMode.Output, new FixedScalable(tier::getStorageCapacity),
                new FixedScalable(tier::getStorageCapacity), MachineBlockEntities.SOLAR_PANELS.get(tier).get(),
                worldPosition, blockState);

        this.tier = tier;
        this.node = new MultiEnergyNode(() -> energyStorage,
                () -> (MultiEnergyStorageWrapper) getExposedEnergyStorage(), worldPosition);
        addDataSlot(NetworkDataSlot.RESOURCE_LOCATION.create(() -> this.getEntityType().orElse(NO_MOB),
                this::setEntityType));
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return null;
    }

    @Override
    public @Nullable MachineEnergyStorage createExposedEnergyStorage() {
        return new MultiEnergyStorageWrapper(this, EnergyIOMode.Output, () -> tier);
    }

    @Override
    public void serverTick() {
        if (isGenerating()) {
            getEnergyStorage().addEnergy(getGenerationRate());
        }
        if (reloadCache != reload && entityData.hasEntity()) {
            Optional<SolarSoul.SoulData> op = SolarSoul.SOLAR.matches(entityData.entityType().get());
            op.ifPresent(data -> soulData = data);
            reloadCache = reload;
        }

        super.serverTick();
    }

    @Override
    protected boolean isActive() {
        return canAct() && hasEnergy() && isGenerating();
    }

    public boolean isGenerating() {
        if (level == null || !this.level.canSeeSky(getBlockPos().above())) {
            return false;
        }
        if (!this.level.dimensionType().hasSkyLight()) {
            return soulData == null
                    || (soulData.level().isPresent() && !soulData.level().get().equals(this.level.dimension()));
        }

        return getGenerationRate() > 0;
    }

    /**
     * Calculates the generation rate for this solar panel.
     * Only generates energy during day before 12_000 ticks (10 minute aka half a minecraft day), or
     * if its day and either night or if it hasLiquidSunshine.
     * @see SolarPanelBlockEntity#hasLiquidSunshine()
     *
     * @return this solar panels generation rate.
     */
    public int getGenerationRate() {
        if (level == null) {
            return 0;
        }

        boolean day;
        boolean night;
        if (soulData != null) {
            day = soulData.daytime();
            night = soulData.nighttime();
        } else {
            day = level.isDay();
            night = level.isNight();
        }

        float progress = 0;
        if (day && (night || hasLiquidSunshine())) {
            progress = 1;
        } else if (day) {
            int dayTime = (int) (level.getDayTime() % GameTicks.DAY_IN_TICKS);
            if (dayTime > GameTicks.minutesToTicks(12)) {
                return 0;
            }
            progress = dayTime > GameTicks.minutesToTicks(5) ? GameTicks.minutesToTicks(10) - dayTime : dayTime;
            progress = (progress - GameTicks.MINUTE_IN_TICKS) / GameTicks.minutesToTicks(4);
        } else if (night) {
            return 0;
        }

        double easing = easing(progress);

        if (level.isThundering()) {
            easing -= 0.7f;
        } else if (level.isRaining()) {
            easing -= 0.3f;
        }

        if (easing < 0) {
            return 0;
        }

        return (int) (easing * tier.getProductionRate());
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

    @Override
    protected boolean shouldPushEnergyTo(Direction direction) {
        if (node.getGraph() == null) {
            return true;
        }

        for (GraphObject<Mergeable.Dummy> neighbour : node.getGraph().getNeighbours(node)) {
            if (neighbour instanceof MultiEnergyNode neighbourMultiEnergyNode) {
                if (neighbourMultiEnergyNode.pos.equals(worldPosition.relative(direction))) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public void setRemoved() {
        if (node.getGraph() != null) {
            node.getGraph().remove(node);
        }

        super.setRemoved();
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (node.getGraph() == null) {
            Graph.integrate(node, List.of());
        }

        for (Direction direction : new Direction[] { Direction.NORTH, Direction.EAST, Direction.SOUTH,
                Direction.WEST }) {
            if (level.getBlockEntity(worldPosition.relative(direction)) instanceof SolarPanelBlockEntity panel
                    && panel.tier == tier) {
                Graph.connect(node, panel.node);
            }
        }
    }

    // Reference: EaseInOutQuad Function
    private static double easing(float progress) {
        if (progress > 0.5f) {
            return 1 - Math.pow(-2 * progress + 2, 2) / 2;
        }

        return 2 * progress * progress;
    }

    @Override
    public IOConfig getDefaultIOConfig() {
        return IOConfig.of(dir -> dir == Direction.UP ? IOMode.NONE : IOMode.PUSH);
    }

    @Override
    public boolean isIOConfigMutable() {
        return false;
    }

    @Override
    public boolean canOpenMenu() {
        return false;
    }

    @Override
    public void loadAdditional(CompoundTag pTag, HolderLookup.Provider lookupProvider) {
        entityData = StoredEntityData.parseOptional(lookupProvider, pTag.getCompound(MachineNBTKeys.ENTITY_STORAGE));

        super.loadAdditional(pTag, lookupProvider);
    }

    @Override
    public void saveAdditional(CompoundTag pTag, HolderLookup.Provider lookupProvider) {
        pTag.put(MachineNBTKeys.ENTITY_STORAGE, entityData.saveOptional(lookupProvider));

        super.saveAdditional(pTag, lookupProvider);
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput components) {
        super.applyImplicitComponents(components);
        entityData = components.getOrDefault(EIODataComponents.STORED_ENTITY, StoredEntityData.EMPTY);
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);

        if (entityData.hasEntity()) {
            components.set(EIODataComponents.STORED_ENTITY, entityData);
        }
    }

    public Optional<ResourceLocation> getEntityType() {
        return entityData.entityType();
    }

    public void setEntityType(ResourceLocation entityType) {
        entityData = StoredEntityData.of(entityType);
    }

    @SubscribeEvent
    static void onReload(RecipesUpdatedEvent event) {
        reload = !reload;
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
}
