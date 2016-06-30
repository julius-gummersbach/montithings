package de.monticore.lang.montiarc.helper;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import de.monticore.lang.expression.symboltable.ValueSymbol;
import de.monticore.lang.montiarc.montiarc._symboltable.ComponentInstanceSymbol;
import de.monticore.lang.montiarc.montiarc._symboltable.ComponentSymbol;
import de.monticore.lang.montiarc.montiarc._symboltable.ConnectorSymbol;
import de.monticore.lang.montiarc.montiarc._symboltable.ExpandedComponentInstanceSymbol;
import de.monticore.lang.montiarc.montiarc._symboltable.PortSymbol;
import de.monticore.lang.montiarc.tagging._symboltable.IsTaggable;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symboltable.types.JTypeSymbol;
import de.monticore.symboltable.types.TypeSymbol;
import de.monticore.symboltable.types.references.ActualTypeArgument;
import de.monticore.symboltable.types.references.JTypeReference;
import de.monticore.symboltable.types.references.TypeReference;

/**
 * Created by Michael von Wenckstern on 20.05.2016.
 * class for pretty printing symbols, this class does not use the AST
 */
public class SymbolPrinter {

  /**
   * help function for nested type arguments such as List<NewType<String, List<String>>>
   */
  public static String printTypeParameters(ActualTypeArgument arg) {
    String ret = arg.getType().getReferencedSymbol().getFullName();
//    String ret = arg.getType().getName();
    if (arg.getType().getActualTypeArguments() != null && !arg.getType().getActualTypeArguments().isEmpty()) {
      ret += "<" + arg.getType().getActualTypeArguments().stream().
          map(a -> printWildCardPrefix(a) + printTypeParameters(a) + printArrayDimensions(a)).collect(Collectors.joining(",")) + ">";
    }
    return ret;
  }

  protected static String printWildCardPrefix(ActualTypeArgument a) {
    if (a.isLowerBound()) {
      return "? super ";
    } else if (a.isUpperBound()) {
      return "? extends ";
    }
    return "";
  }

  protected static String printArrayDimensions(ActualTypeArgument a) {
    if (a.getType() instanceof JTypeReference) {
      int dim = ((JTypeReference)a.getType()).getDimension();
      StringBuilder sb = new StringBuilder();
      for(int i = 0; i < dim; i++) {
        sb.append("[]");
      }
      return sb.toString();
    }
    return "";
  }

  public static String printTypeParameters(List<ActualTypeArgument> arg) {
    if (arg.isEmpty())
      return "";
    return "<" + arg.stream().map(a -> printWildCardPrefix(a) + printTypeParameters(a) + printArrayDimensions(a)).
        collect(Collectors.joining(",")) + ">";
  }

  /**
   * help function for nested type arguments such as List<NewType<String, List<String>>>
   */
  public static String printFormalTypeParameters(JTypeSymbol arg) {
    String ret = arg.getName();

    if (!arg.getSuperTypes().isEmpty()) {
      ret += " extends " + arg.getSuperTypes().stream()
          .map(t -> t.getReferencedSymbol().getFullName() + printTypeParameters(t.getActualTypeArguments()))
          .collect(Collectors.joining("&"));
    }

    if (arg.getFormalTypeParameters() != null && !arg.getFormalTypeParameters().isEmpty()) {
      ret += "<" + arg.getFormalTypeParameters().stream().
          map(a -> printFormalTypeParameters(a)).collect(Collectors.joining(",")) + ">";
    }
    return ret;
  }

  /**
   * @return string representation of the type parameters associated with this port.
   */
  public static String printFormalTypeParameters(List<JTypeSymbol> arg) {
    if (arg.isEmpty())
      return "";
    return "<" + arg.stream()
        .map(a -> printFormalTypeParameters(a))
        .collect(Collectors.joining(",")) + ">";
  }

  public static void printPort(PortSymbol port, IndentPrinter ip) {
    if (port.isIncoming()) {
      ip.print("in ");
    }
    else {
      ip.print("out ");
    }
    ip.print(port.getTypeReference().getName());
    ip.print(printTypeParameters(port.getTypeReference().getActualTypeArguments()));
    ip.print(" ");
    ip.print(port.getName());

    printTags(port, ip);
  }

  public static String printPort(PortSymbol port) {
    IndentPrinter ip = new IndentPrinter();
    printPort(port, ip);
    return ip.getContent();
  }

  public static void printConnector(ConnectorSymbol con, IndentPrinter ip) {
    ip.print(con.getSource());
    ip.print(" -> ");
    ip.print(con.getTarget());

    printTags(con, ip);
  }

  public static String printConnector(ConnectorSymbol con) {
    IndentPrinter ip = new IndentPrinter();
    printConnector(con, ip);
    return ip.getContent();
  }

