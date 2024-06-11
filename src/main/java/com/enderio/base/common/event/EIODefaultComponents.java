package com.enderio.base.common.event;

import com.enderio.api.grindingball.GrindingBallData;
import com.enderio.base.common.init.EIODataComponents;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class EIODefaultComponents {
    @SubscribeEvent
    public static void modifyDefaultComponents(ModifyDefaultComponentsEvent event) {
        // TODO: 20.6: If player adds flint later, only new flint will be valid?
        //             Maybe requires a dive into DFUs?
        event.modify(Items.FLINT, i -> i.set(EIODataComponents.GRINDING_BALL.get(),
            new GrindingBallData(1.2F, 1.25F, 0.85F, 24000)));
    }
}
