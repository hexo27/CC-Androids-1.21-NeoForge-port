package com.thunderbear06.ai.task.tasks;

import com.thunderbear06.entity.android.AndroidEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

public class BreakBlockTask extends MoveToBlockTask
{

    public BreakBlockTask(AndroidEntity android, double moveSpeed, BlockPos pos)
    {
        super(android, moveSpeed, pos);
    }

    @Override
    public String getName()
    {
        return "breakingBlock";
    }

    @Override
    public boolean shouldTick()
    {
        return android.brain.getModules().miningModule.canMineBlock(getTarget());
    }

    @Override
    public void tick()
    {
        Vec3 pos = getTarget().getCenter();
        this.android.getLookControl().setLookAt(pos.x, pos.y, pos.z);

        if (isInRange(3)) {
            this.android.swing(InteractionHand.MAIN_HAND);
            this.android.brain.getModules().miningModule.mine(getTarget());
        }
        else
            super.tick();
    }

    @Override
    public void lastTick()
    {
        this.android.brain.getModules().miningModule.resetBreakProgress(getTarget());
    }
}
