package uj.wmii.pwj.collections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListMerger {

    public static List<Object> mergeLists(List<?> l1, List<?> l2) {
        List<Object> result = new ArrayList<>();
        if(l1 != null && l2 != null) {
            int minLen = (l1.size() < l2.size()) ? l1.size() : l2.size();
            for (int i = 0; i < minLen; i++) {
                result.add(l1.get(i));
                result.add(l2.get(i));
            }
            result.addAll(l1.subList(minLen, l1.size()));
            result.addAll(l2.subList(minLen, l2.size()));
        }
        return Collections.unmodifiableList(result);
    }
}