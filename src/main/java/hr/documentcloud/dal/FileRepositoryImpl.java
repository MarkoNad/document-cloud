package hr.documentcloud.dal;

import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class FileRepositoryImpl implements FileRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public File save(File file) {
        em.persist(file);
        return file;
    }

    @Override
    public List<File> fetchFilesFromDirectory(String directory) {
        final JPAQuery<File> query = new JPAQuery<>(em);
        final QFile file = QFile.file;
        return query.from(file)
                .where(file.path.eq(directory))
                .fetch();
    }

}
