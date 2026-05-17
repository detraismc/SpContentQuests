package dark.detraismc.skyfunutils.setup;

import dark.detraismc.skyfunutils.DetraisRecipeType;
import dark.detraismc.skyfunutils.SkyfunItems;
import dark.detraismc.skyfunutils.SkyfunUtils;
import dark.detraismc.skyfunutils.implementation.*;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.logging.Level;

public final class ToolsSetup {
    public static final ToolsSetup INSTANCE = new ToolsSetup();
    private boolean initialised;

    private ToolsSetup() {
    }

    public void init() {
        if (!this.initialised) {
            this.initialised = true;
            SkyfunUtils plugin = SkyfunUtils.getInstance();

            // ==========================================
            // 1. CROOKS (Vanilla Table Compatible)
            // ==========================================
            new Crook(SkyfunItems.category_tools, SkyfunItems.SKYFUN_CROOK, DetraisRecipeType.SKYFUN_CRAFTING_TABLE, new ItemStack[] {
                    new ItemStack(Material.STICK), new ItemStack(Material.STICK), null,
                    null,                          new ItemStack(Material.STICK), null,
                    null,                          new ItemStack(Material.STICK), null
            }).register(plugin);
            Bukkit.addRecipe(getCrookVanilla(plugin, "skyfun_crook", SkyfunItems.SKYFUN_CROOK, Material.STICK));

            RecipeType.ENHANCED_CRAFTING_TABLE.register(new ItemStack[] {
                    new ItemStack(Material.STICK), new ItemStack(Material.STICK), null,
                    null,                          new ItemStack(Material.STICK), null,
                    null,                          new ItemStack(Material.STICK), null
            }, SkyfunItems.SKYFUN_CROOK);



            new Crook(SkyfunItems.category_tools, SkyfunItems.SKYFUN_BONE_CROOK, DetraisRecipeType.SKYFUN_CRAFTING_TABLE, new ItemStack[] {
                    new ItemStack(Material.BONE), new ItemStack(Material.BONE), null,
                    null,                         new ItemStack(Material.BONE), null,
                    null,                         new ItemStack(Material.BONE), null
            }).register(plugin);
            Bukkit.addRecipe(getCrookVanilla(plugin, "skyfun_bone_crook", SkyfunItems.SKYFUN_BONE_CROOK, Material.BONE));

            RecipeType.ENHANCED_CRAFTING_TABLE.register(new ItemStack[] {
                    new ItemStack(Material.BONE), new ItemStack(Material.BONE), null,
                    null,                          new ItemStack(Material.BONE), null,
                    null,                          new ItemStack(Material.BONE), null
            }, SkyfunItems.SKYFUN_BONE_CROOK);


            // ==========================================
            // 2. HAMMERS (Vanilla Table Compatible)
            // ==========================================

            // Wooden Hammers (Handles multiple wood alternatives)
            Material[] woodTypes = {
                    Material.OAK_PLANKS, Material.SPRUCE_PLANKS, Material.BIRCH_PLANKS,
                    Material.JUNGLE_PLANKS, Material.ACACIA_PLANKS, Material.DARK_OAK_PLANKS,
                    Material.MANGROVE_PLANKS, Material.CHERRY_PLANKS, Material.BAMBOO_PLANKS,
                    Material.CRIMSON_PLANKS, Material.WARPED_PLANKS
            };
            new Hammer(SkyfunItems.category_tools, SkyfunItems.SKYFUN_WOODEN_HAMMER, DetraisRecipeType.SKYFUN_CRAFTING_TABLE, new ItemStack[] {
                    null, SkyfunItems.INFO_ANY_PLANKS, null,
                    null, new ItemStack(Material.STICK), SkyfunItems.INFO_ANY_PLANKS,
                    new ItemStack(Material.STICK), null,null
            }).register(plugin);

            for (int i = 0; i < woodTypes.length; i++) {
                Bukkit.addRecipe(getHammerVanilla(plugin, "skyfun_wooden_hammer_" + i, SkyfunItems.SKYFUN_WOODEN_HAMMER, woodTypes[i]));
                if (i > 0) { // Add secondary recipe displays into the guide book matrix
                    RecipeType.ENHANCED_CRAFTING_TABLE.register(new ItemStack[] {
                            null, new ItemStack(woodTypes[i]), null,
                            null, new ItemStack(Material.STICK), new ItemStack(woodTypes[i]),
                            new ItemStack(Material.STICK), null,null
                    }, SkyfunItems.SKYFUN_WOODEN_HAMMER);
                }
            }

            // Stone Hammer variants
            new Hammer(SkyfunItems.category_tools, SkyfunItems.SKYFUN_STONE_HAMMER, DetraisRecipeType.SKYFUN_CRAFTING_TABLE, new ItemStack[] {
                    null, SkyfunItems.INFO_ANY_COBBLESTONE, null,
                    null, new ItemStack(Material.STICK), SkyfunItems.INFO_ANY_COBBLESTONE,
                    new ItemStack(Material.STICK), null, null
            }).register(plugin);
            Bukkit.addRecipe(getHammerVanilla(plugin, "skyfun_stone_hammer_cobble", SkyfunItems.SKYFUN_STONE_HAMMER, Material.COBBLESTONE));
            Bukkit.addRecipe(getHammerVanilla(plugin, "skyfun_stone_hammer_deepslate", SkyfunItems.SKYFUN_STONE_HAMMER, Material.COBBLED_DEEPSLATE));
            Bukkit.addRecipe(getHammerVanilla(plugin, "skyfun_stone_hammer_blackstone", SkyfunItems.SKYFUN_STONE_HAMMER, Material.BLACKSTONE));

            RecipeType.ENHANCED_CRAFTING_TABLE.register(new ItemStack[] {
                    null, new ItemStack(Material.COBBLESTONE), null,
                    null, new ItemStack(Material.STICK), new ItemStack(Material.COBBLESTONE),
                    new ItemStack(Material.STICK), null, null
            }, SkyfunItems.SKYFUN_STONE_HAMMER);
            RecipeType.ENHANCED_CRAFTING_TABLE.register(new ItemStack[] {
                    null, new ItemStack(Material.COBBLED_DEEPSLATE), null,
                    null, new ItemStack(Material.STICK), new ItemStack(Material.COBBLED_DEEPSLATE),
                    new ItemStack(Material.STICK), null, null
            }, SkyfunItems.SKYFUN_STONE_HAMMER);
            RecipeType.ENHANCED_CRAFTING_TABLE.register(new ItemStack[] {
                    null, new ItemStack(Material.BLACKSTONE), null,
                    null, new ItemStack(Material.STICK), new ItemStack(Material.BLACKSTONE),
                    new ItemStack(Material.STICK), null, null
            }, SkyfunItems.SKYFUN_STONE_HAMMER);


            // Iron Hammer
            new Hammer(SkyfunItems.category_tools, SkyfunItems.SKYFUN_IRON_HAMMER, DetraisRecipeType.SKYFUN_CRAFTING_TABLE, new ItemStack[] {
                    null, new ItemStack(Material.IRON_INGOT), null,
                    null, new ItemStack(Material.STICK), new ItemStack(Material.IRON_INGOT),
                    new ItemStack(Material.STICK), null, null
            }).register(plugin);
            Bukkit.addRecipe(getHammerVanilla(plugin, "skyfun_iron_hammer", SkyfunItems.SKYFUN_IRON_HAMMER, Material.IRON_INGOT));

            RecipeType.ENHANCED_CRAFTING_TABLE.register(new ItemStack[] {
                    null, new ItemStack(Material.IRON_INGOT), null,
                    null, new ItemStack(Material.STICK), new ItemStack(Material.IRON_INGOT),
                    new ItemStack(Material.STICK), null, null
            }, SkyfunItems.SKYFUN_IRON_HAMMER);


            // Golden Hammer
            new Hammer(SkyfunItems.category_tools, SkyfunItems.SKYFUN_GOLDEN_HAMMER, DetraisRecipeType.SKYFUN_CRAFTING_TABLE, new ItemStack[] {
                    null, new ItemStack(Material.GOLD_INGOT), null,
                    null, new ItemStack(Material.STICK), new ItemStack(Material.GOLD_INGOT),
                    new ItemStack(Material.STICK), null, null
            }).register(plugin);
            Bukkit.addRecipe(getHammerVanilla(plugin, "skyfun_golden_hammer", SkyfunItems.SKYFUN_GOLDEN_HAMMER, Material.GOLD_INGOT));

            RecipeType.ENHANCED_CRAFTING_TABLE.register(new ItemStack[] {
                    null, new ItemStack(Material.GOLD_INGOT), null,
                    null, new ItemStack(Material.STICK), new ItemStack(Material.GOLD_INGOT),
                    new ItemStack(Material.STICK), null, null
            }, SkyfunItems.SKYFUN_GOLDEN_HAMMER);

            // Diamond Hammer
            new Hammer(SkyfunItems.category_tools, SkyfunItems.SKYFUN_DIAMOND_HAMMER, DetraisRecipeType.SKYFUN_CRAFTING_TABLE, new ItemStack[] {
                    null, new ItemStack(Material.DIAMOND), null,
                    null, new ItemStack(Material.STICK), new ItemStack(Material.DIAMOND),
                    new ItemStack(Material.STICK), null, null
            }).register(plugin);
            Bukkit.addRecipe(getHammerVanilla(plugin, "skyfun_diamond_hammer", SkyfunItems.SKYFUN_DIAMOND_HAMMER, Material.DIAMOND));

            RecipeType.ENHANCED_CRAFTING_TABLE.register(new ItemStack[] {
                    null, new ItemStack(Material.DIAMOND), null,
                    null, new ItemStack(Material.STICK), new ItemStack(Material.DIAMOND),
                    new ItemStack(Material.STICK), null, null
            }, SkyfunItems.SKYFUN_DIAMOND_HAMMER);



            // Flint Shears
            new FlintShears(SkyfunItems.category_tools, SkyfunItems.SKYFUN_FLINT_SHEARS, DetraisRecipeType.SKYFUN_CRAFTING_TABLE, new ItemStack[] {
                    null, new ItemStack(Material.FLINT), null,
                    new ItemStack(Material.FLINT), null, null,
                    null, null, null
            }).register(plugin);
            Bukkit.addRecipe(getShearsVanilla(plugin));

            RecipeType.ENHANCED_CRAFTING_TABLE.register(new ItemStack[] {
                    null, new ItemStack(Material.FLINT), null,
                    new ItemStack(Material.FLINT), null, null,
                    null, null, null
            }, SkyfunItems.SKYFUN_FLINT_SHEARS);
            RecipeType.ENHANCED_CRAFTING_TABLE.register(new ItemStack[] {
                    null, null, new ItemStack(Material.FLINT),
                    null, new ItemStack(Material.FLINT), null,
                    null, null, null
            }, SkyfunItems.SKYFUN_FLINT_SHEARS);
            RecipeType.ENHANCED_CRAFTING_TABLE.register(new ItemStack[] {
                    null, null, null,
                    null, null, new ItemStack(Material.FLINT),
                    null, new ItemStack(Material.FLINT), null
            }, SkyfunItems.SKYFUN_FLINT_SHEARS);
            RecipeType.ENHANCED_CRAFTING_TABLE.register(new ItemStack[] {
                    null, null, null,
                    null, new ItemStack(Material.FLINT), null,
                    new ItemStack(Material.FLINT), null, null
            }, SkyfunItems.SKYFUN_FLINT_SHEARS);

            // ==========================================
            // 3. MISC ITEMS & MATERIALS
            // ==========================================

            // Grass Seeds
            new GrassSeeds(SkyfunItems.category_tools, SkyfunItems.SKYFUN_GRASS_SEEDS, DetraisRecipeType.SKYFUN_CRAFTING_TABLE, new ItemStack[] {
                    new ItemStack(Material.OAK_SAPLING), new ItemStack(Material.OAK_SAPLING), new ItemStack(Material.OAK_SAPLING),
                    new ItemStack(Material.OAK_SAPLING), new ItemStack(Material.BONE_MEAL),  new ItemStack(Material.OAK_SAPLING),
                    new ItemStack(Material.OAK_SAPLING), new ItemStack(Material.OAK_SAPLING), new ItemStack(Material.OAK_SAPLING)
            }).register(plugin);
            setSeedsVanilla(plugin);

            RecipeType.ENHANCED_CRAFTING_TABLE.register(new ItemStack[] {
                    new ItemStack(Material.OAK_SAPLING), new ItemStack(Material.OAK_SAPLING), new ItemStack(Material.OAK_SAPLING),
                    new ItemStack(Material.OAK_SAPLING), new ItemStack(Material.BONE_MEAL),  new ItemStack(Material.OAK_SAPLING),
                    new ItemStack(Material.OAK_SAPLING), new ItemStack(Material.OAK_SAPLING), new ItemStack(Material.OAK_SAPLING)
            }, SkyfunItems.SKYFUN_GRASS_SEEDS);


            // Silkworm Drop Entry (Uncraftable - Obtained via Crook breaking)
            new Silkworm(
                    SkyfunItems.category_tools,
                    SkyfunItems.SKYFUN_SILKWORM,
                    DetraisRecipeType.CROOK_BREAKING,
                    new ItemStack[] {
                            null, new ItemStack(Material.OAK_LEAVES), null,
                            null, null, null,
                            null, null, null }
            ).register(plugin);

            // Unfired Clay Bucket Setup
            new SlimefunItem(SkyfunItems.category_tools, SkyfunItems.SKYFUN_CLAY_BUCKET, DetraisRecipeType.SKYFUN_CRAFTING_TABLE, new ItemStack[] {
                    null, null, null,
                    new ItemStack(Material.CLAY_BALL), null, new ItemStack(Material.CLAY_BALL),
                    new ItemStack(Material.CLAY_BALL), new ItemStack(Material.CLAY_BALL), new ItemStack(Material.CLAY_BALL)
            }).register(plugin);
            Bukkit.addRecipe(getBucketVanilla(plugin));

            RecipeType.ENHANCED_CRAFTING_TABLE.register(new ItemStack[] {
                    null, null, null,
                    new ItemStack(Material.CLAY_BALL), null, new ItemStack(Material.CLAY_BALL),
                    new ItemStack(Material.CLAY_BALL), new ItemStack(Material.CLAY_BALL), new ItemStack(Material.CLAY_BALL)
            }, SkyfunItems.SKYFUN_CLAY_BUCKET);

            new SlimefunItem(SkyfunItems.category_tools, SkyfunItems.SKYFUN_BUCKET, DetraisRecipeType.SKYFUN_FURNACE, new ItemStack[] {
                    null, null, null,
                    null, SkyfunItems.SKYFUN_CLAY_BUCKET, null,
                    null, null, null
            }).register(plugin);
            NamespacedKey furnaceKey = new NamespacedKey(plugin, "skyfun_bucket");
            FurnaceRecipe furnaceRecipe = new FurnaceRecipe(furnaceKey, new ItemStack(Material.BUCKET),
                    new RecipeChoice.ExactChoice(SkyfunItems.SKYFUN_CLAY_BUCKET), 0.35f, 200);
            Bukkit.addRecipe(furnaceRecipe);

            // Wither Skeleton Skull Machinery Alternative
            new SlimefunItem(SkyfunItems.category_tools, SkyfunItems.SKYFUN_WITHER_SKELETON_SKULL, DetraisRecipeType.SKYFUN_SMELTERY, new ItemStack[] {
                    new ItemStack(Material.SKELETON_SKULL), SlimefunItems.CARBON_CHUNK, null,
                    null, null, null,
                    null, null,  null
            }).register(plugin);

            RecipeType.SMELTERY.register(new ItemStack[] {
                    new ItemStack(Material.SKELETON_SKULL), SlimefunItems.CARBON_CHUNK, null,
                    null, null, null,
                    null, null, null
            }, new ItemStack(Material.WITHER_SKELETON_SKULL));

        }
    }

