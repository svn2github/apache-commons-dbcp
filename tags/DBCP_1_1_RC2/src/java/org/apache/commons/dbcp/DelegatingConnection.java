/*
 * $Source: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//dbcp/src/java/org/apache/commons/dbcp/DelegatingConnection.java,v $
 * $Revision: 1.16 $
 * $Date: 2003/10/09 21:04:44 $
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
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Software Foundation.
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

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

/**
 * A base delegating implementation of {@link Connection}.
 * <p>
 * All of the methods from the {@link Connection} interface
 * simply check to see that the {@link Connection} is active,
 * and call the corresponding method on the "delegate"
 * provided in my constructor.
 * <p>
 * Extends AbandonedTrace to implement Connection tracking and
 * logging of code which created the Connection. Tracking the
 * Connection ensures that the AbandonedObjectPool can close
 * this connection and recycle it if its pool of connections
 * is nearing exhaustion and this connection's last usage is
 * older than the removeAbandonedTimeout.
 *
 * @author Rodney Waldhoff
 * @author Glenn L. Nielsen
 * @author James House (<a href="mailto:james@interobjective.com">james@interobjective.com</a>)
 * @version $Id: DelegatingConnection.java,v 1.16 2003/10/09 21:04:44 rdonkin Exp $
 */
public class DelegatingConnection extends AbandonedTrace
        implements Connection {
    /** My delegate {@link Connection}. */
    protected Connection _conn = null;

    protected boolean _closed = false;

    /**
     * Create a wrapper for the Connectin which traces this
     * Connection in the AbandonedObjectPool.
     *
     * @param c the {@link Connection} to delegate all calls to.
     */
    public DelegatingConnection(Connection c) {
        super();
        _conn = c;
    }

    /**
     * Create a wrapper for the Connection which traces
     * the Statements created so that any unclosed Statements
     * can be closed when this Connection is closed.
     *
     * @param Connection the {@link Connection} to delegate all calls to.
     * @param AbandonedConfig the configuration for tracing abandoned objects
     * @deprecated AbandonedConfig is now deprecated.
     */
    public DelegatingConnection(Connection c, AbandonedConfig config) {
        super(config);
        _conn = c;
    }

    /**
     * Returns my underlying {@link Connection}.
     * @return my underlying {@link Connection}.
     */
    public Connection getDelegate() {
        return _conn;
    }

    public boolean equals(Object obj) {
        Connection delegate = getInnermostDelegate();
        if (delegate == null) {
            return false;
        }
        if (obj instanceof DelegatingConnection) {
            DelegatingConnection c = (DelegatingConnection) obj;
            return delegate.equals(c.getInnermostDelegate());
        }
        else {
            return delegate.equals(obj);
        }
    }

    public int hashCode() {
        Object obj = getInnermostDelegate();
        if (obj == null) {
            return 0;
        }
        return obj.hashCode();
    }


    /**
     * If my underlying {@link Connection} is not a
     * <tt>DelegatingConnection</tt>, returns it,
     * otherwise recursively invokes this method on
     * my delegate.
     * <p>
     * Hence this method will return the first
     * delegate that is not a <tt>DelegatingConnection</tt>,
     * or <tt>null</tt> when no non-<tt>DelegatingConnection</tt>
     * delegate can be found by transversing this chain.
     * <p>
     * This method is useful when you may have nested
     * <tt>DelegatingConnection</tt>s, and you want to make
     * sure to obtain a "genuine" {@link Connection}.
     */
    public Connection getInnermostDelegate() {
        Connection c = _conn;
        while(c != null && c instanceof DelegatingConnection) {
            c = ((DelegatingConnection)c).getDelegate();
            if(this == c) {
                return null;
            }
        }
        return c;
    }

    /** Sets my delegate. */
    public void setDelegate(Connection c) {
        _conn = c;
    }

    /**
     * Closes the underlying connection, and close
     * any Statements that were not explicitly closed.
     */
    public void close() throws SQLException
    {
        passivate();
        _conn.close();
    }

    public Statement createStatement() throws SQLException {
        checkOpen();

        return new DelegatingStatement(this, _conn.createStatement());
    }

    public Statement createStatement(int resultSetType,
                                     int resultSetConcurrency)
            throws SQLException {
        checkOpen();

        return new DelegatingStatement
            (this, _conn.createStatement(resultSetType,resultSetConcurrency));
    }

    public PreparedStatement prepareStatement(String sql) throws SQLException {
        checkOpen();

        return new DelegatingPreparedStatement
            (this, _conn.prepareStatement(sql));
    }

    public PreparedStatement prepareStatement(String sql,
                                              int resultSetType,
                                              int resultSetConcurrency)
            throws SQLException {
        checkOpen();

        return new DelegatingPreparedStatement
            (this, _conn.prepareStatement
                (sql,resultSetType,resultSetConcurrency));
    }

    public CallableStatement prepareCall(String sql) throws SQLException {
        checkOpen();

        return new DelegatingCallableStatement(this, _conn.prepareCall(sql));
    }

    public CallableStatement prepareCall(String sql,
                                         int resultSetType,
                                         int resultSetConcurrency)
            throws SQLException {
        checkOpen();

        return new DelegatingCallableStatement
            (this, _conn.prepareCall(sql, resultSetType,resultSetConcurrency));
    }

    public void clearWarnings() throws SQLException { checkOpen(); _conn.clearWarnings();}
    public void commit() throws SQLException { checkOpen(); _conn.commit();}
    public boolean getAutoCommit() throws SQLException { checkOpen(); return _conn.getAutoCommit();}
    public String getCatalog() throws SQLException { checkOpen(); return _conn.getCatalog();}
    public DatabaseMetaData getMetaData() throws SQLException { checkOpen(); return _conn.getMetaData();}
    public int getTransactionIsolation() throws SQLException { checkOpen(); return _conn.getTransactionIsolation();}
    public Map getTypeMap() throws SQLException { checkOpen(); return _conn.getTypeMap();}
    public SQLWarning getWarnings() throws SQLException { checkOpen(); return _conn.getWarnings();}

    public boolean isClosed() throws SQLException {
         if(_closed || _conn.isClosed()) {
             return true;
         }
         return false;
    }

    public boolean isReadOnly() throws SQLException { checkOpen(); return _conn.isReadOnly();}
    public String nativeSQL(String sql) throws SQLException { checkOpen(); return _conn.nativeSQL(sql);}
    public void rollback() throws SQLException { checkOpen(); _conn.rollback();}
    public void setAutoCommit(boolean autoCommit) throws SQLException { checkOpen(); _conn.setAutoCommit(autoCommit);}
    public void setCatalog(String catalog) throws SQLException { checkOpen(); _conn.setCatalog(catalog);}
    public void setReadOnly(boolean readOnly) throws SQLException { checkOpen(); _conn.setReadOnly(readOnly);}
    public void setTransactionIsolation(int level) throws SQLException { checkOpen(); _conn.setTransactionIsolation(level);}
    public void setTypeMap(Map map) throws SQLException { checkOpen(); _conn.setTypeMap(map);}

    protected void checkOpen() throws SQLException {
        if(_closed) {
            throw new SQLException("Connection is closed.");
        }
    }

    protected void activate() {
        _closed = false;
        setLastUsed();
        if(_conn instanceof DelegatingConnection) {
            ((DelegatingConnection)_conn).activate();
        }
    }

    protected void passivate() throws SQLException {
        _closed = true;
        // The JDBC spec requires that a Connection close any open
        // Statement's when it is closed.
        List statements = getTrace();
        if( statements != null) {
            Statement[] set = new Statement[statements.size()];
            statements.toArray(set);
            for (int i = 0; i < set.length; i++) {
                set[i].close();
            }
            clearTrace();
        }
        setLastUsed(0);
        if(_conn instanceof DelegatingConnection) {
            ((DelegatingConnection)_conn).passivate();
        }
    }

    // ------------------- JDBC 3.0 -----------------------------------------
    // Will be commented by the build process on a JDBC 2.0 system

/* JDBC_3_ANT_KEY_BEGIN */

    public int getHoldability() throws SQLException {
        checkOpen();
        return _conn.getHoldability();
    }

    public void setHoldability(int holdability) throws SQLException {
        checkOpen();
        _conn.setHoldability(holdability);
    }

    public java.sql.Savepoint setSavepoint() throws SQLException {
        checkOpen();
        return _conn.setSavepoint();
    }

    public java.sql.Savepoint setSavepoint(String name) throws SQLException {
        checkOpen();
        return _conn.setSavepoint(name);
    }

    public void rollback(java.sql.Savepoint savepoint) throws SQLException {
        checkOpen();
        _conn.rollback(savepoint);
    }

    public void releaseSavepoint(java.sql.Savepoint savepoint) throws SQLException {
        checkOpen();
        _conn.releaseSavepoint(savepoint);
    }

    public Statement createStatement(int resultSetType,
                                     int resultSetConcurrency,
                                     int resultSetHoldability)
        throws SQLException {
        checkOpen();
        return new DelegatingStatement(this, _conn.createStatement(
            resultSetType, resultSetConcurrency, resultSetHoldability));
    }

    public PreparedStatement prepareStatement(String sql, int resultSetType,
                                              int resultSetConcurrency,
                                              int resultSetHoldability)
        throws SQLException {
        checkOpen();
        return new DelegatingPreparedStatement(this, _conn.prepareStatement(
            sql, resultSetType, resultSetConcurrency, resultSetHoldability));
    }

    public CallableStatement prepareCall(String sql, int resultSetType,
                                         int resultSetConcurrency,
                                         int resultSetHoldability)
        throws SQLException {
        checkOpen();
        return new DelegatingCallableStatement(this, _conn.prepareCall(
            sql, resultSetType, resultSetConcurrency, resultSetHoldability));
    }

    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys)
        throws SQLException {
        checkOpen();
        return new DelegatingPreparedStatement(this, _conn.prepareStatement(
            sql, autoGeneratedKeys));
    }

    public PreparedStatement prepareStatement(String sql, int columnIndexes[])
        throws SQLException {
        checkOpen();
        return new DelegatingPreparedStatement(this, _conn.prepareStatement(
            sql, columnIndexes));
    }

    public PreparedStatement prepareStatement(String sql, String columnNames[])
        throws SQLException {
        checkOpen();
        return new DelegatingPreparedStatement(this, _conn.prepareStatement(
            sql, columnNames));
    }

/* JDBC_3_ANT_KEY_END */

}
