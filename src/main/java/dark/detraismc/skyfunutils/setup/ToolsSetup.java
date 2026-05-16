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
import org.bukkit.inventory.ShapedRecipe;

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
            registerShapedVanilla(plugin, "skyfun_crook", SkyfunItems.SKYFUN_CROOK, new String[]{"SS ", " S ", " S "}, 'S', Material.STICK);

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
            registerShapedVanilla(plugin, "skyfun_bone_crook", SkyfunItems.SKYFUN_BONE_CROOK, new String[]{"BB ", " S ", " S "}, 'B', Material.BONE, 'S', Material.STICK);

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
                    null, new ItemStack(woodTypes[0]), null,
                    null, new ItemStack(Material.STICK), new ItemStack(woodTypes[0]),
                    new ItemStack(Material.STICK), null,null
            }).register(plugin);

            for (int i = 0; i < woodTypes.length; i++) {
                registerShapedVanilla(plugin, "skyfun_wooden_hammer_" + i, SkyfunItems.SKYFUN_WOODEN_HAMMER, new String[]{" H ", " SH", "S  "}, 'H', woodTypes[i], 'S', Material.STICK);
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
                    null, new ItemStack(Material.COBBLESTONE), null,
                    null, new ItemStack(Material.STICK), new ItemStack(Material.COBBLESTONE),
                    new ItemStack(Material.STICK), null, null
            }).register(plugin);
            registerShapedVanilla(plugin, "skyfun_stone_hammer_cobble", SkyfunItems.SKYFUN_STONE_HAMMER, new String[]{" H ", " SH", "S  "}, 'H', Material.COBBLESTONE, 'S', Material.STICK);
            registerShapedVanilla(plugin, "skyfun_stone_hammer_deepslate", SkyfunItems.SKYFUN_STONE_HAMMER, new String[]{" H ", " SH", "S  "}, 'H', Material.COBBLED_DEEPSLATE, 'S', Material.STICK);
            registerShapedVanilla(plugin, "skyfun_stone_hammer_blackstone", SkyfunItems.SKYFUN_STONE_HAMMER, new String[]{" H ", " SH", "S  "}, 'H', Material.BLACKSTONE, 'S', Material.STICK);

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
            registerShapedVanilla(plugin, "skyfun_iron_hammer", SkyfunItems.SKYFUN_IRON_HAMMER, new String[]{" H ", " SH", "S  "}, 'H', Material.IRON_INGOT, 'S', Material.STICK);

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
            registerShapedVanilla(plugin, "skyfun_gold_hammer", SkyfunItems.SKYFUN_GOLDEN_HAMMER, new String[]{" H ", " SH", "S  "}, 'H', Material.GOLD_INGOT, 'S', Material.STICK);

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
            registerShapedVanilla(plugin, "skyfun_diamond_hammer", SkyfunItems.SKYFUN_DIAMOND_HAMMER, new String[]{" H ", " SH", "S  "}, 'H', Material.DIAMOND, 'S', Material.STICK);

            RecipeType.ENHANCED_CRAFTING_TABLE.register(new ItemStack[] {
                    null, new ItemStack(Material.DIAMOND), null,
                    null, new ItemStack(Material.STICK), new ItemStack(Material.DIAMOND),
                    new ItemStack(Material.STICK), null, null
            }, SkyfunItems.SKYFUN_DIAMOND_HAMMER);

            // ==========================================
            // 3. MISC ITEMS & MATERIALS
            // ==========================================

            // Grass Seeds
            new GrassSeeds(SkyfunItems.category_tools, SkyfunItems.SKYFUN_GRASS_SEEDS, DetraisRecipeType.SKYFUN_CRAFTING_TABLE, new ItemStack[] {
                    new ItemStack(Material.OAK_SAPLING), new ItemStack(Material.OAK_SAPLING), new ItemStack(Material.OAK_SAPLING),
                    new ItemStack(Material.OAK_SAPLING), new ItemStack(Material.BONE_MEAL),  new ItemStack(Material.OAK_SAPLING),
                    new ItemStack(Material.OAK_SAPLING), new ItemStack(Material.OAK_SAPLING), new ItemStack(Material.OAK_SAPLING)
            }).register(plugin);
            registerShapedVanilla(plugin, "skyfun_grass_seeds", SkyfunItems.SKYFUN_GRASS_SEEDS, new String[]{"SSS", "SBS", "SSS"}, 'S', Material.OAK_SAPLING, 'B', Material.BONE_MEAL);

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
                    null, new ItemStack(Material.CLAY_BALL), null
            }).register(plugin);
            registerShapedVanilla(plugin, "skyfun_clay_bucket", SkyfunItems.SKYFUN_CLAY_BUCKET, new String[]{"   ", "C C", " C "}, 'C', Material.CLAY_BALL);

            RecipeType.ENHANCED_CRAFTING_TABLE.register(new ItemStack[] {
                    null, null, null,
                    new ItemStack(Material.CLAY_BALL), null, new ItemStack(Material.CLAY_BALL),
                    null, new ItemStack(Material.CLAY_BALL), null
            }, SkyfunItems.SKYFUN_CLAY_BUCKET);


            // Smelting Custom Clay Bucket -> Vanilla Bucket
            RecipeType.SMELTERY.register(new ItemStack[] {
                    SkyfunItems.SKYFUN_CLAY_BUCKET, null, null,
                    null, null, null,
                    null, null, null
            }, new ItemStack(Material.BUCKET));

            NamespacedKey furnaceKey = new NamespacedKey(plugin, "clay_bucket_smelting");
            FurnaceRecipe furnaceRecipe = new FurnaceRecipe(furnaceKey, new ItemStack(Material.BUCKET),
                    new RecipeChoice.ExactChoice(SkyfunItems.SKYFUN_CLAY_BUCKET), 0.35f, 200);
            Bukkit.addRecipe(furnaceRecipe);

            // Wither Skeleton Skull Machinery Alternative
            new SlimefunItem(SkyfunItems.category_tools, SkyfunItems.SKYFUN_WITHER_SKELETON_SKULL, DetraisRecipeType.SKYFUN_HEATED_PRESSURE_CHAMBER, new ItemStack[] {
                    new ItemStack(Material.SKELETON_SKULL), SlimefunItems.CARBON_CHUNK, null,
                    null, null, null,
                    null, null,  null
            }).register(plugin);

            RecipeType.HEATED_PRESSURE_CHAMBER.register(new ItemStack[] {
                    new ItemStack(Material.SKELETON_SKULL), SlimefunItems.CARBON_CHUNK, null,
                    null, null, null,
                    null, null, null
            }, new ItemStack(Material.WITHER_SKELETON_SKULL));
        }
    }

    // Helper method to dynamically register clean 1-ingredient shaped recipes
    private void registerShapedVanilla(SkyfunUtils plugin, String keyString, ItemStack result, String[] shape, char keyChar, Material ingredient) {
        NamespacedKey key = new NamespacedKey(plugin, keyString);
        if (Bukkit.getRecipe(key) == null) {
            ShapedRecipe recipe = new ShapedRecipe(key, result.clone());
            recipe.shape(shape);
            recipe.setIngredient(keyChar, ingredient);
            Bukkit.addRecipe(recipe);
        }
    }

    // Helper method to dynamically register clean 2-ingredient shaped recipes
    private void registerShapedVanilla(SkyfunUtils plugin, String keyString, ItemStack result, String[] shape, char char1, Material ing1, char char2, Material ing2) {
        NamespacedKey key = new NamespacedKey(plugin, keyString);
        if (Bukkit.getRecipe(key) == null) {
            ShapedRecipe recipe = new ShapedRecipe(key, result.clone());
            recipe.shape(shape);
            recipe.setIngredient(char1, ing1);
            recipe.setIngredient(char2, ing2);
            Bukkit.addRecipe(recipe);
        }
    }
}