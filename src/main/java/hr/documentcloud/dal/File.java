package hr.documentcloud.dal;

import lombok.Data;

import javax.persistence.*;
import java.sql.Blob;

@Entity
@Table(name = "file",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"name", "path"})
        }
)
@Data
public class File {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "path")
    private String path;

    @Lob
    @Column(name = "contents")
    private Blob contents;

    private File() {
        // no-arg ctor for Hibernate
    }

    public File(String name, String path, Blob contents) {
        this.name = name;
        this.path = path;
        this.contents = contents;
    }

}