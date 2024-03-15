package uj.wmii.pwj.zd10;

public class Coordinate {
    private Integer x;
    private Integer y;

    public int getX(){return x;}
    public int getY(){return y;}

    public Coordinate(String stringCoordination){
        x =  stringCoordination.charAt(0) - 'A';
        y =  Integer.parseInt(stringCoordination.trim().substring(1));
    }

    public Coordinate(int x1, int y1){
        x =  x1;
        y =  y1;
    }

    public String toString() {
        return "" + (char) (x + 'A') + y.toString();
    }
}