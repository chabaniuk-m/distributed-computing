package ua.exam.model;

import java.time.LocalDate;
import java.util.*;

public class Bus implements BusInterface {
    private String driverName;
    private int busNumber;
    private int routeNumber;
    private String brand;
    private int manufacturedYear;
    private double mileage;
    public static Bus b = new Bus("", 0, 0, "", 0, 0);


    private static final String[] surnames = new String[] {
            "Капітаненко", "Петренко", "Посвятенко", "Зінченко", "Косенко", "Ращенко", "Малаєнко",
            "Ульєнко", "Ботярко", "Мосейко", "Валько", "Гомілко", "Зрайко", "Лешко",
            "Максімко", "Маціборко", "Мицко", "Настобурко", "Перев'язко", "Полатайко", "Половко",
            "Портянко", "Ріжко", "Стинавко", "Титко", "Федушко", "Фуфалько", "Ховавко",
            "Хомко", "Цьомко", "Шайко", "Шляхетко", "Ямборко", "Безштанько", "Вишенько",
            "Двоєнько", "Витребенько", "Тарабанько", "Опенько", "Господинько", "Покинько", "Полинько",
            "Заковінько", "Колінько", "Цвірінько", "Охінько", "Конько", "Головачко", "Мрічко",
            "Галечка", "Безцінний", "Байдачний", "Байрачний", "Бурий", "Криничний", "Зарівний",
            "Кварцяний", "Кирпатий", "Ласкорунський", "Новохацький", "Водолазький", "Князький", "Печенізький",
            "Слизький", "Запорізький", "Кобизький", "Малішевський", "Гриневський", "Яричківський", "Трублаєвський",
            "Поприч", "Тулейбич", "Меланюк", "Лисканюк", "Симовонюк", "Будейчук", "Веприк", "Чаплик"
    };
    private static final String nameFirstLetters = "АБВГДКЛМНОПРСТХ";

    public Bus(String driverName, int busNumber, int routeNumber) {
        this(driverName, busNumber, routeNumber, "Богдан");
    }

    public Bus(String driverName, int busNumber, int routeNumber, String brand) {
        this(driverName, busNumber, routeNumber, brand, 2012);
    }

    public Bus(String driverName, int busNumber, int routeNumber, String brand, int manufacturedYear) {
        this(driverName, busNumber, routeNumber, brand, manufacturedYear, 0d);
    }

    public Bus(String driverName, int busNumber, int routeNumber, String brand, int manufacturedYear, double mileage) {
        this.driverName = driverName;
        this.busNumber = busNumber;
        this.routeNumber = routeNumber;
        this.brand = brand;
        this.manufacturedYear = manufacturedYear;
        this.mileage = mileage;
    }

    @Override
    public String getDriverName() {
        return driverName;
    }

    @Override
    public int getBusNumber() {
        return busNumber;
    }

    @Override
    public int getRouteNumber() {
        return routeNumber;
    }

    @Override
    public String getBrand() {
        return brand;
    }

    @Override
    public int getManufacturedYear() {
        return manufacturedYear;
    }

    @Override
    public double getMileAge() {
        return mileage;
    }

    @Override
    public void setDriverName(String newName) {
        this.driverName = newName;
    }

    @Override
    public void setBusNumber(int newNumber) {
        this.busNumber = newNumber;
    }

    @Override
    public void setRouteNumber(int newNumber) {
        this.routeNumber = newNumber;
    }

    @Override
    public void setBrand(String newBrand) {
        this.brand = newBrand;
    }

    @Override
    public void setManufacturedYear(int newYear) {
        this.manufacturedYear = newYear;
    }

    @Override
    public void setMileAge(double newMileAge) {
        this.mileage = newMileAge;
    }

    @Override
    public List<BusInterface> randomListOfBuses(int n) {

        if (n < 1) {
            throw new RuntimeException("Number of buses cannot be negative");
        }

        Random random = new Random(System.currentTimeMillis());
        List<BusInterface> res = new ArrayList<>(n);
        int nRoutes = n / (random.nextInt(5) + 2) + 1;
        List<String> surnames = Arrays.asList(Bus.surnames);
        List<String> brands = Arrays.asList("Ataman", "Богдан", "Богдан", "Богдан", "Electron", "Electron", "Тюльпан", "Mercedes Benz", "Mercedes Benz");

        for (int i = 0; i < n; i++) {
            // generate random name
            if (n % surnames.size() == 0) {
                Collections.shuffle(surnames, random);
            }
            String driverName = surnames.get(i % surnames.size()) + " "
                    + nameFirstLetters.charAt(random.nextInt(nameFirstLetters.length())) + "."
                    + nameFirstLetters.charAt(random.nextInt(nameFirstLetters.length())) + ".";

            // bus number
            int number = i + 1;

            // generate random route number
            int route = random.nextInt(nRoutes) + 1;

            // generate random manufacture year
            int year = 1990 + random.nextInt(LocalDate.now().getYear() - 1991);

            // random brand
            String brand = brands.get(random.nextInt(brands.size()));

            // random mileage
            double mileage = 2_000d + random.nextInt(60_000) / 10d;

            res.add(new Bus(driverName, number, route, brand, year, mileage * (LocalDate.now().getYear() - year)));
        }

        return res;
    }

    @Override
    public List<BusInterface> getFixedListOfBuses() {
        return new ArrayList<>(10) {{
            add(new Bus("Сидорчук К.І.", 1, 1));
            add(new Bus("Миргородський А.М.", 2, 2, "Mercedes Benz", 2004, 25_120.3));
            add(new Bus("Гуща В.Л.", 3, 2, "Mercedes Benz", 2006, 21_120.3));
            add(new Bus("Слісаренко Б.Р.", 4, 1, "Mercedes Benz", 2006, 22_431.5));
            add(new Bus("Коваленко К.О.", 5, 3, "Богдан", 2008, 15_125.8));
            add(new Bus("Романюк Б.І.", 6, 3, "Богдан", 2008, 14_800));
            add(new Bus("Борзов С.С.", 7, 4, "Mercedes Benz", 2020));
            add(new Bus("Миколайчук Л.П.", 8, 4, "Богдан"));
            add(new Bus("Миронов О.С.", 9, 4, "Богдан", 2000, 34_807));
            add(new Bus("Слюсарев Н.А.", 10, 5, "Mercedes Benz", 2006, 22_431.5));
        }};
    }

    @Override
    public String toString() {
        return "Bus{" +
                "driverName='" + driverName + '\'' +
                ", busNumber=" + busNumber +
                ", routeNumber=" + routeNumber +
                ", brand='" + brand + '\'' +
                ", manufacturedYear=" + manufacturedYear +
                ", mileage=%.1f".formatted(mileage) +
                '}';
    }
}
