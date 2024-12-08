import java.io.IOException;
import java.io.Reader;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;

import cn.wjc.tool.database.mapper.LogMapper;
import cn.wjc.tool.entity.Command;
import cn.wjc.tool.entity.LogEntry;
import cn.wjc.tool.storage.impl.KVStorageImpl;
import cn.wjc.tool.storage.impl.LogStorageImpl;

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
            try {
                testMapper.insertLogEntity(
                        LogEntry.builder().index(1L).term(2L).command(Command.builder().command("这是测试样例").build())
                                .build(),
                        "log");
                System.out.println("写入成功");

            } catch (Exception e) {
                System.err.println(e.toString());
                System.err.println("写入失败");
            }
            LogEntry blog = testMapper.getLogEntryByIndex(2L, "log");
            System.out.println(blog);
            // testMapper.deleteLogEntityLess(5L);
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

    @Test
    public void LogStorageTest() throws Throwable {

        LogStorageImpl logStorage = new LogStorageImpl("log0");
        logStorage.init();
        logStorage
                .appendEntry(LogEntry.builder().index(2L).term(0).command(Command.builder().command("这是测试样例22").build())
                        .build());

        long res = logStorage.getLastLogIndex();
        System.out.println(res);
        LogEntry logEntry = logStorage.getEntry(2);
        LogEntry logEntry2 = logStorage.getEntry(1);
        LogEntry logEntry3 = logStorage.getEntry(0);
        logEntry = logStorage.getEntry(2);
        logEntry2 = logStorage.getEntry(1);
        logEntry3 = logStorage.getEntry(0);
        System.out.println(logEntry);
        System.out.println(logEntry2);
        System.out.println(logEntry3);

    }

    @Test
    public void KVStorageTest() throws Throwable {

        KVStorageImpl kvStorageImpl = new KVStorageImpl("kv_store");
        kvStorageImpl.init();
        kvStorageImpl.setString("test2", "这是一个测试33");

        String res = kvStorageImpl.getString("test");
        String res2 = kvStorageImpl.getString("test2");
        System.out.println(res);
        System.out.println(res2);
    }

    @Test
    public void LogStorageTest2() throws Throwable {
        LogStorageImpl logStorage = new LogStorageImpl("log0");

        LogStorageImpl logStorage2 = new LogStorageImpl("log0");

        LogStorageImpl logStorage3 = new LogStorageImpl("log0");

        long res = logStorage.getLastLogIndex();
        System.out.println(res);

        Thread thread1 = new Thread(new SelectTask(logStorage));
        Thread thread2 = new Thread(new SelectTask(logStorage));
        Thread thread3 = new Thread(new SelectTask(logStorage));

        thread1.start();
        thread2.start();
        thread3.start();
        Thread.sleep(600);
    }

    static class SelectTask implements Runnable {
        LogStorageImpl logStorage;

        public SelectTask(LogStorageImpl logStorage) {
            this.logStorage = logStorage;
        }

        @Override
        public void run() {
            for (int i = 0; i < 20; i++) {
                LogEntry logEntry = logStorage.getEntry(1);
                System.out.println(logEntry);
            }
        }
    }
}
