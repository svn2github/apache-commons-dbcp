/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//dbcp/src/test/org/apache/commons/dbcp/TestManual.java,v 1.1 2001/04/14 17:16:25 rwaldhoff Exp $
 * $Revision: 1.1 $
 * $Date: 2001/04/14 17:16:25 $
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

import junit.framework.*;
import java.sql.*;
import org.apache.commons.pool.*;
import org.apache.commons.pool.impl.*;

/**
 * @author Rodney Waldhoff
 * @version $Id: TestManual.java,v 1.1 2001/04/14 17:16:25 rwaldhoff Exp $
 */
public class TestManual extends TestCase {
    public TestManual(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestManual.class);
    }

    public static void main(String args[]) {
        String[] testCaseName = { TestManual.class.getName() };
        junit.textui.TestRunner.main(testCaseName);
    }

    public void setUp() throws Exception {
        GenericObjectPool pool = new GenericObjectPool(null, 10, GenericObjectPool.WHEN_EXHAUSTED_BLOCK, 2000L, 10, true, true, 10000L, 5, 5000L, true);
        DriverConnectionFactory cf = new DriverConnectionFactory(new TesterDriver(),"jdbc:apache:commons:testdriver",null);
        GenericKeyedObjectPoolFactory opf = new GenericKeyedObjectPoolFactory(null, 10, GenericKeyedObjectPool.WHEN_EXHAUSTED_BLOCK, 2000L, 10, true, true, 10000L, 5, 5000L, true);
        PoolableConnectionFactory pcf = new PoolableConnectionFactory(cf, pool, opf, "SELECT COUNT(*) FROM DUAL", false, true);
        PoolingDriver driver = new PoolingDriver();
        driver.registerPool("test",pool);
        DriverManager.registerDriver(driver);
    }

    public void testSimple() throws Exception {
        Connection conn = DriverManager.getConnection("jdbc:apache:commons:dbcp:test");
        assert(null != conn);
        PreparedStatement stmt = conn.prepareStatement("select * from dual");
        assert(null != stmt);
        ResultSet rset = stmt.executeQuery();
        assert(null != rset);
        assert(rset.next());
        rset.close();
        stmt.close();
        conn.close();
    }

    public void testSimple2() throws Exception {
        Connection conn = DriverManager.getConnection("jdbc:apache:commons:dbcp:test");
        assert(null != conn);
        {
            PreparedStatement stmt = conn.prepareStatement("select * from dual");
            assert(null != stmt);
            ResultSet rset = stmt.executeQuery();
            assert(null != rset);
            assert(rset.next());
            rset.close();
            stmt.close();
        }
        {
            PreparedStatement stmt = conn.prepareStatement("select * from dual");
            assert(null != stmt);
            ResultSet rset = stmt.executeQuery();
            assert(null != rset);
            assert(rset.next());
            rset.close();
            stmt.close();
        }
        conn.close();
        try {
            conn.createStatement();
            fail("Can't use closed connections");
        } catch(SQLException e) {
            ; // expected
        }

        conn = DriverManager.getConnection("jdbc:apache:commons:dbcp:test");
        assert(null != conn);
        {
            PreparedStatement stmt = conn.prepareStatement("select * from dual");
            assert(null != stmt);
            ResultSet rset = stmt.executeQuery();
            assert(null != rset);
            assert(rset.next());
            rset.close();
            stmt.close();
        }
        {
            PreparedStatement stmt = conn.prepareStatement("select * from dual");
            assert(null != stmt);
            ResultSet rset = stmt.executeQuery();
            assert(null != rset);
            assert(rset.next());
            rset.close();
            stmt.close();
        }
        conn.close();
        conn = null;
    }

    public void testPooling() throws Exception {
        Connection conn = DriverManager.getConnection("jdbc:apache:commons:dbcp:test");
        assert(conn != null);
        Connection conn2 = DriverManager.getConnection("jdbc:apache:commons:dbcp:test");
        assert(conn2 != null);
        assert(conn != conn2);
        conn2.close();
        conn.close();
        conn2 = DriverManager.getConnection("jdbc:apache:commons:dbcp:test");
        assertSame(conn,conn2);
    }
}
