package webapp.framework.dao;

import java.io.Serializable;

import javax.persistence.metamodel.SingularAttribute;

import org.springframework.data.jpa.domain.Specification;

/**
 * Range support for {@link Comparable} types.
 */
public class Range<E, D extends Comparable<? super D>> implements Serializable {
    private static final long serialVersionUID = 1L;

    private SingularAttribute<E, D> field;
    private D from;
    private D to;
    private Boolean includeNull;

    /**
     * Constructs a new {@link Range} with no boundaries and no restrictions on field's nullability.
     * @param field the attribute of an existing entity.
     */
    public Range(SingularAttribute<E, D> field) {
        this.field = field;
    }

    /**
     * Constructs a new Range.
     *
     * @param field the property's name of an existing entity.
     * @param from the lower boundary of this range. Null means no lower boundary.
     * @param to the upper boundary of this range. Null means no upper boundary.
     */
    public Range(SingularAttribute<E, D> field, D from, D to) {
        this.field = field;
        this.from = from;
        this.to = to;
    }

    /**
     * Constructs a new Range.
     *
     * @param field an entity's attribute
     * @param from the lower boundary of this range. Null means no lower boundary.
     * @param to the upper boundary of this range. Null means no upper boundary.
     * @param includeNull tells whether null should be filtered out or not.
     */
    public Range(SingularAttribute<E, D> field, D from, D to, Boolean includeNull) {
        this.field = field;
        this.from = from;
        this.to = to;
        this.includeNull = includeNull;
    }

    /**
     * Constructs a new Range by copy.
     */
    public Range(Range<E, D> other) {
        this.field = other.getField();
        this.from = other.getFrom();
        this.to = other.getTo();
        this.includeNull = other.getIncludeNull();
    }

    /**
     * @return the entity's attribute this {@link Range} refers to.
     */
    public SingularAttribute<E, D> getField() {
        return field;
    }

    /**
     * @return the lower range boundary or null for unbound lower range.
     */
    public D getFrom() {
        return from;
    }

    /**
     * Sets the lower range boundary. Accepts null for unbound lower range.
     */
    public void setFrom(D from) {
        this.from = from;
    }

    public boolean isFromSet() {
        return getFrom() != null;
    }

    /**
     * @return the upper range boundary or null for unbound upper range.
     */
    public D getTo() {
        return to;
    }

    /**
     * Sets the upper range boundary. Accepts null for unbound upper range.
     */
    public void setTo(D to) {
        this.to = to;
    }

    public boolean isToSet() {
        return getTo() != null;
    }

    public void setIncludeNull(boolean includeNull) {
        this.includeNull = includeNull;
    }

    public Boolean getIncludeNull() {
        return includeNull;
    }

    public boolean isIncludeNullSet() {
        return includeNull != null;
    }

    public boolean isBetween() {
        return isFromSet() && isToSet();
    }

    public boolean isSet() {
        return isFromSet() || isToSet() || isIncludeNullSet();
    }

    public boolean isValid() {
        if (isBetween()) {
            return getFrom().compareTo(getTo()) <= 0;
        }

        return true;
    }

    public Specification<E> toSpecification() {
        return RangeSpecification.toSpecification(this);
    }
}