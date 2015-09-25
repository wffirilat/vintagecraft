package at.tyron.vintagecraft.Client.Gui;

import java.util.ArrayList;

import at.tyron.vintagecraft.VintageCraft;
import at.tyron.vintagecraft.Interfaces.IItemStoneWorkable;
import at.tyron.vintagecraft.Network.MasonryTechniquePacket;
import at.tyron.vintagecraft.Network.SoundEffectToServerPacket;
import at.tyron.vintagecraft.TileEntity.TEStonecutter;
import at.tyron.vintagecraft.TileEntity.TENoGUIInventory;
import at.tyron.vintagecraft.World.Crafting.EnumMasonryTechnique;
import at.tyron.vintagecraft.World.Crafting.WorkableRecipeBase;
import at.tyron.vintagecraft.World.Crafting.WorkableRecipeManager;
import at.tyron.vintagecraft.WorldProperties.EnumWorkableTechnique;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class GuiStonecutter extends GuiWorkbench {
	private static final ResourceLocation textureCarpenterTable = new ResourceLocation("vintagecraft:textures/gui/container/gui_carpentertable.png");
	private static final ResourceLocation textureSelector = new ResourceLocation("vintagecraft:textures/gui/container/gui_selector.png");

	public GuiStonecutter(InventoryPlayer player, TENoGUIInventory te) {
		super(player, te);
	}
	
	public EnumWorkableTechnique[] getAppliedTechniques() {
        ItemStack itemstack = te.getStackInSlot(0);
        if (itemstack == null || !(itemstack.getItem() instanceof IItemStoneWorkable)) return null;
        IItemStoneWorkable stoneworkable = (IItemStoneWorkable)itemstack.getItem();
        return stoneworkable.getAppliedTechniques(itemstack);
	}


	@Override
	void initButtons() {
		int w = 13;
		int spacing = 14; 
		int basex = 69;
		int basey = 22;
		
        buttons.clear();
		buttons.add(new GuiButtonVC(156, 5, w, w, "recipehelper", this));
		
		buttons.add(new GuiButtonVC(basex, basey, w, w, "split", this, EnumMasonryTechnique.SPLIT));
		buttons.add(new GuiButtonVC(basex + spacing, basey, w, w, "carve", this, EnumMasonryTechnique.CARVE));
		buttons.add(new GuiButtonVC(basex + 2*spacing, basey, w, w, "drill", this, EnumMasonryTechnique.DRILL));
		buttons.add(new GuiButtonVC(basex, basey + spacing, w, w, "plane", this, EnumMasonryTechnique.PLANE));
		buttons.add(new GuiButtonVC(basex + spacing, basey + spacing, w, w, "saw", this, EnumMasonryTechnique.SAW));
		buttons.add(new GuiButtonVC(basex + 2*spacing, basey + spacing, w, w, "join", this, EnumMasonryTechnique.JOIN));
	}

	@Override
	void TechniqueHit(EnumWorkableTechnique technique) {
    	ItemStack hammer = inventorySlots.getSlot(3).getStack();
    	/*if (
    		hammer == null ||
    		!(hammer.getItem() instanceof ItemToolVC) || 
    		((ItemToolVC)hammer.getItem()).tooltype != EnumTool.CARPENTERSTOOLSET
    	) {
    		return;
    	}*/
    	
    	EnumMasonryTechnique masonrytechnique = (EnumMasonryTechnique)technique;
    	
    	
		this.mc.theWorld.playSound(
			mc.thePlayer.posX,
			mc.thePlayer.posY, 
			mc.thePlayer.posZ, 
			masonrytechnique.getSoundName(), 
			1f, 
			1f, 
			false
		);
		
		
		
		if (!inventorySlots.getSlot(2).getHasStack()) {
			VintageCraft.packetPipeline.sendToServer(new SoundEffectToServerPacket(0, 0, 0, masonrytechnique.getSoundName(), 1f));
			VintageCraft.packetPipeline.sendToServer(new MasonryTechniquePacket((EnumMasonryTechnique) technique));
		}
	}

	@Override
	int getCurrentTemplate() {
		return ((TEStonecutter)te).currentTemplate;
	}

	@Override
	void setCurrentTemplate(int template) {
		((TEStonecutter)te).currentTemplate = template;
	}

	@Override
	void bindWorkbenchGUITexture() {
		mc.getTextureManager().bindTexture(textureCarpenterTable);
	}

	@Override
	void bindSelectorGUITexture() {
		mc.getTextureManager().bindTexture(textureSelector);
	}

	@Override
	ArrayList<WorkableRecipeBase> getRecipes() {
		return WorkableRecipeManager.masonry.getRecipes();
	}
	
	@Override
	int buttonsPerRow() {
		return 3;
	}

}
