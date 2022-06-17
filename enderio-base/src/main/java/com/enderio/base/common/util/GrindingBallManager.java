package com.enderio.base.common.util;

import com.enderio.api.grindingball.IGrindingBallData;
import com.enderio.base.EnderIO;
import com.enderio.base.common.init.EIORecipes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Optional;

@Mod.EventBusSubscriber(modid = EnderIO.MODID)
public class GrindingBallManager {
    private static final HashMap<Item, IGrindingBallData> lookup = new HashMap<>();

    public static boolean isGrindingBall(ItemStack stack) {
        Item item = stack.getItem();
        if (!stack.isEmpty() && lookup.containsKey(item))
            return true;
        return false;
    }

    public static Optional<IGrindingBallData> getData(ItemStack stack) {
        Item item = stack.getItem();
        if (!stack.isEmpty() && lookup.containsKey(item))
            return Optional.of(lookup.get(item));
        return Optional.empty();
    }

    @SubscribeEvent
    public static void onRecipesUpdated(RecipesUpdatedEvent event) {
        // Wipe the lookup table
        lookup.clear();

        // Discover all capacitors again.
        event.getRecipeManager()
            .getAllRecipesFor(EIORecipes.Types.GRINDINGBALL)
            .forEach(capacitorDataRecipe ->
                lookup.put(capacitorDataRecipe.getItem(), capacitorDataRecipe)
            );
    }
}
