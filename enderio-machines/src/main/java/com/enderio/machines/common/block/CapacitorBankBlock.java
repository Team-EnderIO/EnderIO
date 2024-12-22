package com.enderio.machines.common.block;

import com.enderio.EnderIOBase;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.core.client.item.AdvancedTooltipProvider;
import com.enderio.core.common.energy.ItemStackEnergy;
import com.enderio.core.common.util.TooltipUtil;
import com.enderio.machines.EnderIOMachines;
import com.enderio.machines.common.blockentity.base.LegacyMachineBlockEntity;
import com.enderio.machines.common.blockentity.capacitorbank.CapacitorBankBlockEntity;
import com.enderio.machines.common.blockentity.capacitorbank.DisplayMode;
import com.enderio.machines.common.blockentity.multienergy.CapacityTier;
import com.enderio.regilite.holder.RegiliteBlockEntity;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.AdvancementEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.jetbrains.annotations.Nullable;

@EventBusSubscriber(modid = EnderIOMachines.MODULE_MOD_ID)
public class CapacitorBankBlock extends LegacyMachineBlock implements AdvancedTooltipProvider {

    public CapacityTier getTier() {
        return tier;
    }

    public final CapacityTier tier;

    public static final ResourceLocation PLACE_ADVANCEMENT_ID = EnderIOBase.loc("place_capacitor_bank");

    public CapacitorBankBlock(Properties properties,
            RegiliteBlockEntity<? extends LegacyMachineBlockEntity> blockEntityType, CapacityTier tier) {
        super(blockEntityType, properties);
        this.tier = tier;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    }

    @Override
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer,
            ItemStack pStack) {
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
        if (pPlacer != null && pLevel.getBlockEntity(pPos) instanceof CapacitorBankBlockEntity capacitorBankBlock) {
            for (Direction direction : Direction.values()) {
                if (pLevel.getBlockEntity(pPos.relative(direction)) instanceof CapacitorBankBlockEntity other
                        && other.tier == tier) {
                    return;
                }
            }
            capacitorBankBlock.setDisplayMode(pPlacer.getDirection().getOpposite(), DisplayMode.BAR);
        }
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState();
    }

    @Override
    public void addCommonTooltips(ItemStack itemStack, @Nullable Player player, List<Component> tooltips) {
        String energy = String.format("%,d", ItemStackEnergy.getEnergyStored(itemStack)) + "/"
                + String.format("%,d", ItemStackEnergy.getMaxEnergyStored(itemStack));
        tooltips.add(TooltipUtil.styledWithArgs(EIOLang.ENERGY_AMOUNT, energy));
    }

    @SubscribeEvent
    public static void rightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (!event.getLevel().isClientSide && event.getEntity().isShiftKeyDown() && event.getLevel()
                .getBlockEntity(event.getHitVec().getBlockPos()) instanceof CapacitorBankBlockEntity capacitorBank) {
            if (capacitorBank.onShiftRightClick(event.getHitVec().getDirection(), event.getEntity())) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onAdvancement(AdvancementEvent.AdvancementEarnEvent event) {
//        if (event.getAdvancement().id().equals(PLACE_ADVANCEMENT_ID) && event.getEntity() instanceof ServerPlayer serverPlayer) {
//            @Nullable
//            ConnectionData connectionData = NetworkHooks.getConnectionData(serverPlayer.connection.connection);
//            if (connectionData != null && !connectionData.getModList().contains("athena")) {
//                serverPlayer.sendSystemMessage(MachineLang.MULTIBLOCK_CONNECTED_TEXTURES);
//            }
//        }
    }
}
