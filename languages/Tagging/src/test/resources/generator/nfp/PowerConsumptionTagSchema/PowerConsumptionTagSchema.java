/* generated by template templates.de.monticore.lang.tagschema.TagSchema*/


package nfp.PowerConsumptionTagSchema;

import de.monticore.lang.tagging._symboltable.TaggingResolver;
import de.monticore.symboltable.resolving.CommonResolvingFilter;

/**
 * generated by TagSchema.ftl
 */
public class PowerConsumptionTagSchema {

  protected static PowerConsumptionTagSchema instance = null;

  protected PowerConsumptionTagSchema() {}

  protected static PowerConsumptionTagSchema getInstance() {
    if (instance == null) {
      instance = new PowerConsumptionTagSchema();
    }
    return instance;
  }

  protected void doRegisterTagTypes(TaggingResolver tagging) {
    tagging.addTagSymbolCreator(new PowerConsumptionSymbolCreator());
    tagging.addTagSymbolResolvingFilter(CommonResolvingFilter.create(PowerConsumptionSymbol.KIND));
    tagging.addTagSymbolCreator(new PowerTesterSymbolCreator());
    tagging.addTagSymbolResolvingFilter(CommonResolvingFilter.create(PowerTesterSymbol.KIND));
    tagging.addTagSymbolCreator(new PowerIdSymbolCreator());
    tagging.addTagSymbolResolvingFilter(CommonResolvingFilter.create(PowerIdSymbol.KIND));
    tagging.addTagSymbolCreator(new PowerBooleanSymbolCreator());
    tagging.addTagSymbolResolvingFilter(CommonResolvingFilter.create(PowerBooleanSymbol.KIND));
  }

  public static void registerTagTypes(TaggingResolver tagging) {
    getInstance().doRegisterTagTypes(tagging);
  }

}