package com.enderio.conduits.integration.ftb_ultimine;

import com.enderio.conduits.EnderIOConduits;
import com.enderio.conduits.api.Conduit;
import com.enderio.conduits.client.model.conduit.facades.FacadeUtil;
import com.enderio.conduits.client.particle.ConduitBreakParticle;
import com.enderio.conduits.common.conduit.bundle.ConduitBundleBlockEntity;
import com.google.common.collect.Maps;
import dev.ftb.mods.ftbultimine.BlockBreakingRegistry;
import dev.ftb.mods.ftbultimine.api.blockbreaking.BlockBreakHandler;
import dev.ftb.mods.ftbultimine.api.shape.Shape;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.Map;

@EventBusSubscriber(modid = EnderIOConduits.MODULE_MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public enum ConduitBlockBreakHandler implements BlockBreakHandler {
    INSTANCE;

    private Map<Player, BreakOperation> breakOperations = Maps.newHashMap();

    @SubscribeEvent
    public static void init(FMLCommonSetupEvent event) {
        BlockBreakingRegistry.INSTANCE.registerHandler(INSTANCE);
        //RegisterBlockBreakHandlerEvent.REGISTER.register(this::registerBlockBreakHandler);
    }

    // TODO: Not fully tested
    @Override
    public Result breakBlock(Player player, BlockPos pos, BlockState state, Shape shape, BlockHitResult hitResult) {
        // TODO: Find a way to share more logic with ConduitBundleBlock...
        var level = player.level();
        BlockPos originPos = hitResult.getBlockPos();

        // Origin bundle breaking, capture necessary context.
        if (pos.equals(originPos)) {
            if (!(level.getBlockEntity(originPos) instanceof ConduitBundleBlockEntity referenceBundle)) {
                return Result.FAIL;
            }

            if (referenceBundle.hasFacade() && FacadeUtil.areFacadesVisible(player)) {
                breakOperations.put(player, new FacadeBreakOperation(referenceBundle.getFacadeBlock()));
            } else {
                Holder<Conduit<?, ?>> conduit = referenceBundle.getShape().getConduit(originPos, hitResult);
                if (conduit == null) {
                    return Result.FAIL;
                }

                breakOperations.put(player, new ConduitBreakOperation(conduit));
            }
        }

        // Get operation
        BreakOperation operation = breakOperations.get(player);
        if (operation == null) {
            return Result.FAIL;
        }

        // Get target bundle
        if (!(level.getBlockEntity(pos) instanceof ConduitBundleBlockEntity targetConduitBundle)) {
            return Result.PASS;
        }

        if (operation instanceof FacadeBreakOperation facadeOperation) {
            if (!targetConduitBundle.hasFacade() || !targetConduitBundle.getFacadeBlock().defaultBlockState().is(facadeOperation.facadeBlock)) {
                return Result.PASS;
            }

            // Drop the facade item
            // TODO: Drop it at the origin pos...
            if (!level.isClientSide()) {
                if (!player.getAbilities().instabuild) {
                    targetConduitBundle.dropFacadeItem();
                }
            }

            int lightLevelBefore = level.getLightEmission(pos);

            targetConduitBundle.setFacadeProvider(ItemStack.EMPTY);

            // Handle light update
            if (lightLevelBefore != level.getLightEmission(pos)) {
                level.getLightEngine().checkBlock(pos);
            }
        } else if (operation instanceof ConduitBreakOperation conduitOperation) {
            if (!targetConduitBundle.hasConduitStrict(conduitOperation.conduit)) {
                return Result.PASS;
            }

            if (level.isClientSide) {
                ConduitBreakParticle.addDestroyEffects(pos, state, conduitOperation.conduit.value());
            }

            targetConduitBundle.removeConduit(conduitOperation.conduit, player);
        }

        if (targetConduitBundle.isEmpty()) {
            if (level.isClientSide()) {
                level.setBlock(pos, level.getFluidState(pos).createLegacyBlock(), 11);
            } else {
                level.removeBlock(pos, false);
            }
        } else {
            level.gameEvent(GameEvent.BLOCK_DESTROY, pos, GameEvent.Context.of(player, state));
        }

        return Result.SUCCESS;
    }

    @Override
    public void postBreak(Player player) {
        breakOperations.remove(player);
    }

    private sealed interface BreakOperation {
    }

    private static final class FacadeBreakOperation implements BreakOperation {
        private final Block facadeBlock;

        private FacadeBreakOperation(Block facadeBlock) {
            this.facadeBlock = facadeBlock;
        }
    }

    private static final class ConduitBreakOperation implements BreakOperation {
        private final Holder<Conduit<?, ?>> conduit;

        private ConduitBreakOperation(Holder<Conduit<?, ?>> conduit) {
            this.conduit = conduit;
        }
    }
}
