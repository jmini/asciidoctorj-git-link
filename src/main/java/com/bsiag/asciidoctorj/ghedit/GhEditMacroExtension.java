/*******************************************************************************
 * Copyright (c) 2016 Jeremie Bresson.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jeremie Bresson - initial API and implementation
 ******************************************************************************/
package com.bsiag.asciidoctorj.ghedit;

import org.asciidoctor.Asciidoctor;
import org.asciidoctor.extension.JavaExtensionRegistry;
import org.asciidoctor.extension.spi.ExtensionRegistry;

public class GhEditMacroExtension implements ExtensionRegistry {

  @Override
  public void register(Asciidoctor asciidoctor) {
    JavaExtensionRegistry javaExtensionRegistry = asciidoctor.javaExtensionRegistry();

    javaExtensionRegistry.inlineMacro("gh-edit", GhEditMacro.class);
  }
}
