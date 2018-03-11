package Reuters.Model;

/**
 * Created by Kaszuba on 02.03.2018.
 */
public class Article {
    private String label;
    private String body;

    public Article(String label, String body) {
        this.label = label;
        this.body = body;
    }
    public Article() {};

    public Article(String label){
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

}
