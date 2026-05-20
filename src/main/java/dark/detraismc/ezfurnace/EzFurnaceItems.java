package dark.detraismc.ezfurnace;

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

public final class EzFurnaceItems {

    public static ItemGroup ezfurnace_category;

    public static final SlimefunItemStack EZFURNACE_COPPER_FURNACE;
    public static final SlimefunItemStack EZFURNACE_IRON_FURNACE;
    public static final SlimefunItemStack EZFURNACE_GOLDEN_FURNACE;
    public static final SlimefunItemStack EZFURNACE_LAPIS_FURNACE;
    public static final SlimefunItemStack EZFURNACE_REDSTONE_FURNACE;
    public static final SlimefunItemStack EZFURNACE_DIAMOND_FURNACE;
    public static final SlimefunItemStack EZFURNACE_EMERALD_FURNACE;
    public static final SlimefunItemStack EZFURNACE_NETHERITE_FURNACE;



    static {
        // --- Categories ---
        ezfurnace_category = new ItemGroup(
                new NamespacedKey(EzFurnace.getInstance(), "ezfurnace_category"),
                new CustomItemStack(Material.FURNACE, "&cEzFurnace")
        );



        EZFURNACE_COPPER_FURNACE = new SlimefunItemStack(
                "EZFURNACE_COPPER_FURNACE",
                Material.FURNACE,
                "&6Copper Furnace",
                "",
                "&7Processing Speed: &e2x",
                "&7Fuel Efficiency: &e2x",
                "&7Luck Multiplier: &e1x"
        );
        EZFURNACE_IRON_FURNACE = new SlimefunItemStack(
                "EZFURNACE_IRON_FURNACE",
                Material.FURNACE,
                "&fIron Furnace",
                "",
                "&7Processing Speed: &e3x",
                "&7Fuel Efficiency: &e3x",
                "&7Luck Multiplier: &e1x"
        );
        EZFURNACE_GOLDEN_FURNACE = new SlimefunItemStack(
                "EZFURNACE_GOLDEN_FURNACE",
                Material.FURNACE,
                "&eGolden Furnace",
                "",
                "&7Processing Speed: &e4x",
                "&7Fuel Efficiency: &e4x",
                "&7Luck Multiplier: &e1x"
        );
        EZFURNACE_LAPIS_FURNACE = new SlimefunItemStack(
                "EZFURNACE_LAPIS_FURNACE",
                Material.FURNACE,
                "&9Lapis Furnace",
                "",
                "&7Processing Speed: &e5x",
                "&7Fuel Efficiency: &e5x",
                "&7Luck Multiplier: &e2x"
        );
        EZFURNACE_REDSTONE_FURNACE = new SlimefunItemStack(
                "EZFURNACE_REDSTONE_FURNACE",
                Material.FURNACE,
                "&2Redstone Furnace",
                "",
                "&7Processing Speed: &e6x",
                "&7Fuel Efficiency: &e6x",
                "&7Luck Multiplier: &e2x"
        );
        EZFURNACE_DIAMOND_FURNACE = new SlimefunItemStack(
                "EZFURNACE_DIAMOND_FURNACE",
                Material.FURNACE,
                "&bDiamond Furnace",
                "",
                "&7Processing Speed: &e7x",
                "&7Fuel Efficiency: &e7x",
                "&7Luck Multiplier: &e2x"
        );
        EZFURNACE_EMERALD_FURNACE = new SlimefunItemStack(
                "EZFURNACE_EMERALD_FURNACE",
                Material.FURNACE,
                "&aEmerald Furnace",
                "",
                "&7Processing Speed: &e8x",
                "&7Fuel Efficiency: &e8x",
                "&7Luck Multiplier: &e2x"
        );
        EZFURNACE_NETHERITE_FURNACE = new SlimefunItemStack(
                "EZFURNACE_NETHERITE_FURNACE",
                Material.FURNACE,
                "&4Netherite Furnace",
                "",
                "&7Processing Speed: &e10x",
                "&7Fuel Efficiency: &e10x",
                "&7Luck Multiplier: &e3x"
        );



    }

    private EzFurnaceItems() {}
}