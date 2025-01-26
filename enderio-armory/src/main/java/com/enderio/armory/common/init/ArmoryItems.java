package com.enderio.armory.common.init;

import com.enderio.armory.EnderIOArmory;
import com.enderio.armory.common.config.ArmoryConfig;
import com.enderio.armory.common.item.darksteel.DarkSteelPickaxeItem;
import com.enderio.armory.common.item.darksteel.DarkSteelSwordItem;
import com.enderio.armory.common.item.darksteel.DarkSteelUpgradeItem;
import com.enderio.armory.common.item.darksteel.upgrades.EmpoweredUpgradeTier;
import com.enderio.armory.common.item.darksteel.upgrades.SpoonUpgrade;
import com.enderio.armory.common.item.darksteel.upgrades.direct.DirectUpgrade;
import com.enderio.armory.common.item.darksteel.upgrades.explosive.ExplosivePenetrationUpgradeTier;
import com.enderio.armory.common.item.darksteel.upgrades.explosive.ExplosiveUpgradeTier;
import com.enderio.armory.common.tag.ArmoryTags;
import com.enderio.base.common.init.EIOCreativeTabs;
import com.enderio.base.common.item.misc.MaterialItem;
import com.enderio.base.common.tag.EIOTags;
import com.enderio.regilite.holder.RegiliteItem;
import com.enderio.regilite.registry.ItemRegistry;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.SimpleTier;

@SuppressWarnings("unused")
public class ArmoryItems {
    private static final ItemRegistry ITEM_REGISTRY = EnderIOArmory.REGILITE.itemRegistry();

    public static final Tier DARK_STEEL_TIER = new SimpleTier(ArmoryTags.Blocks.INCORRECT_FOR_DARK_STEEL_TOOL, 2000,
            8.0f, 3.0f, 25, () -> Ingredient.of(EIOTags.Items.INGOTS_DARK_STEEL));

    public static final RegiliteItem<DarkSteelSwordItem> DARK_STEEL_SWORD = ITEM_REGISTRY
            .registerItem("dark_steel_sword", DarkSteelSwordItem::new, new Item.Properties().durability(2000))
            .setTab(EIOCreativeTabs.GEAR, modifier -> ArmoryItems.DARK_STEEL_SWORD.get().addAllVariants(modifier))
            .setTranslation("The Ender")
            .setModelProvider((prov, ctx) -> prov.handheld(ctx.get()))
            .addItemTags(ArmoryTags.Items.DARK_STEEL_UPGRADEABLE_SWORD)
            .addCapability(ArmoryCapabilities.DARK_STEEL_CAPABILITY, ArmoryCapabilities.DARK_STEEL_PROVIDER)
            .addCapability(Capabilities.EnergyStorage.ITEM, ArmoryCapabilities.DARK_STEEL_ENERGY_STORAGE_PROVIDER);

    public static final RegiliteItem<DarkSteelPickaxeItem> DARK_STEEL_PICKAXE = ITEM_REGISTRY
            .registerItem("dark_steel_pickaxe", DarkSteelPickaxeItem::new, new Item.Properties().durability(2000))
            .setTab(EIOCreativeTabs.GEAR, modifier -> ArmoryItems.DARK_STEEL_PICKAXE.get().addAllVariants(modifier))
            .setTranslation("Darksteel Pickaxe")
            .setModelProvider((prov, ctx) -> prov.handheld(ctx.get()))
            .addItemTags(ArmoryTags.Items.DARK_STEEL_UPGRADEABLE_PICKAXE)
            .addCapability(ArmoryCapabilities.DARK_STEEL_CAPABILITY, ArmoryCapabilities.DARK_STEEL_PROVIDER)
            .addCapability(Capabilities.EnergyStorage.ITEM, ArmoryCapabilities.DARK_STEEL_ENERGY_STORAGE_PROVIDER);

