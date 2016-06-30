/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.lang.montiarc.helper;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.stream.Collectors;

import de.monticore.java.symboltable.JavaTypeSymbolReference;
import de.monticore.symboltable.types.JTypeSymbol;
import de.monticore.symboltable.types.references.ActualTypeArgument;
import de.monticore.symboltable.types.references.JTypeReference;

/**
 * Checks type compatibility of {@link ArcTypeReference}s.
 *
 * @author ahaber, Robert Heim
 */
public class TypeCompatibilityChecker {
  private static int getPositionInFormalTypeParameters(List<JTypeSymbol> formalTypeParameters,
      JTypeReference<? extends JTypeSymbol> searchedFormalTypeParameter) {
    int positionInFormal = 0;
    for (JTypeSymbol formalTypeParameter : formalTypeParameters) {
      if (formalTypeParameter.getName().equals(searchedFormalTypeParameter.getName())) {
        break;
      }
      positionInFormal++;
    }
    return positionInFormal;
  }

  /**
   * Checks compatibility of {@link ArcTypeReference}s. The sourceTypeFomalTypeParameters list all
   * type parameters, while the sourceTypeArguments define the current binding of them. E.g., a
   * generic source Type {@code A<X, Y>} could be bound to
   * <code>{@code A<List<Optional<Integer>>, String>}</code>. For a targetType to match, it must
   * recursively match all generic bindings. In the example, the first recursion would check that
   * formal type-parameters of {@code List} are bound to the same type argument (here
   * {@code Optional}) for both, the source and the target type. The second recursion would check
   * {@code Optional}'s type arguments to be {@code Integer}. Then, the the other type-arguments of
   * {@code A} (here {@code Y}) are checked.
   *
   * @param sourceType
   * @param sourceTypeFormalTypeParameters
   * @param sourceTypeArguments
   * @param targetType
   * @param targetTypeFormalTypeParameters
   * @param targetTypeArguments
   * @return
   */
  public static boolean doTypesMatch(JTypeReference<? extends JTypeSymbol> sourceType,
      List<JTypeSymbol> sourceTypeFormalTypeParameters,
      List<JTypeReference<? extends JTypeSymbol>> sourceTypeArguments, JTypeReference<? extends JTypeSymbol> targetType,
      List<JTypeSymbol> targetTypeFormalTypeParameters,
      List<JTypeReference<? extends JTypeSymbol>> targetTypeArguments) {

    // TODO reuse Java type checker?

    checkNotNull(sourceType);
    checkNotNull(targetType);
    boolean result = false;
    if (sourceType.getReferencedSymbol().isFormalTypeParameter()) {
      // bind the generic to the actual type
      int positionInFormal = getPositionInFormalTypeParameters(sourceTypeFormalTypeParameters,
          sourceType);
      sourceType = sourceTypeArguments.get(positionInFormal);
    }
    if (targetType.getReferencedSymbol().isFormalTypeParameter()) {
      // bind the generic to the actual type
      int positionInFormal = getPositionInFormalTypeParameters(targetTypeFormalTypeParameters,
          targetType);
      targetType = targetTypeArguments.get(positionInFormal);
    }

    if (sourceType.getReferencedSymbol().getFullName()
        .equals(targetType.getReferencedSymbol().getFullName()) &&
        sourceType.getDimension() == targetType.getDimension() &&
        sourceType.getActualTypeArguments().size() == targetType.getActualTypeArguments().size()) {
      result = true;
      // type without generics does match, now we must check that the type arguments match
      List<ActualTypeArgument> sourceParams = sourceType.getActualTypeArguments();
      List<ActualTypeArgument> targetParams = targetType.getActualTypeArguments();
      for (int i = 0; i < sourceParams.size(); i++) {
        JTypeReference<? extends JTypeSymbol> sourceTypesCurrentTypeArgument = (JTypeReference<?>) sourceParams
            .get(i)
            .getType();
        JTypeReference<? extends JTypeSymbol> targetTypesCurrentTypeArgument = (JTypeReference<?>) targetParams
            .get(i)
            .getType();
        if (!doTypesMatch(sourceTypesCurrentTypeArgument,
            sourceTypesCurrentTypeArgument.getReferencedSymbol().getFormalTypeParameters().stream().map(p -> (JTypeSymbol) p).collect(Collectors.toList()),
            sourceTypesCurrentTypeArgument.getActualTypeArguments().stream()
                .map(a -> (JTypeReference<?>) a.getType()).collect(Collectors.toList()),
            targetTypesCurrentTypeArgument,
            targetTypesCurrentTypeArgument.getReferencedSymbol().getFormalTypeParameters().stream().map(p -> (JTypeSymbol) p).collect(Collectors.toList()),
            targetTypesCurrentTypeArgument.getActualTypeArguments().stream()
                .map(a -> (JTypeReference<?>) a.getType()).collect(Collectors.toList()))) {
          result = false;
          break;
        }
      }
    }
    else if (!sourceType.getReferencedSymbol().getFullName()
        .equals(targetType.getReferencedSymbol().getFullName())) {
      // check, if superclass from sourceType is compatible with targetType
      if (sourceType.getReferencedSymbol().getSuperClass().isPresent()) {
        JTypeReference<? extends JTypeSymbol> parent = sourceType.getReferencedSymbol().getSuperClass().get();
        result = doTypesMatch(parent,
            parent.getReferencedSymbol().getFormalTypeParameters().stream().map(p -> (JTypeSymbol) p).collect(Collectors.toList()),
            parent.getActualTypeArguments().stream().map(a -> (JavaTypeSymbolReference) a.getType())
                .collect(Collectors.toList()),
            targetType,
            targetTypeFormalTypeParameters,
            targetTypeArguments);
      }
      if (!result && !sourceType.getReferencedSymbol().getInterfaces().isEmpty()) {
        for (JTypeReference<? extends JTypeSymbol> interf : sourceType.getReferencedSymbol().getInterfaces()) {
          result = doTypesMatch(
              interf,
              interf.getReferencedSymbol().getFormalTypeParameters().stream().map(p -> (JTypeSymbol) p).collect(Collectors.toList()),
              interf.getActualTypeArguments().stream()
                  .map(a -> (JavaTypeSymbolReference) a.getType())
                  .collect(Collectors.toList()),
              targetType,
              targetTypeFormalTypeParameters,
              targetTypeArguments);
          if (result) {
            break;
          }
        }
      }
    }
    return result;
  }

}