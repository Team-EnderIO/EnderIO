package com.enderio.machines.client;

import com.enderio.base.common.handler.TravelHandler;
import com.enderio.machines.EnderIOMachines;
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

@EventBusSubscriber(modid = EnderIOMachines.MODULE_MOD_ID, value = Dist.CLIENT)
public class MachinesClientEvents {
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
