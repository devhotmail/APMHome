package webapp.framework.dao;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Shared base repository providing query by example and pattern.
 */
@NoRepositoryBean
public interface GenericRepositoryUUID<E> extends PagingAndSortingRepository<E, String>,
        JpaSpecificationExecutor<E> {

    E findById(String id);
    
    Page<E> findWithExample(E example, Pageable pageable);

    Page<E> findWithExample(E example, List<Range<E, ?>> ranges, Pageable pageable);

    List<E> find();

    Page<E> findBySearchFilter(final List<SearchFilter> filterFilter, Pageable pageable);
    List<E> findBySearchFilter(final List<SearchFilter> filterFilter);

    /**
     * Lookup entities having at least one String attribute matching the passed pattern. Mostly used from autocomplete components.
     */
    List<E> find(String pattern);
}
