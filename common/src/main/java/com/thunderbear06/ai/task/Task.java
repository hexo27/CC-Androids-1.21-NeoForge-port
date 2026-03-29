package com.thunderbear06.ai.task;

import com.thunderbear06.entity.android.AndroidEntity;

public abstract class Task
{
    protected long inactiveTicksToInterrupt = 100;
    protected long ticksInactive = 0;

    protected final AndroidEntity android;

    public Task(AndroidEntity android)
    {
        this.android = android;
    }

    public abstract String getName();

    public boolean shouldTick()
    {
        return ticksInactive < inactiveTicksToInterrupt;
    }

    public abstract void firstTick();
    public abstract void tick();

    public void lastTick()
    {
        ticksInactive = 0;
    }

    protected void cancel()
    {
        this.android.brain.getTaskManager().clearCurrentTask();
    }
}
