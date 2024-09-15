import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;
import ru.spring.core.project.*;

import java.io.*;
public class TestCustomHandler {
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

            logWriter.write("--------------------\n\n\n\n");
        }
    }
    class ReadOnlyHandler implements HttpHandler{
        private HttpResponse handleGet(HttpRequest request, Resource resource){
            HttpResponse response = new HttpResponse();
            if(resource.resourceIsExist()){
                response.setStatusCode(HttpStatus.OK.statusCode);
                response.setStatus(HttpStatus.OK.statusDescription);
                response.setBody(resource.getData().toString());
            }else{
                response.setStatusCode(HttpStatus.NO_CONTENT.statusCode);
                response.setStatus(HttpStatus.NO_CONTENT.statusDescription);
            }
            return response;
        }
        private HttpResponse handleAccesError(){
            HttpResponse response = new HttpResponse();
            response.setStatusCode(HttpStatus.FORBIDDEN.statusCode);
            response.setStatus(HttpStatus.FORBIDDEN.statusDescription);
            response.setBody("this is only read only endpoint\n");
            return response;
        }
        @Override
        public HttpResponse handle(HttpRequest request, Resource resource) throws RuntimeException{
            HttpResponse retResponse;
            switch (request.getMethod()){
                case GET -> retResponse = handleGet(request, resource);
                case PUT -> retResponse = handleAccesError();
                case POST -> retResponse = handleAccesError();
                case PATCH -> retResponse = handleAccesError();
                case DELETE -> retResponse = handleAccesError();
                default -> throw new RuntimeException("Unknown command");
            }
            return retResponse;
        }
    }
    @Test
    void testCustomHandler() throws IOException, InterruptedException {
        int port = 8090;
        String logFilePath = "src/test/log/testCustomHandler.txt"; // Файл, в который будем записывать лог
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFilePath, false))) {
            writer.write("");  // Очищаем файл
        }

        new Thread(() -> {
            HttpServer server = new HttpServer("127.0.0.1",port);

            JsonObject jsonObject = new JsonObject();
            server.getRouter().createUrlDefaultHandler("api/default");
            jsonObject.addProperty("text","api_default text");
            server.getRouter().setDataByUrl("api/default",jsonObject);

            server.getRouter().createUrlCustomHandler("api/readonly", new ReadOnlyHandler());
            jsonObject = new JsonObject();
            jsonObject.addProperty("text","api_readonly text");
            server.getRouter().setDataByUrl("api/readonly",jsonObject);
            server.bootstrap();
        }).start();
        Thread.sleep(100);




        executeCommand(String.format("curl -v http://127.0.0.1:%d/api/default", port), logFilePath);
        executeCommand(String.format("curl -v http://127.0.0.1:%d/api/readonly", port), logFilePath);


        String command = String.format("curl -v -X POST http://127.0.0.1:%d/api/default \\\n" +
                "-H \"Content-Type: application/json\" \\\n" +
                "-d '{\"firstName\": \"Michael\", \"lastName\": \"Jackson\", \"phone\": \"+7 (952) 812\"}'", port);
        executeCommand(command, logFilePath);


        command = String.format("curl -v -X POST http://127.0.0.1:%d/api/readonly \\\n" +
                "-H \"Content-Type: application/json\" \\\n" +
                "-d '{\"firstName\": \"Michael\", \"lastName\": \"Jackson\", \"phone\": \"+7 (952) 812\"}'", port);
        executeCommand(command, logFilePath);

        command = String.format("curl -v -X DELETE http://127.0.0.1:%d/api/readonly", port);
        executeCommand(command, logFilePath);

        command = String.format("curl -v -X DELETE http://127.0.0.1:%d/api/default", port);
        executeCommand(command, logFilePath);

        executeCommand(String.format("curl -v http://127.0.0.1:%d/api/default", port), logFilePath);
        executeCommand(String.format("curl -v http://127.0.0.1:%d/api/readonly", port), logFilePath);
    }
}
