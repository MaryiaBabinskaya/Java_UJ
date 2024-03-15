package uj.wmii.pwj.collections;

public interface BattleshipGenerator {

    String generateMap();

    static BattleshipGenerator defaultInstance() {
        Battleship result = new Battleship();
        return result;
    }

}
