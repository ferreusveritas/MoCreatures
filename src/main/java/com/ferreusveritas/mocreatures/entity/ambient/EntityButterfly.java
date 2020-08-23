package com.ferreusveritas.mocreatures.entity.ambient;

import com.ferreusveritas.mocreatures.entity.MoCEntityInsect;
import com.ferreusveritas.mocreatures.MoCreatures;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class EntityButterfly extends MoCEntityInsect {

	public EntityButterfly(World world) {
		super(world);
	}

	private int fCounter;

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();

		if (!this.world.isRemote) {
			if (getIsFlying() && this.rand.nextInt(200) == 0) {
				setIsFlying(false);
			}
		}
	}

	@Override
	public void selectType() {
		if (getType() == 0) {
			BlockPos blockPos = new BlockPos(this.posX, this.posY, this.posZ);
			Biome biome = world.getBiome(blockPos);
			if(biome == com.ferreusveritas.stargarden.features.Worlds.duvoticaBiome) {
				setType(11);
			}
			else {
				setType(this.rand.nextInt(10) + 1);
			}
		}
	}

	@Override
	public ResourceLocation getTexture() {
		switch (getType()) {
		case 1:
			return MoCreatures.proxy.getTexture("bfagalaisurticae.png");
		case 2:
			return MoCreatures.proxy.getTexture("bfargyreushyperbius.png");
		case 3:
			return MoCreatures.proxy.getTexture("bfathymanefte.png");
		case 4:
			return MoCreatures.proxy.getTexture("bfcatopsiliapomona.png");
		case 5:
			return MoCreatures.proxy.getTexture("bfmorphopeleides.png");
		case 6:
			return MoCreatures.proxy.getTexture("bfvanessaatalanta.png");
		case 7:
			return MoCreatures.proxy.getTexture("bfpierisrapae.png");
		case 8:
			return MoCreatures.proxy.getTexture("mothcamptogrammabilineata.png");
		case 9:
			return MoCreatures.proxy.getTexture("mothidiaaemula.png");
		case 10:
			return MoCreatures.proxy.getTexture("moththyatirabatis.png");
		case 11:
			return MoCreatures.proxy.getTexture("bfduvotica.png");
		default:
			return MoCreatures.proxy.getTexture("bfpierisrapae.png");
		}
	}

	public float tFloat() {
		if (!getIsFlying()) {
			return 0F;
		}
		if (++this.fCounter > 1000) {
			this.fCounter = 0;
		}

		return MathHelper.cos((this.fCounter * 0.1F)) * 0.2F;
	}

	@Override
	public float getSizeFactor() {
		if (getType() < 8) {
			return 0.7F;
		}
		return 1.0F;
	}

	@Override
	public boolean isMyFavoriteFood(ItemStack stack) {
		return !stack.isEmpty()
				&& (stack.getItem() == Item.getItemFromBlock(Blocks.RED_FLOWER) || stack.getItem() == Item
				.getItemFromBlock(Blocks.YELLOW_FLOWER));
	}

	@Override
	public boolean isAttractedToLight() {
		return getType() > 7;
	}

	@Override
	public boolean isFlyer() {
		return true;
	}

	@Override
	public float getAIMoveSpeed() {
		if (getIsFlying()) {
			return 0.13F;
		}
		return 0.10F;
	}
}
