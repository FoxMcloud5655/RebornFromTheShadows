package net.sonmok14.fromtheshadows.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.sonmok14.fromtheshadows.Fromtheshadows;
import net.sonmok14.fromtheshadows.client.renderer.DiaboliumArmorRenderer;
import net.sonmok14.fromtheshadows.client.renderer.NehemothRenderer;
import net.sonmok14.fromtheshadows.client.renderer.RendererNull;
import net.sonmok14.fromtheshadows.items.DiaboliumArmorItem;
import net.sonmok14.fromtheshadows.utils.registry.EntityRegistry;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;

@Mod.EventBusSubscriber(modid = Fromtheshadows.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEventSubscriber {

    @SubscribeEvent
    public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(EntityRegistry.NEHEMOTH.get(), NehemothRenderer::new);
        event.registerEntityRenderer(EntityRegistry.SCREEN_SHAKE.get(), RendererNull::new);


    }

    @SubscribeEvent
    public static void registerRenderers(final EntityRenderersEvent.AddLayers event) {
            GeoArmorRenderer.registerArmorRenderer(DiaboliumArmorItem.class, new DiaboliumArmorRenderer());
    }
}
