/*
 * $Source: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//dbcp/src/test/org/apache/commons/dbcp/TestAbandonedBasicDataSource.java,v $
 * $Revision: 1.4 $
 * $Date: 2003/09/13 22:44:32 $
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

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @version $Revision: 1.4 $ $Date: 2003/09/13 22:44:32 $
 */
public class TestAbandonedBasicDataSource extends TestConnectionPool {
    public TestAbandonedBasicDataSource(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestAbandonedBasicDataSource.class);
    }

    protected Connection getConnection() throws Exception {
        return ds.getConnection();
    }

    private BasicDataSource ds = null;
    
    public void setUp() throws Exception {
        super.setUp();
        ds = new BasicDataSource();
        ds.setDriverClassName("org.apache.commons.dbcp.TesterDriver");
        ds.setUrl("jdbc:apache:commons:testdriver");
        ds.setMaxActive(getMaxActive());
        ds.setMaxWait(getMaxWait());
        ds.setDefaultAutoCommit(true);
        ds.setDefaultReadOnly(false);
        ds.setUsername("username");
        ds.setPassword("password");
        ds.setValidationQuery("SELECT DUMMY FROM DUAL");

        // abandoned enabled but should not affect the basic tests
        // (very high timeout)
        ds.setLogAbandoned(true);
        ds.setRemoveAbandoned(true);
        ds.setRemoveAbandonedTimeout(10000);
    }

    public void tearDown() throws Exception {
        ds = null;
    }

    public void testPooling() throws Exception {
        // this also needs access to the undelying connection
        ds.setAccessToUnderlyingConnectionAllowed(true);
        super.testPooling();
    }

    // ---------- Abandoned Test -----------

    private void getConnection1() throws Exception {
        System.err.println("BEGIN getConnection1()");
        Connection conn = ds.getConnection();
        System.err.println("conn: " + conn);
        System.err.println("END getConnection1()");
    }

    private void getConnection2() throws Exception {
        System.err.println("BEGIN getConnection2()");
        Connection conn = ds.getConnection();
        System.err.println("conn: " + conn);
        System.err.println("END getConnection2()");
    }

    private void getConnection3() throws Exception {
        System.err.println("BEGIN getConnection3()");
        Connection conn = ds.getConnection();
        System.err.println("conn: " + conn);
        System.err.println("END getConnection3()");
    }

    public void testAbandoned() throws Exception {
        // force abandoned
        ds.setRemoveAbandonedTimeout(0);
        ds.setMaxActive(1);

        System.err.println("----------------------------------------");
        getConnection1();
        getConnection2();
        getConnection3();
        System.err.println("----------------------------------------");
    }
}
