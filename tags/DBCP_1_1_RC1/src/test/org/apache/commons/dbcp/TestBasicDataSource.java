/*
 * $Source: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//dbcp/src/test/org/apache/commons/dbcp/TestBasicDataSource.java,v $
 * $Revision: 1.12 $
 * $Date: 2003/09/20 17:31:38 $
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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @version $Revision: 1.12 $ $Date: 2003/09/20 17:31:38 $
 */
public class TestBasicDataSource extends TestConnectionPool {
    public TestBasicDataSource(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestBasicDataSource.class);
    }

    protected Connection getConnection() throws Exception {
        return ds.getConnection();
    }

    private BasicDataSource ds = null;
    private static String CATALOG = "test catalog";
    
    public void setUp() throws Exception {
        super.setUp();
        ds = new BasicDataSource();
        ds.setDriverClassName("org.apache.commons.dbcp.TesterDriver");
        ds.setUrl("jdbc:apache:commons:testdriver");
        ds.setMaxActive(getMaxActive());
        ds.setMaxWait(getMaxWait());
        ds.setDefaultAutoCommit(true);
        ds.setDefaultReadOnly(false);
        ds.setDefaultTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
        ds.setDefaultCatalog(CATALOG);
        ds.setUsername("username");
        ds.setPassword("password");
        ds.setValidationQuery("SELECT DUMMY FROM DUAL");
    }

    public void tearDown() throws Exception {
        ds = null;
    }
    
    public void testTransactionIsolationBehavior() throws Exception {
        Connection conn = getConnection();
        assertTrue(conn != null);
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

    public void testPreparedStatementPooling() throws Exception {
        ds.setPoolPreparedStatements(true);
        ds.setMaxOpenPreparedStatements(2);
        
        Connection conn = getConnection();
        assertNotNull(conn);
        
        PreparedStatement stmt1 = conn.prepareStatement("select 'a' from dual");
        assertNotNull(stmt1);
        
        PreparedStatement stmt2 = conn.prepareStatement("select 'b' from dual");
        assertNotNull(stmt2);
        
        assertTrue(stmt1 != stmt2);
        
        // go over the maxOpen limit
        PreparedStatement stmt3 = null;
		try {
			stmt3 = conn.prepareStatement("select 'c' from dual");
            fail("expected SQLException");
		} 
        catch (SQLException e) {}
        
        // make idle
        stmt2.close();

        // test cleanup the 'b' statement
        stmt3 = conn.prepareStatement("select 'c' from dual");
        assertNotNull(stmt3);
        assertTrue(stmt3 != stmt1);
        assertTrue(stmt3 != stmt2);
        
        // normal reuse of statement
        stmt1.close();
        PreparedStatement stmt4 = conn.prepareStatement("select 'a' from dual");
        assertNotNull(stmt4);
    }
    
    public void testPooling() throws Exception {
        // this also needs access to the undelying connection
        ds.setAccessToUnderlyingConnectionAllowed(true);
        super.testPooling();
    }    
    
    public void testNoAccessToUnderlyingConnectionAllowed() throws Exception {
        // default: false
        assertEquals(false, ds.isAccessToUnderlyingConnectionAllowed());
        
        Connection conn = getConnection();
        Connection dconn = ((DelegatingConnection) conn).getDelegate();
        assertNull(dconn);
        
        dconn = ((DelegatingConnection) conn).getInnermostDelegate();
        assertNull(dconn);
    }

    public void testAccessToUnderlyingConnectionAllowed() throws Exception {
        ds.setAccessToUnderlyingConnectionAllowed(true);
        assertEquals(true, ds.isAccessToUnderlyingConnectionAllowed());
        
        Connection conn = getConnection();
        Connection dconn = ((DelegatingConnection) conn).getDelegate();
        assertNotNull(dconn);
        
        dconn = ((DelegatingConnection) conn).getInnermostDelegate();
        assertNotNull(dconn);
        
        assertTrue(dconn instanceof TesterConnection);
    }
    
    public void testEmptyValidationQuery() throws Exception {
        assertNotNull(ds.getValidationQuery());
        
        ds.setValidationQuery("");
        assertNull(ds.getValidationQuery());

        ds.setValidationQuery("   ");
        assertNull(ds.getValidationQuery());
    }

    public void testInvalidValidationQuery() {
        try {
            ds.setValidationQuery("invalid");
            ds.getConnection();
            fail("expected SQLException");
        }
        catch (SQLException e) {
            if (e.toString().indexOf("invalid") < 0) {
                fail("expected detailed error message");
            }
        }
    }

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

    public void testNoValidationQuery() throws Exception {
        ds.setTestOnBorrow(true);
        ds.setTestOnReturn(true);
        ds.setTestWhileIdle(true);
        ds.setValidationQuery("");
        
        Connection conn = ds.getConnection();
        conn.close();

        assertEquals(false, ds.getTestOnBorrow());
        assertEquals(false, ds.getTestOnReturn());
        assertEquals(false, ds.getTestWhileIdle());
    }
    
    public void testDefaultCatalog() throws Exception {
        Connection[] c = new Connection[getMaxActive()];
        for (int i = 0; i < c.length; i++) {
            c[i] = getConnection();
            assertTrue(c[i] != null);
            assertEquals(CATALOG, c[i].getCatalog()); 
        }

        for (int i = 0; i < c.length; i++) {
            c[i].setCatalog("error");
            c[i].close();
        }
        
        for (int i = 0; i < c.length; i++) {
            c[i] = getConnection();
            assertTrue(c[i] != null);
            assertEquals(CATALOG, c[i].getCatalog()); 
        }        

        for (int i = 0; i < c.length; i++) {
            c[i].close();
        }
    }
    
    public void testSetAutoCommitTrueOnClose() throws Exception {
        ds.setAccessToUnderlyingConnectionAllowed(true);
        ds.setDefaultAutoCommit(false);
        
        Connection conn = getConnection();
        assertNotNull(conn);
        assertEquals(false, conn.getAutoCommit());

        Connection dconn = ((DelegatingConnection) conn).getInnermostDelegate();
        assertNotNull(dconn);
        assertEquals(false, dconn.getAutoCommit());

        conn.close();

        assertEquals(true, dconn.getAutoCommit());
    }
}
