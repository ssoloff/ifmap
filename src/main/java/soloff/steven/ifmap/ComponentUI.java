/*
 * ComponentUI.java
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

/**
 * All components in the IFMap application that have UIs implement this
 * interface to support generic manipulation.
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
