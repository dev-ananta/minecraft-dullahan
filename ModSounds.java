package com.yourmod.dullahan.init;

import com.yourmod.dullahan.DullahanMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = 
        DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, DullahanMod.MOD_ID);

    // Custom sounds from the provided audio file
    public static final RegistryObject<SoundEvent> DULLAHAN_AMBIENT = registerSoundEvent("dullahan_ambient");
    public static final RegistryObject<SoundEvent> DULLAHAN_HURT = registerSoundEvent("dullahan_hurt");
    public static final RegistryObject<SoundEvent> DULLAHAN_DEATH = registerSoundEvent("dullahan_death");
    public static final RegistryObject<SoundEvent> DULLAHAN_SPAWN = registerSoundEvent("dullahan_spawn");

    private static RegistryObject<SoundEvent> registerSoundEvent(String name) {
        ResourceLocation id = new ResourceLocation(DullahanMod.MOD_ID, name);
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(id));
    }

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}
