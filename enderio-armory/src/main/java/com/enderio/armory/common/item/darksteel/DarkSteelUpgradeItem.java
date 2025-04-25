package com.enderio.armory.common.item.darksteel;

import com.enderio.armory.api.capability.IDarkSteelUpgrade;
import com.enderio.armory.common.lang.ArmoryLang;
import com.enderio.core.client.item.AdvancedTooltipProvider;
import com.enderio.core.common.util.TooltipUtil;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.Nullable;

public class DarkSteelUpgradeItem extends Item implements AdvancedTooltipProvider {

    private final ModConfigSpec.ConfigValue<Integer> levelsRequired;

    private final Supplier<? extends IDarkSteelUpgrade> upgrade;

    public DarkSteelUpgradeItem(Properties pProperties, ModConfigSpec.ConfigValue<Integer> levelsRequired,
            Supplier<? extends IDarkSteelUpgrade> upgrade) {
        super(pProperties.stacksTo(1));
        this.levelsRequired = levelsRequired;
        this.upgrade = upgrade;
    }

    public Supplier<? extends IDarkSteelUpgrade> getUpgrade() {
        return upgrade;
    }

    public ModConfigSpec.ConfigValue<Integer> getLevelsRequired() {
        return levelsRequired;
    }

    @Override
    public void addDetailedTooltips(ItemStack itemStack, @Nullable Player player, List<Component> tooltips) {
        Collection<Component> desc = upgrade.get().getDescription();
        for (Component component : desc) {
            tooltips.add(component.copy().withStyle(ChatFormatting.GRAY));
        }
        tooltips.add(TooltipUtil.withArgs(ArmoryLang.DS_UPGRADE_XP_COST, levelsRequired.get())
                .withStyle(ChatFormatting.DARK_PURPLE));
    }
}
