package com.enderio.enderio.datagen.client;

import com.enderio.core.common.registries.FluidDeferredHolders;
import com.enderio.enderio.EnderIO;
import com.enderio.enderio.api.EnderIORegistries;
import com.enderio.enderio.api.conduits.Conduit;
import com.enderio.enderio.api.io.RedstoneControl;
import com.enderio.enderio.compat.jei.JEILang;
import com.enderio.enderio.content.advancements.AdvancementsLang;
import com.enderio.enderio.content.armory.ArmoryLang;
import com.enderio.enderio.content.capacitors.CapacitorLang;
import com.enderio.enderio.content.conduits.ConduitLang;
import com.enderio.enderio.content.filters.FiltersLang;
import com.enderio.enderio.content.filters.item.general.DamageFilterMode;
import com.enderio.enderio.content.glass.GlassCollisionPredicate;
import com.enderio.enderio.content.glass.GlassLang;
import com.enderio.enderio.content.glass.GlassLighting;
import com.enderio.enderio.content.machines.MachinesLang;
import com.enderio.enderio.content.machines.alloy.AlloySmelterMode;
import com.enderio.enderio.content.machines.powered_spawner.PoweredSpawnerMode;
import com.enderio.enderio.content.tools.ToolsLang;
import com.enderio.enderio.foundation.lang.EIOCommonLang;
import com.enderio.enderio.foundation.tag.EIOTags;
import com.enderio.enderio.init.EIOBlocks;
import com.enderio.enderio.init.EIOConduits;
import com.enderio.enderio.init.EIOEntities;
import com.enderio.enderio.init.EIOFluids;
import com.enderio.enderio.init.EIOItems;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Util;
import net.minecraft.world.item.DyeColor;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.fluids.FluidType;

import java.util.Locale;

