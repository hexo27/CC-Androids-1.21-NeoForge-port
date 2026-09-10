package com.thunderbear06.entity.player;

import com.mojang.authlib.GameProfile;
import com.thunderbear06.ai.AndroidBrain;
import dan200.computercraft.shared.platform.PlatformHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class AndroidPlayer {
    private static final Logger LOGGER = LoggerFactory.getLogger(AndroidPlayer.class);
    private static final GameProfile DEFAULT_PROFILE = new GameProfile(UUID.fromString("0d0c4ca0-4ff1-11e4-916c-0800200c9a66"), "[ComputerCraft]");
    private final ServerPlayer player;

    public AndroidPlayer(ServerPlayer player) {
        this.player = player;
    }

    private static AndroidPlayer create(AndroidBrain brain) {
        ServerLevel world = (ServerLevel) brain.getAndroid().level();
        GameProfile profile = brain.getOwningPlayerProfile();
        AndroidPlayer player = new AndroidPlayer(PlatformHelper.get().createFakePlayer(world, getProfile(profile != null ? profile : DEFAULT_PROFILE)));
        player.setState(brain);
        return player;
    }

    @SuppressWarnings(value = "deprecation")
    public static AndroidPlayer get(AndroidBrain brain) {
        AndroidPlayer player = brain.fakePlayer;
        if (player != null && player.player.getGameProfile() == getProfile(brain.getOwningPlayerProfile()) && player.player.level() == brain.getAndroid().level()) {
            player.setState(brain);
        } else {
            player = brain.fakePlayer = create(brain);
        }

        return player;

    }

    public ServerPlayer player() {
        return this.player;
    }

    private void setState(AndroidBrain brain) {
        if (this.player.containerMenu != this.player.inventoryMenu) {
            LOGGER.warn("Android has open container ({})", this.player.containerMenu);
            this.player.closeContainer();
        }

        setPosition(brain);
        loadHand(brain.getAndroid().getMainHandItem(), InteractionHand.MAIN_HAND);
        loadHand(brain.getAndroid().getOffhandItem(), InteractionHand.OFF_HAND);
    }

    private void setRotation(Vec3 rotation) {
        this.player.setYRot((float) rotation.y);
        this.player.setXRot((float) rotation.x);
    }

    public void setPosition(AndroidBrain brain) {
        this.setRotation(brain.getAndroid().getLookAngle());

        Vec3 pos = brain.getAndroid().position();

        this.player.setPos(pos);

        this.player.xo = pos.x;
        this.player.yo = pos.y;
        this.player.zo = pos.z;

        this.player.xRotO = this.player.getXRot();
        this.player.yHeadRot = this.player.yHeadRotO = this.player.yRotO = this.player.getYRot();
    }

    private static GameProfile getProfile(@Nullable GameProfile profile) {
        return profile != null && profile.getId() != null && !profile.getName().isEmpty() ? profile : DEFAULT_PROFILE;
    }

    public void loadHand(ItemStack stack, InteractionHand hand) {
        this.player.setItemInHand(hand, stack);
    }
}
