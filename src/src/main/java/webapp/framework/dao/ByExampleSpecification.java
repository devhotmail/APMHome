package webapp.framework.dao;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.Attribute.PersistentAttributeType;
import javax.persistence.metamodel.SingularAttribute;
import static org.apache.commons.lang.StringUtils.isNotEmpty;
import org.apache.commons.lang.Validate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

/**
 * Helper to create find by example query.
 */
@Component
public class ByExampleSpecification {

    private EntityManager entityManager;

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public <T> Specification<T> byExample(T example) {
        return byExample(entityManager, example);
    }

    /**
     * Lookup entities having at least one String attribute matching the passed pattern. 
     */
    public <T> Specification<T> byPatternOnStringAttributes(String pattern, Class<T> entityType) {
        return byPatternOnStringAttributes(entityManager, pattern, entityType);
    }

    private <T> Specification<T> byExample(final EntityManager em, final T example) {
        Validate.notNull(example, "example must not be null");

        @SuppressWarnings("unchecked")
        final Class<T> type = (Class<T>) example.getClass();

        return new Specification<T>() {
            @Override
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                List<Predicate> predicates = new ArrayList<Predicate>();

                Set<SingularAttribute<T, ?>> types = em.getMetamodel().entity(type).getDeclaredSingularAttributes();

                for (Attribute<T, ?> attr : types) {
                    if (attr.getPersistentAttributeType() == PersistentAttributeType.MANY_TO_ONE
                            || attr.getPersistentAttributeType() == PersistentAttributeType.ONE_TO_ONE) {
                        continue;
                    }

                    String fieldName = attr.getName();

                    try {
                        Member javaMember = attr.getJavaMember(); 
                        if (attr.getJavaType() == String.class) {
                            String fieldValue;
                            if (javaMember instanceof Field) fieldValue = (String)((Field)javaMember).get(example);
                            else fieldValue = (String) ReflectionUtils.invokeMethod((Method)javaMember, example);
                            
                            if (isNotEmpty(fieldValue)) {
                                // please compiler
                                SingularAttribute<T, String> stringAttr = em.getMetamodel().entity(type)
                                        .getDeclaredSingularAttribute(fieldName, String.class);
                                // apply like
                                predicates.add(builder.like(root.get(stringAttr), pattern(fieldValue)));

                            }
                        } 
                        else {
                            Object fieldValue;
                            if (javaMember instanceof Field) fieldValue = ((Field)javaMember).get(example);
                            else fieldValue = (String) ReflectionUtils.invokeMethod((Method)javaMember, example);

                            if (fieldValue != null) {
                                // please compiler
                                SingularAttribute<T, ?> anyAttr = em.getMetamodel().entity(type)
                                        .getDeclaredSingularAttribute(fieldName, fieldValue.getClass());
                                //  apply equal
                                predicates.add(builder.equal(root.get(anyAttr), fieldValue));
                            }
                        }
                    } catch (Exception e) {
                        throw new IllegalStateException("OOOUCH!!!", e);
                    }
                }

                if (predicates.size() > 0) {
                    return builder.and((Predicate[]) predicates.toArray(new Predicate[predicates.size()]));
                }

                return builder.conjunction();
            }
        };
    }

    private <T> Specification<T> byPatternOnStringAttributes(final EntityManager em, final String pattern,
            final Class<T> type) {
        return new Specification<T>() {
            @Override
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                List<Predicate> predicates = new ArrayList<Predicate>();

                Set<SingularAttribute<T, ?>> types = em.getMetamodel().entity(type).getDeclaredSingularAttributes();

                for (Attribute<T, ?> attr : types) {
                    if (attr.getPersistentAttributeType() == PersistentAttributeType.MANY_TO_ONE
                            || attr.getPersistentAttributeType() == PersistentAttributeType.ONE_TO_ONE) {
                        continue;
                    }

                    String fieldName = attr.getName();

                    try {
                        if (attr.getJavaType() == String.class) {
                            if (isNotEmpty(pattern)) {
                                SingularAttribute<T, String> stringAttr = em.getMetamodel().entity(type)
                                        .getDeclaredSingularAttribute(fieldName, String.class);
                                predicates.add(builder.like(root.get(stringAttr), pattern(pattern)));
                            }
                        }
                    } catch (Exception e) {
                        throw new IllegalStateException("OOOUCH!!!", e);
                    }
                }

                if (predicates.size() > 0) {
                    return builder.or((Predicate[]) predicates.toArray(new Predicate[predicates.size()]));
                }

                return builder.conjunction(); // 1 = 1
            }
        };
    }

    static private String pattern(String str) {
        return "%" + str + "%";
    }
}