package at.tyron.vintagecraft.Network;

import at.tyron.vintagecraft.Interfaces.IItemStoneWorkable;
import at.tyron.vintagecraft.Inventory.ContainerStonecutter;
import at.tyron.vintagecraft.World.Crafting.EnumMasonryTechnique;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class MasonryTechniquePacket implements IMessage {
	EnumMasonryTechnique masonryTechnique;
	
	public MasonryTechniquePacket() {
	}
	
	public MasonryTechniquePacket(EnumMasonryTechnique masonryTechnique) {
		this.masonryTechnique = masonryTechnique;
	}

	
	
	@Override
	public void fromBytes(ByteBuf buf) {
		masonryTechnique = EnumMasonryTechnique.fromId(buf.readInt());
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(masonryTechnique.getId());
	}
	

    public static class Handler implements IMessageHandler<MasonryTechniquePacket, IMessage> {
		@Override
		public IMessage onMessage(MasonryTechniquePacket message, MessageContext ctx) {
			if (ctx.side == Side.CLIENT) return null;
			
			
			EntityPlayerMP player = ctx.getServerHandler().playerEntity;
			
			if (player.openContainer instanceof ContainerStonecutter) {
				ContainerStonecutter container = (ContainerStonecutter) player.openContainer;
    			
    			ItemStack itemstack = container.getSlot(0).getStack();
    			if (itemstack == null || !(itemstack.getItem() instanceof IItemStoneWorkable)) {
    				container.detectAndSendChanges();
    				return null;
    			}
    			
    			IItemStoneWorkable workableItem = (IItemStoneWorkable)itemstack.getItem();	        
    	        
    	        player.addExhaustion(1F); 
    	        
    	        /*ItemStack carpenterToolBox = container.getSlot(3).getStack();
    	        
    	        
    	        if (carpenterToolBox != null) {
    	        	carpenterToolBox.damageItem(1, player);
    	        	
    	        	if (message.carpentryTechnique == EnumMasonryTechnique.JOIN) {
    	        		carpenterToolBox.damageItem(2, player);
    	        	}

    	        	if (carpenterToolBox.stackSize == 0) {
    	        		container.getSlot(3).putStack(null);
    	        		player.worldObj.playSoundEffect(player.posX, player.posY, player.posZ, "random.break", 1f, 1f);
    	        	}
    	        }*/
    	        
    	        
    	        if (container.teTable.onTableUse(player)) {
    	        	workableItem.applyTechnique(itemstack, message.masonryTechnique);
    	        	container.checkCraftable();	
    	        }
    		}
			
			return null;
		}
    }

}
