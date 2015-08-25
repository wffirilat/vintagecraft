package at.tyron.vintagecraft;

import java.util.Random;

import at.tyron.vintagecraft.World.MechnicalNetworkManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;

public class VCraftWorldSavedData extends WorldSavedData {
	long worldTime;
	//int nightSkyType; // Now just seed % 4
	Random rand;
	NBTTagList networks;
	
	public World world;
	
	/*public int getNightSkyType(World world) {
		if (nightSkyType == -1) {
			nightSkyType = world.rand.nextInt(4);
		}
		return nightSkyType;
	}*/
	
	public VCraftWorldSavedData(String name) {
		super(name);
	}
	
	public void setWorld(World world) {
		this.world = world;
		MechnicalNetworkManager.addManager(world, networks);
	}

	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		worldTime = nbt.getLong("worldTime");
		
	/*	if (!nbt.hasKey("nightSkyType")) {
			nightSkyType = -1;	
		} else {
			nightSkyType = nbt.getInteger("nightSkyType");
		}
*/
		networks = (NBTTagList)nbt.getTag("mechanicalNetworks").copy();
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setLong("worldTime", worldTime);
	//	nbt.setInteger("nightSkyType", nightSkyType);
		
		MechnicalNetworkManager manager = MechnicalNetworkManager.getNetworkManagerForWorld(world);
		if (manager != null) {
			networks = manager.saveNetworksToTaglist();
		}
		
		nbt.setTag("mechanicalNetworks", networks);
	}
	
	public long getWorldTime() {
		return worldTime;
	}
	
	public void setWorldTime(long worldTime) {
		this.worldTime = worldTime;
		markDirty();
	}
	
	public NBTTagList getNetworks() {
		return networks;
	}


	public boolean isDirty() {
		return true;
	}
}
