package org.wltea.analyzer.core;


import org.wltea.analyzer.enums.FilterType;

interface ISegmenter {

    /**
     * 从分析器读取下一个可能分解的词元对象
     *
     * @param context 分词算法上下文
     */
    void analyze(AnalyzeContext context, FilterType filterType);


    /**
     * 重置子分析器状态
     */
    void reset();

}
