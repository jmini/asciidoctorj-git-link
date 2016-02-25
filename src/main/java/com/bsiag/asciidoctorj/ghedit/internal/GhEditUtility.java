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

public final class GhEditUtility {
  static final String DEFAULT_TEXT = "edit on GitHub";
  static final String DEFAULT_BRANCH = "master";
  static final String WARN_NO_REPOSITORY_SET = "gh-edit: There is no repository set.";
  static final String WARN_FILE_UNKNWON = "gh-edit: Adoc file is not known";
  static final String WARN_UNEXPECTED_REPOSITORY = "gh-edit: unexpected repository, should match the GitHub pattern {user}/{repository}, current value: ";

  public static GhEditLink compute(String repository, Object textParam, String file) {
    GhEditLink result = new GhEditLink();

    String text;
    String branch;
    if (textParam == null) {
      text = DEFAULT_TEXT;
      branch = DEFAULT_BRANCH;
    }
    else {
      String param = textParam.toString();
      if (param.isEmpty()) {
        text = DEFAULT_TEXT;
        branch = DEFAULT_BRANCH;
      }
      else {
        String[] parts = param.split(",");
        if (parts.length > 1) {
          text = parts[1].trim();
        }
        else {
          text = DEFAULT_TEXT;
        }
        if (parts.length > 0) {
          branch = parts[0].trim();
        }
        else {
          branch = DEFAULT_BRANCH;
        }
      }
    }

    //text:
    result.setText(text);

    //Repository:
    if (repository == null || repository.isEmpty()) {
      result.setWarning(WARN_NO_REPOSITORY_SET);
    }
    else if (file == null) {
      result.setWarning(WARN_FILE_UNKNWON);
    }
    else {
      int i = repository.lastIndexOf('/');
      if (i > 0 && i < repository.length() - 1) {
        String repositoryName = repository.substring(i + 1);

        int j = file.lastIndexOf(repositoryName);
        if (j > 0) {
          StringBuilder sb = new StringBuilder();
          sb.append("https://github.com/");
          sb.append(repository);
          sb.append("/edit/");
          sb.append(branch);
          sb.append(file.substring(j + repositoryName.length()).replaceAll("\\\\", "/"));
          result.setUrl(sb.toString());
        }
      }
      else {
        result.setWarning(WARN_UNEXPECTED_REPOSITORY + repository);
      }
    }

    return result;
  }

  private GhEditUtility() {
  }
}
