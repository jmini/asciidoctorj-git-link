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

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

import org.asciidoctor.Asciidoctor;
import org.asciidoctor.Asciidoctor.Factory;
import org.junit.Test;

public class GhEditTest {

  @Test
  public void testSimple() throws Exception {
    runTest("test_simple");
  }

  @Test
  public void testWithBranch1() throws Exception {
    runTest("test_with_branch-1");
  }

  @Test
  public void testWithBranch2() throws Exception {
    runTest("test_with_branch-2");
  }

  @Test
  public void testWithLinkText() throws Exception {
    runTest("test_with_link_text");
  }

  @Test
  public void testWithAll1() throws Exception {
    runTest("test_with_all-1");
  }

  @Test
  public void testWithAll2() throws Exception {
    runTest("test_with_all-2");
  }

  private void runTest(String fileName) throws IOException, URISyntaxException {
    String content = new String(Files.readAllBytes(Paths.get(getClass().getResource("/" + fileName + ".adoc").toURI())), StandardCharsets.UTF_8);
    String expected = new String(Files.readAllBytes(Paths.get(getClass().getResource("/" + fileName + ".html").toURI())), StandardCharsets.UTF_8);

    Asciidoctor asciidoctor = Factory.create();
    HashMap<String, Object> attributes = new java.util.HashMap<String, Object>();
    attributes.put("docfile", "C:/git/asciidoctorj-gh-edit/src/test/resources/" + fileName + ".adoc");
    HashMap<String, Object> options = new java.util.HashMap<String, Object>();
    options.put("attributes", attributes);
    String html = asciidoctor.convert(content, options);

    assertEquals(expected, html);
  }
}
