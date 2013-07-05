/*
 * Utilities.java
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

package soloff.steven.text;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.util.StringTokenizer;
import javax.swing.SwingUtilities;

/**
 * A collection of methods to deal with various text related activities.
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
