package fatcat.gui;

import java.util.List;

import fatcat.EntityFatCat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiStatusHandler implements IGuiHandler {
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
//		System.out.println("fatcat.GuiStatushandler: getServerGuiElement x="+x+",y="+y+",z="+z);
		List<EntityFatCat> list = world.getEntitiesWithinAABB(EntityFatCat.class, new AxisAlignedBB(x, y, z, x, y, z).expand(1.0F, 1.0F, 1.0F));
		if (!list.isEmpty()) {
			EntityFatCat cat = list.get(0);
			return new ContainerStatus(player, cat);
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
//		System.out.println("fatcat.GuiStatushandler: getClientGuiElement() x="+x+",y="+y+",z="+z);
		List<EntityFatCat> list = world.getEntitiesWithinAABB(EntityFatCat.class, new AxisAlignedBB(x, y, z, x, y, z).expand(1.0F, 1.0F, 1.0F));
		if (!list.isEmpty()) {
			EntityFatCat cat = list.get(0);
			return new GuiStatus(player, cat);
		}
		return null;
	}

}
