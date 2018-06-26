package com.yhl.rpc.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;

/**
 * Created by daoxiangcun on 17-8-14.
 */
public class BlockIOTest {
    public static class BlockServer {
        public void serve(int port) throws IOException {
            ServerSocket server = new ServerSocket(port);
            if (server.isBound()) {
                System.out.println("server bind to " + port + " success");
            }
            while (true) {
                final Socket client = server.accept();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            InputStream inputStream = client.getInputStream();
                            byte[] bytes = new byte[1024];
                            int nLength = inputStream.read(bytes);
                            String dataToWrite = new String(bytes, 0, nLength, Charset.forName("utf-8"));
                            System.out.println("data from client:" + client.getRemoteSocketAddress() + ", data is: " + dataToWrite);
                            OutputStream outputStream = client.getOutputStream();
                            outputStream.write(bytes, 0, nLength);
                            outputStream.flush();
                            outputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            if (client.isConnected()) {
                                try {
                                    client.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }).start();
            }
        }
    }

    public static class BlockClient {
        public void connectServer() throws IOException {
            int port = 6030;
            String host = null;
            Socket client = new Socket(host, port);
            String hello = "hello";
            client.getOutputStream().write(hello.getBytes(Charset.forName("utf-8")));
            byte[] bytes = new byte[1024];
            int nLength = client.getInputStream().read(bytes);
            String recvFromServer = new String(bytes, 0, nLength, Charset.forName("utf-8"));
            System.out.println("recvFromServer:" + recvFromServer);
        }

        public static void main(String[] argvs) {
            for (int i = 0; i < 10; i++) {
                BlockClient client = new BlockClient();
                try {
                    client.connectServer();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] argvs) {
        BlockServer server = new BlockServer();
        try {
            server.serve(6030);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}