package ua.exam.rmi;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.time.temporal.ChronoUnit;
import java.util.Scanner;

public class ClientRmiTask11 {

    private final Server server;

    public static void main(String[] args) throws RemoteException {
        ClientRmiTask11 client = new ClientRmiTask11();
        client.begin();
    }

    public ClientRmiTask11() {
        try {
            Registry registry = LocateRegistry.getRegistry(ServerRmiTask11.PORT);
            server = (Server) registry.lookup(ServerRmiTask11.UNIQUE_BINDING_NAME);
        } catch (RemoteException | NotBoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void begin() throws RemoteException {
        System.out.println("List of all commands:");
        printCommands();
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("> ");
            String command = scanner.nextLine();
            if (command.length() == 0) continue;
            switch (command.charAt(0)) {
                case 's' -> System.out.println(server.present());
                case 'g' -> {
                    if (command.length() == 1 || command.charAt(1) != ' ') invalidCommand(command);
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
                            try {
                                System.out.println(server.generateRandomBuses(n));
                            } catch (RemoteException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }
                case 'f' -> System.out.println(server.generateFixedBuses());
                case 'r' -> {
                    if (command.length() == 1 || command.charAt(1) != ' ') invalidCommand(command);
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
                            try {
                                System.out.println(server.getByRouteNumber(n));
                            } catch (RemoteException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }
                case 'y' -> {
                    if (command.length() == 1 || command.charAt(1) != ' ') invalidCommand(command);
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
                            try {
                                System.out.println(server.getByExploitTerm(n, ChronoUnit.YEARS));
                            } catch (RemoteException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }
                case 'm' -> {
                    if (command.length() == 1 || command.charAt(1) != ' ') invalidCommand(command);
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
                            try {
                                System.out.println(server.getByMileage(n));
                            } catch (RemoteException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }
                case 'e' -> {
                    try {
                        server.exit();
                    } catch (Exception e) {
                        System.out.println("Thank you for using our service!");
                        return;
                    }
                }
                default -> invalidCommand(command);
            }
        }
    }

    public static void invalidCommand(String command) {
        System.out.printf("The command '%s' is not recognized. Try one of the following:\n", command);
        printCommands();
    }

    public static void printCommands() {
        System.out.println("""
                 1) s - show all buses;
                 2) g [n] - generate list of n random buses;
                 3) f - generate fixed list of buses;
                 4) r [n] - show buses for the provided rout n;
                 5) y [y] - show all buses that are in use more than y years;
                 6) m [a] - show all buses that has mileage larger than a;
                 7) e - exit.""");
    }
}
