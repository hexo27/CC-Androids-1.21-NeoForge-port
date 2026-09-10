package com.thunderbear06.entity.render;

import com.thunderbear06.CCAndroids;
import com.thunderbear06.entity.android.frame.AndroidFrame;
import com.thunderbear06.entity.model.AndroidFrameEntityModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;

public class AndroidFrameEntityRenderer extends HumanoidMobRenderer<AndroidFrame, AndroidFrameEntityModel> {
    private final ResourceLocation androidUnfinishedCore = ResourceLocation.fromNamespaceAndPath(CCAndroids.MOD_ID, "textures/entity/android_unfinished_core.png");
    private final ResourceLocation androidUnfinished = ResourceLocation.fromNamespaceAndPath(CCAndroids.MOD_ID, "textures/entity/android_unfinished.png");

    public AndroidFrameEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new AndroidFrameEntityModel(ctx.bakeLayer(ModelLayers.PLAYER)), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(AndroidFrame entity) {
        return entity.hasCore() ? androidUnfinishedCore : androidUnfinished;
    }
}
