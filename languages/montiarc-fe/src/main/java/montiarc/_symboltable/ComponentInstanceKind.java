/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package montiarc._symboltable;

/**
 * Symbol kind of component instances.
 *
 * @author Robert Heim
 */
public class ComponentInstanceKind
    implements de.monticore.symboltable.SymbolKind {

  public static final ComponentInstanceKind INSTANCE = new ComponentInstanceKind();

  private static final String NAME = ComponentInstanceKind.class.getName();

  @Override
  public String getName() {
    return NAME;
  }

  protected ComponentInstanceKind() {
  }

}
