package net.fasolato.jfmigrate.internal;

import org.omg.CORBA.Environment;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by fasolato on 19/04/2017.
 */
public class LoggablePreparedStatement implements PreparedStatement {
    private String command;
    private PreparedStatement inner;
    private Map<Integer, Object> values;

    public LoggablePreparedStatement(Connection conn, String s) throws SQLException {
        command = s;
        inner = conn.prepareStatement(s);
        values = new HashMap<Integer, Object>();
    }

    @Override
    public String toString() {
        String toReturn = command;
        if (!values.isEmpty()) {
            toReturn += System.lineSeparator() + System.lineSeparator() + "Values:" + System.lineSeparator();
            for (Integer i : values.keySet()) {
                toReturn += i + ": " + values.get(i) + System.lineSeparator();
            }
        }
        return toReturn;
    }

    public ResultSet executeQuery() throws SQLException {
        return inner.executeQuery();
    }

    public int executeUpdate() throws SQLException {
        return inner.executeUpdate();
    }

    public void setNull(int i, int i1) throws SQLException {
        values.put(i, null);
        inner.setNull(i, i1);
    }

    public void setBoolean(int i, boolean b) throws SQLException {
        values.put(i, b);
        inner.setBoolean(i, b);
    }

    public void setByte(int i, byte b) throws SQLException {
        values.put(i, b);
        inner.setByte(i, b);
    }

    public void setShort(int i, short i1) throws SQLException {
        values.put(i, i1);
        inner.setShort(i, i1);
    }

    public void setInt(int i, int i1) throws SQLException {
        values.put(i, i1);
        inner.setInt(i, i1);
    }

    public void setLong(int i, long l) throws SQLException {
        values.put(i, l);
        inner.setLong(i, l);
    }

    public void setFloat(int i, float v) throws SQLException {
        values.put(i, v);
        inner.setFloat(i, v);
    }

    public void setDouble(int i, double v) throws SQLException {
        values.put(i, v);
        inner.setDouble(i, v);
    }

    public void setBigDecimal(int i, BigDecimal bigDecimal) throws SQLException {
        values.put(i, bigDecimal);
        inner.setBigDecimal(i, bigDecimal);
    }

    public void setString(int i, String s) throws SQLException {
        values.put(i, s);
        inner.setString(i, s);
    }

    public void setBytes(int i, byte[] bytes) throws SQLException {
        values.put(i, bytes);
        inner.setBytes(i, bytes);
    }

    public void setDate(int i, Date date) throws SQLException {
        values.put(i, date);
        inner.setDate(i, date);
    }

    public void setTime(int i, Time time) throws SQLException {
        values.put(i, time);
        inner.setTime(i, time);
    }

    public void setTimestamp(int i, Timestamp timestamp) throws SQLException {
        values.put(i, timestamp);
        inner.setTimestamp(i, timestamp);
    }

    public void setAsciiStream(int i, InputStream inputStream, int i1) throws SQLException {
        values.put(i, "ASCIIStream");
        inner.setAsciiStream(i, inputStream, i1);
    }

    @Deprecated
    public void setUnicodeStream(int i, InputStream inputStream, int i1) throws SQLException {
        values.put(i, "UnicodeStream");
        inner.setUnicodeStream(i, inputStream, i1);
    }

    public void setBinaryStream(int i, InputStream inputStream, int i1) throws SQLException {
        values.put(i, "BinaryStream");
        inner.setBinaryStream(i, inputStream, i1);
    }

    public void clearParameters() throws SQLException {
        values.clear();
        inner.clearParameters();
    }

    public void setObject(int i, Object o, int i1) throws SQLException {
        values.put(i, o);
        inner.setObject(i, o, i1);
    }

    public void setObject(int i, Object o) throws SQLException {
        values.put(i, o);
        inner.setObject(i, o);
    }

    public boolean execute() throws SQLException {
        return inner.execute();
    }

    public void addBatch() throws SQLException {
        inner.addBatch();
    }

    public void setCharacterStream(int i, Reader reader, int i1) throws SQLException {
        values.put(i, "CharacterStream");
        inner.setCharacterStream(i, reader, i1);
    }

    public void setRef(int i, Ref ref) throws SQLException {
        values.put(i, ref);
        inner.setRef(i, ref);
    }

