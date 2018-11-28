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
package fr.jmini.asciidoctorj.gitlink.internal;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import fr.jmini.asciidoctorj.gitlink.internal.GitLink;
import fr.jmini.asciidoctorj.gitlink.internal.GitLinkUtility;

public class GitLinkUtilityTest {

  private static final String DEFAULT_VIEW_LINK_TEXT = "view on GitHub";
  private static final String DEFAULT_EDIT_LINK_TEXT = "edit on GitHub";
  private static final String CUSTOM_LINK_TEXT = "custom link Text";

  private static final String REPO = "jmini/some-repo";
  private static final String FILE = "test/some-repo/my_file.txt";
  private static final String SUB_FILE = "test/some-repo/folder/TEXT.adoc";
  private static final String WRONG_FILE = "test.txt";

  @Test
  public void testComputeFilepath() throws Exception {
    assertEquals("/my_file.txt", GitLinkUtility.computeFilePath(FILE, "some-repo"));
    assertEquals("/folder/TEXT.adoc", GitLinkUtility.computeFilePath(SUB_FILE, "some-repo"));
    assertEquals("/my_file.txt", GitLinkUtility.computeFilePath("some-repo/my_file.txt", "some-repo"));
    assertEquals("/my_file.txt", GitLinkUtility.computeFilePath("xxx/some-repo/yyyy/some-repo/my_file.txt", "some-repo"));
    assertEquals("/file.txt", GitLinkUtility.computeFilePath("xxx/myproject_repo/file.txt", "myproject"));
    assertEquals("/file.txt", GitLinkUtility.computeFilePath("/myproject_repo/file.txt", "myproject"));
    assertEquals("/some/folder/file.txt", GitLinkUtility.computeFilePath("xxx/myproject.git/some/folder/file.txt", "myproject"));

    //Handle the special case "target/checkout"
    assertEquals("/my_file.txt", GitLinkUtility.computeFilePath("xxx/some-repo/target/checkout/my_file.txt", "some-repo"));
    assertEquals("/my_file.txt", GitLinkUtility.computeFilePath("xxx/some-repo/proj/target/checkout/my_file.txt", "some-repo"));
    assertEquals("/my_file.txt", GitLinkUtility.computeFilePath("xxx/target/checkout/my_file.txt", "some-repo"));
    assertEquals("/my_file.txt", GitLinkUtility.computeFilePath("target/checkout/my_file.txt", "some-repo"));
    assertEquals("/some/folder/file.txt", GitLinkUtility.computeFilePath("xxx/some-repo/target/checkout/some/folder/file.txt", "some-repo"));
    assertEquals("/my_file.txt", GitLinkUtility.computeFilePath("xxx\\some-repo\\target\\checkout\\my_file.txt", "some-repo"));
  }

  @Test
  public void testNullRepository() {
    GitLink link = GitLinkUtility.compute(null, "edit", null, null, null, null, FILE);
    assertLinkEqual(null, DEFAULT_EDIT_LINK_TEXT, GitLinkUtility.WARN_NO_REPOSITORY_SET, link);
  }

  @Test
  public void testNullRepositoryWithText() {
    GitLink link = GitLinkUtility.compute(null, "edit", null, null, null, CUSTOM_LINK_TEXT, FILE);
    assertLinkEqual(null, CUSTOM_LINK_TEXT, GitLinkUtility.WARN_NO_REPOSITORY_SET, link);
  }

  @Test
  public void testEmptyRepository() {
    GitLink link = GitLinkUtility.compute(null, "edit", null, "", null, null, FILE);
    assertLinkEqual(null, DEFAULT_EDIT_LINK_TEXT, GitLinkUtility.WARN_NO_REPOSITORY_SET, link);
  }

  @Test
  public void testFileUnknownFileAndPath() {
    GitLink link = GitLinkUtility.compute(null, "edit", null, REPO, null, null, null);
    assertLinkEqual(null, DEFAULT_EDIT_LINK_TEXT, GitLinkUtility.WARN_FILE_UNKNWON, link);
  }

  @Test
  public void testWrongRepository() {
    GitLink link = GitLinkUtility.compute(null, "edit", null, "xxx", null, null, FILE);
    assertLinkEqual(null, DEFAULT_EDIT_LINK_TEXT, GitLinkUtility.WARN_UNEXPECTED_REPOSITORY + "xxx", link);
  }

  @Test
  public void testWrongRepository2() {
    GitLink link = GitLinkUtility.compute(null, "edit", null, "zzz/", null, null, FILE);
    assertLinkEqual(null, DEFAULT_EDIT_LINK_TEXT, GitLinkUtility.WARN_UNEXPECTED_REPOSITORY + "zzz/", link);
  }

  @Test
  public void testOkDefaultEdit() {
    GitLink link = GitLinkUtility.compute(null, "edit", null, REPO, null, null, FILE);
    assertLinkEqual("https://github.com/jmini/some-repo/edit/master/my_file.txt", DEFAULT_EDIT_LINK_TEXT, null, link);
  }

  @Test
  public void testOkDefaultView() {
    GitLink link = GitLinkUtility.compute(null, "view", null, REPO, null, null, FILE);
    assertLinkEqual("https://github.com/jmini/some-repo/blob/master/my_file.txt", DEFAULT_VIEW_LINK_TEXT, null, link);
  }

  @Test
  public void testNullMode() {
    GitLink link = GitLinkUtility.compute(null, null, null, REPO, null, null, FILE);
    assertLinkEqual("https://github.com/jmini/some-repo/blob/master/my_file.txt", DEFAULT_VIEW_LINK_TEXT, GitLinkUtility.WARN_UNEXPECTED_MODE, link);
  }

  @Test
  public void testWrongMode() {
    GitLink link = GitLinkUtility.compute(null, "xxx", null, REPO, null, null, FILE);
    assertLinkEqual("https://github.com/jmini/some-repo/blob/master/my_file.txt", DEFAULT_VIEW_LINK_TEXT, GitLinkUtility.WARN_UNEXPECTED_MODE, link);
  }

  @Test
  public void testOkWithPath1() {
    GitLink link = GitLinkUtility.compute("path/to/this/File.adoc", "edit", null, REPO, null, null, FILE);
    assertLinkEqual("https://github.com/jmini/some-repo/edit/master/path/to/this/File.adoc", DEFAULT_EDIT_LINK_TEXT, null, link);
  }

  @Test
  public void testOkWithPath2() {
    GitLink link = GitLinkUtility.compute("/path/to/this/File.txt", "edit", null, REPO, null, null, SUB_FILE);
    assertLinkEqual("https://github.com/jmini/some-repo/edit/master/path/to/this/File.txt", DEFAULT_EDIT_LINK_TEXT, null, link);
  }

  @Test
  public void testOkWithPathUnknownFile() {
    GitLink link = GitLinkUtility.compute("/path/to/this/File.txt", "edit", null, REPO, null, null, null);
    assertLinkEqual("https://github.com/jmini/some-repo/edit/master/path/to/this/File.txt", DEFAULT_EDIT_LINK_TEXT, null, link);
  }

  @Test
  public void testOkDefaultForSubFile() {
    GitLink link = GitLinkUtility.compute(null, "edit", null, REPO, null, null, SUB_FILE);
    assertLinkEqual("https://github.com/jmini/some-repo/edit/master/folder/TEXT.adoc", DEFAULT_EDIT_LINK_TEXT, null, link);
  }

  @Test
  public void testOkCustomBranch() {
    GitLink link = GitLinkUtility.compute(null, "edit", null, REPO, "features/preview", null, FILE);
    assertLinkEqual("https://github.com/jmini/some-repo/edit/features/preview/my_file.txt", DEFAULT_EDIT_LINK_TEXT, null, link);
  }

  @Test
  public void testOkCustomText() {
    GitLink link = GitLinkUtility.compute(null, "edit", null, REPO, null, CUSTOM_LINK_TEXT, FILE);
    assertLinkEqual("https://github.com/jmini/some-repo/edit/master/my_file.txt", CUSTOM_LINK_TEXT, null, link);
  }

  @Test
  public void testOkViewDir() {
    GitLink link = GitLinkUtility.compute("some/folder", "viewdir", null, REPO, null, null, FILE);
    assertLinkEqual("https://github.com/jmini/some-repo/tree/master/some/folder", DEFAULT_VIEW_LINK_TEXT, null, link);
  }

  @Test
  public void testOkViewDirTrailingSlash() {
    GitLink link = GitLinkUtility.compute("some/folder/", "viewdir", null, REPO, null, null, FILE);
    assertLinkEqual("https://github.com/jmini/some-repo/tree/master/some/folder", DEFAULT_VIEW_LINK_TEXT, null, link);
  }

  @Test
  public void testOkViewDirStartWithDot() {
    GitLink link = GitLinkUtility.compute("./some/folder", "viewdir", null, REPO, null, null, FILE);
    assertLinkEqual("https://github.com/jmini/some-repo/tree/master/some/folder", DEFAULT_VIEW_LINK_TEXT, null, link);
  }

  @Test
  public void testOkViewDirDotSlash() {
    GitLink link = GitLinkUtility.compute("./", "viewdir", null, REPO, null, null, FILE);
    assertLinkEqual("https://github.com/jmini/some-repo/tree/master", DEFAULT_VIEW_LINK_TEXT, null, link);
  }

  @Test
  public void testOkViewDirDotSlashOtherBranch() {
    GitLink link = GitLinkUtility.compute("./", "viewdir", null, REPO, "gh-pages", null, FILE);
    assertLinkEqual("https://github.com/jmini/some-repo/tree/gh-pages", DEFAULT_VIEW_LINK_TEXT, null, link);
  }

  @Test
  public void testEditButShouldBeViewDir() {
    GitLink link = GitLinkUtility.compute("some/folder/", "edit", null, REPO, null, null, FILE);
    assertLinkEqual("https://github.com/jmini/some-repo/tree/master/some/folder", DEFAULT_VIEW_LINK_TEXT, null, link);
  }

  @Test
  public void testViewButShouldBeViewDir() {
    GitLink link = GitLinkUtility.compute("some/folder/", "view", null, REPO, null, null, FILE);
    assertLinkEqual("https://github.com/jmini/some-repo/tree/master/some/folder", DEFAULT_VIEW_LINK_TEXT, null, link);
  }

  @Test
  public void testOkOtherServer() {
    GitLink link = GitLinkUtility.compute(null, "edit", "https://git.server.com", REPO, null, null, FILE);
    assertLinkEqual("https://git.server.com/jmini/some-repo/edit/master/my_file.txt", DEFAULT_EDIT_LINK_TEXT, null, link);
  }

  @Test
  public void testOkOtherServerTrailingSlash() {
    GitLink link = GitLinkUtility.compute(null, "edit", "https://git.server.com/", REPO, null, null, FILE);
    assertLinkEqual("https://git.server.com/jmini/some-repo/edit/master/my_file.txt", DEFAULT_EDIT_LINK_TEXT, null, link);
  }

  @Test
  public void testOkOtherServerOtherBranch() {
    GitLink link = GitLinkUtility.compute(null, "view", "https://server.com/git-manager", REPO, "features/test", null, FILE);
    assertLinkEqual("https://server.com/git-manager/jmini/some-repo/blob/features/test/my_file.txt", DEFAULT_VIEW_LINK_TEXT, null, link);
  }

  private static void assertLinkEqual(String url, String text, String warning, GitLink actual) {
    assertEquals("url", url, actual.getUrl());
    assertEquals("text", text, actual.getText());
    assertEquals("warning", warning, actual.getWarning());
  }

}
