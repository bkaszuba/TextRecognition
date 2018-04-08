package TextRecognition.Classification;

import TextRecognition.Model.Article;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

public class Knn {
    public String[] chosen;
    public String[] own;
    public String[] places;
    private List<Article> testingArticles;
    private List<Article> classificationArticles;
    private HashMap<Double, String> results;
    private HashMap<String, int[]> percentage;
    private String method;
    private String extractor;
    private ArrayDeque<ArrayDeque<String>> articlesWords = null;

    public Knn(List<Article> testingValues, List<Article> classificationValues, String whatToClassify, String method, String extractor) {
        this.testingArticles = testingValues;
        this.classificationArticles = classificationValues;
        places = new String[]{"usa", "japan", "france", "uk", "canada", "west-germany"};
        own = new String[]{"car", "flower", "animal"};
        if (whatToClassify.equals("reuters")) {
            chosen = places;
        } else {
            chosen = own;
        }
        this.method = method;
        this.extractor = extractor;
        this.percentage = new HashMap<>();
        initializePercentage();
        extractArticlesWords(classificationArticles);

    }

    private void extractArticlesWords(List<Article> articles) {
        articlesWords = new ArrayDeque<>();
        for (Article article : articles) {
            List<String> words = Arrays.asList(article.getBody().split("\\s+"));
            ArrayDeque<String> listOfWords = new ArrayDeque<>(words);
            articlesWords.add(listOfWords);
        }
    }

    public void classify(int k) {
        double all = 0;
        for (Article classifyArt : classificationArticles) {
            all++;
            if (all % 50 == 0)
                System.out.println(".");
            results = new HashMap<>();
            switch (extractor) {
                case "CountVectorizer": {
                    CountVectorizer countVectorizer = new CountVectorizer(classifyArt);
                    for (Article testArt : testingArticles) {
                        countVectorizer.vectorizeArticle(testArt);
                        countVectorizer.countWords();
                        methodMenu(countVectorizer, testArt);
                    }
                    break;
                }
                case "TFIDF": {
                    TFIDFCalculator tfidfCalculator = new TFIDFCalculator(articlesWords, classifyArt);
                    for (Article testArt : testingArticles) {
                        tfidfCalculator.vectorizeArticle(testArt);
                        tfidfCalculator.calculateTFIDF();
                        methodMenu(tfidfCalculator, testArt);
                    }
                    break;
                }
            }
            Map<Double, String> map = new TreeMap<>(results);
            List<String> labels = new ArrayList<>();
            Set set2 = map.entrySet();
            Iterator iterator2 = set2.iterator();
            int counter = 0;
            while (iterator2.hasNext()) {
                Map.Entry me2 = (Map.Entry) iterator2.next();
                labels.add((String) me2.getValue());
                counter++;
                if (counter == k)
                    break;
            }
            Set<String> unique = new HashSet<String>(labels);
            HashMap<Integer, String> frequency = new HashMap<>();
            for (String key : unique) {
                frequency.put(Collections.frequency(labels, key), key);
            }
            NavigableMap<Integer, String> frequencyResult = new TreeMap<>(frequency);
            if (classifyArt.getLabel().equals(frequencyResult.lastEntry().getValue())) {
                setPercentageFound(classifyArt.getLabel());
            } else
                setPercentageNotFound(classifyArt.getLabel());
        }
        System.out.println("=======================================\n RESULTS: ");
        showPercentage();
    }

    private double euclideanMetric(double[][] values) {
        double result = 0;
        for (int i = 0; i < values.length; i++) {
            result += Math.pow((values[i][0] - values[i][1]), 2);
        }
        return Math.abs(result);
    }

    private double manhattanMetric(double[][] values) {
        double result = 0;
        for (int i = 0; i < values.length; i++) {
            result += Math.abs(values[i][0] - values[i][1]);
        }
        return result;
    }

    private double chebyshevMetric(double[][] values) {
        List<Double> tempResults = new ArrayList<>();
        for (int i = 0; i < values.length; i++) {
            tempResults.add(Math.abs(values[i][0] - values[i][1]));
        }
        return Collections.max(tempResults);
    }

    public void methodMenu(Extractor extractor, Article article){
        switch (method) {
            case "euclidean": {
                results.put(euclideanMetric(extractor.getWordsCounter()), article.getLabel());
                break;
            }
            case "chebyshev": {
                results.put(chebyshevMetric(extractor.getWordsCounter()), article.getLabel());
                break;
            }
            case "manhattan": {
                results.put(manhattanMetric(extractor.getWordsCounter()), article.getLabel());
                break;
            }
        }
    }
    // METHODS FOR SHOWING RESULTS AND SAVING DETAILED RESULTS TO FILE
    private void setPercentageFound(String label) {

        int previous = percentage.get(label)[0];
        int total = percentage.get(label)[1];
        percentage.put(label, new int[]{previous + 1, total + 1});
    }

    private void setPercentageNotFound(String label) {
        int previous = percentage.get(label)[0];
        int total = percentage.get(label)[1];
        percentage.put(label, new int[]{previous, total + 1});
    }

    private void showPercentage() {
        double succeded = 0;
        double all = 0;
        Set set = this.percentage.entrySet();
        Iterator iterator = set.iterator();
        List<String> detailedAnswers = new ArrayList<>();
        while (iterator.hasNext()) {
            Map.Entry mentry = (Map.Entry) iterator.next();
            succeded += percentage.get(mentry.getKey())[0];
            all += percentage.get(mentry.getKey())[1];
//            double temp = percentage.get(mentry.getKey())[0] * 100 / percentage.get(mentry.getKey())[1];
//            if (temp ==0) {
//                temp = 1;
//            }
            String v1 = String.format("%1$-10s %2$10d", mentry.getKey(), percentage.get(mentry.getKey())[1]);
            String v2 = String.format("%1$-10s %2$10d", "Success", percentage.get(mentry.getKey())[0]);
            String v3 = String.format("%1$-10s %2$10d", "Fail", (percentage.get(mentry.getKey())[1] - percentage.get(mentry.getKey())[0]));
            //String v4 = String.format("%1$-10s %2$10d", "RESULT", temp);
            //v4 += "%";
            detailedAnswers.add(v1);
            detailedAnswers.add(v2);
            detailedAnswers.add(v3 + "\n");
            //detailedAnswers.add(v4 + "\n");
        }
        System.out.println("\nClassification succeded in " + succeded * 100 / all + "%");
        saveDetailedResultToFile(detailedAnswers);
    }

    private void initializePercentage() {
        for (int i = 0; i < chosen.length; i++) {
            percentage.put(chosen[i], new int[]{0, 0});
        }
    }

    private void saveDetailedResultToFile(List<String> results) {
        try {
            PrintWriter out = new PrintWriter("detailedResult.txt");
            for (String s : results) {
                out.println(s);
            }
            out.close();
            System.out.println("Detailed results saved to -> " + "detailedResult.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }
}
