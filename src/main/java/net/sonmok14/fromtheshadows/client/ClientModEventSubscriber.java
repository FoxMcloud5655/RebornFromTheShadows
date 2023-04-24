package net.sonmok14.fromtheshadows.client;

import net.minecraft.world.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sonmok14.fromtheshadows.Fromtheshadows;
import net.sonmok14.fromtheshadows.client.renderer.ArmoredNehemothRenderer;
import net.sonmok14.fromtheshadows.client.renderer.CultistRenderer;
import net.sonmok14.fromtheshadows.client.renderer.DiaboliumArmorRenderer;
import net.sonmok14.fromtheshadows.client.renderer.FallingBlockRenderer;
import net.sonmok14.fromtheshadows.client.renderer.NehemothRenderer;
import net.sonmok14.fromtheshadows.client.renderer.PlagueArmorRenderer;
import net.sonmok14.fromtheshadows.client.renderer.RendererNull;
import net.sonmok14.fromtheshadows.items.DiaboliumArmorItem;
import net.sonmok14.fromtheshadows.items.PlagueArmorItem;
import net.sonmok14.fromtheshadows.utils.registry.EntityRegistry;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;

@Mod.EventBusSubscriber(modid = Fromtheshadows.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEventSubscriber {
	@SubscribeEvent
	public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(EntityRegistry.CULTIST.get(), CultistRenderer::new);
		event.registerEntityRenderer(EntityRegistry.ARMORED_NEHEMOTH.get(), ArmoredNehemothRenderer::new);
		event.registerEntityRenderer(EntityRegistry.NEHEMOTH.get(), NehemothRenderer::new);
		event.registerEntityRenderer(EntityRegistry.SCREEN_SHAKE.get(), RendererNull::new);
		event.registerEntityRenderer(EntityRegistry.BREATH.get(), RendererNull::new);
		event.registerEntityRenderer(EntityRegistry.SOUL_BREATH.get(), RendererNull::new);
		event.registerEntityRenderer(EntityRegistry.FALLING_BLOCK.get(), FallingBlockRenderer::new);
	}

	@SubscribeEvent
	public static void registerRenderers(EntityRenderersEvent.AddLayers event) {
		GeoArmorRenderer.registerArmorRenderer(DiaboliumArmorItem.class, (GeoArmorRenderer) new DiaboliumArmorRenderer());
		GeoArmorRenderer.registerArmorRenderer(PlagueArmorItem.class, (GeoArmorRenderer) new PlagueArmorRenderer());
	}
}
