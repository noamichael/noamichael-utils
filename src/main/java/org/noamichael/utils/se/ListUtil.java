package org.noamichael.utils.se;

import java.util.List;

/**
 *
 * @author michael
 */
public class ListUtil {

    public <T> T getFirst(List<T> list) {
        if (isNullOrEmpty(list)) {
            return null;
        } 
        return list.get(0);
    }
    
    public <T>  T getMiddle(List<T> list){
        int mid = (list.size()-1) >>> 1;
        return list.get(mid);
    }

    public <T> T getLast(List<T> list) {
        if (isNullOrEmpty(list)) {
            return null;
        }
        return list.get(list.size() - 1);
    }

    public boolean isNullOrEmpty(List<?> list) {
        return list == null || list.isEmpty();
    }
}
