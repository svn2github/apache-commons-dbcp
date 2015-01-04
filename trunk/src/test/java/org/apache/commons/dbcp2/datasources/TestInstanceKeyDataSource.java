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

package org.apache.commons.dbcp2.datasources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbcp2.cpdsadapter.DriverAdapterCPDS;
import org.junit.Test;

/**
 * @version $Id$
 */
public class TestInstanceKeyDataSource {

    /**
     * Verify that exception on setupDefaults does not leak PooledConnection
     * 
     * JIRA: DBCP-237
     */
    @Test
    public void testExceptionOnSetupDefaults() throws Exception {
        DriverAdapterCPDS pcds;
        pcds = new DriverAdapterCPDS();
        pcds.setDriver("org.apache.commons.dbcp2.TesterDriver");
        pcds.setUrl("jdbc:apache:commons:testdriver");
        pcds.setUser("foo");
        pcds.setPassword("bar");
        pcds.setPoolPreparedStatements(false);
        ThrowOnSetupDefaultsDataSource tds = new ThrowOnSetupDefaultsDataSource();
        tds.setConnectionPoolDataSource(pcds);
        int numConnections = tds.getNumActive();
        try {
            tds.getConnection("foo", "bar");
            fail("Expecting SQLException");
        } catch (SQLException ex) {
           //Expected
        }
        assertEquals(numConnections,tds.getNumActive());     
    }
    
    private static class ThrowOnSetupDefaultsDataSource
    extends SharedPoolDataSource {
        private static final long serialVersionUID = -448025812063133259L;

        ThrowOnSetupDefaultsDataSource() {
            super();
        }
        @Override
        protected void setupDefaults(Connection con, String username)
        throws  SQLException {
            throw new SQLException("bang!");
        }
    }

}
