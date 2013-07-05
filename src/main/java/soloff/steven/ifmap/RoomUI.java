/*
 * RoomUI.java
 *
 * Copyright (c) 2000-2001 by Steven M. Soloff.
 * All rights reserved.
 *
 */

package soloff.steven.ifmap;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;

/**
 * The object that provides the user interface for a Room object.
 *
 * @author   Steven M. Soloff
 * @version  1.1.0 (02/06/01)
 */

public class RoomUI
    extends JComponent
    implements ComponentUI
{
    // **********************************************************************
    // *********************         Constants          *********************
    // **********************************************************************

    /**
     * Identifies the room proper during a hit test.
     */
    public static final int HIT_ROOM_CENTER = 0;

    /**
     * Identifies the area north of the room during a hit test.
     */
    public static final int HIT_ROOM_NORTH = 1;

    /**
     * Identifies the area south of the room during a hit test.
     */
    public static final int HIT_ROOM_SOUTH = 2;

    /**
     * Identifies the area west of the room during a hit test.
     */
    public static final int HIT_ROOM_WEST = 3;

    /**
     * Identifies the area east of the room during a hit test.
     */
    public static final int HIT_ROOM_EAST = 4;

    /**
     * Identifies the northwest exit of the room during a hit test.
     */
    public static final int HIT_EXIT_NW = 5;

    /**
     * Identifies the north exit of the room during a hit test.
     */
    public static final int HIT_EXIT_N = 6;

    /**
     * Identifies the northeast exit of the room during a hit test.
     */
    public static final int HIT_EXIT_NE = 7;

    /**
     * Identifies the west exit of the room during a hit test.
     */
    public static final int HIT_EXIT_W = 8;

    /**
     * Identifies the east exit of the room during a hit test.
     */
    public static final int HIT_EXIT_E = 9;

    /**
     * Identifies the southwest exit of the room during a hit test.
     */
    public static final int HIT_EXIT_SW = 10;

    /**
     * Identifies the south exit of the room during a hit test.
     */
    public static final int HIT_EXIT_S = 11;

    /**
     * Identifies the southeast exit of the room during a hit test.
     */
    public static final int HIT_EXIT_SE = 12;

    /**
     * Identifies the up exit of the room during a hit test.
     */
    public static final int HIT_EXIT_U = 13;

    /**
     * Identifies the down exit of the room during a hit test.
     */
    public static final int HIT_EXIT_D = 14;

    /**
     * Identifies the minimum width of a room.
     */
    protected static final int MIN_ROOM_WIDTH = 70;

    /**
     * Identifies the minimum height of a room.
     */
    protected static final int MIN_ROOM_HEIGHT = 40;

    /**
     * Identifies the width of a room exit.
     */
    protected static final int EXIT_WIDTH = 5;

    /**
     * Identifies the height of a room exit.
     */
    protected static final int EXIT_HEIGHT = 5;

    /**
     * Identifies the default foreground color for rooms.
     */
    protected static final Color DEFAULT_FORE_COLOR = Color.black;

    /**
     * Identifies the default background color for rooms.
     */
    protected static final Color DEFAULT_BACK_COLOR = Color.white;


    // **********************************************************************
    // *********************     Instance Variables     *********************
    // **********************************************************************

    /**
     * The MapUI that contains this RoomUI.
     */
    protected MapUI m_oMapUI;

    /**
     * The Room rendered by this UI object.
     */
    protected Room m_oRoom;

    /**
     * The parsed version of the room name.
     */
    protected String[] m_astrText;

    /**
     * Top-left coordinates of each exit in the room.
     */
    protected Point[] m_aptExits;


    // **********************************************************************
    // *********************       Class Variables      *********************
    // **********************************************************************

    /**
     * Serializable class version number.
     */
    private static final long serialVersionUID = -7996048220472876132L;


    // **********************************************************************
    // *********************        Constructors        *********************
    // **********************************************************************

    /**
     * Constructs a new RoomUI object.
     *
     * @param  oMapUI  The MapUI that contains this RoomUI.
     * @param  oRoom  The Room object to be associated with this UI object.
     *
     * @exception  IllegalArgumentException  If oMapUI or oRoom is null.
     */

    public RoomUI( MapUI oMapUI, Room oRoom )
        throws IllegalArgumentException
    {
        /////////////////////////////////////////////////////////////////////
        // VARIABLE DECLARATIONS                                           //

        MouseInputListener oMouseInputListener;  // Mouse input listener for this component
        int                nI;                   // Loop control variable

        //                                                                 //
        /////////////////////////////////////////////////////////////////////

        // Make sure arguments are valid
        if( oMapUI == null || oRoom == null )
            throw new IllegalArgumentException();

        // Initialize instance variables
        m_oMapUI = oMapUI;
        m_oRoom = oRoom;
        m_aptExits = new Point[ Room.NUM_EXITS ];
        for( nI = 0; nI < Room.NUM_EXITS; nI++ )
            m_aptExits[ nI ] = new Point();

        // Initialize component
        setBounds( m_oRoom.m_rectBounds.x, m_oRoom.m_rectBounds.y,
            m_oRoom.m_rectBounds.width, m_oRoom.m_rectBounds.height );
        setRequestFocusEnabled( true );
        updateText();
        updateExitLocations();

        // Add event listeners
        oMouseInputListener = new MouseInputListener();
        addMouseListener( oMouseInputListener );
        addMouseMotionListener( oMouseInputListener );
        addFocusListener( new FocusListener() );
    }


    // **********************************************************************
    // *********************  Public Instance Methods   *********************
    // **********************************************************************

    /**
     * Determines which portion of the room the specified point lies on.
     *
     * @param  pt  The point to be tested.
     *
     * @return  One of the hit test identifiers HIT_EXIT_NW, HIT_EXIT_N,
     *     HIT_EXIT_NE, HIT_EXIT_W, HIT_EXIT_E, HIT_EXIT_SW, HIT_EXIT_S,
     *     HIT_EXIT_SE, HIT_EXIT_U, HIT_EXIT_D, HIT_ROOM_NORTH,
     *     HIT_ROOM_SOUTH, HIT_ROOM_WEST, HIT_ROOM_EAST, HIT_ROOM_CENTER.
     */

    public int hitTest( Point pt )
    {
        /////////////////////////////////////////////////////////////////////
        // VARIABLE DECLARATIONS                                           //

        Rectangle rectExit;  // Bounds of a room exit
        int       nI;        // Loop control variable

        //                                                                 //
        /////////////////////////////////////////////////////////////////////

        // Determine if an exit was hit
        rectExit = new Rectangle( 0, 0, EXIT_WIDTH, EXIT_HEIGHT );
        for( nI = 0; nI < Room.NUM_EXITS; nI++ )
        {
            rectExit.setLocation( m_aptExits[ nI ] );
            if( rectExit.contains( pt ) )
                return( HIT_EXIT_NW + nI );
        }

        // Determine if the room interior was hit or a region outside
        if( pt.y <= EXIT_HEIGHT )
            return( HIT_ROOM_NORTH );
        else if( pt.y >= m_aptExits[ Room.EXIT_S ].y )
            return( HIT_ROOM_SOUTH );
        else if( pt.x <= EXIT_WIDTH )
            return( HIT_ROOM_WEST );
        else if( pt.x >= m_aptExits[ Room.EXIT_E ].x )
            return( HIT_ROOM_EAST );
        else
            return( HIT_ROOM_CENTER );
    }


    // **********************************************************************
    // ********************* Protected Instance Methods *********************
    // **********************************************************************

    /**
     * Updates the room text.  The text is reformatted to the current room
     * bounds.
     */

    protected final void updateText()
    {
        // Reparse the room text
        m_astrText = soloff.steven.text.Utilities.splitStringByLines( m_oRoom.m_strName );
    }

    /**
     * Updates the coordinates of each exit block based on the current size
     * of the component.
     */

    protected final void updateExitLocations()
    {
        // Update exit block locations
        for( int nExit = 0; nExit < Room.NUM_EXITS; nExit++ )
            m_aptExits[ nExit ] = m_oRoom.getExitLocation( nExit );
    }


    // **********************************************************************
    // *********************     ComponentUI Methods    *********************
    // **********************************************************************

    /**
     * @see  soloff.steven.ifmap.ComponentUI#editUI()  editUI
     */

    public void editUI()
    {
        // Display the Room Editor dialog
        RoomEditorDialog dlg = new RoomEditorDialog(
            JOptionPane.getFrameForComponent( this ), m_oRoom );
        dlg.setVisible( true );

        // Update the UI
        updateText();
        repaint();
    }

    /**
     * @see  soloff.steven.ifmap.ComponentUI#deleteUI()  deleteUI
     */

    public void deleteUI()
    {
        // Delete the Room from the Map
        m_oMapUI.deleteRoom( this );
    }


    // **********************************************************************
    // *********************    Component Overrides     *********************
    // **********************************************************************

    /**
     * @see  java.awt.Component#setLocation( int, int )  setLocation
     */

    public void setLocation( int nX, int nY )
    {
        // Call base class implementation and update room bounds
        super.setLocation( nX, nY );
        m_oRoom.m_rectBounds.setLocation( nX, nY );
    }

    /**
     * @see  java.awt.Component#setLocation( Point )  setLocation
     */

    public void setLocation( Point pt )
    {
        // Call basic implementation
        setLocation( pt.x, pt.y );
    }

    /**
     * @see  java.awt.Component#setSize( int, int )  setSize
     */

    public void setSize( int nWidth, int nHeight )
    {
        // Call base class implementation and update room bounds
        super.setSize( nWidth, nHeight );
        m_oRoom.m_rectBounds.setSize( nWidth, nHeight );

        // Update text and exit locations
        updateExitLocations();
        updateText();
    }

    /**
     * @see  java.awt.Component#setSize( Dimension )  setSize
     */

    public void setSize( Dimension dm )
    {
        // Call basic implementation
        setSize( dm.width, dm.height );
    }


    // **********************************************************************
    // *********************    JComponent Overrides    *********************
    // **********************************************************************

    /**
     * @see  javax.swing.JComponent#paintComponent( Graphics )  paintComponent
     */

    protected void paintComponent( Graphics g )
    {
        /////////////////////////////////////////////////////////////////////
        // VARIABLE DECLARATIONS                                           //

        Graphics    gText;        // Graphics context solely for room text
        FontMetrics fm;           // Metrics of selected font
        Dimension   dmText,       // Dimensions of room text
                    dmInterior;   // Dimensions of room interior
        Point       ptText;       // Top-left coordinate of room text
        int         nFontHeight,  // Height of the selected font
                    nI;           // Loop control variable

        //                                                                 //
        /////////////////////////////////////////////////////////////////////

        // Call base class implementation
        super.paintComponent( g );

        // Compute the dimensions of the room interior
        dmInterior = new Dimension( m_oRoom.m_rectBounds.width - 2 * EXIT_WIDTH,
            m_oRoom.m_rectBounds.height - 2 * EXIT_HEIGHT );

        // Draw the room interior
        g.setColor( m_oRoom.m_clrBackground );
        g.fillRect( EXIT_WIDTH, EXIT_HEIGHT, dmInterior.width, dmInterior.height );

        // Draw the room border
        g.setColor( hasFocus() ? Color.red : Color.black );
        g.drawRect( EXIT_WIDTH - 1, EXIT_HEIGHT - 1, dmInterior.width + 1,
            dmInterior.height + 1 );

        // Draw the exits (color is identical to border color)
        for( nI = 0; nI < Room.NUM_EXITS; nI++ )
            g.fillRect( m_aptExits[ nI ].x, m_aptExits[ nI ].y, EXIT_WIDTH, EXIT_HEIGHT );

        // Create a Graphics context for the text and initialize all appropriate members
        gText = g.create( EXIT_WIDTH, EXIT_HEIGHT, dmInterior.width - 1,
            dmInterior.height - 1 );
        gText.setColor( m_oRoom.m_clrForeground );
        fm = gText.getFontMetrics();
        dmText = soloff.steven.text.Utilities.computeMultiLineStringDimension( fm, m_astrText );
        ptText = new Point( (dmInterior.width - dmText.width - 1) / 2,
            (dmInterior.height - dmText.height - 1) / 2 + fm.getAscent() );
        nFontHeight = fm.getHeight();

        // Draw text
        for( nI = 0; nI < m_astrText.length; nI++ )
            gText.drawString( m_astrText[nI], ptText.x + (dmText.width -
                SwingUtilities.computeStringWidth( fm, m_astrText[ nI ] )) / 2,
                ptText.y + nI * nFontHeight );
    }


    // **********************************************************************
    // *********************         Listeners          *********************
    // **********************************************************************

    /**
     * The object that is responsible for listening for focus events fired by
     * the enclosing RoomUI component.
     */

    protected class FocusListener
        extends FocusAdapter
    {
        // ------------------------------------------------------------------
        // -----------------   FocusAdapter Overrides   ---------------------
        // ------------------------------------------------------------------

        /**
         * @see  java.awt.FocusAdapter#focusGained( FocusEvent )  focusGained
         */

        public void focusGained( FocusEvent evt )
        {
            // Repaint the component
            repaint();
        }

        /**
         * @see  java.awt.FocusAdapter#focusLost( FocusEvent )  focusLost
         */

        public void focusLost( FocusEvent evt )
        {
            // Repaint the component
            repaint();
        }
    }

    /**
     * The object that is responsible for listening for mouse input events
     * fired by the enclosing RoomUI component.
     */

    protected class MouseInputListener
        extends MouseInputAdapter
    {
        // ------------------------------------------------------------------
        // ----------------- MouseInputAdapter Overrides --------------------
        // ------------------------------------------------------------------

        /**
         * @see  javax.swing.MouseInputAdapter#mousePressed( MouseEvent )
         *     mousePressed
         */

        public void mousePressed( MouseEvent evt )
        {
            // Request focus when a mouse button is pressed on this component
            requestFocus();
        }

        /**
         * @see  javax.swing.MouseInputAdapter#mouseClicked( MouseEvent )
         *     mouseClicked
         */

        public void mouseClicked( MouseEvent evt )
        {
            // Check for a double left click
            if( evt.getClickCount() == 2 && SwingUtilities.isLeftMouseButton( evt ) )
                editUI();
        }

        /**
         * @see  javax.swing.MouseInputAdapter#mouseMoved( MouseEvent )
         *     mouseMoved
         */

        public void mouseMoved( MouseEvent evt )
        {
            /////////////////////////////////////////////////////////////////
            // VARIABLE DECLARATIONS                                       //

            int nCursorType;  // Type of cursor to be displayed

            //                                                             //
            /////////////////////////////////////////////////////////////////

            // Determine the type of cursor to display
            switch( hitTest( evt.getPoint() ) )
            {
                case HIT_ROOM_CENTER:
                    nCursorType = Cursor.HAND_CURSOR;
                    break;

                case HIT_ROOM_NORTH:
                    nCursorType = Cursor.N_RESIZE_CURSOR;
                    break;

                case HIT_ROOM_SOUTH:
                    nCursorType = Cursor.S_RESIZE_CURSOR;
                    break;

                case HIT_ROOM_WEST:
                    nCursorType = Cursor.W_RESIZE_CURSOR;
                    break;

                case HIT_ROOM_EAST:
                    nCursorType = Cursor.E_RESIZE_CURSOR;
                    break;

                default:
                    nCursorType = Cursor.CROSSHAIR_CURSOR;
                    break;
            }

            // Set the cursor
            setCursor( Cursor.getPredefinedCursor( nCursorType ) );
        }
    }
}
