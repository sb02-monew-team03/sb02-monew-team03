package com.team03.monew.util;

import org.apache.commons.text.similarity.LevenshteinDistance;

public class SimilarityUtil {

  public static double calculateSimilarity(String s1, String s2) {
    if (s1 == null || s2 == null) return 0.0;

    s1 = s1.trim().toLowerCase();
    s2 = s2.trim().toLowerCase();

    int maxLength = Math.max(s1.length(), s2.length());
    if (maxLength == 0) return 1.0;

    int distance = LevenshteinDistance.getDefaultInstance().apply(s1, s2);
    return 1.0 - ((double) distance / maxLength);
  }
}

