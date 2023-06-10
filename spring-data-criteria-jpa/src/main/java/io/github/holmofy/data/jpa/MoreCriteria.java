package io.github.holmofy.data.jpa;

import jakarta.persistence.metamodel.SingularAttribute;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;

import java.util.Collection;

@UtilityClass
public class MoreCriteria {

    public static <T> Specification<T> alwaysTrue() {
        return (root, query, cb) -> cb.isTrue(cb.literal(Boolean.TRUE));
    }

    public static <T> Specification<T> alwaysFalse() {
        return (root, query, cb) -> cb.isTrue(cb.literal(Boolean.FALSE));
    }

    public static <T, Y> Specification<T> isNull(SingularAttribute<T, Y> attr) {
        return (root, query, cb) -> cb.isNull(root.get(attr));
    }

    public static <T, Y> Specification<T> isNotNull(SingularAttribute<T, Y> attr) {
        return (root, query, cb) -> cb.isNotNull(root.get(attr));
    }

    @Nullable
    public static <T> Specification<T> eq(SingularAttribute<T, ?> attr, Object value) {
        return isNullOrEmpty(value) ? null : (root, query, cb) -> cb.equal(root.get(attr), value);
    }

    @Nullable
    public static <T> Specification<T> notEq(SingularAttribute<T, ?> attr, Object value) {
        return isNullOrEmpty(value) ? null : (root, query, cb) -> cb.notEqual(root.get(attr), value);
    }

    @Nullable
    public static <T, Y extends Comparable<? super Y>> Specification<T> lt(SingularAttribute<T, Y> attr, Y value) {
        return isNullOrEmpty(value) ? null : (root, query, cb) -> cb.lessThan(root.get(attr), value);
    }

    @Nullable
    public static <T, Y extends Comparable<? super Y>> Specification<T> lte(SingularAttribute<T, Y> attr, Y value) {
        return isNullOrEmpty(value) ? null : (root, query, cb) -> cb.lessThanOrEqualTo(root.get(attr), value);
    }

    @Nullable
    public static <T, Y extends Comparable<? super Y>> Specification<T> gt(SingularAttribute<T, Y> attr, Y value) {
        return isNullOrEmpty(value) ? null : (root, query, cb) -> cb.greaterThan(root.get(attr), value);
    }

    @Nullable
    public static <T, Y extends Comparable<? super Y>> Specification<T> gte(SingularAttribute<T, Y> attr, Y value) {
        return isNullOrEmpty(value) ? null : (root, query, cb) -> cb.greaterThanOrEqualTo(root.get(attr), value);
    }

    @Nullable
    public static <T, Y extends Comparable<? super Y>> Specification<T> between(SingularAttribute<T, Y> attr, Y begin, Y end) {
        if (begin == null && end == null) {
            return null;
        }
        if (begin != null && end != null) {
            return (root, query, cb) -> cb.between(root.get(attr), begin, end);
        }
        return begin == null ? lte(attr, end) : gte(attr, begin);
    }

    @Nullable
    public static <T> Specification<T> like(SingularAttribute<T, String> attr, String value) {
        return isNullOrEmpty(value) ? null : (root, query, cb) -> cb.like(root.get(attr), value);
    }

    @Nullable
    public static <T> Specification<T> startWith(SingularAttribute<T, String> attr, String value) {
        return isNullOrEmpty(value) ? null : (root, query, cb) -> cb.like(root.get(attr), escapeLikeClause(value) + "%");
    }

    @Nullable
    public static <T> Specification<T> in(SingularAttribute<T, ?> attr, Collection<?> values) {
        return isNullOrEmpty(values) ? null : (root, query, cb) -> cb.isTrue(root.get(attr).in(values));
    }

    @Nullable
    public static <T> Specification<T> notIn(SingularAttribute<T, ?> attr, Collection<?> values) {
        return isNullOrEmpty(values) ? null : (root, query, cb) -> cb.isTrue(root.get(attr).in(values).not());
    }

    public static boolean isNullOrEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isNullOrEmpty(Object obj) {
        return obj == null || obj instanceof String && ((String) obj).isEmpty();
    }

    private static String escapeLikeClause(CharSequence expression) {
        return expression.toString().replace("%", "\\%").replace("_", "\\_");
    }
}
