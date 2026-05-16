package dark.detraismc.skyfunutils;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.items.groups.NestedItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.groups.SubItemGroup;
import io.github.thebusybiscuit.slimefun4.core.attributes.MachineTier;
import io.github.thebusybiscuit.slimefun4.core.attributes.MachineType;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.utils.LoreBuilder;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;

public final class SkyfunItems {

    public static NestedItemGroup category;
    public static ItemGroup category_machine, category_tools, category_mechanic;

    public static final SlimefunItemStack INFO_TWERK_FOR_TREES;

    public static final SlimefunItemStack SKYFUN_CROOK;
    public static final SlimefunItemStack SKYFUN_BONE_CROOK;

    public static final SlimefunItemStack SKYFUN_WOODEN_HAMMER;
    public static final SlimefunItemStack SKYFUN_STONE_HAMMER;
    public static final SlimefunItemStack SKYFUN_IRON_HAMMER;
    public static final SlimefunItemStack SKYFUN_GOLDEN_HAMMER;
    public static final SlimefunItemStack SKYFUN_DIAMOND_HAMMER;

    public static final SlimefunItemStack SKYFUN_SILKWORM;
    public static final SlimefunItemStack SKYFUN_CLAY_BUCKET;
    public static final SlimefunItemStack SKYFUN_WITHER_SKELETON_SKULL;
    public static final SlimefunItemStack SKYFUN_GRASS_SEEDS;

    public static final SlimefunItemStack SKYFUN_SAND_SIEVE;
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

        category_mechanic = new SubItemGroup(
                new NamespacedKey(SkyfunUtils.getInstance(), "skyfun_category_mechanic"),
                category,
                new CustomItemStack(Material.BOOK, "&bSkyfun Utils &8- &eCustom Mechanic")
        );

        category_tools = new SubItemGroup(
                new NamespacedKey(SkyfunUtils.getInstance(), "skyfun_category_tools"),
                category,
                new CustomItemStack(Material.DIAMOND_PICKAXE, "&bSkyfun Utils &8- &6Tools")
        );

        category_machine = new SubItemGroup(
                new NamespacedKey(SkyfunUtils.getInstance(), "skyfun_category_machine"),
                category,
                new CustomItemStack(Material.JUKEBOX, "&bSkyfun Utils &8- &cMachine")
        );

        // --- Informational Display Stack ---
        INFO_TWERK_FOR_TREES = new SlimefunItemStack(
                "INFO_TWERK_FOR_TREES",
                Material.OAK_SAPLING,
                "&aFeature: Twerk for Trees",
                "",
                "&7Spamming your sneak key (&fShift&7)",
                "&7near any growing vanilla sapling",
                "&7gives a chance &7per shift to act",
                "&7like bonemeal!",
                "",
                "&eVisual indicator: &aGreen particles"
        );


        // --- Tools ---
        SKYFUN_CROOK = new SlimefunItemStack(
                "SKYFUN_CROOK",
                Material.WOODEN_HOE,
                "&fCrook",
                "",
                "&9Increases sapling drop rate from leaves",
                "&9Has a chance to drop a Silkworm"
        );

        SKYFUN_BONE_CROOK = new SlimefunItemStack(
                "SKYFUN_BONE_CROOK",
                Material.IRON_HOE,
                "&fBone Crook",
                "",
                "&9A much stronger Crook",
                "&9Increases sapling drop rate from leaves",
                "&9Has a chance to drop a Silkworm"
        );

        SKYFUN_WOODEN_HAMMER = new SlimefunItemStack(
                "SKYFUN_WOODEN_HAMMER",
                Material.WOODEN_PICKAXE,
                "&fWooden Hammer",
                "",
                "&9Crushes Cobblestone into Gravel",
                "&9Crushes Gravel into Sand",
                "",
                "&6You can craft this from any wood",
                "&6type."
        );

        SKYFUN_STONE_HAMMER = new SlimefunItemStack(
                "SKYFUN_STONE_HAMMER",
                Material.STONE_PICKAXE,
                "&fStone Hammer",
                "",
                "&9Crushes Cobblestone into Gravel",
                "&9Crushes Gravel into Sand"
        );

        SKYFUN_IRON_HAMMER = new SlimefunItemStack(
                "SKYFUN_IRON_HAMMER",
                Material.IRON_PICKAXE,
                "&fIron Hammer",
                "",
                "&9Crushes Cobblestone into Gravel",
                "&9Crushes Gravel into Sand"
        );

        SKYFUN_GOLDEN_HAMMER = new SlimefunItemStack(
                "SKYFUN_GOLDEN_HAMMER",
                Material.GOLDEN_PICKAXE,
                "&fGolden Hammer",
                "",
                "&9Crushes Cobblestone into Gravel",
                "&9Crushes Gravel into Sand"
        );

