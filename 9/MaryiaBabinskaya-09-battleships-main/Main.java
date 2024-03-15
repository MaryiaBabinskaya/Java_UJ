//10.19.151.253
package uj.wmii.pwj.zd10;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

public class Main {

    static String path = "Map.txt";
    static Scanner sc = new Scanner(System.in);

    public static void CreateFile(String str) throws IOException {
        Files.deleteIfExists(Path.of(path));
        FileOutputStream out = new FileOutputStream(path);
        out.write(str.getBytes());
        out.close();
    }

    public static void main(String[] args) throws IOException {
        System.out.println("Server or Player? ... ");
        String mode = sc.nextLine();

        GenerateMap generatorMap = new GenerateMap();
        String map = generatorMap.generateMap();
        CreateFile(map);

        if (mode.equals("Player")){
            System.out.println("Port: ... ");
            int port = Integer.parseInt(sc.nextLine());
            System.out.println("Server to connect: ... ");
            String server = sc.nextLine();
            ClientMode client = new ClientMode(server, port, path);
            System.out.println("Connected to server");
            client.letsPlay();
        } else if(mode.equals("Server")) {
            ServerMode server = new ServerMode(path);
            System.out.println("Server started");
            server.letsPlay();
        }
        sc.close();
    }
}