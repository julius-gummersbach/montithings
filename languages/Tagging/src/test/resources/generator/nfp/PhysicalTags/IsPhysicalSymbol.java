/* generated by template templates.de.monticore.lang.tagschema.SimpleTagType*/


package nfp.PhysicalTags;

import de.monticore.lang.tagging._symboltable.TagKind;
import de.monticore.lang.tagging._symboltable.TagSymbol;

/**
 * Created by SimpleTagType.ftl
 */
public class IsPhysicalSymbol extends TagSymbol {
  public static final IsPhysicalKind KIND = IsPhysicalKind.INSTANCE;

  /**
   * is marker symbol so it has no value by itself
   */
  public IsPhysicalSymbol() {
    super(KIND);
  }

  protected IsPhysicalSymbol(IsPhysicalKind kind) {
    super(kind);
  }

  @Override
  public String toString() {
    return "IsPhysical";
  }

  public static class IsPhysicalKind extends TagKind {
    public static final IsPhysicalKind INSTANCE = new IsPhysicalKind();

    protected IsPhysicalKind() {
    }
  }
}