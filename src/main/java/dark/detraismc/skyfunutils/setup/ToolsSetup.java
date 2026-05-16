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

public final class ToolsSetup {
    public static final ToolsSetup INSTANCE = new ToolsSetup();
    private boolean initialised;

    private ToolsSetup() {
    }

    public void init() {
        if (!this.initialised) {
            this.initialised = true;
            SkyfunUtils plugin = SkyfunUtils.getInstance();

            new Crook(SkyfunItems.category_tools, SkyfunItems.SKYFUN_CROOK, RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
                    new ItemStack(Material.STICK), new ItemStack(Material.STICK), null,
                    null, new ItemStack(Material.STICK), null,
                    null, new ItemStack(Material.STICK), null
            }).register(plugin);
            RecipeType.ENHANCED_CRAFTING_TABLE.register(new ItemStack[] {
                    null, new ItemStack(Material.STICK), new ItemStack(Material.STICK),
                    null, new ItemStack(Material.STICK), null,
                    null, new ItemStack(Material.STICK), null
            }, SkyfunItems.SKYFUN_CROOK);

            new Crook(SkyfunItems.category_tools, SkyfunItems.SKYFUN_BONE_CROOK, RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
                    new ItemStack(Material.BONE), new ItemStack(Material.BONE), null,
                    null, new ItemStack(Material.BONE), null,
                    null, new ItemStack(Material.BONE), null
            }).register(plugin);
            RecipeType.ENHANCED_CRAFTING_TABLE.register(new ItemStack[] {
                    null, new ItemStack(Material.BONE), new ItemStack(Material.BONE),
                    null, new ItemStack(Material.BONE), null,
                    null, new ItemStack(Material.BONE), null
            }, SkyfunItems.SKYFUN_BONE_CROOK);

            Material[] woodTypes = {
                    Material.OAK_PLANKS, Material.SPRUCE_PLANKS, Material.BIRCH_PLANKS,
                    Material.JUNGLE_PLANKS, Material.ACACIA_PLANKS, Material.DARK_OAK_PLANKS,
                    Material.MANGROVE_PLANKS, Material.CHERRY_PLANKS, Material.BAMBOO_PLANKS,
                    Material.CRIMSON_PLANKS, Material.WARPED_PLANKS
            };
            new Hammer(SkyfunItems.category_tools, SkyfunItems.SKYFUN_WOODEN_HAMMER, RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
                    null, new ItemStack(woodTypes[0]), null,
                    null, new ItemStack(Material.STICK), new ItemStack(woodTypes[0]),
                    new ItemStack(Material.STICK), null, null
            }).register(plugin);

            for (int i = 1; i < woodTypes.length; i++) {
                Material wood = woodTypes[i];

                RecipeType.ENHANCED_CRAFTING_TABLE.register(new ItemStack[] {
                        null, new ItemStack(wood), null,
                        null, new ItemStack(Material.STICK), new ItemStack(wood),
                        new ItemStack(Material.STICK), null, null
                }, SkyfunItems.SKYFUN_WOODEN_HAMMER);
            }

            new Hammer(SkyfunItems.category_tools, SkyfunItems.SKYFUN_STONE_HAMMER, RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
                    null, new ItemStack(Material.COBBLESTONE), null,
                    null, new ItemStack(Material.STICK), new ItemStack(Material.COBBLESTONE),
                    new ItemStack(Material.STICK), null, null
            }).register(plugin);
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

            new Hammer(SkyfunItems.category_tools, SkyfunItems.SKYFUN_IRON_HAMMER, RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
                    null, new ItemStack(Material.IRON_INGOT), null,
                    null, new ItemStack(Material.STICK), new ItemStack(Material.IRON_INGOT),
                    new ItemStack(Material.STICK), null, null
            }).register(plugin);

            new Hammer(SkyfunItems.category_tools, SkyfunItems.SKYFUN_GOLDEN_HAMMER, RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
                    null, new ItemStack(Material.GOLD_INGOT), null,
                    null, new ItemStack(Material.STICK), new ItemStack(Material.GOLD_INGOT),
                    new ItemStack(Material.STICK), null, null
            }).register(plugin);
            RecipeType.ENHANCED_CRAFTING_TABLE.register(new ItemStack[] {
                    null, SlimefunItems.GOLD_4K, null,
                    null, new ItemStack(Material.STICK), SlimefunItems.GOLD_4K,
                    new ItemStack(Material.STICK), null, null
            }, SkyfunItems.SKYFUN_GOLDEN_HAMMER);

            new Hammer(SkyfunItems.category_tools, SkyfunItems.SKYFUN_DIAMOND_HAMMER, RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
                    null, new ItemStack(Material.DIAMOND), null,
                    null, new ItemStack(Material.STICK), new ItemStack(Material.DIAMOND),
                    new ItemStack(Material.STICK), null, null
            }).register(plugin);


            new FrictionPickaxe(SkyfunItems.category_tools, SkyfunItems.SKYFUN_FRICTION_PICKAXE, RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
                    new ItemStack(Material.FLINT), new ItemStack(Material.FLINT), new ItemStack(Material.FLINT),
                    new ItemStack(Material.IRON_NUGGET), new ItemStack(Material.STICK), new ItemStack(Material.IRON_NUGGET),
                    null, new ItemStack(Material.STICK), null
            }).register(plugin);


            new GrassSeeds(SkyfunItems.category_tools, SkyfunItems.SKYFUN_GRASS_SEEDS, RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
                    new ItemStack(Material.OAK_SAPLING), new ItemStack(Material.OAK_SAPLING), new ItemStack(Material.OAK_SAPLING),
                    new ItemStack(Material.OAK_SAPLING), new ItemStack(Material.BONE_MEAL), new ItemStack(Material.OAK_SAPLING),
                    new ItemStack(Material.OAK_SAPLING), new ItemStack(Material.OAK_SAPLING), new ItemStack(Material.OAK_SAPLING)
            }).register(plugin);

            new Silkworm(
                    SkyfunItems.category_tools,
                    SkyfunItems.SKYFUN_SILKWORM,
                    DetraisRecipeType.CROOK_BREAKING,
                    new ItemStack[] {
                            null, new ItemStack(Material.OAK_LEAVES), null,
                            null, null, null,
                            null, null, null }
            ).register(plugin);

            RecipeType.SMELTERY.register(new ItemStack[] {
                    SkyfunItems.SKYFUN_CLAY_BUCKET, null, null,
                    null, null, null,
                    null, null, null
            }, new ItemStack(Material.BUCKET));

            new SlimefunItem(SkyfunItems.category_tools, SkyfunItems.SKYFUN_CLAY_BUCKET, RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
                    null,                               null,                               null,
                    new ItemStack(Material.CLAY_BALL),  null,                               new ItemStack(Material.CLAY_BALL),
                    null,                               new ItemStack(Material.CLAY_BALL),  null
                    }).register(plugin);

            NamespacedKey key = new NamespacedKey(plugin, "clay_bucket_smelting");
            FurnaceRecipe recipe = new FurnaceRecipe(key, new ItemStack(Material.BUCKET),
                    new RecipeChoice.ExactChoice(SkyfunItems.SKYFUN_CLAY_BUCKET), 0.35f, 200);

            Bukkit.addRecipe(recipe);


        }
    }
}