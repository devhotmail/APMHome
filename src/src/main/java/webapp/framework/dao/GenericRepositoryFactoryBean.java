package webapp.framework.dao;

import java.io.Serializable;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

public class GenericRepositoryFactoryBean<T extends JpaRepository<S, ID>, S, ID extends Serializable> extends
        JpaRepositoryFactoryBean<T, S, ID> {

    @Autowired
    private ByExampleSpecification byExampleSpecification;

    @Override
    protected RepositoryFactorySupport createRepositoryFactory(EntityManager entityManager) {
        return new MyRepositoryFactory(entityManager, byExampleSpecification);
    }

    public void init() {
    }

    private static class MyRepositoryFactory extends JpaRepositoryFactory {

        private ByExampleSpecification byExampleSpecification;

        public MyRepositoryFactory(EntityManager entityManager, ByExampleSpecification byExampleSpecification) {
            super(entityManager);
            this.byExampleSpecification = byExampleSpecification;
        }

        @Override
        protected <T, ID extends Serializable> SimpleJpaRepository<?, ?> getTargetRepository(RepositoryInformation information,
                EntityManager entityManager) {
            JpaEntityInformation<?, Serializable> entityInformation = getEntityInformation(information.getDomainType());
            if (entityInformation.getIdType().isAssignableFrom(String.class)) {
                return new GenericRepositoryUUIDImpl(entityInformation, entityManager, byExampleSpecification);
            } else {
                return new GenericRepositoryImpl(entityInformation, entityManager, byExampleSpecification);
            }
        }

        @Override
        protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
            if (metadata.getIdType().isAssignableFrom(String.class))
                return GenericRepositoryUUIDImpl.class;
            else
                return GenericRepositoryImpl.class;
        }
    }
}
