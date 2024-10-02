package cn.wjc.tool.storage.impl;

import java.io.Reader;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

public class GetDataSource {
    private static SqlSessionFactory sqlSessionFactory;
    static {
        try {
            Reader reader;
            String resource = "mybatis/mybatis-config.xml"; // MyBatis 配置文件的位置
            // 读取 MyBatis 配置文件
            reader = Resources.getResourceAsReader(resource);
            // 构建 SqlSessionFactory
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);

        } catch (Exception e) {
            throw new RuntimeException("Error initializing MyBatis SqlSessionFactory", e);
        }

    }

    public static SqlSessionFactory getMybatisSessionFactory() {
        return sqlSessionFactory;
    }

}
