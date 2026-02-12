package com.dullahan.dullahan.init;

import com.dullahan.dullahan.DullahanMod;
import com.dullahan.dullahan.entity.DullahanEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = 
        DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, DullahanMod.MOD_ID);

    public static final RegistryObject<EntityType<DullahanEntity>> DULLAHAN = 
        ENTITY_TYPES.register("dullahan", 
            () -> EntityType.Builder.of(DullahanEntity::new, MobCategory.MONSTER)
                .sized(0.6F, 1.95F) // Similar to player size
                .clientTrackingRange(10)
                .build("dullahan"));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
