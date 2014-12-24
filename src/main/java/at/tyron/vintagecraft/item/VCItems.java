package at.tyron.vintagecraft.item;

import at.tyron.vintagecraft.ModInfo;
import at.tyron.vintagecraft.World.EnumMaterialDeposit;
import at.tyron.vintagecraft.World.EnumRockType;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class VCItems {
	public static Item stone;
	public static Item ore;
	
	public static void init() {
		stone = new ItemStone().register("stone");
		
		for (EnumRockType rocktype : EnumRockType.values()) {
			ModelBakery.addVariantName(stone, ModInfo.ModID + ":stone/" + rocktype.getName());	
		}
		
		ore = new ItemOre().register("ore");
		for (EnumMaterialDeposit oretype : EnumMaterialDeposit.values()) {
			if (oretype.hasOre) {
				ModelBakery.addVariantName(ore, ModInfo.ModID + ":ore/" + oretype.getName());
			}
		}
		
	}
	

	
}

