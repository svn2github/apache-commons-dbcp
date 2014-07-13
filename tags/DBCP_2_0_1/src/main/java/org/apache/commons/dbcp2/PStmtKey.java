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

import org.apache.commons.dbcp2.PoolingConnection.StatementType;

/**
 * A key uniquely identifying {@link java.sql.PreparedStatement PreparedStatement}s.
 * @since 2.0
 */
public class PStmtKey {

    /** SQL defining Prepared or Callable Statement */
    private final String _sql;

    /** Result set type */
    private final Integer _resultSetType;

    /** Result set concurrency */
    private final Integer _resultSetConcurrency;

    /** Database catalog */
    private final String _catalog;

    /** Auto generated keys */
    private final Integer _autoGeneratedKeys;

    /** Statement type */
    private final StatementType _stmtType;


    public PStmtKey(String sql) {
        this(sql, null, StatementType.PREPARED_STATEMENT, null);
    }

    public PStmtKey(String sql, String catalog) {
        this(sql, catalog, StatementType.PREPARED_STATEMENT, null);
    }

    public PStmtKey(String sql, String catalog, int autoGeneratedKeys) {
        this(sql, catalog, StatementType.PREPARED_STATEMENT, Integer.valueOf(autoGeneratedKeys));
    }

    public PStmtKey(String sql, String catalog, StatementType stmtType, Integer autoGeneratedKeys) {
        _sql = sql;
        _catalog = catalog;
        _stmtType = stmtType;
        _autoGeneratedKeys = autoGeneratedKeys;
        _resultSetType = null;
        _resultSetConcurrency = null;
    }

    public  PStmtKey(String sql, int resultSetType, int resultSetConcurrency) {
        this(sql, null, resultSetType, resultSetConcurrency, StatementType.PREPARED_STATEMENT);
    }

    public PStmtKey(String sql, String catalog, int resultSetType, int resultSetConcurrency) {
        this(sql, catalog, resultSetType, resultSetConcurrency, StatementType.PREPARED_STATEMENT);
    }

    public PStmtKey(String sql, String catalog, int resultSetType, int resultSetConcurrency, StatementType stmtType) {
        _sql = sql;
        _catalog = catalog;
        _resultSetType = Integer.valueOf(resultSetType);
        _resultSetConcurrency = Integer.valueOf(resultSetConcurrency);
        _stmtType = stmtType;
        _autoGeneratedKeys = null;
    }


    public String getSql() {
        return _sql;
    }

    public Integer getResultSetType() {
        return _resultSetType;
    }

    public Integer getResultSetConcurrency() {
        return _resultSetConcurrency;
    }

    public Integer getAutoGeneratedKeys() {
        return _autoGeneratedKeys;
    }

    public String getCatalog() {
        return _catalog;
    }

    public StatementType getStmtType() {
        return _stmtType;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        PStmtKey other = (PStmtKey) obj;
        if (_catalog == null) {
            if (other._catalog != null) {
                return false;
            }
        } else if (!_catalog.equals(other._catalog)) {
            return false;
        }
        if (_resultSetConcurrency == null) {
            if (other._resultSetConcurrency != null) {
                return false;
            }
        } else if (!_resultSetConcurrency.equals(other._resultSetConcurrency)) {
            return false;
        }
        if (_resultSetType == null) {
            if (other._resultSetType != null) {
                return false;
            }
        } else if (!_resultSetType.equals(other._resultSetType)) {
            return false;
        }
        if (_autoGeneratedKeys == null) {
            if (other._autoGeneratedKeys != null) {
                return false;
            }
        } else if (!_autoGeneratedKeys.equals(other._autoGeneratedKeys)) {
            return false;
        }
        if (_sql == null) {
            if (other._sql != null) {
                return false;
            }
        } else if (!_sql.equals(other._sql)) {
            return false;
        }
        if (_stmtType != other._stmtType) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (_catalog == null ? 0 : _catalog.hashCode());
        result = prime * result + (_resultSetConcurrency == null ? 0 : _resultSetConcurrency.hashCode());
        result = prime * result + (_resultSetType == null ? 0 : _resultSetType.hashCode());
        result = prime * result + (_sql == null ? 0 : _sql.hashCode());
        result = prime * result + (_autoGeneratedKeys == null ? 0 : _autoGeneratedKeys.hashCode());
        result = prime * result + _stmtType.hashCode();
        return result;
    }

    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("PStmtKey: sql=");
        buf.append(_sql);
        buf.append(", catalog=");
        buf.append(_catalog);
        buf.append(", resultSetType=");
        buf.append(_resultSetType);
        buf.append(", resultSetConcurrency=");
        buf.append(_resultSetConcurrency);
        buf.append(", autoGeneratedKeys=");
        buf.append(_autoGeneratedKeys);
        buf.append(", statmentType=");
        buf.append(_stmtType);
        return buf.toString();
    }
}