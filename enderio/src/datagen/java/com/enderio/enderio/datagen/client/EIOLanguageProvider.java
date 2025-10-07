package com.enderio.enderio.datagen.client;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.api.EnderIORegistries;
import com.enderio.enderio.api.conduits.Conduit;
import com.enderio.enderio.api.io.RedstoneControl;
import com.enderio.enderio.content.capacitors.CapacitorLang;
import com.enderio.enderio.content.conduits.ConduitLang;
import com.enderio.enderio.content.filters.item.general.DamageFilterMode;
import com.enderio.enderio.content.glass.GlassCollisionPredicate;
import com.enderio.enderio.content.machines.alloy.AlloySmelterMode;
import com.enderio.enderio.content.machines.powered_spawner.PoweredSpawnerMode;
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
