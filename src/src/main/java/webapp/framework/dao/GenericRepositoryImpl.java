package webapp.framework.dao;

import java.util.List;
import javax.persistence.EntityManager;
import org.apache.commons.lang.Validate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Shared base repository providing query by example and query by string pattern.
 */
public class GenericRepositoryImpl<E> extends SimpleJpaRepository<E, Long> implements
        GenericRepository<E> {
    private JpaEntityInformation<E, ?> entityInformation;
    private EntityManager entityManager;
    private ByExampleSpecification byExampleSpecification;
    private Class<E> type;
    private static final int MAX_VALUES_RETREIVED = 500;

    public GenericRepositoryImpl(JpaEntityInformation<E, ?> entityInformation, EntityManager entityManager,
            ByExampleSpecification byExampleSpecification) {
        super(entityInformation, entityManager);
        this.entityInformation = entityInformation;
        this.entityManager = entityManager;
        this.byExampleSpecification = byExampleSpecification;
        this.type = entityInformation.getJavaType();
    }

    @Override
    public Page<E> findWithExample(E example, Pageable pageable) {
        Specifications<E> spec = Specifications.where(byExampleSpecification.byExample(example));
        return findAll(spec, pageable);
    }

    @Override
    public Page<E> findWithExample(E example, List<Range<E, ?>> ranges, Pageable pageable) {
        Specifications<E> spec = Specifications.where(byExampleSpecification.byExample(example));
        spec = RangeSpecification.andRangeIfSet(spec, ranges);
        return findAll(spec, pageable);
    }

    public Page<E> findBySearchFilter(final List<SearchFilter> filterFilter, Pageable pageable){
        if(filterFilter==null)
            return findAll(pageable);
        else{
            Specification<E> spec = BySearchFilterSpecification.bySearchFilter(filterFilter, type);
            return findAll(spec, pageable);
        }
    }

    public List<E> findBySearchFilter(final List<SearchFilter> filterFilter){
        if(filterFilter==null)
            return findAll();
        else{
            Specification<E> spec = BySearchFilterSpecification.bySearchFilter(filterFilter, type);
            return findAll(spec);
        }
    }
    
    @Override
    public E findById(Integer id){
        return entityManager.find(this.type, id);
    }
    
    @Override
    public List<E> find() {
        return findAll(new PageRequest(0, MAX_VALUES_RETREIVED)).getContent();
    }
    
    @Override
    public List<E> find(String pattern) {
        Specifications<E> spec = Specifications
                .where(byExampleSpecification.byPatternOnStringAttributes(pattern, type));
        return findAll(spec, new PageRequest(0, MAX_VALUES_RETREIVED)).getContent();
    }

    @Transactional
    public <S extends E> S save(S entity) {
        Validate.notNull(entity, "The entity to save cannot be null element");

        // creation with auto generated id
        if (entityInformation.isNew(entity)) {
            entityManager.persist(entity);
            return entity;
        }

        // creation with manually assigned key
        if (JpaUtil.isEntityIdManuallyAssigned(type) && !entityManager.contains(entity)) {
            entityManager.persist(entity);
            return entity;
        }

    	entityManager.merge(entity);
        return entity;
    }
}
