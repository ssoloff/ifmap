/*
 * IFMMLFileFilter.java
 *
 * Copyright (c) 2000-2001 by Steven M. Soloff.
 * All rights reserved.
 *
 */

package soloff.steven.ifmap;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 * The object that serves as the file filter for all interactive fiction map
 * markup language (IFMML) files.
 *
 * @author   Steven M. Soloff
 * @version  1.1.0 (02/06/01)
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
