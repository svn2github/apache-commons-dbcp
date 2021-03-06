/*
 * $Source: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//dbcp/src/test/org/apache/commons/dbcp/TesterConnection.java,v $
 * $Revision: 1.7 $
 * $Date: 2003/08/22 16:08:32 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2003 The Apache Software Foundation.  All rights
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
 *    any, must include the following acknowledgement:
 *       "This product includes software developed by the
 *        Apache Software Foundation - http://www.apache.org/"
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
 * http://www.apache.org/
 *
 */

package org.apache.commons.dbcp;

import java.sql.*;
import java.util.Map;

/**
 * A dummy {@link Connection}, for testing purposes.
 * @author Rodney Waldhoff
 * @version $Id: TesterConnection.java,v 1.7 2003/08/22 16:08:32 dirkv Exp $
 */
public class TesterConnection implements Connection {
    protected boolean _open = true;
    protected boolean _autoCommit = true;
    protected int _transactionIsolation = 1;
    protected DatabaseMetaData _metaData = null;
    protected String _catalog = null;
    protected Map _typeMap = null;
    protected boolean _readOnly = false;
    protected SQLWarning warnings = null;

    public void setWarnings(SQLWarning warning) {
        this.warnings = warning;
    }

    public void clearWarnings() throws SQLException {
        checkOpen();
        warnings = null;
    }

    public void close() throws SQLException {
        checkOpen();
        _open = false;
    }

    public void commit() throws SQLException {
        checkOpen();
    }

    public Statement createStatement() throws SQLException {
        checkOpen();
        return new TesterStatement(this);
    }

    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        checkOpen();
        return new TesterStatement(this);
    }

    public boolean getAutoCommit() throws SQLException {
        checkOpen();
        return _autoCommit;
    }

    public String getCatalog() throws SQLException {
        checkOpen();
        return _catalog;
    }

    public DatabaseMetaData getMetaData() throws SQLException {
        checkOpen();
        return _metaData;
    }

    public int getTransactionIsolation() throws SQLException {
        checkOpen();
        return _transactionIsolation;
    }

    public Map getTypeMap() throws SQLException {
        checkOpen();
        return _typeMap;
    }

    public SQLWarning getWarnings() throws SQLException {
        checkOpen();
        return warnings;
    }

    public boolean isClosed() throws SQLException {
        return !_open;
    }

    public boolean isReadOnly() throws SQLException {
        checkOpen();
        return _readOnly;
    }

    public String nativeSQL(String sql) throws SQLException {
        checkOpen();
        return sql;
    }

    public CallableStatement prepareCall(String sql) throws SQLException {
        checkOpen();
        if ("warning".equals(sql)) {
            setWarnings(new SQLWarning("warning in prepareCall"));
        }
        return null;
    }

    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        checkOpen();
        return null;
    }

    public PreparedStatement prepareStatement(String sql) throws SQLException {
        checkOpen();
        return new TesterPreparedStatement(this, sql);
    }

    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        checkOpen();
        return new TesterPreparedStatement(this, sql);
    }

    public void rollback() throws SQLException {
        checkOpen();
    }

    public void setAutoCommit(boolean autoCommit) throws SQLException {
        checkOpen();
        _autoCommit = autoCommit;
    }

    public void setCatalog(String catalog) throws SQLException {
        checkOpen();
        _catalog = catalog;
    }

    public void setReadOnly(boolean readOnly) throws SQLException {
        checkOpen();
        _readOnly = readOnly;
    }

    public void setTransactionIsolation(int level) throws SQLException {
        checkOpen();
        _transactionIsolation = level;
    }

    public void setTypeMap(Map map) throws SQLException {
        checkOpen();
        _typeMap = map;
    }

    protected void checkOpen() throws SQLException {
        if(!_open) {
            throw new SQLException("Connection is closed.");
        }
    }
    // ------------------- JDBC 3.0 -----------------------------------------
    // Will be commented by the build process on a JDBC 2.0 system

/* JDBC_3_ANT_KEY_BEGIN */

    public int getHoldability() throws SQLException {
        throw new SQLException("Not implemented.");
    }

    public void setHoldability(int holdability) throws SQLException {
        throw new SQLException("Not implemented.");
    }

    public java.sql.Savepoint setSavepoint() throws SQLException {
        throw new SQLException("Not implemented.");
    }

    public java.sql.Savepoint setSavepoint(String name) throws SQLException {
        throw new SQLException("Not implemented.");
    }

    public void rollback(java.sql.Savepoint savepoint) throws SQLException {
        throw new SQLException("Not implemented.");
    }

    public void releaseSavepoint(java.sql.Savepoint savepoint) throws SQLException {
        throw new SQLException("Not implemented.");
    }

    public Statement createStatement(int resultSetType,
                                     int resultSetConcurrency,
                                     int resultSetHoldability)
        throws SQLException {
        throw new SQLException("Not implemented.");
    }

    public PreparedStatement prepareStatement(String sql, int resultSetType,
                                              int resultSetConcurrency,
                                              int resultSetHoldability)
        throws SQLException {
        throw new SQLException("Not implemented.");
    }

    public CallableStatement prepareCall(String sql, int resultSetType,
                                         int resultSetConcurrency,
                                         int resultSetHoldability)
        throws SQLException {
        throw new SQLException("Not implemented.");
    }

    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys)
        throws SQLException {
        throw new SQLException("Not implemented.");
    }

    public PreparedStatement prepareStatement(String sql, int columnIndexes[])
        throws SQLException {
        throw new SQLException("Not implemented.");
    }

    public PreparedStatement prepareStatement(String sql, String columnNames[])
        throws SQLException {
        throw new SQLException("Not implemented.");
    }

/* JDBC_3_ANT_KEY_END */
}
