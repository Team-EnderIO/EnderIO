package com.enderio.conduits.tests;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.testframework.conf.ClientConfiguration;
import net.neoforged.testframework.conf.Feature;
import net.neoforged.testframework.conf.FrameworkConfiguration;
import net.neoforged.testframework.impl.MutableTestFramework;
import org.lwjgl.glfw.GLFW;

@Mod(EnderIOConduitsTests.MOD_ID)
public class EnderIOConduitsTests {

    public static final String MOD_ID = "enderio_conduits_tests";

    public EnderIOConduitsTests(IEventBus eventBus, ModContainer container) {
        final MutableTestFramework framework = FrameworkConfiguration
                .builder(ResourceLocation.fromNamespaceAndPath(MOD_ID, "tests"))
                .clientConfiguration(() -> ClientConfiguration.builder()
                        .toggleOverlayKey(GLFW.GLFW_KEY_O)
                        .openManagerKey(GLFW.GLFW_KEY_M)
                        .build())
                .enable(Feature.CLIENT_SYNC, Feature.TEST_STORE)
                .build()
                .create();

        framework.init(eventBus, container);

        NeoForge.EVENT_BUS.addListener((final RegisterCommandsEvent event) -> {
            final LiteralArgumentBuilder<CommandSourceStack> node = Commands.literal("tests");
            framework.registerCommands(node);
            event.getDispatcher().register(node);
        });
    }
}
