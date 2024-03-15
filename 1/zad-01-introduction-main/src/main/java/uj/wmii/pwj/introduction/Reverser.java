package uj.wmii.pwj.introduction;

public class Reverser {

    public String reverse(String input) {
        if (input == null) return null;
        StringBuilder builder = new StringBuilder(input.trim());
        builder.reverse();
        return builder.toString();
    }

    public String reverseWords(String input) {
        if (input == null) return null;
        String[] words = input.trim().split(" ");
        StringBuilder builder = new StringBuilder();
        for (int i = words.length - 1; i >= 0; i--)
            builder.append(words[i]).append(" ");
        return builder.toString().trim();
    }
}
