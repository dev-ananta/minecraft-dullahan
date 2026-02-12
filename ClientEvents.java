package com.yourmod.dullahan.client;

import com.yourmod.dullahan.DullahanMod;
import com.yourmod.dullahan.client.renderer.DullahanRenderer;
import com.yourmod.dullahan.init.ModEntities;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DullahanMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEvents {
    
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.DULLAHAN.get(), DullahanRenderer::new);
    }
}
