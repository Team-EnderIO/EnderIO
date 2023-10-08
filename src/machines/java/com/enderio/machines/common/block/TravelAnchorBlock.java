package com.enderio.machines.common.block;

import com.enderio.base.common.handler.TravelHandler;
import com.enderio.base.common.travel.TravelSavedData;
import com.enderio.machines.common.blockentity.TravelAnchorBlockEntity;
import com.enderio.machines.common.init.MachineBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.WeakHashMap;

@Mod.EventBusSubscriber
public class TravelAnchorBlock extends MachineBlock {
    private static final Map<Player, PlayerSneakEntry> SNEAK_CACHE = new WeakHashMap<>();

    public TravelAnchorBlock(Properties props) {
        super(props, MachineBlockEntities.TRAVEL_ANCHOR);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return MachineBlockEntities.TRAVEL_ANCHOR.create(pPos, pState);
    }

    @SubscribeEvent
    public static void jump(LivingEvent.LivingJumpEvent jumpEvent) {
        if (!jumpEvent.getEntity().level().isClientSide && jumpEvent.getEntity() instanceof Player player) {
            if (player.level().getBlockState(player.blockPosition().below()).getBlock() instanceof TravelAnchorBlock) {
                TravelHandler.blockTeleport(player.level(), player);
            }
        }
    }

    @SubscribeEvent
    public static void sneak(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && event.player instanceof ServerPlayer player && player
            .level()
            .getBlockState(player.blockPosition().below())
            .getBlock() instanceof TravelAnchorBlock) {

            PlayerSneakEntry sneakEntry = getLastSneakEntry(player);
            if ((!sneakEntry.isSneaking() || sneakEntry.atTime() != player.level().getServer().getTickCount() - 1) && player.isShiftKeyDown()) {
                TravelHandler.blockTeleport(player.level(), player);
            }
            SNEAK_CACHE.put(player, new PlayerSneakEntry(player.isShiftKeyDown(), player.level().getServer().getTickCount()));
        }
    }

    private static PlayerSneakEntry getLastSneakEntry(ServerPlayer player){
        return SNEAK_CACHE.getOrDefault(player, new PlayerSneakEntry(false, player.level().getServer().getTickCount() - 1));
    }

    private record PlayerSneakEntry(boolean isSneaking, int atTime){}

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (level.getBlockEntity(pos) instanceof TravelAnchorBlockEntity anchorBlock) {
            TravelSavedData.getTravelData(level).removeTravelTargetAt(level, pos);
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }
}
