package com.enderio.enderio.init;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.foundation.tag.EIOTags;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.EnumMap;
import java.util.List;

public class EIOArmorMaterials {
    private static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS = DeferredRegister.create(Registries.ARMOR_MATERIAL, EnderIO.MOD_ID);

    public static final Holder<ArmorMaterial> DARK_STEEL =
        ARMOR_MATERIALS.register("dark_steel", () -> new ArmorMaterial(
            Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                map.put(ArmorItem.Type.BOOTS, 2);
                map.put(ArmorItem.Type.LEGGINGS, 5);
                map.put(ArmorItem.Type.CHESTPLATE, 6);
                map.put(ArmorItem.Type.HELMET, 2);
                map.put(ArmorItem.Type.BODY, 6);
            }),
            30,
            SoundEvents.ARMOR_EQUIP_GENERIC,
            () -> Ingredient.of(EIOTags.Items.INGOTS_DARK_STEEL),
            List.of(
                new ArmorMaterial.Layer(
                    EnderIO.rl("dark_steel")
                )
            ),
            1F,
            0
        ));

    public static final Holder<ArmorMaterial> END_STEEL =
        ARMOR_MATERIALS.register("end_steel", () -> new ArmorMaterial(
            Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                map.put(ArmorItem.Type.BOOTS, 4);
                map.put(ArmorItem.Type.LEGGINGS, 7);
                map.put(ArmorItem.Type.CHESTPLATE, 10);
                map.put(ArmorItem.Type.HELMET, 4);
                map.put(ArmorItem.Type.BODY, 10);
            }),
            30,
            SoundEvents.ARMOR_EQUIP_GENERIC,
            () -> Ingredient.of(EIOTags.Items.INGOTS_END_STEEL),
            List.of(
                new ArmorMaterial.Layer(
                    EnderIO.rl("end_steel")
                )
            ),
            3F,
            0
        ));

    public static void register(IEventBus bus) {
        ARMOR_MATERIALS.register(bus);
    }
}
