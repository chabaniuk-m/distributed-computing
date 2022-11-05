package ua.lab8;

import ua.lab8.client.Client;
import ua.lab8.client.Controller;
import ua.lab8.client.SocketClient;
import ua.lab8.client.View;

import java.util.Scanner;

public class ClientProcess {

    public static void main(String[] args) {
        Client client = new SocketClient();
        Scanner scanner = new Scanner(System.in);
        Controller controller = new Controller(scanner, client);
        View view = new View(controller, scanner);
        view.start();
    }
}
