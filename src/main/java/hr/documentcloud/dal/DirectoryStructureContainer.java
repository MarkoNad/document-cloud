package hr.documentcloud.dal;

import hr.documentcloud.exception.SerializationException;
import hr.documentcloud.model.DirectoryStructure;
import lombok.extern.log4j.Log4j2;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.*;

@Entity
@Log4j2
public class DirectoryStructureContainer {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    private byte[] structureBytes;

    public DirectoryStructureContainer(DirectoryStructure structure) {
        this.structureBytes = serialize(structure);
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

}
