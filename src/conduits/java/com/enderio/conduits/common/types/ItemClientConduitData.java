package com.enderio.conduits.common.types;

import com.enderio.EnderIO;
import com.enderio.api.conduit.IClientConduitData;
import com.enderio.api.conduit.IConduitType;
import com.enderio.api.misc.Vector2i;
import com.enderio.conduits.common.init.EnderConduitTypes;
import com.enderio.conduits.common.network.C2SSetConduitExtendedData;
import com.enderio.core.client.gui.widgets.CheckBox;
import com.enderio.core.common.network.CoreNetwork;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ItemClientConduitData implements IClientConduitData<ItemExtendedData> {
    @Override
    public List<AbstractWidget> createWidgets(Screen screen, Supplier<BlockPos> blockPos, Supplier<IConduitType<?>> type, ItemExtendedData extendedConduitData, Supplier<Direction> direction, Vector2i widgetsStart) {
        // TODO: Method of doing sync that does not require CoreNetwork in API.
        List<AbstractWidget> widgets = new ArrayList<>();
        widgets.add(new CheckBox(EnderIO.loc("textures/gui/round_robin.png"), widgetsStart.add(110, 20), () -> extendedConduitData.get(direction.get()).roundRobin, bool -> {
            extendedConduitData.compute(direction.get()).roundRobin = bool;
            CoreNetwork.sendToServer(new C2SSetConduitExtendedData(blockPos.get(), type.get(), extendedConduitData));
        }));
        widgets.add(new CheckBox(EnderIO.loc("textures/gui/self_feed.png"), widgetsStart.add(130, 20), () -> extendedConduitData.get(direction.get()).selfFeed, bool -> {
            extendedConduitData.compute(direction.get()).selfFeed = bool;
            CoreNetwork.sendToServer(new C2SSetConduitExtendedData(blockPos.get(), type.get(), extendedConduitData));
        }));
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