    private ShapedRecipe getCrookVanilla(SkyfunUtils plugin, String keyString, ItemStack result, Material ingredient) {
        NamespacedKey key = new NamespacedKey(plugin, keyString);
        ShapedRecipe recipe = new ShapedRecipe(key, result.clone());
        recipe.shape(
                "TT",
                " T",
                " T");
        recipe.setIngredient('T', ingredient);
        return recipe;
    }
    private ShapedRecipe getHammerVanilla(SkyfunUtils plugin, String keyString, ItemStack result, Material ingredient) {
        NamespacedKey key = new NamespacedKey(plugin, keyString);
        ShapedRecipe recipe = new ShapedRecipe(key, result.clone());
        recipe.shape(
                " T ",
                " ST",
                "S  ");
        recipe.setIngredient('T', ingredient);
        recipe.setIngredient('S', Material.STICK);
        return recipe;
    }
    private void setSeedsVanilla(SkyfunUtils plugin) {
        ItemStack resultItem = SkyfunItems.SKYFUN_GRASS_SEEDS.clone();
        NamespacedKey key = new NamespacedKey(plugin, "skyfun_grass_seeds");
        ShapedRecipe recipe = new ShapedRecipe(key, resultItem);
        recipe.shape(
                "TTT",
                "TST",
                "TTT");
        recipe.setIngredient('T', Material.OAK_SAPLING);
        recipe.setIngredient('S', Material.BONE_MEAL);
        Bukkit.addRecipe(recipe);
    }
    private ShapedRecipe getBucketVanilla(SkyfunUtils plugin) {
        ItemStack resultItem = SkyfunItems.SKYFUN_CLAY_BUCKET.clone();
        NamespacedKey key = new NamespacedKey(plugin, "skyfun_clay_bucket");
        ShapedRecipe recipe = new ShapedRecipe(key, resultItem);
        recipe.shape(
                "T T",
                "TTT");
        recipe.setIngredient('T', Material.CLAY_BALL);
        return recipe;
    }

    private ShapedRecipe getShearsVanilla(SkyfunUtils plugin) {
        ItemStack resultItem = SkyfunItems.SKYFUN_FLINT_SHEARS.clone();
        NamespacedKey key = new NamespacedKey(plugin, "skyfun_flint_shears");
        ShapedRecipe recipe = new ShapedRecipe(key, resultItem);
        recipe.shape(
                " T",
                "T ");
        recipe.setIngredient('T', Material.FLINT);
        return recipe;
    }

}