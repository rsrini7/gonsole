package com.codeaffine.gonsole.pdetest;

import org.assertj.core.api.Assertions;
import org.eclipse.ui.console.TextConsolePage;

public class GonsoleAsserts extends Assertions {

  public static TextConsolePageAssert assertThat( TextConsolePage actual ) {
    return TextConsolePageAssert.assertThat( actual );
  }
}