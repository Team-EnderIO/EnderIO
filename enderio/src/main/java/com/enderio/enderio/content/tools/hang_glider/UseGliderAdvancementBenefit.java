package com.enderio.enderio.content.tools.hang_glider;

import com.enderio.enderio.EnderIO;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.entity.player.AdvancementEvent;

import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber
public class UseGliderAdvancementBenefit {

    public static final ResourceLocation USE_GLIDER_ADVANCEMENT = EnderIO.rl("adventure/use_glider");

    public static final Map<Integer, Item> PLAYER_BOUND_GLIDERS = new HashMap<>();

    @SubscribeEvent
    public static void onEarnAdvancement(AdvancementEvent.AdvancementEarnEvent earnAdvancement) {
        if (earnAdvancement.getAdvancement().id().equals(USE_GLIDER_ADVANCEMENT)) {
            Item item = PLAYER_BOUND_GLIDERS.get(earnAdvancement.getEntity().getUUID().hashCode());
            if (item != null && !earnAdvancement.getEntity().addItem(item.getDefaultInstance())) {
                earnAdvancement.getEntity().drop(item.getDefaultInstance(), false);
            }
        }
    }
}
