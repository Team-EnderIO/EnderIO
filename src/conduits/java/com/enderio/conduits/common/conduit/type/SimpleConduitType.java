package com.enderio.conduits.common.conduit.type;

import com.enderio.api.conduit.ConduitData;
import com.enderio.api.conduit.ConduitMenuData;
import com.enderio.api.conduit.ConduitType;
import com.enderio.api.conduit.ticker.ConduitTicker;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.function.Supplier;

/**
 * Only to be used for conduits in EnderIOs Namespace
 */
public class SimpleConduitType<T extends ConduitData<T>> extends ConduitType<T> {

    private final ConduitTicker<T> ticker;
    private final ConduitMenuData menuData;
    private final Supplier<T> extendedDataFactory;

    public SimpleConduitType(
        ConduitTicker<T> ticker,
        Supplier<T> extendedDataFactory,
        ConduitMenuData menuData) {

        this.ticker = ticker;
        this.extendedDataFactory = extendedDataFactory;
        this.menuData = menuData;
    }

    @Override
    public ConduitTicker<T> getTicker() {
        return ticker;
    }

    @Override
    public ConduitMenuData getMenuData() {
        return menuData;
    }

    @Override
    public T createConduitData(Level level, BlockPos pos) {
        return extendedDataFactory.get();
    }
}
