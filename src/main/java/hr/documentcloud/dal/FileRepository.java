package hr.documentcloud.dal;

import com.querydsl.jpa.impl.JPAQuery;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Log4j2
public class FileRepository extends AbstractRepository<File> {

    private static final QFile file = QFile.file;

    public List<File> fetchFilesFromDirectory(String directory) {
        return new JPAQuery<File>(em).from(file)
                .where(file.path.eq(directory))
                .fetch();
    }

    public Optional<File> getByNameAndLocation(String fileName, String directory) {
        log.info("Fetching file '{}' from '{}'.", fileName, directory);
        Optional<File> maybeFile = Optional.ofNullable(
                new JPAQuery<File>(em).from(FileRepository.file)
                        .where(FileRepository.file.name.eq(fileName)
                                .and(FileRepository.file.path.eq(directory))
                        )
                        .fetchFirst()
        );
        log.info("Fetched: {}.", maybeFile);
        return maybeFile;
    }

}
