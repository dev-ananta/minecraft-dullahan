package com.dullahan.dullahan.entity;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

public class DullahanAttackGoal extends MeleeAttackGoal {
    private final DullahanEntity dullahan;
    private int attackTimer = 0;
    private int attackCycle = 0; // Cycles between sword and lightning attacks

    public DullahanAttackGoal(DullahanEntity mob, double speedModifier, boolean followingTargetEvenIfNotSeen) {
        super(mob, speedModifier, followingTargetEvenIfNotSeen);
        this.dullahan = mob;
    }

    @Override
    public void start() {
        super.start();
        this.attackTimer = 0;
    }

    @Override
    public void tick() {
        super.tick();
        
        LivingEntity target = this.dullahan.getTarget();
        if (target == null) {
            return;
        }

        double distanceToTarget = this.dullahan.distanceToSqr(target.getX(), target.getY(), target.getZ());
        
        // Attack logic
        if (attackTimer > 0) {
            attackTimer--;
        }
        
        if (attackTimer <= 0) {
            // Alternate between sword attacks and lightning
            if (attackCycle % 3 == 0 && distanceToTarget <= 64.0D) { // Lightning every 3rd attack
                dullahan.summonLightning(target);
                attackTimer = 100; // 5 second cooldown
                attackCycle++;
            } else if (distanceToTarget <= 4.0D) { // Sword attack when close
                dullahan.performSwordAttack(target);
                attackTimer = 40; // 2 second cooldown
                attackCycle++;
            }
        }
    }

    @Override
    protected void checkAndPerformAttack(LivingEntity target, double distToTargetSqr) {
        double reachSqr = this.getAttackReachSqr(target);
        
        if (distToTargetSqr <= reachSqr && this.getTicksUntilNextAttack() <= 0) {
            this.resetAttackCooldown();
            this.dullahan.performSwordAttack(target);
        }
    }

    @Override
    protected double getAttackReachSqr(LivingEntity target) {
        return 4.0D + target.getBbWidth();
    }
}
