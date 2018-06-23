package fatcat;

import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLLog;

public class CommonProxy {

	public void registerRenderers() {}
	
	public void spawnParticle(EnumParticleTypes type, final double posX, final double posY, final double posZ, double verX, double verY, double verZ, int number) {}
	public void spawnParticle(EnumParticleTypes type, final double posX, final double posY, final double posZ, double verX, double verY, double verZ, int number, int ... options) {}
	
	public void log(World world, String fmt, Object ... data) {
		if (FatCatMod.logging) {
			fmt = "[FatCatMOD]worldTime=" + world.getWorldTime() + ", " + fmt;
			FMLLog.info(fmt, data);
		}
	}
}
