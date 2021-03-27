package org.wltea.analyzer.cfg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DefaultConfig implements Configuration {

    private Logger logger = LoggerFactory.getLogger(DefaultConfig.class);

    private static DefaultConfig defaultConfig = null;

    /*
     * 分词器配置文件路径
     */
    private static final String FILE_NAME = "IKAnalyzer.cfg.xml";

    // 配置属性——扩展字典表名
    //敏感词
    private static final String EXT_DICT_SENSITIVE_WORDS = "ext_dict_sensitive_words";
    //关键词
    private static final String EXT_DICT_KEY_WORDS = "ext_dict_key_words";

    // 配置查询数据类型
    private static final String TYPE = "type";
    // 配置JDBC链接
    private static final String JDBC_PATH = "jdbcUrl";

    public String getDataBasePath() {
        String ss = props.getProperty(TYPE);
        if (ss != null) {
            return ss;
        }
        return "";
    }

    private Properties props;
    /*
     * 是否使用smart方式分词
     */
    private boolean useSmart;

    /**
     * 返回单例
     *
     * @return Configuration单例
     */
    public static synchronized Configuration getInstance() {
        if (defaultConfig == null) {
            return new DefaultConfig();
        }
        return defaultConfig;
    }

    /*
     * 初始化配置文件
     */
    private DefaultConfig() {
        props = new Properties();

        InputStream input = this.getClass().getClassLoader().getResourceAsStream(FILE_NAME);
        if (input != null) {
            try {
                logger.info("初始化配置文件成功->fileName:[{}]", FILE_NAME);
                props.loadFromXML(input);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 返回useSmart标志位 useSmart =true ，分词器使用智能切分策略， =false则使用细粒度切分
     *
     * @return useSmart
     */
    public boolean useSmart() {
        return useSmart;
    }

    /**
     * 设置useSmart标志位
     *
     * @param useSmart useSmart =true ，分词器使用智能切分策略， =false则使用细粒度切分
     */
    public void setUseSmart(boolean useSmart) {
        this.useSmart = useSmart;
    }

    /**
     * 获取敏感词字典表名
     */
    public String getExtDictSensitiveWordsTableName() {
        String ss = props.getProperty(EXT_DICT_SENSITIVE_WORDS);
        logger.info("获取拓展字典表名->tableName:[{}]", ss);
        if (ss != null) {
            return ss;
        }
        return "";
    }

    /**
     * 获取关键词字典表名
     */
    public String getExtDictKeyWordsTableName() {
        String ss = props.getProperty(EXT_DICT_KEY_WORDS);
        logger.info("获取拓展字典表名->tableName:[{}]", ss);
        if (ss != null) {
            return ss;
        }
        return "";
    }

    /**
     * 获取数据库链接
     */
    public String getJdbcurl() {
        String ss = props.getProperty(JDBC_PATH);
        logger.info("获取数据库链接信息->JDBCUrl:[{}]", ss);
        if (ss != null) {
            return ss;
        }
        return "";
    }
}
