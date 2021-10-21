package ru.netology;


public class Main {
    public static void main(String[] args) {
        var server = new Server(64);
        server.listen(9999);
    }
}