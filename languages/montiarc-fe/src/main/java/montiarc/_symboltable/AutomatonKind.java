/* generated from model null*/
/* generated by template symboltable.SymbolKind*/


package montiarc._symboltable;

import de.monticore.symboltable.SymbolKind;

public class AutomatonKind implements SymbolKind {

  public static final AutomatonKind KIND = new AutomatonKind();

  private static final String NAME = AutomatonKind.class.getName();

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public boolean isKindOf(SymbolKind kind) {
    return KIND.equals(kind.getName()) || SymbolKind.super.isKindOf(kind);
  }

}
