package at.tyron.vintagecraft.block;

import java.util.List;
import java.util.Random;

import at.tyron.vintagecraft.VCraftWorld;
import at.tyron.vintagecraft.World.BlocksVC;
import at.tyron.vintagecraft.World.ItemsVC;
import at.tyron.vintagecraft.WorldProperties.EnumTallGrass;
import at.tyron.vintagecraft.interfaces.ISoil;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.ColorizerGrass;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeColorHelper;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


public class BlockTallGrass extends BlockVC implements IPlantable {
	public static final PropertyEnum GRASSTYPE = PropertyEnum.create("grasstype", EnumTallGrass.class);
	
	public BlockTallGrass() {
		super(Material.plants);
		setCreativeTab(CreativeTabs.tabDecorations);
		this.setDefaultState(this.blockState.getBaseState().withProperty(GRASSTYPE, EnumTallGrass.LONG));
		this.setTickRandomly(true);
		float f = 0.5F;
	    this.setBlockBounds(
	    		0.5F - f, 0.0F, 0.5F - f, 
	    		0.5F + f, f, 0.5F + f
	    );
	}
	
	
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return null;
    }
    
   /* @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
    	super.breakBlock(worldIn, pos, state);
    	
    	Item item = null;
    	if (worldIn.rand.nextInt(10) == 0) {
    		//if (worldIn.rand.nextInt(25) == 0) item = Items.carrot;
    		//else if (worldIn.rand.nextInt(25) == 0) item = Items.potato;
    		//else 
    		item = Items.wheat_seeds;
    	}
    	
    	if (item != null) {
    		ItemStack itemstack = new ItemStack(item);
    		spawnAsEntity(worldIn, pos, itemstack);
    	}
    }*/

    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List list) {
    	for (EnumTallGrass grass : EnumTallGrass.values()) {
    		list.add(new ItemStack(itemIn, 1, grass.getMetaData(this)));
    	}
    }

    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(GRASSTYPE, EnumTallGrass.fromMeta(meta));
    }


    public int getMetaFromState(IBlockState state) {
        return ((EnumTallGrass)state.getValue(GRASSTYPE)).getMetaData(this);
    }

    protected BlockState createBlockState() {
        return new BlockState(this, new IProperty[] {GRASSTYPE});
    }
    
    
    
    @Override
    @SideOnly(Side.CLIENT)
    public int getBlockColor() {
        return ColorizerGrass.getGrassColor(0.5D, 1.0D);
    }

	@Override
    @SideOnly(Side.CLIENT)
    public int getRenderColor(IBlockState state) {
        return this.getBlockColor();
    }

	@Override
    @SideOnly(Side.CLIENT)
    public int colorMultiplier(IBlockAccess worldIn, BlockPos pos, int renderPass) {
		return VCraftWorld.instance.getGrassColorAtPos(pos);
    }
	

    
	
	
    @SideOnly(Side.CLIENT)
    public Block.EnumOffsetType getOffsetType() {
        return Block.EnumOffsetType.XZ;
    }
    

    @Override
    public boolean isReplaceable(World worldIn, BlockPos pos) {
    	return true;
    }
    
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        return super.canPlaceBlockAt(worldIn, pos) && worldIn.getBlockState(pos.down()).getBlock().canSustainPlant(worldIn, pos.down(), net.minecraft.util.EnumFacing.UP, this);
    }

    protected boolean canPlaceBlockOn(Block ground) {
        return ground instanceof ISoil && ((ISoil)ground).canGrowGrass(null, null);
    }

    public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
        super.onNeighborBlockChange(worldIn, pos, state, neighborBlock);
        this.checkAndDropBlock(worldIn, pos, state);
    }

    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        this.checkAndDropBlock(worldIn, pos, state);
    }

    protected void checkAndDropBlock(World worldIn, BlockPos pos, IBlockState state) {
        if (!this.canBlockStay(worldIn, pos, state)) {
            this.dropBlockAsItem(worldIn, pos, state, 0);
            worldIn.setBlockState(pos, Blocks.air.getDefaultState(), 3);
        }
    }

    public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state) {
        return this.canPlaceBlockOn(worldIn.getBlockState(pos.down()).getBlock());
    }

    public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state) {
        return null;
    }

    public boolean isOpaqueCube() {
        return false;
    }

    public boolean isFullCube() {
        return false;
    }

    @SideOnly(Side.CLIENT)
    public EnumWorldBlockLayer getBlockLayer() {
        return EnumWorldBlockLayer.CUTOUT;
    }

    
    
    @Override
    public net.minecraftforge.common.EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
        return net.minecraftforge.common.EnumPlantType.Plains;
    }
    @Override
    public IBlockState getPlant(IBlockAccess world, BlockPos pos) {
        return this.getDefaultState();
    }

}