package com.thunderbear06.ai.task.tasks;

import com.thunderbear06.entity.android.AndroidEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

public class InteractBlockTask extends MoveToBlockTask
{
    public InteractBlockTask(AndroidEntity android, double moveSpeed, BlockPos pos)
    {
        super(android, moveSpeed, pos);
    }

    @Override
    public String getName()
    {
        return "usingBlock";
    }

    @Override
    public void lastTick() {
        super.lastTick();

        if (!canReachTarget())
            return;

        BlockPos pos = getTarget();
        this.android.getLookControl().lookAt(pos.getX(), pos.getY(), pos.getZ());
        this.android.brain.getModules().interactionModule.interactWithBlock(Hand.MAIN_HAND, pos);
    }
}
