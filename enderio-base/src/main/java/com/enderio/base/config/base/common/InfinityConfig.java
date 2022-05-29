package com.enderio.base.config.base.common;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.List;

public class InfinityConfig {
    public final ForgeConfigSpec.ConfigValue<Boolean> MAKES_SOUND;
    public final ForgeConfigSpec.ConfigValue<Integer> FIRE_MIN_AGE;
    public final ForgeConfigSpec.ConfigValue<Boolean> ENABLE_IN_ALL_DIMENSIONS;
    public final ForgeConfigSpec.ConfigValue<List<String>> DIMENSION_WHITELIST;
    public final ForgeConfigSpec.ConfigValue<List<String>> INFINITE_BLOCKS;

    public InfinityConfig(ForgeConfigSpec.Builder builder) {
        builder.push("grainsOfInfinity");

        MAKES_SOUND = builder.comment("Should it make a sound when Grains of Infinity drops from a fire?").define("makesSound", true);
        FIRE_MIN_AGE = builder.comment("How old (in ticks) does a fire have to be to be able to spawn Infinity Powder?").defineInRange("fireMinAge", 260, 1, 1000);
        ENABLE_IN_ALL_DIMENSIONS = builder.comment("Whether infinity fire crafting is enabled in all dimensions. If not, dimensions must be on the whitelist.").define("enableInAllDimensions", false);
        DIMENSION_WHITELIST = builder.comment("A list of dimensions that fire crafting can occur in.").define("dimensionWhitelist", new ArrayList<>(List.of("minecraft:overworld")));
        INFINITE_BLOCKS = builder.comment("A list of blocks that infinity fires can be started on.").define("infiniteBlocks", new ArrayList<>(List.of("minecraft:bedrock")));

        builder.pop();
    }
}
