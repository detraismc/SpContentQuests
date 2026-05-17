package dark.detraismc.skyfunutils.setup;

import dark.detraismc.skyfunutils.SkyfunItems;
import dark.detraismc.skyfunutils.SkyfunUtils;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;

public final class DeleteVanillaCraftingSetup {
    public static final DeleteVanillaCraftingSetup INSTANCE = new DeleteVanillaCraftingSetup();
    private boolean initialised;

    private DeleteVanillaCraftingSetup() {
    }

    public void init() {
        if (!this.initialised) {
            this.initialised = true;

            SkyfunUtils plugin = SkyfunUtils.getInstance();

            // Unregister the Crooks
            Bukkit.removeRecipe(new NamespacedKey(plugin, "skyfun_crook"));
            Bukkit.removeRecipe(new NamespacedKey(plugin, "skyfun_bone_crook"));

            // Unregister ALL Wooden Hammer variants (Matches the 0-10 loop from ToolsSetup)
            for (int i = 0; i < 11; i++) {
                Bukkit.removeRecipe(new NamespacedKey(plugin, "skyfun_wooden_hammer_" + i));
            }

            // Unregister Stone Hammer variants
            Bukkit.removeRecipe(new NamespacedKey(plugin, "skyfun_stone_hammer_cobble"));
            Bukkit.removeRecipe(new NamespacedKey(plugin, "skyfun_stone_hammer_deepslate"));
            Bukkit.removeRecipe(new NamespacedKey(plugin, "skyfun_stone_hammer_blackstone"));

            // Unregister Remaining Metal/Gem Hammers
            Bukkit.removeRecipe(new NamespacedKey(plugin, "skyfun_iron_hammer"));
            Bukkit.removeRecipe(new NamespacedKey(plugin, "skyfun_gold_hammer"));
            Bukkit.removeRecipe(new NamespacedKey(plugin, "skyfun_diamond_hammer"));


            // Unregister Miscellaneous Recipes
            Bukkit.removeRecipe(new NamespacedKey(plugin, "skyfun_flint_shears"));
            Bukkit.removeRecipe(new NamespacedKey(plugin, "skyfun_grass_seeds"));
            Bukkit.removeRecipe(new NamespacedKey(plugin, "skyfun_clay_bucket"));
            Bukkit.removeRecipe(new NamespacedKey(plugin, "skyfun_clay_crucible"));
            Bukkit.removeRecipe(new NamespacedKey(plugin, "skyfun_bucket"));
            Bukkit.removeRecipe(new NamespacedKey(plugin, "skyfun_crucible"));
            Bukkit.removeRecipe(new NamespacedKey(plugin, "skyfun_composter"));

            for (int i = 0; i < 11; i++) {
                Bukkit.removeRecipe(new NamespacedKey(plugin, "skyfun_sieve_" + i));
            }
            
        }
    }


}