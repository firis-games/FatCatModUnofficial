package fatcat;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class ItemFeatherToy extends Item {
	public static int MAX_DAMAGE = 64;
	
	public ItemFeatherToy() {
		super();
        this.setMaxDamage(MAX_DAMAGE);
        this.setMaxStackSize(1);
        //this.setCreativeTab(CreativeTabs.TOOLS);
        this.setCreativeTab(FatCatMod.FatCatModTab);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
    {
		playerIn.swingArm(handIn);
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
    }

}
