package fatcat;

import java.math.BigDecimal;

import javax.annotation.Nullable;

import fatcat.FatCatMod.FcmItems;
import fatcat.ai.EntityAIAttackUnfriendlyOwner;
import fatcat.ai.EntityAIEatBlock;
import fatcat.ai.EntityAIEatEntityItem;
import fatcat.ai.EntityAIFatCatBeg;
import fatcat.ai.EntityAIFatCatMate;
import fatcat.ai.EntityAIFatCatSit;
import fatcat.ai.EntityAIFatCatSleep;
import fatcat.ai.EntityAIFatCatWander;
import fatcat.ai.EntityAIShit;
import fatcat.ai.EntityAIWanderToy;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAISit;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityFatCat extends EntityTameable {
	
	//DataManager用の定義
	private static final DataParameter<Integer> WEIGHT_DATA_INDEX = EntityDataManager.<Integer>createKey(EntityFatCat.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> HUNGER_DATA_INDEX = EntityDataManager.<Integer>createKey(EntityFatCat.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> BLADDER_DATA_INDEX = EntityDataManager.<Integer>createKey(EntityFatCat.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> POSE_DATA_INDEX = EntityDataManager.<Integer>createKey(EntityFatCat.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> FACE_DATA_INDEX = EntityDataManager.<Integer>createKey(EntityFatCat.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> TIREDNESS_DATA_INDEX = EntityDataManager.<Integer>createKey(EntityFatCat.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> FRIENDSHIP_DATA_INDEX = EntityDataManager.<Integer>createKey(EntityFatCat.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> SKIN_DATA_INDEX = EntityDataManager.<Integer>createKey(EntityFatCat.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> LOVENESS_DATA_INDEX = EntityDataManager.<Integer>createKey(EntityFatCat.class, DataSerializers.VARINT);
	
	public static final int TIREDNESS_MAX = 2000;
	public static final int FRIENDSHIP_MAX = 2000;
	public static final int LOVENESS_MAX = 2000;
	public static final int HUNGER_MAX = 100;
	public static final int BLADDER_MAX = 100;
	public static final int WEIGHT_STATUS_ADULT = 4000;
	public static final int WEIGHT_STATUS_MAX = 10000;
	public static final int HOUR_TICK = 1000;
	
	private int blinkTick = 0;
	private int myauTick = 0;
	private int brushingTick = 0;
	private boolean onLeash = false;
	private float nextNeckAngleSpeed = 0;
	private float neckAngleSpeed = 0;
	private float walkTick = 0;
	private float sprintTick = 0;
    public boolean isMating = false;
    public boolean tryMating = false;
	
	private EntityAIShit aiUnko;
	private EntityAIFatCatSleep aiSleep;
	
    public enum Face {
    	None,
    	Blink,
    	Sleep,
    	Shit,
    	Myau,
    	Baymax
    }
    public enum Pose {
    	None,
    	Shit,
    	Brushing,
    	Beg
    }
    public enum StatusChangeReason {
    	Tick,
    	NearUnko,
    	NearCat,
    	Eat,
    	Walk,
    	Sprint,
    	AwayFromOwner,
    	Debug,
    	Brushing,
    	Unkoed,
    	OnLeashed,
    	FromNBT,
    	Spawn,
    	Sleep,
    	Hungry,
    	WanderToy
    }

	public EntityFatCat(World world) {
		
		super(world);
    	
		this.setSize(0.9F, 1.0F);
		this.setCatScale();
		//this.setAvoidWaters(true);
        this.setTamed(true);
        
        // 育成モードオフ（デフォルト値に設定）
        if (!FatCatMod.breeding_mode) {
        	this.dataManager.set(WEIGHT_DATA_INDEX, 4000);
        	this.dataManager.set(HUNGER_DATA_INDEX, HUNGER_MAX);
        	this.dataManager.set(BLADDER_DATA_INDEX, 0);
        	this.dataManager.set(TIREDNESS_DATA_INDEX, 0);
        	this.dataManager.set(FRIENDSHIP_DATA_INDEX, FRIENDSHIP_MAX);
        	this.dataManager.set(LOVENESS_DATA_INDEX, 0);
        }
        FatCatMod.proxy.log(world, "EntityFatCat initialized(%s)", this.toString());
	}
	
	@Override
	protected void initEntityAI()
    {
		
		this.aiUnko = new EntityAIShit(this);
		
		this.aiSit = new EntityAISit(this);
		this.aiSit.setSitting(true);

		this.aiSleep = new EntityAIFatCatSleep(this);
		
		/*
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIPanic(this, 1.25D));
        this.tasks.addTask(2, new EntityAIMate(this, 1.0D));
        this.tasks.addTask(3, new EntityAITempt(this, 1.1D, Items.WHEAT, false));
        this.tasks.addTask(4, new EntityAIFollowParent(this, 1.1D));
        this.tasks.addTask(6, new EntityAIWander(this, 1.0D));
        this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
        this.tasks.addTask(8, new EntityAILookIdle(this));
        */
		
		int priority = 0;
        this.tasks.addTask(++priority, new EntityAISwimming(this));
        
        this.tasks.addTask(++priority, new EntityAIAttackUnfriendlyOwner(this));

        //食べ物系
        this.tasks.addTask(++priority, new EntityAIEatEntityItem(this,0.25f,0.6f,100));
        this.tasks.addTask(++priority, new EntityAIEatBlock(this));
        
        //トイレ
        this.tasks.addTask(++priority, this.aiUnko);
        
        
        
        //睡眠
        this.tasks.addTask(++priority, aiSleep);
        
        
        this.tasks.addTask(++priority, new EntityAIFatCatSit(this));
        this.tasks.addTask(++priority, new EntityAIWanderToy(this, 16.0D));
        
        //繁殖関連
        this.tasks.addTask(++priority, new EntityAIFatCatMate(this));
        
        this.tasks.addTask(++priority, new EntityAIFatCatWander(this, 0.5D));
        
        this.tasks.addTask(++priority, new EntityAIFatCatBeg(this, 8.0F));
        
        this.tasks.addTask(++priority, this.aiSit);
        
        priority++;
        this.tasks.addTask(priority, new EntityAIWatchClosest(this, EntityPlayer.class, 10.0F));
        this.tasks.addTask(priority, new EntityAILookIdle(this));
        int targetPriority = 0;
        this.targetTasks.addTask(++targetPriority, new EntityAIHurtByTarget(this, true));
       	
    }

	@Override
	protected void entityInit() {
		
		super.entityInit();
		
		this.dataManager.register(WEIGHT_DATA_INDEX, 500);
		this.dataManager.register(HUNGER_DATA_INDEX, 80);
		this.dataManager.register(BLADDER_DATA_INDEX, 20);
		this.dataManager.register(TIREDNESS_DATA_INDEX, 0);
		this.dataManager.register(FRIENDSHIP_DATA_INDEX, 30);
		this.dataManager.register(POSE_DATA_INDEX, 0);
		this.dataManager.register(FACE_DATA_INDEX, 0);
		this.dataManager.register(SKIN_DATA_INDEX, FatCatMod.instance.skinTypes.get(getRNG().nextInt(FatCatMod.instance.skinTypes.size())));
		this.dataManager.register(LOVENESS_DATA_INDEX, LOVENESS_MAX/3);
	}
	
	@Override
	public EntityFatCat createChild(EntityAgeable p_90011_1_) {
		EntityFatCat cat = new EntityFatCat(this.getEntityWorld());
		// setOwnerId
		cat.setOwnerId(this.getOwner().getUniqueID());
		return cat;
	}

	@Override
	public boolean isChild() {
		return getWeight() < WEIGHT_STATUS_ADULT;
	}
	
	@Override
	public void onUpdate() {
		super.onUpdate();
		
        this.neckAngleSpeed = this.nextNeckAngleSpeed;

        if (this.getPose() == Pose.Beg)
        {
            this.nextNeckAngleSpeed += (1.0F - this.nextNeckAngleSpeed) * 0.4F;
        }
        else
        {
            this.nextNeckAngleSpeed += (0.0F - this.nextNeckAngleSpeed) * 0.4F;
        }
	}

	@Override
    /**
     * main AI tick function, replaces updateEntityActionState
     */
    public void updateAITasks()
    {
    	// 丸1日半で満腹が0になる
		if ((this.ticksExisted % (HOUR_TICK*(24+12)/HUNGER_MAX)) == 0) {
			this.setHunger(getHunger()-1,StatusChangeReason.Tick);
			if (this.getHunger() == 0)
				this.attackEntityFrom(DamageSource.STARVE, getMaxHealth()*0.25f);
			// 腹が減っていると友好度down,重さdown
			if (isHungry()) {
				setFriendship(getFriendship()-1, StatusChangeReason.Hungry);
				setWeight(getWeight()-1, StatusChangeReason.Hungry);
			}
		}
		// 何もしなくても丸1日で100gほど減る
		if ((this.ticksExisted % HOUR_TICK*24/100) == 0) {
			this.setWeight(getWeight()-1, StatusChangeReason.Tick);
		}
		
		if (this.ticksExisted % HOUR_TICK == 0) {
			Entity owner = getOwner();
			if (owner != null) {
				// 離れていると丸2日で友好度が0になる
				float distance = getDistance(owner);
				if (distance > 16.0F) {
					setFriendship(getFriendship()-(FRIENDSHIP_MAX/42),StatusChangeReason.AwayFromOwner);
				}
			}
			
			// 近くにいるネコに恋愛度が上がる(20日でMAX）
			EntityFatCat entity = (EntityFatCat) this.getEntityWorld().findNearestEntityWithinAABB(EntityFatCat.class, getEntityBoundingBox().expand(8.0D, 8.0D, 8.0D), this);
        	if (entity != null) {
        		setLoveness(getLoveness()+(LOVENESS_MAX/(24*20)), StatusChangeReason.NearCat);
        	}
		}

        if (this.getMoveHelper().isUpdating() && !this.isRiding())
        {
            double d0 = this.getMoveHelper().getSpeed();

            if (d0 >= 0.6D)
            {
                this.setSneaking(false);
                this.setSprinting(true);
                setTiredness(getTiredness()+2, StatusChangeReason.Sprint);
                walkTick++;
                if (walkTick % 50 == 0) {
                	// 動いている時に沢山腹が減り、体重も減る
                	setHunger(getHunger()-2, StatusChangeReason.Sprint);
                	setWeight(getWeight()-2, StatusChangeReason.Sprint);
                }
            }
            else if (d0 >= 0.0D)
            {
                this.setSneaking(true);
                this.setSprinting(false);
                setTiredness(getTiredness()+1, StatusChangeReason.Walk);

            	if (this.getPose() == Pose.Brushing) {
            		this.setPose(Pose.None);
            	}
                sprintTick++;
                if (sprintTick % 50 == 0) {
                	setHunger(getHunger()-1, StatusChangeReason.Walk);
                	setWeight(getWeight()-1, StatusChangeReason.Walk);
                }
           }
            else
            {
                this.setSneaking(false);
                this.setSprinting(false);
            }
        }
        else
        {
            this.setSneaking(false);
            this.setSprinting(false);
        }
        
        /* まばたき */
        if (this.getFace() == Face.Blink) {
        	this.blinkTick--;
        	if (this.blinkTick <= 0) {
        		this.setFace(Face.None);
        	}
        }
        if (this.getFace() == Face.None) {
        	if (this.rand.nextInt(100) == 0) {
        		this.setFace(Face.Blink);
        		this.blinkTick = 6;
        	}
        }

        /* 鳴き声 */
        if (this.getFace() == Face.Myau) {
        	this.myauTick--;
        	if (this.myauTick <= 0) {
        		this.setFace(Face.None);
        	}
        }

        /* 横たわるポーズ */
        if (this.getPose() == Pose.Brushing) {
        	this.brushingTick--;
        	if (this.brushingTick <= 0) {
        		this.setPose(Pose.None);
        	}
        }
    }

	@Override
    /**
     * Determines if an entity can be despawned, used on idle far away entities
     */
    protected boolean canDespawn()
    {
        return false;
    }
	
	/*
	 * cat.worldObj.playSound((EntityPlayer)null, new BlockPos(cat.posX+0.5D, cat.posY+0.5D, cat.posZ+0.5D), 
    			SoundEvents.ENTITY_GENERIC_EAT, SoundCategory.PLAYERS, 1.0F, 1.0F);
	 */

	@Override
    /**
     * Returns the sound this mob makes while it's alive.
     * protected String getLivingSound()
     */
    protected SoundEvent getAmbientSound()
    {
		if (!this.getEntityWorld().isRemote) {
			if (!isInSleep()) {
				setFace(Face.Myau);
			}
		}
        if (this.isTamed()) {
        	if (this.isInLove()) {
        		return SoundEvents.ENTITY_CAT_PURR;
        	}
        	else if (isInSleep()) {
        		return new SoundEvent(new ResourceLocation(FatCatMod.MODID, "sleep"));
        	}
        	else {
        		if (this.rand.nextInt(4) == 0) {
            		return new SoundEvent(new ResourceLocation(FatCatMod.MODID, "purreow"));

        		} else {
        			return new SoundEvent(new ResourceLocation(FatCatMod.MODID, "meow"));

        		}
        	}
        }
        return null;
    }

    @Override
    @Nullable
    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
    	setFace(Face.Myau);
		cancelPose();
		/*
        return FatCatMod.MODID + ":hitt";
        */
		return new SoundEvent(new ResourceLocation(FatCatMod.MODID, "hitt"));
    }
    

	@Override
    /**
     * Returns the sound this mob makes on death.
     */
    protected SoundEvent getDeathSound()
    {
		setFace(Face.Myau);
        //return FatCatMod.MODID + ":hitt";
    	return new SoundEvent(new ResourceLocation(FatCatMod.MODID, "hitt"));
    }

    public void setAISit(boolean sit) {
    	this.aiSit.setSitting(sit);
    }
    
	public void eatEntityBounus(EntityItem food) {
		
		ItemStack foodStack = food.getItem();
		
		if (!foodStack.isEmpty()) {
			if (isFoodItem(foodStack.getItem())) {
				fatten(1, StatusChangeReason.Eat);
				this.heal(getMaxHealth()/6);
				setBladder(getBladder()+10, StatusChangeReason.Eat);
				setTiredness(getTiredness()+TIREDNESS_MAX/20, StatusChangeReason.Eat);
				// 6回の食事でお腹がいっぱい
				setHunger(getHunger()+HUNGER_MAX/6, StatusChangeReason.Eat);
				setFriendship(getFriendship()+100, StatusChangeReason.Eat);
				generateRandomParticles(EnumParticleTypes.HEART);
			}
			else {
				// damage by eaten non-food
				this.attackEntityFrom(DamageSource.causeThrownDamage(food, null), 5.0F);
				setFriendship(getFriendship()-200, StatusChangeReason.Eat);
				generateRandomParticles(EnumParticleTypes.SMOKE_NORMAL);
			}
		}
	}

	public boolean isFoodItem(Item item) {
		return item.getCreativeTab() != null && item.getCreativeTab().getTabLabel().equals("food");
	}
	
	public void eatBlockBounus(Block block) {
		if (block != null) {
			// 6回の食事でお腹がいっぱい
			this.heal(getMaxHealth()/6);
			setBladder(getBladder()+10, StatusChangeReason.Eat);
			setTiredness(getTiredness()+TIREDNESS_MAX/20, StatusChangeReason.Eat);
			setHunger(getHunger()+HUNGER_MAX/6, StatusChangeReason.Eat);
			setFriendship(getFriendship()-100, StatusChangeReason.Eat);
		}
	}
	
	// 太る(体重が重いほど太りづらい）
	private void fatten(int rate, StatusChangeReason reason) {
		int add = 0;
		int unit = 150 * rate;
		if (isChild()) {
			add = unit;
		}
		else {
			int max = WEIGHT_STATUS_MAX * 2;
			add = (int)(unit * (1.0F - (getWeight()/max)));
		}
		setWeight(getWeight()+add, reason);
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound table) {
		super.writeEntityToNBT(table);
		table.setInteger("Weight", getWeight());
		table.setInteger("Hunger", getHunger());
		table.setInteger("Bladder", getBladder());
		table.setInteger("Tiredness", getTiredness());
		table.setInteger("Friendship", getFriendship());
		table.setInteger("SkinType", getSkinType());
		table.setInteger("Love", getLoveness());
		FatCatMod.proxy.log(this.getEntityWorld(), "writeEntityToNBT: %s", table.toString());
	}
	
	@Override
	public void readEntityFromNBT(NBTTagCompound table) {
		super.readEntityFromNBT(table);
		this.setWeight(table.getInteger("Weight"),StatusChangeReason.FromNBT);
		this.setHunger(table.getInteger("Hunger"),StatusChangeReason.FromNBT);
		this.setBladder(table.getInteger("Bladder"),StatusChangeReason.FromNBT);
		this.setTiredness(table.getInteger("Tiredness"),StatusChangeReason.FromNBT);
		this.setFriendship(table.getInteger("Friendship"),StatusChangeReason.FromNBT);
		this.setSkinType(table.getInteger("SkinType"));
		this.setLoveness(table.getInteger("Love"),StatusChangeReason.FromNBT);

		this.setCatScale();

		FatCatMod.proxy.log(this.getEntityWorld(), "readEntityToNBT: %s", table.toString());
	}
	
	public int getWeight() {
		return this.dataManager.get(WEIGHT_DATA_INDEX);
	}
	
	public void setWeight(int weight, StatusChangeReason reason) {
		if (!FatCatMod.breeding_mode) {
			return;
		}

		FatCatMod.proxy.log(this.getEntityWorld(), "reason=%s, setWeignt=%d", reason.name(), weight);
		this.dataManager.set(WEIGHT_DATA_INDEX, weight);
	}
	
	// Hunger/空腹度
	public int getHunger() {
		return this.dataManager.get(HUNGER_DATA_INDEX);
	}
	
	public void setHunger(int hunger, StatusChangeReason reason) {
		if (!FatCatMod.breeding_mode) {
			return;
		}

		if (hunger > HUNGER_MAX) {
			hunger = HUNGER_MAX;
		}
		if (hunger < 0) {
			hunger = 0;
		}
		
		FatCatMod.proxy.log(this.getEntityWorld(), "reason=%s, setHunger=%d", reason.name(), hunger);
		this.dataManager.set(HUNGER_DATA_INDEX, hunger);
	}
	
	// ものが食える
	public boolean isEatable() {
		return getHunger() <= HUNGER_MAX*0.9;
	}

	// 腹減り状態
	public boolean isHungry() {
		return getHunger() <= HUNGER_MAX*0.25;
	}

	// 餓死寸前
	public boolean isStarved() {
		return getHunger() <= HUNGER_MAX*0.05;
	}
	

	// Bladder/便意（尿意）
	public int getBladder() {
		return this.dataManager.get(BLADDER_DATA_INDEX);
	}
	
	public void setBladder(int bladder, StatusChangeReason reason) {
		if (!FatCatMod.breeding_mode) {
			return;
		}

		// 便意MAX以上だとダメージ
		if (bladder > 100) {
			attackEntityFrom(DamageSource.GENERIC, 2.0F);
			bladder = 100;
		}
		if (bladder < 0) {
			bladder = 0;
		}
		
		// try to find a rest room (a sand block).
		if (bladder > 50) {
			aiUnko.tryExec = true;
		}
		FatCatMod.proxy.log(this.getEntityWorld(), "reason=%s, setBladder=%d", reason.name(), bladder);
		this.dataManager.set(BLADDER_DATA_INDEX, bladder);
	}


	// Tiredness/疲労度
	public int getTiredness() {
		return this.dataManager.get(TIREDNESS_DATA_INDEX);
	}
	
	public void setTiredness(int tiredness, StatusChangeReason reason) {
		if (!FatCatMod.breeding_mode) {
			return;
		}

		if (tiredness > TIREDNESS_MAX) {
			tiredness = TIREDNESS_MAX;
		}
		if (tiredness < 0) {
			tiredness = 0;
		}
		FatCatMod.proxy.log(this.getEntityWorld(), "reason=%s, setTiredness=%d", reason.name(), tiredness);
		this.dataManager.set(TIREDNESS_DATA_INDEX, tiredness);
	}
	
	// Friendship/友好度
	public int getFriendship() {
		return this.dataManager.get(FRIENDSHIP_DATA_INDEX);
	}
	
	public void setFriendship(int friendship, StatusChangeReason reason) {
		if (!FatCatMod.breeding_mode) {
			return;
		}

		if (friendship > FRIENDSHIP_MAX) {
			friendship = FRIENDSHIP_MAX;
		}
		if (friendship < 0) {
			friendship = 0;
		}
		FatCatMod.proxy.log(this.getEntityWorld(), "reason=%s, setFriendship=%d", reason.name(), friendship);
		this.dataManager.set(FRIENDSHIP_DATA_INDEX, friendship);
	}
	
	// Type of skin
	public int getSkinType() {
		return this.dataManager.get(SKIN_DATA_INDEX);
	}
	
	public void setSkinType(int type) {
		Integer max = FatCatMod.instance.skinTypes.get(FatCatMod.instance.skinTypes.size()-1);
		if (type > max) {
			type = 0;
		}
		if (type < 0) {
			type = max;
		}
		this.dataManager.set(SKIN_DATA_INDEX, type);
	}
	
	// 恋愛度
	public int getLoveness() {
		return this.dataManager.get(LOVENESS_DATA_INDEX);
	}

	public void setLoveness(int loveness, StatusChangeReason reason) {
		if (!FatCatMod.breeding_mode) {
			return;
		}

		if (loveness > LOVENESS_MAX) {
			loveness = LOVENESS_MAX;
		}
		if (loveness < 0) {
			loveness = 0;
		}
		FatCatMod.proxy.log(this.getEntityWorld(), "reason=%s, setLoveness=%d", reason.name(), loveness);
		this.dataManager.set(LOVENESS_DATA_INDEX, loveness);
	}

//	public boolean interact(EntityPlayer player) {
	@Override
	public boolean processInteract(EntityPlayer player, EnumHand hand)
    {
		ItemStack itemstack = player.getHeldItem(hand);
        if (super.processInteract(player, hand)) {
        	return true;
        }
        else if (!itemstack.isEmpty()) {
        	if (itemstack.getItem() == FcmItems.brush && !isInSleep()) {
        		brush(player, itemstack);
        		return false;
        	}
        	if (debugInteract(player, itemstack)) {
        		return false;
        	}
        }
        else {
            openGui(player);
    		return true;
        }
        return false;
    }
	
	/**
	 * 小麦などでinteractされるのを回避するためBreedingItemは常にfalse
	 */
	@Override
	public boolean isBreedingItem(ItemStack p_70877_1_)
	{
		return false;
	}

	
	/* デバッグモード 
	 * シャベル: wooden weight-, iron weight+
	 * ピッケル: wooden skin-, iron skin+
	 * りんご: loveness+
	 * 魚: friendly+
	 */
	private boolean debugInteract(EntityPlayer player, ItemStack itemstack) {
		if (this.getEntityWorld().isRemote) {
			return false;
		}
		if (FatCatMod.DEBUG) {
			if (itemstack.getItem() == Items.WOODEN_SHOVEL) {
				setWeight(getWeight()-500, StatusChangeReason.Debug);
				return true;
			} else if (itemstack.getItem() == Items.IRON_SHOVEL) {
				setWeight(getWeight()+500, StatusChangeReason.Debug);
				return true;
			} else if (itemstack.getItem() == Items.WOODEN_PICKAXE) {
				int type = FatCatMod.instance.skinTypes.indexOf(getSkinType())-1;
				if (type < 0) type = FatCatMod.instance.skinTypes.size()-1;
				setSkinType(FatCatMod.instance.skinTypes.get(type));
				return true;
			} else if (itemstack.getItem() == Items.IRON_PICKAXE) {
				int type = FatCatMod.instance.skinTypes.indexOf(getSkinType())+1;
				if (type >= FatCatMod.instance.skinTypes.size()) type = 0;
				setSkinType(FatCatMod.instance.skinTypes.get(type));
				return true;
			} else if (itemstack.getItem() == Items.BONE) {
				setHunger(getHunger()-HUNGER_MAX/5, StatusChangeReason.Debug);
				this.generateRandomParticles(EnumParticleTypes.HEART);
				return true;
			} else if (itemstack.getItem() == Items.APPLE) {
				setLoveness(getLoveness()+500, StatusChangeReason.Debug);
				this.generateRandomParticles(EnumParticleTypes.HEART);
				return true;
			}  else if (itemstack.getItem() == Items.FISH) {
				setFriendship(getFriendship()+500, StatusChangeReason.Debug);
				this.generateRandomParticles(EnumParticleTypes.HEART);
				return true;
			}  else if (itemstack.getItem() == Items.POTATO) {
				setFriendship(getFriendship()-500, StatusChangeReason.Debug);
				this.generateRandomParticles(EnumParticleTypes.VILLAGER_ANGRY);
				return true;
			} else if (itemstack.getItem() == FcmItems.unko) {
				setBladder(getBladder()+BLADDER_MAX/5, StatusChangeReason.Debug);
				return true;
			} 
			return false;
		} else {
			return false;
		}
	}

	// ブラシをかける
	private void brush(EntityPlayer player, ItemStack itemstack) {
		
		itemstack.damageItem(1, player);
		
		setPose(Pose.Brushing);
		if (getRNG().nextInt(10) == 0) {
			setFriendship(getFriendship()+FRIENDSHIP_MAX/10, StatusChangeReason.Brushing);
			setTiredness(getTiredness()-TIREDNESS_MAX/20, StatusChangeReason.Brushing);
			generateRandomParticles(EnumParticleTypes.HEART);
			if (!this.getEntityWorld().isRemote && getRNG().nextInt(6) == 0) {
				dropItem(FcmItems.furball, 1);
			}
		}
		else if (getRNG().nextInt(100) == 50) {
			setFriendship(getFriendship()-FRIENDSHIP_MAX/10, StatusChangeReason.Brushing);
			generateRandomParticles(EnumParticleTypes.VILLAGER_ANGRY);
			setPose(Pose.None);
		}
	}

	private void openGui(EntityPlayer player) {
		if (!this.getEntityWorld().isRemote)
        {
			player.openGui(FatCatMod.instance, FatCatMod.STATUS_GUI_ID, this.getEntityWorld(), 	(int)this.posX, (int)this.posY, (int)this.posZ);
        }
		
	}
	
	// pose type: Pose.Shit, Pose.Brushing, "beg" 
	public void setPose(Pose pose) {
		if (pose == Pose.Shit) {
			setFace(Face.Shit);
		}
		else if (pose == Pose.Brushing) {
			this.brushingTick = 50;
		}
		else {
			if (getFace() == Face.Shit) {
				setFace(Face.None);
			}
		}
		this.dataManager.set(POSE_DATA_INDEX, pose.ordinal());
	}
	
	public Pose getPose() {
		return Pose.values()[this.dataManager.get(POSE_DATA_INDEX)];
	}
	 
	public void setFace(Face face) {
		if (face == Face.Myau) {
			this.myauTick = 8;
		}
		this.dataManager.set(FACE_DATA_INDEX, face.ordinal());
	}
	
	public Face getFace() {
		return Face.values()[this.dataManager.get(FACE_DATA_INDEX)];
	}
	
	public void doUnko() {
		if (!this.getEntityWorld().isRemote) {
			EntityItem entityitem = new EntityItemUnko(this.getEntityWorld(), this.posX, this.posY - 0.3D, this.posZ, new ItemStack(FcmItems.unko, (getBladder()/20)));
			entityitem.setThrower(this.getCommandSenderEntity().getName());

			float f = 0.3F;
			entityitem.motionX = (double)(MathHelper.sin(this.rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float)Math.PI) * f);
			entityitem.motionZ = (double)(-MathHelper.cos(this.rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float)Math.PI) * f);
			entityitem.motionY = (double)(-MathHelper.sin(this.rotationPitch / 180.0F * (float)Math.PI) * f);

			this.getEntityWorld().spawnEntity(entityitem);
			//worldObj.playSoundEffect(posX, posY, posZ, FatCatMod.MODID + ":unko", 3.0F, 12.0f);
			this.getEntityWorld().playSound(null, new BlockPos(posX, posY, posZ), new SoundEvent(new ResourceLocation(FatCatMod.MODID, "unko")), null, 3.0F, 12.0f);
		}
		setBladder(0, StatusChangeReason.Unkoed);
	}
	
	public void cancelPose() {
		aiSleep.tryWakeup = true;
		brushingTick = 0;
		setPose(Pose.None);
	}
	
	public boolean isInSleep() {
		return getFace() == Face.Sleep;
	}
	
	public float getCatScale() {
        float scale = (new BigDecimal(this.getWeight() / 4500.0F)).setScale(1, BigDecimal.ROUND_DOWN).floatValue();
        scale = (float) Math.max(0.5, scale);
        return scale;
	}
	
    public void setCatScale()
    {
    	float scale = getCatScale();
		this.setScale(scale);
    }
    
    @Override
    /*
     * ロープをつないだ飼い主についていく
     * @see net.minecraft.entity.EntityCreature#updateLeashedState()
     */
    protected void updateLeashedState() {
    	super.updateLeashedState();
        
        if (this.getLeashed() 
        		&& this.getLeashHolder() != null 
        		&& this.getLeashHolder().getEntityWorld() == this.getEntityWorld())
        {
			Entity owner = this.getLeashHolder();
            float distance = this.getDistance(owner);
            
            if (!isInSleep()) {
            	this.setAISit(false);

            	// follow to owner
            	if (distance > 2.0F)
            	{
            		if (this.ticksExisted % 50 == 0)
            			this.setFriendship(this.getFriendship()+1, StatusChangeReason.OnLeashed);
            		this.getNavigator().tryMoveToXYZ(owner.posX+0.5D, owner.posY, owner.posZ+0.5D, 0.3f);
            	}
            }

        	if (!onLeash) {
        		// 親クラスで追加されたEntityAIMoveTowardsRestrictionを無効化
        		this.detachHome();
        		onLeash = true;
        	}
        }
        else if (onLeash) {
        	this.setAISit(true);
        	this.onLeash = false;
        }
    }

    @SideOnly(Side.CLIENT)
    public void generateRandomParticles(EnumParticleTypes type)
    {
        for (int i = 0; i < 7; ++i)
        {
            double d0 = this.rand.nextGaussian() * 0.02D;
            double d1 = this.rand.nextGaussian() * 0.02D;
            double d2 = this.rand.nextGaussian() * 0.02D;
            FatCatMod.proxy.spawnParticle(type, this.posX - 0.5D + (double)(this.rand.nextFloat()), this.posY - 1.0D + (this.height * this.getCatScale()) + (double)(this.rand.nextFloat()), this.posZ - 0.5D + (double)(this.rand.nextFloat()), d0, d1, d2, 1);
        }
    }
    
	@Override
    public boolean attackEntityAsMob(Entity target)
    {
        return target.attackEntityFrom(DamageSource.causeMobDamage(this), 3.0F);
    }
    
    @SideOnly(Side.CLIENT)
    public float getInterestedAngle(float a)
    {
        return (this.neckAngleSpeed + (this.nextNeckAngleSpeed - this.neckAngleSpeed) * a) * 0.15F * (float)Math.PI;
    }
    
    @Override
     /**
     * 死んだ時に自分のネームタグを落とす
     */
    protected void dropFewItems(boolean killed, int num)
    {
    	if (this.hasCustomName()) {
    		ItemStack tag = new ItemStack(Items.NAME_TAG);
    		tag.setStackDisplayName(this.getCustomNameTag());
    		entityDropItem(tag, 0.0F);
    	}
    }
    
    /**
     * onDataWatcherUpdateをnotifyDataManagerChangeへ変換
     * パラメータ変更時に呼び出される。サイズを変更する
     */
    @Override
    public void notifyDataManagerChange(DataParameter<?> key)
    {
        super.notifyDataManagerChange(key);
        if (key == WEIGHT_DATA_INDEX)
        {
        	setCatScale();
        }
    }
    
    
}
