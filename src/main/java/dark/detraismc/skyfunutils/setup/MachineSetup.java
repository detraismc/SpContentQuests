package dark.detraismc.skyfunutils.setup;

import dark.detraismc.skyfunutils.DetraisRecipeType;
import dark.detraismc.skyfunutils.SkyfunItems;
import dark.detraismc.skyfunutils.SkyfunUtils;
import dark.detraismc.skyfunutils.implementation.AutoComposter;
import dark.detraismc.skyfunutils.implementation.AutoSieve;
import dark.detraismc.skyfunutils.implementation.Sieve;
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

public class MachineSetup {
    public static final MachineSetup INSTANCE = new MachineSetup();
    private boolean initialised;

    private MachineSetup() {
    }

    public void init() {
        if (!this.initialised) {
            this.initialised = true;
            SkyfunUtils plugin = SkyfunUtils.getInstance();

            Material[] woodTypes = {
                    Material.OAK_PLANKS, Material.SPRUCE_PLANKS, Material.BIRCH_PLANKS,
                    Material.JUNGLE_PLANKS, Material.ACACIA_PLANKS, Material.DARK_OAK_PLANKS,
                    Material.MANGROVE_PLANKS, Material.CHERRY_PLANKS, Material.BAMBOO_PLANKS,
                    Material.CRIMSON_PLANKS, Material.WARPED_PLANKS
            };
            new Sieve(SkyfunItems.category_machine, SkyfunItems.SKYFUN_SIEVE, DetraisRecipeType.SKYFUN_CRAFTING_TABLE, new ItemStack[] {
                    SkyfunItems.INFO_ANY_PLANKS, new ItemStack(Material.STRING), SkyfunItems.INFO_ANY_PLANKS,
                    SkyfunItems.INFO_ANY_PLANKS, new ItemStack(Material.STRING), SkyfunItems.INFO_ANY_PLANKS,
                    new ItemStack(Material.STICK), null, new ItemStack(Material.STICK)
            }).register(plugin);
            for (int i = 0; i < woodTypes.length; i++) {
                Material wood = woodTypes[i];

                RecipeType.ENHANCED_CRAFTING_TABLE.register(new ItemStack[] {
                        new ItemStack(wood), new ItemStack(Material.STRING), new ItemStack(wood),
                        new ItemStack(wood), new ItemStack(Material.STRING), new ItemStack(wood),
                        new ItemStack(Material.STICK), null, new ItemStack(Material.STICK)
                }, SkyfunItems.SKYFUN_SIEVE);

                ItemStack resultItem = SkyfunItems.SKYFUN_SIEVE.clone();
                NamespacedKey key = new NamespacedKey(plugin, "skyfun_sieve_" + i);
                ShapedRecipe recipe = new ShapedRecipe(key, resultItem);
                recipe.shape(
                        "WSW",
                        "WSW",
                        "T T");
                recipe.setIngredient('W', wood);
                recipe.setIngredient('S', Material.STRING);
                recipe.setIngredient('T', Material.STICK);
                Bukkit.addRecipe(recipe);
            }




            // Composter Alternative
            Material[] woodSlabTypes = {
                    Material.OAK_SLAB, Material.SPRUCE_SLAB, Material.BIRCH_SLAB,
                    Material.JUNGLE_SLAB, Material.ACACIA_SLAB, Material.DARK_OAK_SLAB,
                    Material.MANGROVE_SLAB, Material.CHERRY_SLAB, Material.BAMBOO_SLAB,
                    Material.CRIMSON_SLAB, Material.WARPED_SLAB
            };
            new SlimefunItem(SkyfunItems.category_machine, SkyfunItems.SKYFUN_COMPOSTER, DetraisRecipeType.SKYFUN_CRAFTING_TABLE, new ItemStack[] {
                    SkyfunItems.INFO_ANY_PLANK_SLABS, null, SkyfunItems.INFO_ANY_PLANK_SLABS,
                    SkyfunItems.INFO_ANY_PLANK_SLABS, null, SkyfunItems.INFO_ANY_PLANK_SLABS,
                    SkyfunItems.INFO_ANY_PLANK_SLABS, new ItemStack(Material.COMPOSTER), SkyfunItems.INFO_ANY_PLANK_SLABS
            }).register(plugin);
            for (int i = 0; i < woodSlabTypes.length; i++) {
                Material wood = woodSlabTypes[i];

                RecipeType.ENHANCED_CRAFTING_TABLE.register(new ItemStack[] {
                        new ItemStack(wood), null, new ItemStack(wood),
                        new ItemStack(wood), null, new ItemStack(wood),
                        new ItemStack(wood), new ItemStack(Material.COMPOSTER), new ItemStack(wood)
                }, SlimefunItems.COMPOSTER);

                ItemStack resultItem = SlimefunItems.COMPOSTER.clone();
                NamespacedKey key = new NamespacedKey(plugin, "skyfun_composter_" + i);
                ShapedRecipe recipe = new ShapedRecipe(key, resultItem);
                recipe.shape(
                        "W W",
                        "W W",
                        "WTW");
                recipe.setIngredient('W', wood);
                recipe.setIngredient('T', Material.COMPOSTER);
                Bukkit.addRecipe(recipe);
            }







            // Unfired Crucible Setup
            new SlimefunItem(SkyfunItems.category_machine, SkyfunItems.SKYFUN_CLAY_CRUCIBLE, DetraisRecipeType.SKYFUN_CRAFTING_TABLE, new ItemStack[] {
                    new ItemStack(Material.CLAY_BALL), null, new ItemStack(Material.CLAY_BALL),
                    new ItemStack(Material.CLAY_BALL), null, new ItemStack(Material.CLAY_BALL),
                    new ItemStack(Material.CLAY), new ItemStack(Material.CLAY), new ItemStack(Material.CLAY)
            }).register(plugin);


            ItemStack resultItem = SkyfunItems.SKYFUN_CLAY_CRUCIBLE.clone();
            NamespacedKey key = new NamespacedKey(plugin, "skyfun_clay_crucible");
            ShapedRecipe recipe = new ShapedRecipe(key, resultItem);
            recipe.shape(
                    "W W",
                    "W W",
                    "TTT");
            recipe.setIngredient('W', Material.CLAY_BALL);
            recipe.setIngredient('T', Material.CLAY);
            Bukkit.addRecipe(recipe);


            RecipeType.ENHANCED_CRAFTING_TABLE.register(new ItemStack[] {
                    new ItemStack(Material.CLAY_BALL), null, new ItemStack(Material.CLAY_BALL),
                    new ItemStack(Material.CLAY_BALL), null, new ItemStack(Material.CLAY_BALL),
                    new ItemStack(Material.CLAY), new ItemStack(Material.CLAY), new ItemStack(Material.CLAY)
            }, SkyfunItems.SKYFUN_CLAY_CRUCIBLE);

            new SlimefunItem(SkyfunItems.category_machine, SkyfunItems.SKYFUN_CRUCIBLE, DetraisRecipeType.SKYFUN_FURNACE, new ItemStack[] {
                    null, null, null,
                    null, SkyfunItems.SKYFUN_CLAY_CRUCIBLE, null,
                    null, null, null
            }).register(plugin);
            NamespacedKey furnaceKey = new NamespacedKey(plugin, "skyfun_crucible");
            FurnaceRecipe furnaceRecipe = new FurnaceRecipe(furnaceKey, SlimefunItems.CRUCIBLE,
                    new RecipeChoice.ExactChoice(SkyfunItems.SKYFUN_CLAY_CRUCIBLE), 0.35f, 300);
            Bukkit.addRecipe(furnaceRecipe);









            new AutoComposter(SkyfunItems.category_machine, SkyfunItems.SKYFUN_AUTO_COMPOSTER, RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
                    SlimefunItems.DAMASCUS_STEEL_INGOT, new ItemStack(Material.IRON_HOE), SlimefunItems.DAMASCUS_STEEL_INGOT,
                    SlimefunItems.ELECTRIC_MOTOR, SlimefunItems.COMPOSTER, SlimefunItems.ELECTRIC_MOTOR,
                    SlimefunItems.BRASS_INGOT, SlimefunItems.MEDIUM_CAPACITOR, SlimefunItems.BRASS_INGOT
            })
                    .setCapacity(256)
                    .setEnergyConsumption(9)
                    .setProcessingSpeed(1)
                    .register(plugin);

            new AutoComposter(SkyfunItems.category_machine, SkyfunItems.SKYFUN_AUTO_COMPOSTER_2, RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
                    SlimefunItems.SYNTHETIC_EMERALD, new ItemStack(Material.DIAMOND_HOE), SlimefunItems.SYNTHETIC_EMERALD,
                    SlimefunItems.ELECTRIC_MOTOR, SkyfunItems.SKYFUN_AUTO_COMPOSTER, SlimefunItems.ELECTRIC_MOTOR,
                    SlimefunItems.HARDENED_METAL_INGOT, SlimefunItems.BLISTERING_INGOT_3, SlimefunItems.HARDENED_METAL_INGOT
            })
                    .setCapacity(256)
                    .setEnergyConsumption(27)
                    .setProcessingSpeed(4)
                    .register(plugin);

