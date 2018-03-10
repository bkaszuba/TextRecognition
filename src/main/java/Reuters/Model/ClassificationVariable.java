package Reuters.Model;

/**
 * Created by Kaszuba on 10.03.2018.
 */
public class ClassificationVariable {
    private double value;
    private String clas;

    /**
     * Constructor for testing values
     * @param value - numeric value
     * @param clas - which class
     */
    public ClassificationVariable(double value, String clas) {
        this.value = value;
        this.clas = clas;
    }

    /**
     * Constructor for classification values
     * @param value - numeric value
     */
    public ClassificationVariable(double value) {
        this.value = value;
        this.clas = null;
    }
}
