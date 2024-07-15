package com.enderio.armory.common.init;

import com.enderio.armory.EnderIOArmory;
import com.enderio.armory.common.item.darksteel.DarkSteelSwordItem;
import com.enderio.armory.common.tag.ArmoryTags;
import com.enderio.base.common.init.EIOCreativeTabs;
import com.enderio.base.common.tag.EIOTags;
import com.enderio.regilite.holder.RegiliteItem;
import com.enderio.regilite.registry.ItemRegistry;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.SimpleTier;

@SuppressWarnings("unused")
public class ArmoryItems {
    private static final ItemRegistry ITEM_REGISTRY = EnderIOArmory.REGILITE.itemRegistry();

    /*public static final Tier DARK_STEEL_TIER = TierSortingRegistry.registerTier(
        new SimpleTier(3, 2000, 8.0F, 3, 25, ArmoryTags.Blocks.DARK_STEEL_TIER, () -> Ingredient.of(EIOItems.DARK_STEEL_INGOT.get())),
        EnderIO.loc("dark_steel_tier"), List.of(Tiers.DIAMOND), List.of(Tiers.NETHERITE));*/

    public static final Tier DARK_STEEL_TIER = new SimpleTier(ArmoryTags.Blocks.INCORRECT_FOR_DARK_STEEL_TOOL, 2000, 8.0f, 3.0f, 25, () -> Ingredient.of(
        EIOTags.Items.INGOTS_DARK_STEEL));

    public static final RegiliteItem<DarkSteelSwordItem> DARK_STEEL_SWORD = ITEM_REGISTRY
        .registerItem("dark_steel_sword", DarkSteelSwordItem::new, new Item.Properties().durability(2000))
        .withTab(EIOCreativeTabs.GEAR)
        .withTranslation("The Ender")
        .withModelProvider((prov, ctx) -> prov.handheld(ctx.get()));

