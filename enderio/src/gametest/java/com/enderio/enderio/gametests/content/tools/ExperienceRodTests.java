package com.enderio.enderio.gametests.content.tools;

import com.enderio.enderio.foundation.util.ExperienceUtil;
import com.enderio.enderio.gametests.util.EnderGameTestHelper;
import com.enderio.enderio.init.EIOBlocks;
import com.enderio.enderio.init.EIOFluids;
import com.enderio.enderio.init.EIOItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.testframework.DynamicTest;
import net.neoforged.testframework.annotation.ForEachTest;
import net.neoforged.testframework.annotation.TestHolder;
import net.neoforged.testframework.gametest.StructureTemplateBuilder;

@ForEachTest(groups = "content.tools.experience_rod")
public class ExperienceRodTests {
    @GameTest
    @TestHolder(description = "Taking XP from a tank with the experience rod must give the player everything that was removed.")
    public static void experienceRodConservesXp(final DynamicTest test) {
        test.registerGameTestTemplate(() -> StructureTemplateBuilder.withSize(1, 1, 1)
            .set(0, 0, 0, EIOBlocks.FLUID_TANK.get().defaultBlockState()));

        test.onGameTest(EnderGameTestHelper.class, helper -> {
            var player = helper.makeMockPlayer(GameType.SURVIVAL);
            var xpJuice = EIOFluids.XP_JUICE.source().get();

            // Exactly the amount the rod asks for in one use: the next level's worth of XP.
            int xpStored = player.getXpNeededForNextLevel();
            int fluidStored = xpStored * ExperienceUtil.EXP_TO_FLUID;

            helper.startSequence()
                .thenExecute(() -> helper.fillContainer(0, 1, 0, new FluidStack(xpJuice, fluidStored)))
                .thenExecute(() -> {
                    var rod = new ItemStack(EIOItems.EXPERIENCE_ROD.get());
                    var pos = helper.absolutePos(new BlockPos(0, 1, 0));
                    var hit = new BlockHitResult(pos.getCenter(), Direction.UP, pos, false);
                    rod.getItem().onItemUseFirst(rod, new UseOnContext(helper.getLevel(), player, InteractionHand.MAIN_HAND, rod, hit));
                })
                .thenExecute(() -> {
                    long fluidLeft = helper.getAmountInHandler(0, 1, 0, xpJuice);
                    long xpRemoved = (fluidStored - fluidLeft) / ExperienceUtil.EXP_TO_FLUID;

                    if (xpRemoved <= 0) {
                        throw new GameTestAssertException("The experience rod did not take any XP from the tank");
                    }

                    if (player.totalExperience != xpRemoved) {
                        throw new GameTestAssertException(xpRemoved + " XP was removed from the tank but the player received "
                            + player.totalExperience);
                    }
                })
                .thenSucceed();
        });
    }
}
