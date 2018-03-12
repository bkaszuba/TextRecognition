package TextRecognition.Extraction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import TextRecognition.Classification.TextFeaturesCreator;
import TextRecognition.Model.Article;

public class ReutersParser {
    private Path reutersDir;
    public List<Article> articles;
    private List<String> noValueWords;

    public ReutersParser(Path reutersDir) throws IOException {
        this.reutersDir = reutersDir;
        articles = new ArrayList<>();
        fillNoValueList();
    }

    public void extract() throws IOException {
        System.out.println("Started .sgm extraction from:\t"+ reutersDir);
        long count = 0;
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(reutersDir, "*.sgm")) {
            for (Path sgmFile : stream) {
                extractFile(sgmFile);
                count++;
            }
        }
        if (count == 0) {
            System.err.println("No .sgm files in " + reutersDir);
        }
    }
    Pattern EXTRACTION_PATTERN = Pattern
            .compile("<PLACES><D>(.*?)</D></PLACES>|<BODY>(.*?)</BODY>");

//    Pattern EXTRACTION_PATTERN = Pattern
//            .compile("<AUTHOR>(.*?)</AUTHOR>|<BODY>(.*?)</BODY>");

    private static String[] META_CHARS = {"&", "<", ">", "\"", "'"};
    private static String[] META_CHARS_SERIALIZATIONS = {"&amp;", "&lt;",
            "&gt;", "&quot;", "&apos;"};

    private void extractFile(Path sgmFile) {
        try (BufferedReader reader = Files.newBufferedReader(sgmFile, StandardCharsets.ISO_8859_1)) {
            StringBuilder buffer = new StringBuilder(1024);

            String line = null;
            while ((line = reader.readLine()) != null) {
                if (line.indexOf("</REUTERS") == -1) {
                    buffer.append(line).append(' ');
                } else {
                    Matcher matcher = EXTRACTION_PATTERN.matcher(buffer);
                    List<String> tempValues = new ArrayList<>();
                    while (matcher.find()) {
                        for (int i = 1; i <= matcher.groupCount(); i++) {
                            if (matcher.group(i) != null) {
                                if(!noValueWords.contains(matcher.group(i))) {
                                    tempValues.add(matcher.group(i));
                                }
                            }
                        }
                    }
                    if(tempValues.size() == 2)
                        if(!tempValues.get(0).contains("<D>")) {
                            String tmp = tempValues.get(1);
                            tmp = checkIfContainsNoValueWords(tmp); // delete no value words
                            tmp = replaceNecesarry(tmp); // delete useless spaces or chars
                            tmp = checkIfContainsNoValueWords(tmp); //one more time no value words (bugs don't know why)
                            tmp = tmp.replace(". ", "."); //replace useless space at the end of file
                            articles.add(new Article(tempValues.get(0),tmp)); // add new article
                        }
                    buffer.setLength(0);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            return;
        }
        Path reutersDir = Paths.get(args[0]);
        if (!Files.exists(reutersDir)) {
            return;
        }

        Path outputDir = Paths.get(args[1] + "-tmp");
        Files.createDirectories(outputDir);
        ReutersParser extractor = new ReutersParser(reutersDir);
        extractor.extract();

        Files.move(outputDir, Paths.get(args[1]), StandardCopyOption.ATOMIC_MOVE);
    }

    private String replace(String text, String replace, String replacement){
        return text.replace(replace, replacement);
    }

    private String replaceNecesarry(String text){
        text = text.replaceAll("[^A-Za-z]"," ");
        text = replace(text, "Reuter &#3;", "");
        text = replace(text,"  Reuter",".");
        text = replace(text," .",".");
        text = replace(text,"    ", " ");
        text = text.replaceAll("\\b[\\w']{1,2}\\b", "");
        text = text.replaceAll("\\s{2,}", " ");
        text = replace(text," . ", ".");
        text = replace(text,". ", ".");
        return replace(text, "  ", "");
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
        Scanner read = new Scanner(new File("resources\\noValueWords.txt"));
        read.useDelimiter(",");
        while(read.hasNext()){
            noValueWords.add(read.next());
        }
        read.close();
    }
}
