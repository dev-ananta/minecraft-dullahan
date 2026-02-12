package com.yourmod.dullahan.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.yourmod.dullahan.entity.DullahanEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;

public class DullahanRenderer extends HumanoidMobRenderer<DullahanEntity, HumanoidModel<DullahanEntity>> {
    private static final ResourceLocation DULLAHAN_TEXTURE = 
        new ResourceLocation("yourmod", "textures/entity/dullahan.png");

    public DullahanRenderer(EntityRendererProvider.Context context) {
        super(context, new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER)), 0.5F);
        
        // Add armor layer for leather armor appearance
        this.addLayer(new HumanoidArmorLayer<>(this, 
            new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)),
            new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)),
            context.getModelManager()));
        
        // Add layer to render the netherite sword in hand
        this.addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()));
    }

    @Override
    public ResourceLocation getTextureLocation(DullahanEntity entity) {
        return DULLAHAN_TEXTURE;
    }

    @Override
    protected void scale(DullahanEntity entity, PoseStack poseStack, float partialTickTime) {
        // Make the boss slightly larger
        poseStack.scale(1.2F, 1.2F, 1.2F);
    }
}
