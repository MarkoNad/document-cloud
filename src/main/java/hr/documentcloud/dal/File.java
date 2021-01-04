package hr.documentcloud.dal;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
public class File {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    private String name;

    private String path;

    private byte[] contents;

    public File(String name, String path, byte[] contents) {
        this.name = name;
        this.path = path;
        this.contents = contents;
    }

}