    // public static final RegiliteItem<TravelStaffItem> TRAVEL_STAFF =
    // ITEM_REGISTRY
    // .registerItem("staff_of_travelling", TravelStaffItem::new, new
    // Item.Properties().stacksTo(1))
    // .setTab(EIOCreativeTabs.GEAR, modifier ->
    // EIOItems.TRAVEL_STAFF.get().addAllVariants(modifier))
    // .addCapability(Capabilities.EnergyStorage.ITEM,
    // TravelStaffItem.ENERGY_STORAGE_PROVIDER);

//    public static final ItemEntry<DarkSteelAxeItem> DARK_STEEL_AXE = REGISTRATE
//        .item("dark_steel_axe", DarkSteelAxeItem::new)
//        .tab(EIOCreativeTabs.GEAR, modifier -> ArmoryItems.DARK_STEEL_AXE.get().addAllVariants(modifier))
//        .onRegister(item -> DarkSteelUpgradeRegistry
//            .instance()
//            .addUpgradesForItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(item)), EmpoweredUpgrade.NAME, ForkUpgrade.NAME, DirectUpgrade.NAME))
//        .register();
//
    private static final String UPGRADE_TEXT = " Upgrade";

    public static final RegiliteItem<MaterialItem> DARK_STEEL_UPGRADE_BLANK = ITEM_REGISTRY
            .registerItem("dark_steel_upgrade_blank", props -> new MaterialItem(props, false))
            .setTab(EIOCreativeTabs.GEAR)
            .setTranslation("Blank" + UPGRADE_TEXT)
            .setModelProvider((prov, ctx) -> prov.handheld(ctx.get()));

    public static final RegiliteItem<DarkSteelUpgradeItem> DARK_STEEL_UPGRADE_EMPOWERED_1 = ITEM_REGISTRY
            .registerItem("dark_steel_upgrade_empowered_1",
                    properties -> new DarkSteelUpgradeItem(properties, EmpoweredUpgradeTier.ONE.getActivationCost(),
                            EmpoweredUpgradeTier.ONE.getFactory()))
            .setTab(EIOCreativeTabs.GEAR)
            .setTranslation("Empowered" + UPGRADE_TEXT)
            .setModelProvider((prov, ctx) -> prov.handheld(ctx.get()));

    public static final RegiliteItem<DarkSteelUpgradeItem> DARK_STEEL_UPGRADE_EMPOWERED_2 = ITEM_REGISTRY
            .registerItem("dark_steel_upgrade_empowered_2",
                    properties -> new DarkSteelUpgradeItem(properties, EmpoweredUpgradeTier.TWO.getActivationCost(),
                            EmpoweredUpgradeTier.TWO.getFactory()))
            .setTab(EIOCreativeTabs.GEAR)
            .setTranslation("Empowered II" + UPGRADE_TEXT)
            .setModelProvider((prov, ctx) -> prov.handheld(ctx.get()));

    public static final RegiliteItem<DarkSteelUpgradeItem> DARK_STEEL_UPGRADE_EMPOWERED_3 = ITEM_REGISTRY
            .registerItem("dark_steel_upgrade_empowered_3",
                    properties -> new DarkSteelUpgradeItem(properties, EmpoweredUpgradeTier.THREE.getActivationCost(),
                            EmpoweredUpgradeTier.THREE.getFactory()))
            .setTab(EIOCreativeTabs.GEAR)
            .setTranslation("Empowered III" + UPGRADE_TEXT)
            .setModelProvider((prov, ctx) -> prov.handheld(ctx.get()));

    public static final RegiliteItem<DarkSteelUpgradeItem> DARK_STEEL_UPGRADE_EMPOWERED_4 = ITEM_REGISTRY
            .registerItem("dark_steel_upgrade_empowered_4",
                    properties -> new DarkSteelUpgradeItem(properties, EmpoweredUpgradeTier.FOUR.getActivationCost(),
                            EmpoweredUpgradeTier.FOUR.getFactory()))
            .setTab(EIOCreativeTabs.GEAR)
            .setTranslation("Empowered IV" + UPGRADE_TEXT)
            .setModelProvider((prov, ctx) -> prov.handheld(ctx.get()));

