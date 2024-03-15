package uj.wmii.pwj.introduction;

import java.util.HashMap;

public class Banner {

    public String[] toBanner(String input) {
        int FONT_SIZE = 7;
        if(input == null) {
            return new String[0];
        }

        String[] result = new String[7];
        for (int i = 0; i < 7; i++) {
            result[i] = "";
        }

        HashMap<String, String[]> map = new HashMap<>();
        map.put("A", new String[]{"   #    ", "  # #   ", " #   #  ", "#     # ", "####### ", "#     # ", "#     # "});
        map.put("B", new String[]{"######  ", "#     # ", "#     # ", "######  ", "#     # ", "#     # ", "######  "});
        map.put("C", new String[]{" #####  ", "#     # ", "#       ", "#       ", "#       ", "#     # ", " #####  "});
        map.put("D", new String[]{"######  ", "#     # ", "#     # ", "#     # ", "#     # ", "#     # ", "######  "});
        map.put("E", new String[]{"####### ", "#       ", "#       ", "#####   ", "#       ", "#       ", "####### "});
        map.put("F", new String[]{"####### ", "#       ", "#       ", "#####   ", "#       ", "#       ", "#       "});
        map.put("G", new String[]{" #####  ", "#     # ", "#       ", "#  #### ", "#     # ", "#     # ", " #####  "});
        map.put("H", new String[]{"#     # ", "#     # ", "#     # ", "####### ", "#     # ", "#     # ", "#     # "});
        map.put("I", new String[]{"### ", " # ", " #  ", " #  ", " #  ", " #  ", "### "});
        map.put("J", new String[]{"      # ", "       # ", "      # ", "      # ", "#     # ", "#     # ", " #####  "});
        map.put("K", new String[]{"#    # ", "#   #  ", "#  #   ", "###    ", "#  #   ", "#   #  ", "#    # "});
        map.put("L", new String[]{"#       ", "#       ", "#       ", "#       ", "#       ", "#       ", "####### "});
        map.put("M", new String[]{"#     # ", "##   ## ", "# # # # ", "#  #  # ", "#     # ", "#     # ", "#     # "});
        map.put("N", new String[]{"#     # ", "##    # ", "# #   # ", "#  #  # ", "#   # # ", "#    ## ", "#     # "});
        map.put("O", new String[]{"####### ", "#     # ", "#     # ", "#     # ", "#     # ", "#     # ", "####### "});
        map.put("P", new String[]{"######  ", "#     # ", "#     # ", "######  ", "#       ", "#       ", "#       "});
        map.put("Q", new String[]{" #####  ", "#     # ", "#     # ", "#     # ", "#   # # ", "#    #  ", " #### # "});
        map.put("R", new String[]{"######  ", "#     # ", "#     # ", "######  ", "#   #   ", "#    #  ", "#     # "});
        map.put("S", new String[]{" #####  ", "#     # ", "#       ", " #####  ", "      # ", "#     # ", " #####  "});
        map.put("T", new String[]{"####### ", "   #    ", "   #    ", "   #    ", "   #    ", "   #    ", "   #    "});
        map.put("U", new String[]{"#     # ", "#     # ", "#     # ", "#     # ", "#     # ", "#     # ", " #####  "});
        map.put("V", new String[]{"#     # ", "#     # ", "#     # ", "#     # ", " #   #  ", "  # #   ", "   #    "});
        map.put("W", new String[]{"#     # ", "#  #  # ", "#  #  # ", "#  #  # ", "#  #  # ", "#  #  # ", " ## ##  "});
        map.put("X", new String[]{"#     # ", " #   #  ", "  # #   ", "   #    ", "  # #   ", " #   #  ", "#     # "});
        map.put("Y", new String[]{"#     # ", " #   #  ", "  # #   ", "   #    ", "   #    ", "   #    ", "   #    "});
        map.put("Z", new String[]{"####### ", "     # ", "    #   ", "   #    ", "  #     ", " #      ", "####### "});
        map.put(" ", new String[]{"   ", "   ", "   ", "   ", "   ", "   ", "   "});

        input = input.toUpperCase();

        for(int i = 0; i < input.length(); i++){
            char letter = input.charAt(i);
            String [] letter2 = map.get(Character.toString(letter));
            for(int j = 0; j < FONT_SIZE; j++) {
                result[j] += letter2[j];
            }
        }
        return result;
    }
}
