package com.thunderbear06.ai.task.tasks;

import com.thunderbear06.ai.task.Task;
import com.thunderbear06.entity.android.AndroidEntity;
import net.minecraft.world.entity.LivingEntity;

public abstract class EntityBasedTask extends Task
{
    private final LivingEntity targetEntity;

    public EntityBasedTask(AndroidEntity android, LivingEntity entity)
    {
        super(android);

        targetEntity = entity;
    }

    @Override
    public boolean shouldTick()
    {
        return targetEntity.isAlive();
    }

    @Override
    public void tick()
    {
        this.android.getLookControl().setLookAt(getTarget());
    }

    protected boolean isInRange(double distance)
    {
        return this.android.blockPosition().closerThan(getTarget().blockPosition(), distance);
    }

    protected LivingEntity getTarget()
    {
        return targetEntity;
    }
}
