package TextRecognition.Utils;

import TextRecognition.Model.Article;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kaszuba on 08.04.2018.
 */
public class ArticlesUtils {

    public static List<Article> getNumberOfArticles(List<Article> allArticles, int number, String label){
        List<Article> articles = new ArrayList<>();
        for (Article art: allArticles) {
            if(articles.size() == number)
                break;
            if(art.getLabel().equals(label))
                articles.add(art);
        }
        return articles;
    }
}