  public static String printConfigArguments(List<ValueSymbol<TypeReference<TypeSymbol>>> config) {
    if (config.isEmpty())
      return "";
    return "(" + config.stream().map(a -> a.getValue()).collect(Collectors.joining(",")) + ")";
  }

  public static void printComponentInstance(ComponentInstanceSymbol inst, IndentPrinter ip) {
    ip.print(inst.getComponentType().getName());
    ip.print(printTypeParameters(inst.getComponentType().getActualTypeArguments()));
    ip.print(printConfigArguments(inst.getConfigArguments()));
    ip.print(" ");
    ip.print(inst.getName());
  }

  public static String printComponentInstance(ComponentInstanceSymbol inst) {
    IndentPrinter ip = new IndentPrinter();
    printComponentInstance(inst, ip);
    return ip.getContent();
  }

  public static void printPorts(Collection<PortSymbol> ports, IndentPrinter ip) {
    if (!ports.isEmpty()) {
      ip.println("ports");
      ip.indent();
      int i = 0;
      int s = ports.size();
      for (PortSymbol p : ports) {
        printPort(p, ip);
        if (i == s - 1) {
          ip.println(";");
        }
        else {
          ip.println(",");
        }
        i++;
      }
      ip.unindent();
    }
  }

  public static void printComponent(ComponentSymbol cmp, IndentPrinter ip, boolean skipPackageImport) {
    if (!skipPackageImport) {
      if (cmp.getPackageName() != null && !cmp.getPackageName().isEmpty()) {
        ip.print("package ");
        ip.print(cmp.getPackageName());
        ip.println(";");
      }
      if (cmp.getImports() != null) {
        cmp.getImports().stream().forEachOrdered(a -> ip.println("import " + a.getStatement() + (a.isStar() ? ".*" : "") + ";"));
      }
    }
    ip.print("component " + cmp.getName());
    if (cmp.hasFormalTypeParameters()) {
      ip.print(printFormalTypeParameters(cmp.getFormalTypeParameters()));
    }
    if (cmp.hasConfigParameters()) {
      ip.print("(");
      ip.print(cmp.getConfigParameters().stream().map(a -> a.getType().getName() + " " + a.getName()).
          collect(Collectors.joining(",")));
      ip.print(")");
    }
    ip.println(" {");
    printTags(cmp, ip);

    ip.indent();

    printPorts(cmp.getPorts(), ip);

    cmp.getSubComponents().stream().forEachOrdered(a -> {
      ip.print("component ");
      printComponentInstance(a, ip);
      ip.println(";");
    });

    cmp.getInnerComponents().stream().forEachOrdered(a -> printComponent(a, ip, true));

    cmp.getConnectors().stream().forEachOrdered(a -> {
      ip.print("connect ");
      printConnector(a, ip);
      ip.println(";");
    });

    ip.unindent();
    ip.println("}");
  }

  public static String printComponent(ComponentSymbol cmp) {
    IndentPrinter ip = new IndentPrinter();
    printComponent(cmp, ip, false);
    return ip.getContent();
  }

  public static void printTags(IsTaggable hasTags, IndentPrinter ip) {
    if (!hasTags.getTags().isEmpty()) {
      ip.println("/* tags: ");
      ip.indent();
      ip.indent();
      ip.print(hasTags.getTags().stream().map(t -> t.toString()).
          collect(Collectors.joining("\n")));
      ip.print("  */");
      ip.unindent();
      ip.unindent();
      ip.println();
    }
  }

  public static void printExpandedComponentInstance(ExpandedComponentInstanceSymbol inst, IndentPrinter ip, boolean skipPackageImport) {
    if (!skipPackageImport) {
      ComponentSymbol cmp = inst.getComponentType().getReferencedSymbol();
      if (cmp.getPackageName() != null &&
          !cmp.getPackageName().isEmpty()) {
        ip.print("package ");
        ip.print(cmp.getPackageName());
        ip.println(";");
      }
      if (cmp.getImports() != null) {
        cmp.getImports().stream().forEachOrdered(a -> ip.println("import " + a.getStatement() + (a.isStar() ? ".*" : "") + ";"));
      }
    }
    ip.print("component /*instance*/ " + inst.getName());

    ip.println(" {");
    printTags(inst, ip);

    ip.indent();

    printPorts(inst.getPorts(), ip);

    inst.getSubComponents().stream().forEachOrdered(a -> printExpandedComponentInstance(a, ip, true));

    inst.getConnectors().stream().forEachOrdered(a -> {
      ip.print("connect ");
      printConnector(a, ip);
      ip.println(";");
    });

    ip.unindent();
    ip.println("}");
  }

  public static String printExpandedComponentInstance(ExpandedComponentInstanceSymbol inst) {
    IndentPrinter ip = new IndentPrinter();
    printExpandedComponentInstance(inst, ip, false);
    return ip.getContent();
  }
}