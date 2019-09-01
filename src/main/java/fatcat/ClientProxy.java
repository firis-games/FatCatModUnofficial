package fatcat;

import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumParticleTypes;

public class ClientProxy extends CommonProxy {
	
	@Override
	public void registerRenderers() {
        //registerEntityRenderers();
    	//registerItemRenderers();
	}
	
	@Override
	public void spawnParticle(EnumParticleTypes type, final double posX, final double posY, final double posZ, double verX, double verY, double verZ, int number) {
		spawnParticle(type, posX, posY, posZ, verX, verY, verZ, number, new int[0]);
	}

	@Override
	public void spawnParticle(EnumParticleTypes type, final double posX, final double posY, final double posZ, double verX, double verY, double verZ, int number, int ... options) {
//		System.out.println("ClientProxy(spawnParticle): type="+type);
		for (int i = 0; i < number; i++) {
			// spawnParticle
			Minecraft.getMinecraft().renderGlobal.spawnParticle(type.getParticleID(), type.getShouldIgnoreRange(),posX, posY, posZ, verX, verY, verZ, options);
        }
	}
	
	/* ModelRegistryEventで登録を行う
	@SuppressWarnings("deprecation")
	private void registerEntityRenderers() {
		RenderingRegistry.registerEntityRenderingHandler(EntityFatCat.class, new RenderFatCat(Minecraft.getMinecraft().getRenderManager()));
	}
	
	private void registerItemRenderers() {
		RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();

		renderItem.getItemModelMesher().register(FcmItems.egg, 0, new ModelResourceLocation(FatCatMod.MODID + ":" + FcmItems.egg.getUnlocalizedName().substring(5), "inventory"));
		renderItem.getItemModelMesher().register(FcmItems.unko, 0, new ModelResourceLocation(FatCatMod.MODID + ":" + FcmItems.unko.getUnlocalizedName().substring(5), "inventory"));
		renderItem.getItemModelMesher().register(FcmItems.brush, 0, new ModelResourceLocation(FatCatMod.MODID + ":" + FcmItems.brush.getUnlocalizedName().substring(5), "inventory"));
		renderItem.getItemModelMesher().register(FcmItems.furball, 0, new ModelResourceLocation(FatCatMod.MODID + ":" + FcmItems.furball.getUnlocalizedName().substring(5), "inventory"));
		renderItem.getItemModelMesher().register(FcmItems.feather_toy, 0, new ModelResourceLocation(FatCatMod.MODID + ":" + FcmItems.feather_toy.getUnlocalizedName().substring(5), "inventory"));
	}
	*/
}
