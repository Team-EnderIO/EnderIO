package com.enderio.conduits.common.init;

import com.enderio.EnderIO;
import com.enderio.api.conduit.ConduitApi;
import com.enderio.api.conduit.ConduitType;
import com.enderio.base.common.init.EIOCreativeTabs;
import com.enderio.conduits.common.components.FluidSpeedUpgrade;
import com.enderio.conduits.common.components.ItemSpeedUpgrade;
import com.enderio.conduits.common.items.SpeedUpgradeItem;
import com.enderio.regilite.holder.RegiliteItem;
import com.enderio.regilite.registry.ItemRegistry;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;

import java.util.function.Supplier;

public class ConduitItems {
    private static final ItemRegistry ITEM_REGISTRY = EnderIO.getRegilite().itemRegistry();

    public static final RegiliteItem<Item> ENERGY = createConduitItem(ConduitTypes.ENERGY, "energy");
    public static final RegiliteItem<Item> FLUID = createConduitItem(ConduitTypes.FLUID, "fluid");
    public static final RegiliteItem<Item> PRESSURIZED_FLUID = createConduitItem(ConduitTypes.FLUID2, "pressurized_fluid");
    public static final RegiliteItem<Item> ENDER_FLUID = createConduitItem(ConduitTypes.FLUID3, "ender_fluid");
    public static final RegiliteItem<Item> REDSTONE = createConduitItem(ConduitTypes.REDSTONE, "redstone");
    public static final RegiliteItem<Item> ITEM = createConduitItem(ConduitTypes.ITEM, "item");

    public static final RegiliteItem<SpeedUpgradeItem> ITEM_SPEED_UPGRADE = ITEM_REGISTRY.registerItem("item_speed_upgrade", properties ->
            new SpeedUpgradeItem(properties.component(ConduitComponents.ITEM_SPEED_UPGRADE, new ItemSpeedUpgrade(2))))
        .setTab(EIOCreativeTabs.CONDUITS)
        .addCapability(ConduitCapabilities.ConduitUpgrade.ITEM, SpeedUpgradeItem.ITEM_SPEED_UPGRADE_PROVIDER);

    public static final RegiliteItem<SpeedUpgradeItem> FLUID_SPEED_UPGRADE = ITEM_REGISTRY.registerItem("fluid_speed_upgrade", properties ->
            new SpeedUpgradeItem(properties.component(ConduitComponents.FLUID_SPEED_UPGRADE, new FluidSpeedUpgrade(2))))
        .setTab(EIOCreativeTabs.CONDUITS)
        .addCapability(ConduitCapabilities.ConduitUpgrade.ITEM, SpeedUpgradeItem.FLUID_SPEED_UPGRADE_PROVIDER);

    private static RegiliteItem<Item> createConduitItem(Supplier<? extends ConduitType<?>> type, String itemName) {
        return ITEM_REGISTRY
            .registerItem(itemName + "_conduit",
                p -> ConduitApi.INSTANCE.createConduitItem(type, p))
            .setTab(EIOCreativeTabs.CONDUITS)
            .setModelProvider((prov, ctx) -> prov
                .withExistingParent(itemName+"_conduit", EnderIO.loc("item/conduit"))
                .texture("0", type.get().getItemTexture()));
    }

    public static void register(IEventBus bus) {
        ITEM_REGISTRY.register(bus);
    }
}
