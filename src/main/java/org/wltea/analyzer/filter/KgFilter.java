package org.wltea.analyzer.filter;

import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.wltea.analyzer.enums.FilterType;
import org.wltea.analyzer.lucene.IKAnalyzer;
import org.wltea.analyzer.lucene.IKTokenizer;
import org.wltea.analyzer.result.Result;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class KgFilter {

    /**
     * 过滤
     *
     * @param text
     * @return
     * @throws IOException
     */
    public static Result<List<String>> doFilter(String text, FilterType filterType) throws IOException {
        Set<String> words = new HashSet<>();
        IKAnalyzer analyzer = new IKAnalyzer(true);
        IKTokenizer token = (IKTokenizer) analyzer.tokenStream("", new StringReader(text));
        CharTermAttribute term = token.addAttribute(CharTermAttribute.class);
        token.reset();
        while (token.incrementTokenForType(filterType)) {
            String str = term.toString();
            if (str == null) {
                continue;
            }
            if (str.length() <= 1) {
                continue;
            }
            words.add(term.toString());
        }
        token.end();
        token.close();
        if (words.size() <= 0) {
            return new Result<>("101", "没有检测到");
        }
        return new Result<>("100", "检测到过滤词", new ArrayList<>(words));
    }

}
