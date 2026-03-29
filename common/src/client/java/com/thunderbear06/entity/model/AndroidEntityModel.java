package com.thunderbear06.entity.model;

import com.thunderbear06.entity.android.AndroidEntity;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.PlayerEntityModel;

public class AndroidEntityModel extends PlayerEntityModel<AndroidEntity> {
    public AndroidEntityModel(ModelPart root, boolean thinArms) {
        super(root, thinArms);
    }

    @Override
    protected void animateArms(AndroidEntity entity, float animationProgress) {
        if (!entity.isOn())
            return;

        super.animateArms(entity, animationProgress);
    }

    @Override
    public void setAngles(AndroidEntity livingEntity, float f, float g, float h, float i, float j) {
        if (!livingEntity.isOn()) {
            super.setAngles(livingEntity, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
            return;
        }

        super.setAngles(livingEntity, f, g, h, i, j);
    }
}
