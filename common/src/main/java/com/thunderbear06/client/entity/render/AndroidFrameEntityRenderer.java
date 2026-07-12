package com.thunderbear06.client.entity.render;

import com.thunderbear06.CCAndroids;
import com.thunderbear06.client.entity.model.AndroidFrameEntityModel;
import com.thunderbear06.entity.android.frame.AndroidFrame;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.util.Identifier;

public class AndroidFrameEntityRenderer extends BipedEntityRenderer<AndroidFrame, AndroidFrameEntityModel> {
    private final Identifier androidUnfinishedCore = Identifier.of(CCAndroids.MOD_ID, "textures/entity/android_unfinished_core.png");
    private final Identifier androidUnfinished = Identifier.of(CCAndroids.MOD_ID, "textures/entity/android_unfinished.png");

    public AndroidFrameEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new AndroidFrameEntityModel(ctx.getPart(EntityModelLayers.PLAYER)), 0.5f);
    }

    @Override
    public Identifier getTexture(AndroidFrame entity) {
        return entity.hasCore() ? androidUnfinishedCore : androidUnfinished;
    }
}
