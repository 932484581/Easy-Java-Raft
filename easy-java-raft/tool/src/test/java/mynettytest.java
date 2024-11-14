import org.junit.Test;

import cn.wjc.tool.entity.Request;
import cn.wjc.tool.netty.client.impl.ClientImpl;
import cn.wjc.tool.netty.server.impl.ServerImpl;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class mynettytest {
    @Test
    public void clientTest() throws Throwable {
        log.info("begin test");
        MyServerThread myServerThread = new MyServerThread(8899);
        myServerThread.start(); // 启动第二个线程
        MyServerThread myServerThread2 = new MyServerThread(8890);
        myServerThread2.start(); // 启动第二个线程
        Thread.sleep(1000);
        MyClientThread myClientThread = new MyClientThread();
        myClientThread.start();
        myClientThread.join();
        log.info("successful");
    }

    @Test
    public void finaltest() throws Throwable {
        Request request = Request.builder().cmd(-1).addr("127.0.0.1:9988").build();

        ServerImpl server = ServerImpl.builder().port(9988).build();
        server.init();

        ClientImpl client = new ClientImpl();
        client.connectToServer("127.0.0.1:9988");

        Thread.sleep(1000);

        client.send(request);
        client.send(request);
        client.send(request);

        Thread.sleep(1000);

    }

    public class MyClientThread extends Thread {
        @Override
        public void run() {
            Request request = Request.builder().cmd(-1).addr("127.0.0.1").build();
            ClientImpl client = new ClientImpl();
            try {
                client.init();
                client.connectToServer("127.0.0.1:8899");
                client.connectToServer("127.0.0.1:8890");
                for (int i = 0; i < 10; i++) {
                    Thread.sleep(1000);
                    request.setAddr("127.0.0.1:8899");
                    client.send(request);
                    request.setAddr("127.0.0.1:8890");
                    client.send(request);
                }
            } catch (Throwable e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }

    public class MyServerThread extends Thread {
        private int port;

        public MyServerThread(int port) {
            this.port = port;
        }

        @Override
        public void run() {
            // 线程执行的代码
            try {
                ServerImpl server = ServerImpl.builder().port(port).build();
                server.init();
            } catch (Throwable e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

}
