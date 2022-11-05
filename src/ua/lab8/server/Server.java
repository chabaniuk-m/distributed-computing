package ua.lab8.server;

public abstract class Server {
    public static final String HOST = "localhost";
    public static final int PORT = 10352;

    public abstract String readLine();

    public abstract void println(String message);

    public abstract void close();
}
