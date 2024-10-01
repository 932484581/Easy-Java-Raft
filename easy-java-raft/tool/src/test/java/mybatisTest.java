import java.io.IOException;
import java.io.Reader;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;

import cn.wjc.tool.database.mapper.LogMapper;

/**
 * @author 李昊哲
 * @version 1.0.0
 */
public class mybatisTest {

    @Test
    public void testConnection() throws IOException {
        String resource = "mybatis/mybatis-config.xml"; // MyBatis 配置文件的位置
        Reader reader;
        SqlSession session = null;

        try {
            // 读取 MyBatis 配置文件
            reader = Resources.getResourceAsReader(resource);
            // 构建 SqlSessionFactory
            SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
            // 打开 SqlSession
            session = sqlSessionFactory.openSession();

            // 你可以在这里添加代码来执行一些数据库操作，比如查询、插入等
            /*
             * TestMapper testMapper = session.getMapper(TestMapper.class);
             * KVEntity blog = testMapper.selectTest("test");
             * System.out.println(blog);
             */

            LogMapper testMapper = session.getMapper(LogMapper.class);

            // testMapper.insertLogEntity(
            // LogEntry.builder().index(6L).term(2L).command(Command.builder().command("这是测试样例").build()).build());
            // LogEntry blog = testMapper.getLogEntryByIndex(6L);
            // System.out.println(blog);
            testMapper.deleteLogEntityLess(5L);
            session.commit();
            // 例如：List<YourEntity> list = session.selectList("namespace.statementId");

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error reading MyBatis configuration file.");
        } finally {
            // 关闭 SqlSession
            if (session != null) {
                session.close();
            }
        }
    }
}
