package at.tyron.vintagecraft.Block;

import java.util.List;
import java.util.Random;

import at.tyron.vintagecraft.VintageCraft;
import at.tyron.vintagecraft.BlockClass.BaseBlockClass;
import at.tyron.vintagecraft.BlockClass.BlockClassEntry;
import at.tyron.vintagecraft.BlockClass.PropertyBlockClass;
import at.tyron.vintagecraft.Interfaces.IMultiblock;
import at.tyron.vintagecraft.Item.ItemRockTyped;
import at.tyron.vintagecraft.Item.ItemStone;
import at.tyron.vintagecraft.World.BlocksVC;
import at.tyron.vintagecraft.World.ItemsVC;
import at.tyron.vintagecraft.WorldProperties.Terrain.EnumRockType;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;


public class BlockRock extends BlockVC implements IMultiblock {
	public PropertyBlockClass ROCKTYPE;
	
	// Does the block fall like sand?
	public boolean unstable;
	
	
    public BlockRock() {
        this(Material.rock);
	}
    
	public BlockRock(Material materialIn) {
		super(materialIn);
		setCreativeTab(VintageCraft.terrainTab);
	}

	
	public void init(BlockClassEntry []subtypes, PropertyBlockClass property) {
		this.subtypes = subtypes;
		setTypeProperty(property);
		
		blockState = this.createBlockState();
	
		setDefaultState(subtypes[0].getBlockState(blockState.getBaseState(), getTypeProperty()));
	}

	
	public boolean isFreeFloating(World world, BlockPos pos) {
		return 
			!world.isSideSolid(pos.up(), EnumFacing.DOWN) &&
			!world.isSideSolid(pos.down(), EnumFacing.UP) &&
			!world.isSideSolid(pos.east(), EnumFacing.WEST) &&
			!world.isSideSolid(pos.west(), EnumFacing.EAST) && 
			!world.isSideSolid(pos.north(), EnumFacing.SOUTH) &&
			!world.isSideSolid(pos.south(), EnumFacing.NORTH)
		;
	}


	@Override
	public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
		if (isFreeFloating(worldIn, pos) && getBlockClass() == BlocksVC.rock) {
			worldIn.destroyBlock(pos, true);
		}
		
		super.onNeighborBlockChange(worldIn, pos, state, neighborBlock);
	}
	
    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
    	List<ItemStack> ret = new java.util.ArrayList<ItemStack>();
    	
    	if (getBlockClass() == BlocksVC.rock && !isFreeFloating((World) world, pos)) {
	        Random rand = world instanceof World ? ((World)world).rand : RANDOM;
	
	        EnumRockType rocktype = (EnumRockType) getRockType(state).getKey();
	        
	        ItemStack itemstack = new ItemStack(ItemsVC.stone, 2 + rand.nextInt(3));
	        ItemStone.setRockType(itemstack, rocktype);
	        
	        ret.add(itemstack);
	        
	        if ((rocktype == EnumRockType.LIMESTONE || rocktype == EnumRockType.CHALK) && rand.nextInt(9) == 0) {
	        	ret.add(new ItemStack(Items.flint, 1));
	        }
	        
			return ret;
    	}

    	
    	
    	ItemStack itemstack = new ItemStack(Item.getItemFromBlock(this));
    	ItemRockTyped.withRockTypeType(itemstack, getRockType(state));
        ret.add(itemstack);
        
    	return ret;
    }
    
    
	public BlockClassEntry getRockType(IBlockState state) {
		return (BlockClassEntry)state.getValue(getTypeProperty());
	}
    
   
    @Override
    protected BlockState createBlockState() {
    	if (getTypeProperty() != null) {
    		return new BlockState(this, new IProperty[] {getTypeProperty()});
    	}
    	return new BlockState(this, new IProperty[0]);
    }
	
	
    @Override
    public int getMetaFromState(IBlockState state) {
    	return getBlockClass().getMetaFromState(state);
    }
      
    public IBlockState getStateFromMeta(int meta) {
    	return getBlockClass().getEntryFromMeta(this, meta).getBlockState();
    }

    


	
	@Override
	public void getSubBlocks(Item itemIn, CreativeTabs tab, List list) {
		for (BlockClassEntry entry : subtypes) {
			list.add(entry.getItemStack());
		}
		super.getSubBlocks(itemIn, tab, list);
	}

	@Override
	public int multistateAvailableTypes() {
		return 16;
	}

	@Override
	public IProperty getTypeProperty() {
		return ROCKTYPE;
	}

	@Override
	public void setTypeProperty(PropertyBlockClass property) {
		ROCKTYPE = property;
	}

	@Override
	public BaseBlockClass getBlockClass() {
		return BlocksVC.rock;
	}
	
		
	public float getBlockHardnessMultiplier(IBlockState state) {
		if (!BlocksVC.rock.containsBlock(state.getBlock())) return 1f;
		
		BlockClassEntry rockclass = getRockType(state);
		EnumRockType rocktype = (EnumRockType)rockclass.getKey();
		return rocktype.getHardnessMultiplier();
	}
    
    
    public float getExplosionResistance(Entity exploder) {
        return this.blockResistance * 1.5f;
    }

    
}
