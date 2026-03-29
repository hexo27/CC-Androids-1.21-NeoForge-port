package com.thunderbear06.ai.task.tasks;

import com.thunderbear06.entity.android.AndroidEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Hand;

public class InteractEntityTask extends MoveToEntityTask
{
    public InteractEntityTask(AndroidEntity android, double moveSpeed, LivingEntity entity)
    {
        super(android, moveSpeed, entity);
    }

    @Override
    public String getName()
    {
        return "usingEntity";
    }

    @Override
    public boolean shouldTick()
    {
        return super.shouldTick();
    }

    @Override
    public void lastTick() {
        super.lastTick();

        if (!canReachTarget())
            return;

        android.getLookControl().lookAt(getTarget());
        android.brain.getModules().interactionModule.interactWithEntity(Hand.MAIN_HAND, getTarget());
    }
}
