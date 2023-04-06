// ============================================================================
//
// Copyright (C) 2006-2019 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package com.amalto.workbench.utils;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.amalto.workbench.models.TreeObject;

public class ResourcesUtil {

    private static Log log = LogFactory.getLog(ResourcesUtil.class);

    public static String getXMLString(String uri, TreeObject treeObject) {
        String responseBody = "";//$NON-NLS-1$
        try {
            responseBody = HttpClientUtil.getStringContentByHttpget(uri, treeObject.getUsername(), treeObject.getPassword());
        } catch (XtentisException e) {
            log.error(e.getMessage(), e);
        }
        return responseBody;
    }

    public static HashMap<String, String> getResourcesMapFromURI(String uri, TreeObject treeObject) {
        HashMap<String, String> contentMap = new HashMap<String, String>();
        String responseBody = getXMLString(uri, treeObject);
        Document document = parsXMLString(responseBody);
        if (document == null) {
            return contentMap;
        }
        for (Iterator iterator = document.getRootElement().elementIterator("entry"); iterator.hasNext();) {//$NON-NLS-1$
            Element element = (Element) iterator.next();
            Element nameElement = element.element("name");//$NON-NLS-1$
            Element uriElement = element.element("uri");//$NON-NLS-1$
            if (nameElement != null && uriElement != null) {
                contentMap.put(nameElement.getStringValue(), uriElement.getStringValue());
            } else {
                contentMap.put(element.getStringValue(), uri + "/" + element.getStringValue());//$NON-NLS-1$
            }
        }
        return contentMap;
    }

    public static List<String> getResourcesNameListFromURI(String uri, TreeObject treeObject) throws Exception {
        List<String> nameList = new ArrayList<String>();
        String responseBody = getXMLString(uri, treeObject);
        // nameList=getNameList(responseBody);
        Document document = parsXMLString(responseBody);
        if (document == null) {
            return nameList;
        }
        for (Iterator iterator = document.getRootElement().elementIterator("entry"); iterator.hasNext();) {//$NON-NLS-1$
            Element element = (Element) iterator.next();
            Element nameElement = element.element("name");//$NON-NLS-1$

            if (nameElement != null) {
                nameList.add(nameElement.getStringValue());
            } else {
                nameList.add(element.getStringValue());
            }
        }
        return nameList;
    }

    private static Document parsXMLString(String responseBody) {
        SAXReader saxReader = new SAXReader();
        Document document = null;
        try {
            document = saxReader.read(new StringReader(responseBody));
        } catch (DocumentException e) {

            return null;
        }
        return document;
    }

    public static String getEndpointHost(String uri) {
        if (uri != null) {
            int startPos = uri.indexOf("//") + 2;//$NON-NLS-1$
            int endPos = uri.indexOf(":", startPos);//$NON-NLS-1$
            if (startPos != -1) {
                if (endPos != -1) {
                    return uri.substring(startPos, endPos);
                } else {
                    endPos = uri.indexOf("/", startPos); //$NON-NLS-1$
                    if (endPos != -1) {
                        return uri.substring(startPos, endPos);
                    }
                }
            }
        }
        return uri;
    }

    public static String getEndpointPort(String uri) {
        if (uri != null) {
            String[] splitString = uri.split(":", 3);//$NON-NLS-1$
            if (splitString.length == 3 && splitString[splitString.length - 1] != null) {
                return splitString[2].substring(0, splitString[2].indexOf("/"));//$NON-NLS-1$
            }
            if (splitString.length == 2) {
                return "80";
            }
        }
        return uri;
    }
}
