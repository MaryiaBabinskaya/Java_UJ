package uj.wmii.pwj.collections;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Battleship implements BattleshipGenerator {

    int [][] board = new int [10][10];
    Random random = new Random();
    int row, col;
    int shiftRow, shiftCol;

    public void generateStartPoint(){
        List<int[]> zeroIndices = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (board[i][j] == 0) {
                    zeroIndices.add(new int[]{i, j});
                }
            }
        }
        int[] randomIndex = zeroIndices.get(random.nextInt(zeroIndices.size()));
        row = randomIndex[0];
        col = randomIndex[1];
    }

    public void generateRotation(){
        int upDown_leftRight = random.nextInt(2);
        shiftRow = 0;
        shiftCol = 0;

        if (upDown_leftRight == 0) shiftRow = random.nextInt(3) - 1;
        else shiftCol = random.nextInt(3) - 1;
    }

    public void generateShip4(){
        row = random.nextInt(10);
        col = random.nextInt(10);
        board[row][col] = 1;
        int counter = 3;
        while(counter > 0) {
            generateRotation();
            if (checkBorder(board, row + shiftRow, col + shiftCol) &&
                              board[row + shiftRow][col + shiftCol] == 0) {
                row = row + shiftRow;
                col = col + shiftCol;
                board[row][col] = 1;
                counter--;
            }
        }
    }

    public void generateShip3(){
        int counter = 3;
        int rotation = 0;
        while(counter == 3){
            //1 square
            generateStartPoint();
            int i = row, j = col;
            board[i][j] = 1;
            counter = 2;
            while(counter == 2){
                generateRotation();
                rotation++;
                int second_i, second_j;
                //2 square
                if (checkBorder(board, i + shiftRow, j + shiftCol) &&
                    board[i + shiftRow][j + shiftCol] == 0) {
                    second_i = i + shiftRow;
                    second_j = j + shiftCol;
                    board[second_i][second_j] = 1;
                    counter = 1;
                    rotation = 0;
                    while(counter == 1){
                        generateRotation();
                        rotation++;
                        //3 square
                        if (checkBorder(board, second_i + shiftRow, second_j + shiftCol) &&
                                board[second_i + shiftRow][second_j + shiftCol] == 0) {
                            row = second_i + shiftRow;
                            col = second_j + shiftCol;
                            board[row][col] = 1;
                            counter = 0;
                        }
                        else if(rotation >= 4){
                            board[second_i][second_j] = 0;
                            counter = 2;
                            rotation = 0;
                            break;
                        }
                    }
                }
                else if(rotation >= 4){
                    board[i][j] = 0;
                    counter = 3;
                    rotation = 0;
                    break;
                }
            }
        }
    }

    public void generateShip2(){
        generateStartPoint();
        int counter = 1;
        int i = row, j = col;
        board[i][j] = 1;
        int rotation = 0;
        while(counter == 1) {
            rotation++;
            generateRotation();
            if (checkBorder(board, i + shiftRow, j + shiftCol) &&
                    board[i + shiftRow][j + shiftCol] == 0) {
                row = i + shiftRow;
                col = j + shiftCol;
                board[i][j] = 1;
                board[row][col] = 1;
                counter = 0;
            }
            else if(rotation >= 4){
                board[row][col] = 0;
                board[i][j] = 0;
                generateStartPoint();
                i = row;
                j = col;
                rotation = 0;
                board[i][j] = 1;
            }
        }
    }

    public void generateShip1(){
        generateStartPoint();
        board[row][col] = 1;
    }

    public void createWall(){
        int[] shiftRow = {-1, -1, -1, 0, 0, 1, 1, 1};
        int[] shiftCol = {-1, 0, 1, -1, 1, -1, 0, 1};
        for(int i = 0; i < 10; i++) {
            for(int j = 0; j < 10; j++) {
                if(board[i][j] == 1){
                    for (int q = 0; q < 8; q++) {
                        int newRow = i + shiftRow[q];
                        int newCol = j + shiftCol[q];
                        if (checkBorder(board, newRow, newCol) && board[newRow][newCol] == 0) {
                            board[newRow][newCol] = 2;
                        }
                    }
                }
            }
        }
    }

    static boolean checkBorder(int[][] array, int row, int col) {
        return row >= 0 && row < array.length && col >= 0 && col < array[0].length;
    }

    public String generateMap() {
        String str = "";

        generateShip4();
        createWall();

        for(int i = 0; i < 2; i++){
            generateShip3();
            createWall();
        }

        for(int i = 0; i < 3; i++){
            generateShip2();
            createWall();
        }

        for(int i = 0; i < 4; i++){
            generateShip1();
            if(i != 3) createWall();
        }

        for(int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if(board[i][j] == 1) str += "#";
                else str += ".";
            }
        }
        return str;
    }
}