    public void setBlob(int i, Blob blob) throws SQLException {
        values.put(i, "Blob");
        inner.setBlob(i, blob);
    }

    public void setClob(int i, Clob clob) throws SQLException {
        values.put(i, "Clob");
        inner.setClob(i, clob);
    }

    public void setArray(int i, Array array) throws SQLException {
        values.put(i, array);
        inner.setArray(i, array);
    }

    public ResultSetMetaData getMetaData() throws SQLException {
        return inner.getMetaData();
    }

    public void setDate(int i, Date date, Calendar calendar) throws SQLException {
        values.put(i, date);
        inner.setDate(i, date, calendar);
    }

    public void setTime(int i, Time time, Calendar calendar) throws SQLException {
        values.put(i, time);
        inner.setTime(i, time, calendar);
    }

    public void setTimestamp(int i, Timestamp timestamp, Calendar calendar) throws SQLException {
        values.put(i, timestamp);
        inner.setTimestamp(i, timestamp, calendar);
    }

    public void setNull(int i, int i1, String s) throws SQLException {
        values.put(i, null);
        inner.setNull(i, i1, s);
    }

    public void setURL(int i, URL url) throws SQLException {
        values.put(i, url);
        inner.setURL(i, url);
    }

    public ParameterMetaData getParameterMetaData() throws SQLException {
        return inner.getParameterMetaData();
    }

    public void setRowId(int i, RowId rowId) throws SQLException {
        values.put(i, rowId);
        inner.setRowId(i, rowId);
    }

    public void setNString(int i, String s) throws SQLException {
        values.put(i, s);
        inner.setNString(i, s);
    }

    public void setNCharacterStream(int i, Reader reader, long l) throws SQLException {
        values.put(i, "NCharacterStream");
        inner.setNCharacterStream(i, reader, l);
    }

    public void setNClob(int i, NClob nClob) throws SQLException {
        values.put(i, "NClob");
        inner.setNClob(i, nClob);
    }

    public void setClob(int i, Reader reader, long l) throws SQLException {
        values.put(i, "Clob");
        inner.setClob(i, reader, l);
    }

    public void setBlob(int i, InputStream inputStream, long l) throws SQLException {
        values.put(i, "Blob");
        inner.setBlob(i, inputStream, l);
    }

    public void setNClob(int i, Reader reader, long l) throws SQLException {
        values.put(i, "NClob");
        inner.setNClob(i, reader, l);
    }

    public void setSQLXML(int i, SQLXML sqlxml) throws SQLException {
        values.put(i, sqlxml);
        inner.setSQLXML(i, sqlxml);
    }

    public void setObject(int i, Object o, int i1, int i2) throws SQLException {
        values.put(i, o);
        inner.setObject(i, o, i1, i2);
    }

    public void setAsciiStream(int i, InputStream inputStream, long l) throws SQLException {
        values.put(i, "AsciiStream");
        inner.setAsciiStream(i, inputStream, l);
    }

    public void setBinaryStream(int i, InputStream inputStream, long l) throws SQLException {
        values.put(i, "BinaryStream");
        inner.setBinaryStream(i, inputStream, l);
    }

    public void setCharacterStream(int i, Reader reader, long l) throws SQLException {
        values.put(i, "CharacterStream");
        inner.setCharacterStream(i, reader, l);
    }

    public void setAsciiStream(int i, InputStream inputStream) throws SQLException {
        values.put(i, "ASCIIStream");
        inner.setAsciiStream(i, inputStream);
    }

    public void setBinaryStream(int i, InputStream inputStream) throws SQLException {
        values.put(i, "BinaryStream");
        inner.setBinaryStream(i, inputStream);
    }

    public void setCharacterStream(int i, Reader reader) throws SQLException {
        values.put(i, "CharacterStream");
        inner.setCharacterStream(i, reader);
    }

    public void setNCharacterStream(int i, Reader reader) throws SQLException {
        values.put(i, "NCharacterStream");
        inner.setNCharacterStream(i, reader);
    }

    public void setClob(int i, Reader reader) throws SQLException {
        values.put(i, "Clob");
        inner.setClob(i, reader);
    }

    public void setBlob(int i, InputStream inputStream) throws SQLException {
        values.put(i, "Blob");
        inner.setBlob(i, inputStream);
    }

