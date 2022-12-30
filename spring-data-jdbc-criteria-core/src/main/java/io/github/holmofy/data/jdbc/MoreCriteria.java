package io.github.holmofy.data.jdbc;

import lombok.experimental.UtilityClass;
import org.springframework.data.relational.core.query.Criteria;

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
            return null;
        }
        if (begin != null && end != null) {
            return Criteria.where(column).between(begin, end);
        }
        return begin == null ? lte(column, end) : gte(column, begin);
    }

    public static <T> Criteria like(String column, T obj) {
        return obj == null ? Criteria.empty() : Criteria.where(column).like(obj);
    }

}
