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
package org.xwiki.test.rest;

import java.util.UUID;

import javax.ws.rs.core.MediaType;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.junit.Assert;
import org.junit.Test;
import org.xwiki.rest.Relations;
import org.xwiki.rest.model.jaxb.Link;
import org.xwiki.rest.model.jaxb.Page;
import org.xwiki.rest.model.jaxb.PageSummary;
import org.xwiki.rest.model.jaxb.Pages;
import org.xwiki.rest.model.jaxb.Tag;
import org.xwiki.rest.model.jaxb.Tags;
import org.xwiki.rest.resources.pages.PageResource;
import org.xwiki.rest.resources.pages.PageTagsResource;
import org.xwiki.rest.resources.tags.PagesForTagsResource;
import org.xwiki.rest.resources.tags.TagsResource;
import org.xwiki.test.rest.framework.AbstractHttpTest;
import org.xwiki.test.rest.framework.TestConstants;
import org.xwiki.test.ui.TestUtils;

/**
 * @version $Id$
 */
public class TagsResourceTest extends AbstractHttpTest
{
    @Override
    @Test
    public void testRepresentation() throws Exception
    {
        String tagName = UUID.randomUUID().toString();

        createPageIfDoesntExist(TestConstants.TEST_SPACE_NAME, TestConstants.TEST_PAGE_NAME, "Test");

        GetMethod getMethod =
            executeGet(getUriBuilder(PageResource.class).build(getWiki(), TestConstants.TEST_SPACE_NAME,
                TestConstants.TEST_PAGE_NAME).toString());
        Assert.assertEquals(getHttpMethodInfo(getMethod), HttpStatus.SC_OK, getMethod.getStatusCode());

        Tags tags = objectFactory.createTags();
        Tag tag = objectFactory.createTag();
        tag.setName(tagName);
        tags.getTags().add(tag);

        PutMethod putMethod =
            executePutXml(
                getUriBuilder(PageTagsResource.class).build(getWiki(), TestConstants.TEST_SPACE_NAME,
                    TestConstants.TEST_PAGE_NAME).toString(), tags, TestUtils.ADMIN_CREDENTIALS.getUserName(),
                TestUtils.ADMIN_CREDENTIALS.getPassword());
        Assert.assertEquals(getHttpMethodInfo(putMethod), HttpStatus.SC_ACCEPTED, putMethod.getStatusCode());

        getMethod =
            executeGet(getUriBuilder(PageTagsResource.class).build(getWiki(), TestConstants.TEST_SPACE_NAME,
                TestConstants.TEST_PAGE_NAME).toString());
        Assert.assertEquals(getHttpMethodInfo(getMethod), HttpStatus.SC_OK, getMethod.getStatusCode());

        tags = (Tags) unmarshaller.unmarshal(getMethod.getResponseBodyAsStream());
        boolean found = false;
        for (Tag t : tags.getTags()) {
            if (tagName.equals(t.getName())) {
                found = true;
                break;
            }
        }
        Assert.assertTrue(found);

        getMethod = executeGet(getUriBuilder(TagsResource.class).build(getWiki()).toString());
        Assert.assertEquals(getHttpMethodInfo(getMethod), HttpStatus.SC_OK, getMethod.getStatusCode());

        tags = (Tags) unmarshaller.unmarshal(getMethod.getResponseBodyAsStream());
        found = false;
        for (Tag t : tags.getTags()) {
            if (tagName.equals(t.getName())) {
                found = true;
                break;
            }
        }
        Assert.assertTrue(found);

        getMethod = executeGet(getUriBuilder(PagesForTagsResource.class).build(getWiki(), tagName).toString());
        Assert.assertEquals(getHttpMethodInfo(getMethod), HttpStatus.SC_OK, getMethod.getStatusCode());

        Pages pages = (Pages) unmarshaller.unmarshal(getMethod.getResponseBodyAsStream());

        found = false;
        for (PageSummary pageSummary : pages.getPageSummaries()) {
            if (pageSummary.getFullName().equals(
                String.format("%s.%s", TestConstants.TEST_SPACE_NAME, TestConstants.TEST_PAGE_NAME))) {
                found = true;
            }
        }
        Assert.assertTrue(found);

        getMethod =
            executeGet(getUriBuilder(PageResource.class).build(getWiki(), TestConstants.TEST_SPACE_NAME,
                TestConstants.TEST_PAGE_NAME).toString());
        Assert.assertEquals(getHttpMethodInfo(getMethod), HttpStatus.SC_OK, getMethod.getStatusCode());

        Page page = (Page) unmarshaller.unmarshal(getMethod.getResponseBodyAsStream());
        Link tagsLink = getFirstLinkByRelation(page, Relations.TAGS);
        Assert.assertNotNull(tagsLink);
    }

