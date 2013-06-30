/*
 * IFMap.java
 *
 * Copyright (c) 2000-2001 by Steven M. Soloff.
 * All rights reserved.
 *
 */

package soloff.steven.ifmap;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import javax.swing.ImageIcon;
import javax.swing.UIManager;

/**
 * The object that serves as the application controller.
 *
 * @author   Steven M. Soloff
 * @version  1.1.0 (02/06/01)
 */

public class IFMap
    extends Object
{
    // **********************************************************************
    // *********************         Constants          *********************
    // **********************************************************************

    /**
     * Key for the property that specifies the x-coordinate of the main window.
     */
    public static final String PROP_WINDOW_X = "window.x";

    /**
     * Key for the property that specifies the y-coordinate of the main window.
     */
    public static final String PROP_WINDOW_Y = "window.y";

    /**
     * Key for the property that specifies the width of the main window.
     */
    public static final String PROP_WINDOW_WIDTH = "window.width";

    /**
     * Key for the property that specifies the height of the main window.
     */
    public static final String PROP_WINDOW_HEIGHT = "window.height";

    /**
     * Key for the property that specifies the first file in the MRU list.
     */
    public static final String PROP_FILE_MRU1 = "file.mru1";

    /**
     * Key for the property that specifies the second file in the MRU list.
     */
    public static final String PROP_FILE_MRU2 = "file.mru2";

    /**
     * Key for the property that specifies the third file in the MRU list.
     */
    public static final String PROP_FILE_MRU3 = "file.mru3";

    /**
     * Key for the property that specifies the fourth file in the MRU list.
     */
    public static final String PROP_FILE_MRU4 = "file.mru4";

    /**
     * Key for the property that specifies the look and feel of the view.
     */
    public static final String PROP_VIEW_LAF = "view.laf";

    /**
     * Key for the property that specifies if the grid is enabled in the
     * view.
     */
    public static final String PROP_VIEW_GRID = "view.grid";

    /**
     * Key for the property that specifies if group movement is enabled in
     * the view.
     */
    public static final String PROP_VIEW_GROUPMOVE = "view.groupMove";


    // **********************************************************************
    // *********************     Instance Variables     *********************
    // **********************************************************************

    /**
     * The document that stores the data of the application.
     */
    protected IFMapDoc m_oDocument;

    /**
     * The view that renders the data of the application.
     */
    protected IFMapView m_oView;


    // **********************************************************************
    // *********************      Class Variables       *********************
    // **********************************************************************

    /**
     * The application-wide resource bundle used to load resources.  This
     * instance is shared by all objects in the application.
     */
    protected static ResourceBundle c_oResourceBundle;

    /**
     * Stores all application properties that must remain persistant from
     * session to session.
     */
    protected static Properties c_oProperties;


    // **********************************************************************
    // *********************     Class Initializers     *********************
    // **********************************************************************

    static
    {
        try
        {
            // Get the resource bundle for the application
            c_oResourceBundle = ResourceBundle.getBundle( "soloff/steven/ifmap/resources/IFMap" );
        }
        catch( MissingResourceException eMR )
        {
            // Exit if resources not found
            System.err.println( eMR.getMessage() );
            System.exit( 1 );
        }

        // Initialize the default application properties
        Properties oDefaultProps = new Properties();
        String strRecentFile = getResource( "app.recentFile" );
        oDefaultProps.setProperty( PROP_FILE_MRU1, strRecentFile + " 1" );
        oDefaultProps.setProperty( PROP_FILE_MRU2, strRecentFile + " 2" );
        oDefaultProps.setProperty( PROP_FILE_MRU3, strRecentFile + " 3" );
        oDefaultProps.setProperty( PROP_FILE_MRU4, strRecentFile + " 4" );
        oDefaultProps.setProperty( PROP_WINDOW_X, "100" );
        oDefaultProps.setProperty( PROP_WINDOW_Y, "100" );
        oDefaultProps.setProperty( PROP_WINDOW_WIDTH, Integer.toString( MapUI.MIN_MAP_WIDTH + 30 ) );
        oDefaultProps.setProperty( PROP_WINDOW_HEIGHT, Integer.toString( MapUI.MIN_MAP_HEIGHT + 60 ) );
        oDefaultProps.setProperty( PROP_VIEW_LAF, UIManager.getCrossPlatformLookAndFeelClassName() );
        oDefaultProps.setProperty( PROP_VIEW_GRID, String.valueOf( false ) );
        oDefaultProps.setProperty( PROP_VIEW_GROUPMOVE, String.valueOf( false ) );

        // Create the application properties and load from disk
        c_oProperties = new Properties( oDefaultProps );

        try
        {
            c_oProperties.load( new FileInputStream( "IFMap.settings" ) );
        }
        catch( IOException eIO )
        {
            // Silently ignore missing settings file
        }
    }


    // **********************************************************************
    // *********************        Constructors        *********************
    // **********************************************************************

    /**
     * Constructs a new IFMap object.
     */

    public IFMap()
    {
        // Initialize instance variables
        m_oDocument = new IFMapDoc();
        m_oView = new IFMapView( m_oDocument );

        // Listen for window events fired by the view
        m_oView.addWindowListener( new WindowListener() );
    }


    // **********************************************************************
    // *********************    Public Class Methods    *********************
    // **********************************************************************

    /**
     * Entry point of the application.
     *
     * @param  args  Array of command-line arguments passed to the application.
     */

    public static void main( String[] args )
    {
        // Create a new IFMap
        IFMap theApp = new IFMap();
    }

    /**
     * Gets the value of the specified application resource.
     *
     * @param  strKey  Key for the application resource requested.
     *
     * @return  The value of the specified application resource or null
     *     if the key was not found.
     */

    public static String getResource( String strKey )
    {
        try
        {
            // Get the specified resource string
            return( c_oResourceBundle.getString( strKey ) );
        }
        catch( MissingResourceException eMR )
        {
            // Display error message and return
            System.err.println( eMR.getMessage() );
            return( null );
        }
    }

    /**
     * Gets the value of the specified application property.
     *
     * @param  strKey  Key for the application property requested.
     *
     * @return  The value of the specified application property or
     *     null if the key was not found.
     */

    public static String getProperty( String strKey )
    {
        // Return the value of the specified application property
        return( c_oProperties.getProperty( strKey ) );
    }

    /**
     * Sets the value of the specified application property.
     *
     * @param  strKey  Key for the application property to be set.
     * @param  strValue  Value of the application property to be set.
     */

    public static void setProperty( String strKey, String strValue )
    {
        // Set the value of the specified application property
        c_oProperties.setProperty( strKey, strValue );
    }

    /**
     * Creates an ImageIcon from an image contained within the "resources"
     * directory.
     *
     * @param  strFilename  Name of the image file used to create the
     *     ImageIcon.
     *
     * @return  An ImageIcon created from the specified image.
     */

    public static ImageIcon createImageIcon( String strFilename )
    {
        try
        {
            // Create the ImageIcon from the specified image
            String strPath = "/soloff/steven/ifmap/resources/" + strFilename;
            return( new ImageIcon( Class.forName( "soloff.steven.ifmap.IFMap" ).getResource( strPath ) ) );
        }
        catch( ClassNotFoundException eCNF )
        {
            System.err.println( eCNF.getMessage() );
            System.exit( 1 );
            return( null );
        }
    }


    // **********************************************************************
    // *********************         Listeners          *********************
    // **********************************************************************

    /**
     * The object that is responsible for listening for window events fired
     * by the embedded IFMapView component.
     */

    protected class WindowListener
        extends WindowAdapter
    {
        // ------------------------------------------------------------------
        // -----------------  WindowAdapter Overrides   ---------------------
        // ------------------------------------------------------------------

        /**
         * @see  java.awt.WindowAdapter#windowClosed( WindowEvent )
         *     windowClosed
         */

        public void windowClosed( WindowEvent evt )
        {
            /////////////////////////////////////////////////////////////////
            // VARIABLE DECLARATIONS                                       //

            Point     ptView;  // Current location of the view
            Dimension dmView;  // Current size of the view

            //                                                             //
            /////////////////////////////////////////////////////////////////

            // Store the location and size of the view
            ptView = m_oView.getLocation();
            dmView = m_oView.getSize();

            // Update user settings
            IFMap.setProperty( PROP_FILE_MRU1, m_oView.getMRUFile( 0 ) );
            IFMap.setProperty( PROP_FILE_MRU2, m_oView.getMRUFile( 1 ) );
            IFMap.setProperty( PROP_FILE_MRU3, m_oView.getMRUFile( 2 ) );
            IFMap.setProperty( PROP_FILE_MRU4, m_oView.getMRUFile( 3 ) );
            IFMap.setProperty( PROP_WINDOW_X, Integer.toString( ptView.x ) );
            IFMap.setProperty( PROP_WINDOW_Y, Integer.toString( ptView.y ) );
            IFMap.setProperty( PROP_WINDOW_WIDTH, Integer.toString( dmView.width ) );
            IFMap.setProperty( PROP_WINDOW_HEIGHT, Integer.toString( dmView.height ) );
            IFMap.setProperty( PROP_VIEW_LAF, IFMapView.c_strCurrentLookAndFeel );
            IFMap.setProperty( PROP_VIEW_GRID, String.valueOf( m_oView.m_oMapUI.m_bGridEnabled ) );
            IFMap.setProperty( PROP_VIEW_GROUPMOVE, String.valueOf( m_oView.m_oMapUI.m_bGroupMoveEnabled ) );

            try
            {
                // Store updated user settings
                c_oProperties.store( new FileOutputStream( "IFMap.settings" ),
                    getResource( "app.propsComment" ) );
            }
            catch( IOException eIO )
            {
                // Display error message
                System.err.println( eIO.getMessage() );
            }

            // Exit the application
            System.exit( 0 );
        }
    }
}
