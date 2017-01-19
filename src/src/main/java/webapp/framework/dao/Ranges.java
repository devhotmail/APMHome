package webapp.framework.dao;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import javax.persistence.metamodel.SingularAttribute;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

/**
 * Type safe {@link Range}s for commons types
 */
public class Ranges {
    public static class RangeBigDecimal<E> extends Range<E, BigDecimal> {
        private static final long serialVersionUID = 1L;

        public RangeBigDecimal(SingularAttribute<E, BigDecimal> field) {
            super(field);
        }
    }

    public static class RangeBigInteger<E> extends Range<E, BigInteger> {
        private static final long serialVersionUID = 1L;

        public RangeBigInteger(SingularAttribute<E, BigInteger> field) {
            super(field);
        }
    }

    public static class RangeDate<E> extends Range<E, Date> {
        private static final long serialVersionUID = 1L;

        public RangeDate(SingularAttribute<E, Date> field) {
            super(field);
        }
    }

    public static class RangeDouble<E> extends Range<E, Double> {
        private static final long serialVersionUID = 1L;

        public RangeDouble(SingularAttribute<E, Double> field) {
            super(field);
        }
    }

    public static class RangeFloat<E> extends Range<E, Float> {
        private static final long serialVersionUID = 1L;

        public RangeFloat(SingularAttribute<E, Float> field) {
            super(field);
        }
    }

    public static class RangeInteger<E> extends Range<E, Integer> {
        private static final long serialVersionUID = 1L;

        public RangeInteger(SingularAttribute<E, Integer> field) {
            super(field);
        }
    }

    public static class RangeLocalDate<E> extends Range<E, LocalDate> {
        private static final long serialVersionUID = 1L;

        public RangeLocalDate(SingularAttribute<E, LocalDate> field) {
            super(field);
        }
    }

    public static class RangeLocalDateTime<E> extends Range<E, LocalDateTime> {
        private static final long serialVersionUID = 1L;

        public RangeLocalDateTime(SingularAttribute<E, LocalDateTime> field) {
            super(field);
        }
    }

    public static class RangeLong<E> extends Range<E, Long> {
        private static final long serialVersionUID = 1L;

        public RangeLong(SingularAttribute<E, Long> field) {
            super(field);
        }
    }
}
