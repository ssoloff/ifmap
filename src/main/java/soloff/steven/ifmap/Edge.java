/*
 * Edge.java
 *
 * Copyright (c) 2000-2001 by Steven M. Soloff.
 * All rights reserved.
 *
 */

package soloff.steven.ifmap;

import java.awt.Point;
import java.io.BufferedWriter;
import java.io.IOException;
import org.w3c.dom.Element;

/**
 * The object that represents an edge on the map.
 *
 * @author   Steven M. Soloff
 * @version  1.1.0 (02/06/01)
 */

public class Edge
    extends Object
    implements IFMMLizable
{
    // **********************************************************************
    // *********************         Constants          *********************
    // **********************************************************************

    /**
     * The ID prefix for all instances of this class.
     */
    protected static final String INSTANCE_ID_PREFIX = "Edge";


    // **********************************************************************
    // *********************     Instance Variables     *********************
    // **********************************************************************

    /**
     * The unique identifier of this Edge.
     */
    protected String m_strID;

    /**
     * The Map that contains this Edge.
     */
    protected Map m_oMap;

    /**
     * The identifier of the Room this edge starts at.
     */
    protected String m_strStartRoomID;

    /**
     * The identifier of the Room this edge ends at.
     */
    protected String m_strEndRoomID;

    /**
     * The identifier of the exit this edge starts at.
     */
    protected int m_nStartExit;

    /**
     * The identifier of the exit this edge ends at.
     */
    protected int m_nEndExit;

    /**
     * Indicates this edge is a one-way passage.  It is rendered with an arrow
     * head at the exit the edge ends at.
     */
    protected boolean m_bOneWay;

    /**
     * Indicates this edge is a secret passage.  It is rendered in a different
     * color than a non-secret passage.
     */
    protected boolean m_bSecret;


    // **********************************************************************
    // *********************      Class Variables       *********************
    // **********************************************************************

    /**
     * The identifier for the next edge constructed.
     */
    private static int c_nNextEdgeID = 1;


    // **********************************************************************
    // *********************        Constructors        *********************
    // **********************************************************************

    /**
     * Constructs a new Edge object.
     *
     * @param  oMap  The Map that contains this Edge.
     * @param  oStartRoom  The Room the Edge starts at.
     * @param  nStartExit  The exit in oStartRoom the Edge starts at.
     * @param  oEndRoom  The Room the Edge ends at.
     * @param  nEndExit  The exit in oEndRoom the Edge ends at.
     *
     * @exception  IllegalArgumentException  If oMap, oStartRoom, or oEndRoom
     *     is null; if nStartExit or nEndExit are not in the range
     *      Room.EXIT_FIRST <= x <= Room.EXIT_LAST.
     */

    public Edge( Map oMap, Room oStartRoom, int nStartExit, Room oEndRoom, int nEndExit )
        throws IllegalArgumentException
    {
        // Make sure arguments are valid
        if( oMap == null || oStartRoom == null || oEndRoom == null )
            throw new IllegalArgumentException();
        if( nStartExit < Room.EXIT_FIRST || nStartExit > Room.EXIT_LAST )
            throw new IllegalArgumentException();
        if( nEndExit < Room.EXIT_FIRST || nEndExit > Room.EXIT_LAST )
            throw new IllegalArgumentException();

        // Initialize instance variables
        m_strID = INSTANCE_ID_PREFIX + c_nNextEdgeID;
        m_oMap = oMap;
        m_strStartRoomID = oStartRoom.getID();
        m_strEndRoomID = oEndRoom.getID();
        m_nStartExit = nStartExit;
        m_nEndExit = nEndExit;
        m_bOneWay = false;
        m_bSecret = false;

        // Update next edge identifier
        c_nNextEdgeID++;
    }

    /**
     * Constructs a new Edge object to be initialized from a persistant store.
     * This method cannot be called publically.
     *
     * @exception  IllegalArgumentException  If oMap is null.
     */

    protected Edge( Map oMap )
    {
        // Make sure arguments are valid
        if( oMap == null )
            throw new IllegalArgumentException();

        // Initialize instance variables
        m_oMap = oMap;
    }


    // **********************************************************************
    // *********************  Public Instance Methods   *********************
    // **********************************************************************

    /**
     * Gets the unique identifier of this Edge.
     *
     * @return  The unique identifier of this Edge.
     */

    public String getID()
    {
        // Return the identifier of the edge
        return( m_strID );
    }

    /**
     * Gets the unique identifier of the Room this Edge starts at.
     *
     * @return  The unique identifier of the Room this Edge starts at.
     */

    public String getStartRoomID()
    {
        // Return the identifier of the room this edge starts at
        return( m_strStartRoomID );
    }

    /**
     * Gets the unique identifier of the Room this Edge ends at.
     *
     * @return  The unique identifier of the Room this Edge ends at.
     */

    public String getEndRoomID()
    {
        // Return the identifier of the room this edge ends at
        return( m_strEndRoomID );
    }

    /**
     * Gets the identifier of the exit this Edge starts at.
     *
     * @return  The identifier of the exit this Edge starts at.
     */

    public int getStartExit()
    {
        // Return the identifier of the exit this edge starts at
        return( m_nStartExit );
    }

    /**
     * Gets the identifier of the exit this Edge ends at.
     *
     * @return  The identifier of the exit this Edge ends at.
     */

    public int getEndExit()
    {
        // Return the identifiere of the exit this edge ends at
        return( m_nEndExit );
    }

    /**
     * Gets the starting point of the Edge.  The coordinate is relative
     * to the parent container (the map).
     *
     * @return  The starting point of the Edge.
     */

    public Point getStartPoint()
    {
        /////////////////////////////////////////////////////////////////////
        // VARIABLE DECLARATIONS                                           //

        Room  oRoom;  // Reference to the Room the Edge starts at
        Point pt;     // Starting point of the Edge

        //                                                                 //
        /////////////////////////////////////////////////////////////////////

        // Compute the starting point of the edge (note the point is
        // translated to be in the center of the exit)
        oRoom = m_oMap.getRoom( m_strStartRoomID );
        pt = oRoom.getExitLocation( m_nStartExit );
        pt.translate( oRoom.m_rectBounds.x + RoomUI.EXIT_WIDTH / 2,
            oRoom.m_rectBounds.y + RoomUI.EXIT_HEIGHT / 2 );
        return( pt );
    }

    /**
     * Gets the ending point of the Edge.  The coordinate is relative
     * to the parent container (the map).
     *
     * @return  The ending point of the Edge.
     */

    public Point getEndPoint()
    {
        /////////////////////////////////////////////////////////////////////
        // VARIABLE DECLARATIONS                                           //

        Room  oRoom;  // Reference to the Room the Edge ends at
        Point pt;     // Ending point of the Edge

        //                                                                 //
        /////////////////////////////////////////////////////////////////////

        // Compute the ending point of the edge (note the point is
        // translated to be in the center of the exit)
        oRoom = m_oMap.getRoom( m_strEndRoomID );
        pt = oRoom.getExitLocation( m_nEndExit );
        pt.translate( oRoom.m_rectBounds.x + RoomUI.EXIT_WIDTH / 2,
            oRoom.m_rectBounds.y + RoomUI.EXIT_HEIGHT / 2 );
        return( pt );
    }

    /**
     * Indicates if the edge represents a one-way passage.
     *
     * @return  A flag indicating if the edge is a one-way passage.
     */

    public boolean isOneWay()
    {
        // Return the one-way flag
        return( m_bOneWay );
    }

    /**
     * Sets or clears the flag indicating the edge represents a one-way passage.
     *
     * @param  bOneWay  Flag indicating the edge is a one-way passage.
     */

    public void setOneWay( boolean bOneWay )
    {
        // Set the one-way flag
        m_bOneWay = bOneWay;
    }

    /**
     * Indicates if the edge represents a single passage.
     *
     * @return  A flag indicating if the edge is a secret passage.
     */

    public boolean isSecret()
    {
        // Return the secret flag
        return( m_bSecret );
    }

    /**
     * Sets or clears the flag indicating the edge represents a secret passage.
     *
     * @param  bSecret  Flag indicating the edge is a secret passage.
     */

    public void setSecret( boolean bSecret )
    {
        // Set the secret flag
        m_bSecret = bSecret;
    }


    // **********************************************************************
    // *********************    Public Class Methods    *********************
    // **********************************************************************

    /**
     * Parses the specified Edge ID to obtain the its integral portion.
     *
     * @param  strEdgeID  The Edge ID to parse.
     *
     * @return  The integral portion of the specified Edge ID.
     *
     * @exception  IllegalArgumentException  If strEdgeID is not a valid
     *     Edge identifier.
     */

    public static int parseEdgeID( String strEdgeID )
        throws IllegalArgumentException
    {
        // Make sure arguments are valid
        if( strEdgeID.length() <= INSTANCE_ID_PREFIX.length() ||
            !strEdgeID.substring( 0, INSTANCE_ID_PREFIX.length() ).equals( INSTANCE_ID_PREFIX ) )
            throw new IllegalArgumentException();

        // Parse the integral portion of the ID
        try {
            return( Integer.parseInt( strEdgeID.substring( INSTANCE_ID_PREFIX.length() ) ) );
        }
        catch( NumberFormatException eNFE ) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Gets the ID for the next Edge created.
     *
     * @return  The ID for the next Edge.
     */

    public static int getNextEdgeID()
    {
        // Return the next edge ID
        return( c_nNextEdgeID );
    }

    /**
     * Sets the ID for the next Edge created.
     *
     * @param  nNextEdgeID  The ID for the next Edge.
     *
     * @exception  IllegalArgumentException  If nNextEdgeID is less than 1.
     */

    public static void setNextEdgeID( int nNextEdgeID )
        throws IllegalArgumentException
    {
        // Make sure arguments are valid
        if( nNextEdgeID < 1 )
            throw new IllegalArgumentException();

        // Set the next edge ID
        c_nNextEdgeID = nNextEdgeID;
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
        // Parse simple text attributes
        m_strID = oElement.getAttribute( IFMML_ATTR_EDGE_ID );
        m_bOneWay = Boolean.valueOf( oElement.getAttribute( IFMML_ATTR_EDGE_ONEWAY ) ).booleanValue();
        m_bSecret = Boolean.valueOf( oElement.getAttribute( IFMML_ATTR_EDGE_SECRET ) ).booleanValue();
        m_strStartRoomID = oElement.getAttribute( IFMML_ATTR_EDGE_STARTROOMID );
        m_nStartExit = Room.getExitID( oElement.getAttribute( IFMML_ATTR_EDGE_STARTROOMEXIT ) );
        m_strEndRoomID = oElement.getAttribute( IFMML_ATTR_EDGE_ENDROOMID );
        m_nEndExit = Room.getExitID( oElement.getAttribute( IFMML_ATTR_EDGE_ENDROOMEXIT ) );
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

        // Write EDGE element
        str = "\t<" + IFMML_ELEM_EDGE + " ";
        oWriter.write( str, 0, str.length() );
        str = IFMML_ATTR_EDGE_ID + "=\"" + m_strID + "\" ";
        oWriter.write( str, 0, str.length() );
        str = IFMML_ATTR_EDGE_ONEWAY + "=\"" + (m_bOneWay ? "TRUE" : "FALSE") + "\" ";
        oWriter.write( str, 0, str.length() );
        str = IFMML_ATTR_EDGE_SECRET + "=\"" + (m_bSecret ? "TRUE" : "FALSE") + "\" ";
        oWriter.write( str, 0, str.length() );
        str = IFMML_ATTR_EDGE_STARTROOMID + "=\"" + m_strStartRoomID + "\" ";
        oWriter.write( str, 0, str.length() );
        str = IFMML_ATTR_EDGE_STARTROOMEXIT + "=\"" + Room.getExitName( m_nStartExit ) + "\" ";
        oWriter.write( str, 0, str.length() );
        str = IFMML_ATTR_EDGE_ENDROOMID + "=\"" + m_strEndRoomID + "\" ";
        oWriter.write( str, 0, str.length() );
        str = IFMML_ATTR_EDGE_ENDROOMEXIT + "=\"" + Room.getExitName( m_nEndExit ) + "\" ";
        oWriter.write( str, 0, str.length() );
        str = "/>";
        oWriter.write( str, 0, str.length() );
        oWriter.newLine();
    }
}
