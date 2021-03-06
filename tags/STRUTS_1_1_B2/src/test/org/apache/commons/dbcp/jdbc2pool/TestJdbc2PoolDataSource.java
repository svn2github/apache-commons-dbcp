/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//dbcp/src/test/org/apache/commons/dbcp/jdbc2pool/Attic/TestJdbc2PoolDataSource.java,v 1.1 2002/08/05 06:42:01 jmcnally Exp $
 * $Revision: 1.1 $
 * $Date: 2002/08/05 06:42:01 $
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

package org.apache.commons.dbcp.jdbc2pool;

import junit.framework.*;
import java.sql.*;
import javax.sql.DataSource;
import org.apache.commons.lang.exception.NestableRuntimeException;
import org.apache.commons.pool.*;
import org.apache.commons.pool.impl.*;

import org.apache.commons.dbcp.cpdsadapter.DriverAdapterCPDS;

/**
 * @author Rodney Waldhoff
 * @version $Id: TestJdbc2PoolDataSource.java,v 1.1 2002/08/05 06:42:01 jmcnally Exp $
 */
public class TestJdbc2PoolDataSource extends TestCase 
{
    private static final int MAX_ACTIVE = 10;
    private static final int MAX_WAIT = 50;

    DataSource ds;

    public TestJdbc2PoolDataSource(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestJdbc2PoolDataSource.class);
    }

    public static void main(String args[]) {
        String[] testCaseName = { TestJdbc2PoolDataSource.class.getName() };
        junit.textui.TestRunner.main(testCaseName);
    }

    public void setUp() throws Exception 
    {
        DriverAdapterCPDS pcds = new DriverAdapterCPDS();
        pcds.setDriver("org.apache.commons.dbcp.TesterDriver");
        pcds.setUrl("jdbc:apache:commons:testdriver");
        pcds.setUser("foo");
        pcds.setPassword(null);

        Jdbc2PoolDataSource tds = new Jdbc2PoolDataSource();
        tds.setConnectionPoolDataSource(pcds);
        tds.setDefaultMaxActive(MAX_ACTIVE);
        tds.setDefaultMaxWait(MAX_WAIT);

        ds = tds;
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
        Connection[] c = new Connection[MAX_ACTIVE];
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
        Connection[] c = new Connection[MAX_ACTIVE];
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
        Connection[] c = new Connection[MAX_ACTIVE];
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
        catch(java.util.NoSuchElementException e)
        {
            // should only be able to open 10 connections, so this test should
            // throw an exception
        }

        for (int i=0; i<c.length; i++) 
        {
            c[i].close();
        }
    }

    public void testMultipleThreads()
        throws Exception
    {
        assertTrue(multipleThreads(1));
        assertTrue(!multipleThreads(2*MAX_WAIT));
    }

    private boolean multipleThreads(int holdTime)
        throws Exception
    {
        long startTime = System.currentTimeMillis();
        final boolean[] success = new boolean[1];
        success[0] = true;
        final PoolTest[] pts = new PoolTest[2*MAX_ACTIVE];
        ThreadGroup threadGroup = new ThreadGroup("foo")
            {
                public void uncaughtException(Thread t, Throwable e)
                {
                    /*
                    for (int i = 0; i < pts.length; i++)
                    {
                        System.out.println(i + ": " + pts[i].reportState());
                    }
                    */
                    for (int i = 0; i < pts.length; i++)
                    {
                        pts[i].stop();
                    }
                    
                    //e.printStackTrace();
                    success[0] = false;
                }
            };

        for (int i = 0; i < pts.length; i++)
        {
            pts[i] = new PoolTest(threadGroup, holdTime);
        }
        Thread.currentThread().sleep(10*holdTime);
        for (int i = 0; i < pts.length; i++)
        {
            pts[i].stop();
        }
        long time = System.currentTimeMillis() - startTime; // - (pts.length*10*holdTime);
        System.out.println("Multithread test time = " + time + " ms");

        Thread.currentThread().sleep(holdTime);
        return success[0];
    }

    private static int currentThreadCount = 0;

    private class PoolTest implements Runnable
    {
        /**
         * The number of milliseconds to hold onto a database connection
         */
        private int connHoldTime;
        
        private boolean isRun;
        
        private String state;

        protected PoolTest(ThreadGroup threadGroup, int connHoldTime)
        {
            this.connHoldTime = connHoldTime;
            Thread thread = new Thread(threadGroup, this,
                                       "Thread+" + currentThreadCount++);
            thread.setDaemon(false);
            thread.start();
        }
        
        public void run()
        {
            Thread thread = Thread.currentThread();
            isRun = true;
            while (isRun)
            {
                try
                {
                    Connection conn = null;
                    state="Getting Connection";
                    conn = ds.getConnection();
                    state="Using Connection";
                    assertTrue(null != conn);
                    PreparedStatement stmt = 
                        conn.prepareStatement("select * from dual");
                    assertTrue(null != stmt);
                    ResultSet rset = stmt.executeQuery();
                    assertTrue(null != rset);
                    assertTrue(rset.next());
                    state="Holding Connection";
                    thread.sleep(connHoldTime);
                    state="Returning Connection";
                    rset.close();
                    stmt.close();
                    conn.close();
                }
                catch (Exception e)
                {
                    throw new NestableRuntimeException(e);
                }
            }
        }
        
        public void stop()
        {
            isRun = false;
        }

        public String reportState()
        {
            return state;
        }
    }
}
