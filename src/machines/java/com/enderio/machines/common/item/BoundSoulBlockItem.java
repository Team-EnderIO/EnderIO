package com.enderio.machines.common.item;

import com.enderio.base.common.init.EIOAttachments;
import com.enderio.base.common.init.EIOCapabilities;
import com.enderio.base.common.init.EIOItems;
import com.enderio.base.common.tag.EIOTags;
import com.enderio.core.client.item.IAdvancedTooltipProvider;
import com.enderio.core.common.util.EntityUtil;
import com.enderio.core.common.util.TooltipUtil;
import com.enderio.machines.common.lang.MachineLang;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BoundSoulBlockItem extends BlockItem implements IAdvancedTooltipProvider {

    public BoundSoulBlockItem(Block pBlock, Properties pProperties) {
        super(pBlock, pProperties);
    }

    @Override
    public void addCommonTooltips(ItemStack itemStack, @Nullable Player player, List<Component> tooltips) {
        // TODO: NEO-PORT: Instead of having an item class, have a tooltip event for anything with stored entity data?

        if (itemStack.is(EIOTags.Items.STORED_ENTITY)) {
            var storedEntityData = itemStack.getData(EIOAttachments.STORED_ENTITY);
            if (storedEntityData.getEntityType().isPresent()) {
                tooltips.add(TooltipUtil.style(Component.translatable(EntityUtil.getEntityDescriptionId(storedEntityData.getEntityType().get()))));
                return;
            }
        }

        tooltips.add(TooltipUtil.style(MachineLang.TOOLTIP_NO_SOULBOUND));
    }
}
