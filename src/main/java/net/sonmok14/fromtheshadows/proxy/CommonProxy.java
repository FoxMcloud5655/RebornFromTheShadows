package net.sonmok14.fromtheshadows.proxy;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.Mod;
import net.sonmok14.fromtheshadows.Fromtheshadows;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = Fromtheshadows.MODID, value = Dist.CLIENT)
public class CommonProxy {

	public void clientInit() {}

}
