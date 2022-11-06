package ua.lab8.client;

import java.util.List;

public interface Client {

    // old methods
    // \/\/\/\/\/\/\/\/\/\/
//    String readLine();
//    void println(String str);
    // /\/\/\/\/\/\/\/\/\/\/\
    // old methods

    // new methods
    List<String> queryFolders();

    String saveFile(String jsonObj);

    String saveFolder(String folderName);

    String queryDeleteFolder(String folderName);

    String queryDeleteFile(String folderName, String fileName);

    void queryUpdateFile(String updateExpression);

    void queryCopyFile(String copyExpression);

    List<String> queryFiles(String folderName);

    void close();
}
