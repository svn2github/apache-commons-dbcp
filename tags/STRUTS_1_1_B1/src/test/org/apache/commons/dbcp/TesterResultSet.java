package org.apache.commons.dbcp;

import java.sql.*;
import java.math.BigDecimal;
import java.util.Calendar;

public class TesterResultSet implements ResultSet {
    public TesterResultSet(Statement stmt) {
        _statement = stmt;
    }

    protected Statement _statement = null;
    protected int _rowsLeft = 2;
    protected boolean _open = true;

    public boolean next() throws SQLException {
        checkOpen();
        if(--_rowsLeft > 0) {
            return true;
        } else {
            return false;
        }
    }

    public void close() throws SQLException {
        _open = false;
    }

    public boolean wasNull() throws SQLException {
        checkOpen();
        return false;
    }

    public String getString(int columnIndex) throws SQLException {
        checkOpen();
        return "String" + columnIndex;
    }

    public boolean getBoolean(int columnIndex) throws SQLException {
        checkOpen();
        return true;
    }

    public byte getByte(int columnIndex) throws SQLException {
        checkOpen();
        return (byte)columnIndex;
    }

    public short getShort(int columnIndex) throws SQLException {
        checkOpen();
        return (short)columnIndex;
    }

    public int getInt(int columnIndex) throws SQLException {
        checkOpen();
        return (short)columnIndex;
    }

    public long getLong(int columnIndex) throws SQLException {
        checkOpen();
        return (long)columnIndex;
    }

    public float getFloat(int columnIndex) throws SQLException {
        checkOpen();
        return (float)columnIndex;
    }

    public double getDouble(int columnIndex) throws SQLException {
        checkOpen();
        return (double)columnIndex;
    }

