/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//dbcp/src/java/org/apache/commons/dbcp/PoolableConnectionFactory.java,v 1.7 2003/04/09 00:19:37 dgraham Exp $
 * $Revision: 1.7 $
 * $Date: 2003/04/09 00:19:37 $
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

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.commons.pool.*;

/**
 * A {@link PoolableObjectFactory} that creates
 * {@link PoolableConnection}s.
 *
 * @author Rodney Waldhoff
 * @author Glenn L. Nielsen
 * @author James House (<a href="mailto:james@interobjective.com">james@interobjective.com</a>)
 * @version $Id: PoolableConnectionFactory.java,v 1.7 2003/04/09 00:19:37 dgraham Exp $
 */
public class PoolableConnectionFactory implements PoolableObjectFactory {
    /**
     * Create a new <tt>PoolableConnectionFactory</tt>.
     * @param connFactory the {@link ConnectionFactory} from which to obtain base {@link Connection}s
     * @param pool the {@link ObjectPool} in which to pool those {@link Connection}s
     * @param stmtPoolFactory the {@link KeyedObjectPoolFactory} to use to create {@link KeyedObjectPool}s for pooling {@link PreparedStatement}s, or <tt>null</tt> to disable {@link PreparedStatement} pooling
     * @param validationQuery a query to use to {@link #validateObject validate} {@link Connection}s.  Should return at least one row. May be <tt>null</tt>
     * @param defaultReadOnly the default "read only" setting for borrowed {@link Connection}s
     * @param defaultAutoCommit the default "auto commit" setting for returned {@link Connection}s
     */
    public PoolableConnectionFactory(ConnectionFactory connFactory, ObjectPool pool, KeyedObjectPoolFactory stmtPoolFactory, String validationQuery, boolean defaultReadOnly, boolean defaultAutoCommit) {
        _connFactory = connFactory;
        _pool = pool;
        _pool.setFactory(this);
        _stmtPoolFactory = stmtPoolFactory;
        _validationQuery = validationQuery;
        _defaultReadOnly = defaultReadOnly;
        _defaultAutoCommit = defaultAutoCommit;
    }

    /**
     * Create a new <tt>PoolableConnectionFactory</tt>.
     * @param connFactory the {@link ConnectionFactory} from which to obtain base {@link Connection}s
     * @param pool the {@link ObjectPool} in which to pool those {@link Connection}s
     * @param stmtPoolFactory the {@link KeyedObjectPoolFactory} to use to create {@link KeyedObjectPool}s for pooling {@link PreparedStatement}s, or <tt>null</tt> to disable {@link PreparedStatement} pooling
     * @param validationQuery a query to use to {@link #validateObject validate} {@link Connection}s.  Should return at least one row. May be <tt>null</tt>
     * @param defaultReadOnly the default "read only" setting for borrowed {@link Connection}s
     * @param defaultAutoCommit the default "auto commit" setting for returned {@link Connection}s
     * @param config the AbandonedConfig if tracing SQL objects
     * @deprecated AbandonedConfig is now deprecated.
     */
    public PoolableConnectionFactory(
        ConnectionFactory connFactory,
        ObjectPool pool,
        KeyedObjectPoolFactory stmtPoolFactory,
        String validationQuery,
        boolean defaultReadOnly,
        boolean defaultAutoCommit,
        AbandonedConfig config) {
            
        _connFactory = connFactory;
        _pool = pool;
        _config = config;
        _pool.setFactory(this);
        _stmtPoolFactory = stmtPoolFactory;
        _validationQuery = validationQuery;
        _defaultReadOnly = defaultReadOnly;
        _defaultAutoCommit = defaultAutoCommit;
    }

    /**
     * Sets the {@link ConnectionFactory} from which to obtain base {@link Connection}s.
     * @param connFactory the {@link ConnectionFactory} from which to obtain base {@link Connection}s
     */
    synchronized public void setConnectionFactory(ConnectionFactory connFactory) {
        _connFactory = connFactory;
    }

    /**
     * Sets the query I use to {@link #validateObject validate} {@link Connection}s.
     * Should return at least one row.
     * May be <tt>null</tt>
     * @param validationQuery a query to use to {@link #validateObject validate} {@link Connection}s.
     */
    synchronized public void setValidationQuery(String validationQuery) {
        _validationQuery = validationQuery;
    }

