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

import java.util.HashMap;
import java.util.Map;

import org.asciidoctor.ast.AbstractBlock;
import org.asciidoctor.ast.Inline;
import org.asciidoctor.extension.InlineMacroProcessor;
import org.jruby.RubySymbol;

import com.bsiag.asciidoctorj.ghedit.internal.GhEditLink;
import com.bsiag.asciidoctorj.ghedit.internal.GhEditUtility;

public class GhEditMacro extends InlineMacroProcessor {

  public GhEditMacro(String macroName, Map<String, Object> config) {
    super(macroName, config);
  }

  @Override
  protected Object process(AbstractBlock parent, String repository, Map<String, Object> attributes) {
    String docFile = searchDocFile(parent);

    Object branch = searchAttribute(attributes, "branch", 1);
    Object linkText = searchAttribute(attributes, "link-text", 2);
    GhEditLink link = GhEditUtility.compute(repository, branch, linkText, docFile);

    if (link.getWarning() != null) {
      //TODO: log a warning containing link.getWarning()
    }

    if (link.getUrl() == null) {
      return link.getText();
    }
    else {
      // Define options for an 'anchor' element:
      Map<String, Object> options = new HashMap<String, Object>();
      options.put("type", ":link");
      options.put("target", link.getUrl());

      // Create the 'anchor' node:
      Inline inline = createInline(parent, "anchor", link.getText(), attributes, options);

      // Convert to String value:
      return inline.convert();
    }
  }

  private Object searchAttribute(Map<String, Object> attributes, String attrKey, int attrPosition) {
    Object result;
    //Try to get the attribute by key:
    result = attributes.get(attrKey);
    if (result != null) {
      return result;
    }
    //Try to get the attribute by position:
    result = attributes.get(attrPosition);
    if (result != null) {
      return result;
    }
    //Not found:
    return null;
  }

  /**
   * @param parent
   * @return
   */
  private String searchDocFile(AbstractBlock parent) {
    Map<Object, Object> options = parent.getDocument().getOptions();
    for (Object optionKeyObj : options.keySet()) {
      if (optionKeyObj instanceof RubySymbol) {
        if ("attributes".equals(((RubySymbol) optionKeyObj).toJava(String.class))) {
          Object attributesObj = options.get(optionKeyObj);
          if (attributesObj instanceof Map<?, ?>) {
            Map<?, ?> attributes = (Map<?, ?>) attributesObj;
            Object docfile = attributes.get("docfile");
            if (docfile != null) {
              return docfile.toString();
            }
          }
        }
      }
    }
    return null;
  }
}
