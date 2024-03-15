package uj.wmii.pwj.zd10;

enum StageOfGame {
    TRWA, PRZEGRANA, WYGRANA
}

public class BattleShipsMap {

    int numberRows;
    int numberColumns;
    char[][] stringMap;

    public BattleShipsMap(int _numberRows, int _numberColumns, String preMap){
        numberRows = _numberRows;
        numberColumns = _numberColumns;
        stringMap = new char[numberRows][numberColumns];
        for(int row = 0; row <numberRows; row++){
            for(int column = 0; column < numberColumns; column++)
                stringMap[row][column] = preMap.charAt(row * numberColumns + column);
        }
    }

    public BattleShipsMap(int _numberRows, int _numberColumns){
        numberRows = _numberRows;
        numberColumns = _numberColumns;
        stringMap = new char[numberRows][numberColumns];
        for(int row = 0; row < numberRows; row++){
            for(int column = 0; column < numberColumns; column++){
                stringMap[row][column] = '?';
            }
        }
    }

    public void show(){
        System.out.print(" ");
        for(int column = 0; column < numberColumns; column++)
            System.out.print(" " + column);
        System.out.println();
        for(int row = 0; row < numberRows; row++){
            System.out.print((char)('A' + row) + " ");
            for(int column = 0; column < numberColumns; column++){
                System.out.print(stringMap[row][column] + " ");
            }
            System.out.print("\n");
        }
    }

    public void change(int row, int column, char character){
        stringMap[row][column] = character;
    }

    public boolean isCharacterOnMap(char character){
        for(int row = 0; row < numberRows; row++)
            for(int column = 0; column < numberColumns; column++)
                if(stringMap[row][column] == character)
                    return true;
        return false;
    }

    public boolean isOnMap(int row, int column){
        return row >= 0 && row < numberRows && column >= 0 && column < numberColumns;
    }

    public boolean isCharacter(int row, int column, char character){
        if(isOnMap(row,column))
            return stringMap[row][column] == character;
        return false;
    }

    boolean isCharacterAround(int row, int column, char character){
        for(int a = -1; a <= 1; a++)
            for(int b = -1; b <= 1; b++)
                if(isOnMap(row + a, column + b))
                    if(!(a == 0 && b == 0) && stringMap[row + a][column + b] == character)
                        return true;
        return false;
    }

    Coordinate returnCoordinateOfCharacterAround(int row, int column){
        int A = 0;
        int B = 0;
        for(int a = -1; a <= 1; a++)
            for(int b = -1; b <= 1; b++)
                if(isOnMap(row + a, column + b))
                    if(!(a == 0 && b ==0 ) && stringMap[row + a][column + b]== '?'){
                        A = a;
                        B = b;
                    }
        return new Coordinate(row + A,column + B);
    }

    void discover(int row, int column, char character){
        for(int a = -1; a <=1 ; a++)
            for(int b = -1; b <= 1; b++)
                if(isOnMap(row + a, column + b))
                    if(stringMap[row + a][column + b] == character)
                        stringMap[row + a][column + b]='.';
        for(int a = -1; a <= 1; a++)
            for(int b = -1; b <= 1; b++)
                if(isOnMap(row + a, column + b))
                    if(stringMap[row + a][column + b]=='@' && isCharacterAround(row + a,column + b,character))
                        discover(row + a,column + b,character);
    }
}