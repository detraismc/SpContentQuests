package dark.detraismc.skyfunutils.setup;

import dark.detraismc.skyfunutils.SkyfunItems;
import dark.detraismc.skyfunutils.SkyfunUtils;
import dark.detraismc.skyfunutils.implementation.AutoComposter;
import dark.detraismc.skyfunutils.implementation.AutoSieve;
import dark.detraismc.skyfunutils.implementation.SandSiever;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

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
            new SandSiever(SkyfunItems.category_machine, SkyfunItems.SKYFUN_SAND_SIEVE, RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
                    new ItemStack(woodTypes[0]), new ItemStack(Material.STRING), new ItemStack(woodTypes[0]),
                    new ItemStack(woodTypes[0]), new ItemStack(Material.STRING), new ItemStack(woodTypes[0]),
                    new ItemStack(Material.STICK), null, new ItemStack(Material.STICK)
            }).register(plugin);
            for (int i = 1; i < woodTypes.length; i++) {
                Material wood = woodTypes[i];

                RecipeType.ENHANCED_CRAFTING_TABLE.register(new ItemStack[] {
                        new ItemStack(wood), new ItemStack(Material.STRING), new ItemStack(wood),
                        new ItemStack(wood), new ItemStack(Material.STRING), new ItemStack(wood),
                        new ItemStack(Material.STICK), null, new ItemStack(Material.STICK)
                }, SkyfunItems.SKYFUN_SAND_SIEVE);
            }

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

            new AutoComposter(SkyfunItems.category_machine, SkyfunItems.SKYFUN_AUTO_COMPOSTER_2, RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
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
                    SlimefunItems.ELECTRIC_MOTOR, SkyfunItems.SKYFUN_SAND_SIEVE, SlimefunItems.ELECTRIC_MOTOR,
                    SlimefunItems.BRASS_INGOT, SlimefunItems.MEDIUM_CAPACITOR, SlimefunItems.BRASS_INGOT
            })
                    .setCapacity(256)
                    .setEnergyConsumption(9)
                    .setProcessingSpeed(1)
                    .register(plugin);

            new AutoSieve(SkyfunItems.category_machine, SkyfunItems.SKYFUN_AUTO_SIEVE_2, RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
                    SlimefunItems.SYNTHETIC_DIAMOND, new ItemStack(Material.DIAMOND_HOE), SlimefunItems.SYNTHETIC_DIAMOND,
                    SlimefunItems.REINFORCED_ALLOY_INGOT, SkyfunItems.SKYFUN_AUTO_SIEVE, SlimefunItems.REINFORCED_ALLOY_INGOT,
                    SlimefunItems.HARDENED_METAL_INGOT, SlimefunItems.BLISTERING_INGOT_3, SlimefunItems.HARDENED_METAL_INGOT
            })
                    .setCapacity(256)
                    .setEnergyConsumption(27)
                    .setProcessingSpeed(4)
                    .register(plugin);

            new AutoSieve(SkyfunItems.category_machine, SkyfunItems.SKYFUN_AUTO_SIEVE_3, RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
                    SlimefunItems.CARBONADO, new ItemStack(Material.NETHERITE_HOE), SlimefunItems.CARBONADO,
                    SlimefunItems.REINFORCED_PLATE, SkyfunItems.SKYFUN_AUTO_SIEVE_2, SlimefunItems.REINFORCED_PLATE,
                    SlimefunItems.BLISTERING_INGOT_3, SlimefunItems.BIG_CAPACITOR, SlimefunItems.BLISTERING_INGOT_3
            })
                    .setCapacity(512)
                    .setEnergyConsumption(81)
                    .setProcessingSpeed(16)
                    .register(plugin);

        }
    }
}
