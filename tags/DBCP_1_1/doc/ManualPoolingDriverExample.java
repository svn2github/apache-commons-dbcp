/*
 * $Source: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//dbcp/doc/ManualPoolingDriverExample.java,v $
 * $Revision: 1.4 $
 * $Date: 2003/10/09 21:05:29 $
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

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

//
// Here are the dbcp-specific classes.
// Note that they are only used in the setupDriver
// method. In normal use, your classes interact
// only with the standard JDBC API
//
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.PoolingDriver;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;

//
// Here's a simple example of how to use the PoolingDriver.
// In this example, we'll construct the PoolingDriver manually,
// just to show how the pieces fit together, but you could also
// configure it using an external conifguration file in
// JOCL format (and eventually Digester).
//

//
// To compile this example, you'll want:
//  * commons-pool.jar
//  * commons-dbcp.jar
// in your classpath.
//
// To run this example, you'll want:
//  * commons-collections.jar
//  * commons-pool.jar
//  * commons-dbcp.jar
//  * the classes for your (underlying) JDBC driver
// in your classpath.
//
// Invoke the class using two arguments:
//  * the connect string for your underlying JDBC driver
//  * the query you'd like to execute
// You'll also want to ensure your underlying JDBC driver
// is registered.  You can use the "jdbc.drivers"
// property to do this.
//
// For example:
//  java -Djdbc.drivers=oracle.jdbc.driver.OracleDriver \
//       -classpath commons-collections.jar:commons-pool.jar:commons-dbcp.jar:oracle-jdbc.jar:. \
//       ManualPoolingDriverExample
//       "jdbc:oracle:thin:scott/tiger@myhost:1521:mysid"
//       "SELECT * FROM DUAL"
//
public class ManualPoolingDriverExample {

    public static void main(String[] args) {
        //
        // First we load the underlying JDBC driver.
        // You need this if you don't use the jdbc.drivers
        // system property.
        //
        System.out.println("Loading underlying JDBC driver.");
        Class.forName("oracle.jdbc.driver.OracleDriver");
        System.out.println("Done.");

        //
        // Then we set up and register the PoolingDriver.
        // Normally this would be handled auto-magically by
        // an external configuration, but in this example we'll
        // do it manually.
        //
        System.out.println("Setting up driver.");
        setupDriver(args[0]);
        System.out.println("Done.");

        //
        // Now, we can use JDBC as we normally would.
        // Using the connect string
        //  jdbc:apache:commons:dbcp:example
        // The general form being:
        //  jdbc:apache:commons:dbcp:<name-of-pool>
        //

        Connection conn = null;
        Statement stmt = null;
        ResultSet rset = null;

        try {
            System.out.println("Creating connection.");
            conn = DriverManager.getConnection("jdbc:apache:commons:dbcp:example");
            System.out.println("Creating statement.");
            stmt = conn.createStatement();
            System.out.println("Executing statement.");
            rset = stmt.executeQuery(args[1]);
            System.out.println("Results:");
            int numcols = rset.getMetaData().getColumnCount();
            while(rset.next()) {
                for(int i=1;i<=numcols;i++) {
                    System.out.print("\t" + rset.getString(i));
                }
                System.out.println("");
            }
        } catch(SQLException e) {
            e.printStackTrace();
        } finally {
            try { rset.close(); } catch(Exception e) { }
            try { stmt.close(); } catch(Exception e) { }
            try { conn.close(); } catch(Exception e) { }
        }
    }

    public static void setupDriver(String connectURI) {
        //
        // First, we'll need a ObjectPool that serves as the
        // actual pool of connections.
        //
        // We'll use a GenericObjectPool instance, although
        // any ObjectPool implementation will suffice.
        //
        ObjectPool connectionPool = new GenericObjectPool(null);

        //
        // Next, we'll create a ConnectionFactory that the
        // pool will use to create Connections.
        // We'll use the DriverManagerConnectionFactory,
        // using the connect string passed in the command line
        // arguments.
        //
        ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(connectURI,null);

        //
        // Now we'll create the PoolableConnectionFactory, which wraps
        // the "real" Connections created by the ConnectionFactory with
        // the classes that implement the pooling functionality.
        //
        PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(connectionFactory,connectionPool,null,null,false,true);

        //
        // Finally, we create the PoolingDriver itself...
        //
        PoolingDriver driver = new PoolingDriver();

        //
        // ...and register our pool with it.
        //
        driver.registerPool("example",connectionPool);

        //
        // Now we can just use the connect string "jdbc:apache:commons:dbcp:example"
        // to access our pool of Connections.
        //
    }
}
