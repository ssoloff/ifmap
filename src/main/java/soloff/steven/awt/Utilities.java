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

package soloff.steven.awt;

import java.awt.Rectangle;
import java.awt.Window;

/**
 * A collection of methods to deal with various AWT-related activities.
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
     * Centers the window within its owner.
     *
     * @param  oWindow  The window to be centered.
     *
     * @exception  IllegalArgumentException  If oWindow is null.
     */

    public static final void centerWindowInOwner( Window oWindow )
        throws IllegalArgumentException
    {
        /////////////////////////////////////////////////////////////////////
        // VARIABLE DECLARATIONS                                           //

        Rectangle rectOwnerBounds,  // Bounds of window's owner
                  rectBounds;       // Bounds of window

        //                                                                 //
        /////////////////////////////////////////////////////////////////////

        // Make sure arguments are valid
        if( oWindow == null )
            throw new IllegalArgumentException();

        // Center the component within its parent
        rectOwnerBounds = oWindow.getOwner().getBounds();
        rectBounds = oWindow.getBounds();
        oWindow.setLocation( rectOwnerBounds.x + rectOwnerBounds.width / 2 - rectBounds.width / 2,
            rectOwnerBounds.y + rectOwnerBounds.height / 2 - rectBounds.height / 2 );
    }
}
