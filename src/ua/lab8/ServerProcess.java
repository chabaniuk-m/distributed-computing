package ua.lab8;

import ua.lab8.server.Controller;
import ua.lab8.server.DAO;
import ua.lab8.server.SocketServer;

public class ServerProcess {
    public static void main(String[] args) {
        SocketServer server = new SocketServer();
        DAO dao = new DAO();
        Controller controller = new Controller(dao, server);
        controller.begin();
    }
}
