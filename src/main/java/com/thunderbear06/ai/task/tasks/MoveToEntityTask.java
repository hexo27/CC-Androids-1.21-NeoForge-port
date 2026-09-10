package com.thunderbear06.ai.task.tasks;

import com.thunderbear06.entity.android.AndroidEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.core.BlockPos;

public class MoveToEntityTask extends EntityBasedTask
{
    private final double moveSpeed;
    private final PathNavigation nav;

    public MoveToEntityTask(AndroidEntity android, double moveSpeed, LivingEntity entity)
    {
        super(android, entity);
        this.moveSpeed = moveSpeed;

        nav = android.getNavigation();
    }

    @Override
    public String getName()
    {
        return "movingToEntity";
    }

    @Override
    public boolean shouldTick()
    {
        return super.shouldTick() && !isInRange(1);
    }

    @Override
    public void firstTick() {}

    @Override
    public void tick()
    {
        super.tick();

        if (nav.isDone())
        {
            BlockPos entityPos = getTarget().blockPosition();
            nav.moveTo(entityPos.getX(), entityPos.getY(), entityPos.getZ(), this.moveSpeed);
        }
    }

    @Override
    public void lastTick()
    {
        if (nav.isDone())
            return;

        nav.stop();
    }
}
