package hr.documentcloud.dal;

import lombok.Data;

import javax.persistence.*;
import java.sql.Blob;
import java.time.LocalDateTime;

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

    @Column(name = "last_modified")
    private LocalDateTime lastModified;

    @Lob
    @Column(name = "contents")
    private Blob contents;

    private File() {
        // no-arg ctor for Hibernate
    }

    public File(String name, String path, LocalDateTime lastModified, Blob contents) {
        this.name = name;
        this.path = path;
        this.lastModified = lastModified;
        this.contents = contents;
    }

}