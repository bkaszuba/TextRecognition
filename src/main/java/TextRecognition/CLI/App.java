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

/**
 * Hello world!
 */

public class App {
    public static void main(String[] args) throws IOException {
        String euclideanMetric = "euclidean";
        String chebyshevMetric = "chebyshev";
        String manhattanMetric = "manhattan";
        runReuters(3, euclideanMetric);
        //runOwnText(1, false,euclideanMetric);
    }

     static void runReuters(int k, String metric) throws IOException {
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
        List<Article> test = new ArrayList<>(exm.getArticles().subList(0,9000));
        List<Article> classify = new ArrayList<>(exm.getArticles().subList(9000,13441));
        Knn knn = new Knn(test, classify, "reuters", metric);
        System.out.println("=======================================\nStarted classification!");
        knn.classify(k);
    }

     static void runOwnText(int k, boolean shuffle, String metric) {
        OwnTextParser own = new OwnTextParser();
        List<Article> ownArticles;
        ownArticles = own.parse("resources" + File.separator + "OwnTexts" + File.separator + "ownData7030.sgm");
        if(shuffle) {
            Collections.shuffle(ownArticles);
        }
        List<Article> test = new ArrayList<>(ownArticles.subList(0,70));
        List<Article> classify = new ArrayList<>(ownArticles.subList(70,100));
        Knn knn = new Knn(test, classify, "own", metric);
        System.out.println("=======================================\nStarted classification!");
        knn.classify(k);
    }
}
