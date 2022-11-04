package ua.lab8.model;

public class Folder {
    public String name;         // Назва папки

    public Folder(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return String.format("Folder{ name=\"%s\" }", name);
    }
}
