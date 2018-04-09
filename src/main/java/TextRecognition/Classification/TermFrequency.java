package TextRecognition.Classification;

import TextRecognition.Model.Article;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class TermFrequency implements Extractor{
    private HashMap compareA;
    private HashMap compareB;
    private double[][] wordsCounter;

    public TermFrequency(Article a) {
        this.compareA = extractArticleBody(a);
    }

    public void vectorizeArticle(Article b) {
        this.compareB = extractArticleBody(b);
    }

    private HashMap extractArticleBody(Article a) {
        HashMap<String, Integer> wordsDictionary = new HashMap<>();

        String[] words = a.getBody().split("\\s+");
        for (int i = 0; i < words.length; i++) {
            if (wordsDictionary.containsKey(words[i])) {
                wordsDictionary.put(words[i], wordsDictionary.get(words[i]) + 1);
            } else
                wordsDictionary.put(words[i], 1);
        }
        return wordsDictionary;
    }

    public void countWords() {
        Set set = compareA.entrySet();
        Iterator iterator = set.iterator();
        int iter = 0;

        this.wordsCounter = new double[compareA.size()+compareB.size()][2];
        while (iterator.hasNext()) {
            Map.Entry mentry = (Map.Entry) iterator.next();
            if (compareB.containsKey(mentry.getKey())) {
                wordsCounter[iter][1] = (int) compareB.get(mentry.getKey());
                compareB.remove(mentry.getKey());
            } else {
                wordsCounter[iter][1] = 0;
            }
            wordsCounter[iter][0] = (int) mentry.getValue();
            iter++;
            //compareB.remove(mentry.getKey());
        }
        set = compareB.entrySet();
        iterator = set.iterator();
        while (iterator.hasNext()) {
            Map.Entry mentry = (Map.Entry) iterator.next();
            wordsCounter[iter][0] = 0;
            wordsCounter[iter][1] = (int) mentry.getValue();
            iter++;
        }
    }

    public double[][] getWordsCounter() {
        return wordsCounter;
    }
}
