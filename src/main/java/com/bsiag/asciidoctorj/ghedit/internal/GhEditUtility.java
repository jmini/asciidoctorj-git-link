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
  static final String DEFAULT_EDIT_TEXT = "edit on GitHub";
  static final String DEFAULT_VIEW_TEXT = "view on GitHub";
  static final String DEFAULT_BRANCH = "master";
  static final String WARN_UNEXPECTED_MODE = "gh-edit: The mode is unexpected, using 'edit' as fallback.";
  static final String WARN_NO_REPOSITORY_SET = "gh-edit: There is no repository set.";
  static final String WARN_FILE_UNKNWON = "gh-edit: path and asciidoctor docfile are unknown";
  static final String WARN_UNEXPECTED_REPOSITORY = "gh-edit: unexpected repository, should match the GitHub pattern {user}/{git-repository}, current value: ";

  public static GhEditLink compute(String target, Object modeInput, Object server, Object repository, Object branch, Object linkText, Object file) {
    GhEditLink result = new GhEditLink();

    //Mode:
    GhMode mode = null;
    if (target != null && !"self".equals(target)) {
      if (target.endsWith("/")) {
        mode = GhMode.VIEWDIR;
      }
    }
    if (mode == null) {
      if (modeInput != null && !modeInput.toString().isEmpty()) {
        try {
          mode = GhMode.valueOf(modeInput.toString().toUpperCase());
        }
        catch (IllegalArgumentException e) {
          result.setWarning(WARN_UNEXPECTED_MODE);
          mode = GhMode.VIEW;
        }
      }
      else {
        result.setWarning(WARN_UNEXPECTED_MODE);
        mode = GhMode.VIEW;
      }
    }

    //text:
    if (linkText == null || linkText.toString().isEmpty()) {
      switch (mode) {
        case EDIT:
          result.setText(DEFAULT_EDIT_TEXT);
          break;
        case VIEW:
        case VIEWDIR:
          result.setText(DEFAULT_VIEW_TEXT);
          break;
        default:
          throw new IllegalStateException("unexpected mode");
      }
    }
    else {
      result.setText(linkText.toString());
    }

    //Repository:
    if (repository == null || repository.toString().isEmpty()) {
      result.setWarning(WARN_NO_REPOSITORY_SET);
    }
    else if ((target == null || "self".equals(target)) && (file == null || file.toString().isEmpty())) {
      result.setWarning(WARN_FILE_UNKNWON);
    }
    else {
      String repoString = (String) repository;
      int i = repoString.lastIndexOf('/');
      String filePath = null;

      if (target != null && !"self".equals(target)) {
        filePath = target;
      }
      else if (i > 0 && i < repoString.length() - 1) {
        String repositoryName = repoString.substring(i + 1);
        filePath = computeFilePath(file.toString(), repositoryName);
      }
      else {
        result.setWarning(WARN_UNEXPECTED_REPOSITORY + repoString);
      }

      if (filePath != null) {
        StringBuilder sb = new StringBuilder();
        if (server != null && !server.toString().isEmpty()) {
          String serverString = server.toString();
          sb.append(serverString);
          if (!serverString.endsWith("/")) {
            sb.append("/");
          }
        }
        else {
          sb.append("https://github.com/");
        }
        sb.append(repoString);
        sb.append("/");
        switch (mode) {
          case EDIT:
            sb.append("edit");
            break;
          case VIEW:
            sb.append("blob");
            break;
          case VIEWDIR:
            sb.append("tree");
            break;
          default:
            throw new IllegalStateException("unexpected mode");
        }
        sb.append("/");
        if (branch == null || branch.toString().isEmpty()) {
          sb.append(DEFAULT_BRANCH);
        }
        else {
          sb.append(branch.toString());
        }
        int beginIndex;
        if (filePath.startsWith("./")) {
          beginIndex = 1;
        }
        else if (!filePath.startsWith("/")) {
          sb.append("/");
          beginIndex = 0;
        }
        else {
          beginIndex = 0;
        }
        int endIndex;
        if (filePath.endsWith("/")) {
          endIndex = filePath.length() - 1;
        }
        else {
          endIndex = filePath.length();
        }
        sb.append(filePath.substring(beginIndex, endIndex));
        result.setUrl(sb.toString());
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
