//10.19.151.253
package uj.wmii.pwj.zd10;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

enum Commands {
    start, pudło, trafiony, trafiony_zatopiony, ostatni_zatopiony, blad_komunikacji
}

public class Play{

    private StageOfGame stageOfGame;
    private Commands answer;
    protected Socket socket;
    protected DataInputStream in;
    protected DataOutputStream out;
    Coordinate lastPositiveShot;
    Coordinate lastSendCord;
    boolean shootingMode;
    BattleShipsMap myMap;
    BattleShipsMap enemyMap;
    String move;

    private final Scanner scan = new Scanner(System.in);

    public void printMaps(){
        myMap.show();
        System.out.println();
        enemyMap.show();
    }

    public void isProperMessage(String premessage) throws IOException{
        if(premessage.contains(";")){
            String [] message = premessage.split(";");
            if(translateStringToResult(message[0]) == Commands.blad_komunikacji)
                throw new IOException();
            Coordinate cord = new Coordinate(message[1]);
            if(cord.getX() >= enemyMap.numberRows || cord.getY() >= enemyMap.numberColumns
                    || cord.getX() < 0 || cord.getY() < 0)
                throw new IOException();
        }
    }

    public String chooseOptimalPlaceToShoot(){
        if(shootingMode && enemyMap.isCharacterAround(lastPositiveShot.getX(),lastPositiveShot.getY(),'?'))
            return enemyMap.returnCoordinateOfCharacterAround(lastPositiveShot.getX(),lastPositiveShot.getY()).toString();

        for(int row = 0; row < enemyMap.numberRows; row++)
            for(int column = row % 2; column < enemyMap.numberColumns; column += 2)
                if(enemyMap.isCharacter(row,column,'?'))
                    return new Coordinate(row,column).toString();

        Coordinate result = new Coordinate(0,0);

        for(int row = 0; row < enemyMap.numberRows; row++)
            for(int column = 0; column < enemyMap.numberColumns; column++)
                if(enemyMap.isCharacter(row,column,'?')){
                    result = new Coordinate(row,column);
                    row = enemyMap.numberRows;
                    column = enemyMap.numberColumns;
                }
        return result.toString();
    }

    public Play(String path) {
        stageOfGame = StageOfGame.TRWA;
        answer = Commands.start;
        try {
            File file = new File(path);
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String mapInOneLine = reader.readLine();
            myMap = new BattleShipsMap(10,10,mapInOneLine);
            enemyMap = new BattleShipsMap(10,10);
            reader.readLine();
            reader.close();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    Commands resultShot(String stringCord) {
        Coordinate cord = new Coordinate(stringCord);
        if(!myMap.isCharacterAround(cord.getX(), cord.getY(), '#')
                && myMap.isCharacter(cord.getX(),cord.getY(),'#')){
            myMap.change(cord.getX(),cord.getY(),'@');
            if(!myMap.isCharacterOnMap('#'))
                return Commands.ostatni_zatopiony;
            return Commands.trafiony_zatopiony;
        }
        else if(myMap.isCharacter(cord.getX(),cord.getY(),'#')
                || myMap.isCharacter(cord.getX(),cord.getY(),'@')){
            return Commands.trafiony;
        }
        return Commands.pudło;
    }

    String translateResultToString(Commands result){
        String str = result.toString();
        str = str.replace('_',' ');
        return str;
    }

    Commands translateStringToResult(String str){
        str = str.replace("\n","");
        return switch(str){
            case "pudło" -> Commands.pudło;
            case "trafiony" -> Commands.trafiony;
            case "trafiony zatopiony" -> Commands.trafiony_zatopiony;
            case "start" ->Commands.start;
            case "ostatni zatopiony" -> Commands.ostatni_zatopiony;
            default -> Commands.blad_komunikacji;
        };
    }

    public void updateMap(Commands result, Coordinate cord, BattleShipsMap map){
        if(result == Commands.pudło)
            map.change(cord.getX(), cord.getY(),'~');
        else if(result == Commands.trafiony ||
                result == Commands.trafiony_zatopiony ||
                result == Commands.ostatni_zatopiony)
            map.change(cord.getX(), cord.getY(),'@');
    }

    public void updatePlayerDataStreams() {
        try {
            this.out = new DataOutputStream(this.socket.getOutputStream());
            this.in = new DataInputStream(this.socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection() {
        try {
            this.socket.close();
            this.in.close();
            this.out.close();
            this.scan.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    StageOfGame getStageOfGame(){
        return stageOfGame;
    }

    public void sendMove(){
        try {
            out.writeUTF(move);
        }
        catch (IOException e) {e.printStackTrace();}
    }

    public void prepareAndSendMove() {
        printMaps();
        if(!myMap.isCharacterOnMap('#'))
            stageOfGame = StageOfGame.PRZEGRANA;

        System.out.println("Stan gry : " + stageOfGame.toString());

        if(stageOfGame == StageOfGame.PRZEGRANA){
            move = translateResultToString(Commands.ostatni_zatopiony) + "\n";
            System.out.println("Moj stan gry to PRZEGRANA. Wysyłam wiadomosc: " + move);
            sendMove();
        }

        if(stageOfGame == StageOfGame.TRWA){
            System.out.println();
            System.out.print("Jaki chcesz wykonac ruch? ");
            move = chooseOptimalPlaceToShoot();
            lastSendCord = new Coordinate(move);
            System.out.println(move);
            move = translateResultToString(answer)+";"+move+"\n";
            System.out.println("Wysylam : " + move);
            sendMove();
        }
    }

    public void receiveMove(){
        System.out.println("Czekam na ruch ...");
        String premessage = "";
        for(int nr = 0; nr < 3; nr++){
            try {
                premessage = in.readUTF();
                isProperMessage(premessage);
                break;
            } catch (IOException e) {
                try{socket.setSoTimeout(1000);
                    sendMove();
                }
                catch (SocketException ignored){}
            }
        }
        System.out.println("Otrzymana wiadomosc : " + premessage);
        if(premessage.contains(";")){
            String[] message = premessage.split(";");
            Commands result = translateStringToResult(message[0]);
            updateMap(result,lastSendCord,enemyMap);
            if(result == Commands.trafiony){
                shootingMode = true;
                lastPositiveShot = lastSendCord;
            }
            if(result == Commands.trafiony_zatopiony){
                enemyMap.discover(lastSendCord.getX(), lastSendCord.getY(), '?');
                shootingMode = false;
            }
            Commands result_of_shot = resultShot(message[1]);
            answer = result_of_shot;
            Coordinate cord = new Coordinate(message[1]);
            updateMap(answer,cord,myMap);
        }
        if(translateStringToResult(premessage) == Commands.ostatni_zatopiony){
            updateMap(Commands.ostatni_zatopiony,lastSendCord,enemyMap);
            stageOfGame = StageOfGame.WYGRANA;
        }
    }
}