package dark.detraismc.skyfunutils.setup;

public final class RecipeTypeSetup {
   public static final RecipeTypeSetup INSTANCE = new RecipeTypeSetup();
   private boolean initialised;

   private RecipeTypeSetup() {
   }

   public void init() {
      if (!this.initialised) {
         this.initialised = true;

      }
   }
}
