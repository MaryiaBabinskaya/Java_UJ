package uj.wmii.pwj.anns;

public class MyBeautifulTestSuite {
    @MyTest
    public void testSomething() {
        System.out.println("I'm testing something!");
    }

    @MyTest
    public void imFailue() {
        System.out.println("I AM EVIL.");
        throw new NullPointerException();
    }

    @MyTest(strExpected = {"Maryia"})
    public void voidTypeMethod() {
        System.out.println("I AM EVIL.");
        throw new NullPointerException();
    }

    @MyTest(strParams = {"Maryia", "Babinskaya", "19"}, intExpected = {1, 2, 3})
    public int incorrectNumberOfWords(String str) {
        if (str == null || str.isEmpty()) return 0;
        int count = 1;
        boolean isNewWord = false;
        for (int i = 0; i < str.length(); i++) {
            char currentChar = str.charAt(i);
            if (currentChar == ' ') {
                isNewWord = true;
            } else {
                if (isNewWord) {
                    count++;
                    isNewWord = false;
                }
            }
        }
        return count;
    }

    @MyTest(strParams = {"MARYIA", "BaBiNsKaYa", "19 y/o"}, strExpected = {"maryia", "babinskaya", "19 y/o"})
    public String toLowerCase(String str) {
        if (str == null) return null;
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char currentChar = str.charAt(i);
            if (Character.isUpperCase(currentChar)) result.append(Character.toLowerCase(currentChar));
            else result.append(currentChar);
        }
        return result.toString();
    }

    @MyTest(strParams = {"MARYIA", "BaBiNsKaYa", "19"}, strExpected = {"maryia", "babinskaya", "19"})
    public String incorrectToLowerCase(String str){
        if (str == null) return null;
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char currentChar = str.charAt(i);
            if (currentChar >= 'A') result.append((char) (currentChar + 32));
            else result.append(currentChar);
        }
        return result.toString();
    }

    @MyTest(strParams = {"MARYIA", "babinskaya", "SprinG"}, strExpected = {"MARYIA", "BABINSKAYA", "SPRING"})
    public String toUpperCase(String str) {
        if (str == null) return null;
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char currentChar = str.charAt(i);
            if (currentChar >= 'a' && currentChar <= 'z') result.append((char) (currentChar - 32));
            else result.append(currentChar);
        }
        return result.toString();
    }

    @MyTest(intParams = {1,2,3,4}, intExpected = {2,4,6,8})
    public Integer correctMathCount(int i) {
        return i * 2;
    }

    @MyTest(intParams = {1,2,3,4}, intExpected = {2,4,6,8})
    public Integer incorrectMathCount(int i) {
        return i * 3;
    }
}