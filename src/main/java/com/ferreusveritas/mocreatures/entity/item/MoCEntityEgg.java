package com.ferreusveritas.mocreatures.entity.item;

import com.ferreusveritas.mocreatures.MoCTools;
import com.ferreusveritas.mocreatures.MoCreatures;
import com.ferreusveritas.mocreatures.entity.MoCEntityTameableAquatic;
import com.ferreusveritas.mocreatures.entity.aquatic.EntityMediumFish;
import com.ferreusveritas.mocreatures.entity.aquatic.EntityPiranha;
import com.ferreusveritas.mocreatures.entity.aquatic.EntityShark;
import com.ferreusveritas.mocreatures.entity.aquatic.EntitySmallFish;
import com.ferreusveritas.mocreatures.entity.aquatic.EnumEgg;
import com.ferreusveritas.mocreatures.entity.monster.EntityScorpion;
import com.ferreusveritas.mocreatures.entity.monster.EntityScorpion.ScorpionType;
import com.ferreusveritas.mocreatures.entity.passive.EntityKomodo;
import com.ferreusveritas.mocreatures.entity.passive.EntityOstrich;
import com.ferreusveritas.mocreatures.entity.passive.EntityOstrich.OstrichType;
import com.ferreusveritas.mocreatures.entity.passive.EntityPetScorpion;
import com.ferreusveritas.mocreatures.entity.passive.EntitySnake;
import com.ferreusveritas.mocreatures.entity.passive.EntitySnake.SnakeType;
import com.ferreusveritas.mocreatures.entity.passive.EntityWyvern;
import com.ferreusveritas.mocreatures.entity.passive.EntityWyvern.WyvernType;
import com.ferreusveritas.mocreatures.init.MoCItems;

import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

public class MoCEntityEgg extends EntityLiving {

	private int tCounter;
	private int lCounter;
	public int eggType;

	public MoCEntityEgg(World world, int type) {
		this(world);
		this.eggType = type;
	}

	public MoCEntityEgg(World world) {
		super(world);
		setSize(0.25F, 0.25F);
		this.tCounter = 0;
		this.lCounter = 0;
	}

	public MoCEntityEgg(World world, double d, double d1, double d2) {
		super(world);

		setSize(0.25F, 0.25F);
		this.tCounter = 0;
		this.lCounter = 0;
	}

