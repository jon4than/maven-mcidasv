/*
 * This file is part of McIDAS-V
 *
 * Copyright 2007-2014
 * Space Science and Engineering Center (SSEC)
 * University of Wisconsin - Madison
 * 1225 W. Dayton Street, Madison, WI 53706, USA
 * http://www.ssec.wisc.edu/mcidas
 * 
 * All Rights Reserved
 * 
 * McIDAS-V is built on Unidata's IDV and SSEC's VisAD libraries, and
 * some McIDAS-V source code is based on IDV and VisAD source code.  
 * 
 * McIDAS-V is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * McIDAS-V is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser Public License
 * along with this program.  If not, see http://www.gnu.org/licenses.
 */
package edu.wisc.ssec.mcidasv.util;

import java.io.IOException;

import edu.wisc.ssec.mcidas.adde.AddeException;

import visad.VisADException;
import visad.data.mcidas.AreaAdapter;

/**
 * {@code ErrorCodeAreaAdapter} sole purpose is to allow McIDAS-V to associate
 * the error codes in
 * {@link edu.wisc.ssec.mcidas.adde.AddeException AddeException} with any
 * exceptions generated by {@link visad.data.mcidas.AreaAdapter AreaAdapter}.
 */
public class ErrorCodeAreaAdapter {

//    private static final Logger logger = LoggerFactory.getLogger(ErrorCodeAreaAdapter.class);

    public static final String GENERIC_EXCEPTION_MESSAGE = "Could not create VisAD data object.";

    /**
     * Disallow instances of {@code ErrorCodeAdapter}.
     */
    private ErrorCodeAreaAdapter() { }

    /**
     * Create a {@code AreaAdapter} using the given {@code url}.
     *
     * @param url {@code URL} used to locate AREA. Cannot be {@code null}.
     *
     * @return Either the {@code AreaAdapter} for the given {@code url},
     * or {@code null}.
     *
     * @throws AddeException if the {@code AreaAdapter} had problems.
     */
    public static AreaAdapter createAreaAdapter(final String url) throws AddeException {
        AreaAdapter aa = null;
        try {
            aa = new AreaAdapter(url, false);
        } catch (IOException e) {
            int errorCode = searchStackTrace(e.getCause());
            if (errorCode == 0) {
                throw new AddeException("Could not connect to URL: " + url, e);
            } else {
                throw new AddeException(errorCode, GENERIC_EXCEPTION_MESSAGE, e);
            }
        } catch (VisADException e) {
            int errorCode = searchStackTrace(e.getCause());
            if (errorCode == 0) {
                throw new AddeException(GENERIC_EXCEPTION_MESSAGE, e);
            } else {
                throw new AddeException(errorCode, GENERIC_EXCEPTION_MESSAGE, e);
            }
        }
        return aa;
    }

    // yes. this approach is awful. but since i can't modify the visad code
    // (which is what should be done!)...
    private static int searchStackTrace(final Throwable cause) {
        if (cause == null || cause.getMessage() == null) {
            return -1;
        }

        String message = cause.getMessage().trim();
        if ("DAY= must be used with archived datasets".equals(message)) {
            return -1000;
        } else if ("No images satisfy the selection criteria".equals(message)) {
            return -5000;
        } else if ("Accounting data was not valid".equals(message)) {
            return -6000;
        } else if ("Band keyword required for multi-banded image".equals(message)) {
            return -11011;
        } else if (message.startsWith("Units requested are not available for this")) {
            return -11007;
        } else if ("The requested portion of the image does not exist".equals(message)) {
            return -11010;
        } else if ("Unable to initialize navigation for this image".equals(message)) {
            return -11001;
        } else if (message.endsWith("not present")) {
            return -11003;
        } else if (message.startsWith("Band is not available in ") && message.endsWith(" units")) {
            return -7000;
        } else {
            Throwable nextCause = cause.getCause();
            if (nextCause != null) {
                return searchStackTrace(nextCause);
            } else {
                return 0;
            }
        }
    }
}
