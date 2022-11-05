package ua.lab8.server;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import ua.lab8.client.model.File;
import ua.lab8.client.model.Folder;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Timestamp;

public class Server {
    public static final String HOST = "localhost";
    public static final int PORT = 10352;
    private static final DAO dao = new DAO();
    private static PrintWriter out;
    private static BufferedReader in;

    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(PORT);
        System.out.println("Waiting for a client");
        Socket client = server.accept();
        out = new PrintWriter(client.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        System.out.println("Client connected");
        workWithClient();
        server.close();
    }

    private static void workWithClient() {
        while (true) {
            String command;
            try {
                command = in.readLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            switch (command) {
                case "create folder" -> createFolder();
                case "create file" -> createFile();
                case "delete folder" -> deleteFolder();
                case "delete file" -> deleteFile();
                case "update file" -> updateFile();
                case "copy file" -> copyFile();
                case "folders" -> readAllFolders();
                case "files" -> readAllFilesInFolder();
                case "exit" -> System.exit(0);
                default ->
                    throw new IllegalArgumentException("Command \"" + command + "\" is not recognized");
            }
        }
    }

    private static void copyFile() {
        try {
            String[] srcFileDst = in.readLine().split("/");
            dao.copyFile(srcFileDst[0], srcFileDst[1], srcFileDst[2]);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void updateFile() {
        try {
            String[] pathAndUpdate = in.readLine().split("\\*");
            String[] path = pathAndUpdate[0].split("/");
            String[] update = pathAndUpdate[1].split(":");
            System.out.printf("Updating file \"%s/%s\": %s = %s\n", path[0], path[1], update[0], update[1]);
            dao.updateFile(path[0], path[1], update[0], update[1]);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void deleteFolder() {
        try {
            String folderName = in.readLine();
            dao.deleteFiles(folderName);
            out.println(dao.deleteFolder(folderName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void deleteFile() {
        try {
            var path = in.readLine().split("/");
            out.println(dao.deleteFile(path[0], path[1]));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void readAllFilesInFolder() {
        try {
            out.println(dao.readAllFilesInFolder(new Folder(in.readLine())));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void readAllFolders() {
        System.out.println("Client requested all folders");
        out.println(dao.readAllFolders());
    }

    private static void createFile() {
        File file;
        try {
            StringBuilder sb = new StringBuilder();
            while (true) {
                String str = in.readLine();
                if (str.equals("end")) break;
                sb.append(str).append('\n');
            }
            JSONObject jo = (JSONObject) new JSONParser().parse(sb.toString());
            System.out.println("Received: \n" + jo.toJSONString());
            file = new File(
                    (String) jo.get("folder_name"),
                    (String) jo.get("file_name"),
                    Long.parseLong((String) jo.get("size")),
                    Boolean.parseBoolean((String) jo.get("is_visible")),
                    Boolean.parseBoolean((String) jo.get("is_readable")),
                    Boolean.parseBoolean((String) jo.get("is_writeable")),
                    Timestamp.valueOf((String) jo.get("last_updated")).toLocalDateTime());
            String response = dao.saveIfAbsent(file) ? "success" :
                    "File with name \"" + file.fileName + "\" is already present in folder \"" + file.folderName + "\"";
            out.println(response);
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private static void createFolder() {
        Folder folder;
        try {
            folder = new Folder(in.readLine());
            System.out.println("Received: " + folder);
            String response = dao.saveIfAbsent(folder) ? "success" :
                    "Folder \"" + folder.name + "\" is already present in file system";
            out.println(response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
