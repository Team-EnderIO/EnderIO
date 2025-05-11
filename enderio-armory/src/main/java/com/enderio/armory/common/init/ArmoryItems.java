package com.enderio.armory.common.init;

import com.enderio.armory.EnderIOArmory;
import com.enderio.armory.common.config.ArmoryConfig;
import com.enderio.armory.common.item.darksteel.DarkSteelAxeItem;
import com.enderio.armory.common.item.darksteel.DarkSteelBootsItem;
import com.enderio.armory.common.item.darksteel.DarkSteelChestplateItem;
import com.enderio.armory.common.item.darksteel.DarkSteelHelmetItem;
import com.enderio.armory.common.item.darksteel.DarkSteelLeggingsItem;
import com.enderio.armory.common.item.darksteel.DarkSteelPickaxeItem;
import com.enderio.armory.common.item.darksteel.DarkSteelSwordItem;
import com.enderio.armory.common.item.darksteel.DarkSteelUpgradeItem;
import com.enderio.armory.common.item.darksteel.upgrades.ForkUpgrade;
import com.enderio.armory.common.item.darksteel.upgrades.SpoonUpgrade;
import com.enderio.armory.common.item.darksteel.upgrades.StepAssistUpgrade;
import com.enderio.armory.common.item.darksteel.upgrades.direct.DirectUpgrade;
import com.enderio.armory.common.item.darksteel.upgrades.empowered.EmpoweredUpgradeTier;
import com.enderio.armory.common.item.darksteel.upgrades.explosive.ExplosivePenetrationUpgradeTier;
import com.enderio.armory.common.item.darksteel.upgrades.explosive.ExplosiveUpgradeTier;
import com.enderio.armory.common.item.darksteel.upgrades.flight.ElytraUpgrade;
import com.enderio.armory.common.item.darksteel.upgrades.flight.GliderUpgrade;
import com.enderio.armory.common.item.darksteel.upgrades.jump.JumpUpgradeTier;
import com.enderio.armory.common.item.darksteel.upgrades.nightvision.NightVisisionUpgrade;
import com.enderio.armory.common.item.darksteel.upgrades.solar.SolarUpgradeTier;
import com.enderio.armory.common.item.darksteel.upgrades.speed.SpeedUpgradeTier;
import com.enderio.armory.common.item.darksteel.upgrades.travel.TravelUpgrade;
import com.enderio.armory.common.tag.ArmoryTags;
import com.enderio.base.common.init.EIOCreativeTabs;
import com.enderio.base.common.item.misc.MaterialItem;
import com.enderio.base.common.tag.EIOTags;
import com.enderio.regilite.holder.RegiliteItem;
import com.enderio.regilite.registry.ItemRegistry;
import net.minecraft.tags.ItemTags;
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
            .setTab(EIOCreativeTabs.GEAR/*, modifier -> ArmoryItems.DARK_STEEL_SWORD.get().addAllVariants(modifier)*/)
            .setTranslation("The Ender")
            .setModelProvider((prov, ctx) -> prov.handheld(ctx.get()))
            .addItemTags(ArmoryTags.Items.DARK_STEEL_UPGRADEABLE_SWORD)
            .addItemTags(ItemTags.SWORDS)
            .addCapability(ArmoryCapabilities.DARK_STEEL_CAPABILITY, ArmoryCapabilities.DARK_STEEL_PROVIDER)
            .addCapability(Capabilities.EnergyStorage.ITEM, ArmoryCapabilities.DARK_STEEL_ENERGY_STORAGE_PROVIDER);

    public static final RegiliteItem<DarkSteelPickaxeItem> DARK_STEEL_PICKAXE = ITEM_REGISTRY
            .registerItem("dark_steel_pickaxe", DarkSteelPickaxeItem::new,
                    new Item.Properties().durability(2000).requiredFeatures(ArmoryFeatureFlags.ARMORY_REWRITE))
            .setTab(EIOCreativeTabs.GEAR/*, modifier -> ArmoryItems.DARK_STEEL_PICKAXE.get().addAllVariants(modifier)*/)
            .setTranslation("Dark Pickaxe")
            .setModelProvider((prov, ctx) -> prov.handheld(ctx.get()))
            .addItemTags(ArmoryTags.Items.DARK_STEEL_UPGRADEABLE_PICKAXE)
            .addItemTags(ItemTags.PICKAXES)
            .addCapability(ArmoryCapabilities.DARK_STEEL_CAPABILITY, ArmoryCapabilities.DARK_STEEL_PROVIDER)
            .addCapability(Capabilities.EnergyStorage.ITEM, ArmoryCapabilities.DARK_STEEL_ENERGY_STORAGE_PROVIDER);

    public static final RegiliteItem<DarkSteelAxeItem> DARK_STEEL_AXE = ITEM_REGISTRY
            .registerItem("dark_steel_axe", DarkSteelAxeItem::new,
                    new Item.Properties().durability(2000).requiredFeatures(ArmoryFeatureFlags.ARMORY_REWRITE))
            .setTab(EIOCreativeTabs.GEAR/*, modifier -> ArmoryItems.DARK_STEEL_AXE.get().addAllVariants(modifier)*/)
            .setTranslation("Dark Axe")
            .setModelProvider((prov, ctx) -> prov.handheld(ctx.get()))
            .addItemTags(ArmoryTags.Items.DARK_STEEL_UPGRADEABLE_AXE)
            .addItemTags(ItemTags.AXES)
            .addCapability(ArmoryCapabilities.DARK_STEEL_CAPABILITY, ArmoryCapabilities.DARK_STEEL_PROVIDER)
            .addCapability(Capabilities.EnergyStorage.ITEM, ArmoryCapabilities.DARK_STEEL_ENERGY_STORAGE_PROVIDER);

    public static final RegiliteItem<DarkSteelHelmetItem> DARK_STEEL_HELMET = ITEM_REGISTRY
            .registerItem("dark_steel_helmet", DarkSteelHelmetItem::new,
                    new Item.Properties().durability(2000).requiredFeatures(ArmoryFeatureFlags.ARMORY_REWRITE))
            .setTab(EIOCreativeTabs.GEAR/*, modifier -> ArmoryItems.DARK_STEEL_HELMET.get().addAllVariants(modifier)*/)
            .setTranslation("Dark Helmet")
            .setModelProvider((prov, ctx) -> prov.handheld(ctx.get()))
            .addItemTags(ArmoryTags.Items.DARK_STEEL_UPGRADEABLE_HELMET)
            .addItemTags(ItemTags.HEAD_ARMOR_ENCHANTABLE)
            .addItemTags(ItemTags.HEAD_ARMOR)
            .addCapability(ArmoryCapabilities.DARK_STEEL_CAPABILITY, ArmoryCapabilities.DARK_STEEL_PROVIDER)
            .addCapability(Capabilities.EnergyStorage.ITEM, ArmoryCapabilities.DARK_STEEL_ENERGY_STORAGE_PROVIDER);

    public static final RegiliteItem<DarkSteelChestplateItem> DARK_STEEL_CHESTPLATE = ITEM_REGISTRY
            .registerItem("dark_steel_chestplate", DarkSteelChestplateItem::new,
                    new Item.Properties().durability(2000).requiredFeatures(ArmoryFeatureFlags.ARMORY_REWRITE))
            .setTab(EIOCreativeTabs.GEAR/*, modifier -> ArmoryItems.DARK_STEEL_CHESTPLATE.get().addAllVariants(modifier)*/)
            .setTranslation("Dark Chestplate")
            .setModelProvider((prov, ctx) -> prov.handheld(ctx.get()))
            .addItemTags(ArmoryTags.Items.DARK_STEEL_UPGRADEABLE_CHESTPLATE)
            .addItemTags(ItemTags.CHEST_ARMOR_ENCHANTABLE)
            .addItemTags(ItemTags.CHEST_ARMOR)
            .addCapability(ArmoryCapabilities.DARK_STEEL_CAPABILITY, ArmoryCapabilities.DARK_STEEL_PROVIDER)
            .addCapability(Capabilities.EnergyStorage.ITEM, ArmoryCapabilities.DARK_STEEL_ENERGY_STORAGE_PROVIDER);

    public static final RegiliteItem<DarkSteelLeggingsItem> DARK_STEEL_LEGGINGS = ITEM_REGISTRY
            .registerItem("dark_steel_leggings", DarkSteelLeggingsItem::new,
                    new Item.Properties().durability(2000).requiredFeatures(ArmoryFeatureFlags.ARMORY_REWRITE))
            .setTab(EIOCreativeTabs.GEAR/*, modifier -> ArmoryItems.DARK_STEEL_LEGGINGS.get().addAllVariants(modifier)*/)
            .setTranslation("Dark Leggings")
            .setModelProvider((prov, ctx) -> prov.handheld(ctx.get()))
            .addItemTags(ArmoryTags.Items.DARK_STEEL_UPGRADEABLE_LEGGINGS)
            .addItemTags(ItemTags.LEG_ARMOR_ENCHANTABLE)
            .addItemTags(ItemTags.LEG_ARMOR)
            .addCapability(ArmoryCapabilities.DARK_STEEL_CAPABILITY, ArmoryCapabilities.DARK_STEEL_PROVIDER)
            .addCapability(Capabilities.EnergyStorage.ITEM, ArmoryCapabilities.DARK_STEEL_ENERGY_STORAGE_PROVIDER);

    public static final RegiliteItem<DarkSteelBootsItem> DARK_STEEL_BOOTS = ITEM_REGISTRY
            .registerItem("dark_steel_boots", DarkSteelBootsItem::new,
                    new Item.Properties().durability(2000).requiredFeatures(ArmoryFeatureFlags.ARMORY_REWRITE))
            .setTab(EIOCreativeTabs.GEAR/*, modifier -> ArmoryItems.DARK_STEEL_BOOTS.get().addAllVariants(modifier)*/)
            .setTranslation("Dark Boots")
            .setModelProvider((prov, ctx) -> prov.handheld(ctx.get()))
            .addItemTags(ArmoryTags.Items.DARK_STEEL_UPGRADEABLE_BOOTS)
            .addItemTags(ItemTags.FOOT_ARMOR_ENCHANTABLE)
            .addItemTags(ItemTags.FOOT_ARMOR)
            .addCapability(ArmoryCapabilities.DARK_STEEL_CAPABILITY, ArmoryCapabilities.DARK_STEEL_PROVIDER)
            .addCapability(Capabilities.EnergyStorage.ITEM, ArmoryCapabilities.DARK_STEEL_ENERGY_STORAGE_PROVIDER);

    private static final String UPGRADE_TEXT = " Upgrade";

    public static final RegiliteItem<MaterialItem> DARK_STEEL_UPGRADE_BLANK = ITEM_REGISTRY
            .registerItem("dark_steel_upgrade_blank", props -> new MaterialItem(props, false),
                    new Item.Properties().requiredFeatures(ArmoryFeatureFlags.ARMORY_REWRITE))
            .setTab(EIOCreativeTabs.GEAR)
            .setTranslation("Blank" + UPGRADE_TEXT)
            .setModelProvider((prov, ctx) -> prov.handheld(ctx.get()));

    public static final RegiliteItem<DarkSteelUpgradeItem> DARK_STEEL_UPGRADE_EMPOWERED_1 = ITEM_REGISTRY
            .registerItem("dark_steel_upgrade_empowered_1",
                    properties -> new DarkSteelUpgradeItem(properties, EmpoweredUpgradeTier.ONE.getActivationCost(),
                            EmpoweredUpgradeTier.ONE.getFactory()),
                    new Item.Properties().requiredFeatures(ArmoryFeatureFlags.ARMORY_REWRITE))
            .setTab(EIOCreativeTabs.GEAR)
            .setTranslation("Empowered" + UPGRADE_TEXT)
            .setModelProvider((prov, ctx) -> prov.handheld(ctx.get()));

    public static final RegiliteItem<DarkSteelUpgradeItem> DARK_STEEL_UPGRADE_EMPOWERED_2 = ITEM_REGISTRY
            .registerItem("dark_steel_upgrade_empowered_2",
                    properties -> new DarkSteelUpgradeItem(properties, EmpoweredUpgradeTier.TWO.getActivationCost(),
                            EmpoweredUpgradeTier.TWO.getFactory()),
                    new Item.Properties().requiredFeatures(ArmoryFeatureFlags.ARMORY_REWRITE))
            .setTab(EIOCreativeTabs.GEAR)
            .setTranslation("Empowered II" + UPGRADE_TEXT)
            .setModelProvider((prov, ctx) -> prov.handheld(ctx.get()));

    public static final RegiliteItem<DarkSteelUpgradeItem> DARK_STEEL_UPGRADE_EMPOWERED_3 = ITEM_REGISTRY
            .registerItem("dark_steel_upgrade_empowered_3",
                    properties -> new DarkSteelUpgradeItem(properties, EmpoweredUpgradeTier.THREE.getActivationCost(),
                            EmpoweredUpgradeTier.THREE.getFactory()),
                    new Item.Properties().requiredFeatures(ArmoryFeatureFlags.ARMORY_REWRITE))
            .setTab(EIOCreativeTabs.GEAR)
            .setTranslation("Empowered III" + UPGRADE_TEXT)
            .setModelProvider((prov, ctx) -> prov.handheld(ctx.get()));

    public static final RegiliteItem<DarkSteelUpgradeItem> DARK_STEEL_UPGRADE_EMPOWERED_4 = ITEM_REGISTRY
            .registerItem("dark_steel_upgrade_empowered_4",
                    properties -> new DarkSteelUpgradeItem(properties, EmpoweredUpgradeTier.FOUR.getActivationCost(),
                            EmpoweredUpgradeTier.FOUR.getFactory()),
                    new Item.Properties().requiredFeatures(ArmoryFeatureFlags.ARMORY_REWRITE))
            .setTab(EIOCreativeTabs.GEAR)
            .setTranslation("Empowered IV" + UPGRADE_TEXT)
            .setModelProvider((prov, ctx) -> prov.handheld(ctx.get()));

    public static final RegiliteItem<DarkSteelUpgradeItem> DARK_STEEL_UPGRADE_STEP_ASSIST = ITEM_REGISTRY
            .registerItem("dark_steel_upgrade_step_assist",
                    properties -> new DarkSteelUpgradeItem(properties, ArmoryConfig.COMMON.STEP_ASSIST_ACTIVATION_COST,
                            StepAssistUpgrade::new),
                    new Item.Properties().requiredFeatures(ArmoryFeatureFlags.ARMORY_REWRITE))
            .setTab(EIOCreativeTabs.GEAR)
            .setTranslation("Step Assist" + UPGRADE_TEXT)
            .setModelProvider((prov, ctx) -> prov.handheld(ctx.get()));

    public static final RegiliteItem<DarkSteelUpgradeItem> DARK_STEEL_UPGRADE_SPEED_I = ITEM_REGISTRY
            .registerItem("dark_steel_upgrade_speedboost1",
                    properties -> new DarkSteelUpgradeItem(properties, ArmoryConfig.COMMON.SPEED_ACTIVATION_COST_I,
                            SpeedUpgradeTier.ONE.getFactory()),
                    new Item.Properties().requiredFeatures(ArmoryFeatureFlags.ARMORY_REWRITE))
            .setTab(EIOCreativeTabs.GEAR)
            .setTranslation("Speed" + UPGRADE_TEXT)
            .setModelProvider((prov, ctx) -> prov.handheld(ctx.get()));

    public static final RegiliteItem<DarkSteelUpgradeItem> DARK_STEEL_UPGRADE_SPEED_II = ITEM_REGISTRY
            .registerItem("dark_steel_upgrade_speedboost2",
                    properties -> new DarkSteelUpgradeItem(properties, ArmoryConfig.COMMON.SPEED_ACTIVATION_COST_II,
                            SpeedUpgradeTier.TWO.getFactory()),
                    new Item.Properties().requiredFeatures(ArmoryFeatureFlags.ARMORY_REWRITE))
            .setTab(EIOCreativeTabs.GEAR)
            .setTranslation("Speed II" + UPGRADE_TEXT)
            .setModelProvider((prov, ctx) -> prov.handheld(ctx.get()));

    public static final RegiliteItem<DarkSteelUpgradeItem> DARK_STEEL_UPGRADE_SPEED_III = ITEM_REGISTRY
            .registerItem("dark_steel_upgrade_speedboost3",
                    properties -> new DarkSteelUpgradeItem(properties, ArmoryConfig.COMMON.SPEED_ACTIVATION_COST_III,
                            SpeedUpgradeTier.THREE.getFactory()),
                    new Item.Properties().requiredFeatures(ArmoryFeatureFlags.ARMORY_REWRITE))
            .setTab(EIOCreativeTabs.GEAR)
            .setTranslation("Speed III" + UPGRADE_TEXT)
            .setModelProvider((prov, ctx) -> prov.handheld(ctx.get()));

    public static final RegiliteItem<DarkSteelUpgradeItem> DARK_STEEL_UPGRADE_JUMP_I = ITEM_REGISTRY
            .registerItem("dark_steel_upgrade_jump_1",
                    properties -> new DarkSteelUpgradeItem(properties, ArmoryConfig.COMMON.JUMP_ACTIVATION_COST_I,
                            JumpUpgradeTier.ONE.getFactory()),
                    new Item.Properties().requiredFeatures(ArmoryFeatureFlags.ARMORY_REWRITE))
            .setTab(EIOCreativeTabs.GEAR)
            .setTranslation("Jump" + UPGRADE_TEXT)
            .setModelProvider((prov, ctx) -> prov.handheld(ctx.get()));

    public static final RegiliteItem<DarkSteelUpgradeItem> DARK_STEEL_UPGRADE_JUMP_II = ITEM_REGISTRY
            .registerItem("dark_steel_upgrade_jump_2",
                    properties -> new DarkSteelUpgradeItem(properties, ArmoryConfig.COMMON.JUMP_ACTIVATION_COST_II,
                            JumpUpgradeTier.TWO.getFactory()),
                    new Item.Properties().requiredFeatures(ArmoryFeatureFlags.ARMORY_REWRITE))
            .setTab(EIOCreativeTabs.GEAR)
            .setTranslation("Jump II" + UPGRADE_TEXT)
            .setModelProvider((prov, ctx) -> prov.handheld(ctx.get()));

    public static final RegiliteItem<DarkSteelUpgradeItem> DARK_STEEL_UPGRADE_GLIDER = ITEM_REGISTRY
            .registerItem("dark_steel_upgrade_glider",
                    properties -> new DarkSteelUpgradeItem(properties, ArmoryConfig.COMMON.GLIDER_ACTIVATION_COST,
                            GliderUpgrade::new),
                    new Item.Properties().requiredFeatures(ArmoryFeatureFlags.ARMORY_REWRITE))
            .setTab(EIOCreativeTabs.GEAR)
            .setTranslation("Glider" + UPGRADE_TEXT)
            .setModelProvider((prov, ctx) -> prov.handheld(ctx.get()));

    public static final RegiliteItem<DarkSteelUpgradeItem> DARK_STEEL_UPGRADE_ELYTRA = ITEM_REGISTRY
            .registerItem("dark_steel_upgrade_elytra",
                    properties -> new DarkSteelUpgradeItem(properties, ArmoryConfig.COMMON.ELYTRA_ACTIVATION_COST,
                            ElytraUpgrade::new),
                    new Item.Properties().requiredFeatures(ArmoryFeatureFlags.ARMORY_REWRITE))
            .setTab(EIOCreativeTabs.GEAR)
            .setTranslation("Elytra" + UPGRADE_TEXT)
            .setModelProvider((prov, ctx) -> prov.handheld(ctx.get()));

    public static final RegiliteItem<DarkSteelUpgradeItem> DARK_STEEL_UPGRADE_SPOON = ITEM_REGISTRY
            .registerItem("dark_steel_upgrade_spoon",
                    properties -> new DarkSteelUpgradeItem(properties, ArmoryConfig.COMMON.SPOON_ACTIVATION_COST,
                            SpoonUpgrade::new),
                    new Item.Properties().requiredFeatures(ArmoryFeatureFlags.ARMORY_REWRITE))
            .setTab(EIOCreativeTabs.GEAR)
            .setTranslation("Spoon" + UPGRADE_TEXT)
            .setModelProvider((prov, ctx) -> prov.handheld(ctx.get()));

    public static final RegiliteItem<DarkSteelUpgradeItem> DARK_STEEL_UPGRADE_FORK = ITEM_REGISTRY
            .registerItem("dark_steel_upgrade_fork",
                    properties -> new DarkSteelUpgradeItem(properties, ArmoryConfig.COMMON.FORK_ACTIVATION_COST,
                            ForkUpgrade::new),
                    new Item.Properties().requiredFeatures(ArmoryFeatureFlags.ARMORY_REWRITE))
            .setTab(EIOCreativeTabs.GEAR)
            .setTranslation("Fork" + UPGRADE_TEXT)
            .setModelProvider((prov, ctx) -> prov.handheld(ctx.get()));

    public static final RegiliteItem<DarkSteelUpgradeItem> DARK_STEEL_UPGRADE_DIRECT = ITEM_REGISTRY
            .registerItem("dark_steel_upgrade_direct",
                    properties -> new DarkSteelUpgradeItem(properties, ArmoryConfig.COMMON.DIRECT_ACTIVATION_COST,
                            DirectUpgrade::new),
                    new Item.Properties().requiredFeatures(ArmoryFeatureFlags.ARMORY_REWRITE))
            .setTab(EIOCreativeTabs.GEAR)
            .setTranslation("Direct" + UPGRADE_TEXT)
            .setModelProvider((prov, ctx) -> prov.handheld(ctx.get()));

    public static final RegiliteItem<DarkSteelUpgradeItem> DARK_STEEL_UPGRADE_TRAVEL = ITEM_REGISTRY
            .registerItem("dark_steel_upgrade_travel",
                    properties -> new DarkSteelUpgradeItem(properties, ArmoryConfig.COMMON.TRAVEL_ACTIVATION_COST,
                            TravelUpgrade::new),
                    new Item.Properties().requiredFeatures(ArmoryFeatureFlags.ARMORY_REWRITE))
            .setTab(EIOCreativeTabs.GEAR)
            .setTranslation("Travel" + UPGRADE_TEXT)
            .setModelProvider((prov, ctx) -> prov.handheld(ctx.get()));

    public static final RegiliteItem<DarkSteelUpgradeItem> DARK_STEEL_UPGRADE_EXPLOSIVE_1 = ITEM_REGISTRY
            .registerItem("dark_steel_upgrade_tnt",
                    properties -> new DarkSteelUpgradeItem(properties, ExplosiveUpgradeTier.ONE.getActivationCost(),
                            ExplosiveUpgradeTier.ONE.getFactory()),
                    new Item.Properties().requiredFeatures(ArmoryFeatureFlags.ARMORY_REWRITE))
            .setTab(EIOCreativeTabs.GEAR)
            .setTranslation("Explosive I" + UPGRADE_TEXT)
            .setModelProvider((prov, ctx) -> prov.handheld(ctx.get()));

    public static final RegiliteItem<DarkSteelUpgradeItem> DARK_STEEL_UPGRADE_EXPLOSIVE_2 = ITEM_REGISTRY
            .registerItem("dark_steel_upgrade_tnt2",
                    properties -> new DarkSteelUpgradeItem(properties, ExplosiveUpgradeTier.TWO.getActivationCost(),
                            ExplosiveUpgradeTier.TWO.getFactory()),
                    new Item.Properties().requiredFeatures(ArmoryFeatureFlags.ARMORY_REWRITE))
            .setTab(EIOCreativeTabs.GEAR)
            .setTranslation("Explosive II" + UPGRADE_TEXT)
            .setModelProvider((prov, ctx) -> prov.handheld(ctx.get()));

    public static final RegiliteItem<DarkSteelUpgradeItem> DARK_STEEL_UPGRADE_EXPLOSIVE_PENETRATION_1 = ITEM_REGISTRY
            .registerItem("dark_steel_upgrade_penetration_1",
                    properties -> new DarkSteelUpgradeItem(properties,
                            ExplosivePenetrationUpgradeTier.ONE.getActivationCost(),
                            ExplosivePenetrationUpgradeTier.ONE.getFactory()),
                    new Item.Properties().requiredFeatures(ArmoryFeatureFlags.ARMORY_REWRITE))
            .setTab(EIOCreativeTabs.GEAR)
            .setTranslation("EExplosive Penetration I" + UPGRADE_TEXT)
            .setModelProvider((prov, ctx) -> prov.handheld(ctx.get()));

    public static final RegiliteItem<DarkSteelUpgradeItem> DARK_STEEL_UPGRADE_EXPLOSIVE_PENETRATION_2 = ITEM_REGISTRY
            .registerItem("dark_steel_upgrade_penetration_2",
                    properties -> new DarkSteelUpgradeItem(properties,
                            ExplosivePenetrationUpgradeTier.TWO.getActivationCost(),
                            ExplosivePenetrationUpgradeTier.TWO.getFactory()),
                    new Item.Properties().requiredFeatures(ArmoryFeatureFlags.ARMORY_REWRITE))
            .setTab(EIOCreativeTabs.GEAR)
            .setTranslation("EExplosive Penetration II" + UPGRADE_TEXT)
            .setModelProvider((prov, ctx) -> prov.handheld(ctx.get()));

    public static final RegiliteItem<DarkSteelUpgradeItem> DARK_STEEL_UPGRADE_NIGHT_VISION = ITEM_REGISTRY
            .registerItem("dark_steel_upgrade_nightvision",
                    properties -> new DarkSteelUpgradeItem(properties, ArmoryConfig.COMMON.NIGHT_VISION_ACTIVATION_COST,
                            NightVisisionUpgrade::new),
                    new Item.Properties().requiredFeatures(ArmoryFeatureFlags.ARMORY_REWRITE))
            .setTab(EIOCreativeTabs.GEAR)
            .setTranslation("Night Vision" + UPGRADE_TEXT)
            .setModelProvider((prov, ctx) -> prov.handheld(ctx.get()));

    public static final RegiliteItem<DarkSteelUpgradeItem> DARK_STEEL_UPGRADE_SOLAR_I = ITEM_REGISTRY
            .registerItem("dark_steel_upgrade_solar_1",
                    properties -> new DarkSteelUpgradeItem(properties, ArmoryConfig.COMMON.SOLAR_ACTIVATION_COST_I,
                            SolarUpgradeTier.ONE.getFactory()),
                    new Item.Properties().requiredFeatures(ArmoryFeatureFlags.ARMORY_REWRITE))
            .setTab(EIOCreativeTabs.GEAR)
            .setTranslation("Solar" + UPGRADE_TEXT)
            .setModelProvider((prov, ctx) -> prov.handheld(ctx.get()));

    public static final RegiliteItem<DarkSteelUpgradeItem> DARK_STEEL_UPGRADE_SOLAR_II = ITEM_REGISTRY
            .registerItem("dark_steel_upgrade_solar_2",
                    properties -> new DarkSteelUpgradeItem(properties, ArmoryConfig.COMMON.SOLAR_ACTIVATION_COST_II,
                            SolarUpgradeTier.TWO.getFactory()),
                    new Item.Properties().requiredFeatures(ArmoryFeatureFlags.ARMORY_REWRITE))
            .setTab(EIOCreativeTabs.GEAR)
            .setTranslation("Solar II" + UPGRADE_TEXT)
            .setModelProvider((prov, ctx) -> prov.handheld(ctx.get()));

    public static final RegiliteItem<DarkSteelUpgradeItem> DARK_STEEL_UPGRADE_SOLAR_III = ITEM_REGISTRY
            .registerItem("dark_steel_upgrade_solar_3",
                    properties -> new DarkSteelUpgradeItem(properties, ArmoryConfig.COMMON.SOLAR_ACTIVATION_COST_III,
                            SolarUpgradeTier.THREE.getFactory()),
                    new Item.Properties().requiredFeatures(ArmoryFeatureFlags.ARMORY_REWRITE))
            .setTab(EIOCreativeTabs.GEAR)
            .setTranslation("Solar III" + UPGRADE_TEXT)
            .setModelProvider((prov, ctx) -> prov.handheld(ctx.get()));

    public static void register(IEventBus bus) {
        ITEM_REGISTRY.register(bus);
    }
}
