package uj.wmii.pwj.zd10;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;

public class ServerMode {

    private ServerSocket server;
    Play game;

    public ServerMode(String fileName) {
        try {
            game = new Play(fileName);
            InetAddress ip = InetAddress.getLocalHost();
            this.server = new ServerSocket(0, 1, ip);
            System.out.print("Serwer: " + server.getLocalSocketAddress());
            game.socket = server.accept();
            game.updatePlayerDataStreams();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void letsPlay(){
        while (game.getStageOfGame() == StageOfGame.TRWA){
            game.receiveMove();
            game.prepareAndSendMove();
        }
        if (game.getStageOfGame() == StageOfGame.PRZEGRANA) System.out.println("PRZEGRANA");
        else System.out.println("WYGRANA");
        game.printMaps();
        try {
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        game.closeConnection();
    }
}