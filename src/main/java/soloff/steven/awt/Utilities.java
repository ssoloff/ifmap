/*
 * Utilities.java
 *
 * Copyright (c) 2000 by Steven M. Soloff.
 * All rights reserved.
 *
 */

package soloff.steven.awt;

import java.awt.*;

/**
 * A collection of methods to deal with various AWT-related activities.
 *
 * @author   Steven M. Soloff
 * @version  1.0.0 (11/30/00)
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
