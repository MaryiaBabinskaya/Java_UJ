package uj.wmii.pwj.spreadsheet;

    class A {
        public void print() {
            System.out.println("Class A");
        }
    }

    class B extends A {
        public void print() {
            System.out.println("Class B");
        }
    }

    class C extends A {
        public void print() {
            System.out.println("Class C");
        }
    }

    class D extends B {
        // Klasa D dziedziczy z B, która dziedziczy z A
        // Dziedziczy również po C, która dziedziczy z A
    }

    public class Main {
        public static void main(String[] args) {
            D obj = new D();
            obj.print(); // Wyświetli: "Class B"
        }
}
