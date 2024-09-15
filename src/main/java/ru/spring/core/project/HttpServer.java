package ru.spring.core.project;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;


public class HttpServer {
    private String hostName;
    private int port;

    private final Router router;
    private ServerSocketChannel server;
    private final static int BUFFER_SIZE = 256;
    public HttpServer(String hostName, int port){
        this.hostName = hostName;
        this.port = port;
        router = new Router();
    }
    public Router getRouter() {
        return router;
    }
    public void bootstrap(){
        try {
            server = ServerSocketChannel.open();             // позволяет обрабатывать подключения клиентов без остановки основного потока
            server.bind(new InetSocketAddress(hostName,port)); //
            while(true) {
                SocketChannel clientChannel = server.accept(); // Асинхронно принимает входящее подключение от клиента, в контейнер future
                //System.out.println("In loop");
                new Thread(() -> {
                    try {
                        handleClient(clientChannel);
                    } catch (IOException e) {
                        e.printStackTrace();  // Логируем исключение
                    }
                }).start();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void handleClient(SocketChannel clientChannel) throws IOException {
       // System.out.println("new client connection");
        while (clientChannel != null && clientChannel.isOpen()) {
            String requestStr = readFromChannel(clientChannel);
            HttpRequest request = new HttpRequest(requestStr);
            HttpResponse response = getHttpResponse(request);

            ByteBuffer resp = ByteBuffer.wrap(response.getBytes());
            clientChannel.write(resp);  // после этой функции ответ появляется в браузере
            clientChannel.close();
        }
    }

    private String readFromChannel(SocketChannel clientChannel) throws IOException {
        StringBuilder builder = new StringBuilder();
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
        boolean keepReading = true;
        while (keepReading) {

            int readResult = clientChannel.read(buffer);

            keepReading = readResult == BUFFER_SIZE;
            buffer.flip();
            CharBuffer charBuffer = StandardCharsets.UTF_8.decode(buffer);
            builder.append(charBuffer);
            buffer.clear();
        }

        return builder.toString();
    }

    private HttpResponse getHttpResponse(HttpRequest request){
        return router.handle(request);
    }

}
