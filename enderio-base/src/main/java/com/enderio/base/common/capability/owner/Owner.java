package com.enderio.base.common.capability.owner;

import com.mojang.authlib.GameProfile;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.entity.SkullBlockEntity;

public class Owner implements IOwner {
    private GameProfile profile;

    @Override
    public GameProfile getProfile() {
        return profile;
    }

    @Override
    public void setProfile(GameProfile profile, ProfileSetCallback callback) {
        synchronized (this) {
            this.profile = profile;
        }

        // Perform update.
        SkullBlockEntity.updateGameprofile(this.profile, newProfile -> {
            this.profile = newProfile;
            callback.profileSet(this.profile);
        });
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        if (profile != null) {
            CompoundTag ownerTag = new CompoundTag();
            NbtUtils.writeGameProfile(ownerTag, profile);
            tag.put("Owner", ownerTag);
        }
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if (nbt.contains("Owner")) {
            profile = NbtUtils.readGameProfile(nbt.getCompound("Owner"));
        }
    }
}
