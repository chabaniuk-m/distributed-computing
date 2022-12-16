package ua.exam.model;

import java.util.List;

public interface BusInterface {

    String getDriverName();

    int getBusNumber();

    int getRouteNumber();

    String getBrand();

    int getManufacturedYear();

    double getMileAge();

    void setDriverName(String newName);

    void setBusNumber(int newNumber);

    void setRouteNumber(int newNumber);

    void setBrand(String newBrand);

    void setManufacturedYear(int newYear);

    void setMileAge(double newMileAge);

    List<BusInterface> randomListOfBuses(int n);

    List<BusInterface> getFixedListOfBuses();

    String toString();
}
