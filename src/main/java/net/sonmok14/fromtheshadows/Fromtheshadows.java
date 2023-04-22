package net.sonmok14.fromtheshadows;


import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.sonmok14.fromtheshadows.client.ClientEvent;
import net.sonmok14.fromtheshadows.proxy.ClientProxy;
import net.sonmok14.fromtheshadows.proxy.CommonProxy;
import net.sonmok14.fromtheshadows.utils.registry.EntityRegistry;
import net.sonmok14.fromtheshadows.utils.registry.EntitySpawnRegistry;
import net.sonmok14.fromtheshadows.utils.registry.ItemRegistry;
import net.sonmok14.fromtheshadows.utils.registry.SoundRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.network.GeckoLibNetwork;


@Mod(Fromtheshadows.MODID)
public class Fromtheshadows
{
    public static Fromtheshadows instance;
    public static final String MODID = "fromtheshadows";
    private static final Logger LOGGER = LogManager.getLogger();

    public Fromtheshadows()
    {
        instance = this;
        GeckoLibNetwork.initialize();
        GeckoLib.initialize();
        MinecraftForge.EVENT_BUS.register(new ClientEvent());
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeBus = MinecraftForge.EVENT_BUS;
        forgeBus.addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
        MinecraftForge.EVENT_BUS.register(this);
        EntityRegistry.ENTITY_TYPES.register(modEventBus);
        SoundRegistry.MOD_SOUNDS.register(modEventBus);
        ItemRegistry.ITEMS.register(modEventBus);

    }

    public static CommonProxy PROXY = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> CommonProxy::new);

    private void setupClient(FMLClientSetupEvent event) {
        PROXY.clientInit();
    }

    private void setup(final FMLCommonSetupEvent event)
    {
    }

    private void enqueueIMC(final InterModEnqueueEvent event)
    {
    }

    private void processIMC(final InterModProcessEvent event)
    {
    }
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
    }
}
