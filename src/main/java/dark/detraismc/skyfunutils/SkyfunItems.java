package dark.detraismc.skyfunutils;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.items.groups.NestedItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.groups.SubItemGroup;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;

public final class SkyfunItems {

    public static final NamespacedKey DURABILITY_KEY = new NamespacedKey(SkyfunUtils.getInstance(), "custom_durability");
    public static final NamespacedKey MAX_DURABILITY_KEY = new NamespacedKey(SkyfunUtils.getInstance(), "max_durability");

    public static NestedItemGroup category;
    public static ItemGroup category_materials, category_machine, category_tools;

    public static final SlimefunItemStack SKYFUN_CROOK;
    public static final SlimefunItemStack SKYFUN_BONE_CROOK;

    public static final SlimefunItemStack SKYFUN_WOODEN_HAMMER;
    public static final SlimefunItemStack SKYFUN_STONE_HAMMER;
    public static final SlimefunItemStack SKYFUN_IRON_HAMMER;
    public static final SlimefunItemStack SKYFUN_GOLDEN_HAMMER;
    public static final SlimefunItemStack SKYFUN_DIAMOND_HAMMER;
    public static final SlimefunItemStack SKYFUN_FRICTION_PICKAXE;

    public static final SlimefunItemStack SKYFUN_SILKWORM; // Added Silkworm declaration
    public static final SlimefunItemStack SKYFUN_CLAY_BUCKET;
    public static final SlimefunItemStack SKYFUN_GRASS_SEEDS;
    public static final SlimefunItemStack SKYFUN_SAND_SIEVER;
    public static final SlimefunItemStack SKYFUN_AUTO_COMPOSTER;
    public static final SlimefunItemStack SKYFUN_AUTO_COMPOSTER_2;
    public static final SlimefunItemStack SKYFUN_AUTO_COMPOSTER_3;
    public static final SlimefunItemStack SKYFUN_AUTO_SIEVE;
    public static final SlimefunItemStack SKYFUN_AUTO_SIEVE_2;
    public static final SlimefunItemStack SKYFUN_AUTO_SIEVE_3;