            new AutoComposter(SkyfunItems.category_machine, SkyfunItems.SKYFUN_AUTO_COMPOSTER_3, RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
                    SlimefunItems.CARBONADO, new ItemStack(Material.NETHERITE_HOE), SlimefunItems.CARBONADO,
                    SlimefunItems.REINFORCED_ALLOY_INGOT, SkyfunItems.SKYFUN_AUTO_COMPOSTER_2, SlimefunItems.REINFORCED_ALLOY_INGOT,
                    SlimefunItems.BLISTERING_INGOT_3, SlimefunItems.BIG_CAPACITOR, SlimefunItems.BLISTERING_INGOT_3
            })
                    .setCapacity(512)
                    .setEnergyConsumption(81)
                    .setProcessingSpeed(16)
                    .register(plugin);



            new AutoSieve(SkyfunItems.category_machine, SkyfunItems.SKYFUN_AUTO_SIEVE, RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
                    SlimefunItems.HARDENED_METAL_INGOT, new ItemStack(Material.IRON_HOE), SlimefunItems.HARDENED_METAL_INGOT,
                    SlimefunItems.ELECTRIC_MOTOR, SkyfunItems.SKYFUN_SIEVE, SlimefunItems.ELECTRIC_MOTOR,
                    SlimefunItems.BRASS_INGOT, SlimefunItems.MEDIUM_CAPACITOR, SlimefunItems.BRASS_INGOT
            }, 256, 9, 1).register(plugin);

            new AutoSieve(SkyfunItems.category_machine, SkyfunItems.SKYFUN_AUTO_SIEVE_2, RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
                    SlimefunItems.SYNTHETIC_DIAMOND, new ItemStack(Material.DIAMOND_HOE), SlimefunItems.SYNTHETIC_DIAMOND,
                    SlimefunItems.REINFORCED_ALLOY_INGOT, SkyfunItems.SKYFUN_AUTO_SIEVE, SlimefunItems.REINFORCED_ALLOY_INGOT,
                    SlimefunItems.HARDENED_METAL_INGOT, SlimefunItems.BLISTERING_INGOT_3, SlimefunItems.HARDENED_METAL_INGOT
            }, 256, 27, 4).register(plugin);

            new AutoSieve(SkyfunItems.category_machine, SkyfunItems.SKYFUN_AUTO_SIEVE_3, RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
                    SlimefunItems.CARBONADO, new ItemStack(Material.NETHERITE_HOE), SlimefunItems.CARBONADO,
                    SlimefunItems.REINFORCED_PLATE, SkyfunItems.SKYFUN_AUTO_SIEVE_2, SlimefunItems.REINFORCED_PLATE,
                    SlimefunItems.BLISTERING_INGOT_3, SlimefunItems.BIG_CAPACITOR, SlimefunItems.BLISTERING_INGOT_3
            }, 512, 81, 16).register(plugin);

        }
    }
}
