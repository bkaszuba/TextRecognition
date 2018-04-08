package TextRecognition.Classification;


import TextRecognition.Model.Article;

import java.util.*;

public class TFIDFCalculator {
    private HashMap textA;
    private HashMap textB;
    private double[][] wordsCounter;
    private List<List<String>> articlesWords = null;

    public TFIDFCalculator(List<List<String>> articles, Article a) {
        this.articlesWords = articles;
        this.textA = extractArticleBody(a);
    }

    public void vectorizeArticle(Article b) {
        this.textB = extractArticleBody(b);
    }

    private HashMap extractArticleBody(Article article) {
        HashMap<String, Double> wordsDictionary = new HashMap<>();
        String[] words = article.getBody().split("\\s+");
        //tf
        double result = 0;
        for (int i = 0; i < words.length; i++) {
            for (String word : words) {
                if (words[i].equalsIgnoreCase(word)) {
                    result++;
                }
            }
            wordsDictionary.put(words[i], result / words.length);
            result = 0;
        }
        //idf
        double n = 0;
        for (int i = 0; i < words.length; i++) {
            for (List<String> text : articlesWords) {
                for (String word : text) {
                    if (words[i].equalsIgnoreCase(word)) {
                        n++;
                        break;
                    }
                }
            }
            wordsDictionary.put(words[i], wordsDictionary.get(words[i]) * Math.log(articlesWords.size() / n));
            n = 0;
        }
        return wordsDictionary;
    }


    public void calculateTFIDF() {
        Set set = textA.entrySet();
        Iterator iterator = set.iterator();
        int iter = 0;
        this.wordsCounter = new double[textA.size() + textB.size()][2];
        while (iterator.hasNext()) {
            Map.Entry mentry = (Map.Entry) iterator.next();
            if (textB.containsKey(mentry.getKey())) {
                wordsCounter[iter][1] = (double) textB.get(mentry.getKey());
                textB.remove(mentry.getKey());
            } else {
                wordsCounter[iter][1] = 0;
            }
            wordsCounter[iter][0] = (double) mentry.getValue();
            iter++;
            //compareB.remove(mentry.getKey());
        }
        set = textB.entrySet();
        iterator = set.iterator();
        while (iterator.hasNext()) {
            Map.Entry mentry = (Map.Entry) iterator.next();
            wordsCounter[iter][0] = 0;
            wordsCounter[iter][1] = (double) mentry.getValue();
            iter++;
        }
    }

    public double[][] getWordsCounter() {
        return wordsCounter;
    }

}
