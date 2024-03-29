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

public class GitLink {
  private String url;
  private String text;
  private String warningLogMessage;
  private String debugLogMessage;

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public String getWarningLogMessage() {
    return warningLogMessage;
  }

  public void setWarningLogMessage(String warning) {
    this.warningLogMessage = warning;
  }

  public String getDebugLogMessage() {
    return debugLogMessage;
  }

  public void setDebugLogMessage(String debugLogMessage) {
    this.debugLogMessage = debugLogMessage;
  }

}
