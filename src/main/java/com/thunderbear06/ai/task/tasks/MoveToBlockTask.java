package com.thunderbear06.ai.task.tasks;

import com.thunderbear06.entity.android.AndroidEntity;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.core.BlockPos;

public class MoveToBlockTask extends BlockBasedTask
{
    private final double moveSpeed;
    private final PathNavigation nav;

    public MoveToBlockTask(AndroidEntity android, double moveSpeed, BlockPos pos)
    {
        super(android, pos);
        this.moveSpeed = moveSpeed;
        nav = android.getNavigation();
    }

    @Override
    public String getName()
    {
        return "movingToBlock";
    }

    @Override
    public boolean shouldTick()
    {
        return !isInRange(2);
    }

    @Override
    public void firstTick() {}

    @Override
    public void tick()
    {
        if (!nav.isDone())
            return;

        nav.moveTo(nav.createPath(getTarget(), 0), moveSpeed);
    }

    @Override
    public void lastTick()
    {
        if (nav.isDone())
            return;

        nav.stop();
    }
}
