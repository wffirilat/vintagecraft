package at.tyron.vintagecraft.TileEntity;

import at.tyron.vintagecraft.Interfaces.IItemStoneWorkable;
import at.tyron.vintagecraft.Interfaces.IItemWoodWorkable;
import at.tyron.vintagecraft.Inventory.ContainerStonecutter;
import at.tyron.vintagecraft.World.Crafting.WorkableRecipeBase;
import at.tyron.vintagecraft.World.Crafting.WorkableRecipeManager;
import at.tyron.vintagecraft.WorldProperties.EnumWorkableTechnique;
import at.tyron.vintagecraft.WorldProperties.Terrain.EnumTree;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;

public class TEStonecutter extends TENoGUIInventory {
	public EnumTree treeType;
	public int currentTemplate = -1;
	public EnumFacing orientation;

	public TEStonecutter() {
		storage = new ItemStack[getSizeInventory()];
		orientation = EnumFacing.NORTH;
		treeType = EnumTree.OAK;
	}
	
	
	public EnumTree getTreeType() {
		return treeType;
	}
	
	public boolean onTableUse(EntityPlayer entityplayer) {
		return true;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		treeType = EnumTree.byId(nbttagcompound.getInteger("treeType"));
		currentTemplate = nbttagcompound.getInteger("currenttemplate");

		int num = nbttagcompound.getInteger("orientation");
		orientation = num == -1 ? null : EnumFacing.values()[num];
		
		super.readFromNBT(nbttagcompound);
	}

	
	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		nbttagcompound.setInteger("treeType", treeType != null ? treeType.getId() : -1);
		nbttagcompound.setInteger("currenttemplate", currentTemplate);
		nbttagcompound.setInteger("orientation", orientation.ordinal());
		
		super.writeToNBT(nbttagcompound);
	}

	
	
	
	public void checkCraftable(ContainerStonecutter containerStonecutter) {
		ItemStack itemstack = containerStonecutter.getSlot(0).getStack();
		if (itemstack == null || !(itemstack.getItem() instanceof IItemStoneWorkable)) return;
		
		IItemStoneWorkable stoneworkable = (IItemStoneWorkable)itemstack.getItem();	        
        
        EnumWorkableTechnique []techniques = stoneworkable.getAppliedTechniques(itemstack);
        if (techniques.length == 0) return;
        
        
        
        ItemStack []input;
        if (containerStonecutter.getSlot(1).getStack() != null) {
        	input = new ItemStack[]{itemstack, containerStonecutter.getSlot(1).getStack()};
        } else {
        	input = new ItemStack[]{itemstack};
        }
        
		if (WorkableRecipeManager.masonry.isInvalidRecipe(techniques, input)) {
			worldObj.playSoundEffect(pos.getX(), pos.getY(), pos.getZ(), "vintagecraft:woodcrack", 1f, 1f);
			containerStonecutter.getSlot(2).putStack(null);
			containerStonecutter.getSlot(0).putStack(null);
		} else {
			WorkableRecipeBase recipe = WorkableRecipeManager.masonry.getMatchingRecipe(techniques, input);

			if (recipe != null) {
				//System.out.println("created item  " + recipe.output);
				containerStonecutter.getSlot(2).putStack(recipe.output.copy());
				containerStonecutter.getSlot(1).putStack(null);
				containerStonecutter.getSlot(0).putStack(null);
			}
			
			
				
			
		}
		
		containerStonecutter.detectAndSendChanges();
		
	}


	public void setTreeType(EnumTree treeType) {
		this.treeType = treeType;
	}



	
	
	
	@Override
	public int getSizeInventory() {
		return 4;
	}

	@Override
	public String getName() {
		return "stonecutter";
	}


	@Override
	public IChatComponent getDisplayName() {
		return new ChatComponentText("stonecutter");
	}


	public void setOrientation(EnumFacing facing) {
		this.orientation = facing;
		
	}


	public EnumFacing getOrientation() {
		return orientation;
	}

	
		
	
}
