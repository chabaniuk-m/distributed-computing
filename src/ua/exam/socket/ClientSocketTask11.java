package ua.exam.socket;

import ua.exam.rmi.ClientRmiTask11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.Scanner;

import static ua.exam.rmi.ClientRmiTask11.printCommands;

public class ClientSocketTask11 {
    private final BufferedReader in;
    private final PrintWriter out;

    public ClientSocketTask11() {
        try {
            Socket socket = new Socket(ServerSocketTask11.HOST, ServerSocketTask11.PORT);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            begin();
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        new ClientSocketTask11();
    }

    private void begin() throws RemoteException, IOException {
        System.out.println("List of all commands:");
        printCommands();
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("> ");
            String command = scanner.nextLine();
            if (command.length() == 0) continue;
            switch (command.charAt(0)) {
                case 's' -> {
                    out.println('s');
                    int count = Integer.parseInt(in.readLine());
                    for (int i = 0; i < count; i++) {
                        System.out.println(in.readLine());
                    }
                }
                case 'g' -> {
                    if (command.length() == 1 || command.charAt(1) != ' ') ClientRmiTask11.invalidCommand(command);
                    else {
                        int n = 0;
                        try {
                            n = Integer.parseInt(command.substring(1).trim());
                        } catch (NumberFormatException e) {
                            System.out.println("Please enter a whole number after a space");
                            continue;
                        }
                        if (n <= 0) {
                            System.out.println("Please enter a positive number");
                        } else {
                            out.println("g" + n);
                            System.out.println(in.readLine());
                        }
                    }
                }
                case 'f' -> {
                    out.println('f');
                    System.out.println(in.readLine());
                }
                case 'r' -> {
                    if (command.length() == 1 || command.charAt(1) != ' ') ClientRmiTask11.invalidCommand(command);
                    else {
                        int n = 0;
                        try {
                            n = Integer.parseInt(command.substring(1).trim());
                        } catch (NumberFormatException e) {
                            System.out.println("Please enter a whole number after a space");
                            continue;
                        }
                        if (n <= 0) {
                            System.out.println("Please enter a positive number");
                        } else {
                            out.println("r" + n);
                            int count = Integer.parseInt(in.readLine());
                            for (int i = 0; i < count; i++) {
                                System.out.println(in.readLine());
                            }
                        }
                    }
                }
                case 'y' -> {
                    if (command.length() == 1 || command.charAt(1) != ' ') ClientRmiTask11.invalidCommand(command);
                    else {
                        int n = 0;
                        try {
                            n = Integer.parseInt(command.substring(1).trim());
                        } catch (NumberFormatException e) {
                            System.out.println("Please enter a whole number after a space");
                            continue;
                        }
                        if (n <= 0) {
                            System.out.println("Please enter a positive number");
                        } else {
                            out.println("y" + n);
                            int count = Integer.parseInt(in.readLine());
                            for (int i = 0; i < count; i++) {
                                System.out.println(in.readLine());
                            }
                        }
                    }
                }
                case 'm' -> {
                    if (command.length() == 1 || command.charAt(1) != ' ') ClientRmiTask11.invalidCommand(command);
                    else {
                        double n = 0;
                        try {
                            n = Double.parseDouble(command.substring(1).trim());
                        } catch (NumberFormatException e) {
                            System.out.println("Please enter a whole number after a space");
                            continue;
                        }
                        if (n < 0) {
                            System.out.println("Please enter a non-negative number");
                        } else {
                            out.println("m" + n);
                            int count = Integer.parseInt(in.readLine());
                            for (int i = 0; i < count; i++) {
                                System.out.println(in.readLine());
                            }
                        }
                    }
                }
                case 'e' -> {
                    try {
                        out.println('e');
                        return;
                    } catch (Exception e) {
                        System.out.println("Thank you for using our service!");
                        return;
                    }
                }
                default -> ClientRmiTask11.invalidCommand(command);
            }
        }
    }
}
