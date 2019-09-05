package fatcat;

import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumParticleTypes;

public class ClientProxy extends CommonProxy {
	
	@Override
	public void registerRenderers() {
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
}
