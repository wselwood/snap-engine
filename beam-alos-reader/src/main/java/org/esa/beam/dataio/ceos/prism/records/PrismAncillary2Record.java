/*
 * $Id: PrismAncillary2Record.java,v 1.1 2006/09/13 09:12:34 marcop Exp $
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
import org.esa.beam.dataio.ceos.IllegalCeosFormatException;
import org.esa.beam.dataio.ceos.records.Ancillary2Record;

import java.io.IOException;

public class PrismAncillary2Record extends Ancillary2Record {

    private String _compressionMode;
    private double _ccdTemperature;
    private double _signalProcessingSectionTemperature;
    private double _absoluteCalibrationGain;
    private double _absoluteCalibrationOffset;

    public PrismAncillary2Record(final CeosFileReader reader) throws IOException, IllegalCeosFormatException {
        this(reader, -1);
    }

    public PrismAncillary2Record(final CeosFileReader reader, final long startPos) throws IOException,
                                                                                          IllegalCeosFormatException {
        super(reader, startPos);
    }

    @Override
    protected void readSpecificFields(final CeosFileReader reader) throws IOException,
                                                                          IllegalCeosFormatException {
        reader.seek(getAbsolutPosition(62));
        _compressionMode = reader.readAn(1);

        reader.seek(getAbsolutPosition(78));
        _ccdTemperature = reader.readFn(8);
        _signalProcessingSectionTemperature = reader.readFn(8);

        reader.seek(getAbsolutPosition(2702));
        _absoluteCalibrationGain = reader.readFn(8);
        _absoluteCalibrationOffset = reader.readFn(8);

    }

    public String getCompressionMode() {
        return _compressionMode;
    }

    public double getCcdTemperature() {
        return _ccdTemperature;
    }

    public double getSignalProcessingSectionTemperature() {
        return _signalProcessingSectionTemperature;
    }

    public double getAbsoluteCalibrationGain() {
        return _absoluteCalibrationGain;
    }

    public double getAbsoluteCalibrationOffset() {
        return _absoluteCalibrationOffset;
    }
}
