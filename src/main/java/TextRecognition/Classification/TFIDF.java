package TextRecognition.Classification;


import TextRecognition.Model.Article;

import java.util.*;

public class TFIDF implements Extractor {
    private HashMap textA;
    private HashMap textB;
    private double[][] wordsCounter;
    private ArrayDeque<ArrayDeque<String>> articlesWords = null;

    public TFIDF(ArrayDeque<ArrayDeque<String>> articles, Article a) {
        this.articlesWords = articles;
        this.textA = extractArticleBody(a);
    }

    public void vectorizeArticle(Article b) {
        this.textB = extractArticleBody(b);
    }

    private HashMap extractArticleBody(Article article) {
        HashMap<String, Double> wordsDictionary = new HashMap<>();
        List<String> wordsList = Arrays.asList(article.getBody().split("\\s+"));
        ArrayDeque<String> listOfWords = new ArrayDeque<>(wordsList);
        for (String term : listOfWords) {
            double result = 0;
            for (String word : listOfWords) {
                if (term.equalsIgnoreCase(word)) {
                    result++;
                }
            }
            double n = 0;
            for (ArrayDeque<String> text : articlesWords) {
                for (String word : text) {
                    if (term.equalsIgnoreCase(word)) {
                        n++;
                        break;
                    }
                }
            }
            wordsDictionary.put(term, (result / listOfWords.size()) * Math.log(articlesWords.size() / n));
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
