package TextRecognition.CLI;


import TextRecognition.Classification.TextFeaturesCreator;
import TextRecognition.Extraction.ReutersParser;
import TextRecognition.Extraction.ReutersModifier;
import TextRecognition.Model.Article;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Hello world!
 */

public class App {
    public static void main(String[] args) throws IOException {
        Path x = Paths.get("resources\\Reuters21578");
        List<Article> articles;
        //Retrieving all TextRecognition
        ReutersParser ex = new ReutersParser(x);
        ex.extract();
        articles = ex.articles;
        System.out.println("Articles without empty body or place: " + articles.size());

        ReutersModifier exm = new ReutersModifier(articles);
        System.out.println("Started required place extraction...");
        exm.getOnlyRequiredPlaces();
        System.out.println("Articles with required places: " + articles.size());
        exm.saveArticlesToXML();

        TextFeaturesCreator textFeaturesCreator = new TextFeaturesCreator(articles);
    }
}
