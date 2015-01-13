/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.dbcp2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import javax.sql.DataSource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * TestSuite for BasicDataSource
 *
 * @author Dirk Verbeeck
 * @version $Id$
 */
public class TestBasicDataSource extends TestConnectionPool {

    @Override
    protected Connection getConnection() throws Exception {
        return ds.getConnection();
    }

    protected BasicDataSource ds = null;
    private static final String CATALOG = "test catalog";

    @Before
    public void setUp() throws Exception {
        ds = createDataSource();
        ds.setDriverClassName("org.apache.commons.dbcp2.TesterDriver");
        ds.setUrl("jdbc:apache:commons:testdriver");
        ds.setMaxTotal(getMaxTotal());
        ds.setMaxWaitMillis(getMaxWaitMillis());
        ds.setDefaultAutoCommit(Boolean.TRUE);
        ds.setDefaultReadOnly(Boolean.FALSE);
        ds.setDefaultTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
        ds.setDefaultCatalog(CATALOG);
        ds.setUsername("username");
        ds.setPassword("password");
        ds.setValidationQuery("SELECT DUMMY FROM DUAL");
        ds.setConnectionInitSqls(Arrays.asList(new String[] { "SELECT 1", "SELECT 2"}));
        ds.setDriverClassLoader(new TesterClassLoader());
        ds.setJmxName("org.apache.commons.dbcp2:name=test");
    }

    protected BasicDataSource createDataSource() throws Exception {
        return new BasicDataSource();
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
        ds.close();
        ds = null;
    }

    @Test
    public void testClose() throws Exception {
        ds.setAccessToUnderlyingConnectionAllowed(true);

        // active connection is held open when ds is closed
        Connection activeConnection = getConnection();
        Connection rawActiveConnection = ((DelegatingConnection<?>) activeConnection).getInnermostDelegate();
        assertFalse(activeConnection.isClosed());
        assertFalse(rawActiveConnection.isClosed());

        // idle connection is in pool but closed
        Connection idleConnection = getConnection();
        Connection rawIdleConnection = ((DelegatingConnection<?>) idleConnection).getInnermostDelegate();
        assertFalse(idleConnection.isClosed());
        assertFalse(rawIdleConnection.isClosed());

        // idle wrapper should be closed but raw connection should be open
        idleConnection.close();
        assertTrue(idleConnection.isClosed());
        assertFalse(rawIdleConnection.isClosed());

        ds.close();

        // raw idle connection should now be closed
        assertTrue(rawIdleConnection.isClosed());

        // active connection should still be open
        assertFalse(activeConnection.isClosed());
        assertFalse(rawActiveConnection.isClosed());

        // now close the active connection
        activeConnection.close();

        // both wrapper and raw active connection should be closed
        assertTrue(activeConnection.isClosed());
        assertTrue(rawActiveConnection.isClosed());

        // Verify SQLException on getConnection after close
        try {
            getConnection();
            fail("Expecting SQLException");
        } catch (SQLException ex) {
            // Expected
        }

        // Redundant close is OK
        ds.close();

    }

    @Test
    public void testSetProperties() throws Exception {
        // normal
        ds.setConnectionProperties("name1=value1;name2=value2;name3=value3");
        assertEquals(3, ds.getConnectionProperties().size());
        assertEquals("value1", ds.getConnectionProperties().getProperty("name1"));
        assertEquals("value2", ds.getConnectionProperties().getProperty("name2"));
        assertEquals("value3", ds.getConnectionProperties().getProperty("name3"));

        // make sure all properties are replaced
        ds.setConnectionProperties("name1=value1;name2=value2");
        assertEquals(2, ds.getConnectionProperties().size());
        assertEquals("value1", ds.getConnectionProperties().getProperty("name1"));
        assertEquals("value2", ds.getConnectionProperties().getProperty("name2"));
        assertFalse(ds.getConnectionProperties().containsKey("name3"));

        // no value is empty string
        ds.setConnectionProperties("name1=value1;name2");
        assertEquals(2, ds.getConnectionProperties().size());
        assertEquals("value1", ds.getConnectionProperties().getProperty("name1"));
        assertEquals("", ds.getConnectionProperties().getProperty("name2"));

        // no value (with equals) is empty string
        ds.setConnectionProperties("name1=value1;name2=");
        assertEquals(2, ds.getConnectionProperties().size());
        assertEquals("value1", ds.getConnectionProperties().getProperty("name1"));
        assertEquals("", ds.getConnectionProperties().getProperty("name2"));

        // single value
        ds.setConnectionProperties("name1=value1");
        assertEquals(1, ds.getConnectionProperties().size());
        assertEquals("value1", ds.getConnectionProperties().getProperty("name1"));

        // single value with trailing ;
        ds.setConnectionProperties("name1=value1;");
        assertEquals(1, ds.getConnectionProperties().size());
        assertEquals("value1", ds.getConnectionProperties().getProperty("name1"));

        // single value wit no value
        ds.setConnectionProperties("name1");
        assertEquals(1, ds.getConnectionProperties().size());
        assertEquals("", ds.getConnectionProperties().getProperty("name1"));

        // null should throw a NullPointerException
        try {
            ds.setConnectionProperties(null);
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            // expected
        }
    }

