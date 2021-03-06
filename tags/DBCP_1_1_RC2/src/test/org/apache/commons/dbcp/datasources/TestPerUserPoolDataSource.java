/*
 * $Source: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//dbcp/src/test/org/apache/commons/dbcp/datasources/TestPerUserPoolDataSource.java,v $
 * $Revision: 1.6 $
 * $Date: 2003/10/09 21:05:30 $
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

package org.apache.commons.dbcp.datasources;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javax.sql.DataSource;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.dbcp.TestConnectionPool;
import org.apache.commons.dbcp.cpdsadapter.DriverAdapterCPDS;

/**
 * @author John McNally
 * @author Dirk Verbeeck
 * @version $Revision: 1.6 $ $Date: 2003/10/09 21:05:30 $
 */
public class TestPerUserPoolDataSource extends TestConnectionPool {
    public TestPerUserPoolDataSource(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestPerUserPoolDataSource.class);
    }

    protected Connection getConnection() throws Exception {
        return ds.getConnection("foo","bar");
    }

    private DataSource ds;

    public void setUp() throws Exception {
        DriverAdapterCPDS pcds = new DriverAdapterCPDS();
        pcds.setDriver("org.apache.commons.dbcp.TesterDriver");
        pcds.setUrl("jdbc:apache:commons:testdriver");
        pcds.setUser("foo");
        pcds.setPassword("bar");

        PerUserPoolDataSource tds = new PerUserPoolDataSource();
        tds.setConnectionPoolDataSource(pcds);
        tds.setDefaultMaxActive(getMaxActive());
        tds.setDefaultMaxWait((int)(getMaxWait()));
        tds.setPerUserMaxActive("foo",new Integer(getMaxActive()));
        tds.setPerUserMaxWait("foo",new Integer((int)(getMaxWait())));
        tds.setDefaultTransactionIsolation(
            Connection.TRANSACTION_READ_COMMITTED);

        ds = tds;
    }

    /**
     * Switching 'u1 -> 'u2' and 'p1' -> 'p2' will
     * exhibit the bug detailed in 
     * http://nagoya.apache.org/bugzilla/show_bug.cgi?id=18905
     */
    public void testIncorrectPassword() throws Exception 
    {
        try {
            // Use bad password
            ds.getConnection("u1", "zlsafjk").close();
            fail("Able to retrieve connection with incorrect password");
        } catch (SQLException e1) {
            // should fail

        }
        
        // Use good password
        ds.getConnection("u1", "p1").close();
        try 
        {
            ds.getConnection("u1", "x").close();
            fail("Able to retrieve connection with incorrect password");
        }
        catch (SQLException e)
        {
            if (!e.getMessage().startsWith("Given password did not match")) 
            {
                throw e;
            }
            // else the exception was expected
        }
        
        // Make sure we can still use our good password.
        ds.getConnection("u1", "p1").close();
    }


    public void testSimple() throws Exception 
    {
        Connection conn = ds.getConnection();
        assertTrue(null != conn);
        PreparedStatement stmt = conn.prepareStatement("select * from dual");
        assertTrue(null != stmt);
        ResultSet rset = stmt.executeQuery();
        assertTrue(null != rset);
        assertTrue(rset.next());
        rset.close();
        stmt.close();
        conn.close();
    }

    public void testSimpleWithUsername() throws Exception 
    {
        Connection conn = ds.getConnection("u1", "p1");
        assertTrue(null != conn);
        PreparedStatement stmt = conn.prepareStatement("select * from dual");
        assertTrue(null != stmt);
        ResultSet rset = stmt.executeQuery();
        assertTrue(null != rset);
        assertTrue(rset.next());
        rset.close();
        stmt.close();
        conn.close();
    }

    public void testClosingWithUserName() 
        throws Exception 
    {
        Connection[] c = new Connection[getMaxActive()];
        // open the maximum connections
        for (int i=0; i<c.length; i++) 
        {
            c[i] = ds.getConnection("u1", "p1");
        }

        // close one of the connections
        c[0].close();
        assertTrue(c[0].isClosed());
        // get a new connection
        c[0] = ds.getConnection("u1", "p1");

        for (int i=0; i<c.length; i++) 
        {
            c[i].close();
        }

        // open the maximum connections
        for (int i=0; i<c.length; i++) 
        {
            c[i] = ds.getConnection("u1", "p1");
        }
        for (int i=0; i<c.length; i++) 
        {
            c[i].close();
        }
    }

    public void testSimple2() 
        throws Exception 
    {
        Connection conn = ds.getConnection();
        assertTrue(null != conn);

        PreparedStatement stmt = 
            conn.prepareStatement("select * from dual");
        assertTrue(null != stmt);
        ResultSet rset = stmt.executeQuery();
        assertTrue(null != rset);
        assertTrue(rset.next());
        rset.close();
        stmt.close();
        
        stmt = conn.prepareStatement("select * from dual");
        assertTrue(null != stmt);
        rset = stmt.executeQuery();
        assertTrue(null != rset);
        assertTrue(rset.next());
        rset.close();
        stmt.close();
        
        conn.close();
        try 
        {
            conn.createStatement();
            fail("Can't use closed connections");
        } 
        catch(SQLException e) 
        {
            // expected
        }

        conn = ds.getConnection();
        assertTrue(null != conn);

        stmt = conn.prepareStatement("select * from dual");
        assertTrue(null != stmt);
        rset = stmt.executeQuery();
        assertTrue(null != rset);
        assertTrue(rset.next());
        rset.close();
        stmt.close();

        stmt = conn.prepareStatement("select * from dual");
        assertTrue(null != stmt);
        rset = stmt.executeQuery();
        assertTrue(null != rset);
        assertTrue(rset.next());
        rset.close();
        stmt.close();
        
        conn.close();
        conn = null;
    }

    public void testOpening() 
        throws Exception 
    {
        Connection[] c = new Connection[getMaxActive()];
        // test that opening new connections is not closing previous
        for (int i=0; i<c.length; i++) 
        {
            c[i] = ds.getConnection();
            assertTrue(c[i] != null);
            for (int j=0; j<=i; j++) 
            {
                assertTrue(!c[j].isClosed());
            }
        }

        for (int i=0; i<c.length; i++) 
        {
            c[i].close();
        }
    }

    public void testClosing() 
        throws Exception 
    {
        Connection[] c = new Connection[getMaxActive()];
        // open the maximum connections
        for (int i=0; i<c.length; i++) 
        {
            c[i] = ds.getConnection();
        }

        // close one of the connections
        c[0].close();
        assertTrue(c[0].isClosed());
        
        // get a new connection
        c[0] = ds.getConnection();

        for (int i=0; i<c.length; i++) 
        {
            c[i].close();
        }
    }

    public void testMaxActive() 
        throws Exception 
    {
        Connection[] c = new Connection[getMaxActive()];
        for (int i=0; i<c.length; i++) 
        {
            c[i] = ds.getConnection();
            assertTrue(c[i] != null);            
        }

        try
        {
            ds.getConnection();
            fail("Allowed to open more than DefaultMaxActive connections.");
        }
        catch(java.sql.SQLException e)
        {
            // should only be able to open 10 connections, so this test should
            // throw an exception
        }

        for (int i=0; i<c.length; i++) 
        {
            c[i].close();
        }
    }
    
    public void testPerUserMethods() throws Exception {
        PerUserPoolDataSource tds = (PerUserPoolDataSource) ds;
        
        // you need to set maxActive otherwise there is no accounting
        tds.setPerUserMaxActive("u1", new Integer(5));
        tds.setPerUserMaxActive("u2", new Integer(5));
        
        assertEquals(0, tds.getNumActive());
        assertEquals(0, tds.getNumActive("u1", "p1"));
        assertEquals(0, tds.getNumActive("u2", "p2"));
        assertEquals(0, tds.getNumIdle());
        assertEquals(0, tds.getNumIdle("u1", "p1"));
        assertEquals(0, tds.getNumIdle("u2", "p2"));
        
        Connection conn = tds.getConnection();
        assertNotNull(conn);
        assertEquals(1, tds.getNumActive());
        assertEquals(0, tds.getNumActive("u1", "p1"));
        assertEquals(0, tds.getNumActive("u2", "p2"));
        assertEquals(0, tds.getNumIdle());
        assertEquals(0, tds.getNumIdle("u1", "p1"));
        assertEquals(0, tds.getNumIdle("u2", "p2"));

        conn.close();
        assertEquals(0, tds.getNumActive());
        assertEquals(0, tds.getNumActive("u1", "p1"));
        assertEquals(0, tds.getNumActive("u2", "p2"));
        assertEquals(1, tds.getNumIdle());
        assertEquals(0, tds.getNumIdle("u1", "p1"));
        assertEquals(0, tds.getNumIdle("u2", "p2"));

        conn = tds.getConnection("u1", "p1");
        assertNotNull(conn);
        assertEquals(0, tds.getNumActive());
        assertEquals(1, tds.getNumActive("u1", "p1"));
        assertEquals(0, tds.getNumActive("u2", "p2"));
        assertEquals(1, tds.getNumIdle());
        assertEquals(0, tds.getNumIdle("u1", "p1"));
        assertEquals(0, tds.getNumIdle("u2", "p2"));

        conn.close();
        assertEquals(0, tds.getNumActive());
        assertEquals(0, tds.getNumActive("u1", "p1"));
        assertEquals(0, tds.getNumActive("u2", "p2"));
        assertEquals(1, tds.getNumIdle());
        assertEquals(1, tds.getNumIdle("u1", "p1"));
        assertEquals(0, tds.getNumIdle("u2", "p2"));
    }
    
    public void testMultipleThreads() throws Exception {
        assertTrue(multipleThreads(1));
        assertTrue(!multipleThreads(2 * (int)(getMaxWait())));
    }

    private boolean multipleThreads(int holdTime) throws Exception {
        long startTime = System.currentTimeMillis();
        final boolean[] success = new boolean[1];
        success[0] = true;
        final PoolTest[] pts = new PoolTest[2 * getMaxActive()];
        ThreadGroup threadGroup = new ThreadGroup("foo") {
            public void uncaughtException(Thread t, Throwable e) {
                /*
                for (int i = 0; i < pts.length; i++)
                {
                    System.out.println(i + ": " + pts[i].reportState());
                }
                */
                for (int i = 0; i < pts.length; i++) {
                    pts[i].stop();
                }

                //e.printStackTrace();
                success[0] = false;
            }
        };

        for (int i = 0; i < pts.length; i++) {
            pts[i] = new PoolTest(threadGroup, holdTime);
        }
        Thread.sleep(10 * holdTime);
        for (int i = 0; i < pts.length; i++) {
            pts[i].stop();
        }
        long time = System.currentTimeMillis() - startTime;
        // - (pts.length*10*holdTime);
        System.out.println("Multithread test time = " + time + " ms");

        Thread.sleep(holdTime);
        return success[0];
    }

    private static int currentThreadCount = 0;

    private class PoolTest implements Runnable {
        /**
         * The number of milliseconds to hold onto a database connection
         */
        private int connHoldTime;

        private boolean isRun;

        private String state;

        protected PoolTest(ThreadGroup threadGroup, int connHoldTime) {
            this.connHoldTime = connHoldTime;
            Thread thread =
                new Thread(threadGroup, this, "Thread+" + currentThreadCount++);
            thread.setDaemon(false);
            thread.start();
        }

        public void run() {
            isRun = true;
            while (isRun) {
                try {
                    Connection conn = null;
                    state = "Getting Connection";
                    conn = getConnection();
                    state = "Using Connection";
                    assertTrue(null != conn);
                    PreparedStatement stmt =
                        conn.prepareStatement("select * from dual");
                    assertTrue(null != stmt);
                    ResultSet rset = stmt.executeQuery();
                    assertTrue(null != rset);
                    assertTrue(rset.next());
                    state = "Holding Connection";
                    Thread.sleep(connHoldTime);
                    state = "Returning Connection";
                    rset.close();
                    stmt.close();
                    conn.close();
                } catch (RuntimeException e) {
                    throw e;
                } catch (Exception e) {
                    throw new RuntimeException(e.toString());
                }
            }
        }

        public void stop() {
            isRun = false;
        }

        public String reportState() {
            return state;
        }
    }

    public void testTransactionIsolationBehavior() throws Exception {
        Connection conn = getConnection();
        assertTrue(conn != null);
        assertEquals(Connection.TRANSACTION_READ_COMMITTED, 
                     conn.getTransactionIsolation());
        conn.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
        conn.close();
        
        Connection conn2 = getConnection();
        assertEquals(Connection.TRANSACTION_READ_COMMITTED, 
                     conn2.getTransactionIsolation());
        
        Connection conn3 = getConnection();
        assertEquals(Connection.TRANSACTION_READ_COMMITTED, 
                     conn3.getTransactionIsolation());
        conn2.close();
        conn3.close();
    }     

    public void testSerialization() throws Exception {
        // make sure the pool has initialized
        Connection conn = ds.getConnection();
        conn.close();

        // serialize
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(baos);
        out.writeObject((Serializable)ds);
        byte[] b = baos.toByteArray();
        out.close();

        ByteArrayInputStream bais = new ByteArrayInputStream(b);
        ObjectInputStream in = new ObjectInputStream(bais);
        Object obj = in.readObject();
        in.close();

        assertEquals( 1, ((PerUserPoolDataSource)obj).getNumIdle() );
    }
}
