package uj.wmii.pwj.collections;

import java.util.Map;

public interface JsonMapper {

    String toJson(Map<String, ?> map);

    static JsonMapper defaultInstance() {
        JsonMapper result = new JsonMap();
        return result;
    }

}
