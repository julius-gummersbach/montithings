${tc.signature("packageName", "schemaName", "tagTypeName", "imports", "scopeSymbol", "nameScopeType")}

package ${packageName}.${schemaName};

import java.util.Optional;

<#list imports as import>
import ${import};
</#list>

import de.monticore.lang.tagging._ast.ASTNameScope;
import de.monticore.lang.tagging._ast.ASTScope;
import de.monticore.lang.tagging._ast.ASTTag;
import de.monticore.lang.tagging._ast.ASTTaggingUnit;
import de.monticore.lang.tagging._symboltable.TagSymbolCreator;
import de.monticore.lang.tagging._symboltable.TaggingResolver;
import de.monticore.symboltable.Scope;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.logging.Log;

/**
 * created by SimpleTagTypeCreator.ftl
 */
public class ${tagTypeName}SymbolCreator implements TagSymbolCreator {

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
        .filter(n -> n.endsWith("${schemaName}"))
        .count() == 0) {
      return; // the tagging model is not conform to the ${schemaName} tagging schema
    }
    final String packageName = Joiners.DOT.join(unit.getPackageList());
    final String rootCmp = // if-else does not work b/c of final (required by streams)
        (unit.getTagBody().getTargetModelOpt().isPresent()) ?
            Joiners.DOT.join(packageName, ((ASTNameScope) unit.getTagBody().getTargetModelOpt().get())
                .getQualifiedName().toString()) :
            packageName;

     for (ASTTag element : unit.getTagBody().getTagList()) {
            element.getTagElementList().stream()
              .filter(t -> t.getName().equals("${tagTypeName}"))
              .filter(t -> !t.getTagValueOpt().isPresent()) // only marker tag with no value
              .forEachOrdered(t ->
                  element.getScopeList().stream()
                    .filter(this::checkScope)
                    .map(s -> (ASTNameScope) s)
                    .map(s -> tagging.resolve(Joiners.DOT.join(rootCmp, // resolve down does not try to reload symbol
                            s.getQualifiedName().toString()), ${scopeSymbol}.KIND))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .forEachOrdered(s -> tagging.addTag(s, new ${tagTypeName}Symbol())));
      }
    }

  protected boolean checkScope(ASTScope scope) {
    if (scope.getScopeKind().equals("${nameScopeType}")) {
      return true;
    }
    Log.error(String.format("0xT0005 Invalid scope kind: '%s'. ${tagTypeName} expects as scope kind '${nameScopeType}'.",
        scope.getScopeKind()), scope.get_SourcePositionStart());
    return false;
  }
}