package com.enderio.conduits.common.facades;

import com.enderio.api.conduit.facade.BlockPaintData;
import com.enderio.api.conduit.facade.ConduitFacadeProvider;
import com.enderio.api.conduit.facade.FacadeType;
import com.enderio.base.common.init.EIOCapabilities;
import com.enderio.base.common.util.PaintUtils;
import com.enderio.conduits.common.init.ConduitBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ConduitFacadeItem extends BlockItem {
    
    private final FacadeType facadeType;
    
    public ConduitFacadeItem(FacadeType facadeType, Properties properties) {
        super(ConduitBlocks.CONDUIT.get(), properties);
        this.facadeType = facadeType;
    }

    @Override
    public String getDescriptionId() {
        // Use item description.
        return this.getOrCreateDescriptionId();
    }

    @Override
    public InteractionResult place(BlockPlaceContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        BlockPos clickedPos = context.getClickedPos();
        
        // Check if clicking on an existing conduit block
        BlockState blockState = level.getBlockState(clickedPos);
        if (blockState.getBlock() instanceof com.enderio.conduits.common.conduit.block.ConduitBlock) {
            // Use the block's interaction handler for existing conduits
            return blockState.use(level, player, context.getHand(), context.getHitResult());
        }
        
        // Placing in air or replacing a block - create new conduit bundle with only facade
        if (!blockState.canBeReplaced(context)) {
            return InteractionResult.FAIL;
        }
        
        // Place the conduit block
        InteractionResult result = super.place(context);
        if (result.consumesAction() && level.getBlockEntity(clickedPos) instanceof com.enderio.conduits.common.conduit.block.ConduitBlockEntity conduit) {
            // Set the facade on the newly placed block
            conduit.getBundle().setFacadeProvider(context.getItemInHand().copy());
            if (!level.isClientSide()) {
                conduit.updateShape();
                conduit.updateClient();
            }
            
            // Consume the item
            if (player != null && !player.getAbilities().instabuild) {
                context.getItemInHand().shrink(1);
            }
        }
        
        return result;
    }

    @Override
    protected boolean canPlace(BlockPlaceContext context, BlockState state) {
        // Must have a valid facade
        var facade = context.getItemInHand().getCapability(EIOCapabilities.FACADE).orElse(null);
        if (facade == null || !facade.isValid()) {
            return false;
        }

        return super.canPlace(context, state);
    }
    
    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new FacadeCapabilityProvider(stack, facadeType);
    }
    
    private static class FacadeCapabilityProvider implements ICapabilityProvider, ConduitFacadeProvider {
        private final ItemStack stack;
        private final FacadeType facadeType;
        private final LazyOptional<ConduitFacadeProvider> holder;
        
        public FacadeCapabilityProvider(ItemStack stack, FacadeType facadeType) {
            this.stack = stack;
            this.facadeType = facadeType;
            this.holder = LazyOptional.of(() -> this);
        }

        @Override
        public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
            if (cap == EIOCapabilities.FACADE) {
                return holder.cast();
            }
            return LazyOptional.empty();
        }

        @Override
        public boolean isValid() {
            return PaintUtils.hasPaint(stack);
        }

        @Override
        public Block block() {
            return PaintUtils.getPaint(stack);
        }

        @Override
        public FacadeType type() {
            return facadeType;
        }
    }
}
