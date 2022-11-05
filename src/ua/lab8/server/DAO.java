package ua.lab8.server;

import ua.lab8.client.model.File;
import ua.lab8.client.model.Folder;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DAO {
    private Connection connection;

    public DAO() {
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/filesys", "Admin", "19Max#177m");
            System.out.println("Connection to database is successful");
        } catch (SQLException e) {
            System.out.println("ERROR: cannot connect to database");
            System.out.println(e.getMessage());
            throw new RuntimeException();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException();
        }
    }

    public boolean saveIfAbsent(Folder folder) {
        // TODO check database
        return false;
    }

    public boolean saveIfAbsent(File file) {
        // TODO work with database
        return false;
    }

    public String readAllFolders() {
        // ‼️❕❗last row must be "end"
        // TODO read form database
        return """
                Folder{ name="Documents" }
                Folder{ name="Desktop" }
                Folder{ name="Pictures" }
                Folder{ name="Program Files" }
                end""";
    }

    public String readAllFilesInFolder(Folder folder) {
        // last row must be "end"
        return """
                File{ name="Document/report.docx", size=284398134, visible=true, readable=false, writeable=true, lastUpdated=2022-11-04T03:45:13 }
                end""";
    }

    public String deleteFiles(String folderName, String fileName) {
        // TODO check if file is present is the specified folder if not return ~~file not found~~
        // TODO if present deletes file and return file has been successfully deleted
        return String.format("There is no file \"%s/%s\" in the file system", folderName, fileName);
    }

    public void deleteFiles(String folderName) {
        // TODO delete all files in folder "folderName"
    }

    public String deleteFolder(String folderName) {
        // TODO if present - successfully deleted ❗the folder is always present (checked by client)
        return "There is no folder \"" + folderName + "\" is file system";
    }

    public void updateFile(String folderName, String fileName, String attr, String value) {
        // TODO don't forget to update last_updated attribute
        // file is guarantied present in the provided folder
    }
}
