package org.apache.commons.dbcp.jdbc2pool;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
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
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and 
 *    "Apache Turbine" must not be used to endorse or promote products 
 *    derived from this software without prior written permission. For 
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Turbine", nor may "Apache" appear in their name, without 
 *    prior written permission of the Apache Software Foundation.
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
 */
 
import java.io.Serializable;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.pool.BasePoolableObjectFactory;

class PoolKey
    implements Serializable
{
    private String datasourceName;
    private boolean reusable;
    private String username;
    
    PoolKey()
    {
    }
        
    /* *
     * Get the value of datasourceName.
     * @return value of datasourceName.
     * /
    public String getDatasourceName() 
    {
        return datasourceName;
    }
    
    /* *
     * Set the value of datasourceName.
     * @param v  Value to assign to datasourceName.
     * /
    public void setDatasourceName(String  v) 
    {
        this.datasourceName = v;
    }
    
    /* *
     * Get the value of username.
     * @return value of username.
     * /
    public String getUsername() 
    {
        return username;
    }
    
    /* *
     * Set the value of username.
     * @param v  Value to assign to username.
     * /
    public void setUsername(String  v) 
    {
        this.username = v;
    }
    */    

    /**
     * Initialize key for method with no arguments.
     *
     * @param instanceOrClass the Object on which the method is invoked.  if 
     * the method is static, a String representing the class name is used.
     * @param method the method name
     */
    void init(String username, String datasourceName) 
    {
        this.datasourceName = datasourceName;
        this.username = username;
    }

    public boolean equals(Object obj)
    {
        boolean equal = false;
        if ( obj instanceof PoolKey ) 
        {
            PoolKey pk = (PoolKey)obj;
            equal = ObjectUtils.equals(pk.datasourceName, datasourceName);
            equal &= ObjectUtils.equals(pk.username, username);
        }            

        return equal;
    }

    public int hashCode()
    {
        int h = 0;
        if (datasourceName != null) 
        {
            h += datasourceName.hashCode();
        }
        if (username != null) 
        {
            h += username.hashCode();
        }
        return h;
    }

    public String toString()
    {
        StringBuffer sb = new StringBuffer(50);
        sb.append("PoolKey(");
        sb.append(username).append(", ").append(datasourceName);
        sb.append(')');
        return sb.toString();
    }

    // ************* PoolableObjectFactory implementation *******************

    static class Factory 
        extends BasePoolableObjectFactory
    {
        /**
         * Creates an instance that can be returned by the pool.
         * @return an instance that can be returned by the pool.
         */
        public Object makeObject() 
            throws Exception
        {
            return new PoolKey();
        }
        
        /**
         * Uninitialize an instance to be returned to the pool.
         * @param obj the instance to be passivated
         */
        public void passivateObject(Object obj) 
            throws Exception
        {
            PoolKey key = (PoolKey)obj;
            key.datasourceName = null;
            key.username = null;
        }
    }
}
