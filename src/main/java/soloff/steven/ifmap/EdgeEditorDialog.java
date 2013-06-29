/*
 * EdgeEditorDialog.java
 *
 * Copyright (c) 2000-2001 by Steven M. Soloff.
 * All rights reserved.
 *
 */

package soloff.steven.ifmap;

import java.util.ResourceBundle;
import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;

/**
 * The dialog box used to edit the properties of an Edge object.
 *
 * @author   Steven M. Soloff
 * @version  1.1.0 (02/06/01)
 */

public final class EdgeEditorDialog
    extends JDialog
{
    // **********************************************************************
    // *********************     Instance Variables     *********************
    // **********************************************************************

    /**
     * The Edge object being edited.
     */
    protected Edge m_oEdge;

    /**
     * Checkbox for the one-way passage flag.
     */
    protected JCheckBox m_oOneWayCheck;

    /**
     * Checkbox for the secret passage flag.
     */
    protected JCheckBox m_oSecretCheck;


    // **********************************************************************
    // *********************        Constructors        *********************
    // **********************************************************************

    /**
     * Constructs a new EdgeEditorDialog.
     *
     * @param  oEdge  The Edge object to edit.
     *
     * @exception  IllegalArgumentException  If oEdge is null.
     */

    public EdgeEditorDialog( Frame oOwner, Edge oEdge )
        throws IllegalArgumentException
    {
        // Call base class implementation
        super( oOwner, IFMap.getResource( "edgeEditorDialog.title" ), true );

        // Make sure arguments are valid
        if( oEdge == null )
            throw new IllegalArgumentException();

        // Initialize instance variables
        m_oEdge = oEdge;

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

        Container oContentPane;   // Dialog content pane
        JPanel    oNorthPanel,    // North panel
                  oSouthPanel;    // South panel
        JButton   oOKButton,      // OK button
                  oCancelButton;  // Cancel button

        //                                                                 //
        /////////////////////////////////////////////////////////////////////

        // Create the one-way and secret checkboxes
        m_oOneWayCheck = new JCheckBox( IFMap.getResource( "edgeEditorDialog.oneway.label" ) );
        m_oOneWayCheck.setMnemonic( IFMap.getResource( "edgeEditorDialog.oneway.shortcut" ).charAt( 0 ) );
        m_oOneWayCheck.setSelected( m_oEdge.isOneWay() );
        m_oSecretCheck = new JCheckBox( IFMap.getResource( "edgeEditorDialog.secret.label" ) );
        m_oSecretCheck.setMnemonic( IFMap.getResource( "edgeEditorDialog.secret.shortcut" ).charAt( 0 ) );
        m_oSecretCheck.setSelected( m_oEdge.isSecret() );

        // Initialize the north panel
        oNorthPanel = new JPanel();
        oNorthPanel.setLayout( new BoxLayout( oNorthPanel, BoxLayout.Y_AXIS ) );
        oNorthPanel.setBorder( BorderFactory.createEmptyBorder( 12, 12, 0, 11 ) );
        oNorthPanel.add( m_oOneWayCheck );
        oNorthPanel.add( m_oSecretCheck );

        // Create the OK button
        oOKButton = new JButton( IFMap.getResource( "edgeEditorDialog.ok.label" ) );
        oOKButton.addActionListener( new OKAction() );

        // Create the Cancel button
        oCancelButton = new JButton( IFMap.getResource( "edgeEditorDialog.cancel.label" ) );
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
     * The class that implements the action to close the dialog box and save
     * the results.
     */

    protected class OKAction
        extends AbstractAction
    {
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
            // Close the dialog and save changes
            m_oEdge.setOneWay( m_oOneWayCheck.isSelected() );
            m_oEdge.setSecret( m_oSecretCheck.isSelected() );
            hide();
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
