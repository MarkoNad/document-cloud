package hr.documentcloud.dal.util;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.hibernate.Session;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.SQLException;

@Service
public class LobHelper {

    @PersistenceContext
    protected EntityManager em;

    public Blob createBlob(InputStream content, long size) {
        Session session = getSession();
        return session.getLobHelper().createBlob(content, size);
    }

    public Clob createClob(InputStream content, long size, Charset charset) {
        Session session = getSession();
        return session.getLobHelper().createClob(new InputStreamReader(content, charset), size);
    }

    private Session getSession() {
        return em.unwrap(Session.class);
    }

    @Transactional
    public void writeBlobToOutputStream(Blob blob, OutputStream outputStream) throws SQLException, IOException {
        InputStream blobInputStream = blob.getBinaryStream();
        IOUtils.copy(blobInputStream, outputStream);
        blobInputStream.close();
    }

}