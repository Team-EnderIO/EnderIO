package com.enderio.conduits.tests;

import com.enderio.conduits.api.Conduit;
import com.enderio.conduits.api.EnderIOConduitsRegistries;
import com.enderio.conduits.api.network.IConduitNetwork;
import com.enderio.conduits.common.conduit.ConduitBlockItem;
import com.enderio.conduits.common.conduit.bundle.ConduitBundleBlockEntity;
import com.mojang.authlib.GameProfile;
import java.util.UUID;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestInfo;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.testframework.gametest.ExtendedGameTestHelper;
import org.jetbrains.annotations.Nullable;

public class ConduitGameTestHelper extends ExtendedGameTestHelper {
    private static final UUID DEFAULT_FAKE_PLAYER_UUID = UUID.fromString("dc8dcc7b-033e-4157-a547-26cae4971aba");
    private final Player fakePlayer;

    public ConduitGameTestHelper(GameTestInfo info) {
        super(info);
        fakePlayer = FakePlayerFactory.get(info.getLevel(), new GameProfile(DEFAULT_FAKE_PLAYER_UUID, "[EnderIO]"));
    }

    public Holder<Conduit<?, ?>> getConduit(ResourceKey<Conduit<?, ?>> conduitType) {
        return getLevel().registryAccess()
                .registryOrThrow(EnderIOConduitsRegistries.Keys.CONDUIT)
                .getHolderOrThrow(conduitType);
    }

    public Player getPlayer() {
        return fakePlayer;
    }

    public void placeConduit(Holder<Conduit<?, ?>> conduit, int x, int y, int z) {
        var absolutePos = absolutePos(new BlockPos(x, y, z));
        var conduitItem = ConduitBlockItem.getStackFor(conduit, 1);
        if (conduitItem.getItem() instanceof BlockItem blockItem) {
            blockItem.place(new BlockPlaceContext(getLevel(), fakePlayer, InteractionHand.MAIN_HAND, conduitItem,
                    new BlockHitResult(absolutePos.getCenter(), Direction.DOWN, absolutePos, false)));
        }
    }

    public void fillAir(int startX, int startY, int startZ, int endX, int endY, int endZ) {
        fill(startX, startY, startZ, endX, endY, endZ, Blocks.AIR.defaultBlockState());
    }

    public void fill(int startX, int startY, int startZ, int endX, int endY, int endZ, BlockState blockState) {
        for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
                for (int z = startZ; z <= endZ; z++) {
                    setBlock(x, y, z, blockState);
                }
            }
        }
    }

    public void fillConduits(Holder<Conduit<?, ?>> conduit, int startX, int startY, int startZ, int endX, int endY,
            int endZ) {
        for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
                for (int z = startZ; z <= endZ; z++) {
                    placeConduit(conduit, x, y, z);
                }
            }
        }
    }

    @Nullable
    public ConduitBundleBlockEntity getConduitBundle(int x, int y, int z, boolean allowMissing) {
        if (getLevel()
                .getBlockEntity(absolutePos(new BlockPos(x, y, z))) instanceof ConduitBundleBlockEntity conduitBundle) {
            return conduitBundle;
        }

        if (!allowMissing) {
            throw new GameTestAssertException("No conduit bundle at " + x + ", " + y + ", " + z);
        }
        return null;
    }

    public void assertAllConduitBundlesMatch(int startX, int startY, int startZ, int endX, int endY, int endZ,
            boolean allowMissingBundles, Predicate<ConduitBundleBlockEntity> predicate, String errorMessage) {
        for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
                for (int z = startZ; z <= endZ; z++) {
                    var bundle = getConduitBundle(x, y, z, allowMissingBundles);
                    if (bundle != null && !predicate.test(bundle)) {
                        throw new GameTestAssertException(errorMessage + " at " + x + ", " + y + ", " + z);
                    }
                }
            }
        }
    }

    public void assertAllNetworksMatch(Holder<Conduit<?, ?>> conduit, int startX, int startY, int startZ, int endX,
            int endY, int endZ, boolean allowMissingBundles) {
        boolean foundFirstNetwork = false;
        IConduitNetwork network = null;

        for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
                for (int z = startZ; z <= endZ; z++) {
                    var bundle = getConduitBundle(x, y, z, allowMissingBundles);
                    if (bundle == null) {
                        continue;
                    }

                    if (!foundFirstNetwork) {
                        network = bundle.getConduitNode(conduit).getNetwork();
                        foundFirstNetwork = true;
                        continue;
                    }

                    if (network != bundle.getConduitNode(conduit).getNetwork()) {
                        throw new GameTestAssertException("Conduit node networks differ at " + x + ", " + y + ", " + z);
                    }
                }
            }
        }
    }

    public void assertAllNetworksDiffer(Holder<Conduit<?, ?>> conduit, int startX, int startY, int startZ, int endX,
            int endY, int endZ, boolean allowMissingBundles) {
        for (int x1 = startX; x1 <= endX; x1++) {
            for (int y1 = startY; y1 <= endY; y1++) {
                for (int z1 = startZ; z1 <= endZ; z1++) {
                    var bundleA = getConduitBundle(x1, y1, z1, allowMissingBundles);
                    if (bundleA == null || !bundleA.hasConduitStrict(conduit)) {
                        continue;
                    }

                    var nodeA = bundleA.getConduitNode(conduit);

                    for (int x2 = startX; x2 <= endX; x2++) {
                        for (int y2 = startY; y2 <= endY; y2++) {
                            for (int z2 = startZ; z2 <= endZ; z2++) {
                                if (x1 == x2 && y1 == y2 && z1 == z2) {
                                    continue;
                                }

                                var bundleB = getConduitBundle(x2, y2, z2, allowMissingBundles);
                                if (bundleB == null || !bundleB.hasConduitStrict(conduit)) {
                                    continue;
                                }

                                var nodeB = bundleB.getConduitNode(conduit);

                                if (nodeA.getNetwork() == nodeB.getNetwork()) {
                                    throw new GameTestAssertException("Conduit nodes have same network at " + x1 + ", "
                                            + y1 + ", " + z1 + " and " + x2 + ", " + y2 + ", " + z2);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void assertNetworksMatchBetween(Holder<Conduit<?, ?>> conduit, int x1, int y1, int z1, int x2, int y2,
            int z2) {
        var firstBundle = getConduitBundle(x1, y1, z1, false);
        var secondBundle = getConduitBundle(x2, y2, z2, false);

        var firstNode = firstBundle.getConduitNode(conduit);
        var secondNode = secondBundle.getConduitNode(conduit);

        if (firstNode.getNetwork() != secondNode.getNetwork()) {
            throw new GameTestAssertException("Conduit nodes have different networks at " + x1 + ", " + y1 + ", " + z1
                    + " and " + x2 + ", " + y2 + ", " + z2);
        }
    }

    public void assertNetworksDifferBetween(Holder<Conduit<?, ?>> conduit, int x1, int y1, int z1, int x2, int y2,
            int z2) {
        var firstBundle = getConduitBundle(x1, y1, z1, false);
        var secondBundle = getConduitBundle(x2, y2, z2, false);

        var firstNode = firstBundle.getConduitNode(conduit);
        var secondNode = secondBundle.getConduitNode(conduit);

        if (firstNode.getNetwork() == secondNode.getNetwork()) {
            throw new GameTestAssertException("Conduit nodes have same network at " + x1 + ", " + y1 + ", " + z1
                    + " and " + x2 + ", " + y2 + ", " + z2);
        }
    }
}
