package com.enderio.conduits.common.blockentity;

import com.enderio.EnderIO;
import com.enderio.api.conduit.IConduitScreenData;
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

    public SimpleConduitType(ResourceLocation texture, IConduitTicker ticker, Supplier<T> extendedDataFactory) {
        this(texture, 0, ticker, extendedDataFactory);
    }

    public SimpleConduitType(ResourceLocation texture, int activeLightLevel, IConduitTicker ticker, Supplier<T> extendedDataFactory) {
        this.texture = texture;
        this.activeLightLevel = activeLightLevel;
        this.ticker = ticker;
        this.extendedDataFactory = extendedDataFactory;
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
    public Item getConduitItem() {
        for (Map.Entry<RegistryObject<? extends IConduitType<?>>, ItemEntry<Item>> registryObjectItemEntryEntry : ConduitItems.CONDUITS.entrySet()) {
            if (registryObjectItemEntryEntry.getKey().get() == this)
                return registryObjectItemEntryEntry.getValue().get();
        }
        throw new NullPointerException();
    }

    @Override
    public int getLightLevel(boolean isActive) {
        return isActive ? activeLightLevel : 0;
    }

    @Override
    public ResourceLocation getTextureLocation() {
        return EnderIO.loc("textures/gui/conduit_icon.png");
    }

    @Override
    public Vector2i getTexturePosition() {
        //TODO: allow other things
        return Vector2i.ZERO;
    }

    @Override
    public IConduitTicker getTicker() {
        return ticker;
    }

    @Override
    public IConduitScreenData getData() {
        return new ConduitScreenDataImpl(false, false, false, true, true, false);
    }

    @Override
    public T createExtendedConduitData(Level level, BlockPos pos) {
        return extendedDataFactory.get();
    }
}
