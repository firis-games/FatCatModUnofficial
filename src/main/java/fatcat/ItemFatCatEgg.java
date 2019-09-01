package fatcat;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemFatCatEgg extends Item {
	public ItemFatCatEgg() {
		super();
        //this.setCreativeTab(CreativeTabs.MISC);
        this.setCreativeTab(FatCatMod.FatCatModTab);
	}

    /**
     * Called when a Block is right-clicked with this Item
     */
	@Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
		ItemStack stack = player.getHeldItem(hand);
		
        if (worldIn.isRemote)
        {
            return EnumActionResult.SUCCESS;
        }
        else
        {
            pos = pos.offset(facing);

            EntityFatCat entity = new EntityFatCat(worldIn);
            entity.setPositionAndRotation(pos.getX(), pos.getY(), pos.getZ(), 0.0F, 0.0F);
            // setOwnerId
            entity.setOwnerId(player.getUniqueID());
            worldIn.spawnEntity(entity);

            if (entity != null)
            {
                if (!player.capabilities.isCreativeMode)
                {
                    stack.shrink(1);
                }
            }

            return EnumActionResult.SUCCESS;
        }
    }
}
