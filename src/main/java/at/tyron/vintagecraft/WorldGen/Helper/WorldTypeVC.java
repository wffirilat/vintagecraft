package at.tyron.vintagecraft.WorldGen.Helper;

import at.tyron.vintagecraft.World.VCraftWorld;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.WorldChunkManager;

public class WorldTypeVC extends WorldType {
	public static WorldTypeVC DEFAULT = new WorldTypeVC(0, "VCDefault");
	public static WorldTypeVC FLAT = new WorldTypeVC(1, "VCFlat");
	
	
	public WorldTypeVC(String name) {
		super(name);
	}

	public WorldTypeVC(int i, String par2Str) {
		super(par2Str);
		int oldid = worldTypeId;
		
		this.worldTypeId = i;
		worldTypes[oldid] = null;
		worldTypes[i] = this;
	}
	
	
	@Override
	public WorldChunkManager getChunkManager(World world) {
		if (this == FLAT) {
			return new WorldChunkManagerFlatVC(world);
		}
		return new WorldChunkManagerVC(world);
	}
	
	
	@Override
	public int getMinimumSpawnHeight(World world) {
		return VCraftWorld.seaLevel;
	}
	
	
	@Override
	public void onCustomizeButton(Minecraft mc, GuiCreateWorld guiCreateWorld) {
		// TODO Auto-generated method stub
		super.onCustomizeButton(mc, guiCreateWorld);
	}
	
	
}
