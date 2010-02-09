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
package com.xpn.xwiki.it.framework;

import javax.xml.xpath.XPathConstants;

import org.w3c.dom.NodeList;
import org.xwiki.validator.DutchWebGuidelinesValidator;
import org.xwiki.validator.ValidationError.Type;

public class CustomDutchWebGuidelinesValidator extends DutchWebGuidelinesValidator
{
    private static final String SPACE_META = "space";
    
    private String fullName = "";
    
    /**
     * Set the fullName of the page being analyzed.
     *  
     * @param fullName fullName of the page
     */
    public void setFullPageName(String fullName) 
    {
        this.fullName = fullName;
    }
    
    /**
     * @param metaName name of the meta to get
     * @return the value for the given meta
     */
    private String getMeta(String metaName)
    {
        String exprString = "//meta[@name='" + metaName + "']";
        NodeList meta = (NodeList) evaluate(document, exprString, XPathConstants.NODESET);
        
        return getAttributeValue(meta.item(0), ATTR_CONTENT);
    }
    
    /**
     * @return true if the current page is the give page, false otherwise.
     */
    private boolean isPage(String pageName)
    {
        return pageName.equals(fullName);
    }
    
    /**
     * Use the p (paragraph) element to indicate paragraphs. Do not use the br (linebreak) element to separate
     * paragraphs.
     */
    @Override
    public void validateRpd3s4()
    {
        if (!isPage("XWiki.XWikiSyntax")) {
            super.validateRpd3s4();
        }
    }

    /**
     * Avoid using the sup (superscript) and sub (subscript) element if possible. XWiki exception: wiki syntax
     * allows using sub and sup tags, this usage is demonstrated in the Sandbox space and the XWiki.XWikiSyntax page.
     */
    @Override
    public void validateRpd3s9()
    {
        if (!isPage("XWiki.XWikiSyntax") && !isPage("Sandbox.WebHome")) {
            super.validateRpd3s9();
        }
    }
    
    /**
     * Use ol (ordered list) and ul (unordered list) elements to indicate lists. XWiki exception: XWiki.XWikiSyntax 
     * shows the wiki syntax to use to create lists, this syntax is precisely what's forbidden to use in the generated 
     * html. 
     */
    @Override
    public void validateRpd3s13()
    {
        if (!isPage("XWiki.XWikiSyntax")) {
            super.validateRpd3s13();
        }
    }
    
    /**
     * CSS should be placed in linked files and not mixed with the HTML source code. XWiki exceptions: in the ColorTheme
     * application we have to allow the use of inline styles, this is the only way to offer a preview of the themes 
     * color. In XWiki.XWikiSyntax usage of style custom parameter is demonstrated. In Panels.PanelWizard and 
     * XWiki.Treeview the use of JS libraries make the use of inline styles mandatory.
     * 
     */
    @Override
    public void validateRpd9s1()
    {
        String exprString = "//*[@style]";
        
        if (!getMeta(SPACE_META).equals("ColorThemes") && !isPage("XWiki.XWikiSyntax") 
            && !isPage("Panels.PanelWizard") &&  !isPage("XWiki.Treeview")) {
            // Usage of the style attribute is strictly forbidden in the other spaces.
            assertFalse(Type.ERROR, "rpd9s1.attr", ((Boolean) evaluate(getElement(ELEM_BODY), exprString,
                XPathConstants.BOOLEAN)));
        }
        
        // <style> tags are forbidden everywhere.
        assertFalse(Type.ERROR, "rpd9s1.tag",
            getChildren(getElement(ELEM_BODY), "style").getNodeList().getLength() > 0);
    }
    
    /**
     * Use the scope attribute to associate table labels (th cells) with columns or rows. XWiki exception: wiki syntax
     * allows using table headers without scope, this usage is demonstrated in the Sandbox space and the 
     * XWiki.XWikiSyntax page.
     */
    @Override
    public void validateRpd11s4()
    {
        if (!isPage("XWiki.XWikiSyntax") && !isPage("Sandbox.WebHome")) {
            super.validateRpd11s4();
        }
    }
    
    /**
     * Use the headers and id attributes to associate table labels (th cells) with individual cells in complex tables.
     * XWiki exception: wiki syntax allows using tables without headers, this usage is demonstrated in the Sandbox 
     * space and the XWiki.XWikiSyntax page.
     */
    @Override
    public void validateRpd11s5()
    {
        if (!isPage("XWiki.XWikiSyntax") && !isPage("Sandbox.WebHome")) {
            super.validateRpd11s5();
        }
    }
    
    
    
}
