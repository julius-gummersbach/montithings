/* generated by template templates.de.monticore.lang.tagschema.ValuedTagType*/


package nfp.TransmissionCostsTagSchema;

import de.monticore.lang.tagging._symboltable.TagKind;
import de.monticore.lang.tagging._symboltable.TagSymbol;

import org.jscience.physics.amount.Amount;
import javax.measure.quantity.Power;
import javax.measure.unit.Unit;

/**
 * Created by ValuedTagType.ftl
 */
public class TransCostCmpInstSymbol extends TagSymbol {
  public static final TransCostCmpInstKind KIND = TransCostCmpInstKind.INSTANCE;

  public TransCostCmpInstSymbol(Amount<Power> value) {
    this(KIND, value);
  }

  public TransCostCmpInstSymbol(Number number, Unit<Power> unit) {
    this(KIND, number, unit);
  }

  protected TransCostCmpInstSymbol(TransCostCmpInstKind kind, Amount<Power> value) {
    super(kind, value);
  }

  protected TransCostCmpInstSymbol(TransCostCmpInstKind kind, Number number, Unit<Power> unit) {
    this(kind, number.toString().contains(".") ?
      Amount.valueOf(number.doubleValue(), unit) :
      Amount.valueOf(number.intValue(),
          unit));
  }

  public Amount<Power> getValue() {
     return getValue(0);
  }

  public Number getNumber() {
    return getValue().getExactValue();
  }

  public Unit<Power> getUnit() {
    return getValue().getUnit();
  }

  @Override
  public String toString() {
    return String.format("TransCostCmpInst = %s %s",
      getNumber(), getUnit());
  }

  public static class TransCostCmpInstKind extends TagKind {
    public static final TransCostCmpInstKind INSTANCE = new TransCostCmpInstKind();

    protected TransCostCmpInstKind() {
    }
  }
}