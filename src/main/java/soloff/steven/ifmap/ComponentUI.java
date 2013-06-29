/*
 * ComponentUI.java
 *
 * Copyright (c) 2000-2001 by Steven M. Soloff.
 * All rights reserved.
 *
 */

package soloff.steven.ifmap;

/**
 * All components in the IFMap application that have UIs implement this
 * interface to support generic manipulation.
 *
 * @author   Steven M. Soloff
 * @version  1.1.0 (02/06/01)
 */

public interface ComponentUI
{
    // **********************************************************************
    // *********************  Public Interface Methods  *********************
    // **********************************************************************

    /**
     * Brings up a modal dialog to allow editing of the UI component.
     */

    public abstract void editUI();

    /**
     * Deletes the UI component including removing it from its container.
     */

    public abstract void deleteUI();
}
