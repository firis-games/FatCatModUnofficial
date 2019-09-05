package fatcat.event;

import fatcat.FatCatMod;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootEntryTable;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber
public class LootTableLoadEventHandler {

	/**
	 * 
	 * @param event
	 */
	@SubscribeEvent
	public static void onLootTableLoadEvent(LootTableLoadEvent event) {
		
		//釣りかどうかの判断
		if (LootTableList.GAMEPLAY_FISHING.equals(event.getName())) {
			
			int weight = 1;
			int quality = 0;
			
			//injectからアイテムを追加
			LootEntryTable entry = new LootEntryTable(
					new ResourceLocation(FatCatMod.MODID, "inject/gameplay/fishing"),
					weight,
					quality,
					new LootCondition[0],
					"fatcat_fameplay_fishing");

			LootPool pool = event.getTable().getPool("main");
			if (pool != null) {
				pool.addEntry(entry);
			}
		}
	}
	
}
