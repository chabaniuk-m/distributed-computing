package ua.exam.rmi;

import ua.exam.model.Bus;
import ua.exam.model.BusInterface;

import java.rmi.AlreadyBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class ServerRmiTask11 implements Server {
    public static final int PORT = 12321;
    public static final String UNIQUE_BINDING_NAME = "server.bus";
    private boolean fixed = false;
    private List<BusInterface> buses = null;

    public static void main(String[] args) {
        try {
            final Server server = new ServerRmiTask11();
            final Registry registry = LocateRegistry.createRegistry(PORT);
            Remote stub = UnicastRemoteObject.exportObject(server, 0);
            registry.bind(UNIQUE_BINDING_NAME, stub);

            Thread.sleep(Long.MAX_VALUE);
        } catch (RemoteException | AlreadyBoundException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String generateRandomBuses(int n) throws RemoteException {
        fixed = false;
        buses = Bus.b.randomListOfBuses(n);
        return n + " buses are generated";
    }

    @Override
    public String generateFixedBuses() throws RemoteException {
        if (!fixed) {
            buses = Bus.b.getFixedListOfBuses();
            fixed = true;
            return 10 + " buses are generated";
        } else {
            return "The list of 10 buses have been already set.";
        }
    }

    @Override
    public String present() throws RemoteException {
        if (buses == null || buses.isEmpty()) {
            return "<no buses>";
        } else {
            return String.join("\n", buses.stream().map(BusInterface::toString).toList());
        }
    }

    @Override
    public String getByRouteNumber(int n) throws RemoteException {
        if (buses == null) {
            return "‼️Список автобусів ще ні ініціалізований";
        }
        List<BusInterface> onRoute = buses.stream().filter(b -> b.getRouteNumber() == n).toList();
        if (onRoute.isEmpty())
            return "<no buses>";
        else
            return String.join("\n", onRoute.stream().map(BusInterface::toString).toList());
    }

    @Override
    public String getByExploitTerm(long term, ChronoUnit unit) throws RemoteException {
        if (buses == null) {
            return "‼️Список автобусів ще ні ініціалізований";
        }
        LocalDateTime now = LocalDateTime.now();
        List<BusInterface> res = new ArrayList<>();
        for (BusInterface bus : buses) {
            LocalDateTime exploitStarted = LocalDateTime.of(bus.getManufacturedYear(), Month.JANUARY, 1, 1, 1, 1);
            if (unit.between(exploitStarted, now) > term) {
                res.add(bus);
            }
        }
        if (res.isEmpty()) {
            return "<no buses>";
        } else {
            return String.join("\n", res.stream().map(BusInterface::toString).toList());
        }
    }

    @Override
    public String getByMileage(double mileage) throws RemoteException {
        if (buses == null) {
            return "‼️Список автобусів ще ні ініціалізований";
        }
        List<BusInterface> greaterMileage = buses.stream().filter(b -> b.getMileAge() > mileage).toList();
        if (buses.isEmpty()) {
            return "<no buses>";
        } else {
            return String.join("\n", greaterMileage.stream().map(BusInterface::toString).toList());
        }
    }
}
