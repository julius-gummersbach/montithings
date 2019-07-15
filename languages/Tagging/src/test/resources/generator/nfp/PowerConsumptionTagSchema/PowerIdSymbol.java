/* generated by template templates.de.monticore.lang.tagschema.ValuedTagType*/


package nfp.PowerConsumptionTagSchema;

import de.monticore.lang.tagging._symboltable.TagKind;
import de.monticore.lang.tagging._symboltable.TagSymbol;


/**
 * Created by ValuedTagType.ftl
 */
public class PowerIdSymbol extends TagSymbol {
  public static final PowerIdKind KIND = PowerIdKind.INSTANCE;

  public PowerIdSymbol(Number value) {
    super(KIND, value);
  }

  protected PowerIdSymbol(PowerIdKind kind, Number value) {
    super(kind, value);
  }

  public Number getValue() {
     return getValue(0);
  }

  @Override
  public String toString() {
    return String.format("PowerId = %s",
      getValue().toString());
  }

  public static class PowerIdKind extends TagKind {
    public static final PowerIdKind INSTANCE = new PowerIdKind();

    protected PowerIdKind() {
    }
  }
}