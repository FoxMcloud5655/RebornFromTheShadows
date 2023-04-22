package net.sonmok14.fromtheshadows.client.models;

import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.sonmok14.fromtheshadows.Fromtheshadows;
import net.sonmok14.fromtheshadows.entity.NehemothEntity;
import software.bernie.example.client.model.entity.BikeModel;
import software.bernie.example.entity.GeoExampleEntity;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

public class NehemothModel extends AnimatedGeoModel<NehemothEntity> {

    @Override
    public ResourceLocation getAnimationFileLocation(NehemothEntity entity) {
        return new ResourceLocation(Fromtheshadows.MODID, "animations/dracan.animation.json");
    }

    @Override
    public ResourceLocation getModelLocation(NehemothEntity entity) {
        return new ResourceLocation(Fromtheshadows.MODID, "geo/nehemoth.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(NehemothEntity entity) {
        BlockPos blockpos = new BlockPos(entity.getX(), entity.getEyeY(), entity.getZ());
        if(entity.isSilent())
        {
            return new ResourceLocation(Fromtheshadows.MODID, "textures/entity/nehemoth_stone.png");
        }
        return new ResourceLocation(Fromtheshadows.MODID, "textures/entity/nehemoth.png");
    }

    public void setLivingAnimations(NehemothEntity entity, Integer uniqueID, AnimationEvent customPredicate) {
        super.setLivingAnimations(entity, uniqueID, customPredicate);
        IBone root = this.getAnimationProcessor().getBone("root");
        IBone head = this.getAnimationProcessor().getBone("headrotate");
        EntityModelData extraData = (EntityModelData) customPredicate.getExtraDataOfType(EntityModelData.class).get(0);
        head.setRotationX(extraData.headPitch * 0.01F);
        head.setRotationY(extraData.netHeadYaw * 0.01F);
        head.setRotationX(-1F);

    }
}
