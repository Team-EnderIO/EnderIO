package com.enderio.enderio.datagen.client;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.api.io.RedstoneControl;
import com.enderio.enderio.content.capacitors.CapacitorLang;
import com.enderio.enderio.content.filters.item.general.DamageFilterMode;
import com.enderio.enderio.content.glass.GlassCollisionPredicate;
import com.enderio.enderio.foundation.tag.EIOTags;
import com.enderio.regilite.Regilite;
import com.enderio.regilite.data.RegiliteDataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
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
        addTagTranslations();
        addCapacitorTooltipTranslations();
        addEnumLang();

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

    private void addTagTranslations() {
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

    private void addCapacitorTooltipTranslations() {
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
    private void addEnumLang() {
        add(RedstoneControl.ALWAYS_ACTIVE.getComponent(), "Always Active");
        add(RedstoneControl.ACTIVE_WITH_SIGNAL.getComponent(), "Active With Signal");
        add(RedstoneControl.ACTIVE_WITHOUT_SIGNAL.getComponent(), "Active without Signal");
        add(RedstoneControl.NEVER_ACTIVE.getComponent(), "Never Active");

        add(GlassCollisionPredicate.PLAYERS_PASS.getComponent(), "Not solid to players");
        add(GlassCollisionPredicate.PLAYERS_BLOCK.getComponent(), "Only solid to players");
        add(GlassCollisionPredicate.MOBS_PASS.getComponent(), "Not solid to monsters");
        add(GlassCollisionPredicate.MOBS_BLOCK.getComponent(), "Only solid to monsters");
        add(GlassCollisionPredicate.ANIMALS_PASS.getComponent(), "Not solid to animals");
        add(GlassCollisionPredicate.ANIMALS_BLOCK.getComponent(), "Only solid to animals");

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
    }

    private void add(Component component, String translation) {
        if (component.getContents() instanceof TranslatableContents translatableContents) {
            add(translatableContents.getKey(), translation);
        } else {
            throw new IllegalArgumentException("Component " + component + " is not translatable");
        }
    }
}
