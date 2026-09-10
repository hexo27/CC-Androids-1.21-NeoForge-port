package com.thunderbear06.ai.modules;

import com.thunderbear06.ai.AndroidBrain;
import com.thunderbear06.entity.android.BaseAndroidEntity;
import com.thunderbear06.entity.player.AndroidPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.block.state.BlockState;

public class MiningModule extends AbstractAndroidModule {
    private float breakProgress = 0.0f;

    public MiningModule(BaseAndroidEntity owner, AndroidBrain brain) {
        super(owner, brain);
    }

    public void mine(BlockPos pos) {
        this.breakProgress = tickBreakProgress(pos, this.breakProgress);

        if (this.breakProgress >= 10.0f) {
            breakBlock(pos);
        }
    }

    public void breakBlock(BlockPos pos) {
        AndroidPlayer.get(this.brain).player().gameMode.destroyBlock(pos);
    }

    public void resetBreakProgress(BlockPos pos) {
        this.android.level().destroyBlockProgress(this.android.getId(), pos, -1);
        this.breakProgress = 0.0f;
    }

    public boolean canMineBlock(BlockPos pos) {
        BlockState state = this.android.level().getBlockState(pos);

        return !state.isAir() && state.getDestroySpeed(this.android.level(), pos) > -1;
    }

    private float tickBreakProgress(BlockPos pos, float progress) {
        this.android.level().destroyBlockProgress(this.android.getId(), pos, (int) progress);
        progress += getBreakSpeed(pos);
        return progress;
    }

    private float getBreakSpeed(BlockPos pos) {
        BlockState state = android.level().getBlockState(pos);

        ServerPlayer player = AndroidPlayer.get(brain).player();

        float hardnessMod = state.getDestroySpeed(android.level(), pos);
        int canHarvestMod = player.hasCorrectToolForDrops(state) ? 30 : 100;

        return (player.getDestroySpeed(state) / hardnessMod / canHarvestMod) * 100.0f;
    }
}
