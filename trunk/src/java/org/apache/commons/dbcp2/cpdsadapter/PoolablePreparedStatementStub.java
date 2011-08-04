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

package org.apache.commons.dbcp2.cpdsadapter;

import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.SQLException;
import org.apache.commons.pool2.KeyedObjectPool;
import org.apache.commons.dbcp2.PoolablePreparedStatement;

/**
 * A {@link PoolablePreparedStatement} stub since activate and passivate
 * are declared protected and we need to be able to call them within this
 * package.
 *
 * @author John D. McNally
 * @version $Revision$ $Date$
 */
class PoolablePreparedStatementStub extends PoolablePreparedStatement<PStmtKeyCPDS,PoolablePreparedStatementStub> {

    /**
     * Constructor
     * @param stmt my underlying {@link PreparedStatement}
     * @param key my key" as used by {@link KeyedObjectPool}
     * @param pool the {@link KeyedObjectPool} from which I was obtained.
     * @param conn the {@link Connection} from which I was created
     */
    public PoolablePreparedStatementStub(PreparedStatement stmt,
            PStmtKeyCPDS key,
            KeyedObjectPool<PStmtKeyCPDS, PoolablePreparedStatement<PStmtKeyCPDS, PoolablePreparedStatementStub>> pool,
            Connection conn) {
        super(stmt, key, pool, conn);
    }

    @Override
    public void activate() throws SQLException {
        super.activate();
    }

    @Override
    public void passivate() throws SQLException {
        super.passivate();
    }
}
