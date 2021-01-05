package hr.documentcloud.dal;

import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class FileRepository extends AbstractRepository<File> {

    private static final QFile file = QFile.file;

    public List<File> fetchFilesFromDirectory(String directory) {
        return new JPAQuery<File>(em).from(file)
                .where(file.path.eq(directory))
                .fetch();
    }

    public Optional<File> getByNameAndLocation(String fileName, String directory) {
        return Optional.ofNullable(
                new JPAQuery<File>(em).from(file)
                        .where(file.name.eq(fileName)
                                .and(file.path.eq(directory))
                        )
                        .fetchFirst()
        );
    }

}
