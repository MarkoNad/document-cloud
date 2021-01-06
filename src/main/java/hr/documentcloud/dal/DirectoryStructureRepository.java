package hr.documentcloud.dal;

import com.querydsl.jpa.impl.JPAQuery;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Log4j2
public class DirectoryStructureRepository extends AbstractRepository<DirectoryStructureContainer> {

    final QDirectoryStructureContainer directoryStructureContainer = QDirectoryStructureContainer.directoryStructureContainer;

    public Optional<DirectoryStructureContainer> get() {
        log.info("Fetching directory structure container.");
        Optional<DirectoryStructureContainer> maybeContainer = Optional.ofNullable(new JPAQuery<DirectoryStructureContainer>(em)
                .from(directoryStructureContainer)
//                .where(directoryStructure.id.eq(id)) // TODO
                .fetchFirst()
        );
        log.info("Fetched: {}.", maybeContainer.map(Object::toString));
        return maybeContainer;
    }

}