    public static final RegiliteItem<DarkSteelUpgradeItem> DARK_STEEL_UPGRADE_SPOON = ITEM_REGISTRY
            .registerItem("dark_steel_upgrade_spoon",
                    properties -> new DarkSteelUpgradeItem(properties, ArmoryConfig.COMMON.SPOON_ACTIVATION_COST,
                            SpoonUpgrade::new))
            .setTab(EIOCreativeTabs.GEAR)
            .setTranslation("Spoon" + UPGRADE_TEXT)
            .setModelProvider((prov, ctx) -> prov.handheld(ctx.get()));

    public static final RegiliteItem<DarkSteelUpgradeItem> DARK_STEEL_UPGRADE_DIRECT = ITEM_REGISTRY
            .registerItem("dark_steel_upgrade_direct",
                    properties -> new DarkSteelUpgradeItem(properties, ArmoryConfig.COMMON.DIRECT_ACTIVATION_COST,
                            DirectUpgrade::new))
            .setTab(EIOCreativeTabs.GEAR)
            .setTranslation("Direct" + UPGRADE_TEXT)
            .setModelProvider((prov, ctx) -> prov.handheld(ctx.get()));

    public static final RegiliteItem<DarkSteelUpgradeItem> DARK_STEEL_UPGRADE_EXPLOSIVE_1 = ITEM_REGISTRY
            .registerItem("dark_steel_upgrade_tnt",
                    properties -> new DarkSteelUpgradeItem(properties, ExplosiveUpgradeTier.ONE.getActivationCost(),
                            ExplosiveUpgradeTier.ONE.getFactory()))
            .setTab(EIOCreativeTabs.GEAR)
            .setTranslation("Explosive I" + UPGRADE_TEXT)
            .setModelProvider((prov, ctx) -> prov.handheld(ctx.get()));

    public static final RegiliteItem<DarkSteelUpgradeItem> DARK_STEEL_UPGRADE_EXPLOSIVE_2 = ITEM_REGISTRY
            .registerItem("dark_steel_upgrade_tnt2",
                    properties -> new DarkSteelUpgradeItem(properties, ExplosiveUpgradeTier.TWO.getActivationCost(),
                            ExplosiveUpgradeTier.TWO.getFactory()))
            .setTab(EIOCreativeTabs.GEAR)
            .setTranslation("Explosive II" + UPGRADE_TEXT)
            .setModelProvider((prov, ctx) -> prov.handheld(ctx.get()));

    public static final RegiliteItem<DarkSteelUpgradeItem> DARK_STEEL_UPGRADE_EXPLOSIVE_PENETRATION_1 = ITEM_REGISTRY
            .registerItem("dark_steel_upgrade_penetration_1",
                    properties -> new DarkSteelUpgradeItem(properties,
                            ExplosivePenetrationUpgradeTier.ONE.getActivationCost(),
                            ExplosivePenetrationUpgradeTier.ONE.getFactory()))
            .setTab(EIOCreativeTabs.GEAR)
            .setTranslation("EExplosive Penetration I" + UPGRADE_TEXT)
            .setModelProvider((prov, ctx) -> prov.handheld(ctx.get()));

    public static final RegiliteItem<DarkSteelUpgradeItem> DARK_STEEL_UPGRADE_EXPLOSIVE_PENETRATION_2 = ITEM_REGISTRY
            .registerItem("dark_steel_upgrade_penetration_2",
                    properties -> new DarkSteelUpgradeItem(properties,
                            ExplosivePenetrationUpgradeTier.TWO.getActivationCost(),
                            ExplosivePenetrationUpgradeTier.TWO.getFactory()))
            .setTab(EIOCreativeTabs.GEAR)
            .setTranslation("EExplosive Penetration II" + UPGRADE_TEXT)
            .setModelProvider((prov, ctx) -> prov.handheld(ctx.get()));

    public static void register(IEventBus bus) {
        ITEM_REGISTRY.register(bus);
    }
}
