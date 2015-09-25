package at.tyron.vintagecraft.World.Crafting;

import at.tyron.vintagecraft.WorldProperties.EnumWorkableTechnique;
import net.minecraft.item.ItemStack;

public class MasonryRecipe extends WorkableRecipeBase {
	public MasonryRecipe(ItemStack output, ItemStack input, EnumWorkableTechnique[] techniques) {
		super(output, input, techniques);
	}

	public MasonryRecipe(ItemStack output, ItemStack input, EnumMasonryTechnique[] steps, boolean wildcardMatch) {
		super(output, input, steps, wildcardMatch);
	}

	public MasonryRecipe(ItemStack output, ItemStack input, ItemStack input2, EnumMasonryTechnique[] steps) {
		super(output, input, input2, steps);
	}


}
