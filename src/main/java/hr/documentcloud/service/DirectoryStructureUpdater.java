package hr.documentcloud.service;

import hr.documentcloud.dal.DirectoryStructureContainer;
import hr.documentcloud.dal.DirectoryStructureRepository;
import hr.documentcloud.model.DirectoryStructure;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.ExecutorService;

@Service
@Log4j2
public class DirectoryStructureUpdater {

    private static final Object LOCK = new Object();

    private final DirectoryStructureRepository directoryStructureRepository;
    private final ExecutorService executorService;

    @Autowired
    public DirectoryStructureUpdater(
            DirectoryStructureRepository directoryStructureRepository,
            @Qualifier("cachedThreadPool") ExecutorService executorService) {

        this.directoryStructureRepository = directoryStructureRepository;
        this.executorService = executorService;
    }

    public synchronized void updateDirectoryStructure(String newDirectory) {
        log.info("Submitting a new task to update directory structure with '{}'.", newDirectory);
        DirectoryStructureUpdatingTask updateTask = new DirectoryStructureUpdatingTask(newDirectory);
        executorService.submit(updateTask);
        log.info("Submitted.");
    }

    private class DirectoryStructureUpdatingTask implements Runnable {

        private final String newDirectory;

        private DirectoryStructureUpdatingTask(String newDirectory) {
            this.newDirectory = newDirectory;
        }

        @Override
        public void run() {
            try {
                updateDirectoryStructure(newDirectory);
            } catch(Exception e) {
                log.error("Failed to update directory structure.", e);
            }
        }

        private void updateDirectoryStructure(String newDirectory) {
            synchronized (LOCK) {
                log.info("Updating directory structure with destination directory '{}'.", newDirectory);

                Optional<DirectoryStructureContainer> maybeContainer = directoryStructureRepository.get();

                if (maybeContainer.isPresent()) {
                    DirectoryStructureContainer container = maybeContainer.get();
                    DirectoryStructure structure = container.getDirectoryStructure();
                    structure.updateStructure(newDirectory);
                    container.update(structure);
                    log.info("Updating existing directory structure.");
                    directoryStructureRepository.merge(container);
                } else {
                    DirectoryStructure structure = DirectoryStructure.fromPath(newDirectory);
                    DirectoryStructureContainer container = new DirectoryStructureContainer(structure);
                    log.info("Inserting new directory structure.");
                    directoryStructureRepository.persist(container);
                }
            }
        }
    }

}
