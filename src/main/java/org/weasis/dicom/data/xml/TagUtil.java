/*******************************************************************************
 * Copyright (c) 2014 Weasis Team.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Nicolas Roduit - initial API and implementation
 *******************************************************************************/
package org.weasis.dicom.data.xml;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.dcm4che3.data.ElementDictionary;
import org.dcm4che3.util.TagUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TagUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(TagUtil.class);

    private static final String DELIM_START = "${";
    private static final String DELIM_STOP = "}";

    public enum Level {
        PATIENT("Patient"), STUDY("Study"), SERIES("Series"), INSTANCE("Instance");

        private final String tag;

        private Level(String tag) {
            this.tag = tag;
        }

        @Override
        public String toString() {
            return tag;
        }
    }

    public static final String WadoCompressionRate = "WadoCompressionRate";
    public static final String WadoTransferSyntaxUID = "WadoTransferSyntaxUID";
    public static final String DirectDownloadFile = "DirectDownloadFile";
    public static final String DirectDownloadThumbnail = "DirectDownloadThumbnail";

    private TagUtil() {
    }

    public static String substVars(String val, String currentKey, Map<String, String> cycleMap, Properties configProps,
        Properties extProps) {

        Map<String, String> map = cycleMap == null ? new HashMap<String, String>() : cycleMap;
        map.put(currentKey, currentKey);

        int stopDelim = -1;
        int startDelim;

        do {
            stopDelim = val.indexOf(DELIM_STOP, stopDelim + 1);
            if (stopDelim < 0) {
                return val;
            }
            startDelim = val.indexOf(DELIM_START);
            if (startDelim < 0) {
                return val;
            }
            while (stopDelim >= 0) {
                int idx = val.indexOf(DELIM_START, startDelim + DELIM_START.length());
                if ((idx < 0) || (idx > stopDelim)) {
                    break;
                } else if (idx < stopDelim) {
                    startDelim = idx;
                }
            }
        } while ((startDelim > stopDelim) && (stopDelim >= 0));

        String variable = val.substring(startDelim + DELIM_START.length(), stopDelim);

        if (map.get(variable) != null) {
            throw new IllegalArgumentException("recursive variable reference: " + variable);
        }
        String substValue = System.getProperty(variable);
        if (substValue == null) {
            substValue = configProps == null ? null : configProps.getProperty(variable, null);
            if (substValue == null) {
                substValue = extProps == null ? null : extProps.getProperty(variable, null);
            }
        }

        map.remove(variable);
        String result =
            val.substring(0, startDelim) + substValue + val.substring(stopDelim + DELIM_STOP.length(), val.length());
        return substVars(result, currentKey, map, configProps, extProps);
    }

    public static void addXmlAttribute(int tagID, String value, StringBuilder result) {
        if (value != null) {
            String key = ElementDictionary.getStandardElementDictionary().keywordOf(tagID);
            if (key == null) {
                LOGGER.error("Cannot find keyword of tagID {}", TagUtils.toString(tagID));
            } else {
                result.append(key);
                result.append("=\"");
                result.append(EscapeChars.forXML(value));
                result.append("\" ");
            }
        }
    }

    public static void addXmlAttribute(String tag, String value, StringBuilder result) {
        if (tag != null && value != null) {
            result.append(tag);
            result.append("=\"");
            result.append(EscapeChars.forXML(value));
            result.append("\" ");
        }
    }

    public static void addXmlAttribute(String tag, Boolean value, StringBuilder result) {
        if (tag != null && value != null) {
            result.append(tag);
            result.append("=\"");
            result.append(value ? "true" : "false");
            result.append("\" ");
        }
    }

    public static void addXmlAttribute(String tag, List<String> value, StringBuilder result) {
        if (tag != null && value != null) {
            result.append(tag);
            result.append("=\"");
            int size = value.size();
            for (int i = 0; i < size - 1; i++) {
                result.append(EscapeChars.forXML(value.get(i)) + ",");
            }
            if (size > 0) {
                result.append(EscapeChars.forXML(value.get(size - 1)));
            }
            result.append("\" ");
        }
    }
}
