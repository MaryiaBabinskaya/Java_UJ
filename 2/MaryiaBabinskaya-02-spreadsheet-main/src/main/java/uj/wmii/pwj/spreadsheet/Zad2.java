package uj.wmii.pwj.spreadsheet;

public class Zad2 {
    int number = 28;

    static class Static {
        public void print() {
            //System.out.println(outer); error as it has no access to variable
        }
    }

    class NoStatic {
        public void print() {
            System.out.println(number); //it's ok, it has access to variable
        }
    }
}
