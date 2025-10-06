package com.enderio.conduits.common.conduit;

import com.enderio.api.conduit.ConduitType;
import com.enderio.base.client.tooltip.TooltipHandler;
import com.enderio.conduits.common.conduit.block.ConduitBlock;
import com.enderio.conduits.common.conduit.block.ConduitBlockEntity;
import com.enderio.conduits.common.init.ConduitLang;
import com.enderio.core.common.util.TooltipUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class ConduitBlockItem extends BlockItem {

    private final Supplier<? extends ConduitType<?>> type;

    public ConduitBlockItem(Supplier<? extends ConduitType<?>> type, Block block, Properties properties) {
        super(block, properties);
        this.type = type;
    }

    public ConduitType<?> getType() {
        return type.get();
    }

    @Override
    public String getDescriptionId() {
        return getOrCreateDescriptionId();
    }

    @Override
    public InteractionResult place(BlockPlaceContext context) {
        Level level = context.getLevel();

        // Handle placing into an existing block
        if (level.getBlockState(context.getClickedPos()).getBlock() instanceof ConduitBlock conduitBlock) {
            Optional<InteractionResult> result = conduitBlock.handleBlockPlace(context);
            if (result.isPresent()) {
                return result.get();
            }
        }

        return super.place(context);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag tooltipFlag) {
        type.get().addToTooltip(level, tooltip::add, tooltipFlag);
        super.appendHoverText(stack, level, tooltip, tooltipFlag);
    }
}
