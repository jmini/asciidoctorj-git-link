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

public final class GitLinkUtility {
  private static final String TARGET_CHECKOUT = "target/checkout";
  static final String DEFAULT_EDIT_TEXT = "edit on GitHub";
  static final String DEFAULT_VIEW_TEXT = "view on GitHub";
  static final String DEFAULT_BRANCH = "master";
  static final String WARN_UNEXPECTED_MODE = "git-link: The mode '%s' is unexpected, using 'view' as fallback.";
  static final String DEBUG_UNDEFINED_MODE = "git-link: The mode is not defined, using 'view' as fallback.";
  static final String WARN_NO_REPOSITORY_SET = "git-link: There is no repository set.";
  static final String WARN_FILE_UNKNWON = "git-link: path and asciidoctor docfile are unknown";
  static final String WARN_UNEXPECTED_REPOSITORY = "git-link: unexpected repository, should match the GitHub pattern {user}/{git-repository}, current value: ";

  public static GitLink compute(String target, Object modeInput, Object server, Object repository, Object branch, Object linkText, Object file) {
    GitLink result = new GitLink();

    //Mode:
    GitLinkMode mode = null;
    if (target != null && !"self".equals(target)) {
      if (target.endsWith("/")) {
        mode = GitLinkMode.VIEWDIR;
      }
    }
    if (mode == null) {
      if (modeInput != null && !modeInput.toString().isEmpty()) {
        String modeInputAsString = modeInput.toString();
        try {
          mode = GitLinkMode.valueOf(modeInputAsString.toUpperCase());
        }
        catch (IllegalArgumentException e) {
          result.setWarningLogMessage(String.format(WARN_UNEXPECTED_MODE, modeInputAsString));
          mode = GitLinkMode.VIEW;
        }
      }
      else {
        result.setDebugLogMessage(String.format(DEBUG_UNDEFINED_MODE));
        mode = GitLinkMode.VIEW;
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
      result.setWarningLogMessage(WARN_NO_REPOSITORY_SET);
    }
    else if ((target == null || "self".equals(target)) && (file == null || file.toString().isEmpty())) {
      result.setWarningLogMessage(WARN_FILE_UNKNWON);
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
        result.setWarningLogMessage(WARN_UNEXPECTED_REPOSITORY + repoString);
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

  private GitLinkUtility() {
  }
}
