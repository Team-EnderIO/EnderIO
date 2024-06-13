package com.enderio.conduits.common.conduit.type;

import com.enderio.api.conduit.ClientConduitData;
import com.enderio.api.conduit.ConduitData;
import com.enderio.api.conduit.ConduitMenuData;
import com.enderio.api.conduit.ConduitType;
import com.enderio.api.conduit.ticker.ConduitTicker;
import com.enderio.api.misc.Vector2i;
import me.liliandev.ensure.ensures.EnsureSide;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.function.Supplier;

/**
 * Only to be used for conduits in EnderIOs Namespace
 */
public class SimpleConduitType<T extends ConduitData<T>> extends ConduitType<T> {

    private final ConduitTicker<T> ticker;

    private final ClientConduitData<T> clientConduitData;

    private final ConduitMenuData menuData;

    private final Supplier<T> extendedDataFactory;

    public SimpleConduitType(
        ConduitTicker<T> ticker,
        Supplier<T> extendedDataFactory,
        ConduitMenuData menuData) {

        this(ticker, extendedDataFactory, new ClientConduitData.Simple<>(), menuData);
    }

    public SimpleConduitType(
        ConduitTicker<T> ticker,
        Supplier<T> extendedDataFactory,
        ClientConduitData<T> clientConduitData,
        ConduitMenuData menuData) {

        this.ticker = ticker;
        this.extendedDataFactory = extendedDataFactory;
        this.clientConduitData = clientConduitData;
        this.menuData = menuData;
    }

    @Override
    public ConduitTicker<T> getTicker() {
        return ticker;
    }

    @Override
    @EnsureSide(EnsureSide.Side.CLIENT)
    public ClientConduitData<T> getClientData() {
        return clientConduitData;
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
