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
package fr.jmini.asciidoctorj.gitlink;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.asciidoctor.Asciidoctor;
import org.asciidoctor.extension.JavaExtensionRegistry;
import org.asciidoctor.extension.spi.ExtensionRegistry;

public class GitLinkMacroExtension implements ExtensionRegistry {

  @Override
  public void register(Asciidoctor asciidoctor) {
    JavaExtensionRegistry javaExtensionRegistry = asciidoctor.javaExtensionRegistry();

    Map<String, Object> options = new HashMap<String, Object>();
    options.put("content_model", ":attributes");
    options.put("pos_attrs", new ArrayList<String>(Arrays.asList(":link-text", ":mode", ":branch", ":repository", ":link-window", ":server")));
    GitLinkMacro macro = new GitLinkMacro("git-link", options);

    javaExtensionRegistry.inlineMacro(macro);
  }
}
