package com.enderio.conduits.common.types;

import com.enderio.api.UseOnly;
import com.enderio.api.conduit.IClientConduitData;
import com.enderio.api.conduit.IConduitMenuData;
import com.enderio.api.conduit.IConduitType;
import com.enderio.api.conduit.IExtendedConduitData;
import com.enderio.api.conduit.ticker.IConduitTicker;
import com.enderio.api.misc.Vector2i;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.LogicalSide;

import java.util.function.Supplier;

/**
 * Only to be used for conduits in EnderIOs Namespace
 */
public class SimpleConduitType<T extends IExtendedConduitData<T>> implements IConduitType<T> {

    private final ResourceLocation texture;
    private final IConduitTicker ticker;
    private final Supplier<T> extendedDataFactory;

    private final IClientConduitData<T> clientConduitData;

    private final IConduitMenuData menuData;

    public SimpleConduitType(ResourceLocation texture, IConduitTicker ticker, Supplier<T> extendedDataFactory, ResourceLocation iconTexture, Vector2i iconTexturePos, IConduitMenuData menuData) {
        this(texture, ticker, extendedDataFactory, new IClientConduitData.Simple<>(iconTexture, iconTexturePos), menuData);
    }

    public SimpleConduitType(ResourceLocation texture, IConduitTicker ticker, Supplier<T> extendedDataFactory, IClientConduitData<T> clientConduitData, IConduitMenuData menuData) {
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
    public IConduitTicker getTicker() {
        return ticker;
    }

    @Override
    @UseOnly(LogicalSide.CLIENT)
    public IClientConduitData<T> getClientData() {
        return clientConduitData;
    }

    @Override
    public IConduitMenuData getMenuData() {
        return menuData;
    }

    @Override
    public T createExtendedConduitData(Level level, BlockPos pos) {
        return extendedDataFactory.get();
    }
}
