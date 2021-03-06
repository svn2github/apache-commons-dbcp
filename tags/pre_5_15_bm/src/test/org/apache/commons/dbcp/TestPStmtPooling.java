/*
 * Copyright 1999-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.dbcp;

import java.sql.Connection;
import java.sql.Statement;

import javax.sql.DataSource;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.pool.KeyedObjectPoolFactory;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericKeyedObjectPoolFactory;
import org.apache.commons.pool.impl.GenericObjectPool;

/**
 * TestSuite for BasicDataSource with prepared statement pooling enabled
 * 
 * @author Dirk Verbeeck
 * @version $Revision$ $Date$
 */
public class TestPStmtPooling extends TestCase {
    public TestPStmtPooling(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestPStmtPooling.class);
    }

    public void testStmtPool() throws Exception {
        new TesterDriver();
        ConnectionFactory connFactory = new DriverManagerConnectionFactory(
                "jdbc:apache:commons:testdriver","u1","p1");

        ObjectPool connPool = new GenericObjectPool();
        KeyedObjectPoolFactory stmtPoolFactory = new GenericKeyedObjectPoolFactory(null);

        PoolableConnectionFactory x = new PoolableConnectionFactory(
                connFactory, connPool, stmtPoolFactory,
                null, false, true);

        DataSource ds = new PoolingDataSource(connPool);

        Connection conn = ds.getConnection();
        Statement stmt1 = conn.prepareStatement("select 1 from dual");
        Statement ustmt1 = ((DelegatingStatement) stmt1).getInnermostDelegate();
        stmt1.close();
        Statement stmt2 = conn.prepareStatement("select 1 from dual");
        Statement ustmt2 = ((DelegatingStatement) stmt2).getInnermostDelegate();
        stmt2.close();
        assertSame(ustmt1, ustmt2);
    }
}
