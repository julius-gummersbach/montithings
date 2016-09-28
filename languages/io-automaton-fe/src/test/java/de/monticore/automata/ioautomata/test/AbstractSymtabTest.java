package de.monticore.automata.ioautomata.test;

import java.nio.file.Paths;

import de.monticore.ModelingLanguageFamily;
import de.monticore.automaton.ioautomaton.JavaHelper;
import de.monticore.automaton.ioautomatonjava._symboltable.IOAutomatonJavaLanguageFamily;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.Scope;

public class AbstractSymtabTest {
  protected static Scope createSymTab(String modelPath) {
    ModelingLanguageFamily fam = new IOAutomatonJavaLanguageFamily();
    // TODO how to add java default types?
    final ModelPath mp = new ModelPath(Paths.get(modelPath), Paths.get("src/main/resources/defaultTypes"));
    GlobalScope scope = new GlobalScope(mp, fam);
    JavaHelper.addJavaPrimitiveTypes(scope);
    return scope;
  }
}
