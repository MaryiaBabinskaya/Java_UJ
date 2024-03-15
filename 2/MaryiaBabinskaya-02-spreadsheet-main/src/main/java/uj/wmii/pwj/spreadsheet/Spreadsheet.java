package uj.wmii.pwj.spreadsheet;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Spreadsheet {

    enum Formulas {
        ADD {
            public int apply(int a, int b) {
                return a + b;
            }
        },
        SUB {
            public int apply(int a, int b) {
                return a - b;
            }
        },
        MUL {
            public int apply(int a, int b) {
                return a * b;
            }
        },
        DIV {
            public int apply(int a, int b) {
                return a / b;
            }
        },
        MOD {
            public int apply(int a, int b) {
                return a % b;
            }
        };

        public abstract int apply(int a, int b);
    }

    public String findNumber(String stringFromInput, String[][] input, String[][] result) {
        String regex = "^\\$([A-Z])(\\d+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(stringFromInput);
        do {
            if (matcher.matches()) {
                String letter = matcher.group(1);
                String number = matcher.group(2);
                int row = Integer.parseInt(number) - 1;
                int col = letter.charAt(0) - 'A';
                stringFromInput = input[row][col];
                if (stringFromInput.startsWith("=")) {
                    stringFromInput = solveFormulas(stringFromInput, input, result);
                }
                matcher = pattern.matcher(stringFromInput);
            }
        } while (stringFromInput.charAt(0) != '-' && !Character.isDigit(stringFromInput.charAt(0)));
        return stringFromInput;
    }

    public String solveFormulas(String stringFromInput, String[][] input, String[][] result) {
        String regex1 = "=([A-Z]{3})\\((\\d+|\\$[A-Z]\\d+),(\\d+|\\$[A-Z]\\d+)\\)$";
        Pattern pattern1 = Pattern.compile(regex1);
        Matcher matcher = pattern1.matcher(stringFromInput);
        if (matcher.matches()) {
            String function = matcher.group(1);
            String number1 = matcher.group(2);
            String number2 = matcher.group(3);
            Formulas formula = Formulas.valueOf(function);
            if (number1.startsWith("$")) number1 = findNumber(number1, input, result);
            if (number2.startsWith("$")) number2 = findNumber(number2, input, result);
            stringFromInput = String.valueOf(formula.apply(Integer.parseInt(number1), Integer.parseInt(number2)));
        }
        return stringFromInput;
    }

    public String[][] calculate(String[][] input) {

        String[][] result = new String[input.length][input[0].length];

        for (int i = 0; i < input.length; i++) {
            for (int j = 0; j < input[0].length; j++) {

                if (input[i][j].startsWith("$")) {
                    result[i][j] = findNumber(input[i][j], input, result);
                } else if (input[i][j].startsWith("=")) {
                    result[i][j] = solveFormulas(input[i][j], input, result);
                } else {
                    result[i][j] = input[i][j];
                }

            }
        }
        return result;
    }
}
