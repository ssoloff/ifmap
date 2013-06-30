/*
 * IFMMLizable.java
 *
 * Copyright (c) 2000-2001 by Steven M. Soloff.
 * All rights reserved.
 *
 */

package soloff.steven.ifmap;

import java.io.BufferedWriter;
import java.io.IOException;
import org.w3c.dom.Element;

/**
 * The interface that is supported by objects that support reading/writing
 * from/to an IFMML (IF Map Markup Language) file.
 *
 * @author   Steven M. Soloff
 * @version  1.1.0 (02/06/01)
 */

public interface IFMMLizable
{
    // **********************************************************************
    // *********************         Constants          *********************
    // **********************************************************************

    /**
     * The public identifier of the IFMML DTD.
     */
    public static final String IFMML_DTD_PUBLIC_ID = "-//SOLOFF STEVEN//IFMML 1.0";

    /**
     * The system identifier of the IFMML DTD.
     */
    public static final String IFMML_DTD_SYSTEM_ID = "http://steven.soloff/ifmml/1.0/ifmml.dtd";

    /**
     * Name of the MAP element.
     */
    public static final String IFMML_ELEM_MAP = "MAP";

    /**
     * Name of the ROOM element.
     */
    public static final String IFMML_ELEM_ROOM = "ROOM";

    /**
     * Name of the ID attribute of the ROOM element.
     */
    public static final String IFMML_ATTR_ROOM_ID = "ID";

    /**
     * Name of the NAME attribute of the ROOM element.
     */
    public static final String IFMML_ATTR_ROOM_NAME = "NAME";

    /**
     * Name of the DESCRIPTION attribute of the ROOM element.
     */
    public static final String IFMML_ATTR_ROOM_DESCRIPTION = "DESCRIPTION";

    /**
     * Name of the X attribute of the ROOM element.
     */
    public static final String IFMML_ATTR_ROOM_X = "X";

    /**
     * Name of the Y attribute of the ROOM element.
     */
    public static final String IFMML_ATTR_ROOM_Y = "Y";

    /**
     * Name of the WIDTH attribute of the ROOM element.
     */
    public static final String IFMML_ATTR_ROOM_WIDTH = "WIDTH";

    /**
     * Name of the HEIGHT attribute of the ROOM element.
     */
    public static final String IFMML_ATTR_ROOM_HEIGHT = "HEIGHT";

    /**
     * Name of the FORECOLOR attribute of the ROOM element.
     */
    public static final String IFMML_ATTR_ROOM_FORECOLOR = "FORECOLOR";

    /**
     * Name of the BACKCOLOR attribute of the ROOM element.
     */
    public static final String IFMML_ATTR_ROOM_BACKCOLOR = "BACKCOLOR";

    /**
     * Name of the EDGE element.
     */
    public static final String IFMML_ELEM_EDGE = "EDGE";

    /**
     * Name of the ID attribute of the EDGE element.
     */
    public static final String IFMML_ATTR_EDGE_ID = "ID";

    /**
     * Name of the ONEWAY attribute of the EDGE element.
     */
    public static final String IFMML_ATTR_EDGE_ONEWAY = "ONEWAY";

    /**
     * Name of the SECRET attribute of the EDGE element.
     */
    public static final String IFMML_ATTR_EDGE_SECRET = "SECRET";

    /**
     * Name of the STARTROOMID attribute of the EDGE element.
     */
    public static final String IFMML_ATTR_EDGE_STARTROOMID = "STARTROOMID";

    /**
     * Name of the STARTROOMEXIT attribute of the EDGE element.
     */
    public static final String IFMML_ATTR_EDGE_STARTROOMEXIT = "STARTROOMEXIT";

    /**
     * Name of the ENDROOMID attribute of the EDGE element.
     */
    public static final String IFMML_ATTR_EDGE_ENDROOMID = "ENDROOMID";

    /**
     * Name of the ENDROOMEXIT attribute of the EDGE element.
     */
    public static final String IFMML_ATTR_EDGE_ENDROOMEXIT = "ENDROOMEXIT";


    // **********************************************************************
    // *********************  Public Interface Methods  *********************
    // **********************************************************************

    /**
     * Reads an element and its children from the specified IFMML file.
     *
     * @param  oElement  The root element from where the object should begin
     *     reading the IFMML file.
     *
     * @throws  RuntimeException  If any type of parsing/validation error
     *     occurs.
     */

    public abstract void readIFMML( Element oElement )
        throws RuntimeException;

    /**
     * Writes an element and its children to the specified IFMML file.
     *
     * @param  oWriter  The BufferedWriter used to write to the IFMML file.
     *
     * @throws  IOException  If an I/O error occurs.
     */

    public abstract void writeIFMML( BufferedWriter oWriter )
        throws IOException;
}
