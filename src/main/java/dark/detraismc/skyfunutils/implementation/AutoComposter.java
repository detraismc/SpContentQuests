package dark.detraismc.skyfunutils.implementation;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.RecipeDisplayItem;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.AContainer;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.MachineRecipe;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class AutoComposter extends AContainer implements RecipeDisplayItem {

    public AutoComposter(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);
        addItemHandler(onBreak());
    }

    @Override
    protected void registerDefaultRecipes() {
        // Leaves -> Dirt
        for (Material leaf : Tag.LEAVES.getValues()) {
            registerRecipe(8, new ItemStack(leaf, 8), new ItemStack(Material.DIRT));
        }
        // Saplings -> Dirt
        for (Material sapling : Tag.SAPLINGS.getValues()) {
            registerRecipe(8, new ItemStack(sapling, 8), new ItemStack(Material.DIRT));
        }

        // Custom conversions
        registerRecipe(8, new ItemStack(Material.STONE, 4), new ItemStack(Material.NETHERRACK));
        registerRecipe(8, new ItemStack(Material.SAND, 2), new ItemStack(Material.SOUL_SAND));
    }

    public void registerRecipe(int seconds, ItemStack input, ItemStack output) {
        registerRecipe(new MachineRecipe(seconds, new ItemStack[] { input }, new ItemStack[] { output }));
    }

    @Override
    public List<ItemStack> getDisplayRecipes() {
        List<ItemStack> displayRecipes = new ArrayList<>(recipes.size() * 2);
        for (MachineRecipe recipe : recipes) {
            displayRecipes.add(recipe.getInput()[0]);
            displayRecipes.add(recipe.getOutput()[0]);
        }
        return displayRecipes;
    }

    @Override
    public ItemStack getProgressBar() {
        return new ItemStack(Material.WOODEN_HOE);
    }

    @Override
    public String getMachineIdentifier() {
        return "SKYFUN_AUTO_COMPOSTER";
    }

    public BlockBreakHandler onBreak() {
        return new BlockBreakHandler(false, false) {
            @Override
            public void onPlayerBreak(BlockBreakEvent e, ItemStack item, List<ItemStack> drops) {
                Block b = e.getBlock();
                BlockMenu inv = BlockStorage.getInventory(b);
                if (inv != null) {
                    inv.dropItems(b.getLocation(), getInputSlots());
                    inv.dropItems(b.getLocation(), getOutputSlots());
                }
            }
        };
    }
}