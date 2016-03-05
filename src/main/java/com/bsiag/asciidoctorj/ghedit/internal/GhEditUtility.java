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
  private static final String TARGET_CHECKOUT = "target/checkout";
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

        String filePath = computeFilePath(file, repositoryName);
        if (filePath != null) {
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
          sb.append(filePath);
          result.setUrl(sb.toString());
        }
      }
      else {
        result.setWarning(WARN_UNEXPECTED_REPOSITORY + repository);
      }
    }

    return result;
  }

  static String computeFilePath(String file, String repositoryName) {
    String normalizedFile = file.replaceAll("\\\\", "/");
    int i1 = normalizedFile.lastIndexOf(repositoryName);
    int i2 = normalizedFile.lastIndexOf(TARGET_CHECKOUT);
    if (i1 > -1 && i1 > i2) {
      int j = normalizedFile.indexOf('/', i1);
      return normalizedFile.substring(j);
    }
    else if (i2 > -1 && i2 > i1) {
      int j = i2 + TARGET_CHECKOUT.length();
      return normalizedFile.substring(j);
    }
    else {
      return null;
    }
  }

  private GhEditUtility() {
  }
}
