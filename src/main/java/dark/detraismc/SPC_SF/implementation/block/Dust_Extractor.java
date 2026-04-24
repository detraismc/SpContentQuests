package dark.detraismc.SPC_SF.implementation.block;

import dark.detraismc.SPC_SF.SPCAddon;
import dark.detraismc.SPC_SF.SPCItems;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.RecipeDisplayItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.AContainer;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.MachineRecipe;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class Dust_Extractor extends AContainer implements RecipeDisplayItem {

	SPCAddon plugin = SPCAddon.getInstance();

   public Dust_Extractor() {
       super(SPCItems.category_machine, SPCItems.DUST_EXTRACTOR, RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
                SlimefunItems.ELECTRIC_ORE_GRINDER_3, SlimefunItems.ELECTRIC_GOLD_PAN_3, SlimefunItems.ELECTRIC_DUST_WASHER_3,
       		    SlimefunItems.ELECTRIC_ORE_GRINDER_3, SlimefunItems.ELECTRIC_GOLD_PAN_3, SlimefunItems.ELECTRIC_DUST_WASHER_3,
	    		SPCItems.ARTIFACT_CIRCUIT_BOARD, SPCItems.ELITE_GENERATOR_3, SPCItems.ARTIFACT_CIRCUIT_BOARD
	    });
   }

    @Override
    public List<ItemStack> getDisplayRecipes() {
        List<ItemStack> recipes = new ArrayList<>();

        // 1. Define all the valid input blocks your machine accepts
        Material[] validInputs = {
                Material.COBBLESTONE,
                Material.ANDESITE,
                Material.GRANITE,
                Material.DIORITE,
                Material.BLACKSTONE,
                Material.COBBLED_DEEPSLATE
        };

        // 2. Define all the possible dusts the machine can output
        ItemStack[] possibleOutputs = {
                SlimefunItems.COPPER_DUST,
                SlimefunItems.TIN_DUST,
                SlimefunItems.SILVER_DUST,
                SlimefunItems.LEAD_DUST,
                SlimefunItems.ALUMINUM_DUST,
                SlimefunItems.ZINC_DUST,
                SlimefunItems.MAGNESIUM_DUST,
                SlimefunItems.IRON_DUST,
                SlimefunItems.GOLD_DUST
        };

        // 3. Loop through every input and pair it with every output for the Slimefun Guide
        for (Material inputType : validInputs) {
            for (ItemStack outputDust : possibleOutputs) {
                recipes.add(new ItemStack(inputType, 1)); // The Input
                recipes.add(outputDust);                  // The Output
            }
        }

        return recipes;
    }

   public ItemStack getProgressBar() {
      return new ItemStack(Material.GLOWSTONE_DUST);
   }

    protected MachineRecipe findNextRecipe(BlockMenu menu) {
        if (!this.hasFreeSlot(menu)) {
            return null;
        } else {
            int[] var2 = this.getInputSlots();
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                int slot = var2[var4];
                @SuppressWarnings("deprecation")
                ItemStack item = menu.getItemInSlot(slot);

                // 1. Ensure the item is not null and is NOT a custom Slimefun item
                if (item != null && io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem.getByItem(item) == null) {
                    Material type = item.getType();

                    // 2. Check if the material is one of your allowed stone variants
                    if (type == Material.COBBLESTONE || type == Material.ANDESITE ||
                            type == Material.GRANITE || type == Material.DIORITE ||
                            type == Material.BLACKSTONE || type == Material.COBBLED_DEEPSLATE) {

                        ItemStack output = null;
                        int randos = plugin.mtk.getRandomNumber(1, 10);

                        if (randos == 1) {
                            output = SlimefunItems.COPPER_DUST;
                        } else if (randos == 2) {
                            output = SlimefunItems.TIN_DUST;
                        } else if (randos == 3) {
                            output = SlimefunItems.SILVER_DUST;
                        } else if (randos == 4) {
                            output = SlimefunItems.LEAD_DUST;
                        } else if (randos == 5) {
                            output = SlimefunItems.ALUMINUM_DUST;
                        } else if (randos == 6) {
                            output = SlimefunItems.ZINC_DUST;
                        } else if (randos == 7) {
                            output = SlimefunItems.MAGNESIUM_DUST;
                        } else if (randos == 8) {
                            output = SlimefunItems.IRON_DUST;
                        } else if (randos == 9) {
                            output = SlimefunItems.GOLD_DUST;
                        } else {
                            output = SlimefunItems.GOLD_DUST;
                        }

                        // 3. Dynamically set the recipe's input to the specific material type that was found
                        MachineRecipe recipe = new MachineRecipe(1 / this.getSpeed(), new ItemStack[]{new ItemStack(type)}, new ItemStack[]{output});

                        if (output.getType() != Material.AIR && menu.fits(output, this.getOutputSlots())) {
                            menu.consumeItem(slot);
                            return recipe;
                        }
                    }
                }
            }

            return null;
        }
    }

   @SuppressWarnings("deprecation")
    private boolean hasFreeSlot(@Nonnull BlockMenu menu) {
      int[] var2 = this.getOutputSlots();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         int slot = var2[var4];
         if (menu.getItemInSlot(slot) == null) {
            return true;
         }
      }

      return false;
   }

   public String getMachineIdentifier() {
      return "SPC_DUST_EXTRACTOR";
   }
   
   @Override
   public int getEnergyConsumption() {
       return 120;
   }

   @Override
   public int getCapacity() {
       return 1024;
   }

   @Override
   public String getInventoryTitle() {
       return "&1Dust Extractor";
   }
}

