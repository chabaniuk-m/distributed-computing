package ua.lab8.server;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import ua.lab8.model.File;
import ua.lab8.model.Folder;

import java.sql.Timestamp;

public class Controller {
    private final DAO dao;
    private final Server server;

    public Controller(DAO dao, Server server) {
        this.dao = dao;
        this.server = server;
    }

    public void begin() {
        while (true) {
            String command = server.readLine();
            switch (command) {
                case "create folder" -> createFolder();
                case "create file" -> createFile();
                case "delete folder" -> deleteFolder();
                case "delete file" -> deleteFile();
                case "update file" -> updateFile();
                case "copy file" -> copyFile();
                case "folders" -> readAllFolders();
                case "files" -> readAllFilesInFolder();
                case "exit" -> {
                    server.close();
                    System.exit(0);
                }
                default ->
                    throw new IllegalArgumentException("Command \"" + command + "\" is not recognized");
            }
        }
    }

    private void copyFile() {
        String[] srcFileDst = server.readLine().split("/");
        dao.copyFile(srcFileDst[0], srcFileDst[1], srcFileDst[2]);
    }

    private void updateFile() {
        String[] pathAndUpdate = server.readLine().split("\\*");
        String[] path = pathAndUpdate[0].split("/");
        String[] update = pathAndUpdate[1].split(":");
        dao.updateFile(path[0], path[1], update[0], update[1]);
    }

    private void deleteFolder() {
        String folderName = server.readLine();
        dao.deleteFiles(folderName);
        server.println(dao.deleteFolder(folderName));
    }

    private void deleteFile() {
        var path = server.readLine().split("/");
        server.println(dao.deleteFile(path[0], path[1]));
    }

    private void readAllFilesInFolder() {
        server.println(dao.readAllFilesInFolder(new Folder(server.readLine())));
    }

    private void readAllFolders() {
        server.println(dao.readAllFolders());
    }

    private void createFile() {
        File file;
        StringBuilder sb = new StringBuilder();
        while (true) {
            String str = server.readLine();
            if (str.equals("end")) break;
            sb.append(str).append('\n');
        }
        JSONObject jo;
        try {
            jo = (JSONObject) new JSONParser().parse(sb.toString());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
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
        server.println(response);
    }

    private void createFolder() {
        Folder folder;
        folder = new Folder(server.readLine());
        String response = dao.saveIfAbsent(folder) ? "success" :
                "Folder \"" + folder.name + "\" is already present in file system";
        server.println(response);
    }
}
