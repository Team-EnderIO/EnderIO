package com.enderio.base.common.init;

import com.enderio.EnderIO;
import com.enderio.base.common.loot.CopyAttachment;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class EIOLootFunctions {
    private static final DeferredRegister<LootItemFunctionType> LOOT_FUNCTIONS = DeferredRegister.create(Registries.LOOT_FUNCTION_TYPE, EnderIO.MODID);

    public static final Supplier<LootItemFunctionType> COPY_ATTACHMENT = LOOT_FUNCTIONS.register("nbt", () -> new LootItemFunctionType(CopyAttachment.CODEC));

    public static void register(IEventBus bus) {
        LOOT_FUNCTIONS.register(bus);
    }
}