    static {
        // --- Categories ---
        category = new NestedItemGroup(
                new NamespacedKey(SkyfunUtils.getInstance(), "skyfun_category"),
                new CustomItemStack(Material.GRASS_BLOCK, "&bSkyfun Utils")
        );

        category_machine = new SubItemGroup(
                new NamespacedKey(SkyfunUtils.getInstance(), "skyfun_category_machine"),
                category,
                new CustomItemStack(Material.LOOM, "&bSkyfun Utils &8- &cMachine")
        );

        category_tools = new SubItemGroup(
                new NamespacedKey(SkyfunUtils.getInstance(), "skyfun_category_tools"),
                category,
                new CustomItemStack(Material.DIAMOND_PICKAXE, "&bSkyfun Utils &8- &6Tools")
        );

        category_materials = new SubItemGroup(
                new NamespacedKey(SkyfunUtils.getInstance(), "skyfun_category_materials"),
                category,
                new CustomItemStack(Material.STICK, "&bSkyfun Utils &8- &aMaterials")
        );


        // --- Tools ---
        SKYFUN_CROOK = new SlimefunItemStack(
                "SKYFUN_CROOK",
                Material.WOODEN_HOE,
                "&fCrook",
                "",
                "&7Increases sapling drop rate from leaves",
                "&7Has a chance to drop a Silkworm"
        );

        SKYFUN_BONE_CROOK = new SlimefunItemStack(
                "SKYFUN_BONE_CROOK",
                Material.IRON_HOE,
                "&fBone Crook",
                "",
                "&7A much stronger Crook",
                "&7Increases sapling drop rate from leaves",
                "&7Has a chance to drop a Silkworm"
        );

        SKYFUN_WOODEN_HAMMER = new SlimefunItemStack(
                "SKYFUN_WOODEN_HAMMER",
                Material.WOODEN_PICKAXE,
                "&fWooden Hammer",
                "",
                "&7Crushes Cobblestone into Gravel",
                "&7Crushes Gravel into Sand",
                "&7You can craft this from any",
                "&7wood type."
        );

        SKYFUN_STONE_HAMMER = new SlimefunItemStack(
                "SKYFUN_STONE_HAMMER",
                Material.STONE_PICKAXE,
                "&fStone Hammer",
                "",
                "&7Crushes Cobblestone into Gravel",
                "&7Crushes Gravel into Sand"
        );

        SKYFUN_IRON_HAMMER = new SlimefunItemStack(
                "SKYFUN_IRON_HAMMER",
                Material.IRON_PICKAXE,
                "&fIron Hammer",
                "",
                "&7Crushes Cobblestone into Gravel",
                "&7Crushes Gravel into Sand"
        );

        SKYFUN_GOLDEN_HAMMER = new SlimefunItemStack(
                "SKYFUN_GOLDEN_HAMMER",
                Material.GOLDEN_PICKAXE,
                "&fGolden Hammer",
                "",
                "&7Crushes Cobblestone into Gravel",
                "&7Crushes Gravel into Sand"
        );

        SKYFUN_DIAMOND_HAMMER = new SlimefunItemStack(
                "SKYFUN_DIAMOND_HAMMER",
                Material.DIAMOND_PICKAXE,
                "&bDiamond Hammer",
                "",
                "&7Crushes Cobblestone into Gravel",
                "&7Crushes Gravel into Sand"
        );

        SKYFUN_FRICTION_PICKAXE = new SlimefunItemStack(
                "SKYFUN_FRICTION_PICKAXE",
                Material.IRON_PICKAXE,
                "&cFriction Pickaxe",
                "",
                "&7Creates friction when mining Cobblestone",
                "&7Has a chance to drop Redstone Dust"
        );

        SKYFUN_SAND_SIEVER = new SlimefunItemStack(
                "SKYFUN_SAND_SIEVER",
                Material.LOOM,
                "&eSand Siever",
                "",
                "&7Used to manually sieve sand",
                "&7Drops: Iron, Bone Meal, Gunpowder,",
                "&7Redstone, Lapis, Diamond Nuggets"
        );


        // --- Materials & Items ---
        SKYFUN_SILKWORM = new SlimefunItemStack(
                "SKYFUN_SILKWORM",
                Material.PRISMARINE_CRYSTALS,
                "&fSilkworm",
                "",
                "&7Right-click on leaves",
                "&7to slowly turn them into Cobwebs"
        );

        SKYFUN_CLAY_BUCKET = new SlimefunItemStack(
                "SKYFUN_CLAY_BUCKET",
                Material.CLAY_BALL,
                "&fUnfired Clay Bucket",
                "",
                "&7Smelt this in a furnace or smeltery",
                "&7to get a real Bucket"
        );

        SKYFUN_GRASS_SEEDS = new SlimefunItemStack(
                "SKYFUN_GRASS_SEEDS",
                Material.WHEAT_SEEDS,
                "&aGrass Seeds",
                "",
                "&7Right-click on a Dirt block",
                "&7to convert it into a Grass Block"
        );


        // --- Machines ---
        SKYFUN_AUTO_COMPOSTER = new SlimefunItemStack(
                "SKYFUN_AUTO_COMPOSTER",
                Material.ACACIA_PLANKS,
                "&aAuto Composter &7(Tier I)",
                "",
                "&7Automatically turns organics into Dirt"
        );

        SKYFUN_AUTO_COMPOSTER_2 = new SlimefunItemStack(
                "SKYFUN_AUTO_COMPOSTER_2",
                Material.ACACIA_PLANKS,
                "&aAuto Composter &7(Tier II)",
                "",
                "&7Automatically turns organics into Dirt",
                "&eSpeed: 2x"
        );

        SKYFUN_AUTO_COMPOSTER_3 = new SlimefunItemStack(
                "SKYFUN_AUTO_COMPOSTER_3",
                Material.ACACIA_PLANKS,
                "&aAuto Composter &7(Tier III)",
                "",
                "&7Automatically turns organics into Dirt",
                "&eSpeed: 4x"
        );

        SKYFUN_AUTO_SIEVE = new SlimefunItemStack(
                "SKYFUN_AUTO_SIEVE",
                Material.JUKEBOX,
                "&eAuto Sieve &7(Tier I)",
                "",
                "&7Automatically sieves Sand and Gravel"
        );

        SKYFUN_AUTO_SIEVE_2 = new SlimefunItemStack(
                "SKYFUN_AUTO_SIEVE_2",
                Material.JUKEBOX,
                "&eAuto Sieve &7(Tier II)",
                "",
                "&7Automatically sieves Sand and Gravel",
                "&eSpeed: 2x"
        );

        SKYFUN_AUTO_SIEVE_3 = new SlimefunItemStack(
                "SKYFUN_AUTO_SIEVE_3",
                Material.JUKEBOX,
                "&eAuto Sieve &7(Tier III)",
                "",
                "&7Automatically sieves Sand and Gravel",
                "&eSpeed: 4x"
        );
    }

    private SkyfunItems() {}
}