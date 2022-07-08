package com.enderio.decoration.common.util;

import com.enderio.decoration.client.model.painted.PaintedModel;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class PaintUtils {
    public static Block getBlockFromRL(String rl) {
        //Not Nullable, as ForgeRegistries usually return a default
        return ForgeRegistries.BLOCKS.getValue(new ResourceLocation(rl));
    }

    @Nullable
    public static Block getPaint(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("BlockEntityTag")) {
            CompoundTag blockEntityTag = tag.getCompound("BlockEntityTag");
            if (blockEntityTag.contains("paint")) {
                return getBlockFromRL(blockEntityTag.getString("paint"));
            }
        }
        return null;
    }
}
