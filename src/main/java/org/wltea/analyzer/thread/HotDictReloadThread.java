package org.wltea.analyzer.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wltea.analyzer.dic.Dictionary;

public class HotDictReloadThread implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(Dictionary.class);

    /**
     * 20分钟更新一次词库
     */
    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(1000 * 60 * 20);
                Dictionary.getSingleton().reLoadExtDBDict();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
