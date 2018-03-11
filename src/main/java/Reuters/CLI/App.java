package Reuters.CLI;


import Reuters.Extraction.ExtractReuters;
import Reuters.Extraction.ExtractedModifier;
import Reuters.Model.Article;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Hello world!
 *
 */

public class App 
{
    public static void main( String[] args )
    {
        Path x = Paths.get("resources\\Reuters21578");
        List<Article> articles = new ArrayList<>();
        try {
            ExtractReuters ex = new ExtractReuters(x);
            ex.extract();
            articles = ex.articles;
            System.out.println("Without empty body or place: " +  articles.size());
        } catch (IOException e) {
            e.printStackTrace();
        }
        ExtractedModifier exm = new ExtractedModifier(articles);
        System.out.println("Started place extraction");
        exm.getOnlyRequiredPlaces();
        System.out.println("With required places: " + articles.size());
    }
}
