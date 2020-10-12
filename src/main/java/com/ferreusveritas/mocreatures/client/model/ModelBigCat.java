package com.ferreusveritas.mocreatures.client.model;

import com.ferreusveritas.mocreatures.entity.passive.EntityBigCat;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class ModelBigCat extends ModelBase {

	//fields
	ModelRenderer RightHindFoot;
	ModelRenderer RightHindUpperLeg;
	ModelRenderer RightAnkle;
	ModelRenderer RightHindLowerLeg;
	ModelRenderer Ass;
	ModelRenderer TailTusk;
	ModelRenderer LeftChinBeard;
	ModelRenderer NeckBase;
	ModelRenderer RightEar;
	ModelRenderer LeftEar;
	ModelRenderer ForeheadHair;
	ModelRenderer LeftHarness;
	ModelRenderer RightHarness;
	ModelRenderer LeftUpperLip;
	ModelRenderer RightChinBeard;
	ModelRenderer LeftHindUpperLeg;
	ModelRenderer LeftHindLowerLeg;
	ModelRenderer LeftHindFoot;
	ModelRenderer LeftAnkle;
	ModelRenderer InsideMouth;
	ModelRenderer RightUpperLip;
	ModelRenderer LowerJawTeeth;
	ModelRenderer Nose;
	ModelRenderer LeftFang;
	ModelRenderer UpperTeeth;
	ModelRenderer RightFang;
	ModelRenderer LowerJaw;
	ModelRenderer SaddleFront;
	ModelRenderer LeftUpperLeg;
	ModelRenderer LeftLowerLeg;
	ModelRenderer LeftFrontFoot;
	ModelRenderer LeftClaw2;
	ModelRenderer LeftClaw1;
	ModelRenderer LeftClaw3;
	ModelRenderer RightClaw1;
	ModelRenderer RightClaw2;
	ModelRenderer RightClaw3;
	ModelRenderer RightFrontFoot;
	ModelRenderer RightLowerLeg;
	ModelRenderer RightUpperLeg;
	ModelRenderer Head;
	ModelRenderer ChinHair;
	ModelRenderer NeckHair;
	ModelRenderer Mane;
	ModelRenderer Abdomen;
	ModelRenderer TailRoot;
	ModelRenderer Tail2;
	ModelRenderer Tail3;
	ModelRenderer Tail4;
	ModelRenderer TailTip;
	ModelRenderer Chest;
	ModelRenderer SaddleBack;
	ModelRenderer LeftFootRing;
	ModelRenderer Saddle;
	ModelRenderer LeftFootHarness;
	ModelRenderer RightFootHarness;
	ModelRenderer RightFootRing;
	ModelRenderer HeadBack;
	ModelRenderer HarnessStick;
	ModelRenderer NeckHarness;
	ModelRenderer Collar;
	ModelRenderer StorageChest;

	private float radianF = 57.29578F;

	protected boolean hasMane;
	protected boolean isRidden;
	protected boolean isSaddled;
	protected boolean onAir;
	private boolean isSitting;
	protected boolean poisoning;
	protected boolean movingTail;
	protected int openMouthCounter;
	protected boolean hasSaberTeeth;
	protected boolean hasChest;
	protected boolean isMovingVertically;

	public ModelBigCat() {
		textureWidth = 128;
		textureHeight = 128;

		Chest = new ModelRenderer(this, 0, 18);
		Chest.addBox(-3.5F, 0F, -8F, 7, 8, 9);
		Chest.setRotationPoint(0F, 8F, 0F);

		NeckBase = new ModelRenderer(this, 0, 7);
		NeckBase.addBox(-2.5F, 0F, -2.5F, 5, 6, 5);
		NeckBase.setRotationPoint(0F, -0.5F, -8F);
		setRotation(NeckBase, -14F / radianF, 0F, 0F);
		Chest.addChild(NeckBase);

		Collar = new ModelRenderer(this, 18, 0);
		Collar.addBox(-2.5F, 0F, 0F, 5, 4, 1, 0.0F);
		Collar.setRotationPoint(0.0F, 6F, -2F);
		setRotation(Collar, 20F / radianF, 0F, 0F);
		NeckBase.addChild(Collar);

		HeadBack = new ModelRenderer(this, 0, 0);
		HeadBack.addBox(-2.51F, -2.5F, -1F, 5, 5, 2);
		HeadBack.setRotationPoint(0F, 2.7F, -2.9F);
		setRotation(HeadBack, 14F / radianF, 0F, 0F);
		NeckBase.addChild(HeadBack);

		NeckHarness = new ModelRenderer(this, 85, 32);
		NeckHarness.addBox(-3F, -3F, -2F, 6, 6, 2);
		NeckHarness.setRotationPoint(0F, 0F, 0.95F);
		HeadBack.addChild(NeckHarness);

		HarnessStick = new ModelRenderer(this, 85, 42);
		HarnessStick.addBox(-3.5F, -0.5F, -0.5F, 7, 1, 1);
		HarnessStick.setRotationPoint(0F, -1.8F, 0.5F);
		setRotation(HarnessStick, 45F / radianF, 0F, 0F);
		HeadBack.addChild(HarnessStick);

		LeftHarness = new ModelRenderer(this, 85, 32);
		LeftHarness.addBox(3.2F, -0.6F, 1.5F, 0, 1, 9);
		LeftHarness.setRotationPoint(0F, 8.6F, -13F);
		setRotation(this.LeftHarness, 25F / this.radianF, 0F, 0F);

		RightHarness = new ModelRenderer(this, 85, 31);
		RightHarness.addBox(-3.2F, -0.6F, 1.5F, 0, 1, 9);
		RightHarness.setRotationPoint(0F, 8.6F, -13F);
		setRotation(RightHarness, 25F / radianF, 0F, 0F);

		Head = new ModelRenderer(this, 32, 0);
		Head.addBox(-3.5F, -3F, -2F, 7, 6, 4);
		Head.setRotationPoint(0F, 0.2F, -2.2F);
		HeadBack.addChild(Head);

		Nose = new ModelRenderer(this, 46, 19);
		Nose.addBox(-1.5F, -1F, -2F, 3, 2, 4);
		Nose.setRotationPoint(0F, 0F, -3F);
		setRotation(Nose, 27F / radianF, 0F, 0F);
		Head.addChild(Nose);

		RightUpperLip = new ModelRenderer(this, 34, 19);
		RightUpperLip.addBox(-1F, -1F, -2F, 2, 2, 4);
		RightUpperLip.setRotationPoint(-1.25F, 1F, -2.8F);
		setRotation(RightUpperLip, 10F / radianF, 2F / radianF, -15F / radianF);
		Head.addChild(RightUpperLip);

		LeftUpperLip = new ModelRenderer(this, 34, 25);
		LeftUpperLip.addBox(-1F, -1F, -2F, 2, 2, 4);
		LeftUpperLip.setRotationPoint(1.25F, 1F, -2.8F);
		setRotation(LeftUpperLip, 10F / radianF, -2F / radianF, 15F / radianF);
		Head.addChild(LeftUpperLip);

		UpperTeeth = new ModelRenderer(this, 20, 7);
		UpperTeeth.addBox(-1.5F, -1F, -1.5F, 3, 2, 3);
		UpperTeeth.setRotationPoint(0F, 2F, -2.5F);
		setRotation(UpperTeeth, 15F / radianF, 0F, 0F);
		Head.addChild(UpperTeeth);

		LeftFang = new ModelRenderer(this, 44, 10);
		LeftFang.addBox(-0.5F, -1.5F, -0.5F, 1, 3, 1);
		LeftFang.setRotationPoint(1.2F, 2.8F, -3.4F);
		setRotation(LeftFang, 15F / radianF, 0F, 0F);
		Head.addChild(LeftFang);

		RightFang = new ModelRenderer(this, 48, 10);
		RightFang.addBox(-0.5F, -1.5F, -0.5F, 1, 3, 1);
		RightFang.setRotationPoint(-1.2F, 2.8F, -3.4F);
		setRotation(RightFang, 15F / radianF, 0F, 0F);
		Head.addChild(RightFang);

		InsideMouth = new ModelRenderer(this, 50, 0);
		InsideMouth.addBox(-1.5F, -1F, -1F, 3, 2, 2);
		InsideMouth.setRotationPoint(0F, 2F, -1F);
		Head.addChild(InsideMouth);

		LowerJaw = new ModelRenderer(this, 46, 25);
		LowerJaw.addBox(-1.5F, -1F, -4F, 3, 2, 4);
		LowerJaw.setRotationPoint(0F, 2.1F, 0F);
		Head.addChild(LowerJaw);

		LowerJawTeeth = new ModelRenderer(this, 20, 12);
		LowerJawTeeth.addBox(-1F, 0F, -1F, 2, 1, 2);
		LowerJawTeeth.setRotationPoint(0F, -1.8F, -2.7F);
		LowerJawTeeth.mirror = true;
		LowerJaw.addChild(LowerJawTeeth);

		ChinHair = new ModelRenderer(this, 76, 7);
		ChinHair.addBox(-2.5F, 0F, -2F, 5, 6, 4);
		ChinHair.setRotationPoint(0F, 0F, 1F);
		LowerJaw.addChild(ChinHair);

		LeftChinBeard = new ModelRenderer(this, 48, 10);
		LeftChinBeard.addBox(-1F, -2.5F, -2F, 2, 5, 4);
		LeftChinBeard.setRotationPoint(3.6F, 0F, 0.25F);
		setRotation(LeftChinBeard, 0F, 30F / radianF, 0F);
		Head.addChild(LeftChinBeard);

		RightChinBeard = new ModelRenderer(this, 36, 10);
		RightChinBeard.addBox(-1F, -2.5F, -2F, 2, 5, 4);
		RightChinBeard.setRotationPoint(-3.6F, 0F, 0.25F);
		setRotation(RightChinBeard, 0F, -30F / radianF, 0F);
		Head.addChild(RightChinBeard);

		ForeheadHair = new ModelRenderer(this, 88, 0);
		ForeheadHair.addBox(-1.5F, -1.5F, -1.5F, 3, 3, 3);
		ForeheadHair.setRotationPoint(0F, -3.2F, 0F);
		setRotation(ForeheadHair, 10F / radianF, 0F, 0F);
		Head.addChild(ForeheadHair);

		Mane = new ModelRenderer(this, 94, 0);
		Mane.addBox(-5.5F, -5.5F, -3F, 11, 11, 6);
		Mane.setRotationPoint(0F, 0.7F, 3.7F);
		setRotation(Mane, -5F / radianF, 0F, 0F);
		Head.addChild(Mane);

		RightEar = new ModelRenderer(this, 54, 7);
		RightEar.addBox(-1F, -1F, -0.5F, 2, 2, 1);
		RightEar.setRotationPoint(-2.7F, -3.5F, 1F);
		setRotation(RightEar, 0F, 0F, -15F / radianF);
		Head.addChild(RightEar);

		LeftEar = new ModelRenderer(this, 54, 4);
		LeftEar.addBox(-1F, -1F, -0.5F, 2, 2, 1);
		LeftEar.setRotationPoint(2.7F, -3.5F, 1F);
		setRotation(LeftEar, 0F, 0F, 15F / radianF);
		Head.addChild(LeftEar);

		NeckHair = new ModelRenderer(this, 108, 17);
		NeckHair.addBox(-2F, -1F, -3F, 4, 2, 6);
		NeckHair.setRotationPoint(0F, -0.5F, 3F);
		setRotation(NeckHair, -10.6F / radianF, 0F, 0F);
		NeckBase.addChild(NeckHair);

		Abdomen = new ModelRenderer(this, 0, 35);
		Abdomen.addBox(-3F, 0F, 0F, 6, 7, 7);
		Abdomen.setRotationPoint(0F, 0F, 0F);
		setRotation(Abdomen, -0.0523599F, 0F, 0F);
		Chest.addChild(Abdomen);

		Ass = new ModelRenderer(this, 0, 49);
		Ass.addBox(-2.5F, 0F, 0F, 5, 5, 3);
		Ass.setRotationPoint(0F, 0F, 7F);
		setRotation(Ass, -20F / radianF, 0F, 0F);
		Abdomen.addChild(Ass);

		TailRoot = new ModelRenderer(this, 96, 83);
		TailRoot.addBox(-1F, 0F, -1F, 2, 4, 2);
		TailRoot.setRotationPoint(0F, 1F, 7F);
		setRotation(TailRoot, 87F / radianF, 0F, 0F);
		Abdomen.addChild(TailRoot);

		Tail2 = new ModelRenderer(this, 96, 75);
		Tail2.addBox(-1F, 0F, -1F, 2, 6, 2);
		Tail2.setRotationPoint(-0.01F, 3.5F, 0F);
		setRotation(Tail2, -30F / radianF, 0F, 0F);
		TailRoot.addChild(Tail2);

		Tail3 = new ModelRenderer(this, 96, 67);
		Tail3.addBox(-1F, 0F, -1F, 2, 6, 2);
		Tail3.setRotationPoint(0.01F, 5.5F, 0F);
		setRotation(Tail3, -17F / radianF, 0F, 0F);
		Tail2.addChild(Tail3);

		Tail4 = new ModelRenderer(this, 96, 61);
		Tail4.addBox(-1F, 0F, -1F, 2, 4, 2);
		Tail4.setRotationPoint(-0.01F, 5.5F, 0F);
		setRotation(Tail4, 21F / radianF, 0F, 0F);
		Tail3.addChild(Tail4);

		TailTip = new ModelRenderer(this, 96, 55);
		TailTip.addBox(-1F, 0F, -1F, 2, 4, 2);
		TailTip.setRotationPoint(0.01F, 3.5F, 0F);
		setRotation(TailTip, 21F / radianF, 0F, 0F);
		Tail4.addChild(TailTip);

		TailTusk = new ModelRenderer(this, 96, 49);
		TailTusk.addBox(-1.5F, 0F, -1.5F, 3, 3, 3);
		TailTusk.setRotationPoint(0F, 3.5F, 0F);
		setRotation(TailTusk, 21F / radianF, 0F, 0F);
		Tail4.addChild(TailTusk);

		Saddle = new ModelRenderer(this, 79, 18);
		Saddle.addBox(-4F, -1F, -3F, 8, 2, 6);
		Saddle.setRotationPoint(0F, 0.5F, -1F);
		Chest.addChild(Saddle);

		SaddleFront = new ModelRenderer(this, 101, 26);
		SaddleFront.addBox(-2.5F, -1F, -1.5F, 5, 2, 3);
		SaddleFront.setRotationPoint(0F, -1.0F, -1.5F);
		setRotation(SaddleFront, -10.6F / radianF, 0F, 0F);
		Saddle.addChild(SaddleFront);

		SaddleBack = new ModelRenderer(this, 77, 26);
		SaddleBack.addBox(-4F, -2F, -2F, 8, 2, 4);
		SaddleBack.setRotationPoint(0F, 0.7F, 4F);
		setRotation(SaddleBack, 12.78F / radianF, 0F, 0F);
		Saddle.addChild(SaddleBack);

		LeftFootHarness = new ModelRenderer(this, 81, 18);
		LeftFootHarness.addBox(-0.5F, 0F, -0.5F, 1, 5, 1);
		LeftFootHarness.setRotationPoint(4F, 0F, 0.5F);
		Saddle.addChild(LeftFootHarness);

		LeftFootRing = new ModelRenderer(this, 107, 31);
		LeftFootRing.addBox(0F, 0F, 0F, 1, 2, 2);
		LeftFootRing.setRotationPoint(-0.5F, 5F, -1F);
		LeftFootHarness.addChild(LeftFootRing);

		RightFootHarness = new ModelRenderer(this, 101, 18);
		RightFootHarness.addBox(-0.5F, 0F, -0.5F, 1, 5, 1);
		RightFootHarness.setRotationPoint(-4F, 0F, 0.5F);
		Saddle.addChild(RightFootHarness);

		RightFootRing = new ModelRenderer(this, 101, 31);
		RightFootRing.addBox(0F, 0F, 0F, 1, 2, 2);
		RightFootRing.setRotationPoint(-0.5F, 5F, -1F);
		RightFootHarness.addChild(RightFootRing);

		StorageChest = new ModelRenderer(this, 32, 59);
		StorageChest.addBox(-5F, -2F, -2.5F, 10, 4, 5);
		StorageChest.setRotationPoint(0F, -2F, 5.5F);
		setRotation(StorageChest, -90F / radianF, 0F, 0F);
		Abdomen.addChild(StorageChest);

		LeftUpperLeg = new ModelRenderer(this, 0, 96);
		LeftUpperLeg.addBox(-1.5F, 0F, -2F, 3, 7, 4);
		LeftUpperLeg.setRotationPoint(3.99F, 3F, -7F);
		setRotation(LeftUpperLeg, 15F / radianF, 0F, 0F);
		Chest.addChild(LeftUpperLeg);

		LeftLowerLeg = new ModelRenderer(this, 0, 107);
		LeftLowerLeg.addBox(-1.5F, 0F, -1.5F, 3, 6, 3);
		LeftLowerLeg.setRotationPoint(-0.01F, 6.5F, 0.2F);
		setRotation(LeftLowerLeg, -21.5F / radianF, 0F, 0F);
		LeftUpperLeg.addChild(LeftLowerLeg);

		LeftFrontFoot = new ModelRenderer(this, 0, 116);
		LeftFrontFoot.addBox(-2F, 0F, -2F, 4, 2, 4);
		LeftFrontFoot.setRotationPoint(0F, 5F, -1.0F);
		setRotation(LeftFrontFoot, 6.5F / radianF, 0F, 0F);
		LeftLowerLeg.addChild(LeftFrontFoot);

		LeftClaw1 = new ModelRenderer(this, 16, 125);
		LeftClaw1.addBox(-0.5F, 0F, -0.5F, 1, 1, 2);
		LeftClaw1.setRotationPoint(-1.3F, 1.2F, -3.0F);
		setRotation(LeftClaw1, 45F / radianF, 0F, -1F / radianF);
		LeftFrontFoot.addChild(LeftClaw1);

		LeftClaw2 = new ModelRenderer(this, 16, 125);
		LeftClaw2.addBox(-0.5F, 0F, -0.5F, 1, 1, 2);
		LeftClaw2.setRotationPoint(0F, 1.1F, -3F);
		setRotation(this.LeftClaw2, 45F / radianF, 0F, 0F);
		LeftFrontFoot.addChild(LeftClaw2);

		LeftClaw3 = new ModelRenderer(this, 16, 125);
		LeftClaw3.addBox(-0.5F, 0F, -0.5F, 1, 1, 2);
		LeftClaw3.setRotationPoint(1.3F, 1.2F, -3F);
		setRotation(LeftClaw3, 45F / radianF, 0F, 1F / radianF);
		LeftFrontFoot.addChild(LeftClaw3);

		RightUpperLeg = new ModelRenderer(this, 14, 96);
		RightUpperLeg.addBox(-1.5F, 0F, -2F, 3, 7, 4);
		RightUpperLeg.setRotationPoint(-3.99F, 3F, -7F);
		setRotation(RightUpperLeg, 15F / radianF, 0F, 0F);
		Chest.addChild(RightUpperLeg);

		RightLowerLeg = new ModelRenderer(this, 12, 107);
		RightLowerLeg.addBox(-1.5F, 0F, -1.5F, 3, 6, 3);
		RightLowerLeg.setRotationPoint(0.01F, 6.5F, 0.2F);
		setRotation(RightLowerLeg, -21.5F / radianF, 0F, 0F);
		RightUpperLeg.addChild(RightLowerLeg);

		RightFrontFoot = new ModelRenderer(this, 0, 122);
		RightFrontFoot.addBox(-2F, 0F, -2F, 4, 2, 4);
		RightFrontFoot.setRotationPoint(0F, 5F, -1.0F);
		setRotation(RightFrontFoot, 6.5F / radianF, 0F, 0F);
		RightLowerLeg.addChild(RightFrontFoot);

		RightClaw1 = new ModelRenderer(this, 16, 125);
		RightClaw1.addBox(-0.5F, 0F, -0.5F, 1, 1, 2);
		RightClaw1.setRotationPoint(-1.3F, 1.2F, -3.0F);
		setRotation(this.RightClaw1, 45F / radianF, 0F, -1F / radianF);
		RightFrontFoot.addChild(RightClaw1);

		RightClaw2 = new ModelRenderer(this, 16, 125);
		RightClaw2.addBox(-0.5F, 0F, -0.5F, 1, 1, 2);
		RightClaw2.setRotationPoint(0F, 1.1F, -3F);
		setRotation(this.RightClaw2, 45F / radianF, 0F, 0F);
		RightFrontFoot.addChild(RightClaw2);

		RightClaw3 = new ModelRenderer(this, 16, 125);
		RightClaw3.addBox(-0.5F, 0F, -0.5F, 1, 1, 2);
		RightClaw3.setRotationPoint(1.3F, 1.2F, -3F);
		setRotation(RightClaw3, 45F / radianF, 0F, 1F / radianF);
		RightFrontFoot.addChild(RightClaw3);

		LeftHindUpperLeg = new ModelRenderer(this, 0, 67);
		LeftHindUpperLeg.addBox(-2F, -1.0F, -1.5F, 3, 8, 5);
		LeftHindUpperLeg.setRotationPoint(3F, 3F, 6.8F);
		setRotation(LeftHindUpperLeg, -25F / radianF, 0F, 0F);
		Abdomen.addChild(LeftHindUpperLeg);

		LeftAnkle = new ModelRenderer(this, 0, 80);
		LeftAnkle.addBox(-1F, 0F, -1.5F, 2, 3, 3);
		LeftAnkle.setRotationPoint(-0.5F, 4F, 5F);
		LeftHindUpperLeg.addChild(LeftAnkle);

		LeftHindLowerLeg = new ModelRenderer(this, 0, 86);
		LeftHindLowerLeg.addBox(-1F, 0F, -1F, 2, 3, 2);
		LeftHindLowerLeg.setRotationPoint(0F, 3F, 0.5F);
		LeftAnkle.addChild(this.LeftHindLowerLeg);

		LeftHindFoot = new ModelRenderer(this, 0, 91);
		LeftHindFoot.addBox(-1.5F, 0F, -1.5F, 3, 2, 3);
		LeftHindFoot.setRotationPoint(0F, 2.6F, -0.8F);
		setRotation(LeftHindFoot, 27F / radianF, 0F, 0F);
		LeftHindLowerLeg.addChild(LeftHindFoot);

		RightHindUpperLeg = new ModelRenderer(this, 16, 67);
		RightHindUpperLeg.addBox(-2F, -1F, -1.5F, 3, 8, 5);
		RightHindUpperLeg.setRotationPoint(-2F, 3F, 6.8F);
		setRotation(RightHindUpperLeg, -25F / radianF, 0F, 0F);
		Abdomen.addChild(RightHindUpperLeg);

		RightAnkle = new ModelRenderer(this, 10, 80);
		RightAnkle.addBox(-1F, 0F, -1.5F, 2, 3, 3);
		RightAnkle.setRotationPoint(-0.5F, 4F, 5F);
		RightHindUpperLeg.addChild(RightAnkle);

		RightHindLowerLeg = new ModelRenderer(this, 8, 86);
		RightHindLowerLeg.addBox(-1F, 0F, -1F, 2, 3, 2);
		RightHindLowerLeg.setRotationPoint(0F, 3F, 0.5F);
		RightAnkle.addChild(RightHindLowerLeg);

		RightHindFoot = new ModelRenderer(this, 12, 91);
		RightHindFoot.addBox(-1.5F, 0F, -1.5F, 3, 2, 3);
		RightHindFoot.setRotationPoint(0F, 2.6F, -0.8F);
		setRotation(RightHindFoot, 27F / radianF, 0F, 0F);
		RightHindLowerLeg.addChild(RightHindFoot);

	}

	public void updateAnimationModifiers(Entity entity) {
		EntityBigCat bigcat = (EntityBigCat) entity;
		isSaddled = bigcat.isRideable();
		onAir = bigcat.isOnAir();
		openMouthCounter = bigcat.mouthCounter;
		isRidden = (bigcat.isBeingRidden());
		hasMane = bigcat.hasMane();
		isSitting = bigcat.isSitting();
		movingTail = bigcat.tailCounter != 0;
		hasSaberTeeth = bigcat.hasSaberTeeth();
		hasChest = bigcat.isChested();
		isMovingVertically = bigcat.motionY != 0;
	}

	@Override
	public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		updateAnimationModifiers(entity);
		setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
		renderSaddle(isSaddled);
		renderMane(hasMane);
		//renderCollar(this.isTamed);
		renderTeeth(hasSaberTeeth);
		renderChest(hasChest);

		GlStateManager.pushMatrix();
		//GL11.glTranslatef(0F, yOffset, 0F);

		Chest.render(scale);

		if (isSaddled && isRidden) {
			LeftHarness.render(scale);
			RightHarness.render(scale);
		}

		GlStateManager.popMatrix();
	}

	private void renderTeeth(boolean flag) {
		LeftFang.isHidden = !flag;
		RightFang.isHidden = !flag;
	}

	/*private void renderCollar(boolean flag) {
		Collar.isHidden = !flag;
	}*/

	private void renderSaddle(boolean flag) {
		NeckHarness.isHidden = !flag;
		HarnessStick.isHidden = !flag;
		Saddle.isHidden = !flag;
	}

	private void renderMane(boolean flag) {
		Mane.isHidden = !flag;
		LeftChinBeard.isHidden = !flag;
		RightChinBeard.isHidden = !flag;
		ForeheadHair.isHidden = !flag;
		NeckHair.isHidden = !flag;
		ChinHair.isHidden = !flag;
	}

	private void renderChest(boolean flag) {
		StorageChest.isHidden = !flag;
	}

	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

	public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {

		//f = time
		//f1 = movement speed!
		//f2 = ??timer!
		//f3 = Head Y movement or rotation yaw
		//f4 = Head X movement or rotation Pitch
		//f5 = ?

		float RLegXRot = MathHelper.cos((limbSwing * 0.8F) + 3.141593F) * 0.8F * limbSwingAmount;
		float LLegXRot = MathHelper.cos(limbSwing * 0.8F) * 0.8F * limbSwingAmount;
		float gallopRLegXRot = MathHelper.cos((limbSwing * 0.6F) + 3.141593F) * 0.8F * limbSwingAmount;
		float gallopLLegXRot = MathHelper.cos(limbSwing * 0.6F) * 0.8F * limbSwingAmount;

		//TODO sync with leg movement speed
		Tail2.rotateAngleY = movingTail ? MathHelper.cos(ageInTicks * 0.3F) : 0.0f;

		if (isSitting) {
			Chest.rotationPointY = 14F;
			Abdomen.rotateAngleX = -10F / radianF;
			Chest.rotateAngleX = -45F / radianF;
			RightUpperLeg.rotateAngleX = (35F / radianF);
			RightLowerLeg.rotateAngleX = 5F / radianF;
			LeftUpperLeg.rotateAngleX = (35F / radianF);
			LeftLowerLeg.rotateAngleX = 5F / radianF;
			NeckBase.rotateAngleX = 20F / radianF;
			RightHindUpperLeg.rotationPointY = 1F;
			RightHindUpperLeg.rotateAngleX = -50F / radianF;
			LeftHindUpperLeg.rotationPointY = 1F;
			LeftHindUpperLeg.rotateAngleX = -50F / radianF;
			RightHindFoot.rotateAngleX = 90F / radianF;
			LeftHindFoot.rotateAngleX = 90F / radianF;
			TailRoot.rotateAngleX = 100F / radianF;
			Tail2.rotateAngleX = 35F / radianF;
			Tail3.rotateAngleX = 10F / radianF;
			NeckHair.rotationPointY = 2F;

			Collar.rotateAngleX = 0F / radianF;
			Collar.rotationPointY = 7F;
			Collar.rotationPointZ = -4F;
		}

		else {
			Chest.rotationPointY = 8F;
			Abdomen.rotateAngleX = 0F;
			Chest.rotateAngleX = 0F;
			NeckBase.rotateAngleX = -14F / radianF;
			TailRoot.rotateAngleX = 87F / radianF;
			Tail2.rotateAngleX = -30F / radianF;
			Tail3.rotateAngleX = -17F / radianF;
			RightLowerLeg.rotateAngleX = -21.5F / radianF;
			LeftLowerLeg.rotateAngleX = -21.5F / radianF;
			RightHindUpperLeg.rotationPointY = 3F;
			LeftHindUpperLeg.rotationPointY = 3F;
			RightHindFoot.rotateAngleX = 27F / radianF;
			LeftHindFoot.rotateAngleX = 27F / radianF;
			Collar.rotationPointZ = -2F;
			NeckHair.rotationPointY = -0.5F;
			Collar.rotationPointY = hasMane ? 9.0f : 6.0f;
			Collar.rotateAngleX = (20F / radianF) + MathHelper.cos(limbSwing * 0.8F) * 0.5F * limbSwingAmount;

			boolean galloping = (limbSwingAmount >= 0.97F);
			if (onAir) {
				if (isMovingVertically) {//only when moving up or down
					RightUpperLeg.rotateAngleX = (-35F / radianF);
					LeftUpperLeg.rotateAngleX = (-35F / radianF);
					RightHindUpperLeg.rotateAngleX = (35F / radianF);//LLegXRot;
					LeftHindUpperLeg.rotateAngleX = (35F / radianF);//RLegXRot;
				}
			} else {
				if (galloping) {

					RightUpperLeg.rotateAngleX = (15F / radianF) + gallopRLegXRot;//RLegXRot;
					LeftUpperLeg.rotateAngleX = (15F / radianF) + gallopRLegXRot;//LLegXRot;
					RightHindUpperLeg.rotateAngleX = (-25F / radianF) + gallopLLegXRot;//LLegXRot;
					LeftHindUpperLeg.rotateAngleX = (-25F / radianF) + gallopLLegXRot;//RLegXRot;

					Abdomen.rotateAngleY = 0F;
				} else {
					RightUpperLeg.rotateAngleX = (15F / radianF) + RLegXRot;//rLegRotFinal;//RLegXRot;
					LeftHindUpperLeg.rotateAngleX = (-25F / radianF) + RLegXRot;//rLegRotFinal;//RLegXRot;
					LeftUpperLeg.rotateAngleX = (15F / radianF) + LLegXRot;//lLegRotFinal;//LLegXRot;
					RightHindUpperLeg.rotateAngleX = (-25F / radianF) + LLegXRot;//lLegRotFinal;//LLegXRot;
				}
			}

			if (isRidden) {
				LeftFootHarness.rotateAngleX = -60 / radianF;
				RightFootHarness.rotateAngleX = -60 / radianF;
			} else {
				LeftFootHarness.rotateAngleX = RLegXRot / 3F;
				RightFootHarness.rotateAngleX = RLegXRot / 3F;

				LeftFootHarness.rotateAngleZ = RLegXRot / 5F;
				RightFootHarness.rotateAngleZ = -RLegXRot / 5F;
			}

			float TailXRot = MathHelper.cos(limbSwing * 0.4F) * 0.15F * limbSwingAmount;

			TailRoot.rotateAngleX = (87F / radianF) + TailXRot;
			Tail2.rotateAngleX = (-30F / radianF) + TailXRot;
			Tail3.rotateAngleX = (-17F / radianF) + TailXRot;
			Tail4.rotateAngleX = (21F / radianF) + TailXRot;
			TailTip.rotateAngleX = (21F / radianF) + TailXRot;
			TailTusk.rotateAngleX = (21F / radianF) + TailXRot;

		}//end of not sitting

		float HeadXRot = (headPitch / 57.29578F);

		/**
		 * f = distance walked f1 = speed 0 - 1 f2 = timer
		 */

		this.HeadBack.rotateAngleX = 14F / radianF + HeadXRot;
		this.HeadBack.rotateAngleY = (netHeadYaw / 57.29578F);

		float mouthMov = 0F;
		if (openMouthCounter != 0) {
			if (openMouthCounter < 10) {
				mouthMov = 22 + (openMouthCounter * 3);
			} else if (openMouthCounter > 20) {
				mouthMov = 22 + (90 - (openMouthCounter * 3));
			} else {
				mouthMov = 55F;
			}
		}
		LowerJaw.rotateAngleX = mouthMov / radianF;

		if (isSaddled) {
			LeftHarness.rotateAngleX = 25F / radianF + HeadBack.rotateAngleX;
			LeftHarness.rotateAngleY = HeadBack.rotateAngleY;
			RightHarness.rotateAngleX = 25F / radianF + HeadBack.rotateAngleX;
			RightHarness.rotateAngleY = HeadBack.rotateAngleY;
		}
		
	}
	
}

