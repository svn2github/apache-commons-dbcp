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

import java.sql.Connection;
import java.sql.PreparedStatement;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author Rodney Waldhoff
 * @author Dirk Verbeeck
 * @version $Revision$ $Date$
 */
public class TestDelegatingPreparedStatement extends TestCase {
    public TestDelegatingPreparedStatement(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestDelegatingPreparedStatement.class);
    }

    private DelegatingConnection<Connection> conn = null;
    private Connection delegateConn = null;
    private DelegatingPreparedStatement stmt = null;
    private PreparedStatement delegateStmt = null;

    @Override
    public void setUp() throws Exception {
        delegateConn = new TesterConnection("test", "test");
        conn = new DelegatingConnection<>(delegateConn);
    }

    public void testExecuteQueryReturnsNull() throws Exception {
        delegateStmt = new TesterPreparedStatement(delegateConn,"null");
        stmt = new DelegatingPreparedStatement(conn,delegateStmt);
        assertNull(stmt.executeQuery());
    }

    public void testExecuteQueryReturnsNotNull() throws Exception {
        delegateStmt = new TesterPreparedStatement(delegateConn,"select * from foo");
        stmt = new DelegatingPreparedStatement(conn,delegateStmt);
        assertTrue(null != stmt.executeQuery());
    }

    public void testGetDelegate() throws Exception {
        delegateStmt = new TesterPreparedStatement(delegateConn,"select * from foo");
        stmt = new DelegatingPreparedStatement(conn,delegateStmt);
        assertEquals(delegateStmt,stmt.getDelegate());
    }
}
