package com.enderio.conduits.common.blockentity;

import com.enderio.api.UseOnly;
import com.enderio.api.conduit.IClientConduitData;
import com.enderio.api.conduit.IConduitMenuData;
import com.enderio.api.conduit.IExtendedConduitData;
import com.enderio.api.conduit.ticker.IConduitTicker;
import com.enderio.api.conduit.IConduitType;
import com.enderio.api.misc.Vector2i;
import com.enderio.conduits.common.init.ConduitItems;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.registries.RegistryObject;

import java.util.Map;
import java.util.function.Supplier;

/**
 * Only to be used for conduits in EnderIOs Namespace
 */
public class SimpleConduitType<T extends IExtendedConduitData<T>> implements IConduitType<T> {

    private final ResourceLocation texture;
    private final int activeLightLevel;
    private final IConduitTicker ticker;
    private final Supplier<T> extendedDataFactory;

    private final IClientConduitData.Simple<T> clientConduitData;

    public SimpleConduitType(ResourceLocation texture, IConduitTicker ticker, Supplier<T> extendedDataFactory, ResourceLocation iconTexture, Vector2i iconTexturePos) {
        this(texture, 0, ticker, extendedDataFactory, iconTexture, iconTexturePos);
    }

    public SimpleConduitType(ResourceLocation texture, int activeLightLevel, IConduitTicker ticker, Supplier<T> extendedDataFactory, ResourceLocation iconTexture, Vector2i iconTexturePos) {
        this.texture = texture;
        this.activeLightLevel = activeLightLevel;
        this.ticker = ticker;
        this.extendedDataFactory = extendedDataFactory;
        clientConduitData = new IClientConduitData.Simple<>(iconTexture, iconTexturePos);
    }
    @Override
    public ResourceLocation getTexture(T data) {
        return texture;
    }

    @Override
    public ResourceLocation[] getTextures() {
        return new ResourceLocation[]{texture};
    }

    @Override
    public int getLightLevel(boolean isActive) {
        return isActive ? activeLightLevel : 0;
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
        return new ConduitMenuDataImpl(false, false, false, true, true, false);
    }

    @Override
    public T createExtendedConduitData(Level level, BlockPos pos) {
        return extendedDataFactory.get();
    }
}
