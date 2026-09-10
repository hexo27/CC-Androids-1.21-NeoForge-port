package com.thunderbear06.ai;

import com.thunderbear06.entity.android.AndroidEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;

public class AndroidLookAtEntityGoal extends LookAtPlayerGoal
{
    private final AndroidBrain brain;

    public AndroidLookAtEntityGoal(AndroidEntity android, Class<? extends LivingEntity> targetType, float range)
    {
        super(android, targetType, range);
        brain = android.brain;
    }

    @Override
    public boolean canContinueToUse()
    {
        return brain.getTaskManager().isIdle() && super.canContinueToUse();
    }

    @Override
    public boolean canUse()
    {
        return brain.getTaskManager().isIdle() && super.canUse();
    }
}
