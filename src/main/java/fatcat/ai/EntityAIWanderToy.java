package fatcat.ai;

import fatcat.EntityFatCat;
import fatcat.FatCatMod.FcmItems;
import fatcat.ItemFeatherToy;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/* 猫じゃらしに突撃する */
public class EntityAIWanderToy extends EntityAIBase {
	private EntityFatCat cat;
	private int tick, nextDamageTick;
	private EntityPlayer thePlayer;
	private double minPlayerDistance;
	private boolean moving;

	public EntityAIWanderToy(EntityFatCat cat, double distance) {
		this.cat = cat;
		this.minPlayerDistance = distance;
		this.setMutexBits(14);
		moving = false;
	}

	@Override
	public boolean shouldExecute() {
		this.thePlayer = cat.getEntityWorld().getClosestPlayerToEntity(this.cat, (double) this.minPlayerDistance);
		if (thePlayer == null) {
			return false;
		} else if (this.cat.isInSleep() || this.cat.getLeashed() || this.cat.getOwner() == null) {
			return false;
		/* 友好度がある程度高くないと反応しない  */
		} else if (!this.hasFeatherToy(thePlayer) || cat.getFriendship() < EntityFatCat.FRIENDSHIP_MAX*0.6) {
			return false;
		}
		return true;
	}

	@Override
	public void startExecuting() {
		this.cat.setAISit(false);
		this.cat.setSitting(false);
		tick = nextDamageTick = 0;
		moving = false;
	}

	@Override
	public void resetTask() {
		this.cat.setAISit(true);
	}

	@Override
	public boolean shouldContinueExecuting() {
		return (!cat.getOwner().isDead && hasFeatherToy(thePlayer));
	}

	@Override
	public void updateTask() {
		this.cat.getLookHelper().setLookPosition(this.thePlayer.posX,
				this.thePlayer.posY + (double) this.thePlayer.getEyeHeight(),
				this.thePlayer.posZ, 10.0F,
				(float) this.cat.getVerticalFaceSpeed());

		if (thePlayer.isSwingInProgress && cat.getRNG().nextFloat() < 0.01) {
			this.cat.getNavigator().tryMoveToXYZ(thePlayer.posX,
					thePlayer.posY, thePlayer.posZ, 0.6f);
			// 疲れる・腹が減る
			if (moving == false) {
				cat.setTiredness(cat.getTiredness()+EntityFatCat.TIREDNESS_MAX/7, EntityFatCat.StatusChangeReason.WanderToy);
				cat.setHunger(cat.getHunger()-EntityFatCat.HUNGER_MAX/15, EntityFatCat.StatusChangeReason.WanderToy);
			}
			moving = true;
		}
		if (cat.getDistanceSq(thePlayer) < 0.2D
				&& tick > nextDamageTick && moving) {
			damageFeatherToy(cat, thePlayer);
			nextDamageTick = tick + 20;
			moving = false;
		}
		tick++;
	}

	/**
	 * Gets if the Player has the feather toy in the hand.
	 */
	private boolean hasFeatherToy(EntityPlayer player) {
		ItemStack itemstack = player.inventory.getCurrentItem();
		if (itemstack == null) {
			return false;
		}
		return itemstack.getItem() == FcmItems.feather_toy;
	}

	private void damageFeatherToy(EntityFatCat cat, EntityPlayer player) {
		ItemStack itemstack = player.inventory.getCurrentItem();
		if (itemstack == null) {
			return;
		}
		itemstack.damageItem(ItemFeatherToy.MAX_DAMAGE / 3 + 1, cat);
	}
}
