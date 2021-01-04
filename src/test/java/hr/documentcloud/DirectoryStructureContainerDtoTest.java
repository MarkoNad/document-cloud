package hr.documentcloud;

import hr.documentcloud.model.DirectoryStructure;
import org.junit.jupiter.api.Test;

public class DirectoryStructureContainerDtoTest {

    @Test
    public void test() {
        DirectoryStructure directoryStructure = DirectoryStructure.fromPath("home/a/b/c/c/d/a");
        System.out.println(directoryStructure);

        directoryStructure.updateStructure("home/a/b/c/c");
        System.out.println(directoryStructure);

        directoryStructure.updateStructure("home/a/b/c/c/");
        System.out.println(directoryStructure);

        directoryStructure.updateStructure("home/a/b/bb/dd");
        System.out.println(directoryStructure);
    }

}
