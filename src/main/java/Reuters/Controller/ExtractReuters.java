package Reuters.Controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Reuters.Model.Article;

public class ExtractReuters {
    private Path reutersDir;
    public List<Article> articles;

    public ExtractReuters(Path reutersDir) throws IOException {
        this.reutersDir = reutersDir;
        articles = new ArrayList<>();
    }

    public void extract() throws IOException {
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

    /**
     * Override if you wish to change what is extracted
     */
    protected void extractFile(Path sgmFile) {
        try (BufferedReader reader = Files.newBufferedReader(sgmFile, StandardCharsets.ISO_8859_1)) {
            StringBuilder buffer = new StringBuilder(1024);
            StringBuilder outBuffer = new StringBuilder(1024);

            String line = null;
            int n = 0;
            int er = 0;
            while ((line = reader.readLine()) != null) {
                if (line.indexOf("</REUTERS") == -1) {
                    buffer.append(line).append(' ');
                } else {
                    n++;
                    Matcher matcher = EXTRACTION_PATTERN.matcher(buffer);
                    List<String> tempValues = new ArrayList<>();
                    while (matcher.find()) {
                        for (int i = 1; i <= matcher.groupCount(); i++) {
                            if (matcher.group(i) != null) {
                                tempValues.add(matcher.group(i));
                                outBuffer.append(matcher.group(i));
                            }
                        }
                        outBuffer.append(System.lineSeparator()).append(System.lineSeparator());
                    }
                    if(tempValues.size() == 2)
                        if(!tempValues.get(0).contains("<D>"))
                            articles.add(new Article(tempValues.get(0), tempValues.get(1)));
                        else
                            er++;
                    else{
                        er++;
                    }
                    String out = outBuffer.toString();
                    for (int i = 0; i < META_CHARS_SERIALIZATIONS.length; i++) {
                        out = out.replaceAll(META_CHARS_SERIALIZATIONS[i], META_CHARS[i]);
                    }
                    outBuffer.setLength(0);
                    buffer.setLength(0);
                }
            }
            System.out.println(n + "  " +er);
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
        ExtractReuters extractor = new ExtractReuters(reutersDir);
        extractor.extract();

        Files.move(outputDir, Paths.get(args[1]), StandardCopyOption.ATOMIC_MOVE);
    }
}
