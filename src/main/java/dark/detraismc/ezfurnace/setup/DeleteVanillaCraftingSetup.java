package dark.detraismc.ezfurnace.setup;

import dark.detraismc.ezfurnace.EzFurnace;
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

            EzFurnace plugin = EzFurnace.getInstance();

            Bukkit.removeRecipe(new NamespacedKey(plugin, "ezfurnace_copper_furnace"));
            Bukkit.removeRecipe(new NamespacedKey(plugin, "ezfurnace_iron_furnace"));
            Bukkit.removeRecipe(new NamespacedKey(plugin, "ezfurnace_golden_furnace"));
            Bukkit.removeRecipe(new NamespacedKey(plugin, "ezfurnace_lapis_furnace"));
            Bukkit.removeRecipe(new NamespacedKey(plugin, "ezfurnace_redstone_furnace"));
            Bukkit.removeRecipe(new NamespacedKey(plugin, "ezfurnace_diamond_furnace"));
            Bukkit.removeRecipe(new NamespacedKey(plugin, "ezfurnace_emerald_furnace"));
            Bukkit.removeRecipe(new NamespacedKey(plugin, "ezfurnace_netherite_furnace"));

            
        }
    }


}