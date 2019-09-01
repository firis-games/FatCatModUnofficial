package fatcat.gui;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import fatcat.EntityFatCat;
import fatcat.FatCatMod;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiStatusHandler implements IGuiHandler {
	
	public static Map<UUID, EntityFatCat> serverCatList = new HashMap<>();
	public static Map<UUID, EntityFatCat> clientCatList = new HashMap<>();
		
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		/*
//		System.out.println("fatcat.GuiStatushandler: getServerGuiElement x="+x+",y="+y+",z="+z);
		List<EntityFatCat> list = world.getEntitiesWithinAABB(EntityFatCat.class, new AxisAlignedBB(x, y, z, x, y, z).expand(1.0F, 1.0F, 1.0F));
		if (!list.isEmpty()) {
			EntityFatCat cat = list.get(0);
			return new ContainerStatus(player, cat);
		}
		*/
		
		Object gui = null;
		if (FatCatMod.STATUS_GUI_ID == ID) {
			gui = new ContainerStatus(player, GuiStatusHandler.serverCatList.get(player.getUniqueID()));
			GuiStatusHandler.serverCatList.remove(player.getUniqueID());
		}
		
		return gui;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		/*
//		System.out.println("fatcat.GuiStatushandler: getClientGuiElement() x="+x+",y="+y+",z="+z);
		List<EntityFatCat> list = world.getEntitiesWithinAABB(EntityFatCat.class, new AxisAlignedBB(x, y, z, x, y, z).expand(1.0F, 1.0F, 1.0F));
		if (!list.isEmpty()) {
			EntityFatCat cat = list.get(0);
			return new GuiStatus(player, cat);
		}
		*/
		Object gui = null;
		if (FatCatMod.STATUS_GUI_ID == ID) {
			gui = new GuiStatus(player, GuiStatusHandler.clientCatList.get(player.getUniqueID()));
			GuiStatusHandler.clientCatList.remove(player.getUniqueID());
		}
		return gui;
	}

}
