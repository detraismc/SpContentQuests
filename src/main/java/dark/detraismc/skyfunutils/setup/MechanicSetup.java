package dark.detraismc.skyfunutils.setup;

import dark.detraismc.skyfunutils.DetraisRecipeType;
import dark.detraismc.skyfunutils.SkyfunItems;
import dark.detraismc.skyfunutils.SkyfunUtils;
import dark.detraismc.skyfunutils.implementation.*;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;

public final class MechanicSetup {
    public static final MechanicSetup INSTANCE = new MechanicSetup();
    private boolean initialised;

    private MechanicSetup() {
    }

    public void init() {
        if (!this.initialised) {
            this.initialised = true;
            SkyfunUtils plugin = SkyfunUtils.getInstance();

            new SlimefunItem(
                    SkyfunItems.category_mechanic,
                    SkyfunItems.INFO_TWERK_FOR_TREES,
                    DetraisRecipeType.MECHANIC_INFO,
                    new ItemStack[] {} // No recipe required
            ).register(plugin);


        }
    }
}