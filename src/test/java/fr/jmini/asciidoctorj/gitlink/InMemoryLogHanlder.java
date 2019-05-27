/*******************************************************************************
 * Copyright (c) 2019 Jeremie Bresson.
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
import java.util.Collections;
import java.util.List;

import org.asciidoctor.log.LogHandler;
import org.asciidoctor.log.LogRecord;

public class InMemoryLogHanlder implements LogHandler {

  private List<LogRecord> logs = new ArrayList<>();

  @Override
  public void log(LogRecord logRecord) {
    logs.add(logRecord);
  }

  public List<LogRecord> getLogs() {
    return Collections.unmodifiableList(logs);
  }
}
