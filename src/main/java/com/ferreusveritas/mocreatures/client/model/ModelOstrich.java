
package com.ferreusveritas.mocreatures.client.model;

import com.ferreusveritas.mocreatures.entity.passive.EntityOstrich;
import com.ferreusveritas.mocreatures.entity.passive.EntityOstrich.OstrichHelmetType;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelOstrich extends ModelBase {
	
	public ModelOstrich() {
		textureWidth = 128;
		textureHeight = 128; //64
		
		UBeak = new ModelRenderer(this, 12, 16);
		UBeak.addBox(-1.5F, -15F, -5.5F, 3, 1, 1);
		UBeak.setRotationPoint(0F, 3F, -6F);
		
		UBeak2 = new ModelRenderer(this, 20, 16);
		UBeak2.addBox(-1F, -15F, -7.5F, 2, 1, 2);
		UBeak2.setRotationPoint(0F, 3F, -6F);
		
		UBeakb = new ModelRenderer(this, 12, 16);
		UBeakb.addBox(-1.5F, -15F, -6.5F, 3, 1, 1);
		UBeakb.setRotationPoint(0F, 3F, -6F);
		setRotation(UBeakb, -0.0698132F, 0F, 0F);
		
		UBeak2b = new ModelRenderer(this, 20, 16);
		UBeak2b.addBox(-1F, -15F, -8.5F, 2, 1, 2);
		UBeak2b.setRotationPoint(0F, 3F, -6F);
		setRotation(UBeak2b, -0.0698132F, 0F, 0F);
		
		LBeak = new ModelRenderer(this, 12, 22);
		LBeak.addBox(-1.5F, -14F, -5.5F, 3, 1, 1);
		LBeak.setRotationPoint(0F, 3F, -6F);
		
		LBeakb = new ModelRenderer(this, 12, 22);
		LBeakb.addBox(-1.5F, -14F, -3.9F, 3, 1, 1);
		LBeakb.setRotationPoint(0F, 3F, -6F);
		setRotation(LBeakb, 0.122173F, 0F, 0F);
		
		LBeak2 = new ModelRenderer(this, 20, 22);
		LBeak2.addBox(-1F, -14F, -7.5F, 2, 1, 2);
		LBeak2.setRotationPoint(0F, 3F, -6F);
		
		LBeak2b = new ModelRenderer(this, 20, 22);
		LBeak2b.addBox(-1F, -14F, -5.9F, 2, 1, 2);
		LBeak2b.setRotationPoint(0F, 3F, -6F);
		setRotation(LBeak2b, 0.122173F, 0F, 0F);
		
		Body = new ModelRenderer(this, 0, 38);
		Body.addBox(-4F, 1F, 0F, 8, 10, 16);
		Body.setRotationPoint(0F, 0F, -6F);
		
		LLegA = new ModelRenderer(this, 50, 28);
		LLegA.addBox(-2F, -1F, -2.5F, 4, 6, 5);
		LLegA.setRotationPoint(4F, 5F, 4F);
		setRotation(LLegA, 0.1745329F, 0F, 0F);
		
		LLegB = new ModelRenderer(this, 50, 39);
		LLegB.addBox(-1.5F, 5F, -1.5F, 3, 4, 3);
		LLegB.setRotationPoint(4F, 5F, 4F);
		setRotation(LLegB, 0.1745329F, 0F, 0F);
		
		LLegC = new ModelRenderer(this, 8, 38);
		LLegC.addBox(-1F, 8F, 2.5F, 2, 10, 2);
		LLegC.setRotationPoint(4F, 5F, 4F);
		setRotation(LLegC, -0.2617994F, 0F, 0F);
		
		LFoot = new ModelRenderer(this, 32, 42);
		LFoot.addBox(-1F, 17F, -9F, 2, 1, 5);
		LFoot.setRotationPoint(4F, 5F, 4F);
		setRotation(LFoot, 0.1745329F, 0F, 0F);
		
		RLegA = new ModelRenderer(this, 0, 27);
		RLegA.addBox(-2F, -1F, -2.5F, 4, 6, 5);
		RLegA.setRotationPoint(-4F, 5F, 4F);
		setRotation(RLegA, 0.1745329F, 0F, 0F);
		
		RLegB = new ModelRenderer(this, 18, 27);
		RLegB.addBox(-1.5F, 5F, -1.5F, 3, 4, 3);
		RLegB.setRotationPoint(-4F, 5F, 4F);
		setRotation(RLegB, 0.1745329F, 0F, 0F);
		
		RLegC = new ModelRenderer(this, 0, 38);
		RLegC.addBox(-1F, 8F, 2.5F, 2, 10, 2);
		RLegC.setRotationPoint(-4F, 5F, 4F);
		setRotation(RLegC, -0.2617994F, 0F, 0F);
		
		RFoot = new ModelRenderer(this, 32, 48);
		RFoot.addBox(-1F, 17F, -9F, 2, 1, 5);
		RFoot.setRotationPoint(-4F, 5F, 4F);
		setRotation(RFoot, 0.1745329F, 0F, 0F);
		
		Tail1 = new ModelRenderer(this, 44, 18);
		Tail1.addBox(-0.5F, -2F, -2F, 1, 4, 6);
		Tail1.setRotationPoint(0F, 4F, 15F);
		setRotation(Tail1, 0.3490659F, 0F, 0F);
		
		Tail2 = new ModelRenderer(this, 58, 18);
		Tail2.addBox(-2.6F, -2F, -2F, 1, 4, 6);
		Tail2.setRotationPoint(0F, 4F, 15F);
		setRotation(Tail2, 0.3490659F, -0.2617994F, 0F);
		
		Tail3 = new ModelRenderer(this, 30, 18);
		Tail3.addBox(1.6F, -2F, -2F, 1, 4, 6);
		Tail3.setRotationPoint(0F, 4F, 15F);
		setRotation(Tail3, 0.3490659F, 0.2617994F, 0F);
		
		LWingB = new ModelRenderer(this, 68, 46);
		LWingB.addBox(-0.5F, -3F, 0F, 1, 4, 14);
		LWingB.setRotationPoint(4F, 4F, -3F);
		setRotation(LWingB, 0.0872665F, 0.0872665F, 0F);
		
		LWingC = new ModelRenderer(this, 98, 46);
		LWingC.addBox(-1F, 0F, 0F, 1, 4, 14);
		LWingC.setRotationPoint(4F, 4F, -3F);
		setRotation(LWingC, 0F, 0.0872665F, 0F);
		
		RWingB = new ModelRenderer(this, 68, 0);
		RWingB.addBox(-0.5F, -3F, 0F, 1, 4, 14);
		RWingB.setRotationPoint(-4F, 4F, -3F);
		setRotation(RWingB, 0.0872665F, -0.0872665F, 0F);
		
		RWingC = new ModelRenderer(this, 98, 0);
		RWingC.addBox(0F, 0F, 0F, 1, 4, 14);
		RWingC.setRotationPoint(-4F, 4F, -3F);
		setRotation(RWingC, 0F, -0.0872665F, 0F);
		
		SaddleA = new ModelRenderer(this, 72, 18);
		SaddleA.addBox(-4F, 0.5F, -3F, 8, 1, 8);
		SaddleA.setRotationPoint(0F, 0F, 0F);
		SaddleB = new ModelRenderer(this, 72, 27);
		
		SaddleB.addBox(-1.5F, 0F, -3F, 3, 1, 2);
		SaddleB.setRotationPoint(0F, 0F, 0F);
		
		SaddleL = new ModelRenderer(this, 72, 30);
		SaddleL.addBox(-0.5F, 0F, -0.5F, 1, 6, 1);
		SaddleL.setRotationPoint(4F, 1F, 0F);
		
		SaddleR = new ModelRenderer(this, 84, 30);
		SaddleR.addBox(-0.5F, 0F, -0.5F, 1, 6, 1);
		SaddleR.setRotationPoint(-4F, 1F, 0F);
		
		SaddleL2 = new ModelRenderer(this, 76, 30);
		SaddleL2.addBox(-0.5F, 6F, -1F, 1, 2, 2);
		SaddleL2.setRotationPoint(4F, 1F, 0F);
		
		SaddleR2 = new ModelRenderer(this, 88, 30);
		SaddleR2.addBox(-0.5F, 6F, -1F, 1, 2, 2);
		SaddleR2.setRotationPoint(-4F, 1F, 0F);
		
		SaddleC = new ModelRenderer(this, 84, 27);
		SaddleC.addBox(-4F, 0F, 3F, 8, 1, 2);
		SaddleC.setRotationPoint(0F, 0F, 0F);
		
		NeckD = new ModelRenderer(this, 0, 16);
		NeckD.addBox(-1.5F, -4F, -2F, 3, 8, 3);
		NeckD.setRotationPoint(0F, 3F, -6F);
		setRotation(NeckD, 0.4363323F, 0F, 0F);
		
		Saddlebag = new ModelRenderer(this, 32, 7);
		Saddlebag.addBox(-4.5F, -3F, 5F, 9, 4, 7);
		Saddlebag.setRotationPoint(0F, 0F, 0F);
		setRotation(Saddlebag, -0.2602503F, 0F, 0F);
		
		Flagpole = new ModelRenderer(this, 28, 0);
		Flagpole.addBox(-0.5F, -15F, -0.5F, 1, 17, 1);
		Flagpole.setRotationPoint(0F, 0F, 5F);
		setRotation(Flagpole, -0.2602503F, 0F, 0F);
		
		FlagBlack = new ModelRenderer(this, 108, 8);
		FlagBlack.addBox(0F, -2.1F, 0F, 0, 4, 10);
		FlagBlack.setRotationPoint(0F, -12F, 8F);
		setRotation(FlagBlack, -0.2602503F, 0F, 0F);
		
		FlagDarkGrey = new ModelRenderer(this, 108, 12);
		FlagDarkGrey.addBox(0F, -2.1F, 0F, 0, 4, 10);
		FlagDarkGrey.setRotationPoint(0F, -12F, 8F);
		setRotation(FlagDarkGrey, -0.2602503F, 0F, 0F);
		
		FlagYellow = new ModelRenderer(this, 48, 46);
		FlagYellow.addBox(0F, -2.1F, 0F, 0, 4, 10);
		FlagYellow.setRotationPoint(0F, -12F, 8F);
		setRotation(FlagYellow, -0.2602503F, 0F, 0F);
		
		FlagBrown = new ModelRenderer(this, 48, 42);
		FlagBrown.addBox(0F, -2.1F, 0F, 0, 4, 10);
		FlagBrown.setRotationPoint(0F, -12F, 8F);
		setRotation(FlagBrown, -0.2602503F, 0F, 0F);
		
		FlagGreen = new ModelRenderer(this, 48, 38);
		FlagGreen.addBox(0F, -2.1F, 0F, 0, 4, 10);
		FlagGreen.setRotationPoint(0F, -12F, 8F);
		setRotation(FlagGreen, -0.2602503F, 0F, 0F);
		
		FlagCyan = new ModelRenderer(this, 48, 50);
		FlagCyan.addBox(0F, -2.1F, 0F, 0, 4, 10);
		FlagCyan.setRotationPoint(0F, -12F, 8F);
		setRotation(FlagCyan, -0.2602503F, 0F, 0F);
		
		FlagLightBlue = new ModelRenderer(this, 68, 32);
		FlagLightBlue.addBox(0F, -2.1F, 0F, 0, 4, 10);
		FlagLightBlue.setRotationPoint(0F, -12F, 8F);
		setRotation(FlagLightBlue, -0.2602503F, 0F, 0F);
		
		FlagDarkBlue = new ModelRenderer(this, 68, 28);
		FlagDarkBlue.addBox(0F, -2.1F, 0F, 0, 4, 10);
		FlagDarkBlue.setRotationPoint(0F, -12F, 8F);
		setRotation(FlagDarkBlue, -0.2602503F, 0F, 0F);
		
		FlagPurple = new ModelRenderer(this, 88, 32);
		FlagPurple.addBox(0F, -2.1F, 0F, 0, 4, 10);
		FlagPurple.setRotationPoint(0F, -12F, 8F);
		setRotation(FlagPurple, -0.2602503F, 0F, 0F);
		
		FlagDarkPurple = new ModelRenderer(this, 88, 28);
		FlagDarkPurple.addBox(0F, -2.1F, 0F, 0, 4, 10);
		FlagDarkPurple.setRotationPoint(0F, -12F, 8F);
		setRotation(FlagDarkPurple, -0.2602503F, 0F, 0F);
		
		FlagDarkGreen = new ModelRenderer(this, 108, 32);
		FlagDarkGreen.addBox(0F, -2.1F, 0F, 0, 4, 10);
		FlagDarkGreen.setRotationPoint(0F, -12F, 8F);
		setRotation(FlagDarkGreen, -0.2602503F, 0F, 0F);
		
		FlagLightRed = new ModelRenderer(this, 108, 28);
		FlagLightRed.addBox(0F, -2.1F, 0F, 0, 4, 10);
		FlagLightRed.setRotationPoint(0F, -12F, 8F);
		setRotation(FlagLightRed, -0.2602503F, 0F, 0F);
		
		FlagRed = new ModelRenderer(this, 108, 24);
		FlagRed.addBox(0F, -2.1F, 0F, 0, 4, 10);
		FlagRed.setRotationPoint(0F, -12F, 8F);
		setRotation(FlagRed, -0.2602503F, 0F, 0F);
		
		FlagWhite = new ModelRenderer(this, 108, 20);
		FlagWhite.addBox(0F, -2.1F, 0F, 0, 4, 10);
		FlagWhite.setRotationPoint(0F, -12F, 8F);
		setRotation(FlagWhite, -0.2602503F, 0F, 0F);
		
		FlagGrey = new ModelRenderer(this, 108, 16);
		FlagGrey.addBox(0F, -2.1F, 0F, 0, 4, 10);
		FlagGrey.setRotationPoint(0F, -12F, 8F);
		setRotation(FlagGrey, -0.2602503F, 0F, 0F);
		
		FlagOrange = new ModelRenderer(this, 88, 24);
		FlagOrange.addBox(0F, -2.1F, 0F, 0, 4, 10);
		FlagOrange.setRotationPoint(0F, -12F, 8F);
		setRotation(FlagOrange, -0.2602503F, 0F, 0F);
		
		NeckU = new ModelRenderer(this, 20, 0);
		NeckU.addBox(-1F, -12F, -4F, 2, 5, 2);
		NeckU.setRotationPoint(0F, 3F, -6F);
		
		NeckL = new ModelRenderer(this, 20, 7);
		NeckL.addBox(-1F, -8F, -2.5F, 2, 5, 2);
		NeckL.setRotationPoint(0F, 3F, -6F);
		setRotation(NeckL, 0.2007129F, 0F, 0F);
		
		NeckHarness = new ModelRenderer(this, 0, 11);
		NeckHarness.addBox(-2F, -3F, -2.5F, 4, 1, 4);
		NeckHarness.setRotationPoint(0F, 3F, -6F);
		setRotation(NeckHarness, 0.4363323F, 0F, 0F);
		
		NeckHarness2 = new ModelRenderer(this, 84, 55);
		NeckHarness2.addBox(-3F, -2.5F, -2F, 6, 1, 1);
		NeckHarness2.setRotationPoint(0F, 3F, -6F);
		
		NeckHarnessRight = new ModelRenderer(this, 84, 45);
		NeckHarnessRight.addBox(-2.3F, -3.5F, -0.5F, 0, 3, 12);
		NeckHarnessRight.setRotationPoint(0F, 3F, -6F);
		setRotation(NeckHarnessRight, 0.8983798F, 0F, 0F);
		
		NeckHarnessLeft = new ModelRenderer(this, 84, 45);
		NeckHarnessLeft.addBox(2.3F, -3.5F, -0.5F, 0, 3, 12);
		NeckHarnessLeft.setRotationPoint(0F, 3F, -6F);
		setRotation(NeckHarnessLeft, 0.8983798F, 0F, 0F);
		
		Head = new ModelRenderer(this, 0, 0);
		Head.addBox(-1.5F, -16F, -4.5F, 3, 4, 3);
		Head.setRotationPoint(0F, 3F, -6F);
		
		HelmetLeather = new ModelRenderer(this, 66, 0);
		HelmetLeather.addBox(-2F, -16.5F, -5F, 4, 5, 4);
		HelmetLeather.setRotationPoint(0F, 3F, -6F);
		
		HelmetIron = new ModelRenderer(this, 84, 46);
		HelmetIron.addBox(-2F, -16.5F, -5F, 4, 5, 4);
		HelmetIron.setRotationPoint(0F, 3F, -6F);
		
		HelmetGold = new ModelRenderer(this, 112, 64);
		HelmetGold.addBox(-2F, -16.5F, -5F, 4, 5, 4);
		HelmetGold.setRotationPoint(0F, 3F, -6F);
		
		HelmetDiamond = new ModelRenderer(this, 96, 64);
		HelmetDiamond.addBox(-2F, -16.5F, -5F, 4, 5, 4);
		HelmetDiamond.setRotationPoint(0F, 3F, -6F);
		
		HelmetHide = new ModelRenderer(this, 96, 5);
		HelmetHide.addBox(-2F, -16.5F, -5F, 4, 5, 4);
		HelmetHide.setRotationPoint(0F, 3F, -6F);
		
		HelmetNeckHide = new ModelRenderer(this, 58, 0);
		HelmetNeckHide.addBox(-1.5F, -12F, -4.5F, 3, 1, 3);
		HelmetNeckHide.setRotationPoint(0F, 3F, -6F);
		
		HelmetHideEar1 = new ModelRenderer(this, 84, 9);
		HelmetHideEar1.addBox(-2.5F, -18F, -3F, 2, 2, 1);
		HelmetHideEar1.setRotationPoint(0F, 3F, -6F);
		//setRotation(HelmetHideEar1, 0F, 0F, 0.4363323F);
		
		HelmetHideEar2 = new ModelRenderer(this, 90, 9);
		HelmetHideEar2.addBox(0.5F, -18F, -3F, 2, 2, 1);
		HelmetHideEar2.setRotationPoint(0F, 3F, -6F);
		//setRotation(HelmetHideEar2, 0F, 0F, -0.4363323F);
		
		HelmetFur = new ModelRenderer(this, 84, 0);
		HelmetFur.addBox(-2F, -16.5F, -5F, 4, 5, 4);
		HelmetFur.setRotationPoint(0F, 3F, -6F);
		
		HelmetNeckFur = new ModelRenderer(this, 96, 0);
		HelmetNeckFur.addBox(-1.5F, -12F, -4.5F, 3, 1, 3);
		HelmetNeckFur.setRotationPoint(0F, 3F, -6F);
		
		HelmetFurEar1 = new ModelRenderer(this, 66, 9);
		HelmetFurEar1.addBox(-2.5F, -18F, -3F, 2, 2, 1);
		HelmetFurEar1.setRotationPoint(0F, 3F, -6F);
		
		HelmetFurEar2 = new ModelRenderer(this, 76, 9);
		HelmetFurEar2.addBox(0.5F, -18F, -3F, 2, 2, 1);
		HelmetFurEar2.setRotationPoint(0F, 3F, -6F);
		
		HelmetReptile = new ModelRenderer(this, 64, 64);
		HelmetReptile.addBox(-2F, -16.5F, -5F, 4, 5, 4);
		HelmetReptile.setRotationPoint(0F, 3F, -6F);
		
		HelmetReptileEar2 = new ModelRenderer(this, 114, 45);
		HelmetReptileEar2.addBox(2.5F, -16.5F, -2F, 0, 5, 5);
		HelmetReptileEar2.setRotationPoint(0F, 3F, -6F);
		setRotation(HelmetReptileEar2, 0F, 0.6108652F, 0F);
		
		HelmetReptileEar1 = new ModelRenderer(this, 114, 50);
		HelmetReptileEar1.addBox(-2.5F, -16.5F, -2F, 0, 5, 5);
		HelmetReptileEar1.setRotationPoint(0F, 3F, -6F);
		setRotation(HelmetReptileEar1, 0F, -0.6108652F, 0F);
		
		Tail = new ModelRenderer(this, 30, 28);
		Tail.addBox(-2.5F, -1F, 0F, 5, 5, 5);
		Tail.setRotationPoint(0F, 4F, 10F);
	}
	
	@Override
	public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		
		EntityOstrich entityostrich = (EntityOstrich) entity;
		boolean openMouth = (entityostrich.mouthCounter != 0);
		boolean isSaddled = entityostrich.isRideable();
		boolean isHiding = entityostrich.getHiding();
		boolean wingFlap = (entityostrich.wingCounter != 0);
		boolean bagged = entityostrich.isChested();
		boolean rider = (entityostrich.isBeingRidden());
		int jumpCounter = entityostrich.jumpCounter;
		boolean floating = entityostrich.isOnAir();
		
		helmet = entityostrich.getHelmet();
		flagColor = entityostrich.getFlagColor();
		setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, isHiding, wingFlap, rider, jumpCounter, floating);
		
		Head.render(scale);
		
		NeckU.render(scale);
		NeckD.render(scale);
		NeckL.render(scale);
		Body.render(scale);
		Tail.render(scale);
		LLegA.render(scale);
		LLegB.render(scale);
		LLegC.render(scale);
		LFoot.render(scale);
		RLegA.render(scale);
		RLegB.render(scale);
		RLegC.render(scale);
		RFoot.render(scale);
		
		LWingB.render(scale);
		LWingC.render(scale);
		RWingB.render(scale);
		RWingC.render(scale);
		
		
		Tail1.render(scale);
		Tail2.render(scale);
		Tail3.render(scale);
		
		
		if (openMouth) {
			UBeakb.render(scale);
			UBeak2b.render(scale);
			LBeakb.render(scale);
			LBeak2b.render(scale);
		} else {
			UBeak.render(scale);
			UBeak2.render(scale);
			LBeak.render(scale);
			LBeak2.render(scale);
		}
		
		if (isSaddled) {
			SaddleA.render(scale);
			SaddleB.render(scale);
			SaddleC.render(scale);
			SaddleL.render(scale);
			SaddleR.render(scale);
			SaddleL2.render(scale);
			SaddleR2.render(scale);
			NeckHarness.render(scale);
			NeckHarness2.render(scale);
			if (rider) {
				NeckHarnessLeft.render(scale);
				NeckHarnessRight.render(scale);
			}
		}
		
		if (bagged) {
			Saddlebag.render(scale);
			Flagpole.render(scale);
			switch (flagColor) {
				case 0: FlagWhite.render(scale); break;
				case 1: FlagOrange.render(scale); break;
				case 2: FlagPurple.render(scale); break;
				case 3: FlagLightBlue.render(scale); break;
				case 4: FlagYellow.render(scale); break;
				case 5: FlagGreen.render(scale); break;
				case 6: FlagLightRed.render(scale); break;
				case 7: FlagDarkGrey.render(scale); break;
				case 8: FlagGrey.render(scale); break;
				case 9: FlagCyan.render(scale); break;
				case 10: FlagDarkPurple.render(scale); break;
				case 11: FlagDarkBlue.render(scale); break;
				case 12: FlagBrown.render(scale); break;
				case 13: FlagDarkGreen.render(scale); break;
				case 14: FlagRed.render(scale); break;
				case 15: FlagBlack.render(scale); break;
				default: break;
			}
		}
		
		switch (helmet) {
			case Leather:
				HelmetLeather.render(scale);
				break;
			case Iron:
				HelmetIron.render(scale);
				break;
			case Golden:
				HelmetGold.render(scale);
				break;
			case Diamond:
				HelmetDiamond.render(scale);
				break;
			case Hide:
				HelmetHide.render(scale);
				HelmetNeckHide.render(scale);
				HelmetHideEar1.render(scale);
				HelmetHideEar2.render(scale);
				break;
			case Fur:
				HelmetFur.render(scale);
				HelmetNeckFur.render(scale);
				HelmetFurEar1.render(scale);
				HelmetFurEar2.render(scale);
				break;
			case Reptile:
				HelmetReptile.render(scale);
				HelmetReptileEar1.render(scale);
				HelmetReptileEar2.render(scale);
				break;
			default:
				break;
		}
		
	}
	
	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}
	
	public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, boolean hiding, boolean wing, boolean rider,
			int jumpCounter, boolean floating) {
		float LLegXRot = MathHelper.cos(f * 0.4F) * 1.1F * f1;
		float RLegXRot = MathHelper.cos((f * 0.4F) + 3.141593F) * 1.1F * f1;
		
		if (hiding) {
			Head.rotationPointY = 9.0F;
			Head.rotateAngleX = 2.61799F;
			Head.rotateAngleY = 0.0F;
			
		} else {
			Head.rotationPointY = 3.0F;
			Head.rotateAngleX = (RLegXRot / 20F) + (-f4 / (180F / (float) Math.PI));
			Head.rotateAngleY = f3 / (180F / (float) Math.PI);
		}
		
		if (rider) {
			if (floating) {
				Head.rotateAngleX = 0F;
			} else {
				Head.rotateAngleX = (RLegXRot / 20F);
			}
		}
		
		UBeak.rotationPointY = Head.rotationPointY;
		UBeakb.rotationPointY = Head.rotationPointY;
		UBeak2.rotationPointY = Head.rotationPointY;
		UBeak2b.rotationPointY = Head.rotationPointY;
		LBeak.rotationPointY = Head.rotationPointY;
		LBeakb.rotationPointY = Head.rotationPointY;
		LBeak2.rotationPointY = Head.rotationPointY;
		LBeak2b.rotationPointY = Head.rotationPointY;
		NeckU.rotationPointY = Head.rotationPointY;
		NeckD.rotationPointY = Head.rotationPointY;
		NeckL.rotationPointY = Head.rotationPointY;
		
		UBeak.rotateAngleX = Head.rotateAngleX;
		UBeak.rotateAngleY = Head.rotateAngleY;
		UBeak2.rotateAngleX = Head.rotateAngleX;
		UBeak2.rotateAngleY = Head.rotateAngleY;
		LBeak.rotateAngleX = Head.rotateAngleX;
		LBeak.rotateAngleY = Head.rotateAngleY;
		LBeak2.rotateAngleX = Head.rotateAngleX;
		LBeak2.rotateAngleY = Head.rotateAngleY;
		NeckU.rotateAngleX = Head.rotateAngleX;
		NeckU.rotateAngleY = Head.rotateAngleY;
		NeckD.rotateAngleX = 0.4363323F + Head.rotateAngleX;
		NeckD.rotateAngleY = Head.rotateAngleY;
		NeckL.rotateAngleX = (11.5F / radianF) + Head.rotateAngleX;
		NeckL.rotateAngleY = Head.rotateAngleY;
		
		UBeakb.rotateAngleX = -0.0698132F + Head.rotateAngleX;
		UBeakb.rotateAngleY = Head.rotateAngleY;
		UBeak2b.rotateAngleX = -0.0698132F + Head.rotateAngleX;
		UBeak2b.rotateAngleY = Head.rotateAngleY;
		
		LBeakb.rotateAngleX = (7F / radianF) + Head.rotateAngleX;
		LBeakb.rotateAngleY = Head.rotateAngleY;
		LBeak2b.rotateAngleX = (7F / radianF) + Head.rotateAngleX;
		LBeak2b.rotateAngleY = Head.rotateAngleY;
		
		NeckHarness.rotationPointY = Head.rotationPointY;
		NeckHarness2.rotationPointY = Head.rotationPointY;
		NeckHarnessLeft.rotationPointY = Head.rotationPointY;
		NeckHarnessRight.rotationPointY = Head.rotationPointY;
		
		NeckHarness.rotateAngleX = (25F / radianF) + Head.rotateAngleX;
		NeckHarness.rotateAngleY = Head.rotateAngleY;
		NeckHarness2.rotateAngleX = Head.rotateAngleX;
		NeckHarness2.rotateAngleY = Head.rotateAngleY;
		NeckHarnessLeft.rotateAngleX = (50F / radianF) + Head.rotateAngleX;
		NeckHarnessLeft.rotateAngleY = Head.rotateAngleY;
		NeckHarnessRight.rotateAngleX = (50F / radianF) + Head.rotateAngleX;
		NeckHarnessRight.rotateAngleY = Head.rotateAngleY;
		
		//helmets
		
		switch (helmet) {
			case Leather:
				HelmetLeather.rotationPointY = Head.rotationPointY;
				HelmetLeather.rotateAngleX = Head.rotateAngleX;
				HelmetLeather.rotateAngleY = Head.rotateAngleY;
				break;
			case Iron:
				HelmetIron.rotationPointY = Head.rotationPointY;
				HelmetIron.rotateAngleX = Head.rotateAngleX;
				HelmetIron.rotateAngleY = Head.rotateAngleY;
				break;
			case Golden:
				HelmetGold.rotationPointY = Head.rotationPointY;
				HelmetGold.rotateAngleX = Head.rotateAngleX;
				HelmetGold.rotateAngleY = Head.rotateAngleY;
				break;
			case Diamond:
				HelmetDiamond.rotationPointY = Head.rotationPointY;
				HelmetDiamond.rotateAngleX = Head.rotateAngleX;
				HelmetDiamond.rotateAngleY = Head.rotateAngleY;
				break;
			case Hide:
				HelmetHide.rotationPointY = Head.rotationPointY;
				HelmetHide.rotateAngleX = Head.rotateAngleX;
				HelmetHide.rotateAngleY = Head.rotateAngleY;
				HelmetNeckHide.rotationPointY = Head.rotationPointY;
				HelmetNeckHide.rotateAngleX = Head.rotateAngleX;
				HelmetNeckHide.rotateAngleY = Head.rotateAngleY;
				HelmetHideEar1.rotationPointY = Head.rotationPointY;
				HelmetHideEar1.rotateAngleX = Head.rotateAngleX;
				HelmetHideEar1.rotateAngleY = Head.rotateAngleY;
				HelmetHideEar2.rotationPointY = Head.rotationPointY;
				HelmetHideEar2.rotateAngleX = Head.rotateAngleX;
				HelmetHideEar2.rotateAngleY = Head.rotateAngleY;
				break;
			case Fur:
				HelmetFur.rotationPointY = Head.rotationPointY;
				HelmetFur.rotateAngleX = Head.rotateAngleX;
				HelmetFur.rotateAngleY = Head.rotateAngleY;
				HelmetNeckFur.rotationPointY = Head.rotationPointY;
				HelmetNeckFur.rotateAngleX = Head.rotateAngleX;
				HelmetNeckFur.rotateAngleY = Head.rotateAngleY;
				HelmetFurEar1.rotationPointY = Head.rotationPointY;
				HelmetFurEar1.rotateAngleX = Head.rotateAngleX;
				HelmetFurEar1.rotateAngleY = Head.rotateAngleY;
				HelmetFurEar2.rotationPointY = Head.rotationPointY;
				HelmetFurEar2.rotateAngleX = Head.rotateAngleX;
				HelmetFurEar2.rotateAngleY = Head.rotateAngleY;
				break;
			case Reptile:
				HelmetReptile.rotationPointY = Head.rotationPointY;
				HelmetReptile.rotateAngleX = Head.rotateAngleX;
				HelmetReptile.rotateAngleY = Head.rotateAngleY;
				HelmetReptileEar1.rotationPointY = Head.rotationPointY;
				HelmetReptileEar1.rotateAngleX = Head.rotateAngleX;
				HelmetReptileEar1.rotateAngleY = (-35F / radianF) + Head.rotateAngleY;
				HelmetReptileEar2.rotationPointY = Head.rotationPointY;
				HelmetReptileEar2.rotateAngleX = Head.rotateAngleX;
				HelmetReptileEar2.rotateAngleY = (35F / radianF) + Head.rotateAngleY;
				break;
			default:
				break;
		}
		
		//flag
		float flagF = MathHelper.cos(f * 0.8F) * 0.1F * f1;
		
		switch (flagColor) {
			case 1:
				FlagOrange.rotateAngleY = flagF;
				break;
			case 2:
				FlagPurple.rotateAngleY = flagF;
				break;
			case 3:
				FlagLightBlue.rotateAngleY = flagF;
				break;
			case 4:
				FlagYellow.rotateAngleY = flagF;
				break;
			case 5:
				FlagGreen.rotateAngleY = flagF;
				break;
			case 6:
				FlagLightRed.rotateAngleY = flagF;
				break;
			case 7:
				FlagDarkGrey.rotateAngleY = flagF;
				break;
			case 8:
				FlagGrey.rotateAngleY = flagF;
				break;
			case 9:
				FlagCyan.rotateAngleY = flagF;
				break;
			case 10:
				FlagDarkPurple.rotateAngleY = flagF;
				break;
			case 11:
				FlagDarkBlue.rotateAngleY = flagF;
				break;
			case 12:
				FlagBrown.rotateAngleY = flagF;
				break;
			case 13:
				FlagDarkGreen.rotateAngleY = flagF;
				break;
			case 14:
				FlagRed.rotateAngleY = flagF;
				break;
			case 15:
				FlagBlack.rotateAngleY = flagF;
				break;
			case 16:
				FlagWhite.rotateAngleY = flagF;
				break;
		}
		
		//legs
		
		LLegC.rotationPointY = 5F;
		LLegC.rotationPointZ = 4F;
		RLegC.rotationPointY = 5F;
		RLegC.rotationPointZ = 4F;
		LFoot.rotationPointY = 5F;
		LFoot.rotationPointZ = 4F;
		RFoot.rotationPointY = 5F;
		RFoot.rotationPointZ = 4F;
		
		LLegA.rotateAngleX = 0.1745329F + LLegXRot;
		LLegB.rotateAngleX = LLegA.rotateAngleX;
		LLegC.rotateAngleX = -0.2617994F + +LLegXRot;
		LFoot.rotateAngleX = LLegA.rotateAngleX;
		RLegA.rotateAngleX = 0.1745329F + RLegXRot;
		RLegB.rotateAngleX = RLegA.rotateAngleX;
		RLegC.rotateAngleX = -0.2617994F + +RLegXRot;
		RFoot.rotateAngleX = RLegA.rotateAngleX;
		
		
		//wings
		float wingF = 0F;
		/**
		 * f = distance walked f1 = speed 0 - 1 f2 = timer
		 */
		
		wingF = (10F / radianF) + MathHelper.cos(f * 0.6F) * 0.2F * f1;
		if (wing) {
			wingF += (50 / 57.29578F);
		}
		LWingB.rotateAngleY = 0.0872665F + wingF;
		LWingC.rotateAngleY = 0.0872665F + wingF;
		
		RWingB.rotateAngleY = -0.0872665F - wingF;
		RWingC.rotateAngleY = -0.0872665F - wingF;
		
		LWingB.rotateAngleX = 0.0872665F + (RLegXRot / 10F);
		LWingC.rotateAngleX = (RLegXRot / 10F);
		
		RWingB.rotateAngleX = 0.0872665F + (RLegXRot / 10F);
		RWingC.rotateAngleX = (RLegXRot / 10F);
		
		if (rider) {
			SaddleL.rotateAngleX = -60F / radianF;
			SaddleL2.rotateAngleX = SaddleL.rotateAngleX;
			SaddleR.rotateAngleX = -60F / radianF;
			SaddleR2.rotateAngleX = SaddleR.rotateAngleX;
			
			SaddleL.rotateAngleZ = -40F / radianF;
			SaddleL2.rotateAngleZ = SaddleL.rotateAngleZ;
			SaddleR.rotateAngleZ = 40F / radianF;
			SaddleR2.rotateAngleZ = SaddleR.rotateAngleZ;
		} else {
			SaddleL.rotateAngleX = RLegXRot / 3F;
			SaddleL2.rotateAngleX = RLegXRot / 3F;
			SaddleR.rotateAngleX = RLegXRot / 3F;
			SaddleR2.rotateAngleX = RLegXRot / 3F;
			
			SaddleL.rotateAngleZ = RLegXRot / 5F;
			SaddleL2.rotateAngleZ = RLegXRot / 5F;
			SaddleR.rotateAngleZ = -RLegXRot / 5F;
			SaddleR2.rotateAngleZ = -RLegXRot / 5F;
		}
		
	}
	
	ModelRenderer UBeak;
	ModelRenderer UBeak2;
	ModelRenderer UBeakb;
	ModelRenderer UBeak2b;
	ModelRenderer LBeak;
	ModelRenderer LBeakb;
	ModelRenderer LBeak2;
	ModelRenderer LBeak2b;
	ModelRenderer Body;
	ModelRenderer Tail;
	ModelRenderer LLegA;
	ModelRenderer LLegB;
	ModelRenderer LLegC;
	ModelRenderer LFoot;
	ModelRenderer RLegA;
	ModelRenderer RLegB;
	ModelRenderer RLegC;
	ModelRenderer RFoot;
	ModelRenderer Tail1;
	ModelRenderer Tail2;
	ModelRenderer Tail3;
	ModelRenderer LWingB;
	ModelRenderer LWingC;
	ModelRenderer RWingB;
	ModelRenderer RWingC;
	ModelRenderer SaddleA;
	ModelRenderer SaddleB;
	ModelRenderer SaddleL;
	ModelRenderer SaddleR;
	ModelRenderer SaddleL2;
	ModelRenderer SaddleR2;
	ModelRenderer SaddleC;
	ModelRenderer NeckD;
	ModelRenderer Saddlebag;
	ModelRenderer Flagpole;
	ModelRenderer FlagBlack;
	ModelRenderer FlagDarkGrey;
	ModelRenderer FlagYellow;
	ModelRenderer FlagBrown;
	ModelRenderer FlagGreen;
	ModelRenderer FlagCyan;
	ModelRenderer FlagLightBlue;
	ModelRenderer FlagDarkBlue;
	ModelRenderer FlagPurple;
	ModelRenderer FlagDarkPurple;
	ModelRenderer FlagDarkGreen;
	ModelRenderer FlagLightRed;
	ModelRenderer FlagRed;
	ModelRenderer FlagWhite;
	ModelRenderer FlagGrey;
	ModelRenderer FlagOrange;
	ModelRenderer NeckU;
	ModelRenderer NeckL;
	ModelRenderer NeckHarness;
	ModelRenderer NeckHarness2;
	ModelRenderer NeckHarnessRight;
	ModelRenderer NeckHarnessLeft;
	ModelRenderer Head;
	ModelRenderer HelmetLeather;
	ModelRenderer HelmetIron;
	ModelRenderer HelmetGold;
	ModelRenderer HelmetDiamond;
	ModelRenderer HelmetHide;
	ModelRenderer HelmetNeckHide;
	ModelRenderer HelmetHideEar1;
	ModelRenderer HelmetHideEar2;
	ModelRenderer HelmetFur;
	ModelRenderer HelmetNeckFur;
	ModelRenderer HelmetFurEar1;
	ModelRenderer HelmetFurEar2;
	ModelRenderer HelmetReptile;
	ModelRenderer HelmetReptileEar2;
	ModelRenderer HelmetReptileEar1;
	
	private OstrichHelmetType helmet;
	private int flagColor;
	private float radianF = (float) (180.0f / Math.PI);
}
