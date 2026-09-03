package com.enderio.enderio.gametests.conduits;

import com.enderio.enderio.api.conduits.Conduit;
import com.enderio.enderio.content.conduits.ConduitBlockItem;
import com.enderio.enderio.content.conduits.bundle.ConduitBundleBlockEntity;
import com.enderio.enderio.content.paint.BlockPaintData;
import com.enderio.enderio.init.EIOConduits;
import com.enderio.enderio.init.EIODataComponents;
import com.enderio.enderio.init.EIOItems;
import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.testframework.DynamicTest;
import net.neoforged.testframework.annotation.ForEachTest;
import net.neoforged.testframework.annotation.TestHolder;
import net.neoforged.testframework.gametest.StructureTemplateBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ForEachTest(groups = "conduit.break")
public class ConduitBreakTests {
    private static final BlockPos BUNDLE_POS = new BlockPos(0, 1, 0);

    @GameTest
    @TestHolder(description = "Ensures breaking the last conduit drops its conduit item and installed filters.")
    public static void breaksSingleConduit(final DynamicTest test) {
        test.registerGameTestTemplate(() -> StructureTemplateBuilder.withSize(1, 1, 1));

        test.onGameTest(ConduitGameTestHelper.class, helper -> {
            var itemConduit = helper.getConduit(EIOConduits.ITEM);

            helper.startSequence().thenExecute(() -> {
                prepareBundle(helper);
                helper.placeConduit(itemConduit, BUNDLE_POS.getX(), BUNDLE_POS.getY(), BUNDLE_POS.getZ());
                installItemFilter(helper, itemConduit);

                if (!breakAsPlayerByConduit(helper, itemConduit)) {
                    throw new GameTestAssertException("Breaking the last conduit did not remove the bundle.");
                }

                assertNoBundle(helper);
                assertDrops(helper, ConduitBlockItem.getStackFor(itemConduit, 1), basicItemFilter());
            }).thenSucceed();
        });
    }

    @GameTest
    @TestHolder(description = "Ensures breaking one conduit from a bundle preserves the other conduit and its resources.")
    public static void breaksOneOfMultipleConduits(final DynamicTest test) {
        test.registerGameTestTemplate(() -> StructureTemplateBuilder.withSize(1, 1, 1));

        test.onGameTest(ConduitGameTestHelper.class, helper -> {
            var itemConduit = helper.getConduit(EIOConduits.ITEM);
            var redstoneConduit = helper.getConduit(EIOConduits.REDSTONE);

            helper.startSequence().thenExecute(() -> {
                prepareBundle(helper);
                helper.placeConduit(itemConduit, BUNDLE_POS.getX(), BUNDLE_POS.getY(), BUNDLE_POS.getZ());
                addConduit(helper, redstoneConduit);
                installItemFilter(helper, itemConduit);

                if (breakAsPlayerByConduit(helper, redstoneConduit)) {
                    throw new GameTestAssertException("Breaking one of multiple conduits removed the whole bundle.");
                }

                assertOnlyConduitsRemain(helper, itemConduit);
                assertItemFilterRemains(helper, itemConduit);
                assertDrops(helper, ConduitBlockItem.getStackFor(redstoneConduit, 1));
            }).thenSucceed();
        });
    }

