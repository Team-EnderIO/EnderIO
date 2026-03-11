package com.enderio.enderio.init;

import com.enderio.core.common.item.CreativeTabVariants;
import com.enderio.core.common.item.ICustomCreativeTabEntries;
import com.enderio.enderio.EnderIO;
import com.enderio.enderio.api.EnderIORegistries;
import com.enderio.enderio.api.conduits.Conduit;
import com.enderio.enderio.api.conduits.ConduitApi;
import com.enderio.enderio.content.broken_spawner.BrokenSpawnerItem;
import com.enderio.enderio.content.conduits.ConduitBlockItem;
import com.enderio.enderio.content.paint.block.PaintedBlock;
import com.enderio.enderio.content.tools.vials.SoulVialItem;
import com.enderio.enderio.foundation.lang.EIOCommonLang;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class EIOCreativeTabs {
    private static final List<Predicate<Item>> EXCLUSIONS_PREDICATE = List.of(
        i -> i instanceof BlockItem blockItem && blockItem.getBlock() instanceof PaintedBlock
    );

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, EnderIO.MOD_ID);

    public static final RegistryObject<CreativeModeTab> TAB = CREATIVE_MODE_TABS.register("enderio", () ->
        CreativeModeTab.builder()
            .title(EIOCommonLang.CREATIVE_TAB_TITLE)
            .icon(() -> new ItemStack(EIOItems.CREATIVE_ICON.get()))
            .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
            .withSearchBar()
            .displayItems((CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output) -> {
                addAll(EIOItems.ITEMS, parameters, output);
                addAll(EIOFluids.FLUIDS.itemsRegister(), parameters, output);
                addAll(EIOBlocks.ITEMS, parameters, output);
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
                .of(EIOItems.CONDUCTIVE_ALLOY_INGOT, EIOItems.ENERGETIC_ALLOY_INGOT, EIOItems.VIBRANT_ALLOY_INGOT, EIOItems.REDSTONE_ALLOY_INGOT,
                    EIOItems.PULSATING_ALLOY_INGOT, EIOItems.DARK_STEEL_INGOT, EIOItems.SOULARIUM_INGOT, EIOItems.END_STEEL_INGOT)
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

            if (EXCLUSIONS_PREDICATE.stream().anyMatch(p -> p.test(item))) {
                continue;
            }

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

    // Add soul items at the bottom to avoid pollution
    private static void addSoulItems(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output) {
        output.acceptAll(BrokenSpawnerItem.getPossibleStacks());
        output.acceptAll(SoulVialItem.getAllFilled());
    }
}
