package com.thunderbear06.entity.model;

import com.thunderbear06.CCAndroids;
import com.thunderbear06.entity.android.frame.AndroidFrame;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;

public class AndroidFrameEntityModel extends PlayerModel<AndroidFrame> {
    public AndroidFrameEntityModel(ModelPart root) {
        super(root, false);
    }

    @Override
    public void prepareMobModel(AndroidFrame livingEntity, float limbSwing, float limbSwingAmount, float ageInTicks) {
        byte maxComps = CCAndroids.CONFIG.CompsForConstruction;
        byte maxIngots = CCAndroids.CONFIG.IngotsForConstruction;

        int comps = livingEntity.getComponentsNeeded();
        int ingots = livingEntity.getIngotsNeeded();

        this.leftArm.visible = comps <= maxComps * 0.75;
        this.rightArm.visible = comps <= maxComps * 0.5;
        this.hat.visible = comps == 0;
        this.jacket.visible = ingots <= maxIngots * 0.8;
        this.leftSleeve.visible = ingots <= maxIngots * 0.6;
        this.rightSleeve.visible = ingots <= maxIngots * 0.4;
        this.leftPants.visible = ingots <= maxIngots * 0.2;
        this.rightPants.visible = ingots == 0;

        super.prepareMobModel(livingEntity, limbSwing, limbSwingAmount, ageInTicks);
    }

    @Override
    public void setupAnim(AndroidFrame livingEntity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {}
}
