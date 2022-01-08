package com.enderio.base.common.util;

import com.enderio.base.EnderIO;
import com.enderio.base.common.init.EIOCapabilities;
import com.enderio.base.common.capability.capacitors.CapacitorSpecializations;
import com.enderio.base.common.capability.capacitors.ICapacitorData;
import com.enderio.base.common.init.EIORecipes;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

/**
 * Helper class for Capacitors
 */
@Mod.EventBusSubscriber(modid = EnderIO.MODID)
public class CapacitorUtil {
    /**
     * Static maps with specializations for the "basic"
     */
    private static ArrayList<String> types = new ArrayList<>(); // TODO: make it so mods can add to this, IMC maybe?

    static {
        types.add(CapacitorSpecializations.ALL_ENERGY_CONSUMPTION);
        types.add(CapacitorSpecializations.ALL_PRODUCTION_SPEED);
        types.add(CapacitorSpecializations.ALLOY_ENERGY_CONSUMPTION);
        types.add(CapacitorSpecializations.ALLOY_ENERGY_CONSUMPTION);
    }

    public static void addType(String type) {
        types.add(type);
    }

    /**
     * Returns a random type from the list for loot capacitors.
     *
     * @return
     */
    public static String getRandomType() {
        return types.get(new Random().nextInt(types.size()));
    }

    /**
     * Adds a tooltip for loot capacitors based on it's stats.
     *
     * @param stack
     * @param tooltipComponents
     */
    public static void getTooltip(ItemStack stack, List<Component> tooltipComponents) {
        // TODO: Crashes client if the item was spawned in as it doesn't have any specializations.
        stack.getCapability(EIOCapabilities.CAPACITOR).ifPresent(cap -> {
            TranslatableComponent t = new TranslatableComponent(getFlavor(cap.getFlavor()),
                getGradeText(cap.getSpecializations().values().stream().findFirst().get()),
                getTypeText(cap.getSpecializations().keySet().stream().findFirst().get()), getBaseText(cap.getBase()));
            tooltipComponents.add(t);
        });
    }

    //TODO depending on direction
    private static String getFlavor(int flavor) {
        return "description.enderio.capacitor.flavor." + flavor;
    }

    //TODO depending on direction
    private static TranslatableComponent getBaseText(float base) {
        TranslatableComponent t = new TranslatableComponent("description.enderio.capacitor.base." + (int) Math.ceil(base));
        t.withStyle(ChatFormatting.ITALIC);
        return t;
    }

    //TODO depending on direction
    private static TranslatableComponent getTypeText(String type) {
        TranslatableComponent t = new TranslatableComponent("description.enderio.capacitor.type." + type);
        t.withStyle(ChatFormatting.ITALIC);
        return t;
    }

    //TODO depending on direction
    private static TranslatableComponent getGradeText(float grade) {
        TranslatableComponent t = new TranslatableComponent("description.enderio.capacitor.grade." + (int) Math.ceil(grade));
        t.withStyle(ChatFormatting.ITALIC);
        return t;
    }

    private static final HashMap<Item, ICapacitorData> lookup = new HashMap<>();

    public static Optional<ICapacitorData> getCapacitorData(ItemStack itemStack) {
        // Search for an ICapacitorData capability
        LazyOptional<ICapacitorData> capacitorDataCap = itemStack.getCapability(EIOCapabilities.CAPACITOR);
        if (capacitorDataCap.isPresent())
            return Optional.of(capacitorDataCap.orElseThrow(NullPointerException::new));

        // Find a cached ICapacitorData value
        if (lookup.containsKey(itemStack.getItem())) {
            return Optional.ofNullable(lookup.get(itemStack.getItem()));
        }

        return Optional.empty();
    }

    public static boolean isCapacitor(ItemStack itemStack) {
        LazyOptional<ICapacitorData> capacitorDataCap = itemStack.getCapability(EIOCapabilities.CAPACITOR);
        if (capacitorDataCap.isPresent())
            return true;
        return lookup.containsKey(itemStack.getItem());
    }

    @SubscribeEvent
    public static void onRecipesUpdated(RecipesUpdatedEvent event) {
        // Wipe the lookup table
        lookup.clear();

        // Discover all capacitors again.
        event.getRecipeManager()
            .getAllRecipesFor(EIORecipes.Types.CAPACITOR_DATA)
            .forEach(capacitorDataRecipe -> {
                lookup.put(capacitorDataRecipe.getCapacitorItem(), capacitorDataRecipe.getCapacitorData());
            });
    }
}
