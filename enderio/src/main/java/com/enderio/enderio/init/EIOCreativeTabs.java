package com.enderio.enderio.init;

import com.enderio.core.common.item.CreativeTabVariants;
import com.enderio.core.common.item.ICustomCreativeTabEntries;
import com.enderio.enderio.EnderIO;
import com.enderio.enderio.api.EnderIORegistries;
import com.enderio.enderio.api.conduits.Conduit;
import com.enderio.enderio.content.broken_spawner.BrokenSpawnerItem;
import com.enderio.enderio.content.conduits.ConduitBlockItem;
import com.enderio.enderio.content.tools.vials.SoulVialItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Comparator;
import java.util.stream.Stream;

public class EIOCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, EnderIO.MOD_ID);

    public static final ResourceKey<CreativeModeTab> MAIN = ResourceKey.create(Registries.CREATIVE_MODE_TAB, EnderIO.rl("enderio"));

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TAB = CREATIVE_MODE_TABS.register("enderio", () ->
        CreativeModeTab.builder()
            .title(EnderIO.REGILITE.addTranslation("itemGroup", EnderIO.rl("enderio"), "Ender IO"))
            .icon(() -> new ItemStack(EIOItems.CREATIVE_ICON.get()))
            .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
            .withSearchBar()
            .displayItems((CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output) -> {
                // TODO: Perhaps we need an easier way to provide exclusions so we don't need to subclass ICustomCreativeTabEntries all the time.
                addAll(EIOItems.ITEMS, parameters, output);
                // TODO: Not necessarily the final resting place.
                addAllConduits(parameters, output);
                addSoulItems(parameters, output);
            })
            .build());

    public static void register(IEventBus bus) {
        CREATIVE_MODE_TABS.register(bus);
        bus.addListener(EIOCreativeTabs::addToExistingTabs);
    }

    private static void addToExistingTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.COLORED_BLOCKS) {
            for (var glassSet : EIOBlocks.GLASS_BLOCKS.values()) {
                for (var glassBlock : glassSet.getAllBlocks().toList()) {
                    event.accept(glassBlock, CreativeModeTab.TabVisibility.PARENT_TAB_ONLY);
                }
            }
        } else if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            // TODO: Finish this list
            event.acceptAll(Stream
                .of(EIOItems.COPPER_ALLOY_INGOT, EIOItems.ENERGETIC_ALLOY_INGOT, EIOItems.VIBRANT_ALLOY_INGOT, EIOItems.REDSTONE_ALLOY_INGOT,
                    EIOItems.CONDUCTIVE_ALLOY_INGOT, EIOItems.PULSATING_ALLOY_INGOT, EIOItems.DARK_STEEL_INGOT, EIOItems.SOULARIUM_INGOT,
                    EIOItems.END_STEEL_INGOT)
                .map(i -> i.get().getDefaultInstance())
                .toList());
        }
    }

    private static void addAll(DeferredRegister<Item> items, CreativeModeTab.ItemDisplayParameters properties, CreativeModeTab.Output output) {
        // TODO: Could we use an Ender IO-specific event to interject when adding items.
        //       for example in the modded conduits addon, insert chemical filter after EIO's normal filters
        //       or in endergy add ingots after EIO's normal ingots and so on...
        for (var entry : items.getEntries()) {
            var item = entry.get();
            if (item instanceof ICustomCreativeTabEntries customCreativeTabEntries) {
                if (customCreativeTabEntries.shouldAddDefaultItem()) {
                    output.accept(item);
                }

                customCreativeTabEntries.addAdditionalCreativeTabEntries(properties, output);
            } else {
                output.accept(item);

                // TODO: Remove this old interface
                if (item instanceof CreativeTabVariants variants) {
                    variants.addAllVariants(output);
                }
            }
        }
    }

    private static void addAllConduits(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output) {
        var registry = parameters.holders().lookupOrThrow(EnderIORegistries.Keys.CONDUIT);
        var conduitTypes = registry.listElements().toList();

        var conduitClassTypes = conduitTypes.stream()
            .map(e -> e.value().getClass())
            .sorted(Comparator.comparing(Class::getName))
            .distinct()
            .toList();

        for (var conduitClass : conduitClassTypes) {
            var matchingConduitTypes = conduitTypes.stream()
                .filter(e -> e.value().getClass() == conduitClass)
                // GRIM...
                .sorted((o1, o2) -> compareConduitTo(o1.value(), o2.value()))
                .toList();

            for (var conduitType : matchingConduitTypes) {
                output.accept(ConduitBlockItem.getStackFor(conduitType, 1), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            }
        }
    }

    private static <T extends Conduit<T, ?>> int compareConduitTo(Conduit<T, ?> o1, Conduit<?, ?> o2) {
        return o1.compareTo((T) o2);
    }

    // Add soul items at the bottom to avoid pollution
    private static void addSoulItems(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output) {
        output.acceptAll(BrokenSpawnerItem.getPossibleStacks());
        output.acceptAll(SoulVialItem.getAllFilled());
    }
}
