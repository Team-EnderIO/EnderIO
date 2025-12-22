package com.enderio.enderio.client.content.travel;

import com.enderio.enderio.content.travel.TravelHandler;
import net.minecraft.client.player.ClientInput;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(value = Dist.CLIENT)
public class TravelClientEventHandler {
    private static boolean LAST_JUMPING = false;
    private static boolean LAST_SNEAKING = false;
    private static int JUMP_COOLDOWN = 0;

    @SubscribeEvent
    public static void movementInputUpdate(MovementInputUpdateEvent event) {
        ClientInput input = event.getInput();
        Player player = event.getEntity();
        boolean isNewJump = input.keyPresses.jump() && !LAST_JUMPING;
        LAST_JUMPING = input.keyPresses.jump();
        boolean isNewCrouch = input.keyPresses.shift() && !LAST_SNEAKING;
        LAST_SNEAKING = input.keyPresses.shift();

        if (!player.onGround() || !TravelHandler.canBlockTeleport(player)) {
            JUMP_COOLDOWN = 0;
            return;
        }
        if (isNewJump) {
            boolean success = TravelHandler.blockElevatorTeleport(player.level(), player, Direction.UP, true);
            if (!success) {
                success = TravelHandler.blockTeleport(player.level(), player, true);
            }
            if (success) {
                JUMP_COOLDOWN = 7;
            } else {
                JUMP_COOLDOWN = 0;
            }
        } else if (isNewCrouch) {
            boolean success = TravelHandler.blockElevatorTeleport(player.level(), player, Direction.DOWN, true);
            if (!success) {
                TravelHandler.blockTeleport(player.level(), player, true);
            }
        }

        if (JUMP_COOLDOWN > 0) {
            JUMP_COOLDOWN -= 1;

            input.keyPresses = new Input(
                input.keyPresses.forward(),
                input.keyPresses.backward(),
                input.keyPresses.left(),
                input.keyPresses.right(),
                false,
                input.keyPresses.shift(),
                input.keyPresses.sprint()
            );
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
            if (TravelHandler.blockTeleport(event.getLevel(), event.getEntity(), true)) {
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
        if (TravelHandler.blockTeleport(event.getLevel(), event.getEntity(), true)) {
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
        if (TravelHandler.blockTeleport(event.getLevel(), event.getEntity(), true)) {
            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
        }
    }
}
