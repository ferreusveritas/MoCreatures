package com.ferreusveritas.mocreatures.network.message;

import com.ferreusveritas.mocreatures.entity.EntityAnimalComp;
import com.ferreusveritas.mocreatures.entity.IMoCEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MoCMessageEntityDive implements IMessage, IMessageHandler<MoCMessageEntityDive, IMessage> {
	
	public MoCMessageEntityDive() {
	}
	
	@Override
	public void toBytes(ByteBuf buffer) {
	}
	
	@Override
	public void fromBytes(ByteBuf buffer) {
	}
	
	@Override
	public IMessage onMessage(MoCMessageEntityDive message, MessageContext ctx) {
		if(ctx.getServerHandler().player.getRidingEntity() != null) {
			Entity ridingEntity = ctx.getServerHandler().player.getRidingEntity();
			if (ridingEntity instanceof EntityAnimalComp) {
				((EntityAnimalComp) ridingEntity).setDivePending(true);
			}
			else if (ridingEntity instanceof IMoCEntity) {
				((IMoCEntity) ridingEntity).makeEntityDive();
			}
		}
		return null;
	}
}