    @Test
    public void testTransactionIsolationBehavior() throws Exception {
        Connection conn = getConnection();
        assertNotNull(conn);
        assertEquals(Connection.TRANSACTION_READ_COMMITTED, conn.getTransactionIsolation());
        conn.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
        conn.close();

        Connection conn2 = getConnection();
        assertEquals(Connection.TRANSACTION_READ_COMMITTED, conn2.getTransactionIsolation());

        Connection conn3 = getConnection();
        assertEquals(Connection.TRANSACTION_READ_COMMITTED, conn3.getTransactionIsolation());

        conn2.close();

        conn3.close();
    }

    @Override
    @Test
    public void testPooling() throws Exception {
        // this also needs access to the underlying connection
        ds.setAccessToUnderlyingConnectionAllowed(true);
        super.testPooling();
    }

    @Test
    public void testNoAccessToUnderlyingConnectionAllowed() throws Exception {
        // default: false
        assertEquals(false, ds.isAccessToUnderlyingConnectionAllowed());

        Connection conn = getConnection();
        Connection dconn = ((DelegatingConnection<?>) conn).getDelegate();
        assertNull(dconn);

        dconn = ((DelegatingConnection<?>) conn).getInnermostDelegate();
        assertNull(dconn);
    }

    @Test
    public void testAccessToUnderlyingConnectionAllowed() throws Exception {
        ds.setAccessToUnderlyingConnectionAllowed(true);
        assertEquals(true, ds.isAccessToUnderlyingConnectionAllowed());

        Connection conn = getConnection();
        Connection dconn = ((DelegatingConnection<?>) conn).getDelegate();
        assertNotNull(dconn);

        dconn = ((DelegatingConnection<?>) conn).getInnermostDelegate();
        assertNotNull(dconn);

        assertTrue(dconn instanceof TesterConnection);
    }

    @Test
    public void testEmptyValidationQuery() throws Exception {
        assertNotNull(ds.getValidationQuery());

        ds.setValidationQuery("");
        assertNull(ds.getValidationQuery());

        ds.setValidationQuery("   ");
        assertNull(ds.getValidationQuery());
    }

    @Test
    public void testInvalidValidationQuery() {
        ds.setValidationQuery("invalid");
        try (Connection c = ds.getConnection()) {
            fail("expected SQLException");
        } catch (SQLException e) {
            if (e.toString().indexOf("invalid") < 0) {
                fail("expected detailed error message");
            }
        }
    }

    @Test
    public void testValidationQueryTimoutFail() {
        ds.setTestOnBorrow(true);
        ds.setValidationQueryTimeout(3); // Too fast for TesterStatement
        try (Connection c = ds.getConnection()) {
            fail("expected SQLException");
        } catch (SQLException ex) {
            if (ex.toString().indexOf("timeout") < 0) {
                fail("expected timeout error message");
            }
        }
    }

    @Test
    public void testValidationQueryTimeoutZero() throws Exception {
        ds.setTestOnBorrow(true);
        ds.setTestOnReturn(true);
        ds.setValidationQueryTimeout(0);
        Connection con = ds.getConnection();
        con.close();
    }

