package com.enderio.enderio.content.machines.capacitor_bank.rework;

import com.enderio.core.client.item.AdvancedTooltipProvider;
import com.enderio.core.common.energy.ItemStackEnergy;
import com.enderio.core.common.util.TooltipUtil;
import com.enderio.enderio.content.machines.capacitor_bank.CapacitorTier;
import com.enderio.enderio.content.machines.capacitor_bank.DisplayMode;
import com.enderio.enderio.foundation.block.EIOEntityBlock;
import com.enderio.enderio.foundation.lang.EIOCommonLang;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class NewCapacitorBankBlock extends EIOEntityBlock<NewCapacitorBankBlockEntity> implements AdvancedTooltipProvider {
    private final CapacitorTier tier;

    public NewCapacitorBankBlock(Supplier<BlockEntityType<? extends NewCapacitorBankBlockEntity>> blockEntityTypeSupplier, Properties properties, CapacitorTier tier) {
        super(blockEntityTypeSupplier, properties);
        this.tier = tier;
    }

    public CapacitorTier getTier() {
        return tier;
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        throw new NotImplementedException("Block codecs are a later problem...");
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer,
        ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (placer != null && level.getBlockEntity(pos) instanceof NewCapacitorBankBlockEntity capacitorBankBlock) {
            for (Direction direction : Direction.values()) {
                if (level.getBlockEntity(pos.relative(direction)) instanceof NewCapacitorBankBlockEntity other
                    && other.getTier() == tier) {
                    return;
                }
            }
            capacitorBankBlock.setDisplayMode(placer.getDirection().getOpposite(), DisplayMode.BAR);
        }
    }

    @Override
    public void addCommonTooltips(ItemStack itemStack, @Nullable Player player, List<Component> tooltips) {
        String energy = String.format("%,d", ItemStackEnergy.getEnergyStored(itemStack)) + "/"
            + String.format("%,d", ItemStackEnergy.getMaxEnergyStored(itemStack));
        tooltips.add(TooltipUtil.styledWithArgs(EIOCommonLang.ENERGY_AMOUNT, energy));
    }

    @SubscribeEvent
    public static void rightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (!event.getLevel().isClientSide && event.getEntity().isShiftKeyDown() && event.getLevel()
            .getBlockEntity(event.getHitVec().getBlockPos()) instanceof NewCapacitorBankBlockEntity capacitorBank) {
            if (capacitorBank.onShiftRightClick(event.getHitVec().getDirection(), event.getEntity())) {
                event.setCanceled(true);
            }
        }
    }
}
