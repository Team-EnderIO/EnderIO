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
import net.neoforged.neoforge.common.data.DataMapProvider;

public class ReagentProvider extends DataMapProvider {

    private final Map<Item, Map<TagKey<Item>, Double>> dataMap = new HashMap<>();

    public ReagentProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }

    protected void gather() {
        reagent(net.minecraft.world.item.Items.SUGAR_CANE, MachineTags.Items.CROPS, 0.5D);

        reagent(net.minecraft.world.item.Items.GLOWSTONE_DUST, MachineTags.Items.NATURAL_LIGHTS, 0.25D);
        reagent(net.minecraft.world.item.Items.SEA_PICKLE, MachineTags.Items.NATURAL_LIGHTS, 0.25D);
        reagent(net.minecraft.world.item.Items.GLOW_INK_SAC, MachineTags.Items.NATURAL_LIGHTS, 0.5D);
        reagent(net.minecraft.world.item.Items.GLOW_LICHEN, MachineTags.Items.NATURAL_LIGHTS, 0.20D);
        reagent(net.minecraft.world.item.Items.GLOW_BERRIES, MachineTags.Items.NATURAL_LIGHTS, 0.15D);

        reagent(net.minecraft.world.item.Items.FIRE_CHARGE, MachineTags.Items.EXPLOSIVES, 0.5D);
        reagent(net.minecraft.world.item.Items.FIREWORK_STAR, MachineTags.Items.EXPLOSIVES, 0.4D);
        reagent(net.minecraft.world.item.Items.FIREWORK_ROCKET, MachineTags.Items.EXPLOSIVES, 0.4D);
        reagent(net.minecraft.world.item.Items.GUNPOWDER, MachineTags.Items.EXPLOSIVES, 0.25D);

        reagent(net.minecraft.world.item.Items.TORCHFLOWER, MachineTags.Items.SUNFLOWER, 1.2D);

        reagent(net.minecraft.world.item.Items.SNOW, MachineTags.Items.CLOUD_COLD, 0.12D);
        reagent(net.minecraft.world.item.Items.SNOWBALL, MachineTags.Items.CLOUD_COLD, 0.11D);
        reagent(net.minecraft.world.item.Items.PACKED_ICE, MachineTags.Items.CLOUD_COLD, 9D);
        reagent(net.minecraft.world.item.Items.BLUE_ICE, MachineTags.Items.CLOUD_COLD, 81D);

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
