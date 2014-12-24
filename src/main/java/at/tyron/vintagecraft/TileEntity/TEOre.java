package at.tyron.vintagecraft.TileEntity;

import at.tyron.vintagecraft.World.EnumMaterialDeposit;
import at.tyron.vintagecraft.World.EnumOreType;
import at.tyron.vintagecraft.World.EnumRockType;
import at.tyron.vintagecraft.block.BlockOreVC;
import at.tyron.vintagecraft.block.VCBlocks;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.common.property.Properties;

public class TEOre extends TileEntity { /* extends NetworkTileEntity {*/
	/*public EnumRockType rocktype;
	public EnumMaterialDeposit oretype;*/
	private IExtendedBlockState state;
	
	public static final IUnlistedProperty<Enum>[] properties = new IUnlistedProperty[2];

	static {
		properties[0] = Properties.toUnlisted(PropertyEnum.create("rocktype", EnumRockType.class));
		properties[1] = Properties.toUnlisted(PropertyEnum.create("oretype", EnumMaterialDeposit.class));
    }
	
	public TEOre() {
	//	this.shouldSendInitData = true;
		state = getState();
	}

	@Override
	public boolean canRenderBreaking() {
		return false;
	}
	


	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		
		setRockType(EnumRockType.byId(nbt.getInteger("rocktype")));
		setOreType(EnumMaterialDeposit.byId(nbt.getInteger("oretype")));
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)  {
		super.writeToNBT(nbt);
		
		nbt.setInteger("rocktype", getRockType().id);
		nbt.setInteger("oretype", getOreType().id);
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		writeToNBT(nbt);
		return new S35PacketUpdateTileEntity(pos, 1, nbt);
	}
	
	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		readFromNBT(pkt.getNbtCompound());
		worldObj.markBlockForUpdate(pos);
	}

	
	
	
	
	
	public EnumRockType getRockType() {
		return (EnumRockType)state.getValue(TEOre.properties[0]);
	}
	
	public EnumMaterialDeposit getOreType() {
		return (EnumMaterialDeposit)state.getValue(TEOre.properties[1]);
	}
	
	public TEOre setRockType(EnumRockType rocktype) {
		setState(getState().withProperty(TEOre.properties[0], rocktype));
		return this;
	}
	
	public TEOre setOreType(EnumMaterialDeposit oretype) {
		setState(getState().withProperty(TEOre.properties[1], oretype));
		return this;
	}
	
	
	
	/*@Override
	public void handleInitPacket(NBTTagCompound nbt) {
		setRockType(EnumRockType.byId(nbt.getInteger("rocktype")));
		setOreType(EnumMaterialDeposit.byId(nbt.getInteger("oretype")));
	}

	@Override
	public void handleDataPacket(NBTTagCompound nbt) {
		readFromNBT(nbt);
	}

	@Override
	public void createDataNBT(NBTTagCompound nbt) {
		System.out.println("create data nbt");
	}

	@Override
	public void createInitNBT(NBTTagCompound nbt) {
		nbt.setInteger("rocktype", getRockType().id);
		nbt.setInteger("oretype", getOreType().id);
	}
	*/
	

    public IExtendedBlockState getState() {
        if(state == null) {
            state = (IExtendedBlockState)VCBlocks.rawore.getDefaultState();
        }
        return state;
    }

    public void setState(IExtendedBlockState state) {
        this.state = state;
    }
	
}