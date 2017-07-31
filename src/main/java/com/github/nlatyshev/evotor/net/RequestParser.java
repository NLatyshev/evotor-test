package com.github.nlatyshev.evotor.net;

import com.github.nlatyshev.evotor.net.model.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Just avoid external dependencies. In other case groovy slurper is best choice
 * It closes input stream, because it means is can be read only one once
 */
public class RequestParser {
    private static final Logger log = LoggerFactory.getLogger(RequestParser.class);

    public Request parse(InputStream is) {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();

            final String[] type = {null};
            final Map<String, String> params = new HashMap<String, String>();
            parser.parse(is, new DefaultHandler() {
                boolean request;
                String child;
                String nameAttr;

                @Override
                public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                    if (!request && qName.equals("request")) {
                        request = true;
                    } else if (request && child == null) {
                        child = qName;
                        nameAttr = attributes.getValue("name");
                    } else {
                        throw new SAXException("Expected 'request' root element or its first level children, but got " + qName);
                    }
                }

                @Override
                public void characters(char[] ch, int start, int length) throws SAXException {
                    if (child != null) {
                        switch (child) {
                            case "request-type":
                                if (type[0] == null) {
                                    type[0] = new String(ch, start, length);
                                } else {
                                    throw new SAXException("Duplicated 'request-type' element");
                                }
                                break;
                            case "extra":
                                if (nameAttr != null) {
                                    params.put(nameAttr, new String(ch, start, length));
                                } else {
                                    throw new SAXException("'name' attribute is absent");
                                }
                                break;
                            default:
                                throw new SAXException("Expected 'request-type' or 'extra' but got" + child);
                        }
                    }
                }

                @Override
                public void endElement(String uri, String localName, String qName) throws SAXException {
                    if (request && qName.equals("request")) {
                        request = false;
                    } else if (request && child != null) {
                        child = null;
                    }
                }
            });
            return new Request(type[0], params);
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot parse request", e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    log.warn("Cannot close input stream");
                }
            }
        }

    }

}
