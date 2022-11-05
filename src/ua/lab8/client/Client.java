package ua.lab8.client;

import org.json.simple.JSONObject;
import ua.lab8.server.Server;

import java.io.*;
import java.net.Socket;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

public class Client {
    public static void main(String[] args) {
        View view = new View();
        view.start();
    }

    private Scanner scanner;
    private final Socket socket;
    private final BufferedReader in;
    private final PrintWriter out;
    private final Reader reader;

    public Client(Scanner scanner) {
        this.scanner = scanner;
        try {
            reader = new Reader();
            socket = new Socket(Server.HOST, Server.PORT);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private class Reader {
        String readFolderName(List<String> presentFolderNames) {
            String name;
            while (true) {
                System.out.print("Folder name: ");
                do {
                    name = scanner.nextLine();
                } while (name.isEmpty());
                if (presentFolderNames != null && !presentFolderNames.contains(name)) {
                    System.out.printf("There is no folder with name \"%s\" in file system.\nTry one of the following:\n", name);
                    presentFolderNames.forEach(System.out::println);
                } else
                    break;
            }
            return name;
        }

        String readFileName(List<String> files) {
            String name;
            while (true) {
                System.out.print("File name: ");
                do {
                    name = scanner.nextLine();
                } while (name.isEmpty());
                try {
                    Path.of(name);
                    if (files != null && !files.contains(name)) {
                        System.out.printf("There is no file with name \"%s\" in the current folder.\nTry one of the following:\n", name);
                        files.forEach(System.out::println);
                        continue;
                    }
                    break;
                } catch (InvalidPathException e) {
                    System.out.println(e.getMessage());
                }
            }
            return name;
        }

        public long readSize() {
            long size;
            while (true) {
                System.out.print("File size in bytes: ");
                try {
                    size = Long.parseLong(scanner.nextLine());
                    if (size < 0) throw new NumberFormatException();
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("ERROR: size of file must be a positive integer");
                }
            }
            return size;
        }

        /**
         * Ask user for input before calling
         * @return boolean value
         */
        public boolean readBoolean() {
            boolean b;
            while (true) {
                try {
                    b = scanner.nextBoolean();
                    break;
                } catch (InputMismatchException e) {
                    scanner = new Scanner(System.in);
                    System.out.print("Enter \"true\" or \"false\": ");
                }
            }
            return b;
        }
    }

    private List<String> readFolders(boolean detailed) {
        out.println("folders");
        List<String> folders = new ArrayList<>();
        try {
            while (true) {
                String str = in.readLine();         // something like 'Folder{ name="Documents" }'
                if (str.equals("end")) break;
                folders.add(str);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (!detailed)
            folders = folders.stream().map(folder -> folder.substring(folder.indexOf('\"') + 1, folder.lastIndexOf('\"'))).toList();
        return folders;
    }

    public void createFile() {
        var folders = readFolders(false);
        if (folders.size() == 0 || folders.get(0).equals("<empty>")) {
            System.out.println("There is no folders in file system, but before file creation at least one must be");
            return;
        }
        String folderName = reader.readFolderName(folders);
        long size = reader.readSize();
        System.out.print("Is file visible: ");
        boolean visible = reader.readBoolean();
        System.out.print("Is file readable: ");
        boolean readable = reader.readBoolean();
        System.out.print("Is file writeable: ");
        boolean writeable = reader.readBoolean();
        String fileName = reader.readFileName(null);
        // try to save object
        while (true) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("folder_name", folderName);
            jsonObject.put("file_name", fileName);
            jsonObject.put("size", "" + size);
            jsonObject.put("is_visible", "" + visible);
            jsonObject.put("is_readable", "" + readable);
            jsonObject.put("is_writeable", "" + writeable);
            jsonObject.put("last_updated", "" + Timestamp.valueOf(LocalDateTime.now()));

            out.println("create file");
            out.println(jsonObject.toJSONString() + "\nend");

            String response;
            try {
                response = in.readLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (response.equals("success")) {
                System.out.printf("File \"%s/%s\" is successfully created\n", folderName, fileName);
                break;
            }
            System.out.println(response);
            if (askUser("Choose another file name?"))
                fileName = reader.readFileName(null);
            else if (askUser("Maybe choose another folder location?"))
                folderName = reader.readFolderName(folders);
            else
                return;
        }
    }

    public void createFolder() {
        while (true) {
            String name;
            while (true) {
                System.out.print("Folder name: ");
                name = scanner.nextLine();
                try {
                    Path.of(name + "/a");
                    break;
                } catch (InvalidPathException e) {
                    System.out.println(e.getMessage());
                    if (!askUser("Try another folder name?"))
                        return;
                }
            }
            out.println("create folder");
            out.println(name);
            String response;
            try {
                response = in.readLine().replace('\\', '\n');
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (response.equals("success"))
                break;
            else {
                System.out.println(response);
                if (!askUser("Create another folder?"))
                    break;
            }
        }
    }

    public void deleteFolder() {
        String folderName = reader.readFolderName(readFolders(false));
        if (readFolders(false).isEmpty()) {
            System.out.println("File system is empty. Try to create something first");
            return;
        }        System.out.println("Are you sure you want to delete folder with the following files:");
        getAllFilesInFolder(false, folderName);
        if (!askUser("?"))
            return;
        out.println("delete folder");
        out.println(folderName);
        try {
            System.out.println(in.readLine());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteFile() {
        var folders = readFolders(false);
        if (folders.isEmpty()) {
            System.out.println("File system is empty. Try to create something first");
            return;
        }        String folderName = reader.readFolderName(folders);
        String fileName = reader.readFileName(null);
        out.println("delete file");
        out.println(folderName + "/" + fileName);
        try {
            System.out.println(in.readLine());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateFile() {
        var folders = readFolders(false);
        if (folders.isEmpty()) {
            System.out.println("File system is empty. Try to create something first");
            return;
        }        System.out.println("Firstly we need to find the file");
        String folderName, fileName;
        List<String> files;
        while (true) {
            folderName = reader.readFolderName(folders);
            files = getAllFilesInFolder(false, folderName);
            if (files.isEmpty()) {
                System.out.println("This folder is empty");
                if (askUser("Choose another folder?"))
                    continue;
                else
                    return;
            }
            break;
        }
        while (true) {
            fileName = reader.readFileName(null);
            if (files.contains(fileName))
                break;
            else {
                System.out.printf("There is no file \"%s\" in folder \"%s\".\nTry on of the following:\n", fileName, folderName);
                files.forEach(System.out::println);
            }
        }
        String newValue;
        while (true) {
            System.out.print("Choose attribute: ");
            String attr = scanner.nextLine();
            switch (attr) {
                case "file_name" -> {
                    while (true) {
                        newValue = reader.readFileName(null);
                        if (files.contains(newValue)) {
                            System.out.printf("File with this name is already present in folder \"%s\"\n", folderName);
                            if (askUser("Do you want to choose another name?"))
                                continue;
                            else
                                return;
                        }
                        break;
                    }
                }
                case "folder_name" -> {
                    while (true) {
                        newValue = reader.readFolderName(folders);
                        if (getAllFilesInFolder(false, newValue).contains(fileName)) {
                            System.out.printf("File \"%s\" is already present in folder \"%s\"\n", fileName, folderName);
                            if (askUser("Do you want to choose another folder?"))
                                continue;
                            else
                                return;
                        }
                        break;
                    }
                }
                case "size" -> {
                    long size = reader.readSize();
                    newValue = String.valueOf(size);
                }
                case "is_visible", "is_readable", "is_writeable" -> {
                    System.out.print("new value: ");
                    newValue = String.valueOf(reader.readBoolean());
                }
                default -> {
                    System.out.printf("File doesn't have attribute \"%s\"\nLook at list of file's attributes:\n", attr);
                    System.out.println("""
                             - file_name
                             - folder_name
                             - size
                             - is_visible
                             - is_readable
                             - is_writeable""");
                    continue;
                }
            }
            out.println("update file");
            out.println(folderName + "/" + fileName + " " + attr + ":" + newValue);
            if (attr.equals("file_name"))
                fileName = newValue;
            else if (attr.equals("folder_name"))
                folderName = newValue;
            System.out.printf("File \"%s/%s\" is successfully updated\n", folderName, fileName);
            if (!askUser("Do you want to update another attribute?"))
                break;
        }
    }

    private Map<String, String> readFileMovingAttrs() {
        Map<String, String> map = new HashMap<>();
        var folders = readFolders(false);
        if (folders.isEmpty()) {
            System.out.println("File system is empty. Try to create something first");
            return null;
        } else if (folders.size() == 1) {
            System.out.println("This operation is unsupported: there is only 1 file in the file system");
            return null;
        }
        System.out.println("Firstly we need to find the file by name and folder");
        List<String> files;
        while (true) {
            map.put("src_folder", reader.readFolderName(folders));
            files = getAllFilesInFolder(false, map.get("src_folder"));
            if (files.isEmpty()) {
                System.out.println("This folder is empty");
                if (askUser("Choose another folder?"))
                    continue;
                else
                    return null;
            }
            break;
        }
        String fileName = reader.readFileName(files);
        String dstFolder;
        while (true) {
            dstFolder = reader.readFolderName(folders);
            files = getAllFilesInFolder(false, dstFolder);
            if (files.contains(fileName)) {
                System.out.printf("Folder \"%s\" contains file with name \"%s\"\n", dstFolder, fileName);
                if (askUser("Do you want to choose another folder?"))
                    continue;
                else
                    return null;
            }
            break;
        }
        map.put("file_name", fileName);
        map.put("dst_folder", dstFolder);

        return map;
    }

    public void moveFile() {
        var map = readFileMovingAttrs();
        if (map == null) return;
        String fileName = map.get("file_name");
        String dstFolder = map.get("dst_folder");
        out.println("update file");
        out.println(map.get("src_folder") + "/" + fileName + " folder_name:" + dstFolder);
        System.out.printf("File \"%s\" is successfully moved to folder \"%s\"\n", fileName, dstFolder);
    }

    public void copyFile() {
        var map = readFileMovingAttrs();
        if (map == null) return;
        String fileName = map.get("file_name");
        String srcFolder = map.get("src_folder");
        String dstFolder = map.get("dst_folder");
        out.println("copy file");
        out.println(srcFolder + " " + fileName + " " + dstFolder);
        System.out.printf("File \"%s\" is successfully copied to folder \"%s\"\n", fileName, dstFolder);
    }

    public void showAllFolders() {
        readFolders(true).forEach(System.out::println);
    }

    private List<String> getAllFilesInFolder(boolean detailed, String folderName) {
        out.println("files");
        out.println(folderName);
        List<String> files = new ArrayList<>();
        try {
            do {
                String str = in.readLine();
                if (str.equals("end"))
                    break;
                files.add(str);
            } while (true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (!detailed) {
            files = files.stream().map(file -> file.substring(file.indexOf('/') + 1, file.lastIndexOf('\"'))).toList();
        }
        return files;
    }

    public void showAllFilesInFolder(boolean detailed) {
        var folders = readFolders(false);
        if (folders.isEmpty() || folders.get(0).equals("<empty>")) {
            System.out.println("Operation is unsupported: there is no files in the file system");
            return;
        }
        String folderName;
        while (true) {
            System.out.print("Enter folder name: ");
            folderName = scanner.nextLine();
            if (folders.contains(folderName))
                break;
            else {
                System.out.printf("There is no folder with name \"%s\" in the file system.\nTry on of the following:\n", folderName);
                folders.forEach(System.out::println);
            }
        }
        getAllFilesInFolder(detailed, folderName).forEach(System.out::println);
    }

    public void exit() {
        out.println("exit");
        try {
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean askUser(String question) {
        while (true) {
            scanner = new Scanner(System.in);
            System.out.printf("%s (y/n): ", question);
            String answer = scanner.nextLine();
            if (answer.equals("y"))
                return true;
            else if (answer.equals("n"))
                return false;
            System.out.printf("Don't understand \"%s\"\n", answer);
        }
    }
}
