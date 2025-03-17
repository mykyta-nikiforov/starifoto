package ua.in.photomap.common.rest.toolkit.util;

import java.util.Collection;

public class DiffUtils {

    public static <T extends Comparable<T>> boolean areDifferent(T original, T request) {
        return original != null && request == null
                || original == null && request != null
                || original != null && original.compareTo(request) != 0;
    }

    public static <T> boolean areDifferent(Collection<T> original, Collection<T> request) {
        return request != null && !request.equals(original);
    }
}