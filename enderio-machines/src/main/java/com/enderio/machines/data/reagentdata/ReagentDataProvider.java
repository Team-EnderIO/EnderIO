package com.enderio.machines.data.reagentdata;

import com.enderio.base.api.EnderIO;
import com.enderio.machines.common.datamap.VatReagent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

public class ReagentDataProvider implements DataProvider {

    private final TagsProvider tagsProvider;
    private final DataProvider dataProvider;

    public ReagentDataProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider,
            ExistingFileHelper existingFileHelper) {
        tagsProvider = new TagsProvider(packOutput, lookupProvider, existingFileHelper);
        dataProvider = new DataProvider(packOutput, lookupProvider);
    }

    protected void gather() {
    }

    @Override
    public CompletableFuture<?> run(CachedOutput pOutput) {
        gather();
        List<CompletableFuture<?>> list = new ArrayList<>();
        list.add(tagsProvider.run(pOutput));
        list.add(dataProvider.run(pOutput));
        return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
    }

    public void addReagent(Item item, TagKey<Item> tag, double value) {
        tagsProvider.addItemTag(tag, item);
        dataProvider.addData(tag, item, value);
    }

    @Override
    public String getName() {
        return "Fermenting Reagent Datamaps";
    }

    private static class TagsProvider extends IntrinsicHolderTagsProvider<Item> {
        private final Map<TagKey<Item>, List<Item>> tagsMap = new HashMap<>();

        protected TagsProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> provider,
                @Nullable ExistingFileHelper existingFileHelper) {
            super(packOutput, Registries.ITEM, provider, item -> item.builtInRegistryHolder().key(), EnderIO.NAMESPACE,
                    existingFileHelper);
        }

        public void addItemTag(TagKey<Item> tag, Item item) {
            tagsMap.computeIfAbsent(tag, it -> new ArrayList<>()).add(item);
        }

        @Override
        protected void addTags(HolderLookup.Provider pProvider) {
            tagsMap.forEach((key, value) -> {
                var tag = this.tag(key);
                value.forEach(tag::add);
            });
        }
    }

    private static class DataProvider extends DataMapProvider {
        private final Map<Item, Map<TagKey<Item>, Double>> data = new HashMap<>();

        protected DataProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
            super(packOutput, lookupProvider);
        }

        public void addData(TagKey<Item> tag, Item item, double value) {
            data.computeIfAbsent(item, it -> new HashMap<>()).put(tag, value);
        }

        @Override
        protected void gather() {
            var builder = builder(VatReagent.DATA_MAP);
            data.forEach((item, map) -> {
                builder.add(item.builtInRegistryHolder(), map, false);
            });
        }
    }
}
