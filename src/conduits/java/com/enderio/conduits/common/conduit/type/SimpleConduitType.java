package com.enderio.conduits.common.conduit.type;

import com.enderio.api.UseOnly;
import com.enderio.api.conduit.ClientConduitData;
import com.enderio.api.conduit.ConduitMenuData;
import com.enderio.api.conduit.ConduitType;
import com.enderio.api.conduit.ExtendedConduitData;
import com.enderio.api.conduit.ticker.ConduitTicker;
import com.enderio.api.misc.Vector2i;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.neoforged.fml.LogicalSide;

import java.util.function.Supplier;

/**
 * Only to be used for conduits in EnderIOs Namespace
 */
public class SimpleConduitType<T extends ExtendedConduitData<T>> implements ConduitType<T> {

    private final ResourceLocation texture;
    private final ConduitTicker ticker;

    private final ClientConduitData<T> clientConduitData;

    private final ConduitMenuData menuData;

    private final Supplier<T> extendedDataFactory;

    public SimpleConduitType(
        ResourceLocation texture,
        ConduitTicker ticker,
        Supplier<T> extendedDataFactory,
        ResourceLocation iconTexture,
        Vector2i iconTexturePos,
        ConduitMenuData menuData) {

        this(texture, ticker, extendedDataFactory, new ClientConduitData.Simple<>(iconTexture, iconTexturePos), menuData);
    }

    public SimpleConduitType(
        ResourceLocation texture,
        ConduitTicker ticker,
        Supplier<T> extendedDataFactory,
        ClientConduitData<T> clientConduitData,
        ConduitMenuData menuData) {

        this.texture = texture;
        this.ticker = ticker;
        this.extendedDataFactory = extendedDataFactory;
        this.clientConduitData = clientConduitData;
        this.menuData = menuData;
    }
    @Override
    public ResourceLocation getTexture(T data) {
        return texture;
    }

    @Override
    public ResourceLocation getItemTexture() {
        return texture;
    }

    @Override
    public ConduitTicker getTicker() {
        return ticker;
    }

    @Override
    @UseOnly(LogicalSide.CLIENT)
    public ClientConduitData<T> getClientData() {
        return clientConduitData;
    }

    @Override
    public ConduitMenuData getMenuData() {
        return menuData;
    }

    @Override
    public T createExtendedConduitData(Level level, BlockPos pos) {
        return extendedDataFactory.get();
    }
}
