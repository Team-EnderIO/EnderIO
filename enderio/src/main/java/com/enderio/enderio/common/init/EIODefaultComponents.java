package com.enderio.enderio.common.init;

import com.enderio.enderio.api.EnderIODataComponents;
import com.enderio.enderio.api.components.GrindingBallData;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;

@EventBusSubscriber
public class EIODefaultComponents {
    @SubscribeEvent
    public static void modifyDefaultComponents(ModifyDefaultComponentsEvent event) {
        event.modify(Items.FLINT, i -> i.set(EnderIODataComponents.GRINDING_BALL,
            new GrindingBallData(1.2F, 1.25F, 0.85F, 24000)));
    }
}
