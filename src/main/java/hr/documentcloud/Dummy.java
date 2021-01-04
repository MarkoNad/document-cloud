package hr.documentcloud;

import hr.documentcloud.model.DirectoryStructure;

import java.io.*;

public class Dummy {

    public static void main(String[] args) {
        DirectoryStructure directoryStructure = DirectoryStructure.fromPath("home/a/b/c/c/d/a");
        directoryStructure.updateStructure("home/a/b/c/c");
        directoryStructure.updateStructure("home/a/b/c/c/");
        directoryStructure.updateStructure("home/a/b/bb/dd");

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(directoryStructure);
            out.flush();
            byte[] yourBytes = bos.toByteArray();

            ByteArrayInputStream bis = new ByteArrayInputStream(yourBytes);
            ObjectInput in;
            in = new ObjectInputStream(bis);
            Object o = in.readObject();

            System.out.println(o);

        } catch (Exception ex) {
            System.out.println("IOException is caught");
        }
    }
}
