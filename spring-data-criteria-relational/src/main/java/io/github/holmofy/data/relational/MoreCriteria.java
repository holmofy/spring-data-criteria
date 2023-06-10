package io.github.holmofy.data.relational;

import lombok.experimental.UtilityClass;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Collection;

@UtilityClass
public class MoreCriteria {

    public static Criteria isNull(String column) {
        return Criteria.where(column).isNull();
    }

    public static Criteria isNotNull(String column) {
        return Criteria.where(column).isNotNull();
    }

    public static <T> Criteria eq(String column, T obj) {
        return obj == null ? Criteria.empty() : Criteria.where(column).is(obj);
    }

    public static <T> Criteria lt(String column, T obj) {
        return obj == null ? Criteria.empty() : Criteria.where(column).lessThan(obj);
    }

    public static <T> Criteria lte(String column, T obj) {
        return obj == null ? Criteria.empty() : Criteria.where(column).lessThanOrEquals(obj);
    }

    public static <T> Criteria gt(String column, T obj) {
        return obj == null ? Criteria.empty() : Criteria.where(column).greaterThan(obj);
    }

    public static <T> Criteria gte(String column, T obj) {
        return obj == null ? Criteria.empty() : Criteria.where(column).greaterThanOrEquals(obj);
    }

    public static <T> Criteria between(String column, T begin, T end) {
        if (begin == null && end == null) {
            return Criteria.empty();
        }
        if (begin != null && end != null) {
            return Criteria.where(column).between(begin, end);
        }
        return begin == null ? lte(column, end) : gte(column, begin);
    }

    public static <T extends CharSequence> Criteria like(String column, T obj) {
        return obj == null || obj.length() == 0 ? Criteria.empty() : Criteria.where(column).like(obj);
    }

    public static <T extends CharSequence> Criteria startWith(String column, T obj) {
        return obj == null || obj.length() == 0 ? Criteria.empty() : Criteria.where(column).like(escapeLikeClause(obj) + "%");
    }

    @SafeVarargs
    public static <T> Criteria in(String column, T... objs) {
        return objs == null ? Criteria.empty() : Criteria.where(column).in(Arrays.asList(objs));
    }

    @SafeVarargs
    public static <T> Criteria notIn(String column, T... objs) {
        return objs == null ? Criteria.empty() : Criteria.where(column).notIn(Arrays.asList(objs));
    }

    public static <T> Criteria in(String column, Collection<T> collection) {
        return CollectionUtils.isEmpty(collection) ? Criteria.empty() : Criteria.where(column).in(collection);
    }

    public static <T> Criteria notIn(String column, Collection<T> collection) {
        return CollectionUtils.isEmpty(collection) ? Criteria.empty() : Criteria.where(column).notIn(collection);
    }

    private static String escapeLikeClause(CharSequence expression) {
        return expression.toString().replace("%", "\\%").replace("_", "\\_");
    }

}
