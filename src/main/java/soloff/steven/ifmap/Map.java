/*
 * Map.java
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

import java.awt.Dimension;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * The object that represents a game map.
 */

public class Map
    extends Object
    implements IFMMLizable
{
    // **********************************************************************
    // *********************     Instance Variables     *********************
    // **********************************************************************

    /**
     * The hashtable used to reference all Room objects contained within
     * the Map.
     */
    protected HashMap<String, Room> m_oRoomMap;

    /**
     * The hashtable used to reference all Edge objects contained within
     * the Map.
     */
    protected HashMap<String, Edge> m_oEdgeMap;

    /**
     * Stores the dimensions of the map.  The map dimensions are defined to
     * be the tightest bounding rectangle around all the rooms on the map.
     */
    protected Dimension m_dmMap;


    // **********************************************************************
    // *********************        Constructors        *********************
    // **********************************************************************

    /**
     * Constructs a new Map object.
     */

    public Map()
    {
        // Initialize instance variables
        m_oRoomMap = new HashMap<String, Room>();
        m_oEdgeMap = new HashMap<String, Edge>();
        m_dmMap = new Dimension( MapUI.MIN_MAP_WIDTH, MapUI.MIN_MAP_HEIGHT );
    }


    // **********************************************************************
    // *********************  Public Instance Methods   *********************
    // **********************************************************************

    /**
     * Clears the contents of the Map.
     */

    public void clear()
    {
        // Remove all Rooms and Edges and reset map dimension
        m_oRoomMap.clear();
        m_oEdgeMap.clear();
        m_dmMap.setSize( MapUI.MIN_MAP_WIDTH, MapUI.MIN_MAP_HEIGHT );

        // Reset next Room and Edge IDs
        Room.setNextRoomID( 1 );
        Edge.setNextEdgeID( 1 );
    }

    /**
     * Creates a new Room object and adds it to the Map.
     *
     * @return  The new Room object created.
     */

    public Room createRoom()
    {
        // Create a new Room, add it to the Map, and return it
        Room oRoom = new Room( this );
        addRoom( oRoom );
        return( oRoom );
    }

    /**
     * Creates a new Edge object and adds it to the Map.
     *
     * @param  oStartRoom  The Room the edge starts at.
     * @param  nStartExit  Identifier of the exit of oStartRoom the edge is
     *     connected to.
     * @param  oEndRoom  The Room the edge ends at.
     * @param  nEndExit  Identifier of the exit of oEndRoom the edge is
     *     connected to.
     *
     * @return  The new Edge object created.
     *
     * @exception  IllegalArgumentException  If oStartRoom or oEndRoom is null,
     *     or if nStartExit or nEndExit are not valid exit identifiers.
     */

    public Edge createEdge( Room oStartRoom, int nStartExit, Room oEndRoom, int nEndExit )
        throws IllegalArgumentException
    {
        // Make sure arguments are valid
        if( oStartRoom == null || oEndRoom == null )
            throw new IllegalArgumentException();
        if( nStartExit < Room.EXIT_FIRST || nStartExit > Room.EXIT_LAST )
            throw new IllegalArgumentException();
        if( nEndExit < Room.EXIT_FIRST || nEndExit > Room.EXIT_LAST )
            throw new IllegalArgumentException();

        // Create a new Edge, add it to the Map, and return it
        Edge oEdge = new Edge( this, oStartRoom, nStartExit, oEndRoom, nEndExit );
        addEdge( oEdge );
        return( oEdge );
    }

    /**
     * Removes the specified Room object from the Map and deletes it.  All
     * Edges connected to the Room will also be removed from the Map and
     * deleted.
     *
     * @param  oRoom  The Room object to be deleted.
     *
     * @exception  IllegalArgumentException  If oRoom is null.
     */

    public void deleteRoom( Room oRoom )
        throws IllegalArgumentException
    {
        /////////////////////////////////////////////////////////////////////
        // VARIABLE DECLARATIONS                                           //

        Iterator<Edge> iter;       // Iterator for Edge hash map
        Edge           oEdge;      // An Edge on the Map
        String         strRoomID;  // ID of the Room to be deleted

        //                                                                 //
        /////////////////////////////////////////////////////////////////////

        // Make sure arguments are valid
        if( oRoom == null )
            throw new IllegalArgumentException();

        // Store the ID of the Room to be deleted
        strRoomID = oRoom.getID();

        // Iterate through all edges
        for( iter = m_oEdgeMap.values().iterator(); iter.hasNext(); )
        {
            // Remove any Edge connected to the Room being deleted from the Map
            oEdge = iter.next();
            if( oEdge.getStartRoomID().equals( strRoomID ) ||
                    oEdge.getEndRoomID().equals( strRoomID ) )
                iter.remove();
        }

        // Remove the Room from the Map
        m_oRoomMap.remove( strRoomID );
    }

    /**
     * Removes the specified Edge object from the Map and deletes it.
     *
     * @param  oEdge  The Edge object to be deleted.
     *
     * @exception  IllegalArgumentException  If oEdge is null.
     */

    public void deleteEdge( Edge oEdge )
        throws IllegalArgumentException
    {
        // Make sure arguments are valid
        if( oEdge == null )
            throw new IllegalArgumentException();

        // Remove the Edge from the Map
        m_oEdgeMap.remove( oEdge.getID() );
    }

    /**
     * Returns the Room to which the specified ID is mapped.  Returns null if
     * no Room has the specified ID.
     *
     * @param  strRoomID  ID whose associated Room is to be returned.
     *
     * @return  The Room to which the specified ID is mapped.
     */

    public Room getRoom( String strRoomID )
    {
        // Lookup the Room associated with the specified ID
        return( (Room)m_oRoomMap.get( strRoomID ) );
    }

    /**
     * Returns the Edge to which the specified ID is mapped.  Returns null if
     * no Edge has the specified ID.
     *
     * @param  strEdgeID  ID whose associated Edge is to be returned.
     *
     * @return  The Edge to which the specified ID is mapped.
     */

    public Edge getEdge( String strEdgeID )
    {
        // Lookup the Edge associated with the specified ID
        return( (Edge)m_oEdgeMap.get( strEdgeID ) );
    }

    /**
     * Returns the Edge attached to the Room at the specified exit.  Returns
     * null of no Edge is attached to the specified Room/exit combination.
     *
     * @param  oRoom  The Room to which the Edge is attached to.
     * @param  nExitID  The exit to which the Edge is attached to.
     *
     * @return  The Edge attached to the Room at the specified exit.
     *
     * @exception  IllegalArgumentException  If oRoom is null or if nExitID
     *     is not a valid exit identifier.
     */

    public Edge getEdgeAtRoomExit( Room oRoom, int nExitID )
        throws IllegalArgumentException
    {
        // Make sure arguments are valid
        if( oRoom == null )
            throw new IllegalArgumentException();
        if( nExitID < Room.EXIT_FIRST || nExitID > Room.EXIT_LAST )
            throw new IllegalArgumentException();

        // Iterate through the edge map
        for( final Edge oEdge : m_oEdgeMap.values() )
        {
            // Determine if the Edge is attached to the specified exit of the
            // specified room
            if( (oEdge.m_strStartRoomID.equals( oRoom.m_strID ) && oEdge.m_nStartExit == nExitID) ||
                (oEdge.m_strEndRoomID.equals( oRoom.m_strID ) && oEdge.m_nEndExit == nExitID) )
                return( oEdge );
        }

        // No edge was attached to the Room at the specified exit
        return( null );
    }

    /**
     * Checks the integrity of the Map.  Essentially makes sure that all Edges
     * are connected to valid Rooms.
     *
     * @return  A flag indicating whether or not the Map passed the integrity
     *     check.
     */

    public boolean checkIntegrity()
    {
        // Iterate through the edge map
        for( final Edge oEdge : m_oEdgeMap.values() )
        {
            // Make sure the Edge is valid
            if( m_oRoomMap.get( oEdge.m_strStartRoomID ) == null ||
                m_oRoomMap.get( oEdge.m_strEndRoomID ) == null ||
                oEdge.m_nStartExit < Room.EXIT_FIRST ||
                oEdge.m_nEndExit > Room.EXIT_LAST )
                return( false );
        }

        // Map integrity is ok
        return( true );
    }


    // **********************************************************************
    // ********************* Protected Instance Methods *********************
    // **********************************************************************

    /**
     * Adds the specified Room to the Map.
     *
     * @param  oRoom  The Room to be added to the Map.
     *
     * @exception  IllegalArgumentException  If oRoom is null.
     */

    protected void addRoom( Room oRoom )
        throws IllegalArgumentException
    {
        // Make sure arguments are valid
        if( oRoom == null )
            throw new IllegalArgumentException();

        // Add the Room to the Map
        m_oRoomMap.put( oRoom.getID(), oRoom );
    }

    /**
     * Adds the specified Edge to the Map.
     *
     * @param  oEdge  The Edge to be added to the Map.
     *
     * @exception  IllegalArgumentException  If oEdge is null.
     */

    protected void addEdge( Edge oEdge )
        throws IllegalArgumentException
    {
        // Make sure arguments are valid
        if( oEdge == null )
            throw new IllegalArgumentException();

        // Add the Edge to the Map
        m_oEdgeMap.put( oEdge.getID(), oEdge );
    }


    // **********************************************************************
    // *********************     IFMMLizable Methods    *********************
    // **********************************************************************

    /**
     * @see  soloff.steven.ifmap.IFMMLizable#readIFMML( Element )
     *     readIFMML
     */

    public void readIFMML( Element oElement )
        throws RuntimeException
    {
        /////////////////////////////////////////////////////////////////////
        // VARIABLE DECLARATIONS                                           //

        NodeList oElemList;    // List of Elements
        Room     oRoom;        // New Room created from a ROOM element
        Edge     oEdge;        // New Edge created from an EDGE element
        int      nNextRoomID,  // New next Room ID
                 nNextEdgeID,  // New next Edge ID
                 nElemCount,   // Count of Elements in the list
                 nI;           // Loop control variable

        //                                                                 //
        /////////////////////////////////////////////////////////////////////

        // Initialize next ID counts
        nNextRoomID = nNextEdgeID = 1;

        // Loop through all the ROOM elements that are children of the MAP
        oElemList = oElement.getElementsByTagName( IFMML_ELEM_ROOM );
        nElemCount = oElemList.getLength();
        for( nI = 0; nI < nElemCount; nI++ )
        {
            // Create a new Room and initialize it
            oRoom = new Room( this );
            oRoom.readIFMML( (Element)oElemList.item( nI ) );
            m_oRoomMap.put( oRoom.getID(), oRoom );

            // Update the Map based on the new Room
            nNextRoomID = Math.max( nNextRoomID, Room.parseRoomID( oRoom.getID() ) );
            m_dmMap.width = Math.max( m_dmMap.width, oRoom.m_rectBounds.x + oRoom.m_rectBounds.width );
            m_dmMap.height = Math.max( m_dmMap.height, oRoom.m_rectBounds.y + oRoom.m_rectBounds.height );
        }

        // Loop through all the EDGE elements that are children of the MAP
        oElemList = oElement.getElementsByTagName( IFMML_ELEM_EDGE );
        nElemCount = oElemList.getLength();
        for( nI = 0; nI < nElemCount; nI++ )
        {
            // Create a new Edge and initialize it
            oEdge = new Edge( this );
            oEdge.readIFMML( (Element)oElemList.item( nI ) );
            m_oEdgeMap.put( oEdge.getID(), oEdge );

            // Update the Map based on the new Edge
            nNextEdgeID = Math.max( nNextEdgeID, Edge.parseEdgeID( oEdge.getID() ) );
        }

        // Update next available IDs for Rooms and Edges
        Room.setNextRoomID( nNextRoomID + 1 );
        Edge.setNextEdgeID( nNextEdgeID + 1 );
    }

    /**
     * @see  soloff.steven.ifmap.IFMMLizable#writeIFMML( BufferedWriter )
     *     writeIFMML
     */

    public void writeIFMML( BufferedWriter oWriter )
        throws IOException
    {
        /////////////////////////////////////////////////////////////////////
        // VARIABLE DECLARATIONS                                           //

        String str;  // Text to be written to IFMML file

        //                                                                 //
        /////////////////////////////////////////////////////////////////////

        // Begin MAP element
        str = "<" + IFMML_ELEM_MAP + ">";
        oWriter.write( str, 0, str.length() );
        oWriter.newLine();

        // Write all ROOM elements
        for( final Room room : m_oRoomMap.values() )
            room.writeIFMML( oWriter );

        // Write all EDGE elements
        for( final Edge edge : m_oEdgeMap.values() )
            edge.writeIFMML( oWriter );

        // End MAP element
        str = "</" + IFMML_ELEM_MAP + ">";
        oWriter.write( str, 0, str.length() );
        oWriter.newLine();
    }
}
