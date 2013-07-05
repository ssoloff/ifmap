/*
 * MapUI.java
 *
 * Copyright (c) 2000-2001 by Steven M. Soloff.
 * All rights reserved.
 *
 */

package soloff.steven.ifmap;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Iterator;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;

/**
 * The object that provides the user interface for a Map object.
 *
 * @author   Steven M. Soloff
 * @version  1.1.0 (02/06/01)
 */

public class MapUI
    extends JPanel
    implements ComponentUI
{
    // **********************************************************************
    // *********************         Constants          *********************
    // **********************************************************************

    /**
     * The size (both height and width) of the grid in pixels.
     */
    public static final int GRID_SIZE = 10;

    /**
     * The minimum width of the map in pixels.
     */
    protected static final int MIN_MAP_WIDTH = 400;

    /**
     * The minimum height of the map in pixels.
     */
    protected static final int MIN_MAP_HEIGHT = 200;

    /**
     * The amount in pixels by which the mouse pointer can be +/- from an edge
     * for a hit test to register on the edge in question.
     */
    protected static final int EDGE_HIT_THRESHOLD = 5;

    /**
     * The width in pixels of the head drawn for one-way edges.
     */
    protected static final int EDGE_HEAD_WIDTH = 10;

    /**
     * The height in pixels of the head drawn for one-way edges.
     */
    protected static final int EDGE_HEAD_HEIGHT = 4;


    // **********************************************************************
    // *********************     Instance Variables     *********************
    // **********************************************************************

    /**
     * The Map object rendered by this user interface object.
     */
    protected Map m_oMap;

    /**
     * The hashtable used to reference all RoomUI objects contained within
     * the MapUI.
     */
    protected HashMap m_oRoomUIMap;

    /**
     * Reference to the UI component that currently has the focus (either
     * this MapUI or a RoomUI).  If a MapUI is active, then m_oFocusEdge
     * is a reference to the edge with pseudo-focus.
     */
    ComponentUI m_oActiveComponent;

    /**
     * When the MapUI component has focus, this is a reference to the
     * edge that has the pseudo-focus.
     */
    Edge m_oFocusEdge;

    /**
     * Indicates if the user is currently dragging a room.
     */
    protected boolean m_bDraggingRoom;

    /**
     * Indicates if the user is currently sizing a room.
     */
    protected boolean m_bSizingRoom;

    /**
     * Indicates if the user is currently dragging an edge.
     */
    protected boolean m_bDraggingEdge;

    /**
     * Indicates if the view grid is currently enabled.
     */
    protected boolean m_bGridEnabled;

    /**
     * Indicates if group movement is currently enabled (i.e. when the user
     * drags a room, all rooms and edges are moved simultaneously).
     */
    protected boolean m_bGroupMoveEnabled;

    /**
     * Object used to listen for mouse input events fired by this object
     * and all child UI objects.
     */
    protected MouseInputListener m_oMouseInputListener;

    /**
     * Stores the starting point of an edge while it is being dragged.
     */
    protected Point m_ptDragStart;

    /**
     * Stores the ending point of an edge while it is being dragged.
     */
    protected Point m_ptDragEnd;


    // **********************************************************************
    // *********************       Class Variables      *********************
    // **********************************************************************

    /**
     * Serializable class version number.
     */
    private static final long serialVersionUID = -2766375575957272798L;


    // **********************************************************************
    // *********************        Constructors        *********************
    // **********************************************************************

    /**
     * Constructs a new MapUI object.
     *
     * @param  oMap  The Map object to be associated with this UI object.
     *
     * @exception  IllegalArgumentException  If oMap is null.
     */

    public MapUI( Map oMap )
        throws IllegalArgumentException
    {
        // Make sure arguments are valid
        if( oMap == null )
            throw new IllegalArgumentException();

        // Initialize instance variables
        m_oMap = oMap;
        m_oRoomUIMap = new HashMap();
        m_oActiveComponent = null;
        m_oFocusEdge = null;
        m_bDraggingRoom = false;
        m_bSizingRoom = false;
        m_bDraggingEdge = false;
        m_bGridEnabled = Boolean.valueOf( IFMap.getProperty( IFMap.PROP_VIEW_GRID ) ).booleanValue();
        m_bGroupMoveEnabled = Boolean.valueOf( IFMap.getProperty( IFMap.PROP_VIEW_GROUPMOVE ) ).booleanValue();

        // Remove layout manager
        setLayout( null );

        // Add event listeners
        m_oMouseInputListener = new MouseInputListener();
        addMouseListener( m_oMouseInputListener );
        addMouseMotionListener( m_oMouseInputListener );
        addFocusListener( new FocusListener() );
    }


    // **********************************************************************
    // *********************  Public Instance Methods   *********************
    // **********************************************************************

    /**
     * Indicates if the grid is enabled.
     *
     * @return  A flag indicating if the grid is enabled.
     */

    public boolean isGridEnabled()
    {
        // Return the grid enabled flag
        return( m_bGridEnabled );
    }

    /**
     * Enables or disables the grid.
     *
     * @param  bEnable  Indicates the grid is to be enabled or disabled.
     */

    public void enableGrid( boolean bEnable )
    {
        // Make sure the flag is changing
        if( m_bGridEnabled != bEnable )
        {
            // Set the grid enabled flag and repaint the component
            m_bGridEnabled = bEnable;
            repaint();
        }
    }

    /**
     * Indicates if group movement is enabled.
     *
     * @return  A flag indicating if group movement is enabled.
     */

    public boolean isGroupMoveEnabled()
    {
        // Return the group movement enabled flag
        return( m_bGroupMoveEnabled );
    }

    /**
     * Enables or disables group movement.
     *
     * @param  bEnable  Indicates group movement is to be enabled or disabled.
     */

    public void enableGroupMove( boolean bEnable )
    {
        // Set the group move enabled flag
        m_bGroupMoveEnabled = bEnable;
    }

    /**
     * Indicates whether or not a ComponentUI object is currently active
     * (selected) so that various generic ComponentUI operations can be
     * performed.
     *
     * @return  A flag indicating whether or not a ComponentUI object is
     *     active.
     */

    public boolean isActiveComponentAvailable()
    {
        // Return a flag indicating if there is an active component
        return( m_oActiveComponent != null );
    }

    /**
     * Clears the contents of the map user interface.
     */

    public void clear()
    {
        // Remove all components from this container and reset UI attributes
        removeAll();
        m_oRoomUIMap.clear();
        m_oActiveComponent = null;
        m_oFocusEdge = null;

        // Revalidate and repaint the component
        revalidate();
        repaint();
    }

    /**
     * Creates a new Room/RoomUI combination and adds it to the Map/MapUI
     * objects.
     *
     * @return  The new RoomUI object created.
     */

    public RoomUI createRoom()
    {
        // Create the Room/RoomUI combination and add them to the Map UI
        Room oRoom = m_oMap.createRoom();
        RoomUI oRoomUI = new RoomUI( this, oRoom );
        m_oRoomUIMap.put( oRoom.m_strID, oRoomUI );
        add( oRoomUI );
        oRoomUI.repaint();

        // Add event listeners for the new RoomUI
        oRoomUI.addMouseListener( m_oMouseInputListener );
        oRoomUI.addMouseMotionListener( m_oMouseInputListener );

        // Return the new RoomUI object
        return( oRoomUI );
    }

    /**
     * Creates a new Room/RoomUI combination using the specified Room and
     * adds it to the Map/MapUI objects.
     *
     * @param  oRoom  The Room to be used in the new Room/RoomUI combination.
     *
     * @return  The new RoomUI object created.
     *
     * @exception  IllegalArgumentExeption  If oRoom is null.
     */

    public RoomUI addRoom( Room oRoom )
        throws IllegalArgumentException
    {
        // Make sure arguments are valid
        if( oRoom == null )
            throw new IllegalArgumentException();

        // Create the Room/RoomUI combination and add them to the Map UI
        RoomUI oRoomUI = new RoomUI( this, oRoom );
        m_oRoomUIMap.put( oRoom.m_strID, oRoomUI );
        add( oRoomUI );
        oRoomUI.repaint();

        // Add event listeners for the new RoomUI
        oRoomUI.addMouseListener( m_oMouseInputListener );
        oRoomUI.addMouseMotionListener( m_oMouseInputListener );

        // Return the new RoomUI object
        return( oRoomUI );
    }

    /**
     * Deletes the Room/RoomUI combination and removes it from the
     * Map/MapUI objects.
     *
     * @param  oRoomUI  The RoomUI object of the Room/RoomUI pair to delete.
     */

    public void deleteRoom( RoomUI oRoomUI )
    {
        // Delete the Room in the Map
        Room oRoom = oRoomUI.m_oRoom;
        m_oMap.deleteRoom( oRoom );

        // Remove the Room UI object and repaint the Map UI (NOTE: we repaint
        // the entire Map UI because all the edges that are connected to the
        // room have been deleted and we're not quite sure of their bounds)
        m_oRoomUIMap.remove( oRoom.m_strID );
        remove( oRoomUI );
        if( oRoomUI == m_oActiveComponent )
            m_oActiveComponent = null;
        repaint();
    }

    /**
     * Deletes the Edge and removes it from the Map/MapUI objects.
     *
     * @param  oEdge  The Edge object to delete.
     */

    public void deleteEdge( Edge oEdge )
    {
        // Delete the Edge in the Map
        m_oMap.deleteEdge( oEdge );

        // Remove the Edge UI stuff and repaint the Map UI
        if( this == m_oActiveComponent )
            m_oActiveComponent = null;
        if( oEdge == m_oFocusEdge )
            m_oFocusEdge = null;
        repaintEdge( oEdge );
    }

    /**
     * Edits the active component.  Does nothing if no component is active.
     */

    public void editActiveComponent()
    {
        // Edit the component that has focus in the MapUI
        if( m_oActiveComponent != null )
            m_oActiveComponent.editUI();
    }

    /**
     * Deletes the active component.  Does nothing if no component is active.
     */

    public void deleteActiveComponent()
    {
        // Delete the component that has focus in the MapUI
        if( m_oActiveComponent != null )
            m_oActiveComponent.deleteUI();
    }

    /**
     * Determines which edge on the map the specified point lies on.
     *
     * @param  pt  The point to be tested.
     *
     * @return  The edge underneath the point or null if there is none.
     */

    public Edge edgeHitTest( Point pt )
    {
        /////////////////////////////////////////////////////////////////////
        // VARIABLE DECLARATIONS                                           //

        Edge      oEdge;        // Current Edge
        Iterator  iter;         // Iterator for Edge map
        Rectangle rect;         // Bounding rectangle of circular Edge
        Polygon   poly;         // Bounding polygon of line Edge
        Point     ptEdgeStart,  // Start point of an Edge
                  ptEdgeEnd;    // End point of an Edge
        double    dTheta;       // Angle the edge makes with the horizontal
        int[]     anPolyX,      // Array of x-coordinates for polygon vertices
                  anPolyY;      // Array of y-coordinates for polygon vertices
        int       nDX,          // Width of polygon
                  nDY;          // Height of polygn

        //                                                                 //
        /////////////////////////////////////////////////////////////////////

        // Loop through all the edges on the map
        for( iter = m_oMap.m_oEdgeMap.values().iterator(); iter.hasNext(); )
        {
            // Get a reference to the next edge
            oEdge = (Edge)iter.next();

            // Hit test is handled differently for line edges and circular edges
            if( oEdge.getStartRoomID().equals( oEdge.getEndRoomID() ) )
            {
                // Get the bounds of the circular edge
                rect = getEdgeBounds( oEdge );

                // Determine if the edge boundary contains the specified point
                if( Math.abs( pt.distance( rect.getCenterX(), rect.getCenterY() ) -
                        rect.getWidth() / 2.0 ) <= EDGE_HIT_THRESHOLD )
                    return( oEdge );
            }
            else
            {
                // Compute the height and width of the bounding polygon for the edge
                ptEdgeStart = oEdge.getStartPoint();
                ptEdgeEnd = oEdge.getEndPoint();
                dTheta = Math.atan2( ptEdgeStart.y - ptEdgeEnd.y, ptEdgeEnd.x - ptEdgeStart.x );
                nDX = (int)(EDGE_HIT_THRESHOLD * Math.sin( dTheta ));
                nDY = (int)(EDGE_HIT_THRESHOLD * Math.cos( dTheta ));

                // Compute the bounding polygon of the edge
                anPolyX = new int[ 4 ];
                anPolyY = new int[ 4 ];
                anPolyX[ 0 ] = ptEdgeStart.x - nDX;
                anPolyX[ 1 ] = ptEdgeEnd.x - nDX;
                anPolyX[ 2 ] = ptEdgeEnd.x + nDX;
                anPolyX[ 3 ] = ptEdgeStart.x + nDX;
                anPolyY[ 0 ] = ptEdgeStart.y - nDY;
                anPolyY[ 1 ] = ptEdgeEnd.y - nDY;
                anPolyY[ 2 ] = ptEdgeEnd.y + nDY;
                anPolyY[ 3 ] = ptEdgeStart.y + nDY;
                poly = new Polygon( anPolyX, anPolyY, 4 );

                // Determine if the edge boundary contains the specified point
                if( poly.contains( pt ) )
                    return( oEdge );
            }
        }

        // No edge is under the specified point
        return( null );
    }


    // **********************************************************************
    // ********************* Protected Instance Methods *********************
    // **********************************************************************

    /**
     * Causes a repaint to occur in the region specified by rect.
     *
     * @param  rect  Rectangle that defines the region to be repainted.
     */

    protected void repaintRegion( Rectangle rect )
    {
        // Inflate the specified rectangle by one pixel on all sides and repaint
        Rectangle rectClone = new Rectangle( rect );
        rectClone.grow( 1, 1 );
        repaint( rectClone );
    }

    /**
     * Causes a repaint to occur in the region specified by the bounds of
     * oEdge.
     *
     * @param  oEdge  The edge to be repainted.
     */

    protected void repaintEdge( Edge oEdge )
    {
        // Define rectangle specified by the edge bounds and repaint
        repaintRegion( getEdgeBounds( oEdge ) );
    }

    /**
     * Computes the bounding rectangle of the specified edge.
     *
     * @param  oEdge  The edge whose bounding rectangle is to be computed.
     *
     * @return  The bounding rectangle of the specified edge.
     */

    protected Rectangle getEdgeBounds( Edge oEdge )
    {
        /////////////////////////////////////////////////////////////////////
        // VARIABLE DECLARATIONS                                           //

        Rectangle rectEdge;
        Point ptStart = oEdge.getStartPoint();
        Point ptEnd = oEdge.getEndPoint();

        // CONSTANT DECLARATIONS                                           //

        final double SQRT2           = Math.sqrt( 2.0 );  // Square root of 2
        final int    MIN_EDGE_RADIUS = 10;                // Minimum radius for circular edges

        //                                                                 //
        /////////////////////////////////////////////////////////////////////

        // Determine if the edge is a line edge or a circular edge
        if( oEdge.getStartRoomID().equals( oEdge.getEndRoomID() ) )
        {
            // Determine if the circular edge connects the same exit
            if( oEdge.getStartExit() == oEdge.getEndExit() )
            {
                // Adjust left coordinate of edge bounds so it properly intersects exit
                switch( oEdge.getStartExit() )
                {
                    case Room.EXIT_NW:
                    case Room.EXIT_SW:
                        ptStart.x -= (int)(MIN_EDGE_RADIUS * (SQRT2 + 1.0) * SQRT2 / 2.0);
                        break;

                    case Room.EXIT_NE:
                    case Room.EXIT_SE:
                        ptStart.x -= (int)(MIN_EDGE_RADIUS * (SQRT2 - 1.0)) * SQRT2 / 2.0;
                        break;

                    case Room.EXIT_W:
                        ptStart.x -= 2 * MIN_EDGE_RADIUS;
                        break;

                    case Room.EXIT_U:
                    case Room.EXIT_N:
                    case Room.EXIT_D:
                    case Room.EXIT_S:
                        ptStart.x -= MIN_EDGE_RADIUS;
                        break;
                }

                // Adjust top coordinate of edge bounds so it properly intersects exit
                switch( oEdge.getStartExit() )
                {
                    case Room.EXIT_NW:
                    case Room.EXIT_NE:
                        ptStart.y -= (int)(MIN_EDGE_RADIUS * (SQRT2 + 1.0) * SQRT2 / 2.0);
                        break;

                    case Room.EXIT_U:
                    case Room.EXIT_N:
                        ptStart.y -= 2 * MIN_EDGE_RADIUS;
                        break;

                    case Room.EXIT_W:
                    case Room.EXIT_E:
                        ptStart.y -= MIN_EDGE_RADIUS;
                        break;

                    case Room.EXIT_SW:
                    case Room.EXIT_SE:
                        ptStart.y -= MIN_EDGE_RADIUS * (SQRT2 - 1.0) * SQRT2 / 2.0;
                        break;
                }

                // Compute bounding rectangle of edge
                rectEdge = new Rectangle( ptStart.x, ptStart.y,
                    2 * MIN_EDGE_RADIUS + 1, 2 * MIN_EDGE_RADIUS + 1 );
            }
            else
            {
                Point2D.Double pt1 = new Point2D.Double();
                Point2D.Double pt2 = new Point2D.Double();
                Point2D.Double ptCenter1 = new Point2D.Double();
                Point2D.Double ptCenter2 = new Point2D.Double();
                Point2D.Double ptCenter = new Point2D.Double();
                Point2D.Double ptMid = new Point2D.Double();
                Point2D.Double ptRoomMid = new Point2D.Double();

                RoomUI oRoomUI;
                Rectangle rectRoom;
                double dAlpha, dA, dB, dC, dR, dDiscrim;

                pt1.setLocation( oEdge.getStartPoint() );
                pt2.setLocation( oEdge.getEndPoint() );

                oRoomUI = (RoomUI)m_oRoomUIMap.get( oEdge.getStartRoomID() );
                rectRoom = oRoomUI.getBounds();

                double rTemp = Math.max( rectRoom.width, rectRoom.height );
                rTemp = Math.max( rTemp, Math.sqrt( (pt2.x - pt1.x) * (pt2.x - pt1.x) + (pt2.y - pt1.y) * (pt2.y - pt1.y) ) );
                //dR = 0.75 * pt1.distance( pt2 ); //Math.sqrt( (pt2.x - x1) * (x2 - x1) + (pt2.y - pt1.y) * (pt2.y - pt1.y) );
// !!!!!!!!!!!!!!! go back to original R since most exits will look weird so large
                dR = 0.55 * rTemp;
                dAlpha = 0.5 * (pt2.x * pt2.x - pt1.x * pt1.x + pt2.y * pt2.y - pt1.y * pt1.y);

                // Compute the two solutions for the x-coordinate of circle center
                dA = (pt2.x - pt1.x) * (pt2.x - pt1.x) + (pt2.y - pt1.y) * (pt2.y - pt1.y);
                dB = 2.0 * (pt2.y - pt1.y) * (pt1.y * (pt2.x - pt1.x) - pt1.x * (pt2.y - pt1.y)) -
                    2.0 * dAlpha * (pt2.x - pt1.x);
                dC = (pt2.y - pt1.y) * (pt2.y - pt1.y) * (pt1.x * pt1.x + pt1.y * pt1.y - dR * dR) +
                    dAlpha * (dAlpha - 2.0 * pt1.y * (pt2.y - pt1.y));
                dDiscrim = Math.sqrt( dB * dB - 4.0 * dA * dC );
                ptCenter1.x = (-dB + dDiscrim) / (2.0 * dA);
                ptCenter2.x = (-dB - dDiscrim) / (2.0 * dA);

                // Compute the two solutions for the y-coordinate of circle center
                // (the same as above) dA = (pt2.x - pt1.x) * (pt2.x - pt1.x) + (pt2.y - pt1.y) * (pt2.y - pt1.y);
                dB = 2.0 * (pt2.x - pt1.x) * (pt1.x * (pt2.y - pt1.y) - pt1.y * (pt2.x - pt1.x)) -
                    2.0 * dAlpha * (pt2.y - pt1.y);
                dC = (pt2.x - pt1.x) * (pt2.x - pt1.x) * (pt1.x * pt1.x + pt1.y * pt1.y - dR * dR) +
                    dAlpha * (dAlpha - 2.0 * pt1.x * (pt2.x - pt1.x));
                dDiscrim = Math.sqrt( dB * dB - 4.0 * dA * dC );
                ptCenter1.y = (-dB + dDiscrim) / (2.0 * dA);
                ptCenter2.y = (-dB - dDiscrim) / (2.0 * dA);

                // Compute the midpoints of the room and the line that connects the
                // edge exits
                ptRoomMid.setLocation( rectRoom.getCenterX(), rectRoom.getCenterY() );
                ptMid.setLocation( (pt1.x + pt2.x) / 2.0, (pt1.y + pt2.y) / 2.0 );

                // Arbitrarily use the x- and y- solutions that are skewed the most in the
                // direction of the midpoint of the line that connects the starting and
                // ending points of the edge relative to the midpoint of the room for the
                // center of the arc.
                ptCenter.x = ptMid.x < ptRoomMid.x ? Math.min( ptCenter1.x, ptCenter2.x ) :
                    Math.max( ptCenter1.x, ptCenter2.x );
                ptCenter.y = ptMid.y < ptRoomMid.y ? Math.min( ptCenter1.y, ptCenter2.y ) :
                    Math.max( ptCenter1.y, ptCenter2.y );

                // Make sure the point that was arbitrarily chosen is actually a circle
                // (if it is it will be equidistant from the starting and ending points
                // of the edge).
                if( Math.abs( ptCenter.distance( pt1 ) - ptCenter.distance( pt2 ) ) > 1.0 )
                {
                    // Otherwise switch the solution of the coordinate that is closer to
                    // the midpoint of the room (we want the coordinate furthest away to
                    // dominate the solution).
                    if( Math.abs( ptMid.x - ptRoomMid.x ) > Math.abs( ptMid.y - ptRoomMid.y ) )
                        ptCenter.y = ptCenter.y == ptCenter1.y ? ptCenter2.y : ptCenter1.y;
                    else
                        ptCenter.x = ptCenter.x == ptCenter1.x ? ptCenter2.x : ptCenter1.x;
                }

                // Compute bounding rectangle of edge
                rectEdge = new Rectangle();
                rectEdge.setRect( ptCenter.x - dR, ptCenter.y - dR, 2.0 * dR + 1.0, 2.0 * dR + 1.0 );
            }
        }
        else
        {
            // Compute bounding rectangle of edge
            rectEdge = new Rectangle( ptStart );
            rectEdge.add( ptEnd );
        }

        // Return bounding rectangle of edge
        return( rectEdge );
    }

    /**
     * Draws a one-way head at the endpoint of the specified edge using the
     * given graphics context.
     *
     * @param  g  The graphics context used for drawing.
     * @param  oEdge  The Edge whose one-way head is to be drawn.
     */

    protected void drawEdgeHead( Graphics g, Edge oEdge )
    {
        /////////////////////////////////////////////////////////////////////
        // VARIABLE DECLARATIONS                                           //

        Point  ptEdgeStart,  // Starting point of edge
               ptEdgeEnd,    // Ending point of edge
               pt;           // Reference point along edge head
        int[]  anPolyX;      // x-coordinates of edge head polygon
        int[]  anPolyY;      // y-coordinates of edge head polygon
        double dTheta,       // Angle of edge head
               dSinTheta,    // Sine of dTheta
               dCosTheta;    // Cosine of dTheta
        int    nDX,          // x-distance between reference point and head butt corners
               nDY;          // y-distance between reference point and head butt corners

        //                                                                 //
        /////////////////////////////////////////////////////////////////////

        // Get the starting and ending points of the edge
        ptEdgeStart = oEdge.getStartPoint();
        ptEdgeEnd = oEdge.getEndPoint();

        // Determine if the edge is a line edge or a circular edge
        if( oEdge.getStartRoomID().equals( oEdge.getEndRoomID() ) )
        {
            // Compute various quantities
            Rectangle rectEdge = getEdgeBounds( oEdge );
            double dXCenter = rectEdge.getCenterX();
            double dYCenter = rectEdge.getCenterY();
            double dThetaEnd = Math.atan2( dYCenter - ptEdgeEnd.y, ptEdgeEnd.x - dXCenter );
            double dThetaStart = Math.atan2( dYCenter - ptEdgeStart.y, ptEdgeStart.x - dXCenter );
            double dAbsThetaDiff = Math.abs( dThetaEnd - dThetaStart );

            // Compute the angle of the edge head
            if( (dThetaEnd > dThetaStart && dAbsThetaDiff < Math.PI) ||
                (dThetaEnd < dThetaStart && dAbsThetaDiff > Math.PI) )
                dTheta = dThetaEnd - Math.PI / 2.0;
            else
                dTheta = dThetaEnd + Math.PI / 2.0;
        }
        else
        {
            // Compute the angle of the edge head
            dTheta = Math.atan2( ptEdgeStart.y - ptEdgeEnd.y, ptEdgeEnd.x - ptEdgeStart.x );
        }

        // Compute the bounding polygon of the edge head
        dSinTheta = Math.sin( dTheta );
        dCosTheta = Math.cos( dTheta );
        nDX = (int)(EDGE_HEAD_HEIGHT * dSinTheta);
        nDY = (int)(EDGE_HEAD_HEIGHT * dCosTheta);
        pt = new Point();
        pt.x = ptEdgeEnd.x - (int)(EDGE_HEAD_WIDTH * dCosTheta);
        pt.y = ptEdgeEnd.y + (int)(EDGE_HEAD_WIDTH * dSinTheta);
        anPolyX = new int[ 3 ];
        anPolyY = new int[ 3 ];
        anPolyX[ 0 ] = ptEdgeEnd.x;
        anPolyX[ 1 ] = pt.x - nDX;
        anPolyX[ 2 ] = pt.x + nDX;
        anPolyY[ 0 ] = ptEdgeEnd.y;
        anPolyY[ 1 ] = pt.y - nDY;
        anPolyY[ 2 ] = pt.y + nDY;

        // Draw the edge head polygon
        g.fillPolygon( anPolyX, anPolyY, 3 );
    }


    // **********************************************************************
    // *********************     ComponentUI Methods    *********************
    // **********************************************************************

    /**
     * @see  soloff.steven.ifmap.ComponentUI#editUI()  editUI
     */

    public void editUI()
    {
        // Make sure there is something to edit
        if( m_oFocusEdge != null )
        {
            // Display the Edge Editor dialog
            EdgeEditorDialog dlg = new EdgeEditorDialog(
                JOptionPane.getFrameForComponent( MapUI.this ), m_oFocusEdge );
            dlg.setVisible( true );

            // Repaint the edge
            repaintEdge( m_oFocusEdge );
        }
    }

    /**
     * @see  soloff.steven.ifmap.ComponentUI#deleteUI()  deleteUI
     */

    public void deleteUI()
    {
        // Delete the currently focused Edge
        if( m_oFocusEdge != null )
            deleteEdge( m_oFocusEdge );
    }


    // **********************************************************************
    // *********************    Component Overrides    **********************
    // **********************************************************************

    /**
     * @see  java.awt.Component#getPreferredSize()  getPreferredSize
     */

    public Dimension getPreferredSize()
    {
        // Return the size of the Map
        return( m_oMap.m_dmMap );
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

        Edge           oEdge;      // Edge to be drawn
        Iterator       iter;       // Iterator for edge map
        Rectangle      rectClip,   // The current clipping rectangle
                       rectEdge;   // Bounding rectangle of the current edge
        Point2D.Double pt1;        // Starting point of current circular (arc) edge
        Point2D.Double pt2;        // Ending point of current circular (arc) edge
        Point2D.Double ptCenter;   // Center of bounding rectangle
        Point          ptStart,    // Starting point of current edge
                       ptEnd;      // Ending point of current edge
        double         dAngle1,    // Smaller angle of circular (arc) edge
                       dAngle2,    // Larger angle of circular (arc) edge
                       dArcAngle;  // Angle made by circular (arc) edge
        int            nXMin,      // Minimum grid x-cooddinate
                       nXMax,      // Maximum grid x-coordinate
                       nYMin,      // Minimum grid y-coordinate
                       nYMax,      // Maximum grid y-coordinate
                       nX, nY;     // Loop control variables

        // CONSTANT DECLARATIONS                                           //

        final double RAD2DEG = 180.0 / Math.PI;  // Conversion from radians to degress

        //                                                                 //
        /////////////////////////////////////////////////////////////////////

        // Call base class implementation
        super.paintComponent( g );

        // Get the current clipping rectangle
        rectClip = g.getClipBounds();

        // Draw the grid if necessary
        if( m_bGridEnabled )
        {
            // Initialize bounds of grid
            g.setColor( Color.black );
            nXMin = (rectClip.x / GRID_SIZE) * GRID_SIZE;
            nXMax = ((rectClip.x + rectClip.width) / GRID_SIZE) * GRID_SIZE;
            nYMin = (rectClip.y / GRID_SIZE) * GRID_SIZE;
            nYMax = ((rectClip.y + rectClip.height) / GRID_SIZE) * GRID_SIZE;

            // Draw the grid
            for( nX = nXMin; nX <= nXMax; nX += GRID_SIZE )
                for( nY = nYMin; nY <= nYMax; nY += GRID_SIZE )
                    g.drawLine( nX, nY, nX, nY );
        }

        // Allocate helpers for circular (arc) edges
        pt1 = new Point2D.Double();
        pt2 = new Point2D.Double();
        ptCenter = new Point2D.Double();

        // Iterate through all the edges on the map
        for( iter = m_oMap.m_oEdgeMap.values().iterator(); iter.hasNext(); )
        {
            // Get the bounds of the next Edge
            oEdge = (Edge)iter.next();
            rectEdge = getEdgeBounds( oEdge );
            ptStart = oEdge.getStartPoint();
            ptEnd = oEdge.getEndPoint();

            // Determine if the edge intersects the clipping rectangle
            if( rectClip.intersects( rectEdge ) )
            {
                // Set the color for the edge
                g.setColor( hasFocus() && oEdge == m_oFocusEdge ? Color.red :
                    oEdge.isSecret() ? Color.gray : Color.black );

                // Determine if the edge is a line edge or a circular edge
                if( oEdge.getStartRoomID().equals( oEdge.getEndRoomID() ) )
                {
                    // Determine if the circular edge connects to the same exit
                    if( oEdge.getStartExit() == oEdge.getEndExit() )
                    {
                        // Draw the 360� circular edge
                        g.drawOval( rectEdge.x, rectEdge.y, rectEdge.width, rectEdge.height );
                    }
                    else
                    {
                        // Initialize locations of edge start, end, and bounding rectangle center
                        pt1.setLocation( ptStart );
                        pt2.setLocation( ptEnd );
                        ptCenter.setLocation( rectEdge.getCenterX(), rectEdge.getCenterY() );

                        // Compute the angle the starting and end points make with the
                        // center of the edge's bounding rectangle (remember that y is
                        // positive downward)
                        dAngle1 = Math.atan2( ptCenter.y - pt1.y, pt1.x - ptCenter.x ) * RAD2DEG;
                        dAngle2 = Math.atan2( ptCenter.y - pt2.y, pt2.x - ptCenter.x ) * RAD2DEG;

                        // Since atan2() returns angles in the range {-PI,PI}, we move
                        // them into the range {0,2*PI} since that is the system we
                        // are working in.
                        if( dAngle1 < 0.0 )
                            dAngle1 += 360.0;
                        if( dAngle2 < 0.0 )
                            dAngle2 += 360.0;

                        // Ensure that angle 1 is the smaller of the two angles
                        if( dAngle1 > dAngle2 )
                        {
                            double dTemp = dAngle1;
                            dAngle1 = dAngle2;
                            dAngle2 = dTemp;
                        }

                        // Determine which arc to draw and compute its coverage (if
                        // angle 2 leads angle 1 by more than 180�, draw the CCW arc;
                        // otherwise draw the CW arc; i.e. we draw the longest arc).
                        if( dAngle2 - dAngle1 >= 180.0 )
                            dArcAngle = dAngle2 - dAngle1;
                        else
                            dArcAngle = -(dAngle1 - (dAngle2 - 360.0));

                        // Draw the sub-360� circular edge
                        g.drawArc( rectEdge.x, rectEdge.y, rectEdge.width, rectEdge.height,
                            (int)dAngle1, (int)dArcAngle );

                        // Draw the arrowhead if the edge is one-way
                        if( oEdge.isOneWay() )
                            drawEdgeHead( g, oEdge );
                    }
                }
                else
                {
                    // Draw the edge
                    g.drawLine( ptStart.x, ptStart.y, ptEnd.x, ptEnd.y );

                    // Draw the arrowhead if the edge is one-way
                    if( oEdge.isOneWay() )
                        drawEdgeHead( g, oEdge );
                }
            }
        }

        // Draw the edge being dragged if applicable
        if( m_bDraggingEdge )
        {
            g.setColor( Color.black );
            g.drawLine( m_ptDragStart.x, m_ptDragStart.y, m_ptDragEnd.x, m_ptDragEnd.y );
        }
    }


    // **********************************************************************
    // *********************         Listeners          *********************
    // **********************************************************************

    /**
     * The object that is responsible for listening for focus events fired by
     * the enclosing MapUI component.
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
            // Repaint the edge with focus if applicable
            if( m_oFocusEdge != null )
                repaintEdge( m_oFocusEdge );
        }

        /**
         * @see  java.awt.FocusAdapter#focusLost( FocusEvent )  focusLost
         */

        public void focusLost( FocusEvent evt )
        {
            // Repaint the edge with focus if applicable
            if( m_oFocusEdge != null )
                repaintEdge( m_oFocusEdge );
        }
    }

    /**
     * The object that is responsible for listening for mouse input events
     * fired by the enclosing MapUI component.
     */

    protected class MouseInputListener
        extends MouseInputAdapter
    {
        Point m_ptGlobalMin;
        int m_nHitTest;
        Point m_ptOffset;
        RoomUI m_oRoomUIStart;
        int m_nSTART_EXIT;
        boolean m_bSecret, m_bOneWay;


        // ------------------------------------------------------------------
        // -----------------   Protected Instance Methods   -----------------
        // ------------------------------------------------------------------

        /**
         * Computes the minimal top and left coordinates of all rooms within
         * the map.
         *
         * @return  The global minimal top and left coordinates of all rooms
         *     within the map.
         */

        protected Point computeGlobalMin()
        {
            /////////////////////////////////////////////////////////////////
            // VARIABLE DECLARATIONS                                       //

            Iterator iter;         // Used to iterate the RoomUI list
            Point    ptGlobalMin,  // Global minimal x- and y- coordinates of all rooms
                     ptRoom;       // Location of a room

            //                                                             //
            /////////////////////////////////////////////////////////////////

            // Initialize the global minimal coordinates
            ptGlobalMin = new Point( Integer.MAX_VALUE, Integer.MAX_VALUE );

            // Iterate through all rooms
            for( iter = m_oRoomUIMap.values().iterator(); iter.hasNext(); )
            {
                // Get the location of the next room
                ptRoom = ((RoomUI)iter.next()).getLocation();

                // Check for new minimums
                if( ptRoom.x < ptGlobalMin.x )
                    ptGlobalMin.x = ptRoom.x;
                if( ptRoom.y < ptGlobalMin.y )
                    ptGlobalMin.y = ptRoom.y;
            }

            // Return the global minimum found
            return( ptGlobalMin );
        }

        /**
         * Includes the bounds of each edge that intersects that specified
         * rectangle.  The result is the union of the original rectangle
         * with the bounding rectangle of each edge that intersects it.
         *
         * @param  rect  The base rectangle to check for edge intersections.
         *
         * @return  The union of the original rectangle with the bounding
         *     rectangle of each edge that intersects it.
         */

        protected Rectangle includeEdges( Rectangle rect )
        {
            /////////////////////////////////////////////////////////////////
            // VARIABLE DECARATIONS                                        //

            Iterator  iter;      // Iterator for edge map
            Rectangle rectEdge,  // Bounds of the current edge
                      rectNew;   // New rectangle including intersecting edges

            //                                                             //
            /////////////////////////////////////////////////////////////////

            // Initialize new rectangle
            rectNew = new Rectangle( rect );

            // Iterate through all edges
            for( iter = m_oMap.m_oEdgeMap.values().iterator(); iter.hasNext(); )
            {
                // Get bounds of the next edge
                rectEdge = getEdgeBounds( (Edge)iter.next() );

                // If the edge intersects the base rectangle include it
                if( rect.intersects( rectEdge ) )
                    rectNew.add( rectEdge );
            }

            // Return the new rectangle
            return( rectNew );
        }

        /**
         * Processes mouse events in which a Room is being dragged.
         *
         * @param  evt  Describes the mouse event fired.
         */

        protected void roomDragged( MouseEvent evt )
        {
            /////////////////////////////////////////////////////////////////
            // VARIABLE DECLARATIONS                                       //

            RoomUI    oRoomUI;     // Reference to a RoomUI object
            Component oComponent;  // Component associated with mouse event
            Iterator  iter;        // Used to iterate through all RoomUI objects
            Rectangle rectOld,     // Previous bounds for edge being dragged
                      rectNew;     // New bounds for edge being dragged
            Point     ptCurrent,   // Point associated with mouse event
                      ptRoom,      // Location of room
                      ptTemp;      // Temporary location
            Dimension dmDelta;     // Amount to translate rooms for a global move

            //                                                             //
            /////////////////////////////////////////////////////////////////

            // Compute the old bounds of the room as well as the bounds of
            // all edges that intersect the room
            oComponent = evt.getComponent();
            rectOld = includeEdges( oComponent.getBounds() );

            // Compute the new location of the room
            ptCurrent = SwingUtilities.convertPoint( oComponent, evt.getPoint(), MapUI.this );
            ptRoom = new Point( ptCurrent.x - m_ptOffset.x, ptCurrent.y - m_ptOffset.y );

            // Discretize the location change if the grid is enabled
            if( m_bGridEnabled )
            {
                ptRoom.x = (ptRoom.x / GRID_SIZE) * GRID_SIZE;
                ptRoom.y = (ptRoom.y / GRID_SIZE) * GRID_SIZE;
            }

            // Ensure location does not go negative
            if( ptRoom.x < 0 )
                ptRoom.x = 0;
            if( ptRoom.y < 0 )
                ptRoom.y = 0;

            // Check if group move is enabled
            if( m_bGroupMoveEnabled )
            {
                // Compute the offset that all rooms will be translated by
                ptTemp = oComponent.getLocation();
                dmDelta = new Dimension( ptRoom.x - ptTemp.x, ptRoom.y - ptTemp.y );

                // Ensure global minimum does not go negative
                if( m_ptGlobalMin.x + dmDelta.width < 0 )
                    dmDelta.width = -m_ptGlobalMin.x;
                if( m_ptGlobalMin.y + dmDelta.height < 0 )
                    dmDelta.height = -m_ptGlobalMin.y;

                // Set the new global minimum
                m_ptGlobalMin.translate( dmDelta.width, dmDelta.height );

                // Iterate through all rooms
                for( iter = m_oRoomUIMap.values().iterator(); iter.hasNext(); )
                {
                    // Move this room by the specified amount
                    oRoomUI = (RoomUI)iter.next();
                    ptTemp = oRoomUI.getLocation();
                    ptTemp.translate( dmDelta.width, dmDelta.height );
                    oRoomUI.setLocation( ptTemp );
                }

                // Repaint the entire client area
                repaint();
            }
            else
            {
                // Set the new location of the room
                oComponent.setLocation( ptRoom );

                // Compute the new bounds of the room and repaint the union of
                // the old and new bounds, as well as the bounds of all edges
                // that intersect the room
                rectNew = oComponent.getBounds();
                repaintRegion( includeEdges( rectNew.union( rectOld ) ) );
            }
        }

        /**
         * Processes mouse events in which a Room is being sized.
         *
         * @param  evt  Describes the mouse event fired.
         */

        protected void roomSized( MouseEvent evt )
        {
            /////////////////////////////////////////////////////////////////
            // VARIABLE DECLARATIONS                                       //

            Component oComponent;  // Component associated with mouse event
            Rectangle rectOld,     // Previous bounds for edge being dragged
                      rectNew;     // New bounds for edge being dragged
            Dimension dmRoom,      // Dimensions of the room
                      dmDelta;     // Amount by which room size will change

            //                                                             //
            /////////////////////////////////////////////////////////////////

            // Compute the old bounds of the room as well as the bounds of
            // all edges that intersect the room
            oComponent = evt.getComponent();
            rectOld = includeEdges( oComponent.getBounds() );

            // Get the dimensions of the room and the change in room size
            dmRoom = oComponent.getSize();
            dmDelta = new Dimension( evt.getX(), evt.getY() );

            // Discretize the size change if the grid is enabled
            if( m_bGridEnabled )
            {
                dmDelta.width = (dmDelta.width / GRID_SIZE) * GRID_SIZE;
                dmDelta.height = (dmDelta.height / GRID_SIZE) * GRID_SIZE;
            }

            // Adjust room dimensions depending on the results of the initial hit test
            switch( m_nHitTest )
            {
                case RoomUI.HIT_ROOM_NORTH:
                    dmRoom.height -= dmDelta.height;
                    break;

                case RoomUI.HIT_ROOM_SOUTH:
                    dmRoom.height = dmDelta.height;
                    break;

                case RoomUI.HIT_ROOM_WEST:
                    dmRoom.width -= dmDelta.width;
                    break;

                case RoomUI.HIT_ROOM_EAST:
                    dmRoom.width = dmDelta.width;
                    break;
            }

            // Ensure the room is larger than the minimum allowed size
            if( dmRoom.height >= RoomUI.MIN_ROOM_HEIGHT && dmRoom.width >= RoomUI.MIN_ROOM_WIDTH )
            {
                // Set the new size of the room
                oComponent.setSize( dmRoom );

                // Set the new location of the room
                if( m_nHitTest == RoomUI.HIT_ROOM_NORTH )
                    oComponent.setLocation( oComponent.getX(), oComponent.getY() + dmDelta.height );
                else if( m_nHitTest == RoomUI.HIT_ROOM_WEST )
                    oComponent.setLocation( oComponent.getX() + dmDelta.width, oComponent.getY() );

                // Compute the new bounds of the room and repaint the union of
                // the old and new bounds, as well as the bounds of all edges
                // that intersect the room
                rectNew = oComponent.getBounds();
                repaintRegion( includeEdges( rectNew.union( rectOld ) ) );
            }
        }

        /**
         * Processes mouse events in which an Edge is being dragged.
         *
         * @param  evt  Describes the mouse event fired.
         */

        protected void edgeDragged( MouseEvent evt )
        {
            /////////////////////////////////////////////////////////////////
            // VARIABLE DECLARATIONS                                       //

            Rectangle rectOld,  // Previous bounds for edge being dragged
                      rectNew;  // New bounds for edge being dragged

            //                                                             //
            /////////////////////////////////////////////////////////////////

            // Compute the previous bounds of the dragged edge
            rectOld = new Rectangle( m_ptDragStart );
            rectOld.add( m_ptDragEnd );

            // Update the end of the dragged edge and compute its new bounds
            rectNew = new Rectangle( m_ptDragStart );
            m_ptDragEnd = SwingUtilities.convertPoint( evt.getComponent(),
                evt.getPoint(), MapUI.this );
            rectNew.add( m_ptDragEnd );

            // Repaint the union of the old and new bounds
            repaintRegion( rectNew.union( rectOld ) );
        }


        // ------------------------------------------------------------------
        // -----------------   MouseInputAdapter Overrides  -----------------
        // ------------------------------------------------------------------

        /**
         * @see  java.awt.MouseAdapter#mousePressed( MouseEvent )  mousePressed
         */

        public void mousePressed( MouseEvent evt )
        {
            /////////////////////////////////////////////////////////////////
            // VARIABLE DECLARATIONS                                       //

            Component oComponent;
            Edge oEdge;
            Point ptCurrent;
            int nStartExit;
            Room oRoom;

            //                                                             //
            /////////////////////////////////////////////////////////////////

            oComponent = evt.getComponent();
            m_oActiveComponent = (ComponentUI)oComponent;

            // Determine if the mouse was pressed on this component
            if( oComponent == MapUI.this )
            {
                // Repaint the edge that currently has the focus
                if( m_oFocusEdge != null )
                    repaintEdge( m_oFocusEdge );

                // Determine if the mouse was pressed over an edge
                if( (oEdge = edgeHitTest( evt.getPoint() )) != null )
                {
                    // Update the new edge with focus
                    m_oFocusEdge = oEdge;
                    repaintEdge( m_oFocusEdge );
                }
                else
                {
                    // Clear the focus
                    m_oFocusEdge = null;
                    m_oActiveComponent = null;
                }

                // Request focus
                requestFocus();
            }
            else
            {
                m_oRoomUIStart = (RoomUI)oComponent;
                oRoom = m_oRoomUIStart.m_oRoom;

                // Do hit test and process result
                switch( m_nHitTest = m_oRoomUIStart.hitTest( evt.getPoint() ) )
                {
                    case RoomUI.HIT_ROOM_CENTER:
                        m_ptOffset = evt.getPoint();
                        m_ptGlobalMin = computeGlobalMin();
                        m_bDraggingRoom = true;
                        break;

                    case RoomUI.HIT_ROOM_NORTH:
                    case RoomUI.HIT_ROOM_SOUTH:
                    case RoomUI.HIT_ROOM_WEST:
                    case RoomUI.HIT_ROOM_EAST:
                        m_bSizingRoom = true;
                        break;

                    default:
                        m_bDraggingEdge = true;
                        break;
                }

                // Do further processing when dragging an edge
                if( m_bDraggingEdge )
                {
                    m_nSTART_EXIT = nStartExit = m_nHitTest - RoomUI.HIT_EXIT_NW;
                    oEdge = m_oMap.getEdgeAtRoomExit( m_oRoomUIStart.m_oRoom, nStartExit );

                    // Determine if this is a new edge or an existing edge
                    if( oEdge == null )
                    {
                        // Initialize the endpoints of the new edge
                        ptCurrent = SwingUtilities.convertPoint( oComponent,
                            oRoom.getExitLocation( nStartExit ), MapUI.this );
                        ptCurrent.translate( RoomUI.EXIT_WIDTH / 2, RoomUI.EXIT_HEIGHT / 2 );
                        m_ptDragStart = m_ptDragEnd = ptCurrent;
                        m_bSecret = m_bOneWay = false;
                    }
                    else
                    {
                        ptCurrent = SwingUtilities.convertPoint( oComponent, evt.getPoint(), MapUI.this );
                        m_bSecret = oEdge.isSecret();
                        m_bOneWay = oEdge.isOneWay();

                        m_ptDragEnd = ptCurrent;
                        RoomUI roomUI;
                        int nExit;
                        if( oEdge.getStartRoomID().equals( m_oRoomUIStart.m_oRoom.m_strID ) &&
                            oEdge.getEndRoomID().equals( m_oRoomUIStart.m_oRoom.m_strID ) )
                        {
                            roomUI = (RoomUI)m_oRoomUIMap.get( oEdge.getStartRoomID() );
                            if( oEdge.getStartExit() == nStartExit )
                                nExit = oEdge.getEndExit();
                            else
                                nExit = oEdge.getStartExit();
                        }
                        else if( oEdge.getStartRoomID().equals( m_oRoomUIStart.m_oRoom.m_strID ) )
                        {
                            roomUI = (RoomUI)m_oRoomUIMap.get( oEdge.getEndRoomID() );
                            nExit = oEdge.getEndExit();
                        }
                        else
                        {
                            roomUI = (RoomUI)m_oRoomUIMap.get( oEdge.getStartRoomID() );
                            nExit = oEdge.getStartExit();
                        }
                        m_oRoomUIStart = roomUI;
                        m_nSTART_EXIT = nExit;
                        m_ptDragStart = new Point();
                        Point ptExit = roomUI.m_oRoom.getExitLocation( nExit );
                        ptExit.translate( RoomUI.EXIT_WIDTH / 2,
                            RoomUI.EXIT_HEIGHT / 2 );
                        m_ptDragStart.x = roomUI.m_oRoom.m_rectBounds.x + ptExit.x;
                        m_ptDragStart.y = roomUI.m_oRoom.m_rectBounds.y + ptExit.y;
                        m_oMap.m_oEdgeMap.remove( oEdge.getID() );

                        repaintEdge( oEdge );
                    }
                }
            }
        }

        /**
         * @see  java.awt.MouseAdapter#mouseReleased( MouseEvent )  mouseReleased
         */

        public void mouseReleased( MouseEvent evt )
        {
            Point pt = SwingUtilities.convertPoint( evt.getComponent(), evt.getPoint(), MapUI.this );
            Component comp = getComponentAt( pt );

            Rectangle rectPaint = null;

            if( comp instanceof RoomUI )
            {
                RoomUI oRoomUIEnd = (RoomUI)comp;
                Point pt2 = SwingUtilities.convertPoint( MapUI.this, pt, comp );
                int nEndExit = oRoomUIEnd.hitTest( pt2 );

                if( nEndExit >= RoomUI.HIT_EXIT_NW && nEndExit <= RoomUI.HIT_EXIT_D )
                {
                    nEndExit -= RoomUI.HIT_EXIT_NW;
                    //int nStartExit = m_nHitTest - RoomUI.HIT_EXIT_NW;
                    //System.out.println( nStartExit );
                    Edge edge = m_oMap.createEdge( m_oRoomUIStart.m_oRoom, m_nSTART_EXIT,
                        oRoomUIEnd.m_oRoom, nEndExit );
                    edge.setOneWay( m_bOneWay );
                    edge.setSecret( m_bSecret );

                    //rectPaint = new Rectangle( edge.getStartPoint() );
                    //rectPaint.add( edge.getEndPoint() );
                    rectPaint = getEdgeBounds( edge );
                    m_bDraggingEdge = false;
                    m_ptDragStart = m_ptDragEnd = null;  // do we need this??
                }
            }

            if( m_bDraggingRoom || m_bSizingRoom )
            {
                Rectangle rectNew = evt.getComponent().getBounds();
                boolean bChanged = false;

                // NEED TO OPTIMIZE

                if( rectNew.x + rectNew.width > m_oMap.m_dmMap.width )
                {
                    m_oMap.m_dmMap.width = rectNew.x + rectNew.width;
                    bChanged = true;
                }
                if( rectNew.y + rectNew.height > m_oMap.m_dmMap.height )
                {
                    m_oMap.m_dmMap.height = rectNew.y + rectNew.height;
                    bChanged = true;
                }

                if( !bChanged || MapUI.this.m_bGroupMoveEnabled )
                {
                    Iterator iter = m_oRoomUIMap.values().iterator();
                    int nMaxX = MIN_MAP_WIDTH, nMaxY = MIN_MAP_HEIGHT;
                    while( iter.hasNext() )
                    {
                        RoomUI roomUI = (RoomUI)iter.next();
                        nMaxX = Math.max( nMaxX, roomUI.m_oRoom.m_rectBounds.x + roomUI.m_oRoom.m_rectBounds.width );
                        nMaxY = Math.max( nMaxY, roomUI.m_oRoom.m_rectBounds.y + roomUI.m_oRoom.m_rectBounds.height );
                    }

                    if( nMaxX != m_oMap.m_dmMap.width || nMaxY != m_oMap.m_dmMap.height )
                    {
                        m_oMap.m_dmMap.setSize( nMaxX, nMaxY );
                        bChanged = true;
                    }
                }

                if( bChanged )
                    revalidate();
            }

            if( m_bDraggingEdge )
            {
                rectPaint = new Rectangle( m_ptDragStart );
                rectPaint.add( m_ptDragEnd );
                m_bDraggingEdge = false;
                m_ptDragStart = m_ptDragEnd = null;  // do we need this??
                m_bOneWay = m_bSecret = false;
            }
            else if( m_bDraggingRoom )
                m_bDraggingRoom = false;
            else if( m_bSizingRoom )
                m_bSizingRoom = false;

            // If necessary repaint the specified region
            if( rectPaint != null )
                repaintRegion( rectPaint );
        }

        /**
         * @see  java.awt.MouseAdapter#mouseClicked( MouseEvent )  mouseClicked
         */

        public void mouseClicked( MouseEvent evt )
        {
            /////////////////////////////////////////////////////////////////
            // VARIABLE DECLARATIONS                                       //

            RoomUI oRoomUI;  // Reference to the new Room
            Point  ptMouse;  // Location of mouse click

            //                                                             //
            /////////////////////////////////////////////////////////////////

            // Determine if the mouse was double-clicked on the MapUI component
            if( evt.getComponent() == MapUI.this &&
                evt.getClickCount() == 2 &&
                SwingUtilities.isLeftMouseButton( evt ) )
            {
                // Determine if an edge is under the mouse
                if( edgeHitTest( ptMouse = evt.getPoint() ) == null )
                {
                    // Create a new room at the mouse location
                    if( (oRoomUI = createRoom()) != null )
                        oRoomUI.setLocation( ptMouse );
                }
                else
                    // Edit the edge under the mouse
                    editUI();
            }
        }

        /**
         * @see  java.awt.MouseAdapter#mouseMoved( MouseEvent )  mouseMoved
         */

        public void mouseMoved( MouseEvent evt )
        {
            // Change cursor when mouse is over an edge
            if( evt.getComponent() == MapUI.this )
                setCursor( Cursor.getPredefinedCursor(
                    edgeHitTest( evt.getPoint() ) != null ?
                    Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR ) );
        }

        /**
         * @see  java.awt.MouseAdapter#mouseDragged( MouseEvent )  mouseDragged
         */

        public void mouseDragged( MouseEvent evt )
        {
            // change booleans to states that are located within the mouse listener
            if( m_bDraggingRoom )
                roomDragged( evt );
            else if( m_bSizingRoom )
                roomSized( evt );
            else if( m_bDraggingEdge )
                edgeDragged( evt );
        }
    }
}
