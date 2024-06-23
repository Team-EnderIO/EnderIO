package com.enderio.conduits.client.gui.conduit;

import com.enderio.EnderIO;
import com.enderio.api.ConduitDataAccessor;
import com.enderio.api.conduit.ConduitNode;
import com.enderio.api.conduit.screen.ConduitScreenExtension;
import com.enderio.api.misc.Vector2i;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.conduits.common.conduit.type.item.ItemConduitData;
import com.enderio.conduits.common.init.ConduitTypes;
import com.enderio.core.client.gui.widgets.ToggleIconButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ItemConduitScreenExtension implements ConduitScreenExtension {

    private static final ResourceLocation ICON_ROUND_ROBIN_ENABLED = EnderIO.loc("icon/round_robin_enabled");
    private static final ResourceLocation ICON_ROUND_ROBIN_DISABLED = EnderIO.loc("icon/round_robin_disabled");
    private static final ResourceLocation ICON_SELF_FEED_ENABLED = EnderIO.loc("icon/self_feed_enabled");
    private static final ResourceLocation ICON_SELF_FEED_DISABLED = EnderIO.loc("icon/self_feed_disabled");

    @Override
    public List<AbstractWidget> createWidgets(Screen screen, ConduitDataAccessor conduitDataAccessor, UpdateDispatcher updateConduitData,
        Supplier<Direction> direction, Vector2i widgetsStart) {
        List<AbstractWidget> widgets = new ArrayList<>();

        widgets.add(ToggleIconButton.of(
            widgetsStart.x() + 110,
            widgetsStart.y() + 20,
            16,
            16,
            ICON_ROUND_ROBIN_ENABLED,
            ICON_ROUND_ROBIN_DISABLED,
            EIOLang.ROUND_ROBIN_ENABLED,
            EIOLang.ROUND_ROBIN_DISABLED,
            () -> conduitDataAccessor.getOrCreateData(ConduitTypes.Data.ITEM.get()).get(direction.get()).isRoundRobin,
            bool -> {
                var data = conduitDataAccessor.getOrCreateData(ConduitTypes.Data.ITEM.get());
                var sideData = data.compute(direction.get());
                sideData.isRoundRobin = bool;
                updateConduitData.sendUpdate();
            }));

        widgets.add(ToggleIconButton.of(
            widgetsStart.x() + 130,
            widgetsStart.y() + 20,
            16,
            16,
            ICON_SELF_FEED_ENABLED,
            ICON_SELF_FEED_DISABLED,
            EIOLang.SELF_FEED_ENABLED,
            EIOLang.SELF_FEED_DISABLED,
            () -> conduitDataAccessor.getOrCreateData(ConduitTypes.Data.ITEM.get()).get(direction.get()).isSelfFeed,
            bool -> {
                var data = conduitDataAccessor.getOrCreateData(ConduitTypes.Data.ITEM.get());
                var sideData = data.compute(direction.get());
                sideData.isSelfFeed = bool;
                updateConduitData.sendUpdate();
            }));
        return widgets;
    }
}
