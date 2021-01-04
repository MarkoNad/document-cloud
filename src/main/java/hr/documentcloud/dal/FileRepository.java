package hr.documentcloud.dal;

import java.util.List;

public interface FileRepository {

    File save(File file);
    List<File> fetchFilesFromDirectory(String directory);

}