    // TODO: Bring these back when they are finished.
//    public static final ItemEntry<DarkSteelPickaxeItem> DARK_STEEL_PICKAXE = REGISTRATE
//        .item("dark_steel_pickaxe", DarkSteelPickaxeItem::new)
//        .tab(EIOCreativeTabs.GEAR, modifier -> ArmoryItems.DARK_STEEL_PICKAXE.get().addAllVariants(modifier))
//        .onRegister(item -> DarkSteelUpgradeRegistry
//            .instance()
//            .addUpgradesForItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(item)), EmpoweredUpgrade.NAME, SpoonUpgrade.NAME, DirectUpgrade.NAME,
//                ExplosiveUpgrade.NAME, ExplosivePenetrationUpgrade.NAME))
//        .register();
//
//    public static final ItemEntry<DarkSteelAxeItem> DARK_STEEL_AXE = REGISTRATE
//        .item("dark_steel_axe", DarkSteelAxeItem::new)
//        .tab(EIOCreativeTabs.GEAR, modifier -> ArmoryItems.DARK_STEEL_AXE.get().addAllVariants(modifier))
//        .onRegister(item -> DarkSteelUpgradeRegistry
//            .instance()
//            .addUpgradesForItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(item)), EmpoweredUpgrade.NAME, ForkUpgrade.NAME, DirectUpgrade.NAME))
//        .register();
//
//    private static final String UPGRADE_TEXT = " Upgrade";
//
//    public static final ItemEntry<MaterialItem> DARK_STEEL_UPGRADE_BLANK = REGISTRATE
//        .item("dark_steel_upgrade_blank", props -> new MaterialItem(props, false))
//        .tab(EIOCreativeTabs.GEAR)
//        .lang("Blank" + UPGRADE_TEXT)
//        .register();
//
//    public static final ItemEntry<DarkSteelUpgradeItem> DARK_STEEL_UPGRADE_EMPOWERED_1 = REGISTRATE
//        .item("dark_steel_upgrade_empowered_1",
//            properties -> new DarkSteelUpgradeItem(properties, EmpoweredUpgradeTier.ONE.getActivationCost(), EmpoweredUpgradeTier.ONE.getFactory()))
//        .tab(EIOCreativeTabs.GEAR, modifier -> ArmoryItems.DARK_STEEL_UPGRADE_EMPOWERED_1.get().addAllVariants(modifier))
//        .lang("Empowered" + UPGRADE_TEXT)
//        .register();
//
//    public static final ItemEntry<DarkSteelUpgradeItem> DARK_STEEL_UPGRADE_EMPOWERED_2 = REGISTRATE
//        .item("dark_steel_upgrade_empowered_2",
//            properties -> new DarkSteelUpgradeItem(properties, EmpoweredUpgradeTier.TWO.getActivationCost(), EmpoweredUpgradeTier.TWO.getFactory()))
//        .tab(EIOCreativeTabs.GEAR, modifier -> ArmoryItems.DARK_STEEL_UPGRADE_EMPOWERED_2.get().addAllVariants(modifier))
//        .lang("Empowered II" + UPGRADE_TEXT)
//        .register();
//
//    public static final ItemEntry<DarkSteelUpgradeItem> DARK_STEEL_UPGRADE_EMPOWERED_3 = REGISTRATE
//        .item("dark_steel_upgrade_empowered_3",
//            properties -> new DarkSteelUpgradeItem(properties, EmpoweredUpgradeTier.THREE.getActivationCost(), EmpoweredUpgradeTier.THREE.getFactory()))
//        .tab(EIOCreativeTabs.GEAR, modifier -> ArmoryItems.DARK_STEEL_UPGRADE_EMPOWERED_3.get().addAllVariants(modifier))
//        .lang("Empowered III" + UPGRADE_TEXT)
//        .register();
//
//    public static final ItemEntry<DarkSteelUpgradeItem> DARK_STEEL_UPGRADE_EMPOWERED_4 = REGISTRATE
//        .item("dark_steel_upgrade_empowered_4",
//            properties -> new DarkSteelUpgradeItem(properties, EmpoweredUpgradeTier.FOUR.getActivationCost(), EmpoweredUpgradeTier.FOUR.getFactory()))
//        .tab(EIOCreativeTabs.GEAR, modifier -> ArmoryItems.DARK_STEEL_UPGRADE_EMPOWERED_4.get().addAllVariants(modifier))
//        .lang("Empowered IV" + UPGRADE_TEXT)
//        .register();
//
//    public static final ItemEntry<DarkSteelUpgradeItem> DARK_STEEL_UPGRADE_SPOON = REGISTRATE
//        .item("dark_steel_upgrade_spoon",
//            properties -> new DarkSteelUpgradeItem(properties, BaseConfig.COMMON.DARK_STEEL.SPOON_ACTIVATION_COST, SpoonUpgrade::new))
//        .tab(EIOCreativeTabs.GEAR, modifier -> ArmoryItems.DARK_STEEL_UPGRADE_SPOON.get().addAllVariants(modifier))
//        .lang("Spoon" + UPGRADE_TEXT)
//        .register();
//
//    public static final ItemEntry<DarkSteelUpgradeItem> DARK_STEEL_UPGRADE_FORK = REGISTRATE
//        .item("dark_steel_upgrade_fork",
//            properties -> new DarkSteelUpgradeItem(properties, BaseConfig.COMMON.DARK_STEEL.FORK_ACTIVATION_COST, ForkUpgrade::new))
//        .tab(EIOCreativeTabs.GEAR, modifier -> ArmoryItems.DARK_STEEL_UPGRADE_FORK.get().addAllVariants(modifier))
//        .lang("Fork" + UPGRADE_TEXT)
//        .register();
//
//    public static final ItemEntry<DarkSteelUpgradeItem> DARK_STEEL_UPGRADE_DIRECT = REGISTRATE
//        .item("dark_steel_upgrade_direct",
//            properties -> new DarkSteelUpgradeItem(properties, BaseConfig.COMMON.DARK_STEEL.DIRECT_ACTIVATION_COST, DirectUpgrade::new))
//        .tab(EIOCreativeTabs.GEAR, modifier -> ArmoryItems.DARK_STEEL_UPGRADE_DIRECT.get().addAllVariants(modifier))
//        .lang("Direct" + UPGRADE_TEXT)
//        .register();
//
//    public static final ItemEntry<DarkSteelUpgradeItem> DARK_STEEL_UPGRADE_EXPLOSIVE_1 = REGISTRATE
//        .item("dark_steel_upgrade_tnt",
//            properties -> new DarkSteelUpgradeItem(properties, ExplosiveUpgradeTier.ONE.getActivationCost(), ExplosiveUpgradeTier.ONE.getFactory()))
//        .tab(EIOCreativeTabs.GEAR, modifier -> ArmoryItems.DARK_STEEL_UPGRADE_EXPLOSIVE_1.get().addAllVariants(modifier))
//        .lang("Explosive I" + UPGRADE_TEXT)
//        .register();
//
//    public static final ItemEntry<DarkSteelUpgradeItem> DARK_STEEL_UPGRADE_EXPLOSIVE_2 = REGISTRATE
//        .item("dark_steel_upgrade_tnt1",
//            properties -> new DarkSteelUpgradeItem(properties, ExplosiveUpgradeTier.TWO.getActivationCost(), ExplosiveUpgradeTier.TWO.getFactory()))
//        .tab(EIOCreativeTabs.GEAR, modifier -> ArmoryItems.DARK_STEEL_UPGRADE_EXPLOSIVE_2.get().addAllVariants(modifier))
//        .lang("Explosive II" + UPGRADE_TEXT)
//        .register();
//
//    //TODO: Textures for dark_steel_upgrade_penetration_1 and dark_steel_upgrade_penetration_2 needed
//    public static final ItemEntry<DarkSteelUpgradeItem> DARK_STEEL_UPGRADE_EXPLOSIVE_PENETRATION_1 = REGISTRATE
//        .item("dark_steel_upgrade_penetration_1", properties -> new DarkSteelUpgradeItem(properties, ExplosivePenetrationUpgradeTier.ONE.getActivationCost(),
//            ExplosivePenetrationUpgradeTier.ONE.getFactory()))
//        .tab(EIOCreativeTabs.GEAR, modifier -> ArmoryItems.DARK_STEEL_UPGRADE_EXPLOSIVE_PENETRATION_1.get().addAllVariants(modifier))
//        .lang("Explosive Penetration I" + UPGRADE_TEXT)
//        .register();
//
//    public static final ItemEntry<DarkSteelUpgradeItem> DARK_STEEL_UPGRADE_EXPLOSIVE_PENETRATION_2 = REGISTRATE
//        .item("dark_steel_upgrade_penetration_2", properties -> new DarkSteelUpgradeItem(properties, ExplosivePenetrationUpgradeTier.TWO.getActivationCost(),
//            ExplosivePenetrationUpgradeTier.TWO.getFactory()))
//        .tab(EIOCreativeTabs.GEAR, modifier -> ArmoryItems.DARK_STEEL_UPGRADE_EXPLOSIVE_PENETRATION_2.get().addAllVariants(modifier))
//        .lang("Explosive Penetration II" + UPGRADE_TEXT)
//        .register();

    public static void register(IEventBus bus) {
        ITEM_REGISTRY.register(bus);
    }
}