    @Test
    public void testValidationQueryTimeoutNegative() throws Exception {
        ds.setTestOnBorrow(true);
        ds.setTestOnReturn(true);
        ds.setValidationQueryTimeout(-1);
        Connection con = ds.getConnection();
        con.close();
    }

    @Test
    public void testValidationQueryTimeoutSucceed() throws Exception {
        ds.setTestOnBorrow(true);
        ds.setTestOnReturn(true);
        ds.setValidationQueryTimeout(100); // Works for TesterStatement
        Connection con = ds.getConnection();
        con.close();
    }

    @Test
    public void testEmptyInitConnectionSql() throws Exception {
        ds.setConnectionInitSqls(Arrays.asList(new String[]{"", "   "}));
        assertNotNull(ds.getConnectionInitSqls());
        assertEquals(0, ds.getConnectionInitSqls().size());

        ds.setConnectionInitSqls(null);
        assertNotNull(ds.getConnectionInitSqls());
        assertEquals(0, ds.getConnectionInitSqls().size());
    }

    @Test
    public void testInvalidConnectionInitSql() {
        try {
            ds.setConnectionInitSqls(Arrays.asList(new String[]{"SELECT 1","invalid"}));
            try (Connection c = ds.getConnection()) {}
            fail("expected SQLException");
        }
        catch (SQLException e) {
            if (e.toString().indexOf("invalid") < 0) {
                fail("expected detailed error message");
            }
        }
    }

    @Test
    public void testSetValidationTestProperties() {
        // defaults
        assertEquals(true, ds.getTestOnBorrow());
        assertEquals(false, ds.getTestOnReturn());
        assertEquals(false, ds.getTestWhileIdle());

        ds.setTestOnBorrow(true);
        ds.setTestOnReturn(true);
        ds.setTestWhileIdle(true);
        assertEquals(true, ds.getTestOnBorrow());
        assertEquals(true, ds.getTestOnReturn());
        assertEquals(true, ds.getTestWhileIdle());

        ds.setTestOnBorrow(false);
        ds.setTestOnReturn(false);
        ds.setTestWhileIdle(false);
        assertEquals(false, ds.getTestOnBorrow());
        assertEquals(false, ds.getTestOnReturn());
        assertEquals(false, ds.getTestWhileIdle());
    }

    @Test
    public void testDefaultCatalog() throws Exception {
        Connection[] c = new Connection[getMaxTotal()];
        for (int i = 0; i < c.length; i++) {
            c[i] = getConnection();
            assertTrue(c[i] != null);
            assertEquals(CATALOG, c[i].getCatalog());
        }

        for (Connection element : c) {
            element.setCatalog("error");
            element.close();
        }

        for (int i = 0; i < c.length; i++) {
            c[i] = getConnection();
            assertTrue(c[i] != null);
            assertEquals(CATALOG, c[i].getCatalog());
        }

        for (Connection element : c) {
            element.close();
        }
    }

    @Test
    public void testSetAutoCommitTrueOnClose() throws Exception {
        ds.setAccessToUnderlyingConnectionAllowed(true);
        ds.setDefaultAutoCommit(Boolean.FALSE);

        Connection conn = getConnection();
        assertNotNull(conn);
        assertEquals(false, conn.getAutoCommit());

        Connection dconn = ((DelegatingConnection<?>) conn).getInnermostDelegate();
        assertNotNull(dconn);
        assertEquals(false, dconn.getAutoCommit());

        conn.close();

        assertEquals(true, dconn.getAutoCommit());
    }

    @Test
    public void testInitialSize() throws Exception {
        ds.setMaxTotal(20);
        ds.setMaxIdle(20);
        ds.setInitialSize(10);

        Connection conn = getConnection();
        assertNotNull(conn);
        conn.close();

        assertEquals(0, ds.getNumActive());
        assertEquals(10, ds.getNumIdle());
    }

