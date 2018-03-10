package Reuters.Model;

import java.util.List;

/**
 * Created by Kaszuba on 10.03.2018.
 */
public class Knn {
    private List<ClassificationVariable> testingValues;
    private List<ClassificationVariable>  classificationValues;

    public Knn(List<ClassificationVariable> testingValues, List<ClassificationVariable> classificationValues) {
        this.testingValues = testingValues;
        this.classificationValues = classificationValues;
    }

    public void classify(){
        //TODO
    }
}
