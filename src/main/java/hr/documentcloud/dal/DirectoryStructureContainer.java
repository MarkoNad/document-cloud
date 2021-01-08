package hr.documentcloud.dal;

import hr.documentcloud.exception.SerializationException;
import hr.documentcloud.model.DirectoryStructure;
import lombok.extern.log4j.Log4j2;

import javax.persistence.*;
import java.io.*;
import java.util.Arrays;

@Entity
@Table(name = "directory_structure_container")
@Log4j2
public class DirectoryStructureContainer {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @Column(name = "structure_bytes")
    private byte[] structureBytes;

    private DirectoryStructureContainer() {
        // no-arg ctor for Hibernate
    }

    public DirectoryStructureContainer(DirectoryStructure structure) {
        update(structure);
    }

    private byte[] serialize(DirectoryStructure structure) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(structure);
            out.flush();
            return bos.toByteArray();
        } catch (IOException e) {
            throw new SerializationException("Failed to serialize directory structure.", e);
        }
    }

    public DirectoryStructure getDirectoryStructure() {
        return deserialize(structureBytes);
    }

    private DirectoryStructure deserialize(byte[] structureBytes) {
        ByteArrayInputStream bis = new ByteArrayInputStream(structureBytes);
        try {
            ObjectInput in = new ObjectInputStream(bis);
            return (DirectoryStructure)in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new SerializationException("Failed to deserialize directory structure.", e);
        }
    }

    public void update(DirectoryStructure structure) {
        this.structureBytes = serialize(structure);
    }

    @Override
    public String toString() {
        return "DirectoryStructureContainer{" +
                "id=" + id +
                ", directory structure (deserialized)=\n" + deserialize(structureBytes) +
                '}';
    }
}
