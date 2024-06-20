package com.enderio.conduits.common.types;

import com.enderio.api.UseOnly;
import com.enderio.api.conduit.ConduitMenuData;
import com.enderio.api.conduit.ConduitType;
import com.enderio.api.conduit.ConduitData;
import com.enderio.api.misc.Vector2i;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.LogicalSide;

import java.util.function.Supplier;

/**
 * Only to be used for conduits in EnderIOs Namespace
 */
public class SimpleConduitType<T extends ConduitData<T>> implements ConduitType<T> {

    private final ResourceLocation texture;
    private final IConduitTicker ticker;
    private final Supplier<T> extendedDataFactory;

    private final IClientConduitData<T> clientConduitData;

    private final ConduitMenuData menuData;

    public SimpleConduitType(ResourceLocation texture, IConduitTicker ticker, Supplier<T> extendedDataFactory, ResourceLocation iconTexture, Vector2i iconTexturePos, ConduitMenuData menuData) {
        this(texture, ticker, extendedDataFactory, new IClientConduitData.Simple<>(iconTexture, iconTexturePos), menuData);
    }

    public SimpleConduitType(ResourceLocation texture, IConduitTicker ticker, Supplier<T> extendedDataFactory, IClientConduitData<T> clientConduitData, ConduitMenuData menuData) {
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
    public ConduitMenuData getMenuData() {
        return menuData;
    }

    @Override
    public T createConduitData(Level level, BlockPos pos) {
        return extendedDataFactory.get();
    }
}
