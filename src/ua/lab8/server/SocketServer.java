package ua.lab8.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketServer extends Server {
    private final BufferedReader in;
    private final PrintWriter out;
    private final ServerSocket server;
    private final Socket client;

    public SocketServer() {
        try {
            server = new ServerSocket(PORT);
            System.out.println("Waiting for client...");
            client = server.accept();
            System.out.println("Client is connected");
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out = new PrintWriter(client.getOutputStream(), true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String readLine() {
        try {
            return in.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void println(String message) {
        out.println(message);
    }

    @Override
    public void close() {
        try {
            client.close();
            server.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
