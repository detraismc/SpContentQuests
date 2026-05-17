package dark.detraismc.skyfunutils.implementation;

import dark.detraismc.skyfunutils.utils.SieveRegistry;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.RecipeDisplayItem;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.AContainer;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.MachineRecipe;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class AutoSieve extends AContainer implements RecipeDisplayItem {

    private final int capacity;
    private final int energyConsumption;
    private final int processingSpeed;

    private final Map<Material, List<MachineRecipe>> machineRecipesMap = new HashMap<>();

    // Constructor now correctly handles variable assignments safely
    public AutoSieve(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe, int capacity, int energyConsumption, int processingSpeed) {
        super(itemGroup, item, recipeType, recipe);
        this.capacity = capacity;
        this.energyConsumption = energyConsumption;
        this.processingSpeed = processingSpeed;
    }

    @Override
    public int getCapacity() { return this.capacity; }

    @Override
    public int getEnergyConsumption() { return this.energyConsumption; }

    @Override
    public int getSpeed() { return this.processingSpeed; }

    @Override
    public String getMachineIdentifier() { return getId(); }

    @Override
    public ItemStack getProgressBar() { return new ItemStack(Material.LOOM); }

    @Override
    protected void registerDefaultRecipes() {
        // Since Slimefun triggers this inside super(), processingSpeed hasn't been set yet.
        // We use a clean static base work time of 10 ticks (5 seconds).
        // getSpeed() will handle multiplying the work ticks automatically down the line!
        int baseTime = 8;

        Material[] siftables = {Material.SAND, Material.GRAVEL, Material.SOUL_SAND, Material.SOUL_SOIL, Material.DIRT};

        for (Material mat : siftables) {
            List<SieveRegistry.SiftReward> rewards = SieveRegistry.getRewards(mat);
            List<MachineRecipe> registeredList = new ArrayList<>();

            for (SieveRegistry.SiftReward reward : rewards) {
                MachineRecipe recipe = new MachineRecipe(baseTime, new ItemStack[] { new ItemStack(mat, 1) }, new ItemStack[] { reward.getItemStack() });
                this.registerRecipe(recipe);
                registeredList.add(recipe);
            }
            machineRecipesMap.put(mat, registeredList);
        }
    }

    @Override
    protected MachineRecipe findNextRecipe(BlockMenu menu) {
        for (int slot : getInputSlots()) {
            ItemStack item = menu.getItemInSlot(slot);

            if (item != null && item.getAmount() > 0) {
                Material mat = item.getType();
                List<MachineRecipe> officialRecipes = machineRecipesMap.get(mat);

                if (officialRecipes == null || officialRecipes.isEmpty()) continue;

                int rolledIndex = SieveRegistry.rollIndex(mat);
                if (rolledIndex >= 0 && rolledIndex < officialRecipes.size()) {
                    MachineRecipe rolledRecipe = officialRecipes.get(rolledIndex);

                    if (menu.fits(rolledRecipe.getOutput()[0], getOutputSlots())) {
                        return rolledRecipe;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public List<ItemStack> getDisplayRecipes() {
        List<ItemStack> displayRecipes = new ArrayList<>();
        Material[] siftables = {Material.SAND, Material.GRAVEL, Material.SOUL_SAND, Material.SOUL_SOIL, Material.DIRT};

        for (Material mat : siftables) {
            ItemStack input = new ItemStack(mat);
            int totalWeight = SieveRegistry.getTotalWeight(mat);

            for (SieveRegistry.SiftReward reward : SieveRegistry.getRewards(mat)) {
                double percent = totalWeight > 0 ? ((double) reward.getWeight() / totalWeight) * 100 : 0;
                String displayPercent = String.format("%.1f", percent) + "%";

                displayRecipes.add(input);
                displayRecipes.add(new CustomItemStack(reward.getItemStack(), reward.getDisplayName() + " &7(" + displayPercent + ")"));
            }
        }
        return displayRecipes;
    }
}