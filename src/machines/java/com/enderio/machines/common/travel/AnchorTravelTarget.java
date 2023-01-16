package com.enderio.machines.common.travel;

import com.enderio.EnderIO;
import com.enderio.api.travel.ITravelTarget;
import com.enderio.base.common.config.BaseConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class AnchorTravelTarget implements ITravelTarget {

    public static final ResourceLocation SERIALIZED_NAME = EnderIO.loc("travel_anchor");

    private final BlockPos pos;
    private String name;
    @Nullable
    private Item icon;

    public AnchorTravelTarget(BlockPos pos, String name, @Nullable Item icon) {
        this.pos = pos;
        this.name = name;
        this.icon = icon;
    }

    public AnchorTravelTarget(CompoundTag tag) {
        pos = NbtUtils.readBlockPos(tag.getCompound("pos"));
        name = tag.getString("name");
        String iconName = tag.getString("icon");
        icon = iconName.equals("") ? null : ForgeRegistries.ITEMS.getValue(new ResourceLocation(iconName));
    }

    @Override
    public CompoundTag save() {
        CompoundTag nbt = new CompoundTag();
        nbt.put("pos", NbtUtils.writeBlockPos(pos));
        nbt.putString("name", name);
        if (icon != null)
            nbt.putString("icon", String.valueOf(ForgeRegistries.ITEMS.getKey(icon)));
        return nbt;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof AnchorTravelTarget other))
            return false;
        return pos.equals(other.pos)
            && name.equals(other.name)
            && Objects.equals(icon, other.icon);
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

    @Nullable
    public Item getIcon() {
        return icon;
    }

    public void setIcon(@Nullable Item icon) {
        this.icon = icon;
    }

    @Override
    public int getItem2BlockRange() {
        return BaseConfig.COMMON.ITEMS.TRAVELLING_TO_BLOCK_RANGE.get();
    }

    @Override
    public ResourceLocation getSerializationName() {
        return SERIALIZED_NAME;
    }
}
