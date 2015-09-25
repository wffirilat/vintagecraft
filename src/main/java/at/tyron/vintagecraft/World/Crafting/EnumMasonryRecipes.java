package at.tyron.vintagecraft.World.Crafting;

import java.util.Locale;

import at.tyron.vintagecraft.Item.ItemPlanksVC;
import at.tyron.vintagecraft.Item.ItemRock;
import at.tyron.vintagecraft.World.BlocksVC;
import at.tyron.vintagecraft.WorldProperties.Terrain.EnumRockType;
import at.tyron.vintagecraft.WorldProperties.Terrain.EnumTree;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public enum EnumMasonryRecipes {
	
	TEST_DIAMONDS(new EnumMasonryTechnique[] {
			EnumMasonryTechnique.PLANE,
			EnumMasonryTechnique.PLANE,
			EnumMasonryTechnique.PLANE,
			EnumMasonryTechnique.PLANE,
			EnumMasonryTechnique.PLANE,
			EnumMasonryTechnique.PLANE
			
	}, 16);
	
	EnumMasonryTechnique[] steps;
	int blocks;
	
	private EnumMasonryRecipes(EnumMasonryTechnique[] steps, int blocks) {
		this.steps = steps;
		this.blocks = blocks;
	}
	
	public static void registerRecipes() {
		for (EnumRockType rocktype : EnumRockType.values()) {
			ItemStack stack = BlocksVC.rock.getItemStackFor(rocktype);
			
			TEST_DIAMONDS.registerRecipe(new ItemStack(Items.diamond), stack);
		}
	}
	

	WorkableRecipeBase registerRecipe(ItemStack output, ItemStack input) {
		return registerRecipe(output, input, null);
	}
	
	WorkableRecipeBase registerRecipe(ItemStack output, ItemStack input, ItemStack input2) {
		MasonryRecipe recipe;
		
		if (input2 != null) {
			recipe = new MasonryRecipe(output, input.copy(), input2.copy(), steps);
		} else {
			recipe = new MasonryRecipe(output, input.copy(), steps);
			if (blocks > 0) {
				recipe.setIngredientText(blocks+" stone");
			}
		}
		
		if (input.getItem() instanceof ItemRock) {
			recipe.setDisplayInRecipeHelper(ItemRock.getRockType(input) == EnumRockType.GRANITE);
		} else {
			recipe.setDisplayInRecipeHelper(true);
		}
				
		recipe.setUnlocalizedName(name().toLowerCase(Locale.ROOT));
		WorkableRecipeManager.masonry.registerRecipe(recipe);
		return recipe;
	}

}
