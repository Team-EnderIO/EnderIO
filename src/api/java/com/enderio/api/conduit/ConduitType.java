package com.enderio.api.conduit;

import com.enderio.api.conduit.ticker.ConduitTicker;
import com.enderio.api.conduit.upgrade.ConduitUpgrade;
import com.enderio.api.filter.ResourceFilter;
import com.enderio.api.misc.RedstoneControl;
import com.enderio.api.registry.EnderIORegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Consumer;

public abstract class ConduitType<T extends ConduitData<T>> {

    public abstract ConduitTicker<T> getTicker();

    public abstract ConduitMenuData getMenuData();

    public abstract T createConduitData(Level level, BlockPos pos);

    /**
     * Override this method if your conduit type and your conduit item registry name don't match
     * @return the conduit item that holds this type
     */
    public Item getConduitItem() {
        return ForgeRegistries.ITEMS.getValue(ConduitApi.INSTANCE.getConduitTypeRegistry().getKey(this));
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean canBeInSameBlock(ConduitType<?> other) {
        return true;
    }

    public boolean canBeReplacedBy(ConduitType<?> other) {
        return false;
    }

    public boolean canApplyUpgrade(SlotType slotType, ConduitUpgrade conduitUpgrade) {
        return false;
    }

    public boolean canApplyFilter(SlotType slotType, ResourceFilter resourceFilter) {
        return false;
    }

    public <K> Optional<LazyOptional<K>> proxyCapability(Capability<K> cap, T extendedConduitData, Level level, BlockPos pos, @Nullable Direction direction,
        @Nullable ConduitNode.IOState state) {
        return Optional.empty();
    }

    /**
     * @param level the level
     * @param pos conduit position
     * @param direction direction the conduit connects to
     * @return the connectiondata that should be set on connection based on context
     */
    public ConduitConnectionData getDefaultConnection(Level level, BlockPos pos, Direction direction) {
        return new ConduitConnectionData(false, true, RedstoneControl.NEVER_ACTIVE);
    }

    public record ConduitConnectionData(boolean isInsert, boolean isExtract, RedstoneControl control) {}

    public void addToTooltip(@Nullable Level level, Consumer<Component> tooltipAdder, TooltipFlag tooltipFlag) {
    }

    @Nullable
    public static ResourceLocation getKey(ConduitType<?> conduitType) {
        return ConduitApi.INSTANCE.getConduitTypeRegistry().getKey(conduitType);
    }
}
