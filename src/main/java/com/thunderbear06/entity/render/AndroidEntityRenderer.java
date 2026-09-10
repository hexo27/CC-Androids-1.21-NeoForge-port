package com.thunderbear06.entity.render;

import com.thunderbear06.CCAndroids;
import com.thunderbear06.entity.android.AndroidEntity;
import com.thunderbear06.entity.android.CommandAndroidEntity;
import dan200.computercraft.shared.computer.core.ComputerFamily;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

public class AndroidEntityRenderer extends HumanoidMobRenderer<AndroidEntity, PlayerModel<AndroidEntity>> {
    private final ResourceLocation androidNormal = ResourceLocation.fromNamespaceAndPath(CCAndroids.MOD_ID, "textures/entity/android_normal.png");
    private final ResourceLocation androidAdvanced = ResourceLocation.fromNamespaceAndPath(CCAndroids.MOD_ID, "textures/entity/android_advanced.png");
    private final ResourceLocation androidCommand = ResourceLocation.fromNamespaceAndPath(CCAndroids.MOD_ID, "textures/entity/android_command.png");
    private final ResourceLocation core_emissive = ResourceLocation.fromNamespaceAndPath(CCAndroids.MOD_ID, "textures/entity/emissive/core.png");

    private final ResourceLocation face_anger = ResourceLocation.fromNamespaceAndPath(CCAndroids.MOD_ID, "textures/entity/emissive/face/anger.png");
    private final ResourceLocation face_annoyed = ResourceLocation.fromNamespaceAndPath(CCAndroids.MOD_ID, "textures/entity/emissive/face/annoyed.png");
    private final ResourceLocation face_command = ResourceLocation.fromNamespaceAndPath(CCAndroids.MOD_ID, "textures/entity/emissive/face/command.png");
    private final ResourceLocation face_happy = ResourceLocation.fromNamespaceAndPath(CCAndroids.MOD_ID, "textures/entity/emissive/face/happy.png");
    private final ResourceLocation face_normal = ResourceLocation.fromNamespaceAndPath(CCAndroids.MOD_ID, "textures/entity/emissive/face/normal.png");
    private final ResourceLocation face_sad = ResourceLocation.fromNamespaceAndPath(CCAndroids.MOD_ID, "textures/entity/emissive/face/sad.png");
    private final ResourceLocation face_woozy = ResourceLocation.fromNamespaceAndPath(CCAndroids.MOD_ID, "textures/entity/emissive/face/woozy.png");

    public AndroidEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new PlayerModel<>(context.bakeLayer(ModelLayers.PLAYER), false), 0.5f);

        this.addLayer(new AndroidEmissiveRenderer(this) {
            @Override
            public RenderType getEyesTexture(AndroidEntity entity) {
                if (entity.hasVariant())
                    return RenderType.eyes(getVariantTexture(entity.getVariant(), true));

                return RenderType.eyes(core_emissive);
            }
        });

        this.addLayer(new AndroidEmissiveRenderer(this) {
            @Override
            public RenderType getEyesTexture(AndroidEntity entity) {
                if (entity.hasVariant())
                    return null;

                ResourceLocation face = switch (entity.getFace()) {
                    case 0 -> {
                        if (entity instanceof CommandAndroidEntity)
                            yield face_command;
                        else
                            yield face_normal;
                    }
                    case 1 -> face_anger;
                    case 2 -> face_annoyed;
                    case 3 -> face_happy;
                    case 4 -> face_sad;
                    case 5 -> face_woozy;
                    default -> null;
                };
                return face == null ? null : RenderType.eyes(face);
            }

            @Override
            public Vec3 getColor(AndroidEntity entity) {
                return switch (entity.getComputer().getFamily()) {
                    case NORMAL -> new Vec3(1, 1, 1);
                    case ADVANCED -> new Vec3(1.5, 1, 1);
                    case COMMAND -> new Vec3(1, 0, 0);
                };
            }
        });
    }

    @Override
    public ResourceLocation getTextureLocation(AndroidEntity entity) {
        if (entity.hasVariant())
            return getVariantTexture(entity.getVariant(), false);

        ComputerFamily family = entity.getComputer().family;
        if (family == ComputerFamily.ADVANCED)
            return androidAdvanced;
        if (family == ComputerFamily.COMMAND)
            return androidCommand;
        return androidNormal;
    }

    private ResourceLocation getVariantTexture(byte b, boolean emissive) {
        String path = emissive ? "textures/entity/emissive/variant/" : "textures/entity/variant/";

        String name = switch (b) {
            case 1 -> "android_kaylon.png";
            case 2 -> "android_pinky.png";
            default -> throw new IllegalArgumentException();
        };

        return ResourceLocation.fromNamespaceAndPath(CCAndroids.MOD_ID, path + name);
    }
}
