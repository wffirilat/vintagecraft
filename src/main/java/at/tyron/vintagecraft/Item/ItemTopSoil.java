package at.tyron.vintagecraft.Item;

import java.util.List;

import at.tyron.vintagecraft.Interfaces.ISubtypeFromStackPovider;
import at.tyron.vintagecraft.World.BlocksVC;
import at.tyron.vintagecraft.WorldProperties.Terrain.EnumFertility;
import at.tyron.vintagecraft.WorldProperties.Terrain.EnumOrganicLayer;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemTopSoil extends ItemBlockVC implements ISubtypeFromStackPovider {

	public ItemTopSoil(Block block) {
		super(block);
		setHasSubtypes(true);
	}

	
	@Override
	public void addInformation(ItemStack itemstack, EntityPlayer playerIn, List tooltip, boolean advanced) {
		tooltip.add(StatCollector.translateToLocal(EnumFertility.fromMeta((itemstack.getItemDamage() >> 2) & 3).getStateName() + ".name"));
		tooltip.add(StatCollector.translateToLocal(EnumOrganicLayer.fromMeta(itemstack.getItemDamage() & 3).getStateName() + ".name"));
	}
	
	
    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack stack, int renderPass) {
        return BlocksVC.topsoil.getRenderColor(BlocksVC.topsoil.getStateFromMeta(stack.getMetadata() & 3));
    }


	@Override
	public String getSubType(ItemStack itemstack) {
		EnumFertility fertility = EnumFertility.fromMeta((itemstack.getItemDamage() >> 2) & 3);
		EnumOrganicLayer organiclayer = EnumOrganicLayer.fromMeta(itemstack.getItemDamage() & 3);
		
		return fertility.getStateName() + "_" + organiclayer.getName();
	}
	
	
	@Override
	public int getMetadata(int damage) {
		return damage;
	}

	
}
