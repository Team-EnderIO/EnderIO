package com.enderio.base.common.init;

import com.enderio.base.api.EnderIO;
import com.enderio.base.api.filter.SoulFilter;
import com.enderio.base.api.filter.FilterMenuProvider;
import com.enderio.base.api.filter.FluidFilter;
import com.enderio.base.api.filter.ItemFilter;
import com.enderio.base.api.soul.binding.ISoulBindable;
import com.enderio.base.api.soul.storage.ISoulHandler;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.ItemCapability;
import org.jetbrains.annotations.Nullable;

public class EIOCapabilities {

    public static final class SideConfig {
        public static final BlockCapability<com.enderio.base.api.capability.SideConfig, Direction> BLOCK = BlockCapability
                .createSided(EnderIO.loc("side_config"), com.enderio.base.api.capability.SideConfig.class);
    }

    public static final ItemCapability<FilterMenuProvider, Void> FILTER_MENU_PROVIDER = ItemCapability
            .createVoid(EnderIO.loc("filter_menu_provider"), FilterMenuProvider.class);

    public static final ItemCapability<ItemFilter, Void> ITEM_FILTER = ItemCapability
            .createVoid(EnderIO.loc("item_filter"), ItemFilter.class);

    public static final ItemCapability<FluidFilter, Void> FLUID_FILTER = ItemCapability
            .createVoid(EnderIO.loc("fluid_filter"), FluidFilter.class);

    public static final ItemCapability<SoulFilter, Void> SOUL_FILTER = ItemCapability
            .createVoid(EnderIO.loc("soul_filter"), SoulFilter.class);

    public static final class SoulBindable {
        public static final ItemCapability<ISoulBindable, Void> ITEM = ItemCapability
                .createVoid(EnderIO.loc("soul_bindable"), ISoulBindable.class);

        public static final BlockCapability<ISoulBindable, Void> BLOCK = BlockCapability
                .createVoid(EnderIO.loc("soul_bindable"), ISoulBindable.class);
    }

    public static final class SoulHandler {
        public static final ItemCapability<ISoulHandler, Void> ITEM = ItemCapability
                .createVoid(EnderIO.loc("soul_handler"), ISoulHandler.class);

        public static final BlockCapability<ISoulHandler, @Nullable Direction> BLOCK = BlockCapability
            .createSided(EnderIO.loc("soul_handler"), ISoulHandler.class);
    }
}