	public ResourceLocation getTexture() {
		return MoCreatures.proxy.getTexture("egg.png");
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D); // setMaxHealth
	}

	@Override
	public boolean canBreatheUnderwater() {
		return true;
	}

	@Override
	protected float getSoundVolume() {
		return 0.4F;
	}

	@Override
	public boolean handleWaterMovement() {
		if (this.world.handleMaterialAcceleration(this.getEntityBoundingBox(), Material.WATER, this)) {
			this.inWater = true;
			return true;
		} else {
			this.inWater = false;
			return false;
		}
	}

	@Override
	public void onCollideWithPlayer(EntityPlayer entityplayer) {
		int i = this.eggType;
		if (i == 30) {
			i = 31;
		}
		if ((this.lCounter > 10) && entityplayer.inventory.addItemStackToInventory(new ItemStack(MoCItems.mocegg, 1, i))) {
			this.playSound(SoundEvents.ENTITY_ITEM_PICKUP, 0.2F, (((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F) + 1.0F) * 2.0F);
			if (!this.world.isRemote) {
				entityplayer.onItemPickup(this, 1);

			}
			setDead();
		}
	}

	@Override
	public void onLivingUpdate() {
		this.moveStrafing = 0.0F;
		this.moveForward = 0.0F;
		this.randomYawVelocity = 0.0F;
		travel(this.moveStrafing, this.moveVertical, this.moveForward);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (!this.world.isRemote) {
			if (this.rand.nextInt(20) == 0) {
				this.lCounter++;
			}

			if (this.lCounter > 500) {
				EntityPlayer entityplayer1 = this.world.getClosestPlayerToEntity(this, 24D);
				if (entityplayer1 == null) {
					this.setDead();
				}
			}

			if (isInWater() && (getEggType() < 12 || getEggType() > 69) && (this.rand.nextInt(20) == 0)) {
				this.tCounter++;
				if (this.tCounter % 5 == 0) {
					this.motionY += 0.2;
				}

				if (this.tCounter == 5) {
					NotifyEggHatching();
				}

				if (this.tCounter >= 30) {

					EnumEgg egg = EnumEgg.eggNumToEggType(getEggType());

					MoCEntityTameableAquatic entityspawn = null;
					
					switch(egg) {
						case Shark:	entityspawn = new EntityShark(this.world); break;
						case Piranha: entityspawn = new EntityPiranha(this.world); break;

						//Medium Fish
						case Bass:
						case Cod:
						case Salmon: entityspawn = EntityMediumFish.createEntity(this.world, egg); break;

						//Small Fish
						case Anchovy:
						case AngelFish:
						case Angler:
						case ClownFish:
						case GoldFish:
						case HippoTang:
						case Manderin: entityspawn = EntitySmallFish.createEntity(this.world, egg); break;

						case None:
						default: break;
					}
	
					if(entityspawn != null) {
						entityspawn.setPosition(this.posX, this.posY, this.posZ);
						this.world.spawnEntity(entityspawn);
						entityspawn.setEdad(30);
						EntityPlayer entityplayer = this.world.getClosestPlayerToEntity(this, 24.0);
						if (entityplayer != null) {
							MoCTools.tameWithName(entityplayer, entityspawn);
						}
						
						MoCTools.playCustomSound(this, SoundEvents.ENTITY_CHICKEN_EGG);
					}
					
					setDead();
				}
			}

			else if (!isInWater() && getEggType() > 20 && (this.rand.nextInt(20) == 0)) // non aquatic creatures
			{
				this.tCounter++;
				//if (getEggType() == 30) tCounter = 0; //with this, wild ostriches won't spawn eggs.

				if (this.tCounter % 5 == 0) {
					this.motionY += 0.2D;
				}

				if (this.tCounter == 5) {
					NotifyEggHatching();
				}

				if (this.tCounter >= 30) {

					EnumEgg egg = EnumEgg.eggNumToEggType(getEggType());

					switch(egg) {
						//Snakes
						case DarkSnake:
						case SpottedSnake:
						case OrangeSnake:
						case GreenSnake:
						case CoralSnake:
						case Cobra:
						case RattleSnake:
						case Python: {
							EntitySnake entityspawn = new EntitySnake(this.world);

							SnakeType snakeType = EntitySnake.getSnake(egg);
						
							entityspawn.setPosition(this.posX, this.posY, this.posZ);
							entityspawn.setType(snakeType);
							entityspawn.setEdad(50);
							this.world.spawnEntity(entityspawn);
							EntityPlayer entityplayer = this.world.getClosestPlayerToEntity(this, 24.0);
							if (entityplayer != null) {
								MoCTools.tameWithName(entityplayer, entityspawn);
							}
						}
						break;

						
						case OstrichWild:
						case OstrichStolen: {
							EntityOstrich entityspawn = new EntityOstrich(this.world);
							OstrichType type = OstrichType.Chick;
							entityspawn.setPosition(this.posX, this.posY, this.posZ);
							entityspawn.setType(type);
							entityspawn.setEdad(35);
							this.world.spawnEntity(entityspawn);
							entityspawn.setHealth(entityspawn.getMaxHealth());

							if (egg == EnumEgg.OstrichStolen) {//stolen egg that hatches a tamed ostrich
								EntityPlayer entityplayer = this.world.getClosestPlayerToEntity(this, 24.0);
								if (entityplayer != null) {
									MoCTools.tameWithName(entityplayer, entityspawn);
								}
							}
						}
						break;

						case Komodo: {
							EntityKomodo entityspawn = new EntityKomodo(this.world);
							entityspawn.setPosition(this.posX, this.posY, this.posZ);
							entityspawn.setEdad(30);
							this.world.spawnEntity(entityspawn);
							EntityPlayer entityplayer = this.world.getClosestPlayerToEntity(this, 24.0);
							if (entityplayer != null) {
								MoCTools.tameWithName(entityplayer, entityspawn);
							}
						}
						break;
						
						case DirtScorpion:
						case CaveScorpion:
						case NetherScorpion:
						case FrostScorpion: 
						case UndeadScorpion: {
							EntityPetScorpion entityspawn = new EntityPetScorpion(this.world);
							ScorpionType type = EntityScorpion.getScorpion(egg);
							entityspawn.setPosition(this.posX, this.posY, this.posZ);
							entityspawn.setType(type);
							entityspawn.setAdult(false);
							this.world.spawnEntity(entityspawn);
							entityspawn.setHealth(entityspawn.getMaxHealth());
							EntityPlayer entityplayer = this.world.getClosestPlayerToEntity(this, 24.0);
							if (entityplayer != null) {
								MoCTools.tameWithName(entityplayer, entityspawn);
							}
						}
						break;

						
						case WyvernJungle:
						case WyvernSwamp:
						case WyvernSavanna:
						case WyvernSand:
						case WyvernMother:
						case WyvernUndead:
						case WyvernLight:
						case WyvernDark:
						case WyvernArctic:
						case WyvernCave:
						case WyvernMountain:
						case WyvernSea: {
							EntityWyvern entityspawn = new EntityWyvern(this.world);
							WyvernType type = EntityWyvern.getWyvern(egg);
							entityspawn.setPosition(this.posX, this.posY, this.posZ);
							entityspawn.setType(type);
							entityspawn.setAdult(false);
							entityspawn.setEdad(30);
							this.world.spawnEntity(entityspawn);
							entityspawn.setHealth(entityspawn.getMaxHealth());
							EntityPlayer entityplayer = this.world.getClosestPlayerToEntity(this, 24D);
							if (entityplayer != null) {
								MoCTools.tameWithName(entityplayer, entityspawn);
							}
						}
						break;
						
						default:
					}
					
					MoCTools.playCustomSound(this, SoundEvents.ENTITY_CHICKEN_EGG);
					setDead();
				}
			}
		}
	}

	private void NotifyEggHatching() {
		EntityPlayer entityplayer = this.world.getClosestPlayerToEntity(this, 24D);
		if (entityplayer != null) {
			entityplayer.sendMessage(new TextComponentTranslation("Egg hatching soon! KEEP WATCH! The hatched creature located @ "
					+ (int) this.posX + ", " + (int) this.posY + ", " + (int) this.posZ + " will be lost if you leave area"));
		}
	}

	public int getSize() {
		if (getEggType() == 30 || getEggType() == 31) {
			return 170;
		}
		return 100;
	}

	public int getEggType() {
		return this.eggType;
	}

	public void setEggType(int eggType) {
		this.eggType = eggType;
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbttagcompound) {
		super.readEntityFromNBT(nbttagcompound);
		nbttagcompound = MoCTools.getEntityData(this);
		setEggType(nbttagcompound.getInteger("EggType"));
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbttagcompound) {
		super.writeEntityToNBT(nbttagcompound);
		nbttagcompound = MoCTools.getEntityData(this);
		nbttagcompound.setInteger("EggType", getEggType());
	}

	@Override
	public boolean isEntityInsideOpaqueBlock() {
		return false;
	}
}
