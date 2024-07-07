//package com.enderio.conduits.common.integrations.ae2;
//
//import appeng.api.ids.AEConstants;
//import appeng.api.implementations.items.IFacadeItem;
//import appeng.api.networking.IInWorldGridNodeHost;
//import com.enderio.EnderIO;
//import com.enderio.api.conduit.Conduit;
//import com.enderio.api.conduit.ConduitDataSerializer;
//import com.enderio.api.conduit.ConduitType;
//import com.enderio.api.integration.Integration;
//import com.enderio.api.registry.EnderIORegistries;
//import net.minecraft.core.Direction;
//import net.minecraft.network.chat.Component;
//import net.minecraft.network.chat.MutableComponent;
//import net.minecraft.resources.ResourceKey;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.level.block.state.BlockState;
//import net.neoforged.bus.api.IEventBus;
//import net.neoforged.neoforge.capabilities.BlockCapability;
//import net.neoforged.neoforge.registries.DeferredRegister;
//import org.jetbrains.annotations.Nullable;
//
//import java.util.Optional;
//import java.util.function.Supplier;
//
//public class AE2Integration implements Integration {
//
//    public static final DeferredRegister<ConduitType<?>> CONDUIT_TYPES = DeferredRegister.create(EnderIORegistries.CONDUIT_TYPE, EnderIO.MODID);
//    public static final DeferredRegister<ConduitDataSerializer<?>> CONDUIT_DATA_SERIALIZERS = DeferredRegister.create(EnderIORegistries.CONDUIT_DATA_SERIALIZER, EnderIO.MODID);
//
//    public static final Supplier<ConduitType<AE2Conduit>> AE2_CONDUIT = CONDUIT_TYPES
//        .register("ae2", () -> ConduitType.builder(AE2Conduit.CODEC)
//            .exposeCapability(AE2Integration.IN_WORLD_GRID_NODE_HOST).build());
//
//    //TODO use capability when moved to api by ea2
//    public static BlockCapability<IInWorldGridNodeHost, @Nullable Direction> IN_WORLD_GRID_NODE_HOST = BlockCapability
//        .createSided(ResourceLocation.fromNamespaceAndPath(AEConstants.MOD_ID, "inworld_gridnode_host"), IInWorldGridNodeHost.class);
//
//    public static ResourceKey<Conduit<?>> NORMAL = ResourceKey.create(EnderIORegistries.Keys.CONDUIT, EnderIO.loc("me"));
//    public static ResourceKey<Conduit<?>> DENSE = ResourceKey.create(EnderIORegistries.Keys.CONDUIT, EnderIO.loc("dense_me"));
//
//    public static final Supplier<ConduitDataSerializer<AE2InWorldConduitNodeHost>> DATA_SERIALIZER =
//        CONDUIT_DATA_SERIALIZERS.register("me", () -> AE2InWorldConduitNodeHost.Serializer.INSTANCE);
//
//    private static final Component LANG_ME_CONDUIT = addTranslation("item", EnderIO.loc("conduit.me"), "ME Conduit");
//    private static final Component LANG_DENSE_ME_CONDUIT = addTranslation("item", EnderIO.loc("conduit.dense_me"), "Dense ME Conduit");
//
//    private static MutableComponent addTranslation(String prefix, ResourceLocation id, String translation) {
//        return EnderIO.getRegilite().addTranslation(prefix, id, translation);
//    }
//
//    @Override
//    public void addEventListener(IEventBus modEventBus, IEventBus forgeEventBus) {
//        CONDUIT_TYPES.register(modEventBus);
//        CONDUIT_DATA_SERIALIZERS.register(modEventBus);
//    }
//
//    public Optional<BlockState> getFacadeOf(ItemStack stack) {
//        if (stack.getItem() instanceof IFacadeItem facadeItem) {
//            return Optional.of(facadeItem.getTextureBlockState(stack));
//        }
//        return Optional.empty();
//    }
//
//    public BlockCapability<IInWorldGridNodeHost, Direction> getInWorldGridNodeHost() {
//        return IN_WORLD_GRID_NODE_HOST;
//    }
//}