    @GameTest
    @TestHolder(description = "Ensures a visible facade is broken before the final conduit and its filters.")
    public static void breaksFacadeBeforeSingleConduit(final DynamicTest test) {
        test.registerGameTestTemplate(() -> StructureTemplateBuilder.withSize(1, 1, 1));

        test.onGameTest(ConduitGameTestHelper.class, helper -> {
            var itemConduit = helper.getConduit(EIOConduits.ITEM);

            helper.startSequence().thenExecute(() -> {
                prepareBundle(helper);
                helper.placeConduit(itemConduit, BUNDLE_POS.getX(), BUNDLE_POS.getY(), BUNDLE_POS.getZ());
                installItemFilter(helper, itemConduit);
                addFacade(helper);

                if (breakAsPlayer(helper)) {
                    throw new GameTestAssertException("Breaking a facade removed its conduit bundle.");
                }

                var bundle = bundle(helper);
                if (bundle.hasFacade()) {
                    throw new GameTestAssertException("Breaking the facade did not preserve the conduit.");
                }
                assertOnlyConduitsRemain(helper, itemConduit);
                assertItemFilterRemains(helper, itemConduit);
                assertDrops(helper, facadeStack());
                clearDrops(helper);

                if (!breakAsPlayerByConduit(helper, itemConduit)) {
                    throw new GameTestAssertException("Breaking the exposed final conduit did not remove the bundle.");
                }

                assertNoBundle(helper);
                assertDrops(helper, ConduitBlockItem.getStackFor(itemConduit, 1), basicItemFilter());
            }).thenSucceed();
        });
    }

    @GameTest
    @TestHolder(description = "Ensures a visible facade is broken before one conduit from a multi-conduit bundle.")
    public static void breaksFacadeBeforeOneOfMultipleConduits(final DynamicTest test) {
        test.registerGameTestTemplate(() -> StructureTemplateBuilder.withSize(1, 1, 1));

        test.onGameTest(ConduitGameTestHelper.class, helper -> {
            var itemConduit = helper.getConduit(EIOConduits.ITEM);
            var redstoneConduit = helper.getConduit(EIOConduits.REDSTONE);

            helper.startSequence().thenExecute(() -> {
                prepareBundle(helper);
                helper.placeConduit(itemConduit, BUNDLE_POS.getX(), BUNDLE_POS.getY(), BUNDLE_POS.getZ());
                addConduit(helper, redstoneConduit);
                installItemFilter(helper, itemConduit);
                addFacade(helper);

                if (breakAsPlayer(helper)) {
                    throw new GameTestAssertException("Breaking a facade removed its conduit bundle.");
                }

                var bundle = bundle(helper);
                if (bundle.hasFacade()) {
                    throw new GameTestAssertException("Breaking the facade did not preserve both conduits.");
                }
                assertOnlyConduitsRemain(helper, itemConduit, redstoneConduit);
                assertItemFilterRemains(helper, itemConduit);
                assertDrops(helper, facadeStack());
                clearDrops(helper);

                if (breakAsPlayerByConduit(helper, redstoneConduit)) {
                    throw new GameTestAssertException("Breaking one conduit removed the whole bundle.");
                }

                assertOnlyConduitsRemain(helper, itemConduit);
                assertItemFilterRemains(helper, itemConduit);
                assertDrops(helper, ConduitBlockItem.getStackFor(redstoneConduit, 1));
            }).thenSucceed();
        });
    }

    @GameTest
    @TestHolder(description = "Ensures replacing a conduit bundle block does not drop its conduits, facade, or filters.")
    public static void overwritingBundleDoesNotDropResources(final DynamicTest test) {
        test.registerGameTestTemplate(() -> StructureTemplateBuilder.withSize(1, 1, 1));

        test.onGameTest(ConduitGameTestHelper.class, helper -> {
            var itemConduit = helper.getConduit(EIOConduits.ITEM);
            var redstoneConduit = helper.getConduit(EIOConduits.REDSTONE);

            helper.startSequence().thenExecute(() -> {
                prepareBundle(helper);
                helper.placeConduit(itemConduit, BUNDLE_POS.getX(), BUNDLE_POS.getY(), BUNDLE_POS.getZ());
                addConduit(helper, redstoneConduit);
                installItemFilter(helper, itemConduit);
                addFacade(helper);

                helper.setBlock(BUNDLE_POS, Blocks.STONE.defaultBlockState());

                helper.assertBlockPresent(Blocks.STONE, BUNDLE_POS);
                assertDrops(helper);
            }).thenSucceed();
        });
    }

