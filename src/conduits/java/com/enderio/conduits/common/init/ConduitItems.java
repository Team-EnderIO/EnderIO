package com.enderio.conduits.common.init;

import com.enderio.EnderIO;
import com.enderio.api.conduit.ConduitItemFactory;
import com.enderio.api.conduit.ConduitType;
import com.enderio.base.common.init.EIOCreativeTabs;
import com.enderio.conduits.common.conduit.upgrade.SpeedUpgradeItem;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.common.Mod;

import java.util.function.Supplier;

@Mod.EventBusSubscriber
public class ConduitItems {
    private static final Registrate REGISTRATE = EnderIO.registrate();

    public static final ItemEntry<Item> ENERGY = createConduitItem(EIOConduitTypes.ENERGY, "energy");
    public static final ItemEntry<Item> FLUID = createConduitItem(EIOConduitTypes.FLUID, "fluid");
    public static final ItemEntry<Item> PRESSURIZED_FLUID = createConduitItem(EIOConduitTypes.FLUID2, "pressurized_fluid");
    public static final ItemEntry<Item> ENDER_FLUID = createConduitItem(EIOConduitTypes.FLUID3, "ender_fluid");
    public static final ItemEntry<Item> REDSTONE = createConduitItem(EIOConduitTypes.REDSTONE, "redstone");
    public static final ItemEntry<Item> ITEM = createConduitItem(EIOConduitTypes.ITEM, "item");

    public static final ItemEntry<SpeedUpgradeItem> EXTRACTION_SPEED_UPGRADE_1 = REGISTRATE
        .item("extraction_speed_upgrade_1", props -> new SpeedUpgradeItem(props, 1))
        .lang("Tier 1 Extraction Speed Upgrade")
        .tab(EIOCreativeTabs.CONDUITS)
        .register();

    public static final ItemEntry<SpeedUpgradeItem> EXTRACTION_SPEED_UPGRADE_2 = REGISTRATE
        .item("extraction_speed_upgrade_2", props -> new SpeedUpgradeItem(props, 2))
        .lang("Tier 2 Extraction Speed Upgrade")
        .tab(EIOCreativeTabs.CONDUITS)
        .register();

    public static final ItemEntry<SpeedUpgradeItem> EXTRACTION_SPEED_UPGRADE_3 = REGISTRATE
        .item("extraction_speed_upgrade_3", props -> new SpeedUpgradeItem(props, 3))
        .lang("Tier 3 Extraction Speed Upgrade")
        .tab(EIOCreativeTabs.CONDUITS)
        .register();

    public static final ItemEntry<SpeedUpgradeItem> EXTRACTION_SPEED_UPGRADE_4 = REGISTRATE
        .item("extraction_speed_upgrade_4", props -> new SpeedUpgradeItem(props, 4))
        .lang("Tier 4 Extraction Speed Upgrade")
        .tab(EIOCreativeTabs.CONDUITS)
        .register();

    private static ItemEntry<Item> createConduitItem(Supplier<? extends ConduitType<?>> type, String itemName) {
        return REGISTRATE.item(itemName + "_conduit",
            properties -> ConduitItemFactory.build(type, properties))
            .tab(EIOCreativeTabs.CONDUITS)
            .model((ctx, prov) -> {
                var conduitTypeKey = ConduitType.getKey(type.get());
                prov
                    .withExistingParent(ctx.getName(), EnderIO.loc("item/conduit"))
                    .texture("0", EnderIO.loc("block/conduit/" + conduitTypeKey.getPath()));
            })
            .register();
    }

    public static void register() {}
}
