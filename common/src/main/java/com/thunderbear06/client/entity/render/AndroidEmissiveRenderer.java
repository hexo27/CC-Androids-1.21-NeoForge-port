package com.thunderbear06.client.entity.render;

import com.thunderbear06.client.entity.model.AndroidEntityModel;
import com.thunderbear06.entity.android.AndroidEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public abstract class AndroidEmissiveRenderer extends FeatureRenderer<AndroidEntity, AndroidEntityModel> {

    public AndroidEmissiveRenderer(FeatureRendererContext<AndroidEntity, AndroidEntityModel> context) {
        super(context);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AndroidEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        RenderLayer renderLayer = this.getEyesTexture(entity);
        if (renderLayer == null)
            return;
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(renderLayer);
        this.getContextModel().render(matrices, vertexConsumer, 1, 1, 1);
    }

    public abstract @Nullable RenderLayer getEyesTexture(AndroidEntity entity);

    @Deprecated
    public Vec3d getColor(AndroidEntity entity) {
        return new Vec3d(1,1,1);
    }
}
