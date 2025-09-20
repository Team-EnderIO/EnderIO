package com.enderio.base.common.item.tool;

import com.enderio.base.common.init.EIOBlocks;
import com.enderio.base.common.init.EIODataComponents;
import com.enderio.base.common.init.EIOFluids;
import com.enderio.base.common.tag.EIOTags;
import com.enderio.core.common.capability.StrictFluidHandlerItemStack;
import com.enderio.core.common.item.CreativeTabVariants;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import org.apache.logging.log4j.LogManager;

import java.util.List;

public class ColdFireIgniter extends Item implements CreativeTabVariants {

    public static final ICapabilityProvider<ItemStack, Void, IFluidHandlerItem> FLUID_HANDLER_PROVIDER =
        (stack, v) -> new StrictFluidHandlerItemStack(EIODataComponents.ITEM_FLUID_CONTENT, stack, 1000, EIOTags.Fluids.COLD_FIRE_IGNITER_FUEL);

    public ColdFireIgniter(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        ItemStack itemstack = context.getItemInHand();

        var fluidHandler = itemstack.getCapability(Capabilities.FluidHandler.ITEM);
        if (fluidHandler != null) {
            if (fluidHandler.drain(10, IFluidHandler.FluidAction.SIMULATE).getAmount() == 10) {
                fluidHandler.drain(10, IFluidHandler.FluidAction.EXECUTE);
                Player player = context.getPlayer();
                Level level = context.getLevel();
                BlockPos blockpos = context.getClickedPos().relative(context.getClickedFace());
                BlockState coldFireState = EIOBlocks.COLD_FIRE.get().getStateForPlacement(new BlockPlaceContext(context));
                if (coldFireState.canSurvive(context.getLevel(), blockpos)) {
                    level.playSound(player, blockpos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.4F + 0.8F);
                    level.setBlock(blockpos, coldFireState, Block.UPDATE_ALL_IMMEDIATE);
                    level.gameEvent(player, GameEvent.BLOCK_PLACE, blockpos);
                    if (player instanceof ServerPlayer serverPlayer) {
                        CriteriaTriggers.PLACED_BLOCK.trigger(serverPlayer, blockpos, itemstack);
                    }
                    return InteractionResult.sidedSuccess(level.isClientSide());
                }
            }
            return InteractionResult.FAIL;
        }

        LogManager.getLogger().warn("could not find FluidCapability on ColdFireIgniter");
        return super.useOn(context);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return stack.getCapability(Capabilities.FluidHandler.ITEM) != null;
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return 0x99BD42;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        var fluidHandler = stack.getCapability(Capabilities.FluidHandler.ITEM);
        if (fluidHandler != null) {
            return Math.round(fluidHandler.getFluidInTank(0).getAmount() * 13f / fluidHandler.getTankCapacity(0));
        }

        return 0;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext tooltipContext, List<Component> components, TooltipFlag flag) {
        super.appendHoverText(stack, tooltipContext, components, flag);

        var tankCap = stack.getCapability(Capabilities.FluidHandler.ITEM);
        if (tankCap != null) {
            boolean isOneTank = tankCap.getTanks() == 1;
            if (!isOneTank) {
                components.add(Component.literal("Fluids:"));
            }
            for (int i = 0; i < tankCap.getTanks(); i++) {
                String prefix = isOneTank ? "" : i + ": ";
                Component postFix = tankCap.getFluidInTank(i).isEmpty() ? Component.literal("") : tankCap.getFluidInTank(i).getHoverName();
                components.add(Component.literal(prefix + tankCap.getFluidInTank(i).getAmount() + " / " + tankCap.getTankCapacity(i) + " ").append(postFix));
            }
        }
    }

    @Override
    public void addAllVariants(CreativeModeTab.Output modifier) {
        modifier.accept(this);

        ItemStack is = new ItemStack(this);

        var fluidHandler = is.getCapability(Capabilities.FluidHandler.ITEM);
        if (fluidHandler != null) {
            if (fluidHandler instanceof StrictFluidHandlerItemStack strictFluidHandlerItemStack) {
                strictFluidHandlerItemStack.setFluid(new FluidStack(EIOFluids.VAPOR_OF_LEVITY.getSource(), fluidHandler.getTankCapacity(0)));
                modifier.accept(is);
            }
        }
    }
}