    public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
        checkOpen();
        return new BigDecimal((double)columnIndex);
    }

    public byte[] getBytes(int columnIndex) throws SQLException {
        checkOpen();
        return new byte[] { (byte)columnIndex };
    }

    public java.sql.Date getDate(int columnIndex) throws SQLException {
        checkOpen();
        return null;
    }

    public java.sql.Time getTime(int columnIndex) throws SQLException {
        checkOpen();
        return null;
    }

    public java.sql.Timestamp getTimestamp(int columnIndex) throws SQLException {
        checkOpen();
        return null;
    }

    public java.io.InputStream getAsciiStream(int columnIndex) throws SQLException {
        checkOpen();
        return null;
    }

    public java.io.InputStream getUnicodeStream(int columnIndex) throws SQLException {
        checkOpen();
        return null;
    }

    public java.io.InputStream getBinaryStream(int columnIndex) throws SQLException {
        checkOpen();
        return null;
    }

    public String getString(String columnName) throws SQLException {
        checkOpen();
        return columnName;
    }

    public boolean getBoolean(String columnName) throws SQLException {
        checkOpen();
        return true;
    }

    public byte getByte(String columnName) throws SQLException {
        checkOpen();
        return (byte)(columnName.hashCode());
    }

    public short getShort(String columnName) throws SQLException {
        checkOpen();
        return (short)(columnName.hashCode());
    }

    public int getInt(String columnName) throws SQLException {
        checkOpen();
        return (columnName.hashCode());
    }

    public long getLong(String columnName) throws SQLException {
        checkOpen();
        return (long)(columnName.hashCode());
    }

    public float getFloat(String columnName) throws SQLException {
        checkOpen();
        return (float)(columnName.hashCode());
    }

    public double getDouble(String columnName) throws SQLException {
        checkOpen();
        return (double)(columnName.hashCode());
    }

    public BigDecimal getBigDecimal(String columnName, int scale) throws SQLException {
        checkOpen();
        return new BigDecimal((double)columnName.hashCode());
    }

    public byte[] getBytes(String columnName) throws SQLException {
        checkOpen();
        return columnName.getBytes();
    }

    public java.sql.Date getDate(String columnName) throws SQLException {
        checkOpen();
        return null;
    }

    public java.sql.Time getTime(String columnName) throws SQLException {
        checkOpen();
        return null;
    }

    public java.sql.Timestamp getTimestamp(String columnName) throws SQLException {
        checkOpen();
        return null;
    }

    public java.io.InputStream getAsciiStream(String columnName) throws SQLException {
        checkOpen();
        return null;
    }

    public java.io.InputStream getUnicodeStream(String columnName) throws SQLException {
        checkOpen();
        return null;
    }

    public java.io.InputStream getBinaryStream(String columnName) throws SQLException {
        checkOpen();
        return null;
    }

   public SQLWarning getWarnings() throws SQLException {
       checkOpen();
       return null;
   }

    public void clearWarnings() throws SQLException {
        checkOpen();
    }

    public String getCursorName() throws SQLException {
        checkOpen();
        return null;
    }

    public ResultSetMetaData getMetaData() throws SQLException {
        checkOpen();
        return null;
    }

    public Object getObject(int columnIndex) throws SQLException {
        checkOpen();
        return new Object();
    }

    public Object getObject(String columnName) throws SQLException {
        checkOpen();
        return columnName;
    }

    public int findColumn(String columnName) throws SQLException {
        checkOpen();
        return 1;
    }


    public java.io.Reader getCharacterStream(int columnIndex) throws SQLException {
        checkOpen();
        return null;
    }

    public java.io.Reader getCharacterStream(String columnName) throws SQLException {
        checkOpen();
        return null;
    }

    public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
        checkOpen();
        return new BigDecimal((double)columnIndex);
    }

    public BigDecimal getBigDecimal(String columnName) throws SQLException {
        checkOpen();
        return new BigDecimal((double)columnName.hashCode());
    }

    public boolean isBeforeFirst() throws SQLException {
        checkOpen();
        return _rowsLeft == 2;
    }

    public boolean isAfterLast() throws SQLException {
        checkOpen();
        return _rowsLeft < 0;
    }

    public boolean isFirst() throws SQLException {
        checkOpen();
        return _rowsLeft == 1;
    }

    public boolean isLast() throws SQLException {
        checkOpen();
        return _rowsLeft == 0;
    }

    public void beforeFirst() throws SQLException {
        checkOpen();
    }

    public void afterLast() throws SQLException {
        checkOpen();
    }

    public boolean first() throws SQLException {
        checkOpen();
        return false;
    }

    public boolean last() throws SQLException {
        checkOpen();
        return false;
    }

    public int getRow() throws SQLException {
        checkOpen();
        return 3 - _rowsLeft;
    }

    public boolean absolute( int row ) throws SQLException {
        checkOpen();
        return false;
    }

    public boolean relative( int rows ) throws SQLException {
        checkOpen();
        return false;
    }

    public boolean previous() throws SQLException {
        checkOpen();
        return false;
    }

    public void setFetchDirection(int direction) throws SQLException {
        checkOpen();
    }

    public int getFetchDirection() throws SQLException {
        checkOpen();
        return 1;
    }

    public void setFetchSize(int rows) throws SQLException {
        checkOpen();
    }

    public int getFetchSize() throws SQLException {
        checkOpen();
        return 2;
    }

    public int getType() throws SQLException {
        return 1003;
    }

    public int getConcurrency() throws SQLException {
        return 1007;
    }

    public boolean rowUpdated() throws SQLException {
        checkOpen();
        return false;
    }

    public boolean rowInserted() throws SQLException {
        checkOpen();
        return false;
    }

    public boolean rowDeleted() throws SQLException {
        checkOpen();
        return false;
    }

    public void updateNull(int columnIndex) throws SQLException {
        checkOpen();
    }

    public void updateBoolean(int columnIndex, boolean x) throws SQLException {
        checkOpen();
    }

    public void updateByte(int columnIndex, byte x) throws SQLException {
        checkOpen();
    }

    public void updateShort(int columnIndex, short x) throws SQLException {
        checkOpen();
    }

    public void updateInt(int columnIndex, int x) throws SQLException {
        checkOpen();
    }

    public void updateLong(int columnIndex, long x) throws SQLException {
        checkOpen();
    }

    public void updateFloat(int columnIndex, float x) throws SQLException {
        checkOpen();
    }

    public void updateDouble(int columnIndex, double x) throws SQLException {
        checkOpen();
    }

    public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {
        checkOpen();
    }

    public void updateString(int columnIndex, String x) throws SQLException {
        checkOpen();
    }

    public void updateBytes(int columnIndex, byte x[]) throws SQLException {
        checkOpen();
    }

    public void updateDate(int columnIndex, java.sql.Date x) throws SQLException {
        checkOpen();
    }

    public void updateTime(int columnIndex, java.sql.Time x) throws SQLException {
        checkOpen();
    }

    public void updateTimestamp(int columnIndex, java.sql.Timestamp x) throws SQLException {
        checkOpen();
    }


    public void updateAsciiStream(int columnIndex,
			   java.io.InputStream x,
			   int length) throws SQLException {
        checkOpen();
    }

    public void updateBinaryStream(int columnIndex,
			    java.io.InputStream x,
			    int length) throws SQLException {
        checkOpen();
    }

    public void updateCharacterStream(int columnIndex,
			     java.io.Reader x,
			     int length) throws SQLException {
        checkOpen();
    }

    public void updateObject(int columnIndex, Object x, int scale)
      throws SQLException {
        checkOpen();
    }

    public void updateObject(int columnIndex, Object x) throws SQLException {
        checkOpen();
    }

    public void updateNull(String columnName) throws SQLException {
        checkOpen();
    }

    public void updateBoolean(String columnName, boolean x) throws SQLException {
        checkOpen();
    }

    public void updateByte(String columnName, byte x) throws SQLException {
        checkOpen();
    }

    public void updateShort(String columnName, short x) throws SQLException {
        checkOpen();
    }

    public void updateInt(String columnName, int x) throws SQLException {
        checkOpen();
    }

    public void updateLong(String columnName, long x) throws SQLException {
        checkOpen();
    }

    public void updateFloat(String columnName, float x) throws SQLException {
        checkOpen();
    }

    public void updateDouble(String columnName, double x) throws SQLException {
        checkOpen();
    }

    public void updateBigDecimal(String columnName, BigDecimal x) throws SQLException {
        checkOpen();
    }

    public void updateString(String columnName, String x) throws SQLException {
        checkOpen();
    }

    public void updateBytes(String columnName, byte x[]) throws SQLException {
        checkOpen();
    }

    public void updateDate(String columnName, java.sql.Date x) throws SQLException {
        checkOpen();
    }

    public void updateTime(String columnName, java.sql.Time x) throws SQLException {
        checkOpen();
    }

    public void updateTimestamp(String columnName, java.sql.Timestamp x)
      throws SQLException {
        checkOpen();
    }

    public void updateAsciiStream(String columnName,
			   java.io.InputStream x,
			   int length) throws SQLException {
        checkOpen();
    }

    public void updateBinaryStream(String columnName,
			    java.io.InputStream x,
			    int length) throws SQLException {
        checkOpen();
    }

    public void updateCharacterStream(String columnName,
			     java.io.Reader reader,
			     int length) throws SQLException {
        checkOpen();
    }

    public void updateObject(String columnName, Object x, int scale)
      throws SQLException {
        checkOpen();
    }

    public void updateObject(String columnName, Object x) throws SQLException {
        checkOpen();
    }

    public void insertRow() throws SQLException {
        checkOpen();
    }

    public void updateRow() throws SQLException {
        checkOpen();
    }

    public void deleteRow() throws SQLException {
        checkOpen();
    }

    public void refreshRow() throws SQLException {
        checkOpen();
    }

    public void cancelRowUpdates() throws SQLException {
        checkOpen();
    }

    public void moveToInsertRow() throws SQLException {
        checkOpen();
    }

    public void moveToCurrentRow() throws SQLException {
        checkOpen();
    }

    public Statement getStatement() throws SQLException {
        checkOpen();
        return _statement;
    }


    public Object getObject(int i, java.util.Map map) throws SQLException {
        checkOpen();
        return new Object();
    }

    public Ref getRef(int i) throws SQLException {
        checkOpen();
        return null;
    }

    public Blob getBlob(int i) throws SQLException {
        checkOpen();
        return null;
    }

    public Clob getClob(int i) throws SQLException {
        checkOpen();
        return null;
    }

    public Array getArray(int i) throws SQLException {
        checkOpen();
        return null;
    }

    public Object getObject(String colName, java.util.Map map) throws SQLException {
        checkOpen();
        return colName;
    }

    public Ref getRef(String colName) throws SQLException {
        checkOpen();
        return null;
    }

    public Blob getBlob(String colName) throws SQLException {
        checkOpen();
        return null;
    }

    public Clob getClob(String colName) throws SQLException {
        checkOpen();
        return null;
    }

    public Array getArray(String colName) throws SQLException {
        checkOpen();
        return null;
    }

    public java.sql.Date getDate(int columnIndex, Calendar cal) throws SQLException {
        checkOpen();
        return null;
    }

    public java.sql.Date getDate(String columnName, Calendar cal) throws SQLException {
        checkOpen();
        return null;
    }

    public java.sql.Time getTime(int columnIndex, Calendar cal) throws SQLException {
        checkOpen();
        return null;
    }

    public java.sql.Time getTime(String columnName, Calendar cal) throws SQLException {
        checkOpen();
        return null;
    }

    public java.sql.Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
        checkOpen();
        return null;
    }

    public java.sql.Timestamp getTimestamp(String columnName, Calendar cal)
      throws SQLException {
        checkOpen();
        return null;
    }

    protected void checkOpen() throws SQLException {
        if(!_open) {
            throw new SQLException("Connection is closed.");
        }
    }

    // ------------------- JDBC 3.0 -----------------------------------------
    // Will be uncommented by the build process on a JDBC 3.0 system

