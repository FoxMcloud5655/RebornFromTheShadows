package net.sonmok14.fromtheshadows;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.raid.Raid;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.sonmok14.fromtheshadows.proxy.ClientProxy;
import net.sonmok14.fromtheshadows.proxy.CommonProxy;
import net.sonmok14.fromtheshadows.utils.event.ServerEvents;
import net.sonmok14.fromtheshadows.utils.registry.EffectRegistry;
import net.sonmok14.fromtheshadows.utils.registry.EntityRegistry;
import net.sonmok14.fromtheshadows.utils.registry.ItemRegistry;
import net.sonmok14.fromtheshadows.utils.registry.ParticleRegistry;
import net.sonmok14.fromtheshadows.utils.registry.SoundRegistry;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.network.GeckoLibNetwork;

@Mod(Fromtheshadows.MODID)
public class Fromtheshadows {
	public static Fromtheshadows instance;
	public static final String MODID = "fromtheshadows";
	public static Logger LOGGER = LogManager.getLogger();
	public static CommonProxy PROXY = (CommonProxy) DistExecutor.runForDist(() -> ClientProxy::new, () -> CommonProxy::new);

	public Fromtheshadows() {
		instance = this;
		GeckoLibNetwork.initialize();
		GeckoLib.initialize();
		MinecraftForge.EVENT_BUS.register(new ServerEvents());
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		IEventBus forgeBus = MinecraftForge.EVENT_BUS;
		Fromtheshadows.PROXY.init(forgeBus);
		forgeBus.addListener(this::setup);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
		MinecraftForge.EVENT_BUS.register(this);
		EntityRegistry.ENTITY_TYPES.register(modEventBus);
		ParticleRegistry.PARTICLES.register(modEventBus);
		SoundRegistry.MOD_SOUNDS.register(modEventBus);
		ItemRegistry.ITEMS.register(modEventBus);
		EffectRegistry.EFFECT.register(modEventBus);
	}

	private void setup(FMLCommonSetupEvent event) {
		event.enqueueWork(() -> Raid.RaiderType.create("cultist", EntityRegistry.CULTIST.get(), new int[] {0, 0, 1, 0, 0, 2, 0, 3}));
	}
}
