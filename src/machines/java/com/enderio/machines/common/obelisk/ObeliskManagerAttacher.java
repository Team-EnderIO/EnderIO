package com.enderio.machines.common.obelisk;

import com.enderio.EnderIO;
import com.enderio.machines.common.blockentity.AversionObeliskBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Mod.EventBusSubscriber(modid = EnderIO.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ObeliskManagerAttacher {
    public static final Capability<IObeliskManagerCapability<AversionObeliskBlockEntity>> AVERSION_OBELISK_MANAGER_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<Level> event) {
        if (!(event.getObject() instanceof ServerLevel)) return;

        AversionObeliskManager aversionObeliskManagerBackend = new AversionObeliskManager();
        LazyOptional<IObeliskManagerCapability<AversionObeliskBlockEntity>> aversionObeliskManagerStorage = LazyOptional.of(() -> aversionObeliskManagerBackend);

        ICapabilityProvider aversionObeliskManagerCapabilityProvider = new ICapabilityProvider() {
            @Override
            public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction direction) {
                return AVERSION_OBELISK_MANAGER_CAPABILITY.orEmpty(cap, aversionObeliskManagerStorage);
            }
        };

        event.addCapability(new ResourceLocation(EnderIO.MODID, "aversion_obelisk_manager_capability"), aversionObeliskManagerCapabilityProvider);
    }
}
