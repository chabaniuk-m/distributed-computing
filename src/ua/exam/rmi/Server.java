package ua.exam.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.time.temporal.ChronoUnit;

public interface Server extends Remote {

    String generateRandomBuses(int n) throws RemoteException;

    String generateFixedBuses() throws RemoteException;

    String present() throws RemoteException;

    /**
     * Shows only buses on route n
     */
    String getByRouteNumber(int n) throws RemoteException;

    /**
     * Shows only buses that exploits longer than provided amount of time
     */
    String getByExploitTerm(long term, ChronoUnit unit) throws RemoteException;

    /**
     * Shows only buses which mileage is greater that parameter mileage
     */
    String getByMileage(double mileage) throws RemoteException;

    default void exit() throws RemoteException {
        System.exit(0);
    }
}
