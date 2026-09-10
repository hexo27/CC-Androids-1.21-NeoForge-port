package com.thunderbear06.entity.render;

import com.thunderbear06.entity.android.AndroidEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public abstract class AndroidEmissiveRenderer extends RenderLayer<AndroidEntity, PlayerModel<AndroidEntity>> {

    public AndroidEmissiveRenderer(RenderLayerParent<AndroidEntity, PlayerModel<AndroidEntity>> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, AndroidEntity entity, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        RenderType renderType = this.getEyesTexture(entity);
        if (renderType == null)
            return;
        VertexConsumer vertexConsumer = buffer.getBuffer(renderType);
        Vector3f color = getColor(entity).toVector3f();
        int packedColor = FastColor.ARGB32.color(255, (int) (Mth.clamp(color.x, 0.0F, 1.0F) * 255.0F), (int) (Mth.clamp(color.y, 0.0F, 1.0F) * 255.0F), (int) (Mth.clamp(color.z, 0.0F, 1.0F) * 255.0F));
        this.getParentModel().renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, packedColor);
    }

    public abstract @Nullable RenderType getEyesTexture(AndroidEntity entity);

    public Vec3 getColor(AndroidEntity entity) {
        return new Vec3(1, 1, 1);
    }
}
