package Reuters.Controller;

import Reuters.Model.Article;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kaszuba on 02.03.2018.
 */
public class ExtractedModifier {

    public List<Article> articles;

    public ExtractedModifier(List<Article> articles) {
        this.articles = articles;
    }

    public void getOnlyRequiredPlaces(){
        for (Article art : new ArrayList<>(articles)) {
            if (!"usa".equals(art.getLabel()) && !"japan".equals(art.getLabel()) && !"canada".equals(art.getLabel()) &&
                    !"west-germany".equals(art.getLabel()) && !"france".equals(art.getLabel()) && !"uk".equals(art.getLabel())){
                articles.remove(art);
            }
            if(art.getBody() == null)
                articles.remove(art);
        }
        getNumberOfRequiredPlaces();
    }
    public void getNumberOfRequiredPlaces() {
        int[] places = new int [6];
        for (Article x : articles
                ) {
            switch (x.getLabel()){
                case "usa": places[0]++; break;
                case "west-germany": places[1]++; break;
                case "france": places[2]++; break;
                case "japan": places[3]++; break;
                case "uk": places[4]++; break;
                case "canada": places[5]++; break;
            }
        }
        System.out.println("USA: " + places[0] + " West-Germany: " + places[1]
                + " France: " + places[2] + " Japan: " + places[3] + " UK: " + places[4] + " Canada: " + places[5]);
    }
}
