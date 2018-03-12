package TextRecognition.Classification;

import TextRecognition.Model.Article;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Created by Kaszuba on 12.03.2018.
 */

public class TextFeaturesCreator {
    private List<Article> articles;
    private HashMap<String, Integer> wordsDictionary;

    public TextFeaturesCreator(List<Article> articles) throws FileNotFoundException {
        this.articles = articles;
        fillWordsDictionary();
    }

    private void fillWordsDictionary() {
        System.out.println("Started creating dictionary...");
        wordsDictionary = new HashMap<>();
        int wordsCounter = 0;
        for (Article art : articles) {
            String[] words = art.getBody().split("\\s+");
            for (int i = 0; i < words.length; i++) {
                if (!this.wordsDictionary.containsKey(words[i])) {
                    wordsDictionary.put(words[i], wordsCounter);
                    wordsCounter++;
                    if(wordsCounter%10000 == 0)
                        System.out.println(".");
                }
            }
        }
        System.out.println("Words in dictionary: " + wordsDictionary.size());
    }
}
