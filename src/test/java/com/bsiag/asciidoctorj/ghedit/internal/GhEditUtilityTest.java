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
package com.bsiag.asciidoctorj.ghedit.internal;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class GhEditUtilityTest {

  private static final String DEFAULT_LINK_TEXT = GhEditUtility.DEFAULT_TEXT;
  private static final String CUSTOM_LINK_TEXT = "custom link Text";

  private static final String REPO = "jmini/some-repo";
  private static final String FILE = "test/some-repo/my_file.txt";
  private static final String SUB_FILE = "test/some-repo/folder/TEXT.adoc";
  private static final String WRONG_FILE = "test.txt";

  @Test
  public void testNullRepository() {
    GhEditLink link = GhEditUtility.compute(null, null, null, FILE);
    assertLinkEqual(null, DEFAULT_LINK_TEXT, GhEditUtility.WARN_NO_REPOSITORY_SET, link);
  }

  @Test
  public void testNullRepositoryWithText() {
    GhEditLink link = GhEditUtility.compute(null, null, CUSTOM_LINK_TEXT, FILE);
    assertLinkEqual(null, CUSTOM_LINK_TEXT, GhEditUtility.WARN_NO_REPOSITORY_SET, link);
  }

  @Test
  public void testEmptyRepository() {
    GhEditLink link = GhEditUtility.compute("", null, null, FILE);
    assertLinkEqual(null, DEFAULT_LINK_TEXT, GhEditUtility.WARN_NO_REPOSITORY_SET, link);
  }

  @Test
  public void testFileUnknownRepository() {
    GhEditLink link = GhEditUtility.compute(REPO, null, null, null);
    assertLinkEqual(null, DEFAULT_LINK_TEXT, GhEditUtility.WARN_FILE_UNKNWON, link);
  }

  @Test
  public void testWrongRepository() {
    GhEditLink link = GhEditUtility.compute("xxx", null, null, FILE);
    assertLinkEqual(null, DEFAULT_LINK_TEXT, GhEditUtility.WARN_UNEXPECTED_REPOSITORY + "xxx", link);
  }

  @Test
  public void testWrongRepository2() {
    GhEditLink link = GhEditUtility.compute("zzz/", null, null, FILE);
    assertLinkEqual(null, DEFAULT_LINK_TEXT, GhEditUtility.WARN_UNEXPECTED_REPOSITORY + "zzz/", link);
  }

  @Test
  public void testOkDefault() {
    GhEditLink link = GhEditUtility.compute(REPO, null, null, FILE);
    assertLinkEqual("https://github.com/jmini/some-repo/edit/master/my_file.txt", DEFAULT_LINK_TEXT, null, link);
  }

  @Test
  public void testOkDefaultForSubFile() {
    GhEditLink link = GhEditUtility.compute(REPO, null, null, SUB_FILE);
    assertLinkEqual("https://github.com/jmini/some-repo/edit/master/folder/TEXT.adoc", DEFAULT_LINK_TEXT, null, link);
  }

  @Test
  public void testOkCustomBranch() {
    GhEditLink link = GhEditUtility.compute(REPO, "features/preview", null, FILE);
    assertLinkEqual("https://github.com/jmini/some-repo/edit/features/preview/my_file.txt", DEFAULT_LINK_TEXT, null, link);
  }

  @Test
  public void testOkCustomText() {
    GhEditLink link = GhEditUtility.compute(REPO, null, CUSTOM_LINK_TEXT, FILE);
    assertLinkEqual("https://github.com/jmini/some-repo/edit/master/my_file.txt", CUSTOM_LINK_TEXT, null, link);
  }

  private static void assertLinkEqual(String url, String text, String warning, GhEditLink actual) {
    assertEquals("url", url, actual.getUrl());
    assertEquals("text", text, actual.getText());
    assertEquals("warning", warning, actual.getWarning());
  }

}
