package ua.lab7.sql;

import java.sql.*;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class View {
    private final Scanner scanner = new Scanner(System.in);

    public void start() {
        Controller controller;
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/disk", "Admin", "19Max#177m");
            controller = new Controller(connection, scanner);
            System.out.println("Connection to database is successful");
        } catch (SQLException e) {
            System.out.println("ERROR: cannot connect to database");
            System.out.println(e.getMessage());
            throw new RuntimeException();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException();
        }
        try {
            showCommands();
            while (true) {
                System.out.print("Enter a command: ");
                String command = scanner.nextLine();
                switch (command) {
                    case "exit":
                        return;
                    case "create file":
                        controller.createFile();
                        break;
                    case "create folder":
                        controller.createFolder();
                        break;
                    case "delete file":
                        while (true) {
                            try {
                                System.out.print("Enter file code to delete: ");
                                String str = scanner.nextLine();
                                int code = Integer.parseInt(str);
                                if (code < 0) throw new NumberFormatException();
                                controller.deleteFile(code);
                                break;
                            } catch (NumberFormatException e) {
                                System.out.println("You must enter a non negative integer");
                            }
                        }
                        break;
                    case "delete folder":
                        while (true) {
                            try {
                                System.out.print("Enter folder code to delete: ");
                                String str = scanner.nextLine();
                                int code = Integer.parseInt(str);
                                if (code < 0) throw new NumberFormatException();
                                controller.deleteFolder(code);
                                break;
                            } catch (NumberFormatException e) {
                                System.out.println("You must enter a non negative integer");
                            }
                        }
                        break;
                    case "files":
                        controller.readAllFiles();
                        break;
                    case "folders":
                        controller.readAllFolders();
                        break;
                    case "update file":
                        while (true) {
                            try {
                                System.out.print("Enter file code to update: ");
                                String str = scanner.nextLine();
                                int code = Integer.parseInt(str);
                                if (code < 0) throw new NumberFormatException();
                                controller.updateFile(code);
                                break;
                            } catch (NumberFormatException e) {
                                System.out.println("You must enter a non negative integer");
                            }
                        }
                        break;
                    case "update folder":
                        try {
                            System.out.print("Enter folder code to update: ");
                            String str = scanner.nextLine();
                            int code = Integer.parseInt(str);
                            if (code < 0) throw new NoSuchElementException();
                            controller.updateFolder(code);
                        } catch (NumberFormatException e) {
                            System.out.println("You must enter a non negative integer value");
                        }
                        break;
                    default:
                        System.out.printf("Command \"%s\" is not recognized\nTry one of the following commands:\n", command);
                        showCommands();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void showCommands() {
        System.out.println("""
                 - exit
                 - create file
                 - create folder
                 - delete file: deletes files by code
                 - delete folder: find folder by code and deletes it with all associated files
                 - files: shows all files in the database
                 - folders: shows all folders in the database
                 - update file: finds file by code and updates selected attribute
                 - update folder: finds folder by code and changes its name
                """);
    }
}
