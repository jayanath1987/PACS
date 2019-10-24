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
package org.weasis.dicom.data;

import org.dcm4che3.data.Tag;
import org.weasis.dicom.data.xml.TagUtil;
import org.weasis.dicom.data.xml.XmlDescription;

public class SOPInstance implements XmlDescription {

    private final String sopInstanceUID;
    private String transferSyntaxUID = null;
    private String instanceNumber = null;
    private String directDownloadFile = null;

    public SOPInstance(String sopInstanceUID) {
        if (sopInstanceUID == null) {
            throw new IllegalArgumentException("sopInstanceIUID is null");
        }
        this.sopInstanceUID = sopInstanceUID;
    }

    public String getTransferSyntaxUID() {
        return transferSyntaxUID;
    }

    public void setTransferSyntaxUID(String transferSyntaxUID) {
        this.transferSyntaxUID = transferSyntaxUID;
    }

    public String getSOPInstanceIUID() {
        return sopInstanceUID;
    }

    public String getInstanceNumber() {
        return instanceNumber;
    }

    public void setInstanceNumber(String instanceNumber) {
        this.instanceNumber = instanceNumber == null ? null : instanceNumber.trim();
    }

    public String getDirectDownloadFile() {
        return directDownloadFile;
    }

    public void setDirectDownloadFile(String directDownloadFile) {
        this.directDownloadFile = directDownloadFile;
    }

    @Override
    public String toXml() {
        StringBuilder result = new StringBuilder();
        result.append("\n<");
        result.append(TagUtil.Level.INSTANCE);
        result.append(" ");
        TagUtil.addXmlAttribute(Tag.SOPInstanceUID, sopInstanceUID, result);
        // file_tsuid DICOM Transfer Syntax UID (0002,0010)
        TagUtil.addXmlAttribute(Tag.TransferSyntaxUID, transferSyntaxUID, result);
        TagUtil.addXmlAttribute(Tag.InstanceNumber, instanceNumber, result);
        TagUtil.addXmlAttribute(TagUtil.DirectDownloadFile, directDownloadFile, result);
        result.append("/>");

        return result.toString();
    }

}
