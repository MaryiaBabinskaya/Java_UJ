package uj.wmii.pwj.collections;

import java.util.List;
import java.util.Map;

public class JsonMap implements JsonMapper {

    String str = "";

    Object createString(Object object) {
        String[] split = object.toString().split("\"");
        StringBuilder str1 = new StringBuilder();
        for (int i = 0; i < split.length - 1; i++)
            str1.append(split[i]).append("\\\"");
        object = str1 + split[split.length - 1];
        return object;
    }

    public String createList(List <?> list) {

        Object first;

        if( !list.isEmpty() ) first = list.get(0);
        else return str += "[]";

        str += "[";

        if (first instanceof String) {
            for (int i = 0; i < list.size() - 1; i++)
                str += "\"" + list.get(i) + "\",";
            str += "\"" + list.get(list.size() - 1) + "\"]";
        }

        else if (first instanceof Map <?, ?>) {
            for (int i = 0; i < list.size() - 1; i++)
                str = createMap((Map <?, ?>) list.get(i)) + ",";
            str = createMap((Map <?, ?>) list.get(list.size() - 1)) + "]";
        }

        else {
            for (int i = 0; i < list.size() - 1; i++)
                str += list.get(i) + ",";
            str += list.get(list.size() - 1) + "]";
        }

        return str;
    }

    public String createMap(Map <?, ?> map) {

        Object object;
        int counter = 0;
        str += "{";

        for (Object key: map.keySet()) {

            str += "\"" + key + "\":"; // {"text":
            object = map.get(key);
            counter++;

            if (object instanceof String) { //String -> json string
                object = createString(object);
                if(counter == map.size()) str += "\"" + object + "\"" + "}\n";
                else str += "\"" + object + "\",";
            }

            else if (object instanceof Map <?, ?>) { //Map -> zagnieżdżony object
                str = createMap((Map <?, ?>) object);
                if(counter == map.size()) str += "}\n";
                else str += ",";
            }

            else if (object instanceof List <?>) { //List -> tablica
                str = createList((List<?>) object);
                if(counter == map.size()) str += "}\n";
                else str += ",";
            }

            else { //typy -> typy json
                if (counter == map.size()) str += object + "}\n";
                else str += object + ",";
            }
        }
        return str;
    }

    @Override
    public String toJson(Map<String, ?> map) {
        if (map == null || map.isEmpty()) return "{}";
        str = createMap(map);
        return str;
    }

}