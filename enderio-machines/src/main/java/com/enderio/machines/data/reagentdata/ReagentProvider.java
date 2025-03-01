package com.enderio.machines.data.reagentdata;

import com.enderio.machines.common.datamap.VatReagent;
import com.enderio.machines.common.tag.MachineTags;
import com.enderio.machines.data.tag.MachineItemTagsProvider;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.data.DataMapProvider;

public class ReagentProvider extends DataMapProvider {

    private final Map<Item, Map<TagKey<Item>, Double>> dataMap = new HashMap<>();

    public ReagentProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }

    protected void gather() {
        reagent(Items.SUGAR_CANE, MachineTags.ItemTags.CROPS, 0.5D);

        reagent(Items.GLOWSTONE_DUST, MachineTags.ItemTags.NATURAL_LIGHTS, 0.25D);
        reagent(Items.SEA_PICKLE, MachineTags.ItemTags.NATURAL_LIGHTS, 0.25D);
        reagent(Items.GLOW_INK_SAC, MachineTags.ItemTags.NATURAL_LIGHTS, 0.5D);
        reagent(Items.GLOW_LICHEN, MachineTags.ItemTags.NATURAL_LIGHTS, 0.20D);
        reagent(Items.GLOW_BERRIES, MachineTags.ItemTags.NATURAL_LIGHTS, 0.15D);

        reagent(Items.FIRE_CHARGE, MachineTags.ItemTags.EXPLOSIVES, 0.5D);
        reagent(Items.FIREWORK_STAR, MachineTags.ItemTags.EXPLOSIVES, 0.4D);
        reagent(Items.FIREWORK_ROCKET, MachineTags.ItemTags.EXPLOSIVES, 0.4D);
        reagent(Items.GUNPOWDER, MachineTags.ItemTags.EXPLOSIVES, 0.25D);

        reagent(Items.TORCHFLOWER, MachineTags.ItemTags.SUNFLOWER, 1.2D);

        reagent(Items.SNOW, MachineTags.ItemTags.CLOUD_COLD, 0.12D);
        reagent(Items.SNOWBALL, MachineTags.ItemTags.CLOUD_COLD, 0.11D);
        reagent(Items.PACKED_ICE, MachineTags.ItemTags.CLOUD_COLD, 9D);
        reagent(Items.BLUE_ICE, MachineTags.ItemTags.CLOUD_COLD, 81D);

        var builder = builder(VatReagent.DATA_MAP);
        dataMap.forEach((item, map) -> {
            builder.add(item.builtInRegistryHolder(), map, false);
        });
    }

    public void reagent(Item item, TagKey<Item> tag, double value) {
        MachineItemTagsProvider.tag(tag, item);
        reagentValue(tag, item, value);
    }

    public void reagentValue(TagKey<Item> tag, Item item, double value) {
        dataMap.computeIfAbsent(item, it -> new HashMap<>()).put(tag, value);
    }

    @Override
    public String getName() {
        return "Fermenting Reagent Datamaps";
    }

}