    // Bugzilla Bug 28251:  Returning dead database connections to BasicDataSource
    // isClosed() failure blocks returning a connection to the pool
    @Test
    public void testIsClosedFailure() throws SQLException {
        ds.setAccessToUnderlyingConnectionAllowed(true);
        Connection conn = ds.getConnection();
        assertNotNull(conn);
        assertEquals(1, ds.getNumActive());

        // set an IO failure causing the isClosed mathod to fail
        TesterConnection tconn = (TesterConnection) ((DelegatingConnection<?>)conn).getInnermostDelegate();
        tconn.setFailure(new IOException("network error"));

        try {
            conn.close();
            fail("Expected SQLException");
        }
        catch(SQLException ex) { }

        assertEquals(0, ds.getNumActive());
    }

    /**
     * Bugzilla Bug 29054:
     * The BasicDataSource.setTestOnReturn(boolean) is not carried through to
     * the GenericObjectPool variable _testOnReturn.
     */
    @Test
    public void testPropertyTestOnReturn() throws Exception {
        ds.setValidationQuery("select 1 from dual");
        ds.setTestOnBorrow(false);
        ds.setTestWhileIdle(false);
        ds.setTestOnReturn(true);

        Connection conn = ds.getConnection();
        assertNotNull(conn);

        assertEquals(false, ds.getConnectionPool().getTestOnBorrow());
        assertEquals(false, ds.getConnectionPool().getTestWhileIdle());
        assertEquals(true, ds.getConnectionPool().getTestOnReturn());
    }

    /**
     * Bugzilla Bug 29055: AutoCommit and ReadOnly
     * The DaffodilDB driver throws an SQLException if
     * trying to commit or rollback a readOnly connection.
     */
    @Test
    public void testRollbackReadOnly() throws Exception {
        ds.setDefaultReadOnly(Boolean.TRUE);
        ds.setDefaultAutoCommit(Boolean.FALSE);

        Connection conn = ds.getConnection();
        assertNotNull(conn);
        conn.close();
    }

    /**
     * Bugzilla Bug 29832: Broken behaviour for BasicDataSource.setMaxTotal(0)
     * MaxTotal == 0 should throw SQLException on getConnection.
     * Results from Bug 29863 in commons-pool.
     */
    @Test
    public void testMaxTotalZero() throws Exception {
        ds.setMaxTotal(0);

        try {
            Connection conn = ds.getConnection();
            assertNotNull(conn);
            fail("SQLException expected");

        } catch (SQLException e) {
            // test OK
        }
    }
    
    @Test
    public void testInvalidateConnection() throws Exception {
    	ds.setMaxTotal(2);
    	Connection conn1 = ds.getConnection();
    	Connection conn2 = ds.getConnection();
    	ds.invalidateConnection(conn1);
    	assertTrue(conn1.isClosed());
    	assertEquals(1, ds.getNumActive());
    	assertEquals(0, ds.getNumIdle());
    	Connection conn3 = ds.getConnection();
    	conn2.close();
    	conn3.close();
    }
    
    /**
     * JIRA DBCP-93: If an SQLException occurs after the GenericObjectPool is
     * initialized in createDataSource, the evictor task is not cleaned up.
     */
    @Test
    public void testCreateDataSourceCleanupThreads() throws Exception {
        ds.close();
        ds = null;
        ds = createDataSource();
        ds.setDriverClassName("org.apache.commons.dbcp2.TesterDriver");
        ds.setUrl("jdbc:apache:commons:testdriver");
        ds.setMaxTotal(getMaxTotal());
        ds.setMaxWaitMillis(getMaxWaitMillis());
        ds.setDefaultAutoCommit(Boolean.TRUE);
        ds.setDefaultReadOnly(Boolean.FALSE);
        ds.setDefaultTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
        ds.setDefaultCatalog(CATALOG);
        ds.setUsername("username");
        // Set timeBetweenEvictionRuns > 0, so evictor is created
        ds.setTimeBetweenEvictionRunsMillis(100);
        // Make password incorrect, so createDataSource will throw
        ds.setPassword("wrong");
        ds.setValidationQuery("SELECT DUMMY FROM DUAL");
        int threadCount = Thread.activeCount();
        for (int i = 0; i < 10; i++) {
            try (Connection c = ds.getConnection()){
            } catch (SQLException ex) {
                // ignore
            }
        }
        // Allow one extra thread for JRockit compatibility
        assertTrue(Thread.activeCount() <= threadCount + 1);
    }

