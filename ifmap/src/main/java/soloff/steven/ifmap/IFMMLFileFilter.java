/*
 * IFMMLFileFilter.java
 *
 * Copyright 2000-2013 by Steven Soloff.
 * All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package soloff.steven.ifmap;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 * The object that serves as the file filter for all interactive fiction map
 * markup language (IFMML) files.
 */

public final class IFMMLFileFilter
    extends FileFilter
{
    // **********************************************************************
    // *********************         Constants          *********************
    // **********************************************************************

    /**
     * The extension used to identify XML files.
     */
    private static final String XML_EXTENSION = ".xml";


    // **********************************************************************
    // *********************        Constructors        *********************
    // **********************************************************************

    /**
     * Constructs a new IFMMLFileFilter object.
     */

    public IFMMLFileFilter()
    {
    }


    // **********************************************************************
    // *********************    FileFilter Overrides    *********************
    // **********************************************************************

    /**
     * @see  javax.swing.filechooser.FileFilter#accept( File )  accept
     */

    public boolean accept( File file )
    {
        // Accept all directories and all files ending with ".xml"
        return( file.isDirectory() || file.getName().endsWith( XML_EXTENSION ) );
    }

    /**
     * @see  javax.swing.filechooser.FileFilter#getDescription()  getDescription
     */

    public String getDescription()
    {
        // Return the filter description
        return( IFMap.getResource( "doc.ifmmlDescription" )
            + " (*" + XML_EXTENSION + ")" );
    }
}
