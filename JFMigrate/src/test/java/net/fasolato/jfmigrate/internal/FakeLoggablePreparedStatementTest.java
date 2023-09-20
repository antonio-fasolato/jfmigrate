package net.fasolato.jfmigrate.internal;

import junit.framework.TestCase;

import java.sql.Date;
import java.text.SimpleDateFormat;

public class FakeLoggablePreparedStatementTest extends TestCase {

    public void testToString() {
        String res;
        try (FakeLoggablePreparedStatement st = new FakeLoggablePreparedStatement("?")) {
            st.setDate(1, Date.valueOf("2023-09-20"));

            res = st.toString();
            assertNotNull("String is null",res);
            assertEquals("String is '2023-09-20'", "'2023-09-20'", res);
        } catch (Exception e) {
            fail("Unexpected exception");
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try (FakeLoggablePreparedStatement st = new FakeLoggablePreparedStatement("?")) {
            st.setObject(1, sdf.parse("2023-09-20 09:12:00"));

            res = st.toString();
            assertNotNull("String is null",res);
            assertEquals("String is '2023-09-20'", "'2023-09-20 09:12:00'", res);
        } catch (Exception e) {
            fail("Unexpected exception");
        }
    }
}