package com.enderio.enderio.datagen.client;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.api.EnderIORegistries;
import com.enderio.enderio.api.conduits.Conduit;
import com.enderio.enderio.api.io.RedstoneControl;
import com.enderio.enderio.compat.jei.JEILang;
import com.enderio.enderio.content.capacitors.CapacitorLang;
import com.enderio.enderio.content.conduits.ConduitLang;
import com.enderio.enderio.content.filters.FiltersLang;
import com.enderio.enderio.content.filters.item.general.DamageFilterMode;
import com.enderio.enderio.content.glass.GlassCollisionPredicate;
import com.enderio.enderio.content.glass.GlassLang;
import com.enderio.enderio.content.machines.MachinesLang;
import com.enderio.enderio.content.machines.alloy.AlloySmelterMode;
import com.enderio.enderio.content.machines.powered_spawner.PoweredSpawnerMode;
import com.enderio.enderio.content.tools.ToolsLang;
import com.enderio.enderio.foundation.tag.EIOTags;
import com.enderio.enderio.init.EIOConduits;
import com.enderio.enderio.init.EIOEntities;
import com.enderio.regilite.Regilite;
import com.enderio.regilite.data.RegiliteDataProvider;
import net.minecraft.Util;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.common.data.LanguageProvider;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.function.Supplier;

public class EIOLanguageProvider extends LanguageProvider {
    public EIOLanguageProvider(PackOutput output) {
        super(output, EnderIO.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations() {
        addTags();
        addCapacitorTooltips();
        addEnumNames();
        addConduitDescriptions();
        addConduitLang();
        addEntities();
        addToolsLang();
        addFiltersLang();
        addJeiLang();
        addGlassLang();
        addMachineLang();

        // Gross hack until Regilite is out.
        try {
            Field dataProviderField = Regilite.class.getDeclaredField("dataProvider");
            dataProviderField.setAccessible(true);
            RegiliteDataProvider dataProvider = (RegiliteDataProvider)dataProviderField.get(EnderIO.REGILITE);

            Field langEntriesField = RegiliteDataProvider.class.getDeclaredField("langEntries");
            langEntriesField.setAccessible(true);

            //noinspection unchecked
            Map<Supplier<String>, String> langEntries = (Map<Supplier<String>, String>)langEntriesField.get(dataProvider);

            for (var entry : langEntries.entrySet()) {
                if (entry.getValue().isEmpty()) {
                    continue;
                }

                try {
                    add(entry.getKey().get(), entry.getValue());
                } catch (IllegalStateException ex) {
                    // ignore - just a duplicate key.
                }
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
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
    }

    private void add(ResourceKey<Conduit<?, ?>> key, String translation) {
        add(Component.translatable(Util.makeDescriptionId(EnderIORegistries.Keys.CONDUIT.location().getPath(), key.location())), translation);
    }

    private void add(Component component, String translation) {
        if (component.getContents() instanceof TranslatableContents translatableContents) {
            add(translatableContents.getKey(), translation);
        } else {
            throw new IllegalArgumentException("Component " + component + " is not translatable");
        }
    }
}
