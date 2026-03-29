package com.thunderbear06.ai.task.tasks;

import com.thunderbear06.entity.android.AndroidEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Hand;

public class AttackEntityTask extends MoveToEntityTask
{
    private final boolean oneShot;
    private int attackCooldown;

    public AttackEntityTask(AndroidEntity android, double moveSpeed, LivingEntity entity, boolean isOneShot)
    {
        super(android, moveSpeed, entity);

        oneShot = isOneShot;
    }

    @Override
    public String getName()
    {
        return "attacking";
    }

    @Override
    public boolean shouldTick()
    {
        return getTarget().isAlive() && ticksInactive < inactiveTicksToInterrupt;
    }

    @Override
    public void tick()
    {
        if (this.attackCooldown-- > 0)
            return;

        if (canReachTarget() && this.attackCooldown <= 0)
            attack();
        else
            super.tick();
    }

    private void attack()
    {
        this.attackCooldown = 10;

        LivingEntity target = getTarget();

        this.android.getLookControl().lookAt(target);
        this.android.swingHand(Hand.MAIN_HAND);

        if (this.android.tryAttack(target) && oneShot)
            cancel();
    }

    @Override
    protected boolean canReachTarget() {
        return isInRange(2);
    }
}
