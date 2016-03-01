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

  public static GhEditLink compute(String repository, Object branch, Object linkText, String file) {
    GhEditLink result = new GhEditLink();

    //text:
    if (linkText == null || linkText.toString().isEmpty()) {
      result.setText(DEFAULT_TEXT);
    }
    else {
      result.setText(linkText.toString());
    }

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
          if (branch == null || branch.toString().isEmpty()) {
            sb.append(DEFAULT_BRANCH);
          }
          else {
            sb.append(branch.toString());
          }
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