    /**
     * JIRA DBCP-333: Check that a custom class loader is used.
     * @throws Exception
     */
    @Test
    public void testDriverClassLoader() throws Exception {
        getConnection();
        ClassLoader cl = ds.getDriverClassLoader();
        assertNotNull(cl);
        assertTrue(cl instanceof TesterClassLoader);
        assertTrue(((TesterClassLoader) cl).didLoad(ds.getDriverClassName()));
    }

    /**
     * JIRA: DBCP-342, DBCP-93
     * Verify that when errors occur during BasicDataSource initialization, GenericObjectPool
     * Evictors are cleaned up.
     */
    @Test
    public void testCreateDataSourceCleanupEvictor() throws Exception {
        ds.close();
        ds = null;
        ds = createDataSource();
        ds.setDriverClassName("org.apache.commons.dbcp2.TesterConnRequestCountDriver");
        ds.setUrl("jdbc:apache:commons:testerConnRequestCountDriver");
        ds.setValidationQuery("SELECT DUMMY FROM DUAL");
        ds.setUsername("username");

        // Make password incorrect, so createDataSource will throw
        ds.setPassword("wrong");
        // Set timeBetweenEvictionRuns > 0, so evictor will be created
        ds.setTimeBetweenEvictionRunsMillis(100);
        // Set min idle > 0, so evictor will try to make connection as many as idle count
        ds.setMinIdle(2);

        // Prevent concurrent execution of threads executing test subclasses
        synchronized (TesterConnRequestCountDriver.class) {
            TesterConnRequestCountDriver.initConnRequestCount();

            // user request 10 times
            for (int i=0; i<10; i++) {
                try {
                    @SuppressWarnings("unused")
                    DataSource ds2 = ds.createDataSource();
                } catch (SQLException e) {
                    // Ignore
                }
            }

            // sleep 1000ms. evictor will be invoked 10 times if running.
            Thread.sleep(1000);

            // Make sure there have been no Evictor-generated requests (count should be 10, from requests above)
            assertEquals(10, TesterConnRequestCountDriver.getConnectionRequestCount());
        }

        // make sure cleanup is complete
        assertNull(ds.getConnectionPool());
    }
    
    @Test
    public void testMaxConnLifetimeExceeded() throws Exception {
        try {
            StackMessageLog.lock();
            ds.setMaxConnLifetimeMillis(100);
            Connection conn = ds.getConnection();
            assertEquals(1, ds.getNumActive());
            Thread.sleep(500);
            conn.close();
            assertEquals(0, ds.getNumIdle());
            String message = StackMessageLog.popMessage();
            assertTrue(message.indexOf("exceeds the maximum permitted value") > 0);
        } finally {
            StackMessageLog.clear();
            StackMessageLog.unLock();
        }
    }
    
    @Test
    public void testMaxConnLifetimeExceededMutedLog() throws Exception {
        try {
            StackMessageLog.lock();
            ds.setMaxConnLifetimeMillis(100);
            ds.setLogExpiredConnections(false);
            Connection conn = ds.getConnection();
            assertEquals(1, ds.getNumActive());
            Thread.sleep(500);
            conn.close();
            assertEquals(0, ds.getNumIdle());
            assertTrue(StackMessageLog.isEmpty());
        } finally {
            StackMessageLog.clear();
            StackMessageLog.unLock();
        }
    }
}

/**
 * TesterDriver that keeps a static count of connection requests.
 */
class TesterConnRequestCountDriver extends TesterDriver {
    private static final String CONNECT_STRING = "jdbc:apache:commons:testerConnRequestCountDriver";
    private static AtomicInteger connectionRequestCount = new AtomicInteger(0);

    @Override
    public Connection connect(String url, Properties info) throws SQLException {
        connectionRequestCount.incrementAndGet();
        return super.connect(url, info);
    }

    @Override
    public boolean acceptsURL(String url) throws SQLException {
        return CONNECT_STRING.startsWith(url);
    }

    public static int getConnectionRequestCount() {
        return connectionRequestCount.get();
    }

    public static void initConnRequestCount() {
        connectionRequestCount.set(0);
    }
}
