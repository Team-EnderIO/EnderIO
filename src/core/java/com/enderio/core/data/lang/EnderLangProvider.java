package com.enderio.core.data.lang;

import com.enderio.core.common.registry.EnderBlockRegistry;
import com.enderio.core.common.registry.EnderDeferredBlock;
import com.enderio.core.data.loot.EnderBlockLootProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.function.BiConsumer;

public class EnderLangProvider extends LanguageProvider {
    private final EnderBlockRegistry registry;

    public EnderLangProvider(PackOutput output, String modid, String locale, EnderBlockRegistry registry) {
        super(output, modid, locale);
        this.registry = registry;
    }

    @Override
    protected void addTranslations() {
        for (DeferredHolder<Block, ? extends Block> block : registry.getEntries()) {
            this.add(block.get(), ((EnderDeferredBlock<Block>) block).getTranslation());
        }
    }
}
