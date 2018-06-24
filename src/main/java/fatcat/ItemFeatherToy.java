package fatcat;

import net.minecraft.creativetab.CreativeTabs;
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
        this.setCreativeTab(CreativeTabs.TOOLS);
	}

	/**
     * Called when the equipped item is right clicked.
     */
	@Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer player, EnumHand hand)
    {
		player.swingArm(hand);
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStackIn);
    }

}
