package dark.detraismc.SPC_SF.implementation.block;

import dark.detraismc.SPC_SF.SPCItems;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.RecipeDisplayItem;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.AContainer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Uranium_Extractor extends AContainer implements RecipeDisplayItem {
	private List<ItemStack> recipeList;

	   public Uranium_Extractor() {
	        super(SPCItems.category_machine, SPCItems.URANIUM_EXTRACTOR, RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
                    SlimefunItems.ELECTRIC_ORE_GRINDER_3, SlimefunItems.ELECTRIC_ORE_GRINDER_3, SlimefunItems.ELECTRIC_ORE_GRINDER_3,
                    SlimefunItems.ELECTRIC_GOLD_PAN_3, SlimefunItems.ELECTRIC_DUST_WASHER_3, SlimefunItems.ENHANCED_AUTO_CRAFTER,
                    SPCItems.ARTIFACT_CIRCUIT_BOARD, SPCItems.ELITE_GENERATOR_3, SPCItems.ARTIFACT_CIRCUIT_BOARD
		    });
	    }

    @Override
    protected void registerDefaultRecipes() {
        this.recipeList = new ArrayList<>();

        // 1. Define all the valid input blocks your machine accepts
        Material[] validInputs = {
                Material.COBBLESTONE,
                Material.ANDESITE,
                Material.GRANITE,
                Material.DIORITE,
                Material.BLACKSTONE,
                Material.COBBLED_DEEPSLATE
        };

        // 2. Loop through each material to register the recipe and display
        for (Material inputType : validInputs) {

            // Register the functional recipe (Requires 8 of the block, outputs Uranium, takes 8 ticks/processing time)
            this.registerRecipe(6, new ItemStack[]{new ItemStack(inputType, 8)}, new ItemStack[]{SlimefunItems.URANIUM});

            // Add the items to the visual Slimefun Guide display list
            this.recipeList.add(new ItemStack(inputType, 8));
            this.recipeList.add(SlimefunItems.URANIUM);
        }
    }

	   @Override
	   public String getMachineIdentifier() {
	      return "SPC_URANIUM_EXTRACTOR";
	   }
	   
	   @Override
	    public int getEnergyConsumption() {
	        return 90;
	    }

	    @Override
	    public int getCapacity() {
	        return 1024;
	    }

	    @Override
	    public ItemStack getProgressBar() {
	        return new SlimefunItemStack(SlimefunItems.SMALL_URANIUM, 1);
	    }

	    @Override
	    public String getInventoryTitle() {
	        return "&2Uranium Extractor";
	    }
	    
	    public List<ItemStack> getDisplayRecipes() {
	        return this.recipeList;
	     }
	}