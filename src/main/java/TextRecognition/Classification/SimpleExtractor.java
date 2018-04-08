package TextRecognition.Classification;

import TextRecognition.Model.Article;

import static java.lang.Math.*;


public class SimpleExtractor {

    public double getNGramDistance(Article a, Article b, int n) {
        String textA = a.getBody();
        String textB = b.getBody();
        int cost;
        final char special = '\n';
        final int lengthOfTextA = textA.length();
        final int lengthOfTextB = textB.length();
        char[] sa = new char[lengthOfTextA + n - 1];
        float[] p = new float[lengthOfTextA + 1];
        float[] d = new float[lengthOfTextA + 1];
        for (int i = 0; i < sa.length; i++) {
            if (i < n - 1) {
                sa[i] = special;
            } else {
                sa[i] = textA.charAt(i - n + 1);
            }
        }
        char[] shortWord = new char[n];
        for (int i = 0; i <= lengthOfTextA; i++) {
            p[i] = i;
        }
        for (int i = 1; i <= lengthOfTextB; i++) {
            if (i < n) {
                for (int k = 0; k < n - i; k++) {
                    shortWord[k] = special;
                }
                for (int k = n - i; k < n; k++) {
                    shortWord[k] = textB.charAt(k - (n - i));
                }
            } else {
                shortWord = textB.substring(i - n, i).toCharArray();
            }
            d[0] = i;
            for (int j = 1; j <= lengthOfTextA; j++) {
                cost = 0;
                int temp = n;
                for (int k = 0; k < n; k++) {
                    if (sa[j - 1 + k] != shortWord[k]) {
                        cost++;
                    } else if (sa[j - 1 + k] == special) {
                        temp--;
                    }
                }
                float ec = (float) cost / temp;
                d[j] = min(min(d[j - 1] + 1, p[j] + 1), p[j - 1] + ec);
            }
            float[] temp;
            temp = p;
            p = d;
            d = temp;
        }
        return p[lengthOfTextA] / max(lengthOfTextB, lengthOfTextA);
    }

    public double getLevenshteinDistance(Article a, Article b) {
        double result = 0;
        String[] aWords = a.getBody().split("\\s+");
        String[] bWords = b.getBody().split("\\s+");
        for (int i = 0; i < aWords.length; i++) {
            for (int j = 0; j < bWords.length; j++) {
                result += levenshteinDistance(aWords[i], bWords[j]);
            }
        }
        return result;
    }

    public int levenshteinDistance(CharSequence lhs, CharSequence rhs) {
        int len0 = lhs.length() + 1;
        int len1 = rhs.length() + 1;
        int[] cost = new int[len0];
        int[] newcost = new int[len0];

        for (int i = 0; i < len0; i++) cost[i] = i;
        for (int j = 1; j < len1; j++) {
            newcost[0] = j;
            for (int i = 1; i < len0; i++) {
                int match = (lhs.charAt(i - 1) == rhs.charAt(j - 1)) ? 0 : 1;
                int cost_replace = cost[i - 1] + match;
                int cost_insert = cost[i] + 1;
                int cost_delete = newcost[i - 1] + 1;
                newcost[i] = min(min(cost_insert, cost_delete), cost_replace);
            }
            int[] swap = cost;
            cost = newcost;
            newcost = swap;
        }
        return cost[len0 - 1];
    }

}
