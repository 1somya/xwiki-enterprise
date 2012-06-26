/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.xwiki.test.ui;

import org.junit.Before;
import org.junit.BeforeClass;

/**
 * Base class for admin tests that need to manipulate a repository of extensions.
 * 
 * @version $Id$
 */
public class AbstractExtensionAdminAuthenticatedTest extends AbstractAdminAuthenticatedTest
{
    @Before
    @Override
    public void setUp() throws Exception
    {
        super.setUp();

        // Make sure to have the proper token
        getUtil().recacheSecretToken();
    }

    @BeforeClass
    public static void init() throws Exception
    {
        // Require some AbstractTest initialization
        AbstractTest.init();

        // Make sure repository utils is initialized and set
        RepositoryTestUtils repositoryUtils = getRepositoryTestUtils();

        // This will not be null if we are in the middle of allTests
        if (repositoryUtils == null) {
            RepositoryTestUtils repositoryUtil = new RepositoryTestUtils(context.getUtil());
            repositoryUtil.init();

            // Set integration repository util
            context.getProperties().put(RepositoryTestUtils.PROPERTY_KEY, repositoryUtil);
        }
    }

    protected static RepositoryTestUtils getRepositoryTestUtils()
    {
        return (RepositoryTestUtils) context.getProperties().get(RepositoryTestUtils.PROPERTY_KEY);
    }
}
