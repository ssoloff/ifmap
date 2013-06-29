/*
 * Utilities.java
 *
 * Copyright (c) 2000 by Steven M. Soloff.
 * All rights reserved.
 *
 */

package soloff.steven.text;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.util.StringTokenizer;
import javax.swing.SwingUtilities;

/**
 * A collection of methods to deal with various text related activities.
 *
 * @author   Steven M. Soloff
 * @version  1.0.0 (11/24/00)
 */

public class Utilities
    extends Object
{
//
// Private Constructors
//
    /**
     * Prevents construction of a Utilities object.
     */

    private Utilities()
    {
    }

//
// Public Class Methods
//
    /**
     * Computes the dimensions of a multi-line string using the specified font.
     *
     * @param  oFontMetrics  Font metrics used to size the string.
     * @param  astr  Multi-line string to be sized.
     *
     * @return  Dimensions of the specified string.
     *
     * @exception  IllegalArgumentException  If oFontMetrics or astr is null.
     */

    public static final Dimension computeMultiLineStringDimension( FontMetrics oFontMetrics, String[] astr )
        throws IllegalArgumentException
    {
        /////////////////////////////////////////////////////////////////////
        // VARIABLE DECLARATIONS                                           //

        int nWidth,  // Maximum width of the string
            nLines,  // Number of lines in the string
            nI;      // Loop control variable

        //                                                                 //
        /////////////////////////////////////////////////////////////////////

        // Make sure arguments are valid
        if( oFontMetrics == null || astr == null )
            throw new IllegalArgumentException();

        // Compute the maximum width of the string
        for( nI = 0, nWidth = 0, nLines = astr.length; nI < nLines; nI++ )
            nWidth = Math.max( nWidth, SwingUtilities.computeStringWidth( oFontMetrics, astr[ nI ] ) );

        // Return the dimensions of the string
        return( new Dimension( nWidth, oFontMetrics.getHeight() * astr.length ) );
    }

    /**
     * Splits the specified string into its component lines.  Lines in the
     * source string should be delimited by the '\n' character.
     *
     * @param  str  The string to be split.
     *
     * @return  An array of strings representing each line in the source
     *     string.
     *
     * @exception  IllegalArgumentException   If str is null.
     */

    public static final String[] splitStringByLines( String str )
        throws IllegalArgumentException
    {
        /////////////////////////////////////////////////////////////////////
        // VARIABLE DECLARATIONS                                           //

        String[]        astr;        // Array of strings representing each line in source string
        StringTokenizer oTokenizer;  // Used to tokenize source string
        int             nLines,      // Number of lines in source string
                        nLine,       // Current line in source string
                        nLen,        // Length of source string
                        nI;          // Loop control variable

        //                                                                 //
        /////////////////////////////////////////////////////////////////////

        // Make sure arguments are valid
        if( str == null )
            throw new IllegalArgumentException();

        // Determine the number of lines in the specified string
        for( nI = 0, nLines = 1, nLen = str.length(); nI < nLen ; nI++ )
            if( str.charAt( nI ) == '\n' )
                nLines++;

        // Prepare for tokenization
        astr = new String[ nLines ];
        oTokenizer = new StringTokenizer( str, "\n" );

        // Split the string
        for( nLine = 0; oTokenizer.hasMoreTokens(); )
            astr[ nLine++ ] = oTokenizer.nextToken();

        // Return the split string
        return( astr );
    }
}
