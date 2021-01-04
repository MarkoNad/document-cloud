package hr.documentcloud.dal;

import java.util.Optional;

public interface DirectoryStructureRepository {

    DirectoryStructureContainer save(DirectoryStructureContainer directoryStructureContainer);
    Optional<DirectoryStructureContainer> getById(Long id);

}
