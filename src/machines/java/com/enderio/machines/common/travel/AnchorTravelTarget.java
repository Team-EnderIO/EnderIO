package com.enderio.machines.common.travel;

import com.enderio.EnderIO;
import com.enderio.api.travel.ITravelTarget;
import com.enderio.base.common.config.BaseConfig;
import com.enderio.core.CoreNBTKeys;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;

public class AnchorTravelTarget implements ITravelTarget {

    public static final ResourceLocation SERIALIZED_NAME = EnderIO.loc("travel_anchor");

    private final BlockPos pos;
    private String name;
    private Item icon;
    private boolean visible;

    public AnchorTravelTarget(BlockPos pos, String name, Item icon, boolean visible) {
        this.pos = pos;
        this.name = name;
        this.icon = icon;
        this.visible = visible;
    }

    public AnchorTravelTarget(CompoundTag tag) {
        pos = NbtUtils.readBlockPos(tag.getCompound(CoreNBTKeys.BLOCK_POS));
        name = tag.getString(CoreNBTKeys.ANCHOR_NAME);
        String iconName = tag.getString(CoreNBTKeys.ANCHOR_ICON);
        icon = iconName.equals("") ? Items.AIR : ForgeRegistries.ITEMS.getValue(new ResourceLocation(iconName));
        visible = tag.getBoolean(CoreNBTKeys.ANCHOR_VISIBILITY);
    }

    @Override
    public CompoundTag save() {
        CompoundTag nbt = new CompoundTag();
        nbt.put(CoreNBTKeys.BLOCK_POS, NbtUtils.writeBlockPos(pos));
        nbt.putString(CoreNBTKeys.ANCHOR_NAME, name);
        nbt.putString(CoreNBTKeys.ANCHOR_ICON, String.valueOf(ForgeRegistries.ITEMS.getKey(icon)));
        nbt.putBoolean(CoreNBTKeys.ANCHOR_VISIBILITY, visible);
        return nbt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof AnchorTravelTarget other)) {
            return false;
        }

        return pos.equals(other.pos) && name.equals(other.name) && visible == (other.visible) && Objects.equals(icon, other.icon);

    }

    @Override
    public int hashCode() {
        return Objects.hash(pos, name, icon);
    }

    @Override
    public BlockPos getPos() {
        return pos;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Item getIcon() {
        return icon;
    }

    public void setIcon(Item icon) {
        this.icon = icon;
    }

    public boolean getVisibility() {
        return visible;
    }

    public void setVisibility(boolean visible) {
        this.visible = visible;
    }

    @Override
    public boolean canTravelTo() {
        return getVisibility();
    }

    @Override
    public boolean canTeleportTo() {
        return getVisibility();
    }

    @Override
    public boolean canJumpTo() {
        // TODO: Protected & Private Anchors
        return true;
    }

    @Override
    public int getItem2BlockRange() {
        return BaseConfig.COMMON.ITEMS.TRAVELLING_TO_BLOCK_RANGE.get();
    }

    @Override
    public int getBlock2BlockRange() {
        return BaseConfig.COMMON.ITEMS.TRAVELLING_BLOCK_TO_BLOCK_RANGE.get();
    }

    @Override
    public ResourceLocation getSerializationName() {
        return SERIALIZED_NAME;
    }
}
