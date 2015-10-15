/*
 * EdgeEditorDialog.java
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
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;

/**
 * The dialog box used to edit the properties of an Edge object.
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
    // *********************       Class Variables      *********************
    // **********************************************************************

    /**
     * Serializable class version number.
     */
    private static final long serialVersionUID = 1042757844845382097L;


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
        // -----------------       Class Variables      ---------------------
        // ------------------------------------------------------------------

        /**
         * Serializable class version number.
         */
        private static final long serialVersionUID = -3042679356676825043L;


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
            setVisible( false );
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
        private static final long serialVersionUID = 2227859200996997533L;


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
            setVisible( false );
        }
    }
}
