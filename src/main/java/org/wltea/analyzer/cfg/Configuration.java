package org.wltea.analyzer.cfg;

/**
 * 配置管理类接口
 *
 * @author Administrator
 */
public interface Configuration {
    /**
     * 返回useSmart标志位 useSmart =true ，分词器使用智能切分策略， =false则使用细粒度切分
     *
     * @return useSmart
     */
    boolean useSmart();

    /**
     * 设置useSmart标志位
     *
     * @param useSmart useSmart =true ，分词器使用智能切分策略， =false则使用细粒度切分
     */
    void setUseSmart(boolean useSmart);

    /**
     * 获取数据库表的配置路径
     */
    String getDataBasePath();


    /**
     * 获取敏感词字典表名
     */
    String getExtDictSensitiveWordsTableName();

    /**
     * 获取关键词字典表名
     *
     * @return
     */
    String getExtDictKeyWordsTableName();


    /**
     * 获取数据库链接
     */
    String getJdbcurl();

}
