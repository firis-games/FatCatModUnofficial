package fatcat;

import fatcat.model.RenderFatCat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.EnumParticleTypes;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy {
	@Override
	public void registerRenderers() {
        registerEntityRenderers();
    	registerItemRenderers();
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
	
	@SuppressWarnings("deprecation")
	private void registerEntityRenderers() {
		RenderingRegistry.registerEntityRenderingHandler(EntityFatCat.class, new RenderFatCat(Minecraft.getMinecraft().getRenderManager()));
	}
	
	
	private void registerItemRenderers() {
		RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();

		renderItem.getItemModelMesher().register(FatCatMod.egg, 0, new ModelResourceLocation(FatCatMod.MODID + ":" + FatCatMod.egg.getUnlocalizedName().substring(5), "inventory"));
		renderItem.getItemModelMesher().register(FatCatMod.unko, 0, new ModelResourceLocation(FatCatMod.MODID + ":" + FatCatMod.unko.getUnlocalizedName().substring(5), "inventory"));
		renderItem.getItemModelMesher().register(FatCatMod.brush, 0, new ModelResourceLocation(FatCatMod.MODID + ":" + FatCatMod.brush.getUnlocalizedName().substring(5), "inventory"));
		renderItem.getItemModelMesher().register(FatCatMod.furball, 0, new ModelResourceLocation(FatCatMod.MODID + ":" + FatCatMod.furball.getUnlocalizedName().substring(5), "inventory"));
		renderItem.getItemModelMesher().register(FatCatMod.feather_toy, 0, new ModelResourceLocation(FatCatMod.MODID + ":" + FatCatMod.feather_toy.getUnlocalizedName().substring(5), "inventory"));
	}
}
