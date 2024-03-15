package uj.wmii.pwj.zd10;

import java.io.IOException;
import java.net.Socket;

public class ClientMode {
    Play game;

    public ClientMode(String ip, int port, String path) {
        this.game = new Play(path);
        try {
            game.socket = new Socket(ip, port);
            game.updatePlayerDataStreams();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void letsPlay(){
        while (game.getStageOfGame() == StageOfGame.TRWA){
            game.prepareAndSendMove();
            game.receiveMove();
        }
        if (game.getStageOfGame() == StageOfGame.PRZEGRANA) System.out.println("PRZEGRANA");
        else System.out.println("WYGRANA");
        game.printMaps();
        game.closeConnection();
    }
}