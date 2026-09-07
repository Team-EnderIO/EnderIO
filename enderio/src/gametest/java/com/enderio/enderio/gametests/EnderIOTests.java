package com.enderio.enderio.gametests;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.serialization.Codec;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.testframework.conf.ClientConfiguration;
import net.neoforged.testframework.conf.Feature;
import net.neoforged.testframework.conf.FrameworkConfiguration;
import net.neoforged.testframework.impl.MutableTestFramework;
import org.lwjgl.glfw.GLFW;

import java.util.function.UnaryOperator;

@Mod(EnderIOTests.MOD_ID)
public class EnderIOTests {

    public static final String MOD_ID = "enderio_tests";

    private static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, MOD_ID);

    public static final DataComponentType<Integer> TEST_NUMBER = registerDataComponentType("test_number",
        builder -> builder.persistent(Codec.INT));

    public EnderIOTests(IEventBus eventBus, ModContainer container) {
        final MutableTestFramework framework = FrameworkConfiguration
                .builder(ResourceLocation.fromNamespaceAndPath(MOD_ID, "tests"))
                .clientConfiguration(() -> ClientConfiguration.builder()
                        .toggleOverlayKey(GLFW.GLFW_KEY_O)
                        .openManagerKey(GLFW.GLFW_KEY_M)
                        .build())
                .enable(Feature.CLIENT_SYNC, Feature.TEST_STORE)
                .build()
                .create();

        DATA_COMPONENT_TYPES.register(eventBus);

        framework.init(eventBus, container);

        NeoForge.EVENT_BUS.addListener((final RegisterCommandsEvent event) -> {
            final LiteralArgumentBuilder<CommandSourceStack> node = Commands.literal("tests");
            framework.registerCommands(node);
            event.getDispatcher().register(node);
        });
    }

    private static <T> DataComponentType<T> registerDataComponentType(String name, UnaryOperator<DataComponentType.Builder<T>> builder) {
        var componentType = builder.apply(DataComponentType.builder()).build();
        DATA_COMPONENT_TYPES.register(name, () -> componentType);
        return componentType;
    }
}