/* JDBC_3_ANT_KEY

    public java.net.URL getURL(int columnIndex) throws SQLException {
        throw new SQLException("Not implemented.");
    }

    public java.net.URL getURL(String columnName) throws SQLException {
        throw new SQLException("Not implemented.");
    }

    public void updateRef(int columnIndex, java.sql.Ref x)
        throws SQLException {
        throw new SQLException("Not implemented.");
    }

    public void updateRef(String columnName, java.sql.Ref x)
        throws SQLException {
        throw new SQLException("Not implemented.");
    }

    public void updateBlob(int columnIndex, java.sql.Blob x)
        throws SQLException {
        throw new SQLException("Not implemented.");
    }

    public void updateBlob(String columnName, java.sql.Blob x)
        throws SQLException {
        throw new SQLException("Not implemented.");
    }

    public void updateClob(int columnIndex, java.sql.Clob x)
        throws SQLException {
        throw new SQLException("Not implemented.");
    }

    public void updateClob(String columnName, java.sql.Clob x)
        throws SQLException {
        throw new SQLException("Not implemented.");
    }

    public void updateArray(int columnIndex, java.sql.Array x)
        throws SQLException {
        throw new SQLException("Not implemented.");
    }

    public void updateArray(String columnName, java.sql.Array x)
        throws SQLException {
        throw new SQLException("Not implemented.");
    }

JDBC_3_ANT_KEY */

}
