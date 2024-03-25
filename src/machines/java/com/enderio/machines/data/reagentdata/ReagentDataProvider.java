package com.enderio.machines.data.reagentdata;

import com.enderio.EnderIO;
import com.enderio.machines.common.datamap.VatReagent;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ReagentDataProvider implements DataProvider {

    private final VatTagsProvider tagsProvider;
    private final VatDatamapProvider datamapProvider;

    public ReagentDataProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        tagsProvider = new VatTagsProvider(packOutput, lookupProvider, existingFileHelper);
        datamapProvider = new VatDatamapProvider(packOutput, lookupProvider);
    }

    @Override
    public CompletableFuture<?> run(CachedOutput pOutput) {
        gather();
        List<CompletableFuture<?>> list = new ArrayList<>();
        list.add(tagsProvider.run(pOutput));
        list.add(datamapProvider.run(pOutput));
        return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
    }

    protected void gather() {
        addReagentData(Items.WHEAT, Tags.Items.CROPS, 3D);
    }

    public void addReagentData(Item item, TagKey<Item> tag, double value) {
        tagsProvider.addItemTag(tag, item);
        datamapProvider.addData(tag, item, value);
    }

    @Override
    public String getName() {
        return "ReagentData";
    }

    private static class VatTagsProvider extends IntrinsicHolderTagsProvider<Item> {
        private final Map<TagKey<Item>, List<Item>> tagsMap = new HashMap<>();

        public VatTagsProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> provider, @Nullable ExistingFileHelper existingFileHelper) {
            super(packOutput, Registries.ITEM, provider, item -> item.builtInRegistryHolder().key(), EnderIO.MODID, existingFileHelper);
        }

        public void addItemTag(TagKey<Item> tag, Item item) {
            var list = tagsMap.computeIfAbsent(tag, it -> new ArrayList<>());
            list.add(item);
        }

        @Override
        protected void addTags(HolderLookup.Provider pProvider) {
            tagsMap.forEach((key, value) -> {
                var tag = this.tag(key);
                value.forEach(tag::add);
            });
        }
    }

    private static class VatDatamapProvider extends DataMapProvider {
        private final Map<TagKey<Item>, Map<Item, Double>> data = new HashMap<>();

        protected VatDatamapProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
            super(packOutput, lookupProvider);
        }

        public void addData(TagKey<Item> tag, Item item, double value) {
            var map = data.computeIfAbsent(tag, it -> new HashMap<>());
            map.put(item, value);
        }

        @Override
        protected void gather() {
            var builder = builder(VatReagent.DATA_MAP);
            data.forEach((tag, map) -> {
                map.forEach(((item, value) -> {
                    builder.add(item.builtInRegistryHolder(), Map.of(tag, value), false);
                }));
            });
        }
    }
}
