package ua.lab4;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ProgramB {
    static final int N = 4;
    static final Path filePath = Path.of("C:\\Users\\Admin\\source\\IntelliJ IDEA\\distributed-computing\\src\\ua\\lab4\\resources\\output.txt");
    static final Garden garden = new Garden();
    public static void main(String[] args) {
        List<Thread> list = new ArrayList<>(4){{
            add(new Thread(new Console()));
            add(new Thread(new Gardener()));
            add(new Thread(new File()));
            add(new Thread(new Nature()));
        }};
        list.forEach(Thread::start);
    }
}

class Garden {
    ReentrantReadWriteLock.ReadLock readLock;
    ReentrantReadWriteLock.WriteLock writeLock;
    {
        var lock = new ReentrantReadWriteLock();
        readLock = lock.readLock();
        writeLock = lock.writeLock();
    }
    int[][] array = {
            {1, 0, 1, 1, 0},
            {0, 0, 1, 1, 1},
            {1, 1, 0, 1, 1},
            {0, 1, 0, 0, 0},
            {1, 1, 0, 0, 1}
    };

    @Override
    public String toString() {
        List<String> lines = new ArrayList<>(6);
        for (var row : array) {
            List<String> chars = new ArrayList<>(6);
            for (var c : row) {
                if (c == 0) chars.add("🥀");
                else chars.add("🌹");
            }
            lines.add(String.join(" ", chars));
        }
        lines.add("- - - - - - - -\n");

        return String.join("\n", lines);
    }
}

class Gardener implements Runnable {
    @Override
    public void run() {
        for (int i = 0; i < ProgramB.N; i++) {
            ProgramB.garden.writeLock.lock();
            for (int j = 0; j < ProgramB.garden.array.length; j++) {
                Arrays.fill(ProgramB.garden.array[j], 1);
            }
            ProgramB.garden.writeLock.unlock();
        }
    }
}

class Nature implements Runnable {
    Random random = new Random(System.currentTimeMillis());
    @Override
    public void run() {
        for (int i = 0; i < ProgramB.N; i++) {
            ProgramB.garden.writeLock.lock();
            for (int j = 0; j < ProgramB.garden.array.length; j++) {
                for (int k = 0; k < ProgramB.garden.array[j].length; k++) {
                    ProgramB.garden.array[j][k] = random.nextInt() % 2;
                }
            }
            ProgramB.garden.writeLock.unlock();
        }
    }
}

class Console implements Runnable {
    @Override
    public void run() {
        for (int i = 0; i < ProgramB.N; i++) {
            ProgramB.garden.readLock.lock();
            System.out.print(ProgramB.garden);
            ProgramB.garden.readLock.unlock();
        }
    }
}

class File implements Runnable {
    @Override
    public void run() {
        for (int i = 0; i < ProgramB.N; i++) {
            ProgramB.garden.readLock.lock();
            try {
                Files.write(
                        ProgramB.filePath,
                        ProgramB.garden.toString().getBytes(),
                        StandardOpenOption.APPEND);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                ProgramB.garden.readLock.unlock();
            }
        }
    }
}
