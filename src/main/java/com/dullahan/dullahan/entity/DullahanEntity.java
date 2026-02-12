package com.dullahan.dullahan.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class DullahanEntity extends Monster {
    private static final EntityDataAccessor<Integer> ATTACK_PHASE = SynchedEntityData.defineId(DullahanEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> HAS_SPAWNED = SynchedEntityData.defineId(DullahanEntity.class, EntityDataSerializers.BOOLEAN);
    
    private final ServerBossEvent bossEvent = new ServerBossEvent(this.getDisplayName(), BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.PROGRESS);
    
    private int attackCooldown = 0;
    private int lightningSummonCooldown = 0;
    private static final int SWORD_ATTACK_COOLDOWN = 40; // 2 seconds
    private static final int LIGHTNING_COOLDOWN = 100; // 5 seconds
    private static final int SPAWN_EXPLOSION_DELAY = 10; // 0.5 seconds after spawn
    
    // Attack phases
    private static final int PHASE_IDLE = 0;
    private static final int PHASE_SWORD = 1;
    private static final int PHASE_LIGHTNING = 2;

    public DullahanEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.xpReward = 100; // Boss XP
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ATTACK_PHASE, PHASE_IDLE);
        this.entityData.define(HAS_SPAWNED, false);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 300.0D) // Boss health
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.ATTACK_DAMAGE, 2.0D) // 1 heart per hit
                .add(Attributes.ARMOR, 8.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.8D)
                .add(Attributes.FOLLOW_RANGE, 64.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new DullahanAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void tick() {
        super.tick();
        
        // Handle spawn explosion
        if (!this.level().isClientSide && !this.getHasSpawned() && this.tickCount >= SPAWN_EXPLOSION_DELAY) {
            this.performSpawnExplosion();
            this.setHasSpawned(true);
        }
        
        // Update boss bar
        if (!this.level().isClientSide) {
            this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
        }
        
        // Handle attack cooldowns
        if (attackCooldown > 0) {
            attackCooldown--;
        }
        if (lightningSummonCooldown > 0) {
            lightningSummonCooldown--;
        }
    }

    private void performSpawnExplosion() {
        // 9x9 explosion (radius ~4.5 blocks)
        BlockPos pos = this.blockPosition();
        
        // Create explosion without breaking blocks
        this.level().explode(this, pos.getX(), pos.getY(), pos.getZ(), 3.0F, Level.ExplosionInteraction.NONE);
        
        // Damage nearby players (2 hearts = 4.0 damage)
        AABB damageBox = new AABB(pos).inflate(4.5);
        List<Player> players = this.level().getEntitiesOfClass(Player.class, damageBox);
        for (Player player : players) {
            double distance = player.distanceToSqr(Vec3.atCenterOf(pos));
            if (distance <= 20.25) { // 4.5 squared
                player.hurt(this.damageSources().mobAttack(this), 4.0F);
            }
        }
        
        // Play explosion sound
        this.level().playSound(null, pos, SoundEvents.GENERIC_EXPLODE, this.getSoundSource(), 4.0F, 1.0F);
    }

    public void performSwordAttack(LivingEntity target) {
        if (attackCooldown <= 0 && target != null) {
            this.doHurtTarget(target);
            attackCooldown = SWORD_ATTACK_COOLDOWN;
            this.setAttackPhase(PHASE_SWORD);
            
            // Swing animation
            this.swing(this.getUsedItemHand());
            
            // Reset phase after animation
            this.level().getServer().execute(() -> {
                try {
                    Thread.sleep(300);
                    this.setAttackPhase(PHASE_IDLE);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
    }

    public void summonLightning(LivingEntity target) {
        if (lightningSummonCooldown <= 0 && target != null && !this.level().isClientSide) {
            BlockPos targetPos = target.blockPosition();
            
            // Summon lightning bolt
            net.minecraft.world.entity.LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(this.level());
            if (lightning != null) {
                lightning.moveTo(Vec3.atBottomCenterOf(targetPos));
                lightning.setVisualOnly(false);
                this.level().addFreshEntity(lightning);
                
                // Deal 3 hearts (6.0 damage) to target
                target.hurt(this.damageSources().lightningBolt(), 6.0F);
                
                lightningSummonCooldown = LIGHTNING_COOLDOWN;
                this.setAttackPhase(PHASE_LIGHTNING);
                
                // Reset phase
                this.level().getServer().execute(() -> {
                    try {
                        Thread.sleep(500);
                        this.setAttackPhase(PHASE_IDLE);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            }
        }
    }

    @Override
    public boolean doHurtTarget(net.minecraft.world.entity.Entity target) {
        // Sword damage: 1 heart (2.0 damage)
        return target.hurt(this.damageSources().mobAttack(this), 2.0F);
    }

    // Boss bar management
    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossEvent.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossEvent.removePlayer(player);
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        this.bossEvent.setName(this.getDisplayName());
    }

    // Data accessors
    public int getAttackPhase() {
        return this.entityData.get(ATTACK_PHASE);
    }

    public void setAttackPhase(int phase) {
        this.entityData.set(ATTACK_PHASE, phase);
    }

    public boolean getHasSpawned() {
        return this.entityData.get(HAS_SPAWNED);
    }

    public void setHasSpawned(boolean spawned) {
        this.entityData.set(HAS_SPAWNED, spawned);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("AttackPhase", this.getAttackPhase());
        tag.putBoolean("HasSpawned", this.getHasSpawned());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("AttackPhase")) {
            this.setAttackPhase(tag.getInt("AttackPhase"));
        }
        if (tag.contains("HasSpawned")) {
            this.setHasSpawned(tag.getBoolean("HasSpawned"));
        }
        if (this.hasCustomName()) {
            this.bossEvent.setName(this.getDisplayName());
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        // You'll need to register your custom sound
        return SoundEvents.WITHER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.WITHER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.WITHER_DEATH;
    }

    @Override
    public boolean canChangeDimensions() {
        return false; // Boss shouldn't teleport to other dimensions
    }

    @Override
    protected boolean shouldDespawnInPeaceful() {
        return false; // Boss shouldn't despawn in peaceful
    }
}
