package TextRecognition.Extraction;

import TextRecognition.Model.Article;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class OwnTextParser {
    private final static String LABEL_TAG = "<LABEL>(.+?)</LABEL>";
    private final static String BODY_TAG = "<BODY>(.+?)</BODY>";
    private List<String> noValueWords;

    public OwnTextParser() {
        try {
            fillNoValueList();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    public List<Article> parse(String file){
        List<Article> articles = new ArrayList<>();
        this.getTag(file, LABEL_TAG, articles);
        this.getTag(file, BODY_TAG, articles);
        return articles;
    }

    private void getTag(String file, String tag, List<Article> articles) {
        BufferedReader br;
        FileReader fr;
        Pattern p = Pattern.compile(tag);
        try {
            int i = 0;
            fr = new FileReader(file);
            br = new BufferedReader(fr);
            String currentLine;
            while((currentLine = br.readLine()) != null) {
                Matcher m = p.matcher(currentLine);
                // if we find a match, get the group
                if (m.find()) {
                    if (tag.equals(LABEL_TAG)) {
                        articles.add(new Article(m.group(1)));
                    } else {
                            String body = checkIfContainsNoValueWords(m.group(1));
                            body = replaceNecesarry(body);
                            articles.get(i).setBody(body);
                    }
                    i++;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String replaceNecesarry(String text){
        text = text.replaceAll("[^A-Za-z]"," ");
        text = text.replaceAll("\\b[\\w']{1,2}\\b", "");
        text = text.replaceAll("\\s{2,}", " ");
        return text;
    }

    private String checkIfContainsNoValueWords(String text){
        StringBuilder sB = new StringBuilder();
        String[] words = text.split("\\s+");
        for(int i=0; i<words.length; i++){
            if(!noValueWords.contains(words[i])){
                sB.append(words[i]).append(" ");
            }
        }
        return sB.toString();
    }

    private void fillNoValueList() throws FileNotFoundException {
        this.noValueWords = new ArrayList<>();
        String dir = "resources" + File.separator + "noValueWords.txt";
        Scanner read = new Scanner(new File(dir));
        read.useDelimiter(",");
        while(read.hasNext()){
            noValueWords.add(read.next());
        }
        System.out.println();
        read.close();
    }

}
