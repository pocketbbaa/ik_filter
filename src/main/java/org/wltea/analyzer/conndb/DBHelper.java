package org.wltea.analyzer.conndb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class DBHelper {

    private static Logger logger = LoggerFactory.getLogger(DBHelper.class);

    private static Connection conn;// 创建用于连接数据库的Connection对象

    private static final int PAGE_INDEX = 1;
    private static final int PAGE_SIZE = 2000;

    private static Connection getConn(String jdbcUrl) throws Exception {

        try {
            Class.forName("com.mysql.jdbc.Driver");// 加载Mysql数据驱动
            String[] path = jdbcUrl.split(",");
            String url = path[0] + "?useUnicode=true&characterEncoding=UTF-8&useSSL=true";
            conn = DriverManager.getConnection(url, path[1], path[2]);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return conn;
    }

    public static List<String> getKey(String tableName, String type, String jdbcUrl) throws Exception {
        logger.info("连接数据库了 tableName:[{}],type:[{}],jdbcUrl:[{}]", tableName, type, jdbcUrl);
        int pageIndex = PAGE_INDEX;
        int pageSize = PAGE_SIZE;
        int start = (pageIndex - 1) * pageSize;
        int end = pageSize;
        long startTime = System.currentTimeMillis();
        List<String> list = new ArrayList<>();
        List<String> tempList;
        pageIndex++;
        while ((tempList = getResult(tableName, type, jdbcUrl, start, end)).size() > 0) {
            start = (pageIndex - 1) * pageSize;
            end = pageSize;
            pageIndex++;
            list.addAll(tempList);
        }
        long endTime = System.currentTimeMillis();
        logger.info("分页获取数据，获取全部数据用了[{}]次", pageIndex);
        logger.info("获取数据库->tableName:[{}]词典所花费时间为-》time:[{}] 毫秒", tableName, endTime - startTime);
        return list;
    }

    /**
     * 循环获取数据库数据
     *
     * @return a
     */
    private static List<String> getResult(String tableName, String type, String jdbcUrl, int start, int end)
            throws Exception {
        conn = getConn(jdbcUrl);
        String sql = "select word_desc from " + tableName + " where status=" + type + " limit " + start + "," + end;
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        List<String> list = new ArrayList<>();
        while (rs.next()) {
            String data = rs.getString("word_desc");
            if (data == null || (data = data.trim()).length() == 0) {
                continue;
            }
            list.add(data);
        }
        rs.close();
        ps.close();
        conn.close();
        return list;

    }

}