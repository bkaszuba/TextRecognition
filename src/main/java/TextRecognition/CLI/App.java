package TextRecognition.CLI;


import TextRecognition.Classification.Knn;
import TextRecognition.Extraction.OwnTextParser;
import TextRecognition.Extraction.ReutersParser;
import TextRecognition.Extraction.ReutersModifier;
import TextRecognition.Model.Article;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;


public class App {
    public static void main(String[] args) throws IOException {
        handleMenu();
    }

    static void runReuters(int k, String metric, String extractor) throws IOException {
        System.out.println(metric + " " + extractor);
        Path x = Paths.get("resources" + File.separator + "reuters21578");
        List<Article> reutersArticles;
        ReutersParser ex = new ReutersParser(x);
        ex.extract();
        reutersArticles = ex.articles;
        System.out.println("Articles without empty body or place: " + reutersArticles.size());

        //Only for reuters files with extraction pattern containing PLACE
        ReutersModifier exm = new ReutersModifier(reutersArticles);
        System.out.println("Started required place extraction...");
        exm.getOnlyRequiredPlaces();
        System.out.println("Articles with required chosen: " + reutersArticles.size());
        exm.saveArticlesToXML();
        List<Article> test = new ArrayList<>(exm.getArticles().subList(0, 100));
        List<Article> classify = new ArrayList<>(exm.getArticles().subList(100, 140));
//        List<Article> test = new ArrayList<>(exm.getArticles().subList(0,9000));
//        List<Article> classify = new ArrayList<>(exm.getArticles().subList(9000,13441));
        Knn knn = new Knn(test, classify, "reuters", metric, extractor);
        System.out.println("=======================================\nStarted classification!");
        knn.classify(k);
    }

    static void runOwnText(int k, boolean shuffle, String metric, String extractor) {
        System.out.println(metric + " " + extractor + " " + k);
        OwnTextParser own = new OwnTextParser();
        List<Article> ownArticles;
        ownArticles = own.parse("resources" + File.separator + "OwnTexts" + File.separator + "ownData7030.sgm");
        if (shuffle) {
            Collections.shuffle(ownArticles);
        }
        List<Article> test = new ArrayList<>(ownArticles.subList(0, 70));
        List<Article> classify = new ArrayList<>(ownArticles.subList(70, 100));
        Knn knn = new Knn(test, classify, "own", metric, extractor);
        System.out.println("=======================================\nStarted classification!");
        knn.classify(k);
    }

    static int showMenuExtractors(){
        System.out.println("1.TFIDF");
        System.out.println("2.TermFrequency");
        System.out.println("3.SimpleExtractor");
        Scanner sc = new Scanner(System.in);
        return sc.nextInt();
    }

    static int showMenuMethods(int extractor) {
        int choice = 0;
        switch (extractor) {
            case 1 :
            case 2 :{
                System.out.println("1.Euclidean distance");
                System.out.println("2.Chebyshev distance");
                System.out.println("3.Manhattan distance");
                Scanner sc = new Scanner(System.in);
                choice = sc.nextInt();
                break;
            }
            case 3: {
                System.out.println("1.Levenshtein");
                System.out.println("2.N-grams");
                Scanner sc = new Scanner(System.in);
                choice = sc.nextInt();
                break;
            }
        }
        return choice;
    }

     static int showMenuDataSet() {
        System.out.println("1.Reuters");
        System.out.println("2.Own texts");
        Scanner sc = new Scanner(System.in);
        return sc.nextInt();
    }

    static void handleMenu() throws IOException {
        int extractor = showMenuExtractors();
        int method = showMenuMethods(extractor);
        int dataSet = showMenuDataSet();
        String extractorName = null;
        String methodName = null;
        switch (extractor) {
            case 1: {
                extractorName = "TFIDF";
                break;
            }
            case 2:
            case 3: {
                extractorName = "TermFrequency";
                break;
            }
        }

        if(extractor == 1 || extractor == 2) {
            switch (method) {
                case 1: {
                    methodName = "euclidean";
                    break;
                }
                case 2: {
                    methodName = "chebyshev";
                    break;
                }
                case 3: {
                    methodName = "manhattan";
                    break;
                }
            }
        }
        else {
            switch (method) {
                case 1: {
                    methodName = "levenshtein";
                    break;
                }
                case 2: {
                    methodName = "ngram";
                    break;
                }
            }
        }
        System.out.println("Type K for Knn");
        int k;
        Scanner sc = new Scanner(System.in);
        k = sc.nextInt();
        switch (dataSet) {
            case 1: {
                runReuters(k, methodName, extractorName);
            }
            case 2: {
                runOwnText(k, true,methodName, extractorName);
            }
        }

    }
}