/*
 /**
 * Copyright 2005 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nutch.plugin;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.nutch.util.NutchConf;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * The <code>PluginManifestParser</code> parser just parse the manifest file
 * in all plugin directories.
 * 
 * @author joa23
 */
public class PluginManifestParser {
    public static final Logger LOG = PluginRepository.LOG;

    private static final boolean WINDOWS = System.getProperty("os.name")
            .startsWith("Windows");

    /**
     * Returns a list with plugin descriptors.
     * 
     * @return ArrayList
     *  
     */
    public static ArrayList parsePluginFolder() {
        ArrayList list = new ArrayList();
        String[] pluginFolders = NutchConf.get().getStrings("plugin.folders");
        Pattern excludes = Pattern.compile(NutchConf.get().get(
                "plugin.excludes", ""));
        Pattern includes = Pattern.compile(NutchConf.get().get(
                "plugin.includes", ""));
        if (pluginFolders == null)
            throw new IllegalArgumentException("plugin.folders is not defined");
        for (int i = 0; i < pluginFolders.length; i++) {
            String name = pluginFolders[i];
            File directory = getPluginFolder(name);
            if (directory == null)
                continue;
            LOG.info("Plugins: looking in: " + directory);
            File[] files = directory.listFiles();
            if (files == null)
                continue;
            for (int j = 0; j < files.length; j++) {
                File oneSubFolder = files[j];
                if (oneSubFolder.isDirectory()) {

                    if (!includes.matcher(oneSubFolder.getName()).matches()) {
                        LOG.info("not including: " + oneSubFolder);
                        continue;
                    }

                    if (excludes.matcher(oneSubFolder.getName()).matches()) {
                        LOG.info("excluding: " + oneSubFolder);
                        continue;
                    }

                    String manifestPath = oneSubFolder.getAbsolutePath()
                            + File.separator + "plugin.xml";
                    try {
                        LOG.info("parsing: " + manifestPath);
                        list.add(parseManifestFile(manifestPath));
                    } catch (MalformedURLException e) {
                        LOG.info(e.toString());
                    } catch (SAXException e) {
                        LOG.info(e.toString());
                    } catch (IOException e) {
                        LOG.info(e.toString());
                    } catch (ParserConfigurationException e) {
                        LOG.info(e.toString());
                    }
                }
            }
        }
        return list;
    }

    /**
     * Return the named plugin folder. If the name is absolute then it is
     * returned. Otherwise, for relative names, the classpath is scanned.
     */
    static File getPluginFolder(String name) {
        File directory = new File(name);
        if (!directory.isAbsolute()) {
            URL url = PluginManifestParser.class.getClassLoader().getResource(
                    name);
            if (url == null) {
                LOG.info("Plugins: directory not found: " + name);
                return null;
            } else if (!"file".equals(url.getProtocol())) {
                LOG.info("Plugins: not a file: url. Can't load plugins from: "
                        + url);
                return null;
            }
            String path = url.getPath();
            if (WINDOWS && path.startsWith("/")) // patch a windows bug
                path = path.substring(1);
            try {
                path = URLDecoder.decode(path, "UTF-8"); // decode the url path
            } catch (UnsupportedEncodingException e) {
            }
            directory = new File(path);
        }
        return directory;
    }

    /**
     * @param manifestPath
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     * @throws MalformedURLException
     */
    private static PluginDescriptor parseManifestFile(String pManifestPath)
            throws MalformedURLException, SAXException, IOException,
            ParserConfigurationException {
        Document document = parseXML(new File(pManifestPath).toURL());
        String pPath = new File(pManifestPath).getParent();
        return parsePlugin(document, pPath);
    }

