package com.yhl.rpc.client;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by daoxiangcun on 17-8-14.
 */
public class NIOTest {
    public static class NIOServer {
        public void serve(int port) throws IOException {
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            InetSocketAddress address = new InetSocketAddress(port);
            serverSocketChannel.socket().bind(address);
            if (serverSocketChannel.socket().isBound()) {
                System.out.println("nio, server bind to " + port + " success");
            }
            Selector selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            while (true) {
                selector.select();
                Set<SelectionKey> keysSet = selector.selectedKeys();
                System.out.println("selector.select()");
                Iterator<SelectionKey> iterator = keysSet.iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    System.out.println("key is accept:" + key.isAcceptable() + ", isReadable:" + key.isReadable() + ", isWritable:" + key.isWritable());
                    try {
                        if (key.isAcceptable()) {
                            ServerSocketChannel server = (ServerSocketChannel) key.channel();
                            SocketChannel client = server.accept();
                            client.configureBlocking(false);
                            client.register(selector, SelectionKey.OP_READ);
                        }
                        if (key.isReadable()) {
                            SocketChannel client = (SocketChannel) key.channel();
                            ByteBuffer buffer = ByteBuffer.allocate(1024);
                            int nReadBytes = client.read(buffer);
                            if (nReadBytes <= 0) {
                                client.close();
                                continue;
                            }
                            buffer.flip();
                            String receivedString = Charset.forName("utf-8").newDecoder().decode(buffer).toString();
                            System.out.println("nio, from client:" + receivedString);
                            String sendString = receivedString;
                            sendMsg(client, sendString);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        key.cancel();
                        try {
                            key.channel().close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    public static void sendMsg(SocketChannel socketChannel, String message) throws IOException {
        ByteBuffer writeBuffer = ByteBuffer.wrap(message.getBytes("utf-8"));
        socketChannel.write(writeBuffer);
    }

    public static class NIOClient {
        public void connectServerUseSelector() throws IOException {
            int port = 6030;
            InetSocketAddress address = new InetSocketAddress(port);
            SocketChannel client = SocketChannel.open(address);
            client.configureBlocking(false);
            Selector selector = Selector.open();  // 打开并注册选择器到信道
            client.register(selector, SelectionKey.OP_READ);
            sendMsg(client, "hello, I'm client");
            while (selector.select() > 0) {
                Set<SelectionKey> keysSet = selector.selectedKeys();
                System.out.println("client selector.select()");
                Iterator<SelectionKey> iterator = keysSet.iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    if (key.isReadable()) {
                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        int nBytes = client.read(buffer);
                        if (nBytes <= 0) {
                            client.close();
                            continue;
                        }
                        buffer.flip();
                        String receivedString = Charset.forName("utf-8").newDecoder().decode(buffer).toString();
                        System.out.println("nio, from server:" + receivedString);
                    }
                }
            }
        }

        public void connectServer() {
            try {
                int port = 6030;
                InetSocketAddress address = new InetSocketAddress(port);
                SocketChannel socketChannel = SocketChannel.open(address);
                socketChannel.configureBlocking(false);
                ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                sendMsg(socketChannel, "hello, 1111111");
                while (true) {
                    byteBuffer.clear();
                    int readBytes = socketChannel.read(byteBuffer);
                    System.out.println("readBytes = " + readBytes);
                    if (readBytes > 0) {
                        byteBuffer.flip();
                        System.out.println("readBytes = " + readBytes);
                        System.out.println("from server: data = " + new String(byteBuffer.array(), 0, readBytes));
                        socketChannel.close();
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public static void main(String[] argvs) throws IOException {
            NIOClient client = new NIOClient();
            client.connectServerUseSelector();  // 使用selector
            ///client.connectServer();  // 不使用selector
        }
    }

    public static void main(String[] argvs) {
        NIOServer server = new NIOServer();
        try {
            server.serve(6030);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}