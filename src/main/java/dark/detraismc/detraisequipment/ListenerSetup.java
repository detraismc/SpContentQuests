package dark.detraismc.detraisequipment;

import dark.detraismc.detraisequipment.listener.DurabilityListener;
import org.bukkit.Bukkit;

final class ListenerSetup {
   static final ListenerSetup INSTANCE = new ListenerSetup();
   private boolean initialised;

   private ListenerSetup() {
   }

   public void init() {
      if (!this.initialised) {
         this.initialised = true;
         Bukkit.getPluginManager().registerEvents(new DurabilityListener(), DetraisEquipment.getInstance());
      }
   }
}
