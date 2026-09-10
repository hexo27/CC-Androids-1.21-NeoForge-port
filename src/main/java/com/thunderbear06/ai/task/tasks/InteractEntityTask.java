package com.thunderbear06.ai.task.tasks;

import com.thunderbear06.entity.android.AndroidEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.InteractionHand;

public class InteractEntityTask extends MoveToEntityTask
{
    private boolean complete = false;

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
        return getTarget().isAlive() && !complete;
    }

    @Override
    public void tick()
    {
        if (isInRange(2))
        {
            android.getLookControl().setLookAt(getTarget());
            android.brain.getModules().interactionModule.interactWithEntity(InteractionHand.MAIN_HAND, getTarget());
            complete = true;
        }
        else
            super.tick();
    }
}
