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
package org.xwiki.test.selenium;

import java.io.IOException;

import junit.framework.Test;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.xwiki.test.selenium.framework.AbstractXWikiTestCase;
import org.xwiki.test.selenium.framework.ColibriSkinExecutor;
import org.xwiki.test.selenium.framework.XWikiTestSuite;

/**
 * Tests the wiki editor.
 * 
 * @version $Id$
 */
public class WikiEditorTest extends AbstractXWikiTestCase
{
    private static final String SYNTAX = "xwiki/2.1";

    public static Test suite()
    {
        XWikiTestSuite suite = new XWikiTestSuite("Tests the wiki editor");
        suite.addTestSuite(WikiEditorTest.class, ColibriSkinExecutor.class);
        return suite;
    }

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        loginAsAdmin();
    }

    public void testEmptyLineAndSpaceCharactersBeforeSectionTitleIsNotRemoved()
    {
        createPage("Test", "WikiEdit", "\n== Section ==\n\ntext", SYNTAX);
        open("Test", "WikiEdit", "edit", "editor=wiki");
        assertEquals("\n== Section ==\n\ntext", getFieldValue("content"));
    }

    public void testBoldButton()
    {
        testToolBarButton("Bold", "**%s**", "Text in Bold");
    }

    public void testItalicsButton()
    {
        testToolBarButton("Italics", "//%s//", "Text in Italics");
    }

    public void testUnderlineButton()
    {
        testToolBarButton("Underline", "__%s__", "Text in Underline");
    }

    public void testLinkButton()
    {
        testToolBarButton("Internal Link", "[[%s]]", "Link Example");
    }

    public void testHRButton()
    {
        testToolBarButton("Horizontal ruler", "\n----\n", "");
    }

    public void testImageButton()
    {
        testToolBarButton("Attached Image", "[[image:%s]]", "example.jpg");
    }

    /**
     * Tests that users can completely remove the content from a document (make the document empty). In previous
     * versions (pre-1.5M2), removing all content in page had no effect. See XWIKI-1007.
     */
    public void testEmptyDocumentContentIsAllowed()
    {
        createPage("Test", "EmptyWikiContent", "this is some content", SYNTAX);
        editInWikiEditor("Test", "EmptyWikiContent", SYNTAX);
        setFieldValue("content", "");
        clickEditSaveAndView();
        assertFalse(getSelenium().isAlertPresent());
        assertEquals(-1, getSelenium().getLocation().indexOf("/edit/"));
        assertTextNotPresent("this is some content");
    }

    /**
     * Test the ability to add edit comments and the ability to disable the edit comments feature.
     */
    public void testEditComment() throws IOException
    {
        try {
            editInWikiEditor("Test", "EditComment", SYNTAX);
            assertTrue(getSelenium().isVisible("comment"));

            // Test for XWIKI-2487: Hiding the edit comment field doesn't work
            setXWikiConfiguration("xwiki.editcomment.hidden=1");
            editInWikiEditor("Test", "EditComment", SYNTAX);
            assertFalse(getSelenium().isVisible("comment"));
        } finally {
            setXWikiConfiguration("xwiki.editcomment.hidden=0");
        }
    }

    /**
     * Verify that the preview works when the document content contains script requiring programming rights. See also
     * XWIKI-2490.
     */
    public void testPreviewModeWithContentRequiringProgrammingRights()
    {
        editInWikiEditor("Test", "PreviewMode", SYNTAX);
        setFieldValue("content", "{{velocity}}$xwiki.hasAccessLevel('programming') $tdoc.author $tdoc.contentAuthor $tdoc.creator{{/velocity}}");
        clickEditPreview();
        assertTextPresent("true XWiki.Admin XWiki.Admin XWiki.Admin");
    }

    /**
     * Verify minor edit feature is working
     */
    public void testMinorEdit()
    {
        try {
            editInWikiEditor("Test", "MinorEdit", SYNTAX);
            // Note: Revision 2.1 is used since starting with 1.9-rc-1 editInWikiEditor creates an initial version to
            // set the syntax.
            setFieldValue("content", "version=1.2");
            // Save & Continue = minor edit.
            clickEditSaveAndContinue();
            setFieldValue("content", "version=2.1");
            clickEditSaveAndView();

            open("Test", "MinorEdit", "viewrev", "rev=2.1");
            assertTextPresent("version=2.1");

            editInWikiEditor("Test", "MinorEdit", SYNTAX);
            setFieldValue("content", "version=2.2");
            getSelenium().click("minorEdit");
            clickEditSaveAndView();

            open("Test", "MinorEdit", "viewrev", "rev=2.2");
            assertTextPresent("version=2.2");
        } finally {
            deletePage("Test", "MinorEdit");
        }
    }

    /**
     * Tests that the specified tool bar button works.
     * 
     * @param buttonTitle the title of a tool bar button
     * @param format the format of the text inserted by the specified button
     * @param defaultText the default text inserted if there's no text selected in the text area
     */
    private void testToolBarButton(String buttonTitle, String format, String defaultText)
    {
        editInWikiEditor(this.getClass().getSimpleName(), getName(), SYNTAX);
        WebElement textArea = getDriver().findElement(By.id("content"));
        textArea.clear();
        textArea.sendKeys("a");
        String buttonLocator = "//img[@title = '" + buttonTitle + "']";
        getSelenium().click(buttonLocator);
        // Type b and c on two different lines and move the caret after b.
        textArea.sendKeys("b", Keys.ENTER, "c", Keys.ARROW_LEFT, Keys.ARROW_LEFT);
        getSelenium().click(buttonLocator);
        // Move the caret after c, type d and e, then select d.
        textArea.sendKeys(Keys.PAGE_DOWN, Keys.END, "de", Keys.ARROW_LEFT, Keys.chord(Keys.SHIFT, Keys.ARROW_LEFT));
        getSelenium().click(buttonLocator);
        if (defaultText.isEmpty()) {
            assertEquals("a" + format + "b" + format + "\nc" + format + "de", textArea.getAttribute("value"));
        } else {
            assertEquals(
                String.format("a" + format + "b" + format + "\nc" + format + "e", defaultText, defaultText, "d"),
                textArea.getAttribute("value"));
        }
    }
}
