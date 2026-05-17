package dark.detraismc.skyfunutils.setup;

import dark.detraismc.skyfunutils.DetraisRecipeType;
import dark.detraismc.skyfunutils.SkyfunItems;
import dark.detraismc.skyfunutils.SkyfunUtils;
import dark.detraismc.skyfunutils.implementation.Crook;
import dark.detraismc.skyfunutils.implementation.GrassSeeds;
import dark.detraismc.skyfunutils.implementation.Hammer;
import dark.detraismc.skyfunutils.implementation.Silkworm;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;

public final class AllowVanillaCraftingSetup {
    public static final AllowVanillaCraftingSetup INSTANCE = new AllowVanillaCraftingSetup();
    private boolean initialised;

    private AllowVanillaCraftingSetup() {
    }

    public void init() {
        if (!this.initialised) {
            this.initialised = true;

            SkyfunItems.SKYFUN_CROOK.getItem().setUseableInWorkbench(true);
            SkyfunItems.SKYFUN_BONE_CROOK.getItem().setUseableInWorkbench(true);
            SkyfunItems.SKYFUN_WOODEN_HAMMER.getItem().setUseableInWorkbench(true);
            SkyfunItems.SKYFUN_STONE_HAMMER.getItem().setUseableInWorkbench(true);
            SkyfunItems.SKYFUN_IRON_HAMMER.getItem().setUseableInWorkbench(true);
            SkyfunItems.SKYFUN_GOLDEN_HAMMER.getItem().setUseableInWorkbench(true);
            SkyfunItems.SKYFUN_DIAMOND_HAMMER.getItem().setUseableInWorkbench(true);
            SkyfunItems.SKYFUN_GRASS_SEEDS.getItem().setUseableInWorkbench(true);
            SkyfunItems.SKYFUN_CLAY_BUCKET.getItem().setUseableInWorkbench(true);
            SkyfunItems.SKYFUN_FLINT_SHEARS.getItem().setUseableInWorkbench(true);

            SkyfunItems.SKYFUN_SIEVE.getItem().setUseableInWorkbench(true);
            SkyfunItems.SKYFUN_CLAY_CRUCIBLE.getItem().setUseableInWorkbench(true);
            SkyfunItems.SKYFUN_COMPOSTER.getItem().setUseableInWorkbench(true);

            SlimefunItems.COMPOSTER.getItem().setUseableInWorkbench(true);
            SlimefunItems.CRUCIBLE.getItem().setUseableInWorkbench(true);
            
        }
    }


}