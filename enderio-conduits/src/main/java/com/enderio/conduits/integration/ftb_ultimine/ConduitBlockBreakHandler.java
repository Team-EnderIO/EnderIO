package com.enderio.conduits.integration.ftb_ultimine;

import com.enderio.conduits.EnderIOConduits;
import com.enderio.conduits.api.Conduit;
import com.enderio.conduits.client.model.conduit.facades.FacadeUtil;
import com.enderio.conduits.client.particle.ConduitBreakParticle;
import com.enderio.conduits.common.conduit.bundle.ConduitBundleBlockEntity;
import dev.ftb.mods.ftbultimine.BlockBreakingRegistry;
import dev.ftb.mods.ftbultimine.api.blockbreaking.BlockBreakHandler;
import dev.ftb.mods.ftbultimine.api.shape.Shape;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

@EventBusSubscriber(modid = EnderIOConduits.MODULE_MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public enum ConduitBlockBreakHandler implements BlockBreakHandler {
    INSTANCE;

    @SubscribeEvent
    public static void init(FMLCommonSetupEvent event) {
        BlockBreakingRegistry.INSTANCE.registerHandler(INSTANCE);
        //RegisterBlockBreakHandlerEvent.REGISTER.register(this::registerBlockBreakHandler);
    }

    // TODO: Not fully tested
    @Override
    public Result breakBlock(Player player, BlockPos blockPos, BlockState blockState, Shape shape, BlockHitResult blockHitResult) {
        // TODO: Find a way to share more logic with ConduitBundleBlock...
        var level = player.level();

        // Get original mining target.
        // TODO: The reference gets broken first...
        if (!(level.getBlockEntity(blockPos) instanceof ConduitBundleBlockEntity referenceBundle)) {
            return Result.PASS;
        }

        // Get target bundle
        if (!(level.getBlockEntity(blockPos) instanceof ConduitBundleBlockEntity targetConduitBundle)) {
            return Result.PASS;
        }

        if (referenceBundle.hasFacade() && FacadeUtil.areFacadesVisible(player)) {
            if (!targetConduitBundle.hasFacade() || !targetConduitBundle.getFacadeBlock().defaultBlockState().is(referenceBundle.getFacadeBlock())) {
                return Result.PASS;
            }

            if (!level.isClientSide()) {
                if (!player.getAbilities().instabuild) {
                    targetConduitBundle.dropFacadeItem();
                }
            }

            int lightLevelBefore = level.getLightEmission(blockPos);

            targetConduitBundle.setFacadeProvider(ItemStack.EMPTY);

            // Handle light update
            if (lightLevelBefore != level.getLightEmission(blockPos)) {
                level.getLightEngine().checkBlock(blockPos);
            }

            // TODO: Needs isEmpty check...

            return Result.SUCCESS;
        } else {
            // TODO: Integrate conduit A11Y logic.

            Holder<Conduit<?, ?>> conduit = referenceBundle.getShape().getConduit((blockHitResult).getBlockPos(), blockHitResult);
            if (conduit == null) {
                return Result.PASS;
            }

            if (!targetConduitBundle.hasConduitStrict(conduit)) {
                return Result.PASS;
            }

            if (level.isClientSide) {
                ConduitBreakParticle.addDestroyEffects(blockPos, blockState, conduit.value());
            }

            targetConduitBundle.removeConduit(conduit, player);

            if (targetConduitBundle.isEmpty()) {
                if (level.isClientSide()) {
                    level.setBlock(blockPos, level.getFluidState(blockPos).createLegacyBlock(), 11);
                } else {
                    level.removeBlock(blockPos, false);
                }
            } else {
                level.gameEvent(GameEvent.BLOCK_DESTROY, blockPos, GameEvent.Context.of(player, blockState));
            }

            return Result.SUCCESS;
        }
    }
}