public class EIOLanguageProvider extends LanguageProvider {
    public EIOLanguageProvider(PackOutput output) {
        super(output, EnderIO.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations() {
        addTags();
        addCapacitorTooltips();
        addEnumNames();
        addArmoryLang();
        addConduitDescriptions();
        addConduitLang();
        addEntities();
        addToolsLang();
        addFiltersLang();
        addJeiLang();
        addGlassLang();
        addMachineLang();
        addItems();
        addBlocks();
        addFluids();
        addAdvancementsLang();
        addCommonLang();
    }

    private void addTags() {
        add(EIOTags.Items.GRINDING_BALLS, "Grinding Balls");
        add(EIOTags.Items.HIDE_FACADES, "Hides Facades");
        add(EIOTags.Items.GLIDER, "Gliders");
        add(EIOTags.Items.INSULATION_METAL, "Insulation Metal");
        add(EIOTags.Items.BROKEN_SPAWNER_BLACKLIST, "Broken Spawner Blacklist");
        add(EIOTags.Items.ELECTROMAGNET_BLACKLIST, "Electromagnet Blacklist");

        // TODO: Glass tags?

        add(EIOTags.Items.SEEDS, "Seeds");
        add(EIOTags.Items.CROPS, "Crops");
        add(EIOTags.Items.MEAT, "Meat");
        add(EIOTags.Items.EXPLOSIVES, "Explosives");
        add(EIOTags.Items.BLAZE_POWDER, "Blaze Powder");
        add(EIOTags.Items.NATURAL_LIGHTS, "Natural Lights");
        add(EIOTags.Items.SUNFLOWER, "Sunflower");
        add(EIOTags.Items.AMETHYST, "Amethyst");
        add(EIOTags.Items.CLOUD_COLD, "Cloud Cold");
        add(EIOTags.Items.PRISMARINE, "Prismarine");
        add(EIOTags.Items.LIGHTNING_ROD, "Lightning Rod");
        add(EIOTags.Items.WIND_CHARGES, "Wind Charges");

        add(EIOTags.Blocks.BLOCKS_TELEPORTATION, "Prevents Teleportation");
        add(EIOTags.Blocks.REDSTONE_CONNECTABLE, "Redstone Connectable");
        add(EIOTags.Blocks.RANGE_EXTENDER, "Range Extender");
        add(EIOTags.Blocks.MIND_KILLER, "Mind Killer");

        add(EIOTags.Fluids.COLD_FIRE_IGNITER_FUEL, "Cold Fire Igniter Fuel");
        add(EIOTags.Fluids.STAFF_OF_LEVITY_FUEL, "Staff of Levity Fuel");
        add(EIOTags.Fluids.SOLAR_PANEL_LIGHT, "Solar Panel Light");
        add(EIOTags.Fluids.SOLAR_PANEL_DARK, "Solar Panel Dark");

        add(EIOTags.EntityTypes.SPAWNER_BLACKLIST, "Spawner Blacklist");
        add(EIOTags.EntityTypes.SOUL_VIAL_BLACKLIST, "Soul Vial Blacklist");
        add(EIOTags.EntityTypes.SOUL_VIAL_WHITELIST, "Soul Vial Whitelist");
    }

    private void addCapacitorTooltips() {
        add(CapacitorLang.CAPACITOR_TOOLTIP_BASE, "Base Modifier: %s");
        add(CapacitorLang.CAPACITOR_TOOLTIP_ENERGY_CAPACITY, "Energy Capacity Modifier: %s");
        add(CapacitorLang.CAPACITOR_TOOLTIP_ENERGY_USE, "Energy Use Modifier: %s");
        add(CapacitorLang.CAPACITOR_TOOLTIP_FUEL_EFFICIENCY, "Fuel Efficiency Modifier: %s");
        add(CapacitorLang.CAPACITOR_TOOLTIP_BURNING_ENERGY_GENERATION, "Burning Energy Generation Modifier: %s");

        add(CapacitorLang.LOOT_CAPACITOR_BASE_DUD, "Capacitor Dud");
        add(CapacitorLang.LOOT_CAPACITOR_BASE_NORMAL, "Capacitor");
        add(CapacitorLang.LOOT_CAPACITOR_BASE_ENHANCED, "Enhanced Capacitor");
        add(CapacitorLang.LOOT_CAPACITOR_BASE_WONDER, "Wonder Capacitor");
        add(CapacitorLang.LOOT_CAPACITOR_BASE_IMPOSSIBLE, "Impossible Capacitor");

        add(CapacitorLang.LOOT_CAPACITOR_TYPE_ENERGY_CAPACITY, "Insatiable");
        add(CapacitorLang.LOOT_CAPACITOR_TYPE_ENERGY_USE, "Hungry");
        add(CapacitorLang.LOOT_CAPACITOR_TYPE_FUEL_EFFICIENCY, "Efficient");
        add(CapacitorLang.LOOT_CAPACITOR_TYPE_BURNING_ENERGY_GENERATION, "Hot");
        add(CapacitorLang.LOOT_CAPACITOR_TYPE_UNKNOWN, "Mystery");

        add(CapacitorLang.LOOT_CAPACITOR_MODIFIER_FAILED, "Failed");
        add(CapacitorLang.LOOT_CAPACITOR_MODIFIER_SIMPLE, "Simple");
        add(CapacitorLang.LOOT_CAPACITOR_MODIFIER_NICE, "Nice");
        add(CapacitorLang.LOOT_CAPACITOR_MODIFIER_GOOD, "Good");
        add(CapacitorLang.LOOT_CAPACITOR_MODIFIER_ENHANCED, "Enhanced");
        add(CapacitorLang.LOOT_CAPACITOR_MODIFIER_PREMIUM, "Premium");
        add(CapacitorLang.LOOT_CAPACITOR_MODIFIER_INCREDIBLY, "Incredibly");
        add(CapacitorLang.LOOT_CAPACITOR_MODIFIER_UNSTABLE, "Unstable");
    }

    // Ignore nulls here, we know what should have lang keys.
    @SuppressWarnings("DataFlowIssue")
    private void addEnumNames() {
        add(RedstoneControl.ALWAYS_ACTIVE.getComponent(), "Always Active");
        add(RedstoneControl.ACTIVE_WITH_SIGNAL.getComponent(), "Active With Signal");
        add(RedstoneControl.ACTIVE_WITHOUT_SIGNAL.getComponent(), "Active without Signal");
        add(RedstoneControl.NEVER_ACTIVE.getComponent(), "Never Active");

        // region Filters

        add(DamageFilterMode.IGNORE.getComponent(), "Ignore Damage");
        add(DamageFilterMode.UP_TO_25.getComponent(), "Up to 25%% Damaged");
        add(DamageFilterMode.MORE_THAN_25.getComponent(), "More than 25%% Damaged");
        add(DamageFilterMode.UP_TO_50.getComponent(), "Up to 50%% Damaged");
        add(DamageFilterMode.MORE_THAN_50.getComponent(), "More than 50%% Damaged");
        add(DamageFilterMode.UP_TO_75.getComponent(), "Up to 75%% Damaged");
        add(DamageFilterMode.MORE_THAN_75.getComponent(), "More than 75%% Damaged");
        add(DamageFilterMode.NOT_DAMAGED.getComponent(), "Not Damaged");
        add(DamageFilterMode.ONLY_DAMAGED.getComponent(), "Only Damaged");
        add(DamageFilterMode.IS_DAMAGEABLE.getComponent(), "Can Be Damaged");
        add(DamageFilterMode.NOT_DAMAGEABLE.getComponent(), "Cannot Be Damaged");

        // endregion

        // region Glass

        add(GlassCollisionPredicate.PLAYERS_PASS.getComponent(), "Not solid to players");
        add(GlassCollisionPredicate.PLAYERS_BLOCK.getComponent(), "Only solid to players");
        add(GlassCollisionPredicate.MOBS_PASS.getComponent(), "Not solid to monsters");
        add(GlassCollisionPredicate.MOBS_BLOCK.getComponent(), "Only solid to monsters");
        add(GlassCollisionPredicate.ANIMALS_PASS.getComponent(), "Not solid to animals");
        add(GlassCollisionPredicate.ANIMALS_BLOCK.getComponent(), "Only solid to animals");

        // endregion

        // region Machines

        add(AlloySmelterMode.ALL.getComponent(), "Alloying and Smelting");
        add(AlloySmelterMode.ALLOYS.getComponent(), "Alloying Only");
        add(AlloySmelterMode.FURNACE.getComponent(), "Smelting Only");

        add(PoweredSpawnerMode.SPAWN.getComponent(), "Spawn Mobs");
        add(PoweredSpawnerMode.CAPTURE.getComponent(), "Capture Mobs");

        // endregion
    }

    private void addArmoryLang() {
        add(ArmoryLang.ENDER_HEAD_DROP_CHANCE, "%s%% chance to drop a mob head");
        add(ArmoryLang.DURABILITY_AMOUNT, "Durability %s");
    }

    private void addConduitDescriptions() {
        add(EIOConduits.ENERGY, "Energy Conduit");
        add(EIOConduits.ENHANCED_ENERGY, "Enhanced Energy Conduit");
        add(EIOConduits.ENDER_ENERGY, "Ender Energy Conduit");
        add(EIOConduits.REDSTONE, "Redstone Conduit");
        add(EIOConduits.FLUID, "Fluid Conduit");
        add(EIOConduits.PRESSURIZED_FLUID, "Pressurized Fluid Conduit");
        add(EIOConduits.ENDER_FLUID, "Ender Fluid Conduit");
        add(EIOConduits.ITEM, "Item Conduit");
        add(EIOConduits.ENHANCED_ITEM, "Enhanced Item Conduit");
        add(EIOConduits.ENDER_ITEM, "Ender Item Conduit");
    }

    private void addConduitLang() {
        add(ConduitLang.CHANNEL, "Channel");
        add(ConduitLang.REDSTONE_CHANNEL, "Signal Color");
        add(ConduitLang.ROUND_ROBIN_ENABLED, "Round Robin Enabled");
        add(ConduitLang.ROUND_ROBIN_DISABLED, "Round Robin Disabled");
        add(ConduitLang.SELF_FEED_ENABLED, "Self Feed Enabled");
        add(ConduitLang.SELF_FEED_DISABLED, "Self Feed Disabled");

        add(ConduitLang.INSERT, "Insert");
        add(ConduitLang.EXTRACT, "Extract");
        add(ConduitLang.INPUT, "Input");
        add(ConduitLang.OUTPUT, "Output");
        add(ConduitLang.PRIORITY, "Priority");

        add(ConduitLang.ERROR_NO_SCREEN_TYPE, "Error: No screen type defined");

        add(ConduitLang.FLUID_CONDUIT_CHANGE_FLUID1, "Locked Fluid: ");
        add(ConduitLang.FLUID_CONDUIT_CHANGE_FLUID2, "Click to reset!");
        add(ConduitLang.FLUID_CONDUIT_CHANGE_FLUID3, "Fluid: %s");

        add(ConduitLang.REDSTONE_CONDUIT_SIGNAL_COLOR, "Signal Color");
        add(ConduitLang.REDSTONE_CONDUIT_STRONG_SIGNAL, "Strong Signal");

        add(ConduitLang.GRAPH_TICK_RATE_TOOLTIP, "Network Ticks: %s/sec");
        add(ConduitLang.ENERGY_RATE_TOOLTIP, "Max Output %s \u00B5I/t");
        add(ConduitLang.FLUID_RAW_RATE_TOOLTIP, "Rate: %s mB/network tick");
        add(ConduitLang.FLUID_EFFECTIVE_RATE_TOOLTIP, "Effective Rate: %s mB/t");
        add(ConduitLang.MULTI_FLUID_TOOLTIP, "Allows multiple fluids to be transported on the same line");
        add(ConduitLang.ITEM_RAW_RATE_TOOLTIP, "Rate: %s Items/network tick");
        add(ConduitLang.ITEM_EFFECTIVE_RATE_TOOLTIP, "Effective Rate: %s Items/sec");

        add(ConduitLang.TRANSPARENT_FACADE_TOOLTIP, "Transparent: Hides conduits when painted with a translucent block");
        add(ConduitLang.BLAST_RESIST_FACADE_TOOLTIP, "Hardened: Resists breaking and explosions");

        add(ConduitLang.CONDUIT_PROBE_MODE_TOOLTIP, "Mode %s");
        add(ConduitLang.CONDUIT_PROBE_STATE_PROBE, "Contains copied conduit data:");
        add(ConduitLang.CONDUIT_PROBE_STATE_COPY_PASTE, "Probe");
        add(ConduitLang.CONDUIT_PROBE_CONTAINS_COPIED, "Copy/Paste");
        add(ConduitLang.CONDUIT_PROBE_MESSAGE_SWITCHED_MODE, "Switched conduit probe mode to %s");
        add(ConduitLang.CONDUIT_PROBE_MESSAGE_COPIED, "Copied data: %s");
        add(ConduitLang.CONDUIT_PROBE_MESSAGE_PASTED, "Pasted data: %s");
    }

    private void addContainerTitles() {
        // TODO: we should use container.<X> keys.
    }

    private void addEntities() {
        add(EIOEntities.PAINTED_SAND.get(), "Falling Painted Sand");
    }

    private void addToolsLang() {
        add(ToolsLang.COORDINATE_SELECTOR_NO_PAPER, "No paper in inventory");
        add(ToolsLang.COORDINATE_SELECTOR_NO_BLOCK, "No block in range");

        add(ToolsLang.GLIDER_DISABLED, "Gliding is disabled: ");
        add(ToolsLang.GLIDER_DISABLED_FALL_FLYING, "Elytra Flight");

        add(ToolsLang.SOUL_VIAL_ERROR_PLAYER, "You cannot put player in a bottle!");
        add(ToolsLang.SOUL_VIAL_ERROR_BOSS, "Nice try. Bosses don't like bottles.");
        add(ToolsLang.SOUL_VIAL_ERROR_BLACKLISTED, "This entity has been blacklisted.");
        add(ToolsLang.SOUL_VIAL_ERROR_FAILED, "This entity cannot be captured.");
        add(ToolsLang.SOUL_VIAL_ERROR_DEAD, "Cannot capture a dead mob!");

        add(ToolsLang.SOUL_VIAL_TOOLTIP_HEALTH, "Health: %s/%s");
    }

    private void addFiltersLang() {
        add(FiltersLang.CONFIGURED, "Configured");
        add(FiltersLang.FILTER_CONFIG_NOT_ALLOWED_COMPONENT_MATCH, "This filter uses component matching which is no longer available to this item. Clear this filter using the crafting grid to remove this warning.");

        add(FiltersLang.FILTER_ALLOW_LIST, "Allow List");
        add(FiltersLang.FILTER_DENY_LIST, "Deny List");
        add(FiltersLang.FILTER_MATCH_COMPONENTS, "Match Components");
        add(FiltersLang.FILTER_IGNORE_COMPONENTS, "Ignore Components");
        add(FiltersLang.DAMAGE_FILTER_MODE, "Damage Filter");

        add(FiltersLang.GUI_FILTER, "Filter");
        add(FiltersLang.GUI_CONFIRM, "Confirm");
    }

    private void addJeiLang() {
        add(JEILang.FIRE_CRAFTING_TITLE, "Fire Crafting");
        add(JEILang.FIRE_CRAFTING_VALID_BLOCKS, "Valid Blocks:");
        add(JEILang.FIRE_CRAFTING_VALID_DIMENSIONS, "Valid Dimensions:");
        add(JEILang.FIRE_CRAFTING_CHANCE, "%s%% Chance");
        add(JEILang.FIRE_CRAFTING_DROPS, "Drops %s");

        add(JEILang.ALLOY_SMELTING_TITLE, "Alloy Smelting");
        add(JEILang.ENCHANTER_TITLE, "Enchanting");
        add(JEILang.SAG_MILL_TITLE, "SAG Milling");
        add(JEILang.SLICING_TITLE, "Slicing");
        add(JEILang.SOUL_BINDING_TITLE, "Soul Binding");
        add(JEILang.TANK_TITLE, "Fluid Tank");
        add(JEILang.SOUL_ENGINE_TITLE, "Soul Engine");
        add(JEILang.VAT_TITLE, "VAT Fermentation");
        add(JEILang.WEATHER_CHANGE_TITLE, "Weather Obelisk");
    }

    private void addGlassLang() {
        add(GlassLang.EMITS_LIGHT, "Emits Light");
        add(GlassLang.BLOCKS_LIGHT, "Blocks Light");

        for (var lighting : GlassLighting.values()) {
            String lightingName = lighting != GlassLighting.NONE ? lighting.englishName() + " " : "";
            String lightingKeyName = lighting != GlassLighting.NONE ? "_" + lighting.shortName() : "";

            add(Util.makeDescriptionId("block", EnderIO.id("clear_glass" + lightingKeyName)), lightingName + "Clear Glass");
            add(Util.makeDescriptionId("block", EnderIO.id("fused_quartz" + lightingKeyName)), lightingName + "Fused Quartz");

            for (var color : DyeColor.values()) {
                String colorName = createEnglishPrefix(color);

                add(Util.makeDescriptionId("block", EnderIO.id("clear_glass" + lightingKeyName + "_" + color.getName().toLowerCase(Locale.ROOT))),
                    colorName + lightingName + "Clear Glass");
                add(Util.makeDescriptionId("block", EnderIO.id("fused_quartz" + lightingKeyName + "_" + color.getName().toLowerCase(Locale.ROOT))),
                    colorName + lightingName + "Fused Quartz");
            }
        }
    }

    private static String createEnglishPrefix(DyeColor color) {
        StringBuilder builder = new StringBuilder();
        boolean nextUpper = true;
        for (char c : color.getName().replace("_", " ").toCharArray()) {
            if (nextUpper) {
                builder.append(Character.toUpperCase(c));
                nextUpper = false;
                continue;
            }
            if (c == ' ') {
                nextUpper = true;
            }
            builder.append(c);
        }
        builder.append(" ");
        return builder.toString();
    }

    private void addMachineLang() {
        add(MachinesLang.TOOLTIP_PROGRESS, "Progress %s%%");

        add(MachinesLang.STATUS_ACTIVE, "The machine is active");
        add(MachinesLang.STATUS_IDLE, "The machine is idle");
        add(MachinesLang.STATUS_NO_CAPACITOR, "Install a capacitor to be able to use this machine");
        add(MachinesLang.STATUS_NO_ENERGY, "There is not enough power to use the machine");
        add(MachinesLang.STATUS_ENERGY_FULL, "The energy storage is full");
        add(MachinesLang.STATUS_DRAIN_NO_SOURCE, "The Drain needs a source block under it to work");
        add(MachinesLang.STATUS_EMPTY_TANK, "The tank is empty");
        add(MachinesLang.STATUS_FULL_TANK, "The tank is full");
        add(MachinesLang.STATUS_BLOCKED_REDSTONE, "The machine is blocked by redstone");
        add(MachinesLang.STATUS_OUTPUT_FULL, "There is not enough room for the output");
        add(MachinesLang.STATUS_INPUT_EMPTY, "There is no item in the input");

        add(MachinesLang.GUI_NO_FLUID, "No Fluid");

        add(MachinesLang.GENERATING, "Generating %s\u00B5I/t");
        add(MachinesLang.FUEL_EFFICIENCY, "Efficiency %s%%");

        add(MachinesLang.RANGE, "Range");
        add(MachinesLang.MAX_RANGE, "Maximum Range");
        add(MachinesLang.SHOW_RANGE, "Show Range");
        add(MachinesLang.HIDE_RANGE, "Hide Range");

        add(MachinesLang.NOCAP_TITLE, "Capacitor Missing");
        add(MachinesLang.NOCAP_DESC, "Insert any capacitor so \n this machine can work!");

        add(MachinesLang.ALLOY_SMELTER_MODE, "Smelting Mode");
        add(MachinesLang.POWERED_SPAWNER_MODE, "Spawner Mode");

        add(MachinesLang.SAG_MILL_GRINDING_BALL_TITLE, "SAG Mill Grinding Ball");
        add(MachinesLang.SAG_MILL_GRINDING_BALL_REMAINING, "Remaining: %s%%");
        add(MachinesLang.SAG_MILL_CHANCE, "Chance: %s%%");
        add(MachinesLang.SAG_MILL_CHANCE_GRINDING_BALL, "Chance: %s%% (modified by grinding ball)");

        add(MachinesLang.OBELISK_UPKEEP, "Upkeep %s\u00B5I/t");
        add(MachinesLang.OBELISK_NO_SOUL_FILTER, "No Soul Filter Installed");

        add(MachinesLang.XP_RETRIEVE_1, "Retrieve 1 level of XP");
        add(MachinesLang.XP_RETRIEVE_10, "Retrieve 10 levels of XP");
        add(MachinesLang.XP_RETRIEVE_ALL, "Retrieve all levels of XP");
        add(MachinesLang.XP_STORE_1, "Store 1 level of XP");
        add(MachinesLang.XP_STORE_10, "Store 10 levels of XP");
        add(MachinesLang.XP_STORE_ALL, "Store all levels of XP");

        add(MachinesLang.VAT_TRANSFER_TANK, "Transfer tank contents");
        add(MachinesLang.VAT_DUMP_TANK, "Void tank contents");

        add(MachinesLang.POWERED_SPAWNER_STATUS_OVERCROWDED_MOBS, "Too many mobs");
        add(MachinesLang.POWERED_SPAWNER_STATUS_OVERCROWDED_SPAWNERS, "Too many spawners");
        add(MachinesLang.POWERED_SPAWNER_STATUS_OTHER_MOD, "Blocked by another mod");
        add(MachinesLang.POWERED_SPAWNER_STATUS_DISABLED, "Disabled by config");
        add(MachinesLang.POWERED_SPAWNER_STATUS_UNKNOWN_MOB, "Unknown mob");

        add(MachinesLang.PHOTOVOLTAIC_CELL, "Solar Power!");
        add(MachinesLang.PHOTOVOLTAIC_CELL_ADVANCED, "Produces Power during daylight hours");
        add(MachinesLang.PHOTOVOLTAIC_CELL_ADVANCED2, "Must have a clear line of sight to the sky");
        add(MachinesLang.PHOTOVOLTAIC_CELL_ADVANCED3, "Max Output: ");

        add(MachinesLang.FARMING_STATION_EXPERIMENT, "Ender IO: Farming Station");
        add(MachinesLang.ENDERFACE_EXPERIMENT, "Ender IO: The Ender IO");
        add(MachinesLang.NIARD_EXPERIMENT, "Ender IO: Niard");
    }

    private void addItems() {
        // Alloys
        add(EIOItems.COPPER_ALLOY_INGOT.get(), "Copper Alloy Ingot");
        add(EIOItems.ENERGETIC_ALLOY_INGOT.get(), "Energetic Alloy Ingot");
        add(EIOItems.VIBRANT_ALLOY_INGOT.get(), "Vibrant Alloy Ingot");
        add(EIOItems.REDSTONE_ALLOY_INGOT.get(), "Redstone Alloy Ingot");
        add(EIOItems.CONDUCTIVE_ALLOY_INGOT.get(), "Conductive Alloy Ingot");
        add(EIOItems.PULSATING_ALLOY_INGOT.get(), "Pulsating Alloy Ingot");
        add(EIOItems.DARK_STEEL_INGOT.get(), "Dark Steel Ingot");
        add(EIOItems.SOULARIUM_INGOT.get(), "Soularium Ingot");
        add(EIOItems.END_STEEL_INGOT.get(), "End Steel Ingot");

        add(EIOItems.COPPER_ALLOY_NUGGET.get(), "Copper Alloy Nugget");
        add(EIOItems.ENERGETIC_ALLOY_NUGGET.get(), "Energetic Alloy Nugget");
        add(EIOItems.VIBRANT_ALLOY_NUGGET.get(), "Vibrant Alloy Nugget");
        add(EIOItems.REDSTONE_ALLOY_NUGGET.get(), "Redstone Alloy Nugget");
        add(EIOItems.CONDUCTIVE_ALLOY_NUGGET.get(), "Conductive Alloy Nugget");
        add(EIOItems.PULSATING_ALLOY_NUGGET.get(), "Pulsating Alloy Nugget");
        add(EIOItems.DARK_STEEL_NUGGET.get(), "Dark Steel Nugget");
        add(EIOItems.SOULARIUM_NUGGET.get(), "Soularium Nugget");
        add(EIOItems.END_STEEL_NUGGET.get(), "End Steel Nugget");

        // Grinding balls
        add(EIOItems.SOULARIUM_BALL.get(), "Soularium Grinding Ball");
        add(EIOItems.CONDUCTIVE_ALLOY_BALL.get(), "Conductive Alloy Grinding Ball");
        add(EIOItems.PULSATING_ALLOY_BALL.get(), "Pulsating Alloy Grinding Ball");
        add(EIOItems.REDSTONE_ALLOY_BALL.get(), "Redstone Alloy Grinding Ball");
        add(EIOItems.ENERGETIC_ALLOY_BALL.get(), "Energetic Alloy Grinding Ball");
        add(EIOItems.VIBRANT_ALLOY_BALL.get(), "Vibrant Alloy Grinding Ball");
        add(EIOItems.COPPER_ALLOY_BALL.get(), "Copper Alloy Grinding Ball");
        add(EIOItems.DARK_STEEL_BALL.get(), "Dark Steel Grinding Ball");
        add(EIOItems.END_STEEL_BALL.get(), "End Steel Grinding Ball");

        // Crafting Components
        add(EIOItems.SILICON.get(), "Silicon");
        add(EIOItems.GRAINS_OF_INFINITY.get(), "Grains of Infinity");
        add(EIOItems.INFINITY_ROD.get(), "Infinity Rod");
        add(EIOItems.CONDUIT_BINDER_COMPOSITE.get(), "Conduit Binder Composite");
        add(EIOItems.CONDUIT_BINDER.get(), "Conduit Binder");

        // TODO: Does Bimetal make sense anymore?
        add(EIOItems.GEAR_IRON.get(), "Infinity Bimetal Gear");
        add(EIOItems.GEAR_ENERGIZED.get(), "Energized Bimetal Gear");
        add(EIOItems.GEAR_VIBRANT.get(), "Vibrant Bimetal Gear");
        add(EIOItems.GEAR_DARK_STEEL.get(), "Dark Bimetal Gear");

        add(EIOItems.ZOMBIE_ELECTRODE.get(), "Zombie Electrode");
        add(EIOItems.Z_LOGIC_CONTROLLER.get(), "Z-Logic Controller");
        add(EIOItems.FRANK_N_ZOMBIE.get(), "Frank'N'Zombie");
        add(EIOItems.ENDER_RESONATOR.get(), "Ender Resonator");
        add(EIOItems.SENTIENT_ENDER.get(), "Sentient Ender");
        add(EIOItems.SKELETAL_CONTRACTOR.get(), "Skeletal Contractor");
        add(EIOItems.GUARDIAN_DIODE.get(), "Guardian Diode");
        add(EIOItems.SUSPICIOUS_SEED.get(), "Suspicious Seed");

        // Capacitors
        add(EIOItems.BASIC_CAPACITOR.get(), "Basic Capacitor");
        add(EIOItems.DOUBLE_LAYER_CAPACITOR.get(), "Double-Layer Capacitor");
        add(EIOItems.OCTADIC_CAPACITOR.get(), "Octadic Capacitor");
        add(EIOItems.LOOT_CAPACITOR.get(), "Loot Capacitor");

        // Crystals
        add(EIOItems.PULSATING_CRYSTAL.get(), "Pulsating Crystal");
        add(EIOItems.VIBRANT_CRYSTAL.get(), "Vibrant Crystal");
        add(EIOItems.ENDER_CRYSTAL.get(), "Ender Crystal");
        add(EIOItems.ENTICING_CRYSTAL.get(), "Enticing Crystal");
        add(EIOItems.WEATHER_CRYSTAL.get(), "Weather Crystal");
        add(EIOItems.PRESCIENT_CRYSTAL.get(), "Prescient Crystal");

        // Powders and Fragments
        add(EIOItems.FLOUR.get(), "Flour");
        add(EIOItems.POWDERED_COAL.get(), "Powdered Coal");
        add(EIOItems.POWDERED_IRON.get(), "Powdered Iron");
        add(EIOItems.POWDERED_GOLD.get(), "Powdered Gold");
        add(EIOItems.POWDERED_COPPER.get(), "Powdered Copper");
        add(EIOItems.POWDERED_TIN.get(), "Powdered Tin");
        add(EIOItems.POWDERED_ENDER_PEARL.get(), "Powdered Ender Pearl");
        add(EIOItems.POWDERED_OBSIDIAN.get(), "Powdered Obsidian");
        add(EIOItems.POWDERED_COBALT.get(), "Powdered Cobalt");
        add(EIOItems.POWDERED_LAPIS_LAZULI.get(), "Powdered Lapis Lazuli");
        add(EIOItems.POWDERED_QUARTZ.get(), "Powdered Quartz");
        add(EIOItems.PRESCIENT_POWDER.get(), "Grains of Prescience");
        add(EIOItems.VIBRANT_POWDER.get(), "Grains of Vibrancy");
        add(EIOItems.PULSATING_POWDER.get(), "Grains of Piezallity");
        add(EIOItems.ENDER_CRYSTAL_POWDER.get(), "Grains of the End");
        add(EIOItems.PHOTOVOLTAIC_COMPOSITE.get(), "Photovoltaic Composite");
        add(EIOItems.SOUL_POWDER.get(), "Soul Powder");
        add(EIOItems.CONFUSION_POWDER.get(), "Confusion Powder");
        add(EIOItems.WITHERING_POWDER.get(), "Withering Powder");

        // Dyes
        add(EIOItems.DYE_GREEN.get(), "Organic Green Dye");
        add(EIOItems.DYE_BROWN.get(), "Organic Brown Dye");
        add(EIOItems.DYE_BLACK.get(), "Organic Black Dye");

        // Misc Materials
        add(EIOItems.PHOTOVOLTAIC_PLATE.get(), "Photovoltaic Plate");
        add(EIOItems.NUTRITIOUS_STICK.get(), "Nutritious Stick");
        add(EIOItems.PLANT_MATTER_GREEN.get(), "Clippings and Trimmings");
        add(EIOItems.PLANT_MATTER_BROWN.get(), "Twigs and Prunings");
        add(EIOItems.GLIDER_WING.get(), "Glider Wing");
        add(EIOItems.ANIMAL_TOKEN.get(), "Animal Token");
        add(EIOItems.MONSTER_TOKEN.get(), "Monster Token");
        add(EIOItems.PLAYER_TOKEN.get(), "Player Token");
        add(EIOItems.CAKE_BASE.get(), "Cake Base");
        add(EIOItems.BLACK_PAPER.get(), "Black Paper");
        add(EIOItems.CLAYED_GLOWSTONE.get(), "Clayed Glowstone");
        add(EIOItems.NETHERCOTTA.get(), "Nethercotta");
        add(EIOItems.BROKEN_SPAWNER.get(), "Broken Spawner");

        // Gliders
        add(EIOItems.GLIDER.get(), "Glider");

        // Fun
        add(EIOItems.ENDERIOS.get(), "\"Enderios\"");

        // Tools
        add(EIOItems.SOUL_VIAL.get(), "Soul Vial");
        add(EIOItems.VOID_VIAL.get(), "Vial of the Void");
        add(EIOItems.YETA_WRENCH.get(), "Yeta Wrench");
        add(EIOItems.COORDINATE_SELECTOR.get(), "Coordinate Selector");
        add(EIOItems.LOCATION_PRINTOUT.get(), "Location Printout");
        add(EIOItems.LEVITATION_STAFF.get(), "Staff of Levity");
        add(EIOItems.TRAVEL_STAFF.get(), "Staff of Travelling");
        add(EIOItems.ELECTROMAGNET.get(), "Electromagnet");
        add(EIOItems.COLD_FIRE_IGNITER.get(), "Cold Fire Igniter");
        add(EIOItems.CONDUIT_PROBE.get(), "Conduit Probe");

        add(EIOItems.DARK_STEEL_SWORD.get(), "The Ender");

        // Filters
        add(EIOItems.BASIC_ITEM_FILTER.get(), "Basic Item Filter");
        add(EIOItems.BIG_ITEM_FILTER.get(), "Big Item Filter");
        add(EIOItems.ADVANCED_ITEM_FILTER.get(), "Advanced Item Filter");
        add(EIOItems.BIG_ADVANCED_ITEM_FILTER.get(), "Big Advanced Item Filter");

        add(EIOItems.BASIC_FLUID_FILTER.get(), "Basic Fluid Filter");

        add(EIOItems.BASIC_SOUL_FILTER.get(), "Basic Soul Filter");

        add(EIOItems.REDSTONE_FILTER_BASE.get(), "Redstone Filter Base");
        add(EIOItems.NOT_FILTER.get(), "Redstone NOT Filter");
        add(EIOItems.OR_FILTER.get(), "Redstone OR Filter");
        add(EIOItems.AND_FILTER.get(), "Redstone AND Filter");
        add(EIOItems.NOR_FILTER.get(), "Redstone NOR Filter");
        add(EIOItems.NAND_FILTER.get(), "Redstone NAND Filter");
        add(EIOItems.XOR_FILTER.get(), "Redstone XOR Filter");
        add(EIOItems.XNOR_FILTER.get(), "Redstone XNOR Filter");
        add(EIOItems.TLATCH_FILTER.get(), "Redstone Toggle Filter");
        add(EIOItems.COUNT_FILTER.get(), "Redstone Count Filter");
        add(EIOItems.SENSOR_FILTER.get(), "Redstone Sensor Filter");
        add(EIOItems.TIMER_FILTER.get(), "Redstone Timer Filter");

        // Conduits
        add(EIOItems.CONDUIT.get(), "<MISSING> Conduit");
        add(EIOItems.CONDUIT_FACADE.get(), "Conduit Facade");
        add(EIOItems.TRANSPARENT_CONDUIT_FACADE.get(), "Transparent Conduit Facade");
        add(EIOItems.HARDENED_CONDUIT_FACADE.get(), "Hardened Conduit Facade");
        add(EIOItems.TRANSPARENT_HARDENED_CONDUIT_FACADE.get(), "Transparent Hardened Conduit Facade");

        // Creative Tab Icon
        add(EIOItems.CREATIVE_ICON.get(), "Internal Item - Unobtainable");
    }

    private void addBlocks() {
        // Alloys
        add(EIOBlocks.COPPER_ALLOY_BLOCK.get(), "Copper Alloy Block");
        add(EIOBlocks.ENERGETIC_ALLOY_BLOCK.get(), "Energetic Alloy Block");
        add(EIOBlocks.VIBRANT_ALLOY_BLOCK.get(), "Vibrant Alloy Block");
        add(EIOBlocks.REDSTONE_ALLOY_BLOCK.get(), "Redstone Alloy Block");
        add(EIOBlocks.CONDUCTIVE_ALLOY_BLOCK.get(), "Conductive Alloy Block");
        add(EIOBlocks.PULSATING_ALLOY_BLOCK.get(), "Pulsating Alloy Block");
        add(EIOBlocks.DARK_STEEL_BLOCK.get(), "Dark Steel Block");
        add(EIOBlocks.SOULARIUM_BLOCK.get(), "Soularium Block");
        add(EIOBlocks.END_STEEL_BLOCK.get(), "End Steel Block");

        // Chassis
        add(EIOBlocks.VOID_CHASSIS.get(), "Void Chassis");
        add(EIOBlocks.ENSOULED_CHASSIS.get(), "Ensouled Chassis");

        // Dark Steel Building Blocks
        add(EIOBlocks.DARK_STEEL_LADDER.get(), "Dark Steel Ladder");
        add(EIOBlocks.DARK_STEEL_BARS.get(), "Dark Steel Bars");
        add(EIOBlocks.DARK_STEEL_DOOR.get(), "Dark Steel Door");
        add(EIOBlocks.DARK_STEEL_TRAPDOOR.get(), "Dark Steel Trapdoor");
        add(EIOBlocks.END_STEEL_BARS.get(), "End Steel Bars");
        add(EIOBlocks.REINFORCED_OBSIDIAN.get(), "Reinforced Obsidian");

        // Painted Blocks
        add(EIOBlocks.PAINTED_FENCE.get(), "Painted Fence");
        add(EIOBlocks.PAINTED_FENCE_GATE.get(), "Painted Fence Gate");
        add(EIOBlocks.PAINTED_SAND.get(), "Painted Sand");
        add(EIOBlocks.PAINTED_STAIRS.get(), "Painted Stairs");
        add(EIOBlocks.PAINTED_CRAFTING_TABLE.get(), "Painted Crafting Table");
        add(EIOBlocks.PAINTED_REDSTONE_BLOCK.get(), "Painted Redstone Block");
        add(EIOBlocks.PAINTED_TRAPDOOR.get(), "Painted Trapdoor");
        add(EIOBlocks.PAINTED_WOODEN_PRESSURE_PLATE.get(), "Painted Wooden Pressure Plate");
        add(EIOBlocks.PAINTED_SLAB.get(), "Painted Slab");
        add(EIOBlocks.PAINTED_GLOWSTONE.get(), "Painted Glowstone");
        add(EIOBlocks.PAINTED_WALL.get(), "Painted Wall");

        // Resetting Levers
        for (var lever : EIOBlocks.RESETTING_LEVERS) {
            int duration = lever.get().delay() / 20;
            String durationLabel = "(" + (duration >= 60 ? duration / 60 : duration) + " " + (duration == 60 ? "minute" : duration > 60 ? "minutes" : "seconds") + ")";
            add(lever.get(), "Resetting Lever " + (lever.get().inverted() ? "Inverted " : "") + durationLabel);
        }

        // Pressure plates
        add(EIOBlocks.DARK_STEEL_PRESSURE_PLATE.get(), "Dark Steel Pressure Plate");
        add(EIOBlocks.SILENT_DARK_STEEL_PRESSURE_PLATE.get(), "Silent Dark Steel Pressure Plate");
        add(EIOBlocks.SOULARIUM_PRESSURE_PLATE.get(), "Soularium Pressure Plate");
        add(EIOBlocks.SILENT_SOULARIUM_PRESSURE_PLATE.get(), "Silent Soularium Pressure Plate");
        add(EIOBlocks.SILENT_OAK_PRESSURE_PLATE.get(), "Silent Oak Pressure Plate");
        add(EIOBlocks.SILENT_ACACIA_PRESSURE_PLATE.get(), "Silent Acacia Pressure Plate");
        add(EIOBlocks.SILENT_DARK_OAK_PRESSURE_PLATE.get(), "Silent Dark Oak Pressure Plate");
        add(EIOBlocks.SILENT_SPRUCE_PRESSURE_PLATE.get(), "Silent Spruce Pressure Plate");
        add(EIOBlocks.SILENT_BIRCH_PRESSURE_PLATE.get(), "Silent Birch Pressure Plate");
        add(EIOBlocks.SILENT_JUNGLE_PRESSURE_PLATE.get(), "Silent Jungle Pressure Plate");
        add(EIOBlocks.SILENT_CRIMSON_PRESSURE_PLATE.get(), "Silent Crimson Pressure Plate");
        add(EIOBlocks.SILENT_WARPED_PRESSURE_PLATE.get(), "Silent Warped Pressure Plate");
        add(EIOBlocks.SILENT_STONE_PRESSURE_PLATE.get(), "Silent Stone Pressure Plate");
        add(EIOBlocks.SILENT_POLISHED_BLACKSTONE_PRESSURE_PLATE.get(), "Silent Polished Blackstone Pressure Plate");
        add(EIOBlocks.SILENT_HEAVY_WEIGHTED_PRESSURE_PLATE.get(), "Silent Heavy Weighted Pressure Plate");
        add(EIOBlocks.SILENT_LIGHT_WEIGHTED_PRESSURE_PLATE.get(), "Silent Light Weighted Pressure Plate");

        // Miscellaneous
        add(EIOBlocks.CONDUIT_BUNDLE.get(), "Conduit Bundle");
        add(EIOBlocks.SOUL_CHAIN.get(), "Soul Chain");
        add(EIOBlocks.COLD_FIRE.get(), "Cold Fire");
        add(EIOBlocks.ENDERMAN_HEAD.get(), "Enderman Head");
        add(EIOBlocks.INDUSTRIAL_INSULATION.get(), "Industrial Insulation");

        // Machine Blocks
        add(EIOBlocks.FLUID_TANK.get(), "Fluid Tank");
        add(EIOBlocks.PRESSURIZED_FLUID_TANK.get(), "Pressurized Fluid Tank");
        add(EIOBlocks.ENCHANTER.get(), "Enchanter");
        add(EIOBlocks.ENDERFACE.get(), "Ender IO");
        add(EIOBlocks.ALLOY_SMELTER.get(), "Alloy Smelter");
        add(EIOBlocks.PAINTING_MACHINE.get(), "Painting Machine");
        add(EIOBlocks.WIRELESS_CHARGER.get(), "Wireless Charger");
        add(EIOBlocks.STIRLING_GENERATOR.get(), "Stirling Generator");
        add(EIOBlocks.SAG_MILL.get(), "SAG Mill");
        add(EIOBlocks.SLICE_AND_SPLICE.get(), "Slice'N'Splice");
        add(EIOBlocks.IMPULSE_HOPPER.get(), "Impulse Hopper");
        add(EIOBlocks.SOUL_BINDER.get(), "Soul Binder");
        add(EIOBlocks.CRAFTER.get(), "Crafter");
        add(EIOBlocks.DRAIN.get(), "Drain");
        add(EIOBlocks.WIRED_CHARGER.get(), "Wired Charger");
        add(EIOBlocks.WIRELESS_CHARGER_ANTENNA.get(), "Pulsating Wireless Antenna");
        add(EIOBlocks.WIRELESS_CHARGER_ANTENNA_ADVANCED.get(), "Vibrant Wireless Antenna");
        add(EIOBlocks.POWERED_SPAWNER.get(), "Powered Spawner");
        add(EIOBlocks.MIND_KILLER.get(), "Mind Killer");
        add(EIOBlocks.VACUUM_CHEST.get(), "Vacuum Chest");
        add(EIOBlocks.XP_VACUUM.get(), "XP Vacuum");
        add(EIOBlocks.TRAVEL_ANCHOR.get(), "Travel Anchor");
        add(EIOBlocks.PAINTED_TRAVEL_ANCHOR.get(), "Painted Travel Anchor");
        add(EIOBlocks.SOUL_ENGINE.get(), "Soul Engine");
        add(EIOBlocks.NIARD.get(), "Niard");
        add(EIOBlocks.VAT.get(), "VAT");
        add(EIOBlocks.XP_OBELISK.get(), "XP Obelisk");
        add(EIOBlocks.FARMING_STATION.get(), "Farming Station");
        add(EIOBlocks.INHIBITOR_OBELISK.get(), "Inhibitor Obelisk");
        add(EIOBlocks.AVERSION_OBELISK.get(), "Aversion Obelisk");
        add(EIOBlocks.RELOCATOR_OBELISK.get(), "Relocator Obelisk");
        add(EIOBlocks.ATTRACTOR_OBELISK.get(), "Attractor Obelisk");
        add(EIOBlocks.WEATHER_OBELISK.get(), "Weather Obelisk");
        add(EIOBlocks.BLOCK_DETECTOR.get(), "Block Detector");
//        add(EIOBlocks.CREATIVE_POWER.get(), "Creative Power");

        // Solar Panels
//        for (var entry : EIOBlocks.SOLAR_PANELS.entrySet()) {
//            String displayName = switch (entry.getKey()) {
//                case ENERGETIC -> "Energetic Photovoltaic Module";
//                case PULSATING -> "Pulsating Photovoltaic Module";
//                case VIBRANT -> "Vibrant Photovoltaic Module";
//            };
//            add(entry.getValue().get(), displayName);
//        }

        // Capacitor Banks
//        for (var entry : EIOBlocks.CAPACITOR_BANKS.entrySet()) {
//            String displayName = switch (entry.getKey()) {
//                case BASIC -> "Basic Capacitor Bank";
//                case ADVANCED -> "Advanced Capacitor Bank";
//                case VIBRANT -> "Vibrant Capacitor Bank";
//            };
//            add(entry.getValue().get(), displayName);
//        }
    }

    private void addFluids() {
        add(EIOFluids.NUTRIENT_DISTILLATION, "Nutrient Distillation");
        add(EIOFluids.DEW_OF_THE_VOID, "Dew of the Void");
        add(EIOFluids.VAPOR_OF_LEVITY, "Vapor of Levity");
        add(EIOFluids.HOOTCH, "Hootch");
        add(EIOFluids.ROCKET_FUEL, "Rocket Fuel");
        add(EIOFluids.FIRE_WATER, "Fire Water");
        add(EIOFluids.XP_JUICE, "XP Juice");
        add(EIOFluids.LIQUID_SUNSHINE, "Liquid Sunshine");
        add(EIOFluids.LIQUID_DARKNESS, "Liquid Darkness");
        add(EIOFluids.CLOUD_SEED, "Cloud Seed");
        add(EIOFluids.CLOUD_SEED_CONCENTRATED, "Cloud Seed Concentrated");
    }

    private void addCommonLang() {
        add(EIOCommonLang.CREATIVE_TAB_TITLE, "Ender IO");
        add(EIOCommonLang.TOOLTIP_ENERGY_EQUIVALENCE, "A unit of energy, equivalent to FE.");
        add(EIOCommonLang.BLOCK_BLAST_RESISTANT, "Blast resistant");

        add(EIOCommonLang.REDSTONE_MODE, "Redstone Mode");

        add(EIOCommonLang.TANK_EMPTY_STRING, "Empty tank");
        add(EIOCommonLang.FLUID_TANK_TOOLTIP, "%d/%d mb of %s"); // [amount]/[capacity] mb of [FluidName]

        add(EIOCommonLang.ENERGY_AMOUNT, "%s \u00B5I");

        add(EIOCommonLang.VISIBLE, "Visible");
        add(EIOCommonLang.NOT_VISIBLE, "Hidden");

        add(EIOCommonLang.TOOLTIP_NO_SOULBOUND, "This item can have a soul bound to it.");

        add(EIOCommonLang.SUSPICIOUS_SEED_LORE, "The seed appears to interact with nearby experience orbs...");

        add(EIOCommonLang.SHOW_DETAIL_TOOLTIP, "<Hold Shift>");

        add(EIOCommonLang.IOCONFIG, "IO Configuration");
        add(EIOCommonLang.TOGGLE_NEIGHBOUR, "Show/Hide Neighbours");

        add(EIOCommonLang.PUSH, "Push");
        add(EIOCommonLang.PULL, "Pull");
        add(EIOCommonLang.BOTH, "Push / Pull");
        add(EIOCommonLang.DISABLED, "Disabled");
        add(EIOCommonLang.NONE, "None");

        add(EIOCommonLang.GRINDINGBALL_MAIN_OUTPUT, "Main Output %s%%");
        add(EIOCommonLang.GRINDINGBALL_BONUS_OUTPUT, "Bonus Output %s%%");
        add(EIOCommonLang.GRINDINGBALL_POWER_USE, "Power Use %s%%");

        add(EIOCommonLang.DARK_STEEL_LADDER_FASTER, "Faster than regular ladders");
        add(EIOCommonLang.TOO_MANY_LEVELS, "You have more than 21862 levels, that's too much XP.");
    }

    private void addAdvancementsLang() {
        add(AdvancementsLang.PLACE_CAPACITOR_BANK_ADVANCEMENT_TITLE, "Modular Power Storage");
        add(AdvancementsLang.PLACE_CAPACITOR_BANK_ADVANCEMENT_DESCRIPTION, "Build a Capacitor Bank");
        add(AdvancementsLang.MULTIBLOCK_CONNECTED_TEXTURES, "If you are looking for connected textures on the capacitor bank, you might want to install Athena on your client.");

        add(AdvancementsLang.USE_GLIDER_ADVANCEMENT_TITLE, "Majestic");
        add(AdvancementsLang.USE_GLIDER_ADVANCEMENT_DESCRIPTION, "Do you really trust some leather?");

        add(AdvancementsLang.RICH_ADVANCEMENT_TITLE, "Don't tell the others");
        add(AdvancementsLang.RICH_ADVANCEMENT_DESCRIPTION, "Make others think you are rich");

        add(AdvancementsLang.RICHER_ADVANCEMENT_TITLE, "Is this real?");
        add(AdvancementsLang.RICHER_ADVANCEMENT_DESCRIPTION, "Make others think you are richer");
    }

    private void add(ResourceKey<Conduit<?, ?>> key, String translation) {
        add(Component.translatable(Util.makeDescriptionId(EnderIORegistries.Keys.CONDUIT.identifier().getPath(), key.identifier())), translation);
    }

    private void add(Component component, String translation) {
        if (component.getContents() instanceof TranslatableContents translatableContents) {
            add(translatableContents.getKey(), translation);
        } else {
            throw new IllegalArgumentException("Component " + component + " is not translatable");
        }
    }

    public void add(FluidDeferredHolders key, String name) {
        if (key.bucket() != null) {
            add(key.bucket().get(), name + " Bucket");
        }

        add(key.block().get(), name);
        add(key.type().get(), name);
    }

    public void add(FluidType key, String name) {
        this.add(key.getDescriptionId(), name);
    }
}
