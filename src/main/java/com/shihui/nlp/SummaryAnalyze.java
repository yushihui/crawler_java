package com.shihui.nlp;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.NShort.NShortSegment;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.NotionalTokenizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * Created by Syu on 4/12/2017.
 */
public class SummaryAnalyze {


    private static final String LOCATION = "C:\\PMSummary\\";
    private static final String LOCATION_NLP = "C:\\nlp\\";
    private static final Logger Log = LoggerFactory.getLogger(SummaryAnalyze.class);

    public static void main(String rags[]) {
        Segment nShortSegment = new NShortSegment().enableCustomDictionary(false).enablePlaceRecognize(true).enableOrganizationRecognize(true);
        List<Term> res = nShortSegment.seg("你好，欢迎使用hanLP汉语处理包！ HEllo");
        //abc
        //add new line 4
        //add new line 6
        new SummaryAnalyze().readFile();
    }


    private List<String> getWordsSummary(String content) {
        List<Term> words = NotionalTokenizer.segment(content);    // stop words data/dictionary/stopwords.txt
        List<String> plainWords = words.parallelStream().map(w -> w.word.toUpperCase()).collect(toList());
        return plainWords;
    }

    private List<String> getKeywords(String content) {
        return HanLP.extractKeyword(content, 5);
    }


    private List<String> getSummary(String content) {
        List<String> ret = HanLP.extractSummary(content, 2);

        ret.forEach(r -> System.out.println(r));
        return ret;
    }

    private void write2File(List<Multiset.Entry<String>> result, String fileName) {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(LOCATION_NLP + fileName))) {
            result.forEach(fw -> {
                Log.info("{}:{}", fw.getElement(), fw.getCount());
                try {
                    writer.write("{text:'" + fw.getElement() + "',size:" + fw.getCount() + "},");
                    writer.newLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            });
        } catch (Exception e) {

        }
    }

    private void readFile() {
        try (Stream<Path> paths = Files.walk(Paths.get(LOCATION)).parallel()) {
            Vector<String> words = new Vector<>();
            paths.forEach(filePath -> {
                if (Files.isRegularFile(filePath)) {
                    StringBuilder sb = new StringBuilder("");
                    try (Stream<String> lines = Files.lines(filePath)) {
                        lines.forEach(l -> sb.append(l));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    String content = sb.toString();
                    words.addAll(getWordsSummary(content));
                    //words.addAll(getSummary(content));

                }
            });
            //words.forEach(w -> System.out.println(w));

            HashMultiset<String> summary = HashMultiset.create(words);
            Set<Multiset.Entry<String>> finalWords = summary.entrySet();
            List<Multiset.Entry<String>> result = finalWords.parallelStream().filter(entry -> entry.getElement().trim().length() > 1).sorted((ent1, ent2) -> Integer.compare(ent2.getCount(), ent1.getCount())).collect(toList());
            write2File(result, "normal345678.txt");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
