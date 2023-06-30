package com.enderio.conduits.common.types;

import com.enderio.EnderIO;
import com.enderio.api.conduit.IClientConduitData;
import com.enderio.api.misc.Vector2i;
import com.enderio.conduits.common.init.EnderConduitTypes;
import com.enderio.core.client.gui.widgets.CheckBox;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ItemClientConduitData implements IClientConduitData<ItemExtendedData> {
    @Override
    public List<AbstractWidget> createWidgets(Screen screen, ItemExtendedData extendedConduitData, Supplier<Direction> direction, Vector2i widgetsStart) {
        List<AbstractWidget> widgets = new ArrayList<>();
        widgets.add(new CheckBox(EnderIO.loc("textures/gui/round_robin.png"), widgetsStart.add(110, 20), () -> extendedConduitData.get(direction.get()).roundRobin, bool -> extendedConduitData.compute(direction.get()).roundRobin = bool));
        widgets.add(new CheckBox(EnderIO.loc("textures/gui/self_feed.png"), widgetsStart.add(130, 20), () -> extendedConduitData.get(direction.get()).selfFeed, bool -> extendedConduitData.compute(direction.get()).selfFeed = bool));
        return widgets;
    }

    @Override
    public ResourceLocation getTextureLocation() {
        return EnderConduitTypes.ICON_TEXTURE;
    }

    @Override
    public Vector2i getTexturePosition() {
        return new Vector2i(0, 72);
    }
}
