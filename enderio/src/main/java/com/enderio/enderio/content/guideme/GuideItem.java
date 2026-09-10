package com.enderio.enderio.content.guideme;

import com.enderio.enderio.EnderIO;
import guideme.GuidesCommon;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class GuideItem extends Item {
    private static final ResourceLocation GUIDE = EnderIO.rl("guide");

    public GuideItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if (level.isClientSide) {
            GuidesCommon.openGuide(player, GUIDE);
        }

        return new InteractionResultHolder<>(InteractionResult.FAIL, player.getItemInHand(usedHand));
    }
}