    @Test
    public void testPUTTagsWithTextPlain() throws Exception
    {
        createPageIfDoesntExist(TestConstants.TEST_SPACE_NAME, TestConstants.TEST_PAGE_NAME, "Test");

        String tagName = UUID.randomUUID().toString();

        GetMethod getMethod =
            executeGet(getUriBuilder(PageResource.class).build(getWiki(), TestConstants.TEST_SPACE_NAME,
                TestConstants.TEST_PAGE_NAME).toString());
        Assert.assertEquals(getHttpMethodInfo(getMethod), HttpStatus.SC_OK, getMethod.getStatusCode());

        PutMethod putMethod =
            executePut(
                getUriBuilder(PageTagsResource.class).build(getWiki(), TestConstants.TEST_SPACE_NAME,
                    TestConstants.TEST_PAGE_NAME).toString(), tagName, MediaType.TEXT_PLAIN,
                TestUtils.ADMIN_CREDENTIALS.getUserName(), TestUtils.ADMIN_CREDENTIALS.getPassword());
        Assert.assertEquals(getHttpMethodInfo(putMethod), HttpStatus.SC_ACCEPTED, putMethod.getStatusCode());

        getMethod =
            executeGet(getUriBuilder(PageTagsResource.class).build(getWiki(), TestConstants.TEST_SPACE_NAME,
                TestConstants.TEST_PAGE_NAME).toString());
        Assert.assertEquals(getHttpMethodInfo(getMethod), HttpStatus.SC_OK, getMethod.getStatusCode());

        Tags tags = (Tags) unmarshaller.unmarshal(getMethod.getResponseBodyAsStream());
        boolean found = false;
        for (Tag t : tags.getTags()) {
            if (tagName.equals(t.getName())) {
                found = true;
                break;
            }
        }
        Assert.assertTrue(found);
    }

    @Test
    public void testPUTTagsFormUrlEncoded() throws Exception
    {
        createPageIfDoesntExist(TestConstants.TEST_SPACE_NAME, TestConstants.TEST_PAGE_NAME, "Test");

        String tagName = UUID.randomUUID().toString();

        GetMethod getMethod =
            executeGet(getUriBuilder(PageResource.class).build(getWiki(), TestConstants.TEST_SPACE_NAME,
                TestConstants.TEST_PAGE_NAME).toString());
        Assert.assertEquals(getHttpMethodInfo(getMethod), HttpStatus.SC_OK, getMethod.getStatusCode());

        NameValuePair[] nameValuePairs = new NameValuePair[1];
        nameValuePairs[0] = new NameValuePair("tags", tagName);

        PostMethod postMethod =
            executePostForm(
                String.format(
                    "%s?method=PUT",
                    getUriBuilder(PageTagsResource.class).build(getWiki(), TestConstants.TEST_SPACE_NAME,
                        TestConstants.TEST_PAGE_NAME).toString()), nameValuePairs,
                TestUtils.ADMIN_CREDENTIALS.getUserName(), TestUtils.ADMIN_CREDENTIALS.getPassword());
        Assert.assertEquals(getHttpMethodInfo(postMethod), HttpStatus.SC_ACCEPTED, postMethod.getStatusCode());

        getMethod =
            executeGet(getUriBuilder(PageTagsResource.class).build(getWiki(), TestConstants.TEST_SPACE_NAME,
                TestConstants.TEST_PAGE_NAME).toString());
        Assert.assertEquals(getHttpMethodInfo(getMethod), HttpStatus.SC_OK, getMethod.getStatusCode());

        Tags tags = (Tags) unmarshaller.unmarshal(getMethod.getResponseBodyAsStream());
        boolean found = false;
        for (Tag t : tags.getTags()) {
            if (tagName.equals(t.getName())) {
                found = true;
                break;
            }
        }
        Assert.assertTrue(found);
    }
}