        SKYFUN_DIAMOND_HAMMER = new SlimefunItemStack(
                "SKYFUN_DIAMOND_HAMMER",
                Material.DIAMOND_PICKAXE,
                "&bDiamond Hammer",
                "",
                "&9Crushes Cobblestone into Gravel",
                "&9Crushes Gravel into Sand"
        );


        SKYFUN_SAND_SIEVE = new SlimefunItemStack(
                "SKYFUN_SAND_SIEVE",
                Material.LOOM,
                "&eSand Sieve",
                "",
                "&7Used to manually sieve sand",
                "&7Drops: Iron, Bone Meal, Gunpowder,",
                "&7Redstone, Lapis, Diamond"
        );


        // --- Materials & Items ---
        SKYFUN_SILKWORM = new SlimefunItemStack(
                "SKYFUN_SILKWORM",
                Material.PRISMARINE_CRYSTALS,
                "&fSilkworm",
                "",
                "&9Right-click on leaves",
                "&9to slowly turn them into Cobwebs"
        );

        SKYFUN_CLAY_BUCKET = new SlimefunItemStack(
                "SKYFUN_CLAY_BUCKET",
                Material.CLAY_BALL,
                "&fUnfired Clay Bucket",
                "",
                "&9Smelt this in a furnace or smeltery",
                "&9to get a real Bucket"
        );

        SKYFUN_WITHER_SKELETON_SKULL = new SlimefunItemStack(
                "SKYFUN_WITHER_SKELETON_SKULL",
                Material.WITHER_SKELETON_SKULL,
                "&bWither Skeleton Skull"
        );

        SKYFUN_GRASS_SEEDS = new SlimefunItemStack(
                "SKYFUN_GRASS_SEEDS",
                Material.WHEAT_SEEDS,
                "&aGrass Seeds",
                "",
                "&9Right-click on a Dirt block",
                "&9to convert it into a Grass Block"
        );


        // --- Machines ---
        SKYFUN_AUTO_COMPOSTER = new SlimefunItemStack(
                "SKYFUN_AUTO_COMPOSTER",
                Material.ACACIA_PLANKS,
                "&aAuto Composter &7- &eI",
                "",
                LoreBuilder.machine(MachineTier.ADVANCED, MachineType.MACHINE),
                LoreBuilder.speed(1),
                LoreBuilder.powerBuffer(256),
                LoreBuilder.powerPerSecond(18)
        );

        SKYFUN_AUTO_COMPOSTER_2 = new SlimefunItemStack(
                "SKYFUN_AUTO_COMPOSTER_2",
                Material.ACACIA_PLANKS,
                "&aAuto Composter &7- &eII",
                "",
                LoreBuilder.machine(MachineTier.ADVANCED, MachineType.MACHINE),
                LoreBuilder.speed(4),
                LoreBuilder.powerBuffer(256),
                LoreBuilder.powerPerSecond(54)
        );

        SKYFUN_AUTO_COMPOSTER_3 = new SlimefunItemStack(
                "SKYFUN_AUTO_COMPOSTER_3",
                Material.ACACIA_PLANKS,
                "&aAuto Composter &7- &eIII",
                "",
                LoreBuilder.machine(MachineTier.END_GAME, MachineType.MACHINE),
                LoreBuilder.speed(16),
                LoreBuilder.powerBuffer(512),
                LoreBuilder.powerPerSecond(162)
        );

        SKYFUN_AUTO_SIEVE = new SlimefunItemStack(
                "SKYFUN_AUTO_SIEVE",
                Material.JUKEBOX,
                "&eAuto Sieve &7- &eI",
                "",
                LoreBuilder.machine(MachineTier.ADVANCED, MachineType.MACHINE),
                LoreBuilder.speed(1),
                LoreBuilder.powerBuffer(256),
                LoreBuilder.powerPerSecond(10)
        );

        SKYFUN_AUTO_SIEVE_2 = new SlimefunItemStack(
                "SKYFUN_AUTO_SIEVE_2",
                Material.JUKEBOX,
                "&eAuto Sieve &7- &eII",
                "",
                LoreBuilder.machine(MachineTier.ADVANCED, MachineType.MACHINE),
                LoreBuilder.speed(4),
                LoreBuilder.powerBuffer(256),
                LoreBuilder.powerPerSecond(30)
        );

        SKYFUN_AUTO_SIEVE_3 = new SlimefunItemStack(
                "SKYFUN_AUTO_SIEVE_3",
                Material.JUKEBOX,
                "&eAuto Sieve &7- &eIII",
                "",
                LoreBuilder.machine(MachineTier.END_GAME, MachineType.MACHINE),
                LoreBuilder.speed(16),
                LoreBuilder.powerBuffer(512),
                LoreBuilder.powerPerSecond(90)
        );
    }

    private SkyfunItems() {}
}