    @GameTest
    @TestHolder(description = "Ensures sneaking with a wrench removes the final conduit and drops its resources.")
    public static void wrenchRemovesSingleConduit(final DynamicTest test) {
        test.registerGameTestTemplate(() -> StructureTemplateBuilder.withSize(1, 1, 1));

        test.onGameTest(ConduitGameTestHelper.class, helper -> {
            var itemConduit = helper.getConduit(EIOConduits.ITEM);

            helper.startSequence().thenExecute(() -> {
                prepareBundle(helper);
                helper.placeConduit(itemConduit, BUNDLE_POS.getX(), BUNDLE_POS.getY(), BUNDLE_POS.getZ());
                installItemFilter(helper, itemConduit);

                wrenchAsPlayer(helper, itemConduit);

                assertNoBundle(helper);
                assertDrops(helper, ConduitBlockItem.getStackFor(itemConduit, 1), basicItemFilter());
            }).thenSucceed();
        });
    }

    @GameTest
    @TestHolder(description = "Ensures sneaking with a wrench removes only one conduit from a multi-conduit bundle.")
    public static void wrenchRemovesOneOfMultipleConduits(final DynamicTest test) {
        test.registerGameTestTemplate(() -> StructureTemplateBuilder.withSize(1, 1, 1));

        test.onGameTest(ConduitGameTestHelper.class, helper -> {
            var itemConduit = helper.getConduit(EIOConduits.ITEM);
            var redstoneConduit = helper.getConduit(EIOConduits.REDSTONE);

            helper.startSequence().thenExecute(() -> {
                prepareBundle(helper);
                helper.placeConduit(itemConduit, BUNDLE_POS.getX(), BUNDLE_POS.getY(), BUNDLE_POS.getZ());
                addConduit(helper, redstoneConduit);
                installItemFilter(helper, itemConduit);

                wrenchAsPlayer(helper, redstoneConduit);

                assertOnlyConduitsRemain(helper, itemConduit);
                assertItemFilterRemains(helper, itemConduit);
                assertDrops(helper, ConduitBlockItem.getStackFor(redstoneConduit, 1));
            }).thenSucceed();
        });
    }

    @GameTest
    @TestHolder(description = "Ensures sneaking with a wrench removes a final conduit while preserving its facade.")
    public static void wrenchRemovesSingleConduitWithFacade(final DynamicTest test) {
        test.registerGameTestTemplate(() -> StructureTemplateBuilder.withSize(1, 1, 1));

        test.onGameTest(ConduitGameTestHelper.class, helper -> {
            var itemConduit = helper.getConduit(EIOConduits.ITEM);

            helper.startSequence().thenExecute(() -> {
                prepareBundle(helper);
                helper.placeConduit(itemConduit, BUNDLE_POS.getX(), BUNDLE_POS.getY(), BUNDLE_POS.getZ());
                installItemFilter(helper, itemConduit);
                addFacade(helper);

                wrenchAsPlayer(helper, itemConduit);

                var bundle = bundle(helper);
                if (!bundle.hasFacade() || !bundle.getConduits().isEmpty()) {
                    throw new GameTestAssertException("Wrenching the final conduit did not preserve only the facade.");
                }
                assertDrops(helper, ConduitBlockItem.getStackFor(itemConduit, 1), basicItemFilter());
            }).thenSucceed();
        });
    }

    @GameTest
    @TestHolder(description = "Ensures sneaking with a wrench preserves a facade while removing one conduit.")
    public static void wrenchRemovesOneOfMultipleConduitsWithFacade(final DynamicTest test) {
        test.registerGameTestTemplate(() -> StructureTemplateBuilder.withSize(1, 1, 1));

        test.onGameTest(ConduitGameTestHelper.class, helper -> {
            var itemConduit = helper.getConduit(EIOConduits.ITEM);
            var redstoneConduit = helper.getConduit(EIOConduits.REDSTONE);

            helper.startSequence().thenExecute(() -> {
                prepareBundle(helper);
                helper.placeConduit(itemConduit, BUNDLE_POS.getX(), BUNDLE_POS.getY(), BUNDLE_POS.getZ());
                addConduit(helper, redstoneConduit);
                installItemFilter(helper, itemConduit);
                addFacade(helper);

                wrenchAsPlayer(helper, redstoneConduit);

                if (!bundle(helper).hasFacade()) {
                    throw new GameTestAssertException("Wrenching one conduit removed the facade.");
                }
                assertOnlyConduitsRemain(helper, itemConduit);
                assertItemFilterRemains(helper, itemConduit);
                assertDrops(helper, ConduitBlockItem.getStackFor(redstoneConduit, 1));
            }).thenSucceed();
        });
    }

