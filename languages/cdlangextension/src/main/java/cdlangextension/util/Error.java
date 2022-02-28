// (c) https://github.com/MontiCore/monticore
package cdlangextension.util;

import java.util.regex.Pattern;

/**
 * This is the mixin interface for all CDLangExtension errors. This interface serves as an extension
 * point to add new error codes in submodules of the MontiThings framework.
 */
public interface Error {

  Pattern ERROR_CODE_PATTERN = Pattern.compile("0xCDE(\\d{4})(\\d{4})*");

  /**
   * @return The unique error code of this error.
   */
  String getErrorCode();

  /**
   *
   * @return The error message of this error.
   */
  String printErrorMessage();
}