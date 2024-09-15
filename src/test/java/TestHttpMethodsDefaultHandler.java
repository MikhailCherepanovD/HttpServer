import org.junit.jupiter.api.Test;
import ru.spring.core.project.HttpServer;

import java.io.*;

public class TestHttpMethodsDefaultHandler {
    void executeCommand(String command, String logFilePath) throws IOException {
        Process process = Runtime.getRuntime().exec(new String[]{"bash", "-c", command});

        // Чтение стандартного вывода (stdout)
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuilder outputBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            outputBuilder.append(line).append("\n");
        }

        // Чтение потока ошибок (stderr)
        BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        StringBuilder errorBuilder = new StringBuilder();
        while ((line = errorReader.readLine()) != null) {
            errorBuilder.append(line).append("\n");
        }

        // Ждем завершения процесса
        int exitCode;
        try {
            exitCode = process.waitFor();
        } catch (InterruptedException e) {
            throw new RuntimeException("Process interrupted", e);
        }

        // Запись результата и ошибок в файл
        try (BufferedWriter logWriter = new BufferedWriter(new FileWriter(logFilePath, true))) {
            logWriter.write("Command: " + command + "\n");
            logWriter.write("Exit Code: " + exitCode + "\n");

            if (outputBuilder.length() > 0) {
                logWriter.write("Standard Output:\n" + outputBuilder.toString() + "\n");
            }

            if (errorBuilder.length() > 0) {
                logWriter.write("Error Output:\n" + errorBuilder.toString() + "\n");
            }

            logWriter.write("--------------------\n\n\n\n"); // Разделитель для удобства
        }
    }


    @Test
    public void testGet() throws IOException, InterruptedException {
        int port = 8081;
        String logFilePath = "src/test/log/testGet.txt"; // Файл, в который будем записывать лог
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFilePath, false))) {
            writer.write("");  // Очищаем файл
        }
        new Thread(() -> {
            HttpServer server = new HttpServer("127.0.0.1",port);
            server.getRouter().createUrlDefaultHandler("api/user");
            server.getRouter().createUrlDefaultHandler("api/server");
            server.bootstrap();
        }).start();
        Thread.sleep(100);


        executeCommand(String.format("curl -v http://127.0.0.1:%d/", port), logFilePath);
        executeCommand(String.format("curl -v http://127.0.0.1:%d/api", port), logFilePath);
        executeCommand(String.format("curl -v http://127.0.0.1:%d/api/user", port), logFilePath);
        executeCommand(String.format("curl -v http://127.0.0.1:%d/asd", port), logFilePath);// несуществующий URL
    }

    @Test
    public void testPost() throws IOException, InterruptedException {
        int port = 8082;
        String logFilePath = "src/test/log/testPost.txt"; // Файл, в который будем записывать лог
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFilePath, false))) {
            writer.write("");  // Очищаем файл
        }
        new Thread(() -> {
            HttpServer server = new HttpServer("127.0.0.1",port);
            server.getRouter().createUrlDefaultHandler("api/user/user1");
            server.getRouter().createUrlDefaultHandler("api/user/user2");
            server.getRouter().createUrlDefaultHandler("api/user/user3");
            server.getRouter().createUrlDefaultHandler("api/server");
            server.bootstrap();
        }).start();
        Thread.sleep(100);

        String command = String.format("curl -v -X POST http://127.0.0.1:%d/api/user/user1 \\\n" +
                "-H \"Content-Type: application/json\" \\\n" +
                "-d '{\"firstName\": \"Michael\", \"lastName\": \"Jackson\", \"phone\": \"+7 (952) 812\"}'", port);
        executeCommand(command, logFilePath);

        command = String.format("curl -v -X POST http://127.0.0.1:%d/api/user/user2 \\\n" +
                "-H \"Content-Type: application/json\" \\\n" +
                "-d '{\"firstName\": \"Arnold\", \"lastName\": \"Schwarzenegger\", \"phone\": \"+2\"}'", port);
        executeCommand(command, logFilePath);

        command = String.format("curl -v -X POST http://127.0.0.1:%d/api/server \\\n" +
                "-H \"Content-Type: application/json\" \\\n" +
                "-d '{\"serverName\": \"real_server\"}'", port);
        executeCommand(command, logFilePath);

        command = String.format("curl -v -X POST http://127.0.0.1:%d/api/user/hello \\\n" +
                "-H \"Content-Type: application/json\" \\\n" +
                "-d '{\"firstName\": \"Arnold\", \"lastName\": \"Schwarzenegger\", \"phone\": \"+2\"}'", port); // URL не существует
        executeCommand(command, logFilePath);



        executeCommand(String.format("curl -v http://127.0.0.1:%d/api/user/user1", port), logFilePath);
        executeCommand(String.format("curl -v http://127.0.0.1:%d/api/user/user2", port), logFilePath);
        executeCommand(String.format("curl -v http://127.0.0.1:%d/api/service", port), logFilePath);
    }

    @Test
    public void testPut() throws IOException, InterruptedException {
        int port = 8083;
        String logFilePath = "src/test/log/testPut.txt"; // Файл, в который будем записывать лог
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFilePath, false))) {
            writer.write("");  // Очищаем файл
        }
        new Thread(() -> {
            HttpServer server = new HttpServer("127.0.0.1",port);
            server.getRouter().createUrlDefaultHandler("api/user/user1");
            server.getRouter().createUrlDefaultHandler("api/user/user2");
            server.getRouter().createUrlDefaultHandler("api/user/user3");
            server.getRouter().createUrlDefaultHandler("api/server");
            server.bootstrap();
        }).start();
        Thread.sleep(100);

        String command = String.format("curl -v -X POST http://127.0.0.1:%d/api/user/user1 \\\n" +
                "-H \"Content-Type: application/json\" \\\n" +
                "-d '{\"firstName\": \"Michael\", \"lastName\": \"Jackson\", \"phone\": \"+7 (952) 812\"}'", port);
        executeCommand(command, logFilePath);

        command = String.format("curl -v -X POST http://127.0.0.1:%d/api/user/user2 \\\n" +
                "-H \"Content-Type: application/json\" \\\n" +
                "-d '{\"firstName\": \"Arnold\", \"lastName\": \"Schwarzenegger\", \"phone\": \"+2\"}'", port);
        executeCommand(command, logFilePath);

        command = String.format("curl -v -X POST http://127.0.0.1:%d/api/server \\\n" +
                "-H \"Content-Type: application/json\" \\\n" +
                "-d '{\"serverName\": \"real_server\"}'", port);
        executeCommand(command, logFilePath);

        command = String.format("curl -v -X POST http://127.0.0.1:%d/api/user/hello \\\n" +
                "-H \"Content-Type: application/json\" \\\n" +
                "-d '{\"firstName\": \"Arnold\", \"lastName\": \"Schwarzenegger\", \"phone\": \"+2\"}'", port); // URL не существует
        executeCommand(command, logFilePath);



        executeCommand(String.format("curl -v http://127.0.0.1:%d/api/user/user1", port), logFilePath);
        executeCommand(String.format("curl -v http://127.0.0.1:%d/api/user/user2", port), logFilePath);
        executeCommand(String.format("curl -v http://127.0.0.1:%d/api/service", port), logFilePath);


    }

    @Test
    public void testPatch() throws IOException, InterruptedException {
        int port = 8084;
        String logFilePath = "src/test/log/testPatch.txt"; // Файл, в который будем записывать лог
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFilePath, false))) {
            writer.write("");  // Очищаем файл
        }
        new Thread(() -> {
            HttpServer server = new HttpServer("127.0.0.1",port);
            server.getRouter().createUrlDefaultHandler("api/user/user1");
            server.getRouter().createUrlDefaultHandler("api/server");
            server.bootstrap();
        }).start();
        Thread.sleep(100);

        String command = String.format("curl -v -X POST http://127.0.0.1:%d/api/user/user1 \\\n" +
                "-H \"Content-Type: application/json\" \\\n" +
                "-d '{\"firstName\": \"Michael\", \"lastName\": \"Jackson\", \"phone\": \"+7 (952) 812\"}'", port);
        executeCommand(command, logFilePath);

        command = String.format("curl -v -X PATCH http://127.0.0.1:%d/api/user/user1 \\\n" +
                "-H \"Content-Type: application/json\" \\\n" +
                "-d '{\"firstName\": \"Arnold\", \"lastName\": \"Jackson\", \"phone\": \"+2\"}'", port); // изменилось часть данных
        executeCommand(command, logFilePath);

        command = String.format("curl -v -X PATCH http://127.0.0.1:%d/api/server \\\n" +
                "-H \"Content-Type: application/json\" \\\n" +
                "-d '{\"firstName\": \"Arnold\", \"lastName\": \"Jackson\", \"phone\": \"+2\"}'", port); // добавляем данные
        executeCommand(command, logFilePath);


        executeCommand(String.format("curl -v http://127.0.0.1:%d/api/user/user1", port), logFilePath);
        executeCommand(String.format("curl -v http://127.0.0.1:%d/api/server", port), logFilePath);
    }

    @Test
    public void testDelete() throws IOException, InterruptedException {
        int port = 8085;
        String logFilePath = "src/test/log/testDelete.txt"; // Файл, в который будем записывать лог
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFilePath, false))) {
            writer.write("");  // Очищаем файл
        }
        new Thread(() -> {
            HttpServer server = new HttpServer("127.0.0.1",port);
            server.getRouter().createUrlDefaultHandler("api/user/user1");
            server.getRouter().createUrlDefaultHandler("api/server");
            server.bootstrap();
        }).start();
        Thread.sleep(100);

        String command = String.format("curl -v -X POST http://127.0.0.1:%d/api/user/user1 \\\n" +
                "-H \"Content-Type: application/json\" \\\n" +
                "-d '{\"firstName\": \"Michael\", \"lastName\": \"Jackson\", \"phone\": \"+7 (952) 812\"}'", port);
        executeCommand(command, logFilePath);

        executeCommand(String.format("curl -v http://127.0.0.1:%d/api/user/user1", port), logFilePath);

        command = String.format("curl -v -X DELETE http://127.0.0.1:%d/api/user/user1", port);
        executeCommand(command, logFilePath);

        executeCommand(String.format("curl -v http://127.0.0.1:%d/api/user/user1", port), logFilePath);

    }
}
