package dark.detraismc.ezfurnace.setup;

import dark.detraismc.ezfurnace.EzFurnaceItems;

public final class AllowVanillaCraftingSetup {
    public static final AllowVanillaCraftingSetup INSTANCE = new AllowVanillaCraftingSetup();
    private boolean initialised;

    private AllowVanillaCraftingSetup() {
    }

    public void init() {
        if (!this.initialised) {
            this.initialised = true;

            EzFurnaceItems.EZFURNACE_COPPER_FURNACE.getItem().setUseableInWorkbench(true);
            EzFurnaceItems.EZFURNACE_IRON_FURNACE.getItem().setUseableInWorkbench(true);
            EzFurnaceItems.EZFURNACE_GOLDEN_FURNACE.getItem().setUseableInWorkbench(true);
            EzFurnaceItems.EZFURNACE_LAPIS_FURNACE.getItem().setUseableInWorkbench(true);
            EzFurnaceItems.EZFURNACE_REDSTONE_FURNACE.getItem().setUseableInWorkbench(true);
            EzFurnaceItems.EZFURNACE_DIAMOND_FURNACE.getItem().setUseableInWorkbench(true);
            EzFurnaceItems.EZFURNACE_EMERALD_FURNACE.getItem().setUseableInWorkbench(true);
            EzFurnaceItems.EZFURNACE_NETHERITE_FURNACE.getItem().setUseableInWorkbench(true);
            
        }
    }


}