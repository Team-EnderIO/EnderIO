package com.enderio.conduits.common.conduit.type.redstone;

import com.enderio.base.api.filter.ResourceFilter;
import com.enderio.conduits.api.Conduit;
import com.enderio.conduits.api.ConduitMenuData;
import com.enderio.conduits.api.ConduitNode;
import com.enderio.conduits.api.ConduitType;
import com.enderio.conduits.api.SlotType;
import com.enderio.conduits.common.init.ConduitTypes;
import com.enderio.conduits.common.redstone.RedstoneExtractFilter;
import com.enderio.conduits.common.redstone.RedstoneInsertFilter;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record RedstoneConduit(
    ResourceLocation texture,
    ResourceLocation activeTexture,
    Component description
) implements Conduit<RedstoneConduit> {

    public static MapCodec<RedstoneConduit> CODEC = RecordCodecBuilder.mapCodec(
        builder -> builder.group(
            ResourceLocation.CODEC.fieldOf("texture").forGetter(RedstoneConduit::texture),
            ResourceLocation.CODEC.fieldOf("active_texture").forGetter(RedstoneConduit::activeTexture),
            ComponentSerialization.CODEC.fieldOf("description").forGetter(RedstoneConduit::description)
        ).apply(builder, RedstoneConduit::new)
    );

    private static final RedstoneConduitTicker TICKER = new RedstoneConduitTicker();
    private static final ConduitMenuData MENU_DATA = new ConduitMenuData.Simple(true, true, false, true, true, false);

    @Override
    public int graphTickRate() {
        return 2;
    }

    @Override
    public ConduitType<RedstoneConduit> type() {
        return ConduitTypes.REDSTONE.get();
    }

    @Override
    public RedstoneConduitTicker getTicker() {
        return TICKER;
    }

    @Override
    public ConduitMenuData getMenuData() {
        return MENU_DATA;
    }

    @Override
    public boolean canApplyFilter(SlotType slotType, ResourceFilter resourceFilter) {
        return switch (slotType) {
            case FILTER_EXTRACT -> resourceFilter instanceof RedstoneExtractFilter;
            case FILTER_INSERT -> resourceFilter instanceof RedstoneInsertFilter;
            default -> false;
        };
    }

    @Override
    public ResourceLocation getTexture(ConduitNode node) {
        RedstoneConduitData data = node.getData(ConduitTypes.Data.REDSTONE.get());
        return data != null && data.isActive() ? activeTexture() : texture();
    }

    @Override
    public int compareTo(@NotNull RedstoneConduit o) {
        return 0;
    }
}
