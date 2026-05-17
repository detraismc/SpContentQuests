package dark.detraismc.skyfunutils.setup;

import dark.detraismc.skyfunutils.SkyfunUtils;
import dark.detraismc.skyfunutils.listener.TreeTwerker;
import org.bukkit.Bukkit;

public class ListenerSetup {
   public static final ListenerSetup INSTANCE = new ListenerSetup();
   private boolean initialised;

   private ListenerSetup() {
   }

   public void init() {
      if (!this.initialised) {
         this.initialised = true;
         Bukkit.getPluginManager().registerEvents(new TreeTwerker(), SkyfunUtils.getInstance());
      }
   }
}
