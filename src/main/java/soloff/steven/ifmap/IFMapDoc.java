/*
 * IFMapDoc.java
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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * The object that serves as the document for the application.
 */

public class IFMapDoc
    extends Object
{
    // **********************************************************************
    // *********************     Instance Variables     *********************
    // **********************************************************************

    /**
     * Reference to the view attached to this document.
     */
    protected IFMapView m_oView;

    /**
     * The Map managed by this document.
     */
    protected Map m_oMap;

    /**
     * The File in which this document persists.
     */
    protected File m_oFile;

    /**
     * The DocumentBuilder used to parse and XML/IFMML documents.
     */
    protected DocumentBuilder m_oDocBuilder;


    // **********************************************************************
    // *********************        Constructors        *********************
    // **********************************************************************

    /**
     * Constructs a new IFMapDoc object.
     */

    public IFMapDoc()
    {
        // Initialize instance variables
        m_oMap = new Map();

        // Create and initialize a new DocumentBuilderFactory instance
        DocumentBuilderFactory oDocBuilderFactory = DocumentBuilderFactory.newInstance();
        oDocBuilderFactory.setValidating( true );
        oDocBuilderFactory.setIgnoringComments( true );
        oDocBuilderFactory.setIgnoringElementContentWhitespace( true );
        oDocBuilderFactory.setCoalescing( false );
        oDocBuilderFactory.setExpandEntityReferences( true );

        try
        {
            // Create and initialize a new DocumentBuilder instance
            m_oDocBuilder = oDocBuilderFactory.newDocumentBuilder();
            m_oDocBuilder.setEntityResolver( new IFMapDoc.SAXEntityResolver() );
            m_oDocBuilder.setErrorHandler( new IFMapDoc.SAXErrorHandler() );
        }
        catch( ParserConfigurationException e )
        {
            System.err.println( e.getMessage() );
            System.exit( 1 );
        }
    }


    // **********************************************************************
    // *********************  Public Instance Methods   *********************
    // **********************************************************************

    /**
     * Gets the name by which this document is known to the view.  The
     * returned string is typically displayed in the view's titlebar.
     *
     * @return  The name of the document.
     */

    public String getName()
    {
        // Return the name of the document
        if( m_oFile != null )
            return( m_oFile.getName() );
        else
            return( IFMap.getResource( "doc.defaultName" ) );
    }

    /**
     * Gets the name of the file that is currently loaded in the document.
     *
     * @return  The name of the file currently loaded in the document; null
     *     if no file is loaded.
     */

    public String getFileName()
    {
        // Return the name of the file loaded in the document
        if( m_oFile != null )
            return( m_oFile.getAbsolutePath() );
        else
            return( null );
    }

    /**
     * Clears the contents of the document.
     */

    public void clear()
    {
        // Clear the Map and the File
        m_oMap.clear();
        m_oFile = null;
    }

    /**
     * Loads the contents of the document from the specified file.
     *
     * @param  strFileName  Name of the file from which the document's
     *     contents will be loaded.
     *
     * @exception  IllegalArgumentException  If strFileName is null.
     */

    public void load( String strFileName )
        throws IllegalArgumentException
    {
        /////////////////////////////////////////////////////////////////////
        // VARIABLE DECLARATIONS                                           //

        Document oDoc;  // DOM for the IFMML document

        //                                                                 //
        /////////////////////////////////////////////////////////////////////

        // Make sure arguments are valid
        if( strFileName == null )
            throw new IllegalArgumentException();

        try
        {
            // Clear the document
            clear();

            // Parse the document and read it
            m_oFile = new File( strFileName );
            oDoc = m_oDocBuilder.parse( m_oFile );
            m_oMap.readIFMML( oDoc.getDocumentElement() );

            // Check integrity of Map
            // (NOTE: I think this can be removed since the DTD validates
            // everything we are checking here.)
            if( !m_oMap.checkIntegrity() )
                throw new Exception( IFMap.getResource( "msg.badMapIntegrity" ) );
        }
        catch( Exception e )
        {
            // Display error message and clear the document
            clear();
            JOptionPane.showMessageDialog( m_oView, e.getMessage(),
                IFMap.getResource( "app.title" ),
                JOptionPane.ERROR_MESSAGE );
        }
    }

    /**
     * Saves the contents of the document to the specified file.
     *
     * @param  strFileName  Name of the file to which the document's
     *     contents will be saved.
     *
     * @exception  IllegalArgumentException  If strFileName is null.
     */

    public void save( String strFileName )
        throws IllegalArgumentException
    {
        /////////////////////////////////////////////////////////////////////
        // VARIABLE DECLARATIONS                                           //

        FileWriter     oFileWriter;      // FileWriter for specified file
        BufferedWriter oBufferedWriter;  // BufferedWriter for specified file
        String         str;              // Buffer used for writing

        //                                                                 //
        /////////////////////////////////////////////////////////////////////

        // Make sure arguments are valid
        if( strFileName == null )
            throw new IllegalArgumentException();

        try
        {
            // Open the specified file
            m_oFile = new File( strFileName );
            oFileWriter = new FileWriter( m_oFile );
            oBufferedWriter = new BufferedWriter( oFileWriter );

            // Write XML directive and document type declaration
            str = "<?xml version=\"1.0\" standalone=\"no\"?>";
            oBufferedWriter.write( str, 0, str.length() );
            oBufferedWriter.newLine();
            str = "<!DOCTYPE " + IFMMLizable.IFMML_ELEM_MAP + " PUBLIC \"" +
                IFMMLizable.IFMML_DTD_PUBLIC_ID + "\" \"" +
                IFMMLizable.IFMML_DTD_SYSTEM_ID + "\">";
            oBufferedWriter.write( str, 0, str.length() );
            oBufferedWriter.newLine();

            // Write IFMML for the Map
            m_oMap.writeIFMML( oBufferedWriter );

            // Close file
            oBufferedWriter.flush();
            oFileWriter.close();
        }
        catch( IOException eIO )
        {
            // Display error message
            JOptionPane.showMessageDialog( m_oView, eIO.getMessage(),
                IFMap.getResource( "app.title" ),
                JOptionPane.ERROR_MESSAGE );
        }
    }


    // **********************************************************************
    // *********************  Package Instance Methods  *********************
    // **********************************************************************

    /**
     * Sets the view attached to this document.  This method is called by the
     * view when it is constructed.
     *
     * @param  oView  Reference to the view attached to this document.
     *
     * @exception  IllegalArgumentException  If the specified view is null.
     */

    void setView( IFMapView oView )
        throws IllegalArgumentException
    {
        // Make sure arguments are valid
        if( oView == null )
            throw new IllegalArgumentException();

        // Store reference to the specified view
        m_oView = oView;
    }


    // **********************************************************************
    // *********************          Handlers          *********************
    // **********************************************************************

    /**
     * The object that is responsible for resolving entities encountered by
     * the XML parser.
     */
    protected static class SAXEntityResolver
        extends Object
        implements EntityResolver
    {
        // ------------------------------------------------------------------
        // -----------------    EntityHandler Methods    --------------------
        // ------------------------------------------------------------------

        /**
         * @see  org.xml.sax.EntityHandler#resolveEntity( String, String )  resolveEntity
         */

        public InputSource resolveEntity( String publicId, String systemId )
            throws SAXException, IOException
        {
            if( IFMMLizable.IFMML_DTD_PUBLIC_ID.equals( publicId ) )
            {
                return new InputSource( getClass().getClassLoader().getResourceAsStream( "soloff/steven/ifmap/resources/IFMML.dtd" ) );
            }

            return null;
        }
    }

    /**
     * The object that is responsible for handling SAX errors fired by the
     * XML parser.
     */

    protected static class SAXErrorHandler
        extends Object
        implements ErrorHandler
    {
        // ------------------------------------------------------------------
        // -----------------  Private Instance Methods  ---------------------
        // ------------------------------------------------------------------

        /**
         * Returns a string describing parse exception details.
         *
         * @param  e  SAXParseException whose information is to be extracted.
         *
         * @return  A string describing the parse exception details.
         */

        private String getParseExceptionInfo( SAXParseException e )
        {
            /////////////////////////////////////////////////////////////////
            // VARIABLE DECLARATIONS                                       //

            String strSystemID,  // System ID that caused the exception
                   strInfo;      // Info extracted from the exception

            //                                                             //
            /////////////////////////////////////////////////////////////////

            // Format the exception information details
            if( (strSystemID = e.getSystemId()) == null )
                strSystemID = "null";
            strInfo = "URI=" + strSystemID + " Line=" + e.getLineNumber() +
                ": " + e.getMessage();
            return( strInfo );
        }


        // ------------------------------------------------------------------
        // -----------------    ErrorHandler Methods    ---------------------
        // ------------------------------------------------------------------

        /**
         * @see  org.xml.sax.ErrorHandler#error( SAXParseException )  error
         */

        public void error( SAXParseException e )
            throws SAXException
        {
            // Throw new exception
            String strMessage = "Error: " + getParseExceptionInfo( e );
            throw new SAXException( strMessage );
        }

        /**
         * @see  org.xml.sax.ErrorHandler#fatalError( SAXParseException )
         *     fatalError
         */

        public void fatalError( SAXParseException e )
            throws SAXException
        {
            // Throw new exception
            String strMessage = "Fatal Error: " + getParseExceptionInfo( e );
            throw new SAXException( strMessage );
        }

        /**
         * @see  org.xml.sax.ErrorHandler#warning( SAXParseException )  warning
         */

        public void warning( SAXParseException e )
            throws SAXException
        {
            // Display warning message but silently ignore
            System.err.println( "Warning: " + getParseExceptionInfo( e ) );
        }
    }
}
