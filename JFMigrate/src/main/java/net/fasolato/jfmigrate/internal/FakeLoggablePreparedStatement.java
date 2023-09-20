package net.fasolato.jfmigrate.internal;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class FakeLoggablePreparedStatement implements PreparedStatement {
    private final String command;
    private final Map<Integer, Object> values;

    public FakeLoggablePreparedStatement(String s) {
        command = s;
        values = new HashMap<Integer, Object>();
    }

    @Override
    public String toString() {
        String toReturn = command;
        for (Integer i : values.keySet()) {
            if (String.class.isAssignableFrom(values.get(i).getClass())) {
                toReturn = toReturn.replaceFirst("\\?", "'" + values.get(i).toString() + "'");
            } else if (Date.class.isAssignableFrom(values.get(i).getClass())) {
                toReturn = toReturn.replaceFirst("\\?", "'" + values.get(i).toString() + "'");
            } else if (java.util.Date.class.isAssignableFrom(values.get(i).getClass())) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                toReturn = toReturn.replaceFirst("\\?", "'" + sdf.format(values.get(i)) + "'");
            } else {
                toReturn = toReturn.replaceFirst("\\?", values.get(i).toString());
            }
        }
        return toReturn;
    }

    public ResultSet executeQuery() throws SQLException {
        throw new SQLException("Not implemented in FakeLoggablePreparedStatement");
    }

    public int executeUpdate() throws SQLException {
        throw new SQLException("Not implemented in FakeLoggablePreparedStatement");
    }

    public void setNull(int i, int i1) throws SQLException {
        values.put(i, null);
    }

    public void setBoolean(int i, boolean b) throws SQLException {
        values.put(i, b);
    }

    public void setByte(int i, byte b) throws SQLException {
        values.put(i, b);
    }

    public void setShort(int i, short i1) throws SQLException {
        values.put(i, i1);
    }

    public void setInt(int i, int i1) throws SQLException {
        values.put(i, i1);
    }

    public void setLong(int i, long l) throws SQLException {
        values.put(i, l);
    }

    public void setFloat(int i, float v) throws SQLException {
        values.put(i, v);
    }

    public void setDouble(int i, double v) throws SQLException {
        values.put(i, v);
    }

    public void setBigDecimal(int i, BigDecimal bigDecimal) throws SQLException {
        values.put(i, bigDecimal);
    }

    public void setString(int i, String s) throws SQLException {
        values.put(i, s);
    }

    public void setBytes(int i, byte[] bytes) throws SQLException {
        values.put(i, bytes);
    }

    public void setDate(int i, Date date) throws SQLException {
        values.put(i, date);
    }

    public void setTime(int i, Time time) throws SQLException {
        values.put(i, time);
    }

    public void setTimestamp(int i, Timestamp timestamp) throws SQLException {
        values.put(i, timestamp);
    }

    public void setAsciiStream(int i, InputStream inputStream, int i1) throws SQLException {
        values.put(i, "ASCIIStream");
    }

    @Deprecated
    public void setUnicodeStream(int i, InputStream inputStream, int i1) throws SQLException {
        values.put(i, "UnicodeStream");
    }

    public void setBinaryStream(int i, InputStream inputStream, int i1) throws SQLException {
        values.put(i, "BinaryStream");
    }

    public void clearParameters() throws SQLException {
        values.clear();
    }

    public void setObject(int i, Object o, int i1) throws SQLException {
        values.put(i, o);
    }

    public void setObject(int i, Object o) throws SQLException {
        values.put(i, o);
    }

    public boolean execute() throws SQLException {
        throw new SQLException("Not implemented in FakeLoggablePreparedStatement");
    }

    public void addBatch() throws SQLException {
    }

    public void setCharacterStream(int i, Reader reader, int i1) throws SQLException {
        values.put(i, "CharacterStream");
    }

    public void setRef(int i, Ref ref) throws SQLException {
        values.put(i, ref);
    }

    public void setBlob(int i, Blob blob) throws SQLException {
        values.put(i, "Blob");
    }

    public void setClob(int i, Clob clob) throws SQLException {
        values.put(i, "Clob");
    }

    public void setArray(int i, Array array) throws SQLException {
        values.put(i, array);
    }

    public ResultSetMetaData getMetaData() throws SQLException {
        return null;
    }

    public void setDate(int i, Date date, Calendar calendar) throws SQLException {
        values.put(i, date);
    }

    public void setTime(int i, Time time, Calendar calendar) throws SQLException {
        values.put(i, time);
    }

    public void setTimestamp(int i, Timestamp timestamp, Calendar calendar) throws SQLException {
        values.put(i, timestamp);
    }

    public void setNull(int i, int i1, String s) throws SQLException {
        values.put(i, null);
    }

    public void setURL(int i, URL url) throws SQLException {
        values.put(i, url);
    }

    public ParameterMetaData getParameterMetaData() throws SQLException {
        return null;
    }

    public void setRowId(int i, RowId rowId) throws SQLException {
        values.put(i, rowId);
    }

    public void setNString(int i, String s) throws SQLException {
        values.put(i, s);
    }

    public void setNCharacterStream(int i, Reader reader, long l) throws SQLException {
        values.put(i, "NCharacterStream");
    }

    public void setNClob(int i, NClob nClob) throws SQLException {
        values.put(i, "NClob");
    }

    public void setClob(int i, Reader reader, long l) throws SQLException {
        values.put(i, "Clob");
    }

    public void setBlob(int i, InputStream inputStream, long l) throws SQLException {
        values.put(i, "Blob");
    }

    public void setNClob(int i, Reader reader, long l) throws SQLException {
        values.put(i, "NClob");
    }

    public void setSQLXML(int i, SQLXML sqlxml) throws SQLException {
        values.put(i, sqlxml);
    }

    public void setObject(int i, Object o, int i1, int i2) throws SQLException {
        values.put(i, o);
    }

    public void setAsciiStream(int i, InputStream inputStream, long l) throws SQLException {
        values.put(i, "AsciiStream");
    }

    public void setBinaryStream(int i, InputStream inputStream, long l) throws SQLException {
        values.put(i, "BinaryStream");
    }

    public void setCharacterStream(int i, Reader reader, long l) throws SQLException {
        values.put(i, "CharacterStream");
    }

    public void setAsciiStream(int i, InputStream inputStream) throws SQLException {
        values.put(i, "ASCIIStream");
    }

    public void setBinaryStream(int i, InputStream inputStream) throws SQLException {
        values.put(i, "BinaryStream");
    }

    public void setCharacterStream(int i, Reader reader) throws SQLException {
        values.put(i, "CharacterStream");
    }

    public void setNCharacterStream(int i, Reader reader) throws SQLException {
        values.put(i, "NCharacterStream");
    }

    public void setClob(int i, Reader reader) throws SQLException {
        values.put(i, "Clob");
    }

    public void setBlob(int i, InputStream inputStream) throws SQLException {
        values.put(i, "Blob");
    }

    public void setNClob(int i, Reader reader) throws SQLException {
        values.put(i, "NClob");
    }

    public ResultSet executeQuery(String s) throws SQLException {
        throw new SQLException("Not implemented in FakeLoggablePreparedStatement");
    }

    public int executeUpdate(String s) throws SQLException {
        throw new SQLException("Not implemented in FakeLoggablePreparedStatement");
    }

    public void close() throws SQLException {
    }

    public int getMaxFieldSize() throws SQLException {
        return 0;
    }

    public void setMaxFieldSize(int i) throws SQLException {
    }

    public int getMaxRows() throws SQLException {
        return 0;
    }

    public void setMaxRows(int i) throws SQLException {
    }

    public void setEscapeProcessing(boolean b) throws SQLException {
    }

    public int getQueryTimeout() throws SQLException {
        return 0;
    }

    public void setQueryTimeout(int i) throws SQLException {
    }

    public void cancel() throws SQLException {
    }

    public SQLWarning getWarnings() throws SQLException {
        return null;
    }

    public void clearWarnings() throws SQLException {
    }

    public void setCursorName(String s) throws SQLException {
    }

    public boolean execute(String s) throws SQLException {
        throw new SQLException("Not implemented in FakeLoggablePreparedStatement");
    }

    public ResultSet getResultSet() throws SQLException {
        return null;
    }

    public int getUpdateCount() throws SQLException {
        return 0;
    }

    public boolean getMoreResults() throws SQLException {
        return false;
    }

    public void setFetchDirection(int i) throws SQLException {
    }

    public int getFetchDirection() throws SQLException {
        return 0;
    }

    public void setFetchSize(int i) throws SQLException {
    }

    public int getFetchSize() throws SQLException {
        return 0;
    }

    public int getResultSetConcurrency() throws SQLException {
        return 0;
    }

    public int getResultSetType() throws SQLException {
        return 0;
    }

    public void addBatch(String s) throws SQLException {
    }

    public void clearBatch() throws SQLException {
    }

    public int[] executeBatch() throws SQLException {
        throw new SQLException("Not implemented in FakeLoggablePreparedStatement");
    }

    public Connection getConnection() throws SQLException {
        return null;
    }

    public boolean getMoreResults(int i) throws SQLException {
        return false;
    }

    public ResultSet getGeneratedKeys() throws SQLException {
        return null;
    }

    public int executeUpdate(String s, int i) throws SQLException {
        throw new SQLException("Not implemented in FakeLoggablePreparedStatement");
    }

    public int executeUpdate(String s, int[] ints) throws SQLException {
        throw new SQLException("Not implemented in FakeLoggablePreparedStatement");
    }

    public int executeUpdate(String s, String[] strings) throws SQLException {
        throw new SQLException("Not implemented in FakeLoggablePreparedStatement");
    }

    public boolean execute(String s, int i) throws SQLException {
        throw new SQLException("Not implemented in FakeLoggablePreparedStatement");
    }

    public boolean execute(String s, int[] ints) throws SQLException {
        throw new SQLException("Not implemented in FakeLoggablePreparedStatement");
    }

    public boolean execute(String s, String[] strings) throws SQLException {
        throw new SQLException("Not implemented in FakeLoggablePreparedStatement");
    }

    public int getResultSetHoldability() throws SQLException {
        return 0;
    }

    public boolean isClosed() throws SQLException {
        return false;
    }

    public void setPoolable(boolean b) throws SQLException {
    }

    public boolean isPoolable() throws SQLException {
        return true;
    }

    public void closeOnCompletion() throws SQLException {
    }

    public boolean isCloseOnCompletion() throws SQLException {
        return false;
    }

    public <T> T unwrap(Class<T> aClass) throws SQLException {
        return null;
    }

    public boolean isWrapperFor(Class<?> aClass) throws SQLException {
        return false;
    }
}