    /**
     * @param url
     * @return Document
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     * @throws DocumentException
     */
    private static Document parseXML(URL url) throws SAXException, IOException,
            ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(url.openStream());
    }

    /**
     * @param pDocument
     * @throws MalformedURLException
     */
    private static PluginDescriptor parsePlugin(Document pDocument, String pPath)
            throws MalformedURLException {
        Element rootElement = pDocument.getDocumentElement();
        String id = rootElement.getAttribute("id");
        String name = rootElement.getAttribute("name");
        String version = rootElement.getAttribute("version");
        String providerName = rootElement.getAttribute("provider-name");
        String pluginClazz = null;
        if (rootElement.getAttribute("class").trim().length() > 0) {
            pluginClazz = rootElement.getAttribute("class");
        }
        PluginDescriptor pluginDescriptor = new PluginDescriptor(id, version,
                name, providerName, pluginClazz, pPath);
                 LOG.fine("plugin: id="+id+" name="+name+" version="+version
                 +" provider="+providerName+"class="+pluginClazz);
        parseExtension(rootElement, pluginDescriptor);
        parseExtensionPoints(rootElement, pluginDescriptor);
        parseLibraries(rootElement, pluginDescriptor);
        return pluginDescriptor;
    }

    /**
     * @param pRootElement
     * @param pDescriptor
     * @throws MalformedURLException
     */
    private static void parseLibraries(Element pRootElement,
            PluginDescriptor pDescriptor) throws MalformedURLException {
        NodeList nodelist = pRootElement.getElementsByTagName("runtime");
        if (nodelist.getLength() > 0) {

            Element runtime = (Element) nodelist.item(0);

            NodeList libraries = runtime.getElementsByTagName("library");
            for (int i = 0; i < libraries.getLength(); i++) {
                Element library = (Element) libraries.item(i);
                String libName = library.getAttribute("name");
                NodeList list = library.getElementsByTagName("export");
                Element exportElement = (Element) list.item(0);
                if (exportElement != null)
                    pDescriptor.addExportedLibRelative(libName);
                else
                    pDescriptor.addNotExportedLibRelative(libName);
            }
        }
    }

    /**
     * @param rootElement
     * @param pluginDescriptor
     */
    private static void parseExtensionPoints(Element pRootElement,
            PluginDescriptor pPluginDescriptor) {
        NodeList list = pRootElement.getElementsByTagName("extension-point");
        if (list != null) {
            for (int i = 0; i < list.getLength(); i++) {
                Element oneExtensionPoint = (Element) list.item(i);
                String id = oneExtensionPoint.getAttribute("id");
                String name = oneExtensionPoint.getAttribute("name");
                String schema = oneExtensionPoint.getAttribute("schema");
                ExtensionPoint extensionPoint = new ExtensionPoint(id, name,
                        schema);
                //LOG.fine("plugin: point="+id);
                pPluginDescriptor.addExtensionPoint(extensionPoint);
            }
        }
    }

    /**
     * @param rootElement
     * @param pluginDescriptor
     */
    private static void parseExtension(Element pRootElement,
            PluginDescriptor pPluginDescriptor) {
        NodeList extensions = pRootElement.getElementsByTagName("extension");
        if (extensions != null) {
            for (int i = 0; i < extensions.getLength(); i++) {
                Element oneExtension = (Element) extensions.item(i);
                String pointId = oneExtension.getAttribute("point");
                
                NodeList extensionImplementations = oneExtension
                        .getChildNodes();
                if (extensionImplementations != null) {
                    for (int j = 0; j < extensionImplementations.getLength(); j++) {
                        Node node = extensionImplementations.item(j);
                        if (!node.getNodeName().equals("implementation")) {
                            continue;
                        }
                        Element oneImplementation = (Element) node;
                        String id = oneImplementation.getAttribute("id");
                        String extensionClass = oneImplementation
                                .getAttribute("class");
                        LOG.info("impl: point=" + pointId + " class="
                                + extensionClass);
                        Extension extension = new Extension(pPluginDescriptor,
                                pointId, id, extensionClass);
                        NamedNodeMap list = oneImplementation.getAttributes();
                        for (int k = 0; k < list.getLength(); k++) {
                            Node attribute = list.item(k);
                            String name = attribute.getNodeName();
                            if (name.equals("id") || name.equals("class"))
                                continue;
                            String value = attribute.getNodeValue();
                            extension.addAttribute(name, value);
                        }
                        pPluginDescriptor.addExtension(extension);
                    }
                }
            }
        }
    }
}
