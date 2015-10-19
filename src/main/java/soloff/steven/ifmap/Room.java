/*
 * Room.java
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

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.BufferedWriter;
import java.io.IOException;
import org.w3c.dom.Element;

/**
 * The object that represents a room on the map.
 */

public class Room
    extends Object
    implements IFMMLizable
{
    // **********************************************************************
    // *********************         Constants          *********************
    // **********************************************************************

    /**
     * Identifies the northwest exit.
     */
    public static final int EXIT_NW = 0;

    /**
     * Identifies the north exit.
     */
    public static final int EXIT_N = 1;

    /**
     * Identifies the northeast exit.
     */
    public static final int EXIT_NE = 2;

    /**
     * Identifies the west exit.
     */
    public static final int EXIT_W = 3;

    /**
     * Identifies the east exit.
     */
    public static final int EXIT_E = 4;

    /**
     * Identifies the southwest exit.
     */
    public static final int EXIT_SW = 5;

    /**
     * Identifies the south exit.
     */
    public static final int EXIT_S = 6;

    /**
     * Identifies the southeast exit.
     */
    public static final int EXIT_SE = 7;

    /**
     * Identifies the up exit.
     */
    public static final int EXIT_U = 8;

    /**
     * Identifies the down exit.
     */
    public static final int EXIT_D = 9;

    /**
     * The identifier of the first exit.
     */
    public static final int EXIT_FIRST = 0;

    /**
     * The identifier of the last exit.
     */
    public static final int EXIT_LAST = 9;

    /**
     * The total number of exits in the Room.
     */
    public static final int NUM_EXITS = EXIT_LAST - EXIT_FIRST + 1;

    /**
     * Holds the name of each exit indexed by its identifier.
     */
    protected static final String[] EXIT_NAMES =
    {
        "NW", "N", "NE", "W", "E", "SW", "S", "SE", "U", "D"
    };

    /**
     * The ID prefix for all instances of this class.
     */
    protected static final String INSTANCE_ID_PREFIX = "Room";


    // **********************************************************************
    // *********************     Instance Variables     *********************
    // **********************************************************************

    /**
     * The unique identifier of this Room.
     */
    protected String m_strID;

    /**
     * The Map that contains this Room.
     */
    protected Map m_oMap;

    /**
     * The name of the room.
     */
    protected String m_strName;

    /**
     * The description of the room.
     */
    protected String m_strDescription;

    /**
     * The bounds of the room on the map.
     */
    protected Rectangle m_rectBounds;

    /**
     * The foreground color of the room.
     */
    protected Color m_clrForeground;

    /**
     * The background color of the room.
     */
    protected Color m_clrBackground;


    // **********************************************************************
    // *********************      Class Variables       *********************
    // **********************************************************************

    /**
     * The identifier for the next room constructed.
     */
    private static int c_nNextRoomID = 1;


    // **********************************************************************
    // *********************        Constructors        *********************
    // **********************************************************************

    /**
     * Constructs a new Room object.
     *
     * @param  oMap  The Map that contains this Room.
     *
     * @exception  IllegalArgumentException  If oMap is null.
     */

    public Room( Map oMap )
        throws IllegalArgumentException
    {
        // Make sure arguments are valid
        if( oMap == null )
            throw new IllegalArgumentException();

        // Initialize instance variables
        m_strID = INSTANCE_ID_PREFIX + c_nNextRoomID;
        m_oMap = oMap;
        m_strName = m_strID;
        m_strDescription = "";
        m_rectBounds = new Rectangle( 0, 0, RoomUI.MIN_ROOM_WIDTH, RoomUI.MIN_ROOM_HEIGHT );
        m_clrForeground = RoomUI.DEFAULT_FORE_COLOR;
        m_clrBackground = RoomUI.DEFAULT_BACK_COLOR;

        // Update next room identifier
        c_nNextRoomID++;
    }


    // **********************************************************************
    // *********************  Public Instance Methods   *********************
    // **********************************************************************

    /**
     * Gets the unique identifier of this Room.
     *
     * @return  The unique identifier of this Room.
     */

    public String getID()
    {
        // Return the identifiere of the room
        return( m_strID );
    }

    /**
     * Gets the name of this Room.
     *
     * @return  The name of this Room.
     */

    public String getName()
    {
        // Return the name of the room
        return( m_strName );
    }

    /**
     * Sets the name of this Room.
     *
     * @param  strName  The name of this Room.
     *
     * @exception  IllegalArgumentException  If strName is null.
     */

    public void setName( String strName )
        throws IllegalArgumentException
    {
        // Make sure arguments are valid
        if( strName == null )
            throw new IllegalArgumentException();

        // Set the room name
        m_strName = strName;
    }

    /**
     * Gets the description of this Room.
     *
     * @return  The description of this Room.
     */

    public String getDescription()
    {
        // Return the description of the room
        return( m_strDescription );
    }

    /**
     * Sets the description of this Room.
     *
     * @param  strDescription  The description of this Room.
     *
     * @exception  IllegalArgumentException  If strDescription is null.
     */

    public void setDescription( String strDescription )
        throws IllegalArgumentException
    {
        // Make sure arguments are valid
        if( strDescription == null )
            throw new IllegalArgumentException();

        // Set the room description
        m_strDescription = strDescription;
    }

    /**
     * Gets the bounds of this Room.  The bounds are relative to the parent
     * container (the map).
     *
     * @return  The bounds of this Room.
     */

    public Rectangle getBounds()
    {
        // Return a copy of the bounds of the room
        return( new Rectangle( m_rectBounds ) );
    }

    /**
     * Sets the bounds of this Room.  The coordinates in rectBounds are
     * copied so that it may be reused.
     *
     * @param  rectBounds  The bounds of this Room.
     *
     * @exception  IllegalArgumentException  If rectBounds is null.
     */

    public void setBounds( Rectangle rectBounds )
        throws IllegalArgumentException
    {
        // Make sure arguments are valid
        if( rectBounds == null )
            throw new IllegalArgumentException();

        // Set the room bounds
        m_rectBounds.setRect( rectBounds );
    }

    /**
     * Gets the foreground color of this Room.
     *
     * @return  The foreground color of this Room.
     */

    public Color getForeground()
    {
        // Return the foreground color of the room
        return( m_clrForeground );
    }

    /**
     * Sets the foreground color of this Room.
     *
     * @param  clrForeground  The color to become this room's foreground color.
     *     If null then the room will inherit the foreground color of its
     *     parent.
     */

    public void setForeground( Color clrForeground )
    {
        // Set the room foreground color
        m_clrForeground = clrForeground;
    }

    /**
     * Gets the background color of this Room.
     *
     * @return  The background color of this Room.
     */

    public Color getBackground()
    {
        // Return the background color of the room
        return( m_clrBackground );
    }

    /**
     * Sets the background color of this Room.
     *
     * @param  clrBackground  The color to become this room's background color.
     *     If null then the room will inherit the background color of its
     *     parent.
     */

    public void setBackground( Color clrBackground )
    {
        // Set the room background color
        m_clrBackground = clrBackground;
    }

    /**
     * Gets the coordinates of the top-left corner of each exit block based on
     * the current size of the Room.  The coordinate is relative to the
     * coordinate system of the Room.
     *
     * @param  nExitID  Identifier of the exit whose location is desired.
     *
     * @return  The top-left coordinate of the requested exit block.
     *
     * @exception  IllegalArgumentException  If nExitID is not a valid exit
     *     identifier.
     */

    public Point getExitLocation( int nExitID )
        throws IllegalArgumentException
    {
        /////////////////////////////////////////////////////////////////////
        // VARIABLE DECLARATIONS                                           //

        int nX,  // x-coordinate of requested exit
            nY;  // y-coordinate of requested exit

        //                                                                 //
        /////////////////////////////////////////////////////////////////////

        // Make sure arguments are valid
        if( nExitID < EXIT_FIRST || nExitID > EXIT_LAST )
            throw new IllegalArgumentException();

        // Compute the coordinates of the requested exit
        switch( nExitID )
        {
            case EXIT_NW:
                nX = 0;
                nY = 0;
                break;

            case EXIT_N:
                nX = m_rectBounds.width / 2 - RoomUI.EXIT_WIDTH / 2;
                nY = 0;
                break;

            case EXIT_NE:
                nX = m_rectBounds.width - RoomUI.EXIT_WIDTH;
                nY = 0;
                break;

            case EXIT_W:
                nX = 0;
                nY = m_rectBounds.height / 2 - RoomUI.EXIT_HEIGHT / 2;
                break;

            case EXIT_E:
                nX = m_rectBounds.width - RoomUI.EXIT_WIDTH;
                nY = m_rectBounds.height / 2 - RoomUI.EXIT_HEIGHT / 2;
                break;

            case EXIT_SW:
                nX = 0;
                nY = m_rectBounds.height - RoomUI.EXIT_HEIGHT;
                break;

            case EXIT_S:
                nX = m_rectBounds.width / 2 - RoomUI.EXIT_WIDTH / 2;
                nY = m_rectBounds.height - RoomUI.EXIT_HEIGHT;
                break;

            case EXIT_SE:
                nX = m_rectBounds.width - RoomUI.EXIT_WIDTH;
                nY = m_rectBounds.height - RoomUI.EXIT_HEIGHT;
                break;

            case EXIT_U:
                nX = (m_rectBounds.width / 2 - RoomUI.EXIT_WIDTH / 2) / 2;
                nY = 0;
                break;

            case EXIT_D:
                nX = (m_rectBounds.width / 2 - RoomUI.EXIT_WIDTH / 2 +
                    m_rectBounds.width - RoomUI.EXIT_WIDTH) / 2;
                nY = m_rectBounds.height - RoomUI.EXIT_HEIGHT;
                break;

            default:
                nX = nY = 0;
                break;
        }

        // Return the location of the requested exit
        return( new Point( nX, nY ) );
    }


    // **********************************************************************
    // *********************    Public Class Methods    *********************
    // **********************************************************************

    /**
     * Parses the specified Room ID to obtain the its integral portion.
     *
     * @param  strRoomID  The Room ID to parse.
     *
     * @return  The integral portion of the specified Room ID.
     *
     * @exception  IllegalArgumentException  If strRoomID is not a valid
     *     Room identifier.
     */

    public static int parseRoomID( String strRoomID )
        throws IllegalArgumentException
    {
        // Make sure arguments are valid
        if( strRoomID.length() <= INSTANCE_ID_PREFIX.length() ||
            !strRoomID.substring( 0, INSTANCE_ID_PREFIX.length() ).equals( INSTANCE_ID_PREFIX ) )
            throw new IllegalArgumentException();

        // Parse the integral portion of the ID
        try
        {
            return( Integer.parseInt( strRoomID.substring( INSTANCE_ID_PREFIX.length() ) ) );
        }
        catch( NumberFormatException eNFE )
        {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Gets the ID for the next Room created.
     *
     * @return  The ID for the next Room.
     */

    public static int getNextRoomID()
    {
        // Return the next room ID
        return( c_nNextRoomID );
    }

    /**
     * Sets the ID for the next Room created.
     *
     * @param  nNextRoomID  The ID for the next Room.
     *
     * @exception  IllegalArgumentException  If nNextRoomID is less than 1.
     */

    public static void setNextRoomID( int nNextRoomID )
        throws IllegalArgumentException
    {
        // Make sure arguments are valid
        if( nNextRoomID < 1 )
            throw new IllegalArgumentException();

        // Set the next room ID
        c_nNextRoomID = nNextRoomID;
    }


    // **********************************************************************
    // *********************  Protected Class Methods   *********************
    // **********************************************************************

    /**
     * Gets the name of the requested exit.
     *
     * @param  nExitID  Identifier of the exit whose name is desired.
     *
     * @return  The name of the requested exit.
     *
     * @exception  IllegalArgumentException  If nExitID is not a valid exit
     *     identifier.
     */

    protected static String getExitName( int nExitID )
        throws IllegalArgumentException
    {
        // Make sure arguments are valid
        if( nExitID >= EXIT_NAMES.length )
            throw new IllegalArgumentException();

        // Return the corresponding name of the specified exit
        return( EXIT_NAMES[ nExitID ] );
    }

    /**
     * Gets the ID of the named exit.
     *
     * @param  strExitName  Name of the exit whose ID is desired.
     *
     * @return  The ID of the requested exit.
     *
     * @exception  IllegalArgumentException  If strExitName is not a valid exit
     *     name.
     */

    protected static int getExitID( String strExitName )
        throws IllegalArgumentException
    {
        // Return the corresponding ID of the specified exit
        for( int nI = 0; nI < EXIT_NAMES.length; nI++ )
            if( strExitName.equals( EXIT_NAMES[ nI ] ) )
                return( nI );

        // No matching name could be found
        throw new IllegalArgumentException();
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

        String strColor,    // String consisting of color value to be parsed
               strMessage;  // Used to format error messages
        int    nRadix;      // Radix used to interpret color values

        //                                                                 //
        /////////////////////////////////////////////////////////////////////

        try
        {
            // Parse simple text attributes
            m_strID = oElement.getAttribute( IFMML_ATTR_ROOM_ID );
            m_strName = oElement.getAttribute( IFMML_ATTR_ROOM_NAME );
            m_strDescription = oElement.getAttribute( IFMML_ATTR_ROOM_DESCRIPTION );
            m_rectBounds.x = Integer.parseInt( oElement.getAttribute( IFMML_ATTR_ROOM_X ) );
            m_rectBounds.y = Integer.parseInt( oElement.getAttribute( IFMML_ATTR_ROOM_Y ) );
            m_rectBounds.width = Integer.parseInt( oElement.getAttribute( IFMML_ATTR_ROOM_WIDTH ) );
            m_rectBounds.height = Integer.parseInt( oElement.getAttribute( IFMML_ATTR_ROOM_HEIGHT ) );

            // Parse foreground color attribute
            if( (strColor = oElement.getAttribute( IFMML_ATTR_ROOM_FORECOLOR )).startsWith( "#" ) )
            {
                strColor = strColor.substring( 1 );
                nRadix = 16;
            }
            else
                nRadix = 10;
            m_clrForeground = new Color( Integer.parseInt( strColor, nRadix ) );

            // Parse background color attribute
            if( (strColor = oElement.getAttribute( IFMML_ATTR_ROOM_BACKCOLOR )).startsWith( "#" ) )
            {
                strColor = strColor.substring( 1 );
                nRadix = 16;
            }
            else
                nRadix = 10;
            m_clrBackground = new Color( Integer.parseInt( strColor, nRadix ) );
        }
        catch( NumberFormatException eNF )
        {
            // Throw new exception
            strMessage = IFMap.getResource( "msg.illegalRoomNumberFormat" ) +
                "\"" + m_strID + "\" (" + eNF.getMessage() + ").";
            throw new RuntimeException( strMessage );
        }

        // Check room bounds
        if( m_rectBounds.x < 0 ||
            m_rectBounds.y < 0 ||
            m_rectBounds.width < RoomUI.MIN_ROOM_WIDTH ||
            m_rectBounds.height < RoomUI.MIN_ROOM_HEIGHT )
        {
            // Throw new exception
            strMessage = IFMap.getResource( "msg.illegalRoomBounds" ) +
                "\"" + m_strID + "\".";
            throw new RuntimeException( strMessage );
        }
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

        // Write ROOM element
        str = "\t<" + IFMML_ELEM_ROOM + " ";
        oWriter.write( str, 0, str.length() );
        str = IFMML_ATTR_ROOM_ID + "=\"" + m_strID + "\" ";
        oWriter.write( str, 0, str.length() );
        str = IFMML_ATTR_ROOM_NAME + "=\"" + m_strName + "\" ";
        oWriter.write( str, 0, str.length() );
        str = IFMML_ATTR_ROOM_DESCRIPTION + "=\"" + m_strDescription.replace( '\n', '|' ) + "\" ";
        oWriter.write( str, 0, str.length() );
        str = IFMML_ATTR_ROOM_X + "=\"" + m_rectBounds.x + "\" ";
        oWriter.write( str, 0, str.length() );
        str = IFMML_ATTR_ROOM_Y + "=\"" + m_rectBounds.y + "\" ";
        oWriter.write( str, 0, str.length() );
        str = IFMML_ATTR_ROOM_WIDTH + "=\"" + m_rectBounds.width + "\" ";
        oWriter.write( str, 0, str.length() );
        str = IFMML_ATTR_ROOM_HEIGHT + "=\"" + m_rectBounds.height + "\" ";
        oWriter.write( str, 0, str.length() );
        str = IFMML_ATTR_ROOM_FORECOLOR + "=\"#" + Integer.toHexString( m_clrForeground.getRGB() & 0x00FFFFFF ).toUpperCase() + "\" ";
        oWriter.write( str, 0, str.length() );
        str = IFMML_ATTR_ROOM_BACKCOLOR + "=\"#" + Integer.toHexString( m_clrBackground.getRGB() & 0x00FFFFFF ).toUpperCase() + "\" ";
        oWriter.write( str, 0, str.length() );
        str = "/>";
        oWriter.write( str, 0, str.length() );
        oWriter.newLine();
    }
}
