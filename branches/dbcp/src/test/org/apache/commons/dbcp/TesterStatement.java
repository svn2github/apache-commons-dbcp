/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//dbcp/src/test/org/apache/commons/dbcp/TesterStatement.java,v 1.1 2001/04/14 17:16:42 rwaldhoff Exp $
 * $Revision: 1.1 $
 * $Date: 2001/04/14 17:16:42 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2001 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */

package org.apache.commons.dbcp;

import java.sql.*;
import java.util.Calendar;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;

public class TesterStatement implements Statement {
    public TesterStatement(Connection conn) {
        _connection = conn;
    }
    protected Connection _connection = null;
    protected boolean _open = true;
    protected int _rowsUpdated = 1;
    protected boolean _executeResponse = true;
    protected int _maxFieldSize = 1024;
    protected int _maxRows = 1024;
    protected boolean _escapeProcessing = false;
    protected int _queryTimeout = 1000;
    protected String _cursorName = null;
    protected int _fetchDirection = 1;
    protected int _fetchSize = 1;
    protected int _resultSetConcurrency = 1;
    protected int _resultSetType = 1;

    public ResultSet executeQuery(String sql) throws SQLException {
        checkOpen();
        return new TesterResultSet(this);
    }

    public int executeUpdate(String sql) throws SQLException {
        checkOpen();
        return _rowsUpdated;
    }

    public void close() throws SQLException {
        _open = false;
    }

    public int getMaxFieldSize() throws SQLException {
        checkOpen();
        return _maxFieldSize;
    }

    public void setMaxFieldSize(int max) throws SQLException {
        checkOpen();
        _maxFieldSize = max;
    }

    public int getMaxRows() throws SQLException {
        checkOpen();
        return _maxRows;
    }

    public void setMaxRows(int max) throws SQLException {
        checkOpen();
        _maxRows = max;
    }

    public void setEscapeProcessing(boolean enable) throws SQLException {
        checkOpen();
        _escapeProcessing = enable;
    }

    public int getQueryTimeout() throws SQLException {
        checkOpen();
        return _queryTimeout;
    }

    public void setQueryTimeout(int seconds) throws SQLException {
        checkOpen();
        _queryTimeout = seconds;
    }

    public void cancel() throws SQLException {
        checkOpen();
    }

    public SQLWarning getWarnings() throws SQLException {
        checkOpen();
        return null;
    }

    public void clearWarnings() throws SQLException {
        checkOpen();
    }

    public void setCursorName(String name) throws SQLException {
        checkOpen();
        _cursorName = name;
    }

    public boolean execute(String sql) throws SQLException {
        checkOpen();
        return _executeResponse;
    }

    public ResultSet getResultSet() throws SQLException {
        checkOpen();
        return new TesterResultSet(this);
    }

    public int getUpdateCount() throws SQLException {
        checkOpen();
        return _rowsUpdated;
    }

    public boolean getMoreResults() throws SQLException {
        checkOpen();
        return false;
    }

    public void setFetchDirection(int direction) throws SQLException {
        checkOpen();
        _fetchDirection = direction;
    }

    public int getFetchDirection() throws SQLException {
        checkOpen();
        return _fetchDirection;
    }

    public void setFetchSize(int rows) throws SQLException {
        checkOpen();
        _fetchSize = rows;
    }

    public int getFetchSize() throws SQLException {
        checkOpen();
        return _fetchSize;
    }

    public int getResultSetConcurrency() throws SQLException {
        checkOpen();
        return _resultSetConcurrency;
    }

    public int getResultSetType() throws SQLException {
        checkOpen();
        return _resultSetType;
    }

    public void addBatch(String sql) throws SQLException {
        checkOpen();
    }

    public void clearBatch() throws SQLException {
        checkOpen();
    }

    public int[] executeBatch() throws SQLException {
        checkOpen();
        return new int[0];
    }

    public Connection getConnection() throws SQLException {
        checkOpen();
        return _connection;
    }

    protected void checkOpen() throws SQLException {
        if(!_open) {
            throw new SQLException("Connection is closed.");
        }
    }
}
