package TextRecognition.Extraction;

import TextRecognition.Model.Article;

import java.beans.XMLEncoder;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kaszuba on 02.03.2018.
 */
public class ReutersModifier {

    public List<Article> articles;

    public ReutersModifier(List<Article> articles) {
        this.articles = articles;
    }
    public ReutersModifier() {}

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
    public void saveArticlesToXML(){
        XMLEncoder encoder=null;
        try{
            encoder=new XMLEncoder(new BufferedOutputStream(new FileOutputStream("articles.xml")));
        }catch(FileNotFoundException fileNotFound){
            System.out.println("ERROR: While Creating or Opening the File dvd.xml");
        }
        encoder.writeObject(articles);
        encoder.close();
    }
}
