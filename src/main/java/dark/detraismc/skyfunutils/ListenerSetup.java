package dark.detraismc.skyfunutils;

import org.bukkit.Bukkit;

final class ListenerSetup {
   static final ListenerSetup INSTANCE = new ListenerSetup();
   private boolean initialised;

   private ListenerSetup() {
   }

   public void init() {
      if (!this.initialised) {
         this.initialised = true;
         Bukkit.getPluginManager().registerEvents(new DurabilityListener(), SkyfunUtils.getInstance());
      }
   }
}
