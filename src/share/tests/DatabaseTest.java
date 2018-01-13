package share.tests;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import org.junit.jupiter.api.Assertions;
import share.database.Database;
import share.models.*;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseTest {
    private Database db = new Database();

    @Test
    public void saveUser() throws Exception {
        //todo: change every time u run
        User u = new User("Alex", "1234", "alex@email.com", "...");

        int id = db.saveUser(u);
        assertEquals(true, id != -1);

        User u2 = new User("celina", "1234", "celina@email.com", "Hoi! Ik heet celina");
        int idd = db.saveUser(u2);
        assertEquals(-2, idd);
        db.closeConn();

    }

    @Test
    public void logIn() throws Exception {
        User u = db.logIn("test@email.com", "1234");
        assertEquals("test@email.com", u.getEmail());
        User u2 = db.logIn("doijoefj", "");
        assertEquals(null, u2   );
        db.closeConn();
    }

    @Test
    public void getConn() throws Exception {
        assertEquals(db.getConn() != null, true);
        db.closeConn();
    }
}