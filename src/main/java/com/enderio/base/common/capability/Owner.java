package com.enderio.base.common.capability;

import com.enderio.api.capability.IOwner;
import com.enderio.base.EIONBTKeys;
import com.mojang.authlib.GameProfile;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.Nullable;

public class Owner implements IOwner, INBTSerializable<CompoundTag> {

    @Nullable
    private GameProfile profile;

    @Nullable
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
            tag.put(EIONBTKeys.OWNER, ownerTag);
        }
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if (nbt.contains(EIONBTKeys.OWNER)) {
            profile = NbtUtils.readGameProfile(nbt.getCompound(EIONBTKeys.OWNER));
        }
    }
}
