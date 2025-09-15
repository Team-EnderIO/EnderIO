/*package com.enderio.machines.common.integrations.jsonthings;

import com.enderio.machines.common.block.CapacitorBankBlock;
import com.enderio.machines.common.blockentity.base.LegacyMachineBlockEntity;
import com.enderio.machines.common.blockentity.multienergy.CapacityTier;
import com.enderio.regilite.holder.RegiliteBlockEntity;
import com.google.common.collect.Maps;
import dev.gigaherz.jsonthings.things.IFlexBlock;
import dev.gigaherz.jsonthings.things.events.FlexEventHandler;
import dev.gigaherz.jsonthings.things.events.FlexEventType;
import dev.gigaherz.jsonthings.things.shapes.DynamicShape;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class FlexCapacitorBankBlock extends CapacitorBankBlock implements IFlexBlock {

    private final Map<FlexEventType, FlexEventHandler> eventHandlers = Maps.newHashMap();

    public FlexCapacitorBankBlock(Properties properties, RegiliteBlockEntity<? extends LegacyMachineBlockEntity> blockEntityType, CapacityTier tier) {
        super(properties, blockEntityType, tier);
    }

    @Override
    public void setGeneralShape(@Nullable DynamicShape shape) {
    }

    @Override
    public void setCollisionShape(@Nullable DynamicShape shape) {
    }

    @Override
    public void setRaytraceShape(@Nullable DynamicShape shape) {
    }

    @Override
    public void setRenderShape(@Nullable DynamicShape shape) {
    }

    @Override
    public <T> void addEventHandler(FlexEventType<T> event, FlexEventHandler<T> eventHandler) {
        eventHandlers.put(event, eventHandler);
    }

    @Nullable
    @Override
    public <T> FlexEventHandler<T> getEventHandler(FlexEventType<T> event) {
        return eventHandlers.get(event);
    }
}*/
