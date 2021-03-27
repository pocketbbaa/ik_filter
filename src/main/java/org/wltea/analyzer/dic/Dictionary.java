package org.wltea.analyzer.dic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wltea.analyzer.cfg.Configuration;
import org.wltea.analyzer.conndb.DBHelper;
import org.wltea.analyzer.thread.HotDictReloadThread;

import java.util.List;

/**
 * 词典管理类,单子模式
 *
 * @author Administrator
 */
public class Dictionary {

    private static Logger logger = LoggerFactory.getLogger(Dictionary.class);

    /**
     * 词典单子实例
     */
    private static Dictionary singleton;

    /**
     * 敏感词词典对象
     */
    private DictSegment _ExtDictSW;

    /**
     * 关键词词典对象
     */
    private DictSegment _ExtDictKW;

    /**
     * 配置对象
     */
    private Configuration cfg;

    private Dictionary(Configuration cfg) {
        this.cfg = cfg;
    }

    /**
     * 词典初始化 由于IK Analyzer的词典采用Dictionary类的静态方法进行词典初始化
     * 只有当Dictionary类被实际调用时，才会开始载入词典， 这将延长首次分词操作的时间 该方法提供了一个在应用加载阶段就初始化字典的手段
     *
     * @return Dictionary
     */
    public static Dictionary initial(Configuration cfg) {
        synchronized (Dictionary.class) {
            if (singleton == null) {
                singleton = new Dictionary(cfg);
                logger.info("--初始化主词典对象--");
                singleton.loadMainDict();

                new Thread(new HotDictReloadThread()).start();

                try {
                    logger.info("--初始词典--");
                    //加载敏感词
                    singleton.loadExtDBDictSW(cfg.getDataBasePath(), cfg.getExtDictSensitiveWordsTableName(), cfg.getJdbcurl());
                    //加载关键词
                    singleton.loadExtDBDictKW(cfg.getDataBasePath(), cfg.getExtDictKeyWordsTableName(), cfg.getJdbcurl());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return singleton;
    }

    /**
     * 获取词典单子实例
     *
     * @return Dictionary 单例对象
     */
    public static Dictionary getSingleton() {
        if (singleton == null) {
            throw new IllegalStateException("词典尚未初始化，请先调用initial方法");
        }
        return singleton;
    }

    /**
     * 检索匹配敏感词词典
     *
     * @param charArray
     * @param begin
     * @param length
     * @return Hit 匹配结果描述
     */
    public Hit matchInSWDict(char[] charArray, int begin, int length) {
        return singleton._ExtDictSW.match(charArray, begin, length);
    }

    /**
     * 检索匹配关键词词典
     *
     * @param charArray
     * @param begin
     * @param length
     * @return Hit 匹配结果描述
     */
    public Hit matchInKWDict(char[] charArray, int begin, int length) {
        return singleton._ExtDictKW.match(charArray, begin, length);
    }


    /**
     * 从已匹配的Hit中直接取出DictSegment，继续向下匹配
     *
     * @param charArray
     * @param currentIndex
     * @param matchedHit
     * @return Hit
     */
    public Hit matchWithHit(char[] charArray, int currentIndex, Hit matchedHit) {
        DictSegment ds = matchedHit.getMatchedDictSegment();
        return ds.match(charArray, currentIndex, 1, matchedHit);
    }

    /**
     * 加载主词典及扩展词典
     *
     */
    private void loadMainDict() {
        // 建立一个主词典实例
        _ExtDictSW = new DictSegment((char) 0);
        _ExtDictKW = new DictSegment((char) 0);
    }


    /**
     * 从数据库加载敏感词词
     **/
    private void loadExtDBDictSW(String type, String extDictTable, String jdbcUrl) throws Exception {
        logger.info("从数据库加敏感词->type:【{}】,extDictTable:【{}】,jdbcUrl:【{}】", type, extDictTable, jdbcUrl);
        List<String> list = DBHelper.getKey(extDictTable, type, jdbcUrl);
        if (list == null || list.size() <= 0) {
            logger.info("没有获取到敏感词！！！");
            return;
        }
        logger.info("获取到的敏感词数量：【{}】", list.size());
        for (String word : list) {
            _ExtDictSW.fillSegment(word.trim().toLowerCase().toCharArray());
        }
    }

    /**
     * 从数据库加载关键词
     **/
    private void loadExtDBDictKW(String type, String extDictTable, String jdbcUrl) throws Exception {
        logger.info("从数据库加载关键词->type:【{}】,extDictTable:【{}】,jdbcUrl:【{}】", type, extDictTable, jdbcUrl);
        List<String> list = DBHelper.getKey(extDictTable, type, jdbcUrl);
        if (list == null || list.size() <= 0) {
            logger.info("没有获取到关键词！！！");
            return;
        }
        logger.info("获取到的关键词数量：【{}】", list.size());
        for (String word : list) {
            _ExtDictKW.fillSegment(word.trim().toLowerCase().toCharArray());
        }
    }

    /**
     * 重新加载拓展词典（mysql中获取），实现动态更新词库
     */
    public void reLoadExtDBDict() {
        if (singleton == null) {
            throw new IllegalStateException("词典尚未初始化，请先调用initial方法");
        }
        try {
            loadExtDBDictSW(cfg.getDataBasePath(), cfg.getExtDictSensitiveWordsTableName(), cfg.getJdbcurl());
            loadExtDBDictKW(cfg.getDataBasePath(), cfg.getExtDictKeyWordsTableName(), cfg.getJdbcurl());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