    public void setNClob(int i, Reader reader) throws SQLException {
        values.put(i, "NClob");
        inner.setNClob(i, reader);
    }

    public ResultSet executeQuery(String s) throws SQLException {
        return inner.executeQuery(s);
    }

    public int executeUpdate(String s) throws SQLException {
        return inner.executeUpdate(s);
    }

    public void close() throws SQLException {
        inner.close();
    }

    public int getMaxFieldSize() throws SQLException {
        return inner.getMaxFieldSize();
    }

    public void setMaxFieldSize(int i) throws SQLException {
        inner.setMaxFieldSize(i);
    }

    public int getMaxRows() throws SQLException {
        return inner.getMaxRows();
    }

    public void setMaxRows(int i) throws SQLException {
        inner.setMaxRows(i);
    }

    public void setEscapeProcessing(boolean b) throws SQLException {
        inner.setEscapeProcessing(b);
    }

    public int getQueryTimeout() throws SQLException {
        return inner.getQueryTimeout();
    }

    public void setQueryTimeout(int i) throws SQLException {
        inner.setQueryTimeout(i);
    }

    public void cancel() throws SQLException {
        inner.cancel();
    }

    public SQLWarning getWarnings() throws SQLException {
        return inner.getWarnings();
    }

    public void clearWarnings() throws SQLException {
        inner.clearWarnings();
    }

    public void setCursorName(String s) throws SQLException {
        inner.setCursorName(s);
    }

    public boolean execute(String s) throws SQLException {
        return inner.execute(s);
    }

    public ResultSet getResultSet() throws SQLException {
        return inner.getResultSet();
    }

    public int getUpdateCount() throws SQLException {
        return inner.getUpdateCount();
    }

    public boolean getMoreResults() throws SQLException {
        return inner.getMoreResults();
    }

    public void setFetchDirection(int i) throws SQLException {
        inner.setFetchDirection(i);
    }

    public int getFetchDirection() throws SQLException {
        return inner.getFetchDirection();
    }

    public void setFetchSize(int i) throws SQLException {
        inner.setFetchSize(i);
    }

    public int getFetchSize() throws SQLException {
        return inner.getFetchSize();
    }

    public int getResultSetConcurrency() throws SQLException {
        return inner.getResultSetConcurrency();
    }

    public int getResultSetType() throws SQLException {
        return inner.getResultSetType();
    }

    public void addBatch(String s) throws SQLException {
        inner.addBatch(s);
    }

    public void clearBatch() throws SQLException {
        inner.clearBatch();
    }

    public int[] executeBatch() throws SQLException {
        return inner.executeBatch();
    }

    public Connection getConnection() throws SQLException {
        return inner.getConnection();
    }

    public boolean getMoreResults(int i) throws SQLException {
        return inner.getMoreResults();
    }

    public ResultSet getGeneratedKeys() throws SQLException {
        return inner.getGeneratedKeys();
    }

    public int executeUpdate(String s, int i) throws SQLException {
        return inner.executeUpdate(s, i);
    }

    public int executeUpdate(String s, int[] ints) throws SQLException {
        return inner.executeUpdate(s, ints);
    }

    public int executeUpdate(String s, String[] strings) throws SQLException {
        return inner.executeUpdate(s, strings);
    }

    public boolean execute(String s, int i) throws SQLException {
        return inner.execute(s, i);
    }

    public boolean execute(String s, int[] ints) throws SQLException {
        return inner.execute(s, ints);
    }

    public boolean execute(String s, String[] strings) throws SQLException {
        return inner.execute(s, strings);
    }

    public int getResultSetHoldability() throws SQLException {
        return inner.getResultSetHoldability();
    }

    public boolean isClosed() throws SQLException {
        return inner.isClosed();
    }

    public void setPoolable(boolean b) throws SQLException {
        inner.setPoolable(b);
    }

    public boolean isPoolable() throws SQLException {
        return inner.isPoolable();
    }

    public void closeOnCompletion() throws SQLException {
        inner.closeOnCompletion();
    }

    public boolean isCloseOnCompletion() throws SQLException {
        return inner.isCloseOnCompletion();
    }

    public <T> T unwrap(Class<T> aClass) throws SQLException {
        return inner.unwrap(aClass);
    }

    public boolean isWrapperFor(Class<?> aClass) throws SQLException {
        return inner.isWrapperFor(aClass);
    }
}
