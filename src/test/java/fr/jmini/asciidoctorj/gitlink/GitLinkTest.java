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

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

import org.asciidoctor.Asciidoctor;
import org.asciidoctor.Asciidoctor.Factory;
import org.asciidoctor.log.LogRecord;
import org.asciidoctor.log.Severity;
import org.junit.jupiter.api.Test;

class GitLinkTest {

  @Test
  void testSimple() throws Exception {
    List<LogRecord> logs = runTest("test_simple");
    assertThat(logs).isEmpty();
  }

  @Test
  void testEmpty() throws Exception {
    List<LogRecord> logs = runTest("test_empty");
    assertThat(logs).hasSize(1);
    LogRecord log = logs.get(0);
    assertThat(log.getSeverity()).isEqualTo(Severity.DEBUG);
    assertThat(log.getMessage()).isEqualTo("git-link: The mode is not defined, using 'view' as fallback.");
  }

  @Test
  void testEmpty2() throws Exception {
    List<LogRecord> logs = runTest("test_empty2");
    assertThat(logs).hasSize(1);
    LogRecord log = logs.get(0);
    assertThat(log.getSeverity()).isEqualTo(Severity.DEBUG);
    assertThat(log.getMessage()).isEqualTo("git-link: The mode is not defined, using 'view' as fallback.");
  }

  @Test
  void testEmpty3() throws Exception {
    List<LogRecord> logs = runTest("test_empty3");
    assertThat(logs).isEmpty();
  }

  @Test
  void testRepositoryMissing() throws Exception {
    List<LogRecord> logs = runTest("test_repository_missing");
    assertThat(logs).hasSize(2);
    LogRecord log1 = logs.get(0);
    assertThat(log1.getSeverity()).isEqualTo(Severity.WARN);
    assertThat(log1.getMessage()).isEqualTo("git-link: There is no repository set.");
    LogRecord log2 = logs.get(1);
    assertThat(log2.getSeverity()).isEqualTo(Severity.DEBUG);
    assertThat(log2.getMessage()).isEqualTo("git-link: The mode is not defined, using 'view' as fallback.");
  }

  @Test
  void testWithBranch1() throws Exception {
    List<LogRecord> logs = runTest("test_with_branch-1");
    assertThat(logs).isEmpty();
  }

  @Test
  void testWithBranch2() throws Exception {
    List<LogRecord> logs = runTest("test_with_branch-2");
    assertThat(logs).isEmpty();
  }

  @Test
  void testWithLinkText() throws Exception {
    List<LogRecord> logs = runTest("test_with_link_text");
    assertThat(logs).isEmpty();
  }

  @Test
  void testWithAll1() throws Exception {
    List<LogRecord> logs = runTest("test_with_all-1");
    assertThat(logs).isEmpty();
  }

  @Test
  void testWithAll2() throws Exception {
    List<LogRecord> logs = runTest("test_with_all-2");
    assertThat(logs).isEmpty();
  }

  private List<LogRecord> runTest(String fileName) throws IOException, URISyntaxException {
    String content = new String(Files.readAllBytes(Paths.get(getClass().getResource("/" + fileName + ".adoc").toURI())), StandardCharsets.UTF_8);
    String expected = new String(Files.readAllBytes(Paths.get(getClass().getResource("/" + fileName + ".html").toURI())), StandardCharsets.UTF_8);

    Asciidoctor asciidoctor = Factory.create();
    InMemoryLogHanlder logHandler = new InMemoryLogHanlder();
    asciidoctor.registerLogHandler(logHandler);

    HashMap<String, Object> attributes = new java.util.HashMap<String, Object>();
    attributes.put("docfile", "C:/git/asciidoctorj-git-link/src/test/resources/" + fileName + ".adoc");
    HashMap<String, Object> options = new java.util.HashMap<String, Object>();
    options.put("attributes", attributes);
    String html = asciidoctor.convert(content, options);

    assertThat(html).isEqualTo(expected);

    return logHandler.getLogs();
  }
}
