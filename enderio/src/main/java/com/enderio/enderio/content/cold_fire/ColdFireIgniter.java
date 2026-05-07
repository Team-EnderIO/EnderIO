package com.enderio.enderio.content.cold_fire;

import com.enderio.core.common.capability.StrictItemAccessFluidHandler;
import com.enderio.core.common.item.ICustomCreativeTabEntries;
import com.enderio.enderio.foundation.tag.EIOTags;
import com.enderio.enderio.init.EIOBlocks;
import com.enderio.enderio.init.EIODataComponents;
import com.enderio.enderio.init.EIOFluids;
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
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.resource.ResourceStack;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.apache.logging.log4j.LogManager;

import java.util.function.Consumer;

public class ColdFireIgniter extends Item implements ICustomCreativeTabEntries {

    public static final ICapabilityProvider<ItemStack, ItemAccess, ResourceHandler<FluidResource>> FLUID_HANDLER_PROVIDER =
        (stack, itemAccess) -> new StrictItemAccessFluidHandler(itemAccess, EIODataComponents.ITEM_FLUID_CONTENT, 1000,
            EIOTags.Fluids.COLD_FIRE_IGNITER_FUEL);

    public ColdFireIgniter(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        ItemStack itemstack = context.getItemInHand();

        var fluidHandler = itemstack.getCapability(Capabilities.Fluid.ITEM, ItemAccess.forPlayerInteraction(context.getPlayer(), context.getHand()));
        if (fluidHandler != null) {
            try (Transaction transaction = Transaction.openRoot()) {

                ResourceStack<FluidResource> extracted = ResourceHandlerUtil.extractFirst(fluidHandler,
                    fr -> fr.is(EIOTags.Fluids.COLD_FIRE_IGNITER_FUEL), 10, transaction);

                if (extracted.amount() == 10) {
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

                        transaction.commit();
                        return InteractionResult.SUCCESS;
                    }
                }
            }

            return InteractionResult.FAIL;
        }

        LogManager.getLogger().warn("could not find FluidCapability on ColdFireIgniter");
        return super.useOn(context);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return stack.getCapability(Capabilities.Fluid.ITEM, ItemAccess.forStack(stack)) != null;
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return 0x99BD42;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        var fluidHandler = stack.getCapability(Capabilities.Fluid.ITEM, ItemAccess.forStack(stack));
        if (fluidHandler != null) {
            return Math.round(fluidHandler.getAmountAsInt(0) * 13f / fluidHandler.getCapacityAsInt(0, fluidHandler.getResource(0)));
        }

        return 0;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltipDisplay, tooltipAdder, flag);

        // TODO: This feels like the kind of thing that should be shared logic somewhere.
        var tankCap = stack.getCapability(Capabilities.Fluid.ITEM, ItemAccess.forStack(stack));
        if (tankCap != null) {
            boolean isOneTank = tankCap.size() == 1;
            if (!isOneTank) {
                tooltipAdder.accept(Component.literal("Fluids:"));
            }
            for (int i = 0; i < tankCap.size(); i++) {
                String prefix = isOneTank ? "" : i + ": ";
                Component postFix = tankCap.getAmountAsInt(i) <= 0 ? Component.literal("") : tankCap.getResource(i).getHoverName();
                tooltipAdder.accept(Component.literal(prefix + tankCap.getAmountAsInt(i) + " / " + tankCap.getCapacityAsInt(i, tankCap.getResource(i)) + " ").append(postFix));
            }
        }
    }

    @Override
    public void addAdditionalCreativeTabEntries(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output) {
        ItemStack is = new ItemStack(this);

        var fluidHandler = is.getCapability(Capabilities.Fluid.ITEM, ItemAccess.forStack(is));
        if (fluidHandler != null) {
            int capacity = fluidHandler.getCapacityAsInt(0, FluidResource.of(EIOFluids.VAPOR_OF_LEVITY.source()));
            is.set(EIODataComponents.ITEM_FLUID_CONTENT, SimpleFluidContent.copyOf(new FluidStack(EIOFluids.VAPOR_OF_LEVITY.source(), capacity)));
            output.accept(is);
        }
    }
}
