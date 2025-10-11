package com.enderio.enderio.client.content.travel;

import com.enderio.enderio.api.poi.EnderPOI;
import com.enderio.enderio.content.travel.TravelHandler;
import net.minecraft.client.player.Input;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.Optional;

@EventBusSubscriber(value = Dist.CLIENT)
public class TravelClientEventHandler {
    private static boolean LAST_JUMPING = false;
    private static boolean LAST_SNEAKING = false;
    private static int JUMP_COOLDOWN = 0;

    @SubscribeEvent
    public static void movementInputUpdate(MovementInputUpdateEvent event) {
        Input input = event.getInput();
        Player player = event.getEntity();
        boolean isNewJump = input.jumping && !LAST_JUMPING;
        LAST_JUMPING = input.jumping;
        boolean isNewCrouch = input.shiftKeyDown && !LAST_SNEAKING;
        LAST_SNEAKING = input.shiftKeyDown;

        if (!player.onGround() || !TravelHandler.canBlockTeleport(player)) {
            JUMP_COOLDOWN = 0;
            return;
        }
        if (isNewJump) {
            Optional<EnderPOI> enderPOI = TravelHandler.getElevatorAnchorTarget(player, Direction.UP);
            boolean success = enderPOI.isPresent() && enderPOI.get().onActivation(player.level(), player);
            if (success) {
                JUMP_COOLDOWN = 7;
            } else {
                JUMP_COOLDOWN = 0;
            }
        } else if (isNewCrouch) {
            Optional<EnderPOI> enderPOI = TravelHandler.getElevatorAnchorTarget(player, Direction.DOWN);
            enderPOI.ifPresent(poi -> poi.onActivation(player.level(), player));
        }

        if (JUMP_COOLDOWN > 0) {
            JUMP_COOLDOWN -= 1;
            input.jumping = false;
        }
    }

    @SubscribeEvent
    public static void emptyClick(PlayerInteractEvent.RightClickEmpty event) {
        Player player = event.getEntity();
        // Credit to castcrafter/travel_anchors
        if (TravelHandler.canBlockTeleport(player) && !player.isShiftKeyDown() && event.getHand() == InteractionHand.MAIN_HAND && event
            .getEntity()
            .getItemInHand(InteractionHand.OFF_HAND)
            .isEmpty() && event.getItemStack().isEmpty()) {
            Optional<EnderPOI> enderPOI = TravelHandler.getEnderPOIs(player);
            if (enderPOI.isPresent() && enderPOI.get().onActivation(event.getLevel(), event.getEntity())) {
                player.swing(event.getHand(), true);
                // TODO: 20.6: Is this important?
                //event.setCancellationResult(InteractionResult.SUCCESS);
            }
        }
    }

    @SubscribeEvent
    public static void blockClick(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        if (!TravelHandler.canBlockTeleport(player)) {
            return;
        }
        Optional<EnderPOI> enderPOI = TravelHandler.getEnderPOIs(player);
        if (enderPOI.isPresent() && enderPOI.get().onActivation(event.getLevel(), event.getEntity())) {
            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void itemClick(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        if (!TravelHandler.canBlockTeleport(player)) {
            return;
        }
        Optional<EnderPOI> enderPOI = TravelHandler.getEnderPOIs(player);
        if (enderPOI.isPresent() && enderPOI.get().onActivation(event.getLevel(), event.getEntity())) {
            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
        }
    }
}