    private static void prepareBundle(ConduitGameTestHelper helper) {
        helper.setBlock(BUNDLE_POS, Blocks.AIR.defaultBlockState());
        clearDrops(helper);
    }

    private static ConduitBundleBlockEntity bundle(ConduitGameTestHelper helper) {
        return helper.getConduitBundle(BUNDLE_POS.getX(), BUNDLE_POS.getY(), BUNDLE_POS.getZ(), false);
    }

    private static void installItemFilter(ConduitGameTestHelper helper, Holder<Conduit<?, ?>> itemConduit) {
        bundle(helper).getConnectionInventory(itemConduit, Direction.NORTH).setStackInSlot(0, basicItemFilter());
    }

    private static void addConduit(ConduitGameTestHelper helper, Holder<Conduit<?, ?>> conduit) {
        bundle(helper).addConduit(conduit, null, helper.getPlayer());
        if (!bundle(helper).hasConduitStrict(conduit)) {
            throw new GameTestAssertException("Could not add a second conduit to the bundle.");
        }
        bundle(helper).updateShape();
    }

    private static void addFacade(ConduitGameTestHelper helper) {
        bundle(helper).setFacadeProvider(facadeStack());
    }

    private static ItemStack basicItemFilter() {
        return new ItemStack(EIOItems.BASIC_ITEM_FILTER.get());
    }

    private static ItemStack facadeStack() {
        var facade = new ItemStack(EIOItems.CONDUIT_FACADE.get());
        facade.set(EIODataComponents.BLOCK_PAINT, BlockPaintData.of(Blocks.STONE));
        return facade;
    }

    private static boolean breakAsPlayer(ConduitGameTestHelper helper) {
        BlockPos pos = helper.absolutePos(BUNDLE_POS);
        var state = helper.getLevel().getBlockState(pos);
        var hit = new BlockHitResult(pos.getCenter(), Direction.NORTH, pos, false);
        var player = new TargetingFakePlayer(helper.getLevel(), hit);
        return state.onDestroyedByPlayer(helper.getLevel(), pos, player, true, helper.getLevel().getFluidState(pos));
    }

    private static boolean breakAsPlayerByConduit(ConduitGameTestHelper helper, Holder<Conduit<?, ?>> target) {
        BlockPos pos = helper.absolutePos(BUNDLE_POS);
        var state = helper.getLevel().getBlockState(pos);
        var hit = createConduitHit(helper, pos, target);
        var player = new TargetingFakePlayer(helper.getLevel(), hit);
        return state.onDestroyedByPlayer(helper.getLevel(), pos, player, true, helper.getLevel().getFluidState(pos));
    }

    private static void wrenchAsPlayer(ConduitGameTestHelper helper, Holder<Conduit<?, ?>> target) {
        BlockPos pos = helper.absolutePos(BUNDLE_POS);
        var player = new TargetingFakePlayer(helper.getLevel(), createConduitHit(helper, pos, target));
        player.setShiftKeyDown(true);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(EIOItems.YETA_WRENCH.get()));

