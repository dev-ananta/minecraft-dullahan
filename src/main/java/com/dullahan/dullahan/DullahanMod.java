package com.dullahan.dullahan;

import com.mojang.logging.LogUtils;
import com.dullahan.dullahan.entity.DullahanEntity;
import com.dullahan.dullahan.init.ModEntities;
import com.dullahan.dullahan.init.ModSounds;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(DullahanMod.MOD_ID)
public class DullahanMod {
    public static final String MOD_ID = "dullahan";
    private static final Logger LOGGER = LogUtils.getLogger();

    public DullahanMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register deferred registers
        ModEntities.register(modEventBus);
        ModSounds.register(modEventBus);

        // Register setup and attribute events
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::entityAttributes);

        // Register ourselves for server and other game events
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            // Register spawn placement
            SpawnPlacements.register(ModEntities.DULLAHAN.get(),
                    SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    Monster::checkMonsterSpawnRules);
        });
        
        LOGGER.info("Dullahan Mod setup complete!");
    }

    private void entityAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.DULLAHAN.get(), DullahanEntity.createAttributes().build());
    }
}