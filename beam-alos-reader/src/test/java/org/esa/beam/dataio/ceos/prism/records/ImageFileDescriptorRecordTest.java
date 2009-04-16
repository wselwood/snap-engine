/*
 * $Id: ImageFileDescriptorRecordTest.java,v 1.2 2006/09/14 09:15:55 marcop Exp $
 *
 * Copyright (C) 2002 by Brockmann Consult (info@brockmann-consult.de)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation. This program is distributed in the hope it will
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.esa.beam.dataio.ceos.prism.records;

import org.esa.beam.dataio.ceos.CeosFileReader;
import org.esa.beam.dataio.ceos.CeosTestHelper;
import org.esa.beam.dataio.ceos.IllegalCeosFormatException;
import org.esa.beam.dataio.ceos.records.BaseImageFileDescriptorRecord;
import org.esa.beam.dataio.ceos.records.BaseImageFileDescriptorRecordTest;

import javax.imageio.stream.ImageOutputStream;
import java.io.IOException;

public class ImageFileDescriptorRecordTest extends BaseImageFileDescriptorRecordTest {

    @Override
    protected BaseImageFileDescriptorRecord createImageFileDescriptorRecord(final CeosFileReader reader) throws
                                                                                                         IOException,
                                                                                                         IllegalCeosFormatException {
        return new ImageFileDescriptorRecord(reader);

    }

    @Override
    protected BaseImageFileDescriptorRecord createImageFileDescriptor(final CeosFileReader reader,
                                                                      final int startPos) throws IOException,
                                                                                                 IllegalCeosFormatException {
        return new ImageFileDescriptorRecord(reader, startPos);

    }

    @Override
    protected void writeBytes341To392(final ImageOutputStream ios) throws IOException {
        // Field 28
        ios.writeBytes("   148SB"); // auxDataLocator
        // Field 29
        ios.writeBytes("  4912SB"); // qualityInformationLocator
        // Field 30
        ios.writeBytes("  61 4SB"); // extractionStartPointLocator
        CeosTestHelper.writeBlanks(ios, 28);

    }

    @Override
    protected void assertBytes341To392(final BaseImageFileDescriptorRecord record) {
        final ImageFileDescriptorRecord imageFileDescriptorRecord = (ImageFileDescriptorRecord) record;

        assertEquals("   148SB", imageFileDescriptorRecord.getLocatorAUXData());
        assertEquals("  4912SB", imageFileDescriptorRecord.getLocatorQualityInformation());
        assertEquals("  61 4SB", imageFileDescriptorRecord.getLocatorExtractionStartPoint());

        // nothing else to test
    }
}
