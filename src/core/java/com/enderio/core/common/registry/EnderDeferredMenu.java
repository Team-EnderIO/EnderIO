package com.enderio.core.common.registry;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.function.Supplier;

public class EnderDeferredMenu<T extends AbstractContainerMenu> extends EnderDeferredObject<MenuType<? extends AbstractContainerMenu>, MenuType<T>> {

    private Supplier<MenuScreens.ScreenConstructor<T, ? extends AbstractContainerScreen<T>>> screenConstructor;

    protected EnderDeferredMenu(ResourceKey<MenuType<? extends AbstractContainerMenu>> key) {
        super(key);
    }

    public EnderDeferredMenu<T> setScreenConstructor(Supplier<MenuScreens.ScreenConstructor<T, ? extends AbstractContainerScreen<T>>> screenConstructor) {
        this.screenConstructor = screenConstructor;
        return this;
    }

    public Supplier<MenuScreens.ScreenConstructor<T, ? extends AbstractContainerScreen<T>>> getScreenConstructor() {
        return screenConstructor;
    }

    @Override
    public EnderDeferredMenu<T> setTranslation(String translation) {
        this.translation = translation;
        return this;
    }

    public static <T extends AbstractContainerMenu> EnderDeferredMenu<T> createMenu(EnderDeferredObject<MenuType<?>, MenuType<T>> holder) {
        return new EnderDeferredMenu<>(holder.getKey());
    }
}
