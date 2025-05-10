package com.enderio.conduits.server.tests;

import com.enderio.conduits.api.Conduit;
import com.enderio.conduits.api.bundle.ConduitBundle;
import com.enderio.conduits.api.network.IConduitNetwork;
import com.enderio.conduits.common.conduit.ConduitBlockItem;
import com.enderio.conduits.common.conduit.bundle.ConduitBundleBlockEntity;
import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestInfo;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.testframework.gametest.ExtendedGameTestHelper;

import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ConduitGameTestHelper extends ExtendedGameTestHelper {
    private static final UUID DEFAULT_FAKE_PLAYER_UUID = UUID.fromString("dc8dcc7b-033e-4157-a547-26cae4971aba");
    private final Player fakePlayer;

    public ConduitGameTestHelper(GameTestInfo info) {
        super(info);
        fakePlayer = new FakePlayer(info.getLevel(), new GameProfile(DEFAULT_FAKE_PLAYER_UUID, "[EnderIO]"));
    }

    public Player getPlayer() {
        return fakePlayer;
    }

    public void placeConduit(Holder<Conduit<?, ?>> conduit, int x, int y, int z) {
        var absolutePos = absolutePos(new BlockPos(x, y, z));
        var conduitItem = ConduitBlockItem.getStackFor(conduit, 1);
        if (conduitItem.getItem() instanceof BlockItem blockItem) {
            blockItem.place(new BlockPlaceContext(getLevel(), fakePlayer, InteractionHand.MAIN_HAND, conduitItem, new BlockHitResult(absolutePos.getCenter(), Direction.DOWN, absolutePos, false)));
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

    public void fillConduits(Holder<Conduit<?, ?>> conduit, int startX, int startY, int startZ, int endX, int endY, int endZ) {
        for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
                for (int z = startZ; z <= endZ; z++) {
                    placeConduit(conduit, x, y, z);
                }
            }
        }
    }

    public ConduitBundleBlockEntity getConduitBundle(int x, int y, int z) {
        if (getLevel().getBlockEntity(absolutePos(new BlockPos(x, y, z))) instanceof ConduitBundleBlockEntity conduitBundle) {
            return conduitBundle;
        }

        throw new GameTestAssertException("No conduit bundle at " + x + ", " + y + ", " + z);
    }

    public void assertAllConduitBundlesMatch(int startX, int startY, int startZ, int endX, int endY, int endZ, Predicate<ConduitBundleBlockEntity> predicate, String errorMessage) {
        for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
                for (int z = startZ; z <= endZ; z++) {
                    if (!predicate.test(getConduitBundle(x, y, z))) {
                        throw new GameTestAssertException(errorMessage + " at " + x + ", " + y + ", " + z);
                    }
                }
            }
        }
    }

    public void assertAllConduitNodesSameNetwork(Holder<Conduit<?, ?>> conduit, int startX, int startY, int startZ, int endX, int endY, int endZ) {
        boolean foundFirstNetwork = false;
        IConduitNetwork network = null;

        for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
                for (int z = startZ; z <= endZ; z++) {
                    var bundle = getConduitBundle(x, y, z);
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

    public void assertConduitNodesDifferentNetwork(Holder<Conduit<?, ?>> conduit, int x1, int y1, int z1, int x2, int y2, int z2) {
        var firstBundle = getConduitBundle(x1, y1, z1);
        var secondBundle = getConduitBundle(x2, y2, z2);

        var firstNode = firstBundle.getConduitNode(conduit);
        var secondNode = secondBundle.getConduitNode(conduit);

        if (firstNode.getNetwork() == secondNode.getNetwork()) {
            throw new GameTestAssertException("Conduit nodes have same network at " + x1 + ", " + y1 + ", " + z1 + " and " + x2 + ", " + y2 + ", " + z2);
        }
    }
}
