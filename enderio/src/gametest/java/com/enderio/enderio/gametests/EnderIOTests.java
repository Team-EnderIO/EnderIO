package com.enderio.enderio.gametests;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.neoforged.testframework.conf.ClientConfiguration;
import net.neoforged.testframework.conf.Feature;
import net.neoforged.testframework.conf.FrameworkConfiguration;
import net.neoforged.testframework.impl.MutableTestFramework;
import org.lwjgl.glfw.GLFW;

@Mod(EnderIOTests.MOD_ID)
public class EnderIOTests {

    public static final String MOD_ID = "enderio_tests";

    public EnderIOTests(IEventBus eventBus, ModContainer container) {
        final MutableTestFramework framework = FrameworkConfiguration
                .builder(new ResourceLocation(MOD_ID, "tests"))
                .clientConfiguration(() -> ClientConfiguration.builder()
                        .toggleOverlayKey(GLFW.GLFW_KEY_O)
                        .openManagerKey(GLFW.GLFW_KEY_M)
                        .build())
                .enable(Feature.CLIENT_SYNC, Feature.TEST_STORE)
                .build()
                .create();

        framework.init(eventBus, container);

        MinecraftForge.EVENT_BUS.addListener((final RegisterCommandsEvent event) -> {
            final LiteralArgumentBuilder<CommandSourceStack> node = Commands.literal("tests");
            framework.registerCommands(node);
            event.getDispatcher().register(node);
        });
    }
}
