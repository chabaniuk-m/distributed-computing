package ua.lab4;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ProgramA {
    public static final Path filePath = Path.of("C:\\Users\\Admin\\source\\IntelliJ IDEA\\distributed-computing\\src\\ua\\lab4\\resources\\file.txt");
    /**
     * number of threads which are currently reading from the file
     */
    public static Integer reading = 0;
    /**
     * is there a writer
     */
    public static Boolean writing = false;
    public static final Object manager = new Object();
    public static final Object writer = new Object();
    public static final Object reader = new Object();

    public static void main(String[] args) throws IOException {
        List<Thread> list = new ArrayList<>(12){{
            add(new Thread(new Adder("Суховецький Максим Миколайович - +380 (68) 597 46 00"), "Add1"));
            add(new Thread(new NameFinder("+48 668 648 824"), "FindName1"));
            add(new Thread(new PhoneFinder("Суховецький Максим Миколайович"), "FindPhone1"));
            add(new Thread(new Remover("Панчук Тетьяна Володимирівна"), "Remove1"));
            add(new Thread(new Adder("Гончарук Наталя Леонідіївна - +380 (98) 297 88 74"), "Add2"));
            add(new Thread(new NameFinder("+380 (68) 597 46 00"), "FindName2"));
            add(new Thread(new PhoneFinder("Чабанюк Ганна Онуфріївна"), "FindPhone2"));
            add(new Thread(new Remover("Суховецький Максим Миколайович"), "Remove2"));
            add(new Thread(new Adder("Потовський Сергій Миколайович - +380 (67) 433 04 83"), "Add3"));
            add(new Thread(new NameFinder("+380 (73) 505 20 09"), "FindName3"));
            add(new Thread(new PhoneFinder("Слюсарев Володимир Олександрович"), "FindPhone3"));
            add(new Thread(new Remover("Миргородський Артем Олегович"), "Remove3"));
        }};
        list.forEach(Thread::start);
    }
}

class PhoneFinder implements Runnable {
    private final Path filePath = ProgramA.filePath;
    private final String name;

    public PhoneFinder(String name) {
        this.name = name;
    }

    static void waitWriterToFinish() {
        while (true) {
            synchronized (ProgramA.writer) {
                    if (ProgramA.writing) {
                        try {
                            ProgramA.writer.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
            }
            synchronized (ProgramA.manager) {
                if (!ProgramA.writing) {
                    ProgramA.reading++;
                    break;
                }
            }
        }
    }

    @Override
    public void run() {
        waitWriterToFinish();
        findUserByName();
        synchronized (ProgramA.reader) {
            ProgramA.reading--;
            if (ProgramA.reading == 0)
                ProgramA.reader.notifyAll();
        }
    }

    private void findUserByName() {
        List<String> lines;
        try {
            lines = Files.readAllLines(filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        var found = false;
        for (var line : lines) {
            var nf = new NamePhone(line);
            if (Objects.equals(name, nf.name)) {
                System.out.printf("Потік %s знайшов номер телефону %s за користувачем %s\n", Thread.currentThread().getName(), nf.phone, nf.name);
                found = true;
                break;
            }
        }
        if (!found) {
            System.out.printf("Потік %s не знайшов користувача %s\n", Thread.currentThread().getName(), name);
        }
    }
}

class NameFinder implements Runnable {
    private final Path filePath = ProgramA.filePath;
    private final String phoneNumber;

    public NameFinder(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public void run() {
        PhoneFinder.waitWriterToFinish();
        findUserByPhoneNumber();
        synchronized (ProgramA.reader) {
            ProgramA.reading--;
            if (ProgramA.reading == 0)
                ProgramA.reader.notifyAll();
        }
    }

    private void findUserByPhoneNumber() {
        List<String> lines;
        try {
            lines = Files.readAllLines(filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        var found = false;
        for (var line : lines) {
            var nf = new NamePhone(line);
            if (Objects.equals(phoneNumber, nf.phone)) {
                System.out.printf("Потік %s знайшов користувача %s за номером телефону %s\n", Thread.currentThread().getName(), nf.name, nf.phone);
                found = true;
                break;
            }
        }
        if (!found) {
            System.out.printf("Потік %s не знайшов номер телефону %s\n", Thread.currentThread().getName(), phoneNumber);
        }
    }
}

class Remover implements Runnable {
    private final Path filePath = ProgramA.filePath;
    private final String name;

    public Remover(String name) {
        this.name = name;
    }

    static void waitReadersAndOtherWritersToFinish() {
        while (true) {
            synchronized (ProgramA.writer) {
                if (ProgramA.writing) {
                    try {
                        ProgramA.writer.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            synchronized (ProgramA.reader) {
                if (ProgramA.reading > 0) {
                    try {
                        ProgramA.reader.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            synchronized (ProgramA.manager) {
                if (!ProgramA.writing && ProgramA.reading == 0) {
                    ProgramA.writing = true;
                    break;
                }
            }
        }
    }

    @Override
    public void run() {
        waitReadersAndOtherWritersToFinish();
        removeUser();
        // синхронізація надлишкова оскільки зараз виконується лише 1 потік
        synchronized (ProgramA.writer) {
            ProgramA.writing = false;
            ProgramA.writer.notifyAll();
        }
    }

    private void removeUser() {
        List<String> lines;
        try {
            lines = Files.readAllLines(filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        for (int i = 0; i < lines.size(); i++) {
            var nf = new NamePhone(lines.get(i));
            if (Objects.equals(nf.name, name)) {
                lines.remove(i);
                System.out.printf("Потік %s видалив користувача користувача %s\n", Thread.currentThread().getName(), name);
                break;
            }
            if (i == lines.size() - 1) {
                System.out.printf("Потік %s не знайшов користувача %s\n", Thread.currentThread().getName(), name);
            }
        }
        try {
            Files.write(filePath, lines);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

class Adder implements Runnable {
    private final Path filePath = ProgramA.filePath;
    private final String toAdd;

    public Adder(String toAdd) {
        this.toAdd = toAdd;
    }

    @Override
    public void run() {
        Remover.waitReadersAndOtherWritersToFinish();
        addLine();
        synchronized (ProgramA.writer) {
            ProgramA.writing = false;
            ProgramA.writer.notifyAll();
        }
    }

    private void addLine() {
        List<String> lines;
        try {
            lines = Files.readAllLines(filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        lines.add(toAdd);
        try {
            Files.write(filePath, lines);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.printf("Потік %s додав запис %s\n", Thread.currentThread().getName(), toAdd);
    }
}

class NamePhone {
    String phone, name;
    NamePhone(String str) {
        var arr = str.split(" - ");
        name = arr[0];
        phone = arr[1];
    }
}