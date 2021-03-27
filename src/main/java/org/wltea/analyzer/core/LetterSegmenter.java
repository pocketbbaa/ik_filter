package org.wltea.analyzer.core;

import org.wltea.analyzer.enums.FilterType;

import java.util.Arrays;

/**
 * 英文字符及阿拉伯数字子分词器（字符分词器）
 *
 * @author Administrator
 */
public class LetterSegmenter implements ISegmenter {

    // 子分词器标签
    private static final String SEGMENTER_NAME = "LETTER_SEGMENTER";
    // 链接符号
    private static final char[] Letter_Connector = new char[]{'#', '&', '+', '-', '.', '@', '_'};

    // 数字符号
    private static final char[] Num_Connector = new char[]{',', '.'};

    LetterSegmenter() {
        Arrays.sort(Letter_Connector);
        Arrays.sort(Num_Connector);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.wltea.analyzer.core.ISegmenter#analyze(org.wltea.analyzer.core.
     * AnalyzeContext)
     */
    public void analyze(AnalyzeContext context, FilterType filterType) {
        boolean bufferLockFlag = false;
        // 处理英文字母
        bufferLockFlag = this.processEnglishLetter(context) || bufferLockFlag;
        // 处理阿拉伯字母
        bufferLockFlag = this.processArabicLetter(context) || bufferLockFlag;
        // 处理混合字母(这个要放最后处理，可以通过QuickSortSet排除重复)
        bufferLockFlag = this.processMixLetter(context) || bufferLockFlag;

        // 判断是否锁定缓冲区
        if (bufferLockFlag) {
            context.lockBuffer(SEGMENTER_NAME);
        } else {
            // 对缓冲区解锁
            context.unlockBuffer(SEGMENTER_NAME);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.wltea.analyzer.core.ISegmenter#reset()
     */
    public void reset() {
    }

    /**
     * 处理数字字母混合输出 如：windos2000 | linliangyi2005@gmail.com
     *
     * @param input
     * @param context
     * @return
     */
    private boolean processMixLetter(AnalyzeContext context) {
        return false;
    }

    /**
     * 处理纯英文字母输出
     *
     * @param context
     * @return
     */
    private boolean processEnglishLetter(AnalyzeContext context) {
        return false;
    }

    /**
     * 处理阿拉伯数字输出
     *
     * @param context
     * @return
     */
    private boolean processArabicLetter(AnalyzeContext context) {
        return false;
    }

}
