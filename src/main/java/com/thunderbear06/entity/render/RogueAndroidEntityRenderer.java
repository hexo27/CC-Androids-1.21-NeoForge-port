package com.thunderbear06.entity.render;

import com.thunderbear06.CCAndroids;
import com.thunderbear06.entity.android.RogueDroidEntity;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;

public class RogueAndroidEntityRenderer extends HumanoidMobRenderer<RogueDroidEntity, PlayerModel<RogueDroidEntity>> {
    private final ResourceLocation androidRogue = ResourceLocation.fromNamespaceAndPath(CCAndroids.MOD_ID, "textures/entity/android_rogue.png");
    private final ResourceLocation androidRogueE = ResourceLocation.fromNamespaceAndPath(CCAndroids.MOD_ID, "textures/entity/emissive/android_rogue_e.png");

    public RogueAndroidEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new PlayerModel<>(ctx.bakeLayer(ModelLayers.PLAYER), false), 0.5f);
        this.addLayer(new EyesLayer<>(this) {
            @Override
            public RenderType renderType() {
                return RenderType.eyes(androidRogueE);
            }
        });
    }

    @Override
    public ResourceLocation getTextureLocation(RogueDroidEntity entity) {
        return androidRogue;
    }
}
