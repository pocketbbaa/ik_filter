package org.wltea.analyzer.core;

import org.wltea.analyzer.dic.Hit;
import org.wltea.analyzer.enums.FilterType;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * 中文数量词子分词器
 */
class CN_QuantifierSegmenter implements ISegmenter {

    //子分词器标签
    private static final String SEGMENTER_NAME = "QUAN_SEGMENTER";

    //中文数词
    private static String Chn_Num = "";//Cnum
    private static Set<Character> ChnNumberChars = new HashSet<>();

    static {
        char[] ca = Chn_Num.toCharArray();
        for (char nChar : ca) {
            ChnNumberChars.add(nChar);
        }
    }

    /*
     * 词元的开始位置，
     * 同时作为子分词器状态标识
     * 当start > -1 时，标识当前的分词器正在处理字符
     */
    private int nStart;
    /*
     * 记录词元结束位置
     * end记录的是在词元中最后一个出现的合理的数词结束
     */
    private int nEnd;

    //待处理的量词hit队列
    private List<Hit> countHits;


    CN_QuantifierSegmenter() {
        nStart = -1;
        nEnd = -1;
        this.countHits = new LinkedList<Hit>();
    }

    /**
     * 分词
     */
    public void analyze(AnalyzeContext context, FilterType filterType) {
        //处理中文数词
        this.processCNumber(context);
        //处理中文量词
        this.processCount(context);

        //判断是否锁定缓冲区
        if (this.nStart == -1 && this.nEnd == -1 && countHits.isEmpty()) {
            //对缓冲区解锁
            context.unlockBuffer(SEGMENTER_NAME);
        } else {
            context.lockBuffer(SEGMENTER_NAME);
        }
    }


    /**
     * 重置子分词器状态
     */
    public void reset() {
        nStart = -1;
        nEnd = -1;
        countHits.clear();
    }

    /**
     * 处理数词
     */
    private void processCNumber(AnalyzeContext context) {
        if (nStart == -1 && nEnd == -1) {//初始状态
            if (CharacterUtil.CHAR_CHINESE == context.getCurrentCharType()
                    && ChnNumberChars.contains(context.getCurrentChar())) {
                //记录数词的起始、结束位置
                nStart = context.getCursor();
                nEnd = context.getCursor();
            }
        } else {//正在处理状态
            if (CharacterUtil.CHAR_CHINESE == context.getCurrentCharType()
                    && ChnNumberChars.contains(context.getCurrentChar())) {
                //记录数词的结束位置
                nEnd = context.getCursor();
            } else {
                //输出数词
                this.outputNumLexeme(context);
                //重置头尾指针
                nStart = -1;
                nEnd = -1;
            }
        }

        //缓冲区已经用完，还有尚未输出的数词
        if (context.isBufferConsumed()) {
            if (nStart != -1 && nEnd != -1) {
                //输出数词
                outputNumLexeme(context);
                //重置头尾指针
                nStart = -1;
                nEnd = -1;
            }
        }
    }

    /**
     * 处理中文量词
     *
     * @param context
     */
    private void processCount(AnalyzeContext context) {
    }

    /**
     * 判断是否需要扫描量词
     *
     * @return
     */
    private boolean needCountScan(AnalyzeContext context) {
        if ((nStart != -1 && nEnd != -1) || !countHits.isEmpty()) {
            //正在处理中文数词,或者正在处理量词
            return true;
        } else {
            //找到一个相邻的数词
            if (!context.getOrgLexemes().isEmpty()) {
                Lexeme l = context.getOrgLexemes().peekLast();
                if (Lexeme.TYPE_CNUM == l.getLexemeType() || Lexeme.TYPE_ARABIC == l.getLexemeType()) {
                    if (l.getBegin() + l.getLength() == context.getCursor()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 添加数词词元到结果集
     *
     * @param context
     */
    private void outputNumLexeme(AnalyzeContext context) {
        if (nStart > -1 && nEnd > -1) {
            //输出数词
            Lexeme newLexeme = new Lexeme(context.getBufferOffset(), nStart, nEnd - nStart + 1, Lexeme.TYPE_CNUM);
            context.addLexeme(newLexeme);

        }
    }

}
