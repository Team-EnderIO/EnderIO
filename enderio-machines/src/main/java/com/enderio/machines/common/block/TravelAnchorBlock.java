package com.enderio.machines.common.block;

import com.enderio.base.common.handler.travel.TeleportHandler;
import com.enderio.machines.common.init.MachineBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.world.NoteBlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.WeakHashMap;

@Mod.EventBusSubscriber
public class TravelAnchorBlock extends Block implements EntityBlock {

    private static final WeakHashMap<Player, Pair<Boolean, Integer>> sneakCache = new WeakHashMap<>();

    public TravelAnchorBlock(Properties props) {
        super(props);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return MachineBlockEntities.TRAVEL_ANCHOR.create(pPos, pState);
    }

    @SubscribeEvent
    public static void jump(LivingEvent.LivingJumpEvent jumpEvent) {
        if (!jumpEvent.getEntity().level.isClientSide && jumpEvent.getEntity() instanceof Player player) {
            if (player.level.getBlockState(player.blockPosition().below()).getBlock() instanceof TravelAnchorBlock) {
                TeleportHandler.blockTeleport(player.level, player);
            }
        }
    }

    @SubscribeEvent
    public static void sneak(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END
            && event.player instanceof ServerPlayer player
            && player.level.getBlockState(player.blockPosition().below()).getBlock() instanceof TravelAnchorBlock) {

            Pair<Boolean, Integer> sneakEntry = sneakCache.getOrDefault(player, Pair.of(false, player.getLevel().getServer().getTickCount()-1));
            if ((!sneakEntry.getLeft() || sneakEntry.getRight() != player.getLevel().getServer().getTickCount()-1)
                && player.isShiftKeyDown()) {

                TeleportHandler.blockTeleport(player.level, player);
            }
            sneakCache.put(player, Pair.of(player.isShiftKeyDown(), player.getLevel().getServer().getTickCount()));
        }
    }
}
