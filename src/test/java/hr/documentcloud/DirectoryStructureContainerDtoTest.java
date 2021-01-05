package hr.documentcloud;

import hr.documentcloud.model.DirectoryStructure;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class DirectoryStructureContainerDtoTest {

    @Test
    public void notARealTest() {
        DirectoryStructure directoryStructure = DirectoryStructure.fromPath("home/a/b/c/c/d/a");
        System.out.println(directoryStructure);

        directoryStructure.updateStructure("home/a/b/c/c");
        System.out.println(directoryStructure);

        directoryStructure.updateStructure("home/a/b/c/c/");
        System.out.println(directoryStructure);

        directoryStructure.updateStructure("home/a/b/bb/dd");
        System.out.println(directoryStructure);

        System.out.println(directoryStructure.getSubfolderNames("home/a/b"));
    }

    @Test
    public void testSubfolderNames() {
        DirectoryStructure directoryStructure = DirectoryStructure.fromPath("home/a/b/c/c/d/a");

        directoryStructure.updateStructure("home/a/b/c/c");
        directoryStructure.updateStructure("home/a/b/c/c/");
        directoryStructure.updateStructure("home/a/b/bb/dd");

        assertTrue(directoryStructure.getSubfolderNames("home").contains("a"));
        assertTrue(directoryStructure.getSubfolderNames("home/a").contains("b"));
        assertTrue(directoryStructure.getSubfolderNames("home/a/b").contains("c"));
        assertTrue(directoryStructure.getSubfolderNames("home/a/b").contains("bb"));
        assertTrue(directoryStructure.getSubfolderNames("home/a/b/c").contains("c"));
        assertTrue(directoryStructure.getSubfolderNames("home/a/b/bb").contains("dd"));
    }

}
