package com.enderio.api.capability;

import com.mojang.authlib.GameProfile;

/**
 * A capability holding a game profile declaring the owner of something.
 */
public interface IOwner {

    /**
     * Get the owner's profile.
     */
    GameProfile getProfile();

    /**
     * Set the owner's profile.
     */
    default void setProfile(GameProfile profile) {
        setProfile(profile, prof -> {});
    }

    /**
     * Set the owner's profile.
     * Add a callback used once the profile has been set, allows you to update the client.
     */
    void setProfile(GameProfile profile, ProfileSetCallback callback);

    interface ProfileSetCallback {
        void profileSet(GameProfile profile);
    }

}
