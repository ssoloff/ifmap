/*
 * IFMapView.java
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

import java.awt.BorderLayout;
import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * The object that provides a graphical user interface for the application.
 */

public class IFMapView
    extends JFrame
{
    // **********************************************************************
    // *********************     Instance Variables     *********************
    // **********************************************************************

    /**
     * Reference to the document attached to this view.
     */
    protected IFMapDoc m_oDocument;

    /**
     * The MapUI used to render the Map in the document.
     */
    protected MapUI m_oMapUI;


    // **********************************************************************
    // *********************      Class Variables       *********************
    // **********************************************************************

    /**
     * Serializable class version number.
     */
    private static final long serialVersionUID = -1205169782355746924L;

    /**
     * The class name of the current L&F.
     */
    protected static String c_strCurrentLookAndFeel;


    // **********************************************************************
    // *********************     Class Initializers     *********************
    // **********************************************************************

    static
    {
        try
        {
            // Set the look-and-feel for the application
            c_strCurrentLookAndFeel = IFMap.getProperty( IFMap.PROP_VIEW_LAF );
            UIManager.setLookAndFeel( c_strCurrentLookAndFeel );
        }
        catch( Exception e )
        {
            // Use the cross-platform look-and-feel
            c_strCurrentLookAndFeel = UIManager.getCrossPlatformLookAndFeelClassName();
        }
    }


    // **********************************************************************
    // *********************        Constructors        *********************
    // **********************************************************************

    /**
     * Constructs a new IFMapView object.
     *
     * @exception  IllegalArgumentException  If the specified document is
     *     null.
     */

    public IFMapView( IFMapDoc oDocument )
        throws IllegalArgumentException
    {
        // Make sure arguments are valid
        if( oDocument == null )
            throw new IllegalArgumentException();

        // Initialize member variables
        m_oDocument = oDocument;
        m_oDocument.setView( this );

        // Initialize layout and display view
        updateTitle();
        initLayout();
        setVisible( true );
    }


    // **********************************************************************
    // *********************  Public Instance Methods   *********************
    // **********************************************************************

    /**
     * Clears the contents of the view.
     */

    public void clear()
    {
        // Clear the MapUI
        m_oMapUI.clear();
        updateTitle();
    }

    /**
     * Loads the contents of the view from the document.
     */

    public void load()
    {
        // Clear the contents of the view
        clear();

        // Add each RoomUI to the view
        for( final Room room : m_oMapUI.m_oMap.m_oRoomMap.values() )
            m_oMapUI.addRoom( room );

        // Revalidate and repaint the view
        m_oMapUI.revalidate();
        m_oMapUI.repaint();
    }

    /**
     * Gets the name of the specified MRU file.
     *
     * @param  nMRUIndex  Index of the MRU file to be retrieved.  Must be
     *     between 0 and 3.
     *
     * @return  The name of the specified MRU file.
     *
     * @throws  IllegalArgumentException  If nMRUIndex is illegal.
     */

    public String getMRUFile( int nMRUIndex )
        throws IllegalArgumentException
    {
        // Make sure arguments are valid
        if( nMRUIndex < 0 || (nMRUIndex - MenuBar.MENUITEM_MRU_FIRST) > MenuBar.MENUITEM_MRU_LAST )
            throw new IllegalArgumentException();

        // Return the name of the specified MRU file
        JMenu oMenu = getJMenuBar().getMenu( MenuBar.MENU_FILE );
        JMenuItem oMenuItem = oMenu.getItem( MenuBar.MENUITEM_MRU_FIRST + nMRUIndex );
        return( oMenuItem.getText().substring( 2 ) );
    }


    // **********************************************************************
    // *********************  Private Instance Methods  *********************
    // **********************************************************************

    /**
     * Updates the title of the view.
     */

    private void updateTitle()
    {
        // Update the title with the current document name
        setTitle( m_oDocument.getName() + " - " + IFMap.getResource( "app.title" ) );
    }

    /**
     * Adds the specified file to the MRU list.
     *
     * @param  strFilePath  The absolute path of the file to be added to the
     *     MRU list.
     */

    private void addMRUFile( String strFilePath )
    {
        /////////////////////////////////////////////////////////////////////
        // VARIABLE DECLARATIONS                                           //

        JMenu     oMenu;      // Reference to a JMenu
        JMenuItem oMenuItem;  // Reference to a JMenuItem
        boolean   bAddToMRU;  // Indicates the specified file needs to be added
        int       nMRU,       // MRU index
                  nI;         // Loop control variable

        //                                                                 //
        /////////////////////////////////////////////////////////////////////

        // Get a reference to the file menu
        oMenu = getJMenuBar().getMenu( MenuBar.MENU_FILE );

        // Determine if the specified file is already present in the MRU list
        for( nI = MenuBar.MENUITEM_MRU_FIRST, bAddToMRU = true;
             nI <= MenuBar.MENUITEM_MRU_LAST;
             nI++ )
        {
            // Get a reference to the current menu item
            oMenuItem = oMenu.getItem( nI );

            // If the file already exists move it to the top of the MRU list and exit
            if( oMenuItem.getText().substring( 2 ).equals( strFilePath ) )
            {
                oMenu.remove( nI );
                oMenu.add( oMenuItem, MenuBar.MENUITEM_MRU_FIRST );
                bAddToMRU = false;
                break;
            }
        }

        // Add the new file to the end of the MRU list
        if( bAddToMRU )
        {
            oMenu.remove( MenuBar.MENUITEM_MRU_LAST );
            oMenuItem = new JMenuItem( "1 " + strFilePath );
            oMenuItem.addActionListener( new OpenMRUAction( strFilePath ) );
            oMenu.add( oMenuItem, MenuBar.MENUITEM_MRU_FIRST );
        }

        // Reset all MRU mnemonics
        for( nI = MenuBar.MENUITEM_MRU_FIRST; nI <= MenuBar.MENUITEM_MRU_LAST; nI++ )
        {
            nMRU = nI - MenuBar.MENUITEM_MRU_FIRST + 1;
            oMenuItem = oMenu.getItem( nI );
            oMenuItem.setText( Integer.toString( nMRU ) + " " + oMenuItem.getText().substring( 2 ) );
            oMenuItem.setMnemonic( '0' + nMRU );
        }
    }

    /**
     * Initializes the layout of the components within the frame.
     */

    private void initLayout()
    {
        // Initialize generic layout parameters
        setLocation( Integer.parseInt( IFMap.getProperty( IFMap.PROP_WINDOW_X ) ),
            Integer.parseInt( IFMap.getProperty( IFMap.PROP_WINDOW_Y ) ) );
        setSize( Integer.parseInt( IFMap.getProperty( IFMap.PROP_WINDOW_WIDTH ) ),
            Integer.parseInt( IFMap.getProperty( IFMap.PROP_WINDOW_HEIGHT ) ) );
        setDefaultCloseOperation( DISPOSE_ON_CLOSE );
        setIconImage( IFMap.createImageIcon( "icon.gif" ).getImage() );

        // Initialize layout
        getContentPane().setLayout( new BorderLayout() );
        m_oMapUI = new MapUI( m_oDocument.m_oMap );
        getContentPane().add( new JScrollPane( m_oMapUI ), BorderLayout.CENTER );

        // Create the menu
        setJMenuBar( new IFMapView.MenuBar() );
    }


    // **********************************************************************
    // *********************           Menus            *********************
    // **********************************************************************

    /**
     * The object that represents the menu bar for the application.
     */

    protected class MenuBar
        extends JMenuBar
    {
        // ------------------------------------------------------------------
        // -----------------           Constants            -----------------
        // ------------------------------------------------------------------

        /**
         * Index of the File menu on the menu bar.
         */
        public static final int MENU_FILE = 0;

        /**
         * Index of the first MRU menu item on the File menu.
         */
        public static final int MENUITEM_MRU_FIRST = 5;

        /**
         * Index of the last MRU menu item on the File menu.
         */
        public static final int MENUITEM_MRU_LAST = 8;


        // ------------------------------------------------------------------
        // -----------------       Class Variables      ---------------------
        // ------------------------------------------------------------------

        /**
         * Serializable class version number.
         */
        private static final long serialVersionUID = -8451319082147398944L;


        // ------------------------------------------------------------------
        // -----------------          Constructors          -----------------
        // ------------------------------------------------------------------

        /**
         * Constructs a new MenuBar object.
         */

        public MenuBar()
        {
            /////////////////////////////////////////////////////////////////
            // VARIABLE DECLARATIONS                                       //

            JMenu                       oMenu,          // A menu on the menu bar
                                        oLAFPopupMenu;  // Popup menu for the LAF
            JMenuItem                   oMenuItem;      // An item on the menu
            UIManager.LookAndFeelInfo[] aLAF;           // Array of available LAFs
            ButtonGroup                 oBtnGroup;      // Button group for LAF items
            int                         nI;             // Loop control variable

            //                                                             //
            /////////////////////////////////////////////////////////////////

            // Create the File menu
            oMenu = new JMenu( IFMap.getResource( "menu.file.label" ) );
            oMenu.setMnemonic( IFMap.getResource( "menu.file.shortcut" ).charAt( 0 ) );
            oMenuItem = new JMenuItem( IFMap.getResource( "menu.file.new.label" ) );
            oMenuItem.setMnemonic( IFMap.getResource( "menu.file.new.shortcut" ).charAt( 0 ) );
            oMenuItem.setAccelerator( KeyStroke.getKeyStroke(
                (int)IFMap.getResource( "menu.file.new.accel" ).charAt( 0 ),
                Event.CTRL_MASK ) );
            oMenuItem.addActionListener( new NewFileAction() );
            oMenu.add( oMenuItem );
            oMenuItem = new JMenuItem( IFMap.getResource( "menu.file.open.label" ) );
            oMenuItem.setMnemonic( IFMap.getResource( "menu.file.open.shortcut" ).charAt( 0 ) );
            oMenuItem.setAccelerator( KeyStroke.getKeyStroke(
                (int)IFMap.getResource( "menu.file.open.accel" ).charAt( 0 ),
                Event.CTRL_MASK ) );
            oMenuItem.addActionListener( new OpenFileAction() );
            oMenu.add( oMenuItem );
            oMenuItem = new JMenuItem( IFMap.getResource( "menu.file.save.label" ) );
            oMenuItem.setMnemonic( IFMap.getResource( "menu.file.save.shortcut" ).charAt( 0 ) );
            oMenuItem.setAccelerator( KeyStroke.getKeyStroke(
                (int)IFMap.getResource( "menu.file.save.accel" ).charAt( 0 ),
                Event.CTRL_MASK ) );
            oMenuItem.addActionListener( new SaveFileAction( false ) );
            oMenu.add( oMenuItem );
            oMenuItem = new JMenuItem( IFMap.getResource( "menu.file.saveAs.label" ) );
            oMenuItem.setMnemonic( IFMap.getResource( "menu.file.saveAs.shortcut" ).charAt( 0 ) );
            oMenuItem.addActionListener( new SaveFileAction( true ) );
            oMenu.add( oMenuItem );
            oMenu.addSeparator();
            oMenuItem = new JMenuItem( "1 " + IFMap.getProperty( IFMap.PROP_FILE_MRU1 ) );
            oMenuItem.setMnemonic( '1' );
            oMenuItem.addActionListener( new OpenMRUAction( IFMap.getProperty( IFMap.PROP_FILE_MRU1 ) ) );
            oMenu.add( oMenuItem );
            oMenuItem = new JMenuItem( "2 " + IFMap.getProperty( IFMap.PROP_FILE_MRU2 ) );
            oMenuItem.setMnemonic( '2' );
            oMenuItem.addActionListener( new OpenMRUAction( IFMap.getProperty( IFMap.PROP_FILE_MRU2 ) ) );
            oMenu.add( oMenuItem );
            oMenuItem = new JMenuItem( "3 " + IFMap.getProperty( IFMap.PROP_FILE_MRU3 ) );
            oMenuItem.setMnemonic( '3' );
            oMenuItem.addActionListener( new OpenMRUAction( IFMap.getProperty( IFMap.PROP_FILE_MRU3 ) ) );
            oMenu.add( oMenuItem );
            oMenuItem = new JMenuItem( "4 " + IFMap.getProperty( IFMap.PROP_FILE_MRU4 ) );
            oMenuItem.setMnemonic( '4' );
            oMenuItem.addActionListener( new OpenMRUAction( IFMap.getProperty( IFMap.PROP_FILE_MRU4 ) ) );
            oMenu.add( oMenuItem );
            oMenu.addSeparator();
            oMenuItem = new JMenuItem( IFMap.getResource( "menu.file.exit.label" ) );
            oMenuItem.setMnemonic( IFMap.getResource( "menu.file.exit.shortcut" ).charAt( 0 ) );
            oMenuItem.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_F4, Event.ALT_MASK ) );
            oMenuItem.addActionListener( new ExitAction() );
            oMenu.add( oMenuItem );
            add( oMenu );

            // Create the Edit menu
            oMenu = new JMenu( IFMap.getResource( "menu.edit.label" ) );
            oMenu.setMnemonic( IFMap.getResource( "menu.edit.shortcut" ).charAt( 0 ) );
            oMenu.addItemListener( new EditMenuItemListener() );
            oMenuItem = new JMenuItem( IFMap.getResource( "menu.edit.insertRoom.label" ) );
            oMenuItem.setMnemonic( IFMap.getResource( "menu.edit.insertRoom.shortcut" ).charAt( 0 ) );
            oMenuItem.setAccelerator( KeyStroke.getKeyStroke(
                (int)IFMap.getResource( "menu.edit.insertRoom.accel" ).charAt( 0 ),
                Event.CTRL_MASK ) );
            oMenuItem.addActionListener( new InsertRoomAction() );
            oMenu.add( oMenuItem );
            oMenu.addSeparator();
            oMenuItem = new JMenuItem( IFMap.getResource( "menu.edit.editObject.label" ) );
            oMenuItem.setMnemonic( IFMap.getResource( "menu.edit.editObject.shortcut" ).charAt( 0 ) );
            oMenuItem.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_ENTER, ActionEvent.ALT_MASK ) );
            oMenuItem.addActionListener( new EditObjectAction() );
            oMenu.add( oMenuItem );
            oMenuItem = new JMenuItem( IFMap.getResource( "menu.edit.deleteObject.label" ) );
            oMenuItem.setMnemonic( IFMap.getResource( "menu.edit.deleteObject.shortcut" ).charAt( 0 ) );
            oMenuItem.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_DELETE, 0 ) );
            oMenuItem.addActionListener( new DeleteObjectAction() );
            oMenu.add( oMenuItem );
            add( oMenu );

            // Create the View->LookAndFeel popup menu
            aLAF = UIManager.getInstalledLookAndFeels();
            oBtnGroup = new ButtonGroup();
            oLAFPopupMenu = new JMenu( IFMap.getResource( "menu.view.laf.label" ) );
            oLAFPopupMenu.setMnemonic( IFMap.getResource( "menu.view.laf.shortcut" ).charAt( 0 ) );
            for( nI = 0; nI < aLAF.length; nI++ )
            {
                // Create a menu item for the current LAF
                oMenuItem = new JRadioButtonMenuItem( aLAF[ nI ].getName() );
                oMenuItem.addActionListener( new ChangeLookAndFeelAction( aLAF[ nI ].getClassName() ) );
                oBtnGroup.add( oMenuItem );
                oLAFPopupMenu.add( oMenuItem );

                // Select the current LAF
                if( aLAF[ nI ].getClassName().equals( c_strCurrentLookAndFeel ) )
                    oMenuItem.setSelected( true );
            }

            // Create the View menu
            oMenu = new JMenu( IFMap.getResource( "menu.view.label" ) );
            oMenu.setMnemonic( IFMap.getResource( "menu.view.shortcut" ).charAt( 0 ) );
            oMenu.add( oLAFPopupMenu );
            oMenu.addSeparator();
            oMenuItem = new JCheckBoxMenuItem( IFMap.getResource( "menu.view.grid.label" ), m_oMapUI.m_bGridEnabled );
            oMenuItem.setMnemonic( IFMap.getResource( "menu.view.grid.shortcut" ).charAt( 0 ) );
            oMenuItem.setAccelerator( KeyStroke.getKeyStroke(
                (int)IFMap.getResource( "menu.view.grid.accel" ).charAt( 0 ),
                Event.CTRL_MASK | Event.ALT_MASK ) );
            oMenuItem.addActionListener( new ToggleGridAction() );
            oMenu.add( oMenuItem );
            oMenuItem = new JCheckBoxMenuItem( IFMap.getResource( "menu.view.groupMove.label" ), m_oMapUI.m_bGroupMoveEnabled );
            oMenuItem.setMnemonic( IFMap.getResource( "menu.view.groupMove.shortcut" ).charAt( 0 ) );
            oMenuItem.setAccelerator( KeyStroke.getKeyStroke(
                (int)IFMap.getResource( "menu.view.groupMove.accel" ).charAt( 0 ),
                Event.CTRL_MASK | Event.ALT_MASK ) );
            oMenuItem.addActionListener( new ToggleGroupMoveAction() );
            oMenu.add( oMenuItem );
            add( oMenu );

            // Create the Help menu
            oMenu = new JMenu( IFMap.getResource( "menu.help.label" ) );
            oMenu.setMnemonic( IFMap.getResource( "menu.help.shortcut" ).charAt( 0 ) );
            oMenuItem = new JMenuItem( IFMap.getResource( "menu.help.about.label" ) );
            oMenuItem.setMnemonic( IFMap.getResource( "menu.help.about.shortcut" ).charAt( 0 ) );
            oMenuItem.addActionListener( new AboutAction() );
            oMenu.add( oMenuItem );
            add( oMenu );
        }
    }


    // **********************************************************************
    // *********************         Listeners          *********************
    // **********************************************************************

    /**
     * The class that updates the state of items on the Edit menu.
     */

    protected class EditMenuItemListener
        extends Object
        implements ItemListener
    {
        // ------------------------------------------------------------------
        // -----------------    ItemListener Methods    ---------------------
        // ------------------------------------------------------------------

        /**
         * @see  java.awt.event.ItemListener#itemStateChanged  itemStateChanged
         */

        public void itemStateChanged( ItemEvent evt )
        {
            /////////////////////////////////////////////////////////////////
            // VARIABLE DECLARATIONS                                       //

            JMenu   oMenu;    // The Menu that fired the event
            boolean bEnable;  // Indicates if menu items should be enabled

            //                                                             //
            /////////////////////////////////////////////////////////////////

            // Determine if the menu items in question should be enabled
            bEnable = evt.getStateChange() == ItemEvent.SELECTED ?
                m_oMapUI.isActiveComponentAvailable() : true;

            // Set the enabled status of the Edit/Delete Object menu items
            oMenu = (JMenu)evt.getItem();
            oMenu.getItem( 2 ).setEnabled( bEnable );
            oMenu.getItem( 3 ).setEnabled( bEnable );
        }
    }


    // **********************************************************************
    // *********************          Actions           *********************
    // **********************************************************************

    /**
     * The class that implements the action to create a new file.
     */

    protected class NewFileAction
        extends AbstractAction
    {
        // ------------------------------------------------------------------
        // -----------------       Class Variables      ---------------------
        // ------------------------------------------------------------------

        /**
         * Serializable class version number.
         */
        private static final long serialVersionUID = 6537290547587426046L;


        // ------------------------------------------------------------------
        // -----------------        Constructors        ---------------------
        // ------------------------------------------------------------------

        /**
         * Constructs a new NewFileAction.
         */

        NewFileAction()
        {
            // Call base class implementation
            super( "NewFile" );
        }


        // ------------------------------------------------------------------
        // -----------------   ActionListener Methods   ---------------------
        // ------------------------------------------------------------------

        /**
         * @see  java.awt.event.ActionListener#actionPerformed  actionPerformed
         */

        public void actionPerformed( ActionEvent evt )
        {
            // Clear the document and the view
            m_oDocument.clear();
            IFMapView.this.clear();
        }
    }

    /**
     * The class that implements the action to open an existing file.
     */

    protected class OpenFileAction
        extends AbstractAction
    {
        // ------------------------------------------------------------------
        // -----------------       Class Variables      ---------------------
        // ------------------------------------------------------------------

        /**
         * Serializable class version number.
         */
        private static final long serialVersionUID = -4794561084789098425L;


        // ------------------------------------------------------------------
        // -----------------        Constructors        ---------------------
        // ------------------------------------------------------------------

        /**
         * Constructs a new OpenFileAction.
         */

        OpenFileAction()
        {
            // Call base class implementation
            super( "OpenFile" );
        }


        // ------------------------------------------------------------------
        // -----------------   ActionListener Methods   ---------------------
        // ------------------------------------------------------------------

        /**
         * @see  java.awt.event.ActionListener#actionPerformed  actionPerformed
         */

        public void actionPerformed( ActionEvent evt )
        {
            // Create and initialize the file chooser
            JFileChooser oChooser = new JFileChooser();
            oChooser.setAcceptAllFileFilterUsed( false );
            oChooser.setFileFilter( new IFMMLFileFilter() );

            // Prompt the user for the name of the document
            if( oChooser.showOpenDialog( IFMapView.this ) == JFileChooser.APPROVE_OPTION )
            {
                // Load the selected file into the document and refresh the view
                String strFilePath = oChooser.getSelectedFile().getAbsolutePath();
                m_oDocument.load( strFilePath );
                IFMapView.this.load();
                IFMapView.this.addMRUFile( strFilePath );
            }
        }
    }

    /**
     * The class that implements the action to save the document using its
     * current name.
     */

    protected class SaveFileAction
        extends AbstractAction
    {
        // ------------------------------------------------------------------
        // -----------------     Instance Variables     ---------------------
        // ------------------------------------------------------------------

        /**
         * Indicates the user should always be prompted for the filename to
         * save to.
         */
        boolean m_bAlwaysPromptForFileName;


        // ------------------------------------------------------------------
        // -----------------       Class Variables      ---------------------
        // ------------------------------------------------------------------

        /**
         * Serializable class version number.
         */
        private static final long serialVersionUID = -2982735627320127159L;


        // ------------------------------------------------------------------
        // -----------------        Constructors        ---------------------
        // ------------------------------------------------------------------

        /**
         * Constructs a new SaveFileAction.
         *
         * @param  bAlwaysPromptForFileName  Indicates the user should be
         *     prompted for the filename to save to.
         */

        SaveFileAction( boolean bAlwaysPromptForFileName )
        {
            // Call base class implementation
            super( "SaveFile" );

            // Initialize instance variables
            m_bAlwaysPromptForFileName = bAlwaysPromptForFileName;
        }


        // ------------------------------------------------------------------
        // -----------------   ActionListener Methods   ---------------------
        // ------------------------------------------------------------------

        /**
         * @see  java.awt.event.ActionListener#actionPerformed  actionPerformed
         */

        public void actionPerformed( ActionEvent evt )
        {
            /////////////////////////////////////////////////////////////////
            // VARIABLE DECLARATIONS                                       //

            JFileChooser oChooser;     // Dialog used to select filename
            String       strFileName;  // New filename of the document

            //                                                             //
            /////////////////////////////////////////////////////////////////

            // Set the filename for the document
            if( m_bAlwaysPromptForFileName || m_oDocument.getFileName() == null )
            {
                // Create and initialize the file chooser
                oChooser = new JFileChooser( m_oDocument.getFileName() );
                oChooser.setAcceptAllFileFilterUsed( false );
                oChooser.setFileFilter( new IFMMLFileFilter() );

                // Prompt the user for the name of the document
                if( oChooser.showSaveDialog( IFMapView.this ) == JFileChooser.APPROVE_OPTION )
                    strFileName = oChooser.getSelectedFile().getAbsolutePath();
                else
                    return;
            }
            else
                // Use the existing name of the document
                strFileName = m_oDocument.getFileName();

            // Save the document
            m_oDocument.save( strFileName );
            updateTitle();
        }
    }

    /**
     * The class that implements the action to open a file from the MRU list.
     */

    protected class OpenMRUAction
        extends AbstractAction
    {
        // ------------------------------------------------------------------
        // -----------------     Instance Variables     ---------------------
        // ------------------------------------------------------------------

        /**
         * The absolute path name of the MRU file associated with this action.
         */
        String m_strFilePath;


        // ------------------------------------------------------------------
        // -----------------       Class Variables      ---------------------
        // ------------------------------------------------------------------

        /**
         * Serializable class version number.
         */
        private static final long serialVersionUID = -8161525765615980095L;


        // ------------------------------------------------------------------
        // -----------------        Constructors        ---------------------
        // ------------------------------------------------------------------

        /**
         * Constructs a new OpenMRUAction.
         *
         * @param  strFilePath  The absolute path name of the MRU file
         *     associated with this action.
         */

        OpenMRUAction( String strFilePath )
        {
            // Call base class implementation
            super( "OpenMRU" );

            // Initialize instance variables
            m_strFilePath = strFilePath;
        }


        // ------------------------------------------------------------------
        // -----------------   ActionListener Methods   ---------------------
        // ------------------------------------------------------------------

        /**
         * @see  java.awt.event.ActionListener#actionPerformed  actionPerformed
         */

        public void actionPerformed( ActionEvent evt )
        {
            // Load the selected file into the document and refresh the view
            m_oDocument.load( m_strFilePath );
            IFMapView.this.load();
            IFMapView.this.addMRUFile( m_strFilePath );
        }
    }

    /**
     * The class that implements the action to exit the application.
     */

    protected class ExitAction
        extends AbstractAction
    {
        // ------------------------------------------------------------------
        // -----------------       Class Variables      ---------------------
        // ------------------------------------------------------------------

        /**
         * Serializable class version number.
         */
        private static final long serialVersionUID = 6848421657908990513L;


        // ------------------------------------------------------------------
        // -----------------        Constructors        ---------------------
        // ------------------------------------------------------------------

        /**
         * Constructs a new ExitAction.
         */

        ExitAction()
        {
            // Call base class implementation
            super( "Exit" );
        }


        // ------------------------------------------------------------------
        // -----------------   ActionListener Methods   ---------------------
        // ------------------------------------------------------------------

        /**
         * @see  java.awt.event.ActionListener#actionPerformed  actionPerformed
         */

        public void actionPerformed( ActionEvent evt )
        {
            // Dispose of the view
            dispose();
        }
    }

    /**
     * The class that implements the action to insert a room onto the map.
     */

    protected class InsertRoomAction
        extends AbstractAction
    {
        // ------------------------------------------------------------------
        // -----------------       Class Variables      ---------------------
        // ------------------------------------------------------------------

        /**
         * Serializable class version number.
         */
        private static final long serialVersionUID = -6684298387243293948L;


        // ------------------------------------------------------------------
        // -----------------        Constructors        ---------------------
        // ------------------------------------------------------------------

        /**
         * Constructs a new InsertRoomAction.
         */

        InsertRoomAction()
        {
            // Call base class implementation
            super( "InsertRoom" );
        }


        // ------------------------------------------------------------------
        // -----------------   ActionListener Methods   ---------------------
        // ------------------------------------------------------------------

        /**
         * @see  java.awt.event.ActionListener#actionPerformed  actionPerformed
         */

        public void actionPerformed( ActionEvent evt )
        {
            // Insert a new Room
            m_oMapUI.createRoom();
        }
    }

    /**
     * The class that implements the action to edit an object on the map.
     */

    protected class EditObjectAction
        extends AbstractAction
    {
        // ------------------------------------------------------------------
        // -----------------       Class Variables      ---------------------
        // ------------------------------------------------------------------

        /**
         * Serializable class version number.
         */
        private static final long serialVersionUID = -6917886564447854051L;


        // ------------------------------------------------------------------
        // -----------------        Constructors        ---------------------
        // ------------------------------------------------------------------

        /**
         * Constructs a new EditObjectAction.
         */

        EditObjectAction()
        {
            // Call base class implementation
            super( "EditObject" );
        }


        // ------------------------------------------------------------------
        // -----------------   ActionListener Methods   ---------------------
        // ------------------------------------------------------------------

        /**
         * @see  java.awt.event.ActionListener#actionPerformed  actionPerformed
         */

        public void actionPerformed( ActionEvent evt )
        {
            // Edit the active component in the MapUI
            m_oMapUI.editActiveComponent();
        }
    }

    /**
     * The class that implements the action to delete an object on the map.
     */

    protected class DeleteObjectAction
        extends AbstractAction
    {
        // ------------------------------------------------------------------
        // -----------------       Class Variables      ---------------------
        // ------------------------------------------------------------------

        /**
         * Serializable class version number.
         */
        private static final long serialVersionUID = 18100730483104851L;


        // ------------------------------------------------------------------
        // -----------------        Constructors        ---------------------
        // ------------------------------------------------------------------

        /**
         * Constructs a new DeleteObjectAction.
         */

        DeleteObjectAction()
        {
            // Call base class implementation
            super( "DeleteObject" );
        }


        // ------------------------------------------------------------------
        // -----------------   ActionListener Methods   ---------------------
        // ------------------------------------------------------------------

        /**
         * @see  java.awt.event.ActionListener#actionPerformed  actionPerformed
         */

        public void actionPerformed( ActionEvent evt )
        {
            // Delete the active component in the MapUI
            m_oMapUI.deleteActiveComponent();
        }
    }

    /**
     * The class that implements the action to change the Look & Feel of the
     * application.
     */

    protected class ChangeLookAndFeelAction
        extends AbstractAction
    {
        // ------------------------------------------------------------------
        // -----------------     Instance Variables     ---------------------
        // ------------------------------------------------------------------

        /**
         * The class name of the L&F associated with this action.
         */
        String m_strLookAndFeel;


        // ------------------------------------------------------------------
        // -----------------       Class Variables      ---------------------
        // ------------------------------------------------------------------

        /**
         * Serializable class version number.
         */
        private static final long serialVersionUID = -436550997055437481L;


        // ------------------------------------------------------------------
        // -----------------        Constructors        ---------------------
        // ------------------------------------------------------------------

        /**
         * Constructs a new ChangeLookAndFeelAction.
         *
         * @param  strLookAndFeel  The class name of the L&F associated with
         *     this action.
         */

        ChangeLookAndFeelAction( String strLookAndFeel )
        {
            // Call base class implementation
            super( "ChangeLookAndFeel" );

            // Initialize instance variables
            m_strLookAndFeel = strLookAndFeel;
        }


        // ------------------------------------------------------------------
        // -----------------   ActionListener Methods   ---------------------
        // ------------------------------------------------------------------

        /**
         * @see  java.awt.event.ActionListener#actionPerformed  actionPerformed
         */

        public void actionPerformed( ActionEvent evt )
        {
            try
            {
                // Set the new look and feel
                if( !c_strCurrentLookAndFeel.equals( m_strLookAndFeel ) )
                {
                    UIManager.setLookAndFeel( m_strLookAndFeel );
                    SwingUtilities.updateComponentTreeUI( IFMapView.this );
                    c_strCurrentLookAndFeel = m_strLookAndFeel;
                }
            }
            catch( Exception e )
            {
                System.out.println( "Failed to load L&F: " + m_strLookAndFeel );
                System.out.println( e );
            }
        }
    }

    /**
     * The class that implements the action to toggle the grid in the view.
     */

    protected class ToggleGridAction
        extends AbstractAction
    {
        // ------------------------------------------------------------------
        // -----------------       Class Variables      ---------------------
        // ------------------------------------------------------------------

        /**
         * Serializable class version number.
         */
        private static final long serialVersionUID = 768345438838330031L;


        // ------------------------------------------------------------------
        // -----------------        Constructors        ---------------------
        // ------------------------------------------------------------------

        /**
         * Constructs a new ToggleGridAction.
         */

        ToggleGridAction()
        {
            // Call base class implementation
            super( "ToggleGrid" );
        }


        // ------------------------------------------------------------------
        // -----------------   ActionListener Methods   ---------------------
        // ------------------------------------------------------------------

        /**
         * @see  java.awt.event.ActionListener#actionPerformed  actionPerformed
         */

        public void actionPerformed( ActionEvent evt )
        {
            // Toggle the grid setting
            m_oMapUI.enableGrid( !m_oMapUI.isGridEnabled() );
        }
    }

    /**
     * The class that implements the action to toggle group movement in the
     * view.
     */

    protected class ToggleGroupMoveAction
        extends AbstractAction
    {
        // ------------------------------------------------------------------
        // -----------------       Class Variables      ---------------------
        // ------------------------------------------------------------------

        /**
         * Serializable class version number.
         */
        private static final long serialVersionUID = 2999019747374571510L;


        // ------------------------------------------------------------------
        // -----------------        Constructors        ---------------------
        // ------------------------------------------------------------------

        /**
         * Constructs a new ToggleGroupMoveAction.
         */

        ToggleGroupMoveAction()
        {
            // Call base class implementation
            super( "ToggleGroupMove" );
        }


        // ------------------------------------------------------------------
        // -----------------   ActionListener Methods   ---------------------
        // ------------------------------------------------------------------

        /**
         * @see  java.awt.event.ActionListener#actionPerformed  actionPerformed
         */

        public void actionPerformed( ActionEvent evt )
        {
            // Toggle the group movement setting
            m_oMapUI.enableGroupMove( !m_oMapUI.isGroupMoveEnabled() );
        }
    }

    /**
     * The class that implements the action to view the About dialog.
     */

    protected class AboutAction
        extends AbstractAction
    {
        // ------------------------------------------------------------------
        // -----------------       Class Variables      ---------------------
        // ------------------------------------------------------------------

        /**
         * Serializable class version number.
         */
        private static final long serialVersionUID = -1967979928779718104L;


        // ------------------------------------------------------------------
        // -----------------        Constructors        ---------------------
        // ------------------------------------------------------------------

        /**
         * Constructs a new AboutAction.
         */

        AboutAction()
        {
            // Call base class implementation
            super( "About" );
        }


        // ------------------------------------------------------------------
        // -----------------   ActionListener Methods   ---------------------
        // ------------------------------------------------------------------

        /**
         * @see  java.awt.event.ActionListener#actionPerformed  actionPerformed
         */

        public void actionPerformed( ActionEvent evt )
        {
            // Display the About dialog
            JOptionPane.showMessageDialog( IFMapView.this,
                IFMap.getResource( "aboutDialog.message" ),
                IFMap.getResource( "aboutDialog.title" ), JOptionPane.OK_OPTION,
                IFMap.createImageIcon( "icon.gif" ) );
        }
    }
}
