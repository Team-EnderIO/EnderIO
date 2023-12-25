package com.enderio.core.common.registry;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.flag.FeatureFlagRegistry;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.network.IContainerFactory;

import java.util.function.Supplier;

public class EnderMenuRegistry extends EnderRegistry<MenuType<?>>{

    protected EnderMenuRegistry(String namespace) {
        super(BuiltInRegistries.MENU.key(), namespace);
    }

    public static EnderMenuRegistry create(String modid) {
        return new EnderMenuRegistry(modid);
    }

    public <T extends AbstractContainerMenu> EnderDeferredMenu<T> registerMenu(String name, IContainerFactory<T> factory, Supplier<MenuScreens.ScreenConstructor<T, ? extends AbstractContainerScreen<T>>> screenConstructor) {
        EnderDeferredObject<MenuType<?>, MenuType<T>> holder = this.register(name, () -> IMenuTypeExtension.create(factory));
        return EnderDeferredMenu.createMenu(holder).setScreenConstructor(screenConstructor);
    }
}
