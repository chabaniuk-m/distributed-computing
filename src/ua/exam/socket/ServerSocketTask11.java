package ua.exam.socket;

import ua.exam.model.Bus;
import ua.exam.model.BusInterface;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class ServerSocketTask11 {
    public static final String HOST = "localhost";
    public static final int PORT = 10352;
    private final BufferedReader in;
    private final PrintWriter out;
    private final ServerSocket server;
    private final Socket client;
    private boolean fixed = false;
    private List<BusInterface> buses = null;

    public static void main(String[] args) {
        new ServerSocketTask11();
    }

    public ServerSocketTask11() {
        try {
            server = new ServerSocket(PORT);
            System.out.println("Waiting for client...");
            client = server.accept();
            System.out.println("Client is connected");
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out = new PrintWriter(client.getOutputStream(), true);
            begin();
            server.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void begin() {
        while (true) {
            String command = readLine();
            switch (command.charAt(0)) {
                case 'g' -> g(Integer.parseInt(command.substring(1)));
                case 'f' -> f();
                case 's' -> s();
                case 'r' -> r(Integer.parseInt(command.substring(1)));
                case 'y' -> y(Integer.parseInt(command.substring(1)));
                case 'm' -> m(Double.parseDouble(command.substring(1)));
                case 'e' -> {
                    return;
                }
            }
        }
    }

    private void g(int n) {
        fixed = false;
        buses = Bus.b.randomListOfBuses(n);
        out.println(n + " buses are generated");
    }

    private void f() {
        if (!fixed) {
            buses = Bus.b.getFixedListOfBuses();
            fixed = true;
            out.println(10 + " buses are generated");
        } else {
            out.println("The list of 10 buses have been already set.");
        }
    }

    private void s() {
        if (buses == null || buses.isEmpty()) {
            out.println(1);
            out.println("<no buses>");
        } else {
            out.println(buses.size());
            out.println(String.join("\n", buses.stream().map(BusInterface::toString).toList()));
        }
    }

    private void r(int n) {
        if (buses == null) {
            out.println(1);
            out.println("‼️Список автобусів ще ні ініціалізований");
            return;
        }
        List<BusInterface> onRoute = buses.stream().filter(b -> b.getRouteNumber() == n).toList();
        if (onRoute.isEmpty()) {
            out.println(1);
            out.println("<no buses>");
        }
        else {
            out.println(onRoute.size());
            out.println(String.join("\n", onRoute.stream().map(BusInterface::toString).toList()));
        }
    }

    private void y(int term) {
        if (buses == null) {
            out.println(1);
            out.println("‼️Список автобусів ще ні ініціалізований");
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        List<BusInterface> res = new ArrayList<>();
        for (BusInterface bus : buses) {
            LocalDateTime exploitStarted = LocalDateTime.of(bus.getManufacturedYear(), Month.JANUARY, 1, 1, 1, 1);
            if (ChronoUnit.YEARS.between(exploitStarted, now) > term) {
                res.add(bus);
            }
        }
        if (res.isEmpty()) {
            out.println(1);
            out.println("<no buses>");
        } else {
            out.println(res.size());
            out.println(String.join("\n", res.stream().map(BusInterface::toString).toList()));
        }
    }

    private void m(double mileage) {
        if (buses == null) {
            out.println(1);
            out.println("‼️Список автобусів ще ні ініціалізований");
            return;
        }
        List<BusInterface> greaterMileage = buses.stream().filter(b -> b.getMileAge() > mileage).toList();
        if (buses.isEmpty()) {
            out.println(1);
            out.println("<no buses>");
        } else {
            out.println(greaterMileage.size());
            out.println(String.join("\n", greaterMileage.stream().map(BusInterface::toString).toList()));
        }
    }

    private String readLine() {
        try {
            return in.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
