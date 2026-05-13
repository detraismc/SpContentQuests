package dark.detraismc.detraisequipment;

final class RecipeTypeSetup {
   static final RecipeTypeSetup INSTANCE = new RecipeTypeSetup();
   private boolean initialised;

   private RecipeTypeSetup() {
   }

   public void init() {
      if (!this.initialised) {
         this.initialised = true;

      }
   }
}