    /**
     * Sets the {@link ObjectPool} in which to pool {@link Connection}s.
     * @param pool the {@link ObjectPool} in which to pool those {@link Connection}s
     */
    synchronized public void setPool(ObjectPool pool) {
        if(null != _pool && pool != _pool) {
            try {
                _pool.close();
            } catch(Exception e) {
                // ignored !?!
            }
        }
        _pool = pool;
    }

    public ObjectPool getPool() {
        return _pool;
    }

    /**
     * Sets the {@link KeyedObjectPoolFactory} I use to create {@link KeyedObjectPool}s
     * for pooling {@link PreparedStatement}s.
     * Set to <tt>null</tt> to disable {@link PreparedStatement} pooling.
     * @param stmtPoolFactory the {@link KeyedObjectPoolFactory} to use to create {@link KeyedObjectPool}s for pooling {@link PreparedStatement}s
     */
    synchronized public void setStatementPoolFactory(KeyedObjectPoolFactory stmtPoolFactory) {
        _stmtPoolFactory = stmtPoolFactory;
    }

    /**
     * Sets the default "read only" setting for borrowed {@link Connection}s
     * @param defaultReadOnly the default "read only" setting for borrowed {@link Connection}s
     */
    public void setDefaultReadOnly(boolean defaultReadOnly) {
        _defaultReadOnly = defaultReadOnly;
    }

    /**
     * Sets the default "auto commit" setting for borrowed {@link Connection}s
     * @param defaultAutoCommit the default "auto commit" setting for borrowed {@link Connection}s
     */
    public void setDefaultAutoCommit(boolean defaultAutoCommit) {
        _defaultAutoCommit = defaultAutoCommit;
    }

    synchronized public Object makeObject() throws Exception {
        Connection conn = _connFactory.createConnection();
        if(null != _stmtPoolFactory) {
            KeyedObjectPool stmtpool = _stmtPoolFactory.createPool();
            conn = new PoolingConnection(conn,stmtpool);
            stmtpool.setFactory((PoolingConnection)conn);
        }
        return new PoolableConnection(conn,_pool,_config);
    }

    public void destroyObject(Object obj) throws Exception {
        if(obj instanceof PoolableConnection) {
            ((PoolableConnection)obj).reallyClose();
        }
    }

    synchronized public boolean validateObject(Object obj) {
        if(obj instanceof Connection) {
            String query = _validationQuery;
            Connection conn = (Connection)obj;
            try {
                if(conn.isClosed()) {
                    return false;
                }
            } catch(SQLException e) {
                return false;
            }
            if(null != query) {
                Statement stmt = null;
                ResultSet rset = null;
                try {
                    stmt = conn.createStatement();
                    rset = stmt.executeQuery(query);
                    if(rset.next()) {
                        return true;
                    } else {
                        return false;
                    }
                } catch(Exception e) {
                    return false;
                } finally {
                    try {
                        rset.close();
                    } catch(Exception t) {
                        // ignored
                    }
                    try {
                        stmt.close();
                    } catch(Exception t) {
                        // ignored
                    }

                }
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    public void passivateObject(Object obj) throws Exception {
        if(obj instanceof Connection) {
            Connection conn = (Connection)obj;
            if(!conn.getAutoCommit()) {
                conn.rollback();
            }
        }
        if(obj instanceof DelegatingConnection) {
            ((DelegatingConnection)obj).passivate();
        }
    }

    public void activateObject(Object obj) throws Exception {
        if(obj instanceof DelegatingConnection) {
            ((DelegatingConnection)obj).activate();
        }
        if(obj instanceof Connection) {
            Connection conn = (Connection)obj;
            conn.setAutoCommit(_defaultAutoCommit);
            conn.setReadOnly(_defaultReadOnly);
        }
    }

    protected ConnectionFactory _connFactory = null;
    protected String _validationQuery = null;
    protected ObjectPool _pool = null;
    protected KeyedObjectPoolFactory _stmtPoolFactory = null;
    protected boolean _defaultReadOnly = false;
    protected boolean _defaultAutoCommit = true;
    
    /**
     * @deprecated AbandonedConfig is now deprecated.
     */
    protected AbandonedConfig _config = null;
}
