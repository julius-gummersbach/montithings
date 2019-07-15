/* generated by template templates.de.monticore.lang.tagschema.ValuedTagTypeCreator*/


package nfp.PowerConsumptionTagSchema;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;


import de.monticore.lang.tagging._ast.ASTNameScope;
import de.monticore.lang.tagging._ast.ASTScope;
import de.monticore.lang.tagging._ast.ASTTag;
import de.monticore.lang.tagging._ast.ASTTaggingUnit;
import de.monticore.lang.tagging._symboltable.TagSymbolCreator;
import de.monticore.lang.tagging._symboltable.TaggingResolver;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.Symbol;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.logging.Log;

import de.monticore.lang.tagvalue._parser.TagValueParser;
import de.monticore.lang.tagvalue._ast.ASTBooleanTagValue;

/**
 * created by ValuedTagTypeCreator.ftl
 */
public class PowerBooleanSymbolCreator implements TagSymbolCreator {

  public static Scope getGlobalScope(final Scope scope) {
    Scope s = scope;
    while (s.getEnclosingScope().isPresent()) {
      s = s.getEnclosingScope().get();
    }
    return s;
  }

  public void create(ASTTaggingUnit unit, TaggingResolver tagging) {
    if (unit.getQualifiedNameList().stream()
        .map(q -> q.toString())
        .filter(n -> n.endsWith("PowerConsumptionTagSchema"))
        .count() == 0) {
      return; // the tagging model is not conform to the PowerConsumptionTagSchema tagging schema
    }
    final String packageName = Joiners.DOT.join(unit.getPackageList());
    final String rootCmp = // if-else does not work b/c of final (required by streams)
        (unit.getTagBody().getTargetModelOpt().isPresent()) ?
            Joiners.DOT.join(packageName, ((ASTNameScope) unit.getTagBody().getTargetModelOpt().get())
                .getQualifiedName().toString()) :
            packageName;

    for (ASTTag element : unit.getTagBody().getTagList()) {
           element.getTagElementList().stream()
              .filter(t -> t.getName().equals("PowerBoolean"))
              .filter(t -> t.isPresentTagValue())
              .map(t -> checkContent(t.getTagValueOpt().get()))
              .filter(r -> r != null)
          .forEachOrdered(v ->
              element.getScopeList().stream()
                .filter(this::checkScope)
                .map(s -> (ASTNameScope) s)
                .map(s -> tagging.resolve(Joiners.DOT.join(rootCmp, // resolve down does not try to reload symbol
                        s.getQualifiedName().toString()), ConnectorSymbol.KIND))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEachOrdered(s -> tagging.addTag(s, new PowerBooleanSymbol(v))));
    }
  }

  protected Boolean checkContent(String s) {
    TagValueParser parser = new TagValueParser();
    Optional<ASTBooleanTagValue> ast;
    try {
      boolean enableFailQuick = Log.isFailQuickEnabled();
      Log.enableFailQuick(false);
      long errorCount = Log.getErrorCount();

      ast = parser.parse_StringBooleanTagValue(s);

      Log.enableFailQuick(enableFailQuick);
      if (Log.getErrorCount() > errorCount) {
        throw new Exception("Error occured during parsing.");
      }
    } catch (Exception e) {
      Log.error(String.format("0xT0004 Could not parse %s with TagValueParser#parseBooleanTagValue.",
          s), e);
      return null;
    }
    if (!ast.isPresent()) {
      return null;
    }
    return ast.get().getT().isPresent();
  }

  protected ConnectorSymbol checkKind(Collection<Symbol> symbols) {
    ConnectorSymbol ret = null;
    for (Symbol symbol : symbols) {
      if (symbol.getKind().isSame(ConnectorSymbol.KIND)) {
        if (ret != null) {
          Log.error(String.format("0xA4095 Found more than one symbol: '%s' and '%s'",
              ret, symbol));
          return null;
        }
        ret = (ConnectorSymbol)symbol;
      }
    }
    if (ret == null) {
      Log.error(String.format("0xT0001 Invalid symbol kinds: %s. tagTypeName expects as symbol kind 'ConnectorSymbol.KIND'.",
          symbols.stream().map(s -> "'" + s.getKind().toString() + "'").collect(Collectors.joining(", "))));
      return null;
    }
    return ret;
  }

  protected boolean checkScope(ASTScope scope) {
    if (scope.getScopeKind().equals("ConnectorScope")) {
      return true;
    }
    Log.error(String.format("0xT0005 Invalid scope kind: '%s'. PowerBoolean expects as scope kind 'ConnectorScope'.",
        scope.getScopeKind()), scope.get_SourcePositionStart());
    return false;
  }
}