package hr.documentcloud.dal;

import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;

@Repository
public class DirectoryStructureRepositoryImpl implements DirectoryStructureRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public DirectoryStructureContainer save(DirectoryStructureContainer directoryStructureContainer) {
        em.persist(directoryStructureContainer);
        return directoryStructureContainer;
    }

    @Override
    public Optional<DirectoryStructureContainer> getById(Long id) {
        final JPAQuery<DirectoryStructureContainer> query = new JPAQuery<>(em);
        final QDirectoryStructure directoryStructure = QDirectoryStructure.directoryStructure;
        return Optional.ofNullable(query.from(directoryStructure)
                .where(directoryStructure.id.eq(id))
                .fetchFirst());
    }

}