        ItemInteractionResult result = bundle(helper).onWrenched(
            new UseOnContext(player, InteractionHand.MAIN_HAND, (BlockHitResult) player.pick(player.blockInteractionRange() + 5, 0.0f, false)));
        if (result == ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION) {
            throw new GameTestAssertException("Wrenching a conduit was not handled.");
        }
    }

    private static BlockHitResult createConduitHit(ConduitGameTestHelper helper, BlockPos pos, Holder<Conduit<?, ?>> target) {
        List<AABB> conduitShapes = bundle(helper).getShape().getShapeFor(target).toAabbs();
        for (AABB conduitShape : conduitShapes) {
            Vec3 hitLocation = conduitShape.getCenter().add(pos.getX(), pos.getY(), pos.getZ());
            var hit = new BlockHitResult(hitLocation, Direction.NORTH, pos, false);
            if (target.equals(bundle(helper).getShape().getConduit(pos, hit))) {
                return hit;
            }
        }

        throw new GameTestAssertException("Could not create a player hit for conduit " + target.getRegisteredName());
    }

    private static class TargetingFakePlayer extends FakePlayer {
        private final HitResult hit;

        private TargetingFakePlayer(ServerLevel level, HitResult hit) {
            super(level, new GameProfile(UUID.randomUUID(), "[EnderIO] break test"));
            this.hit = hit;
        }

        @Override
        public HitResult pick(double distance, float partialTick, boolean includeFluids) {
            return hit;
        }
    }

    @SafeVarargs
    private static void assertOnlyConduitsRemain(ConduitGameTestHelper helper, Holder<Conduit<?, ?>>... expectedConduits) {
        var bundle = bundle(helper);
        if (bundle.getConduits().size() != expectedConduits.length) {
            throw new GameTestAssertException("Expected " + expectedConduits.length + " conduits to remain but found " + bundle.getConduits().size());
        }

        for (Holder<Conduit<?, ?>> expectedConduit : expectedConduits) {
            if (!bundle.hasConduitStrict(expectedConduit)) {
                throw new GameTestAssertException("Expected conduit to remain in the bundle: " + expectedConduit.getRegisteredName());
            }
        }
    }

    private static void assertItemFilterRemains(ConduitGameTestHelper helper, Holder<Conduit<?, ?>> itemConduit) {
        var filter = bundle(helper).getConnectionInventory(itemConduit, Direction.NORTH).getStackInSlot(0);
        if (!ItemStack.isSameItemSameComponents(filter, basicItemFilter())) {
            throw new GameTestAssertException("The filter on the remaining item conduit was removed.");
        }
    }

    private static void assertNoBundle(ConduitGameTestHelper helper) {
        if (helper.getConduitBundle(BUNDLE_POS.getX(), BUNDLE_POS.getY(), BUNDLE_POS.getZ(), true) != null) {
            throw new GameTestAssertException("Expected the conduit bundle to be removed.");
        }
    }

    private static void assertDrops(ConduitGameTestHelper helper, ItemStack... expectedDrops) {
        List<ItemStack> actualDrops = itemDrops(helper);
        if (actualDrops.size() != expectedDrops.length) {
            throw new GameTestAssertException("Expected " + expectedDrops.length + " item drops but found " + actualDrops.size());
        }

        List<ItemStack> unmatchedDrops = new ArrayList<>(actualDrops);
        for (ItemStack expected : expectedDrops) {
            boolean matched = unmatchedDrops.removeIf(
                actual -> actual.getCount() == expected.getCount() && ItemStack.isSameItemSameComponents(actual, expected));
            if (!matched) {
                throw new GameTestAssertException("Expected drop was not found: " + expected);
            }
        }
    }

    private static List<ItemStack> itemDrops(ConduitGameTestHelper helper) {
        BlockPos pos = helper.absolutePos(BUNDLE_POS);
        return helper.getLevel().getEntitiesOfClass(ItemEntity.class, new AABB(pos).inflate(2.0)).stream().map(ItemEntity::getItem).toList();
    }

    private static void clearDrops(ConduitGameTestHelper helper) {
        BlockPos pos = helper.absolutePos(BUNDLE_POS);
        helper.getLevel().getEntitiesOfClass(ItemEntity.class, new AABB(pos).inflate(2.0)).forEach(ItemEntity::discard);
    }
}
