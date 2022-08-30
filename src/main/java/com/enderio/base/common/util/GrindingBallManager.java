package com.enderio.base.common.util;

import com.enderio.EnderIO;
import com.enderio.api.grindingball.IGrindingBallData;
import com.enderio.base.common.init.EIORecipes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;

@Mod.EventBusSubscriber(modid = EnderIO.MODID)
public class GrindingBallManager {
    private static final HashMap<Item, IGrindingBallData> itemToData = new HashMap<>();
    private static final HashMap<ResourceLocation, IGrindingBallData> idToData = new HashMap<>();

    public static boolean isGrindingBall(ItemStack stack) {
        return !stack.isEmpty()
            && itemToData.containsKey(stack.getItem());
    }

    public static IGrindingBallData getData(ItemStack stack) {
        Item item = stack.getItem();
        return itemToData.getOrDefault(item, IGrindingBallData.IDENTITY);
    }

    public static IGrindingBallData getData(ResourceLocation dataId) {
        return idToData.getOrDefault(dataId, IGrindingBallData.IDENTITY);
    }

    @SubscribeEvent
    public static void onRecipesUpdated(RecipesUpdatedEvent event) {
        // Wipe the lookup table
        itemToData.clear();
        idToData.clear();

        // Discover all grindingballs again.
        event.getRecipeManager()
            .getAllRecipesFor(EIORecipes.GRINDING_BALL.type().get())
            .forEach(grindingBallRecipe -> {
                    itemToData.put(grindingBallRecipe.getItem(), grindingBallRecipe);
                    idToData.put(grindingBallRecipe.getId(), grindingBallRecipe);
                });
    }
}
