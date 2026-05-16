package dark.detraismc.skyfunutils.implementation;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.RecipeDisplayItem;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.AContainer;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.MachineRecipe;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class AutoSieve extends AContainer implements RecipeDisplayItem {

    private int capacity = 256;
    private int energyConsumption = 1;
    private int processingSpeed = 1;

    public AutoSieve(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);
    }

    @Override
    public int getCapacity() {
        return this.capacity;
    }

    @Override
    public int getEnergyConsumption() {
        return this.energyConsumption;
    }

    @Override
    public int getSpeed() {
        return this.processingSpeed;
    }

    @Override
    public String getMachineIdentifier() {
        return "SKYFUN_AUTO_SIEVE";
    }

    @Override
    public ItemStack getProgressBar() {
        return new ItemStack(Material.SCAFFOLDING);
    }

    @Override
    protected void registerDefaultRecipes() {
        // Dynamic recipes handled in findNextRecipe
    }

    @Override
    protected MachineRecipe findNextRecipe(BlockMenu menu) {
        for (int slot : getInputSlots()) {
            ItemStack item = menu.getItemInSlot(slot);

            if (item != null && item.getType() == Material.SAND) {
                ItemStack output = getRandomDrop();

                // If the drop is null (dust), we use AIR so Slimefun handles consumption correctly
                if (output == null) output = new ItemStack(Material.AIR);

                // Processing time is 10 seconds divided by processing speed
                int time = Math.max(1, 10 / processingSpeed);

                MachineRecipe recipe = new MachineRecipe(time, new ItemStack[] { new ItemStack(Material.SAND, 1) }, new ItemStack[] { output });

                if (output.getType() == Material.AIR || menu.fits(output, getOutputSlots())) {
                    return recipe;
                }
            }
        }
        return null;
    }

    private ItemStack getRandomDrop() {
        int roll = ThreadLocalRandom.current().nextInt(100);
        if (roll < 1) return new ItemStack(Material.DIAMOND);
        if (roll < 10) return new ItemStack(Material.LAPIS_LAZULI);
        if (roll < 25) return new ItemStack(Material.REDSTONE);
        if (roll < 45) return new ItemStack(Material.IRON_NUGGET);
        if (roll < 65) return new ItemStack(Material.GUNPOWDER);
        if (roll < 90) return new ItemStack(Material.BONE_MEAL);
        return null;
    }

    @Override
    public List<ItemStack> getDisplayRecipes() {
        List<ItemStack> displayRecipes = new ArrayList<>();
        ItemStack sand = new ItemStack(Material.SAND);
        displayRecipes.add(sand); displayRecipes.add(new CustomItemStack(Material.DIAMOND, "&bDiamond &7(1%)"));
        displayRecipes.add(sand); displayRecipes.add(new CustomItemStack(Material.LAPIS_LAZULI, "&9Lapis Lazuli &7(9%)"));
        displayRecipes.add(sand); displayRecipes.add(new CustomItemStack(Material.REDSTONE, "&cRedstone &7(15%)"));
        displayRecipes.add(sand); displayRecipes.add(new CustomItemStack(Material.IRON_NUGGET, "&fIron Nugget &7(20%)"));
        displayRecipes.add(sand); displayRecipes.add(new CustomItemStack(Material.GUNPOWDER, "&8Gunpowder &7(20%)"));
        displayRecipes.add(sand); displayRecipes.add(new CustomItemStack(Material.BONE_MEAL, "&fBone Meal &7(25%)"));
        return displayRecipes;
    }
}