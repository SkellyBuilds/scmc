package com.skellybuilds.SCMC.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Accessor mixin to provide access to the underlying {@link GameProfile} during the server
 * login handling.
 */
@Mixin(ServerLoginNetworkHandler.class)
public interface ServerLoginNetworkHandlerAccessor {

    @Accessor("profile")
    GameProfile getGameProfile();

}