package ua.lab8.client;

public interface Client {
    String readLine();
    void println(String str);

    void close();
}
