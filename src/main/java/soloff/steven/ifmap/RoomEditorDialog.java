/*
 * RoomEditorDialog.java
 *
 * Copyright (c) 2000-2001 by Steven M. Soloff.
 * All rights reserved.
 *
 */

package soloff.steven.ifmap;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * The dialog box used to edit the properties of a Room object.
 *
 * @author   Steven M. Soloff
 * @version  1.1.0 (02/06/01)
 */

public class RoomEditorDialog
    extends JDialog
{
    // **********************************************************************
    // *********************     Instance Variables     *********************
    // **********************************************************************

    /**
     * The Room object being edited.
     */
    protected Room m_oRoom;

    /**
     * Text field for the room name.
     */
    protected JTextField m_oNameField;

    /**
     * Text field for the room description.
     */
    protected JTextArea m_oDescriptionField;

    /**
     * Panel to display the foreground color of the room.
     */
    protected JPanel m_oForeColorPanel;

    /**
     * Panel to display the background color of the room.
     */
    protected JPanel m_oBackColorPanel;


    // **********************************************************************
    // *********************       Class Variables      *********************
    // **********************************************************************

    /**
     * Serializable class version number.
     */
    private static final long serialVersionUID = -4146992201575466176L;


    // **********************************************************************
    // *********************        Constructors        *********************
    // **********************************************************************

    /**
     * Constructs a new RoomEditorDialog.
     *
     * @param  oRoom  The Room object to edit.
     *
     * @exception  IllegalArgumentException  If oRoom is null.
     */

    public RoomEditorDialog( Frame oOwner, Room oRoom )
        throws IllegalArgumentException
    {
        // Call base class implementation
        super( oOwner, IFMap.getResource( "roomEditorDialog.title" ), true );

        // Make sure arguments are valid
        if( oRoom == null )
            throw new IllegalArgumentException();

        // Initialize instance variables
        m_oRoom = oRoom;

        // Initialize layout
        initLayout();
    }


    // **********************************************************************
    // *********************  Private Instance Methods  *********************
    // **********************************************************************

    /**
     * Initializes the layout of the components within the dialog.
     */

    private void initLayout()
    {
        /////////////////////////////////////////////////////////////////////
        // VARIABLE DECLARATIONS                                           //

        Container   oContentPane;            // Dialog content pane
        JPanel      oNorthPanel,             // North panel
                    oSouthPanel,             // South panel
                    oColorPanel,             // Panel for room colors
                    oForeColorPanel,         // Panel for foreground color
                    oForeColorCtrlPanel,     // Panel for foreground color controls
                    oBackColorPanel,         // Panel for background color
                    oBackColorCtrlPanel;     // Panel for background color controls
        JScrollPane oScrollPane;             // Scroll pane for description field
        JButton     oSelectForeColorButton,  // Foreground color selection button
                    oSelectBackColorButton,  // Background color selection button
                    oOKButton,               // OK button
                    oCancelButton;           // Cancel button
        JLabel      oNameLabel,              // Label for the name field
                    oDescriptionLabel;       // Label for the description field

        //                                                                 //
        /////////////////////////////////////////////////////////////////////

        // Create name controls
        m_oNameField = new JTextField( m_oRoom.getName() );
        m_oNameField.setAlignmentX( 0.0F );
        oNameLabel = new JLabel( IFMap.getResource( "roomEditorDialog.name.label" ) );
        oNameLabel.setDisplayedMnemonic( IFMap.getResource( "roomEditorDialog.name.shortcut" ).charAt( 0 ) );
        oNameLabel.setLabelFor( m_oNameField );

        // Create description controls
        m_oDescriptionField = new JTextArea( m_oRoom.getDescription(), 5, 25 );
        m_oDescriptionField.setLineWrap( true );
        m_oDescriptionField.setWrapStyleWord( true );
        oScrollPane = new JScrollPane( m_oDescriptionField );
        oScrollPane.setAlignmentX( 0.0F );
        oDescriptionLabel = new JLabel( IFMap.getResource( "roomEditorDialog.description.label" ) );
        oDescriptionLabel.setDisplayedMnemonic( IFMap.getResource( "roomEditorDialog.description.shortcut" ).charAt( 0 ) );
        oDescriptionLabel.setLabelFor( m_oDescriptionField );

        // Create foreground color controls
        m_oForeColorPanel = new JPanel();
        m_oForeColorPanel.setBorder( BorderFactory.createLineBorder( Color.black ) );
        m_oForeColorPanel.setBackground( m_oRoom.getForeground() );
        oSelectForeColorButton = new JButton( IFMap.getResource( "roomEditorDialog.foreColorButton.label" ) );
        oSelectForeColorButton.setMnemonic( IFMap.getResource( "roomEditorDialog.foreColorButton.shortcut" ).charAt( 0 ) );
        oSelectForeColorButton.addActionListener( new SelectColorAction( true ) );
        oForeColorCtrlPanel = new JPanel();
        oForeColorCtrlPanel.setLayout( new BoxLayout( oForeColorCtrlPanel, BoxLayout.X_AXIS ) );
        oForeColorCtrlPanel.setAlignmentX( 0.0F );
        oForeColorCtrlPanel.add( m_oForeColorPanel );
        oForeColorCtrlPanel.add( Box.createRigidArea( new Dimension( 5, 0 ) ) );
        oForeColorCtrlPanel.add( oSelectForeColorButton );
        oForeColorPanel = new JPanel();
        oForeColorPanel.setLayout( new BoxLayout( oForeColorPanel, BoxLayout.Y_AXIS ) );
        oForeColorPanel.add( new JLabel( IFMap.getResource( "roomEditorDialog.foreColor.label" ) ) );
        oForeColorPanel.add( Box.createRigidArea( new Dimension( 0, 3 ) ) );
        oForeColorPanel.add( oForeColorCtrlPanel );

        // Create background color controls
        m_oBackColorPanel = new JPanel();
        m_oBackColorPanel.setBorder( BorderFactory.createLineBorder( Color.black ) );
        m_oBackColorPanel.setBackground( m_oRoom.getBackground() );
        oSelectBackColorButton = new JButton( IFMap.getResource( "roomEditorDialog.backColorButton.label" ) );
        oSelectBackColorButton.setMnemonic( IFMap.getResource( "roomEditorDialog.backColorButton.shortcut" ).charAt( 0 ) );
        oSelectBackColorButton.addActionListener( new SelectColorAction( false ) );
        oBackColorCtrlPanel = new JPanel();
        oBackColorCtrlPanel.setLayout( new BoxLayout( oBackColorCtrlPanel, BoxLayout.X_AXIS ) );
        oBackColorCtrlPanel.setAlignmentX( 0.0F );
        oBackColorCtrlPanel.add( m_oBackColorPanel );
        oBackColorCtrlPanel.add( Box.createRigidArea( new Dimension( 5, 0 ) ) );
        oBackColorCtrlPanel.add( oSelectBackColorButton );
        oBackColorPanel = new JPanel();
        oBackColorPanel.setLayout( new BoxLayout( oBackColorPanel, BoxLayout.Y_AXIS ) );
        oBackColorPanel.add( new JLabel( IFMap.getResource( "roomEditorDialog.backColor.label" ) ) );
        oBackColorPanel.add( Box.createRigidArea( new Dimension( 0, 3 ) ) );
        oBackColorPanel.add( oBackColorCtrlPanel );

        // Create the color controls
        oColorPanel = new JPanel();
        oColorPanel.setLayout( new BoxLayout( oColorPanel, BoxLayout.X_AXIS ) );
        oColorPanel.setAlignmentX( 0.0F );
        oColorPanel.add( oForeColorPanel );
        oColorPanel.add( Box.createRigidArea( new Dimension( 11, 0 ) ) );
        oColorPanel.add( oBackColorPanel );

        // Initialize the north panel
        oNorthPanel = new JPanel();
        oNorthPanel.setLayout( new BoxLayout( oNorthPanel, BoxLayout.Y_AXIS ) );
        oNorthPanel.setBorder( BorderFactory.createEmptyBorder( 12, 12, 0, 11 ) );
        oNorthPanel.add( oNameLabel );
        oNorthPanel.add( Box.createRigidArea( new Dimension( 0, 3 ) ) );
        oNorthPanel.add( m_oNameField );
        oNorthPanel.add( Box.createRigidArea( new Dimension( 0, 7 ) ) );
        oNorthPanel.add( oDescriptionLabel );
        oNorthPanel.add( Box.createRigidArea( new Dimension( 0, 3 ) ) );
        oNorthPanel.add( oScrollPane );
        oNorthPanel.add( Box.createRigidArea( new Dimension( 0, 7 ) ) );
        oNorthPanel.add( oColorPanel );

        // Create the OK button
        oOKButton = new JButton( IFMap.getResource( "roomEditorDialog.ok.label" ) );
        oOKButton.addActionListener( new OKAction() );

        // Create the Cancel button
        oCancelButton = new JButton( IFMap.getResource( "roomEditorDialog.cancel.label" ) );
        oCancelButton.addActionListener( new CancelAction() );

        // Initialize the south panel
        oSouthPanel = new JPanel();
        oSouthPanel.setLayout( new BoxLayout( oSouthPanel, BoxLayout.X_AXIS ) );
        oSouthPanel.setBorder( BorderFactory.createEmptyBorder( 17, 12, 11, 11 ) );
        oSouthPanel.add( Box.createHorizontalGlue() );
        oSouthPanel.add( oOKButton );
        oSouthPanel.add( Box.createRigidArea( new Dimension( 5, 0 ) ) );
        oSouthPanel.add( oCancelButton );

        // Initialize the content pane
        oContentPane = getContentPane();
        oContentPane.setLayout( new BorderLayout() );
        oContentPane.add( oNorthPanel, BorderLayout.NORTH );
        oContentPane.add( oSouthPanel, BorderLayout.SOUTH );
        getRootPane().setDefaultButton( oOKButton );
        pack();

        // Initialize dialog properties
        soloff.steven.awt.Utilities.centerWindowInOwner( this );
        setResizable( false );
    }


    // **********************************************************************
    // *********************          Actions           *********************
    // **********************************************************************

    /**
     * The class that implements the action to choose either the foreground
     * or background color for the Room.
     */

    protected class SelectColorAction
        extends AbstractAction
    {
        // ------------------------------------------------------------------
        // -----------------     Instance Variables     ---------------------
        // ------------------------------------------------------------------

        /**
         * Indicates that the foreground color of the room is to be selected,
         * otherwise the background color is to be selected.
         */
        protected boolean m_bSelectFore;


        // ------------------------------------------------------------------
        // -----------------       Class Variables      ---------------------
        // ------------------------------------------------------------------

        /**
         * Serializable class version number.
         */
        private static final long serialVersionUID = 7702394062156593563L;


        // ------------------------------------------------------------------
        // -----------------        Constructors        ---------------------
        // ------------------------------------------------------------------

        /**
         * Constructs a new SelectColorAction.
         */

        SelectColorAction( boolean bSelectFore )
        {
            // Call base class implementation
            super( "SelectColor" );

            // Initialize instance variables
            m_bSelectFore = bSelectFore;
        }


        // ------------------------------------------------------------------
        // -----------------   ActionListener Methods   ---------------------
        // ------------------------------------------------------------------

        /**
         * @see  javax.swing.Action#actionPerformed( ActionEvent )
         *     actionPerformed
         */

        public void actionPerformed( ActionEvent evt )
        {
            // Display the color chooser dialog
            Color clr = JColorChooser.showDialog( RoomEditorDialog.this,
                IFMap.getResource( m_bSelectFore ? "foreColorChooser.title" : "backColorChooser.title" ),
                m_bSelectFore ? m_oForeColorPanel.getBackground() : m_oBackColorPanel.getBackground() );

            // Set the new color
            if( clr != null )
            {
                if( m_bSelectFore )
                    m_oForeColorPanel.setBackground( clr );
                else
                    m_oBackColorPanel.setBackground( clr );
            }
        }
    }

    /**
     * The class that implements the action to close the dialog box and save
     * the results.
     */

    protected class OKAction
        extends AbstractAction
    {
        // ------------------------------------------------------------------
        // -----------------       Class Variables      ---------------------
        // ------------------------------------------------------------------

        /**
         * Serializable class version number.
         */
        private static final long serialVersionUID = -5911507547580586614L;


        // ------------------------------------------------------------------
        // -----------------        Constructors        ---------------------
        // ------------------------------------------------------------------

        /**
         * Constructs a new OKAction.
         */

        OKAction()
        {
            // Call base class implementation
            super( "OK" );
        }


        // ------------------------------------------------------------------
        // -----------------   ActionListener Methods   ---------------------
        // ------------------------------------------------------------------

        /**
         * @see  javax.swing.Action#actionPerformed( ActionEvent )
         *     actionPerformed
         */

        public void actionPerformed( ActionEvent evt )
        {
            // The room name cannot be empty
            if( m_oNameField.getText().length() > 0 )
            {
                // Close the dialog and save changes
                m_oRoom.setName( m_oNameField.getText() );
                m_oRoom.setDescription( m_oDescriptionField.getText() );
                m_oRoom.setForeground( m_oForeColorPanel.getBackground() );
                m_oRoom.setBackground( m_oBackColorPanel.getBackground() );
                hide();
            }
            else
                // Display warning message
                JOptionPane.showMessageDialog( RoomEditorDialog.this,
                    IFMap.getResource( "roomEditorDialog.warning.emptyName" ),
                    IFMap.getResource( "app.title" ),
                    JOptionPane.WARNING_MESSAGE );
        }
    }

    /**
     * The class that implements the action to close the dialog box and
     * disregard the results.
     */

    protected class CancelAction
        extends AbstractAction
    {
        // ------------------------------------------------------------------
        // -----------------       Class Variables      ---------------------
        // ------------------------------------------------------------------

        /**
         * Serializable class version number.
         */
        private static final long serialVersionUID = -7943901985215621212L;


        // ------------------------------------------------------------------
        // -----------------        Constructors        ---------------------
        // ------------------------------------------------------------------

        /**
         * Constructs a new OKAction.
         */

        CancelAction()
        {
            // Call base class implementation
            super( "Cancel" );
        }


        // ------------------------------------------------------------------
        // -----------------   ActionListener Methods   ---------------------
        // ------------------------------------------------------------------

        /**
         * @see  javax.swing.Action#actionPerformed( ActionEvent )
         *     actionPerformed
         */

        public void actionPerformed( ActionEvent evt )
        {
            // Close the dialog without saving changes
            hide();
        }
    }
}
