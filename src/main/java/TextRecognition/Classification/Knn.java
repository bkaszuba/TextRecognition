package TextRecognition.Classification;

import TextRecognition.Model.Article;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

/**
 * Created by Kaszuba on 10.03.2018.
 */
public class Knn {
    public String [] places;
    private List<Article> testingArticles;
    private List<Article>  classificationArticles;
    private HashMap<Double, String> results;
    private HashMap<String, int []> percentage;

    public Knn(List<Article> testingValues, List<Article> classificationValues) {
        this.testingArticles = testingValues;
        this.classificationArticles = classificationValues;
        places = new String[]{"usa", "japan", "france", "uk", "canada", "west-germany"};
        this.percentage = new HashMap<>();
        initializePercentage();
    }

    public void classify(int k){
        double all = 0;

        for (Article classifyArt: classificationArticles) {
            all++;
            if(all%250==0)
                System.out.println(".");
            results = new HashMap<>();
            CountVectorizer countVectorizer = new CountVectorizer(classifyArt);

            for (Article testArt : testingArticles) {

                countVectorizer.vectorizeArticle(testArt);
                countVectorizer.countWords();
                euclideanMetric(countVectorizer.getWordsCounter());
                results.put(euclideanMetric(countVectorizer.getWordsCounter()), testArt.getLabel());

            }

            Map<Double, String> map = new TreeMap<>(results);
            List<String> labels = new ArrayList<>();
            //System.out.println("After Sorting:");
            Set set2 = map.entrySet();
            Iterator iterator2 = set2.iterator();
            int counter = 0;
            while (iterator2.hasNext()) {
                Map.Entry me2 = (Map.Entry) iterator2.next();
                //System.out.print(me2.getKey() + ": ");
                //System.out.println(me2.getValue());
                labels.add((String) me2.getValue());
                counter++;
                if (counter == k)
                    break;

            }
            Set<String> unique = new HashSet<String>(labels);
            HashMap<Integer, String> frequency = new HashMap<>();
            for (String key : unique) {
                frequency.put(Collections.frequency(labels,key), key);
                //System.out.println(key + " : " + Collections.frequency(labels,key) );
            }
            NavigableMap<Integer, String> frequencyResult = new TreeMap<>(frequency);
            if(classifyArt.getLabel().equals(frequencyResult.lastEntry().getValue())) {
                setPercentageFound(classifyArt.getLabel());
            }
            else
                setPercentageNotFound(classifyArt.getLabel());
        }
        System.out.println("=======================================\n RESULTS: ");
        showPercentage();
    }

    private double euclideanMetric(int [][] values){
        double result =0;
        double [] tempResults = new double[values.length];
        for(int i=0; i<values.length; i++){
            tempResults[i] = Math.pow((values[i][0] - values[i][1]), 2);
        }
        for(int j=0; j<tempResults.length;j++){
            result += tempResults[j];
        }
        return Math.abs(result);
    }

    private void setPercentageFound(String label){

        int previous = percentage.get(label)[0];
        int total = percentage.get(label)[1];
        percentage.put(label, new int[]{previous+1,total+1});
    }
    private void setPercentageNotFound(String label){
        int previous = percentage.get(label)[0];
        int total = percentage.get(label)[1];
        percentage.put(label, new int[]{previous,total+1});
    }

    private void showPercentage(){
        double succeded = 0; double all = 0;
        Set set = this.percentage.entrySet();
        Iterator iterator = set.iterator();
        List<String> detailedAnswers = new ArrayList<>();
        while(iterator.hasNext()){
            Map.Entry mentry = (Map.Entry) iterator.next();
            succeded += percentage.get(mentry.getKey())[0];
            all += percentage.get(mentry.getKey())[1];
            String v1 = String.format("%1$-10s %2$10d", mentry.getKey(),  percentage.get(mentry.getKey())[1]);
            String v2 = String.format("%1$-10s %2$10d", "Success", percentage.get(mentry.getKey())[0]);
            String v3 = String.format("%1$-10s %2$10d", "Fail", (percentage.get(mentry.getKey())[1] - percentage.get(mentry.getKey())[0]));
            String v4 = String.format("%1$-10s %2$10d", "RESULT", percentage.get(mentry.getKey())[0]*100/percentage.get(mentry.getKey())[1]);
            v4 +="%";
            detailedAnswers.add(v1+"\n");detailedAnswers.add(v1+"\n");detailedAnswers.add(v3+"\n");detailedAnswers.add(v4+"\n");
        }
        System.out.println("\nClassification succeded in "+ succeded*100/all+"%");
        saveDetailedResultToFile(detailedAnswers);
    }

    private void initializePercentage(){
        for(int i=0; i<places.length;i++){
            percentage.put(places[i], new int[]{0,0});
        }
    }

    private void saveDetailedResultToFile(List<String> results){
        try {
            PrintWriter out = new PrintWriter("detailedResult.txt");
            for (String s :results) {
                out.println(s);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }
}
