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
package com.xpn.xwiki.it.selenium;

import com.xpn.xwiki.it.selenium.framework.AbstractXWikiTestCase;
import com.xpn.xwiki.it.selenium.framework.AlbatrossSkinExecutor;
import com.xpn.xwiki.it.selenium.framework.XWikiTestSuite;

import junit.framework.Test;

/**
 * Verify the skin customization features available in the Administration (like changing
 * the default CSS, etc).
 *
 * @version $Id: $
 */
public class SkinCustomizationsTest extends AbstractXWikiTestCase
{
    public static Test suite()
    {
        XWikiTestSuite suite = new XWikiTestSuite("Verify the skin customization features "
            + "available in the Administration");
        suite.addTestSuite(SkinCustomizationsTest.class, AlbatrossSkinExecutor.class);
        return suite;
    }

    protected void setUp() throws Exception
    {
        super.setUp();
        loginAsAdmin();
    }

    protected void tearDown() throws Exception
    {
        // Ensure that we reset the style
        openAdministrationPage();
        clickLinkWithText("Presentation");
        getSelenium().type("XWiki.XWikiPreferences_0_stylesheet", "style.css");
        clickEditSaveAndContinue();
    }

    public void testChangeDefaultStyleCss() throws Exception
    {
        openAdministrationPage();
        clickLinkWithText("Presentation");
        getSelenium().type("XWiki.XWikiPreferences_0_stylesheet", "somestyle.css");
        clickEditSaveAndContinue();
        assertTrue(getSelenium().isElementPresent(
            "xpath=//head/link[@href='/xwiki/bin/skin/skins/albatross/somestyle.css']"));
    }
}