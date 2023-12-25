package com.enderio.base.common.util;

import com.enderio.api.grindingball.IGrindingBallData;
import com.enderio.base.common.init.EIORecipes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.util.thread.EffectiveSide;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.HashMap;
import java.util.List;

@Mod.EventBusSubscriber
public class GrindingBallManager {
    private static final HashMap<Item, IGrindingBallData> ITEM_TO_DATA = new HashMap<>();
    private static final HashMap<ResourceLocation, IGrindingBallData> ID_TO_DATA = new HashMap<>();

    private static boolean clearCache = false;

    public static boolean isGrindingBall(ItemStack stack) {
        checkCacheRebuild();
        return !stack.isEmpty()
            && ITEM_TO_DATA.containsKey(stack.getItem());
    }

    public static IGrindingBallData getData(ItemStack stack) {
        checkCacheRebuild();
        Item item = stack.getItem();
        return ITEM_TO_DATA.getOrDefault(item, IGrindingBallData.IDENTITY);
    }

    public static List<Item> getGrindingBalls() {
        checkCacheRebuild();
        return List.copyOf(ITEM_TO_DATA.keySet());
    }

    public static IGrindingBallData getData(ResourceLocation dataId) {
        checkCacheRebuild();
        return ID_TO_DATA.getOrDefault(dataId, IGrindingBallData.IDENTITY);
    }


    @SubscribeEvent
    public static void registerReloadListener(AddReloadListenerEvent event) {
        //Fired on datapack reload
        clearCache = true;
    }
    @SubscribeEvent
    public static void onRecipesUpdated(RecipesUpdatedEvent event) {
        rebuildCache(event.getRecipeManager());
    }

    private static void checkCacheRebuild() {
        if (clearCache && EffectiveSide.get().isServer()) {
            rebuildCache(ServerLifecycleHooks.getCurrentServer().getRecipeManager());
            clearCache = false;
        }
    }

    private static void rebuildCache(RecipeManager manager) {

        // Wipe the lookup table
        ITEM_TO_DATA.clear();
        ID_TO_DATA.clear();

        // Discover all grindingballs again.
        manager.getAllRecipesFor(EIORecipes.GRINDING_BALL.type().get())
            .forEach(grindingBallRecipe -> {
                ITEM_TO_DATA.put(grindingBallRecipe.getItem(), grindingBallRecipe);
                ID_TO_DATA.put(grindingBallRecipe.getGrindingBallId(), grindingBallRecipe);
            });
    }
}
