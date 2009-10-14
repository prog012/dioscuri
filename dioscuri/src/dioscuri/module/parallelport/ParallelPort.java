/*
 * $Revision: 159 $ $Date: 2009-08-17 12:52:56 +0000 (ma, 17 aug 2009) $ $Author: blohman $
 * 
 * Copyright (C) 2007  National Library of the Netherlands, Nationaal Archief of the Netherlands
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 * For more information about this project, visit
 * http://dioscuri.sourceforge.net/
 * or contact us via email:
 * jrvanderhoeven at users.sourceforge.net
 * blohman at users.sourceforge.net
 * 
 * Developed by:
 * Nationaal Archief               <www.nationaalarchief.nl>
 * Koninklijke Bibliotheek         <www.kb.nl>
 * Tessella Support Services plc   <www.tessella.com>
 *
 * Project Title: DIOSCURI
 *
 */

package dioscuri.module.parallelport;
/*
 * Information used in this module was taken from:
 * - http://mudlist.eorbit.net/~adam/pickey/ports.html
 */

import java.util.logging.Level;
import java.util.logging.Logger;

import dioscuri.Emulator;
import dioscuri.exception.ModuleException;
import dioscuri.exception.ModuleUnknownPort;
import dioscuri.exception.ModuleWriteOnlyPortException;
import dioscuri.module.Module;
import dioscuri.module.ModuleMotherboard;
import dioscuri.module.ModuleParallelPort;


/**
 * An implementation of a parallel port module.
 *  
 * @see Module
 * 
 * Metadata module
 * ********************************************
 * general.type                : parallelport
 * general.name                : General Parallel Port
 * general.architecture        : Von Neumann
 * general.description         : Models a 25-pin parallel port 
 * general.creator             : Tessella Support Services, Koninklijke Bibliotheek, Nationaal Archief of the Netherlands
 * general.version             : 1.0
 * general.keywords            : Parallel port, port, IEEE 1284, Centronics, LPT, printer
 * general.relations           : Motherboard
 * general.yearOfIntroduction  : 
 * general.yearOfEnding        : 
 * general.ancestor            : 
 * general.successor           : 
 * 
 * 
 */


// TODO: Class is (mostly) a stub to return requested values to the BIOS
public class ParallelPort extends ModuleParallelPort
{

    // Attributes

    // Relations
    private Emulator emu;
    private String[] moduleConnections = new String[] {"motherboard"}; 
    private ModuleMotherboard motherboard;

    // Toggles
    private boolean isObserved;
    private boolean debugMode;
    
    // Logging
    private static Logger logger = Logger.getLogger("dioscuri.module.parallelport");
    
    // Constants
    // FIXME: Separate ports into different parallel devices
    private final static int DATA_PORT = 0x378;      // Read/Write port (?)
    private final static int STATUS_PORT = 0x379;    // Read/Write port
    private final static int CONTROL_PORT = 0x37A;   // Read/Write port

    private final static int DATA_PORT2 = 0x278;      // Read/Write port (?)
    private final static int STATUS_PORT2 = 0x279;    // Read/Write port
    private final static int CONTROL_PORT2 = 0x27A;   // Read/Write port
    
    // Module specifics
    public final static int MODULE_ID       = 1;
    public final static String MODULE_TYPE  = "parallelport";
    public final static String MODULE_NAME  = "25-pin IEEE 1284 parallel port";
    
    
    // Constructor

    /**
     * Class constructor
     * 
     */
    public ParallelPort(Emulator owner)
    {
        emu = owner;
        
        // Initialise variables
        isObserved = false;
        debugMode = false;

        logger.log(Level.INFO, "[" + MODULE_TYPE + "] " + MODULE_NAME + " -> Module created successfully.");
    }

    //******************************************************************************
    // Module Methods
    
    /**
     * Returns the ID of the module
     * 
     * @return string containing the ID of module 
     * @see Module
     */
    public int getID()
    {
        return MODULE_ID;
    }

    
    /**
     * Returns the type of the module
     * 
     * @return string containing the type of module 
     * @see Module
     */
    public String getType()
    {
        return MODULE_TYPE;
    }


    /**
     * Returns the name of the module
     * 
     * @return string containing the name of module 
     * @see Module
     */
    public String getName()
    {
        return MODULE_NAME;
    }

    
    /**
     * Returns a String[] with all names of modules it needs to be connected to
     * 
     * @return String[] containing the names of modules, or null if no connections
     */
    public String[] getConnection()
    {
        // Return all required connections;
        return moduleConnections;
    }

    
    /**
     * Sets up a connection with another module
     * 
     * @param mod   Module that is to be connected to this class
     * 
     * @return true if connection has been established successfully, false otherwise
     * 
     * @see Module
     */
    public boolean setConnection(Module mod)
    {
        // Set connection for motherboard
        if (mod.getType().equalsIgnoreCase("motherboard"))
        {
            this.motherboard = (ModuleMotherboard)mod;
            return true;
        }
        return false;
    }


    /**
     * Checks if this module is connected to operate normally
     * 
     * @return true if this module is connected successfully, false otherwise
     */
    public boolean isConnected()
    {
        // Check if module if connected
        if (motherboard != null)
        {
            return true;
        }
        return false;
    }


    /**
     * Reset all parameters of module
     * 
     * @return boolean true if module has been reset successfully, false otherwise
     */
    public boolean reset()
    {
        // Register I/O ports 0x37[8-A], 0x27[8-A] in I/O address space
        motherboard.setIOPort(DATA_PORT, this);
        motherboard.setIOPort(STATUS_PORT, this);
        motherboard.setIOPort(CONTROL_PORT, this);

        motherboard.setIOPort(DATA_PORT2, this);
        motherboard.setIOPort(STATUS_PORT2, this);
        motherboard.setIOPort(CONTROL_PORT2, this);

        logger.log(Level.INFO, "[" + MODULE_TYPE + "] Module has been reset.");

        return true;

    }

    
    /**
     * Starts the module
     * @see Module
     */
    public void start()
    {
        // Nothing to start
    }
    

    /**
     * Stops the module
     * @see Module
     */
    public void stop()
    {
        // Nothing to stop
    }
    
    
    /**
     * Returns the status of observed toggle
     * 
     * @return state of observed toggle
     * 
     * @see Module
     */
    public boolean isObserved()
    {
        return isObserved;
    }


    /**
     * Sets the observed toggle
     * 
     * @param status
     * 
     * @see Module
     */
    public void setObserved(boolean status)
    {
        isObserved = status;
    }


    /**
     * Returns the status of the debug mode toggle
     * 
     * @return state of debug mode toggle
     * 
     * @see Module
     */
    public boolean getDebugMode()
    {
        return debugMode;
    }


    /**
     * Sets the debug mode toggle
     * 
     * @param status
     * 
     * @see Module
     */
    public void setDebugMode(boolean status)
    {
        debugMode = status;
    }


    /**
     * Returns data from this module
     *
     * @param Module requester, the requester of the data
     * @return byte[] with data
     * 
     * @see Module
     */
    public byte[] getData(Module requester)
    {
        return null;
    }


    /**
     * Set data for this module
     *
     * @param byte[] containing data
     * @param Module sender, the sender of the data
     * 
     * @return true if data is set successfully, false otherwise
     * 
     * @see Module
     */
    public boolean setData(byte[] data, Module sender)
    {
        return false;
    }


    /**
     * Set String[] data for this module
     * 
     * @param String[] data
     * @param Module sender, the sender of the data
     * 
     * @return boolean true is successful, false otherwise
     * 
     * @see Module
     */
    public boolean setData(String[] data, Module sender)
    {
        return false;
    }

    
    /**
     * Returns a dump of this module
     * 
     * @return string
     * 
     * @see Module
     */
    public String getDump()
    {
        String dump ="Parallel port status:\n";
        
        dump += "This module is only a stub, no contents available" + "\n";

        return dump;
    }


    //******************************************************************************
    // ModuleDevice Methods
    
    /**
     * Retrieve the interval between subsequent updates
     * 
     * @return int interval in microseconds
     */
    public int getUpdateInterval()
    {
        return -1;
    }

    /**
     * Defines the interval between subsequent updates
     * 
     * @param int interval in microseconds
     */
    public void setUpdateInterval(int interval) {}


    /**
     * Update device
     * 
     */
    public void update() {}
    

    /**
     * IN instruction to parallel port<BR>
     * @param portAddress   the target port; can be any of 0x027[8-A], 0x037[8-A], or 0x03B[C-E]<BR>
     * 
     * IN to portAddress 378h does ...<BR>
     * IN to portAddress 379h does ...<BR>
     * IN to portAddress 37Ah does ...<BR>
     * 
     * @return byte of data from ...
     */
    public byte getIOPortByte(int portAddress) throws ModuleUnknownPort, ModuleWriteOnlyPortException
    {
        logger.log(Level.CONFIG, "[" + MODULE_TYPE + "]" + " IO read from " + portAddress);

        switch (portAddress)
        {
            // Return identical values to Bochs during BIOS boot:
            case (DATA_PORT):
                logger.log(Level.FINE, "[" + MODULE_TYPE + "] returning default value 'available'");
            return (byte) 0xAA;

            case (DATA_PORT2):
                    logger.log(Level.FINE, "[" + MODULE_TYPE + "] returning default value 'not available'");
            return (byte) 0xFF;
            
            case (STATUS_PORT):
            case (STATUS_PORT2):
                logger.log(Level.FINE, "[" + MODULE_TYPE + "] returning default value 0x58");
                return 0x58; 
                
            // Return identical values to Bochs during BIOS boot:
            case (CONTROL_PORT):
                logger.log(Level.FINE, "[" + MODULE_TYPE + "] returning default value 'available'");
            return (byte) 0x0C;

            case (CONTROL_PORT2):
                    logger.log(Level.FINE, "[" + MODULE_TYPE + "] returning default value 'not available'");
            return (byte) 0xFF;
            
            default:
                    throw new ModuleUnknownPort("[" + MODULE_TYPE + "] Unknown I/O port requested");
        }
    }

    /**
     * OUT instruction to parallel port<BR>
     * @param portAddress   the target port; can be any of 0x027[8-A], 0x037[8-A], or 0x03B[C-E]<BR>
     * 
     * OUT to portAddress 378h does ...<BR>
     * OUT to portAddress 379h does ...<BR>
     * OUT to portAddress 37Ah does ...<BR>
     */
    public void setIOPortByte(int portAddress, byte data) throws ModuleUnknownPort
    {
        logger.log(Level.CONFIG, "[" + MODULE_TYPE + "]" + " IO write to " + portAddress + " = " + data);
       
        switch (portAddress)
        {
            case (DATA_PORT):
                logger.log(Level.FINE, "[" + MODULE_TYPE + "] OUT on port " + Integer.toHexString(DATA_PORT).toUpperCase() + " received, not handled");
                return;

            case (DATA_PORT2):
                logger.log(Level.FINE, "[" + MODULE_TYPE + "] OUT on port " + Integer.toHexString(portAddress).toUpperCase() + " received, not handled");
                return;
            
            case (STATUS_PORT):
            case (STATUS_PORT2):
                // Do nothing
                logger.log(Level.FINE, "[" + MODULE_TYPE + "] OUT on port " + Integer.toHexString(portAddress).toUpperCase() + " received, not handled");
                return;
                
            case (CONTROL_PORT):
                logger.log(Level.FINE, "[" + MODULE_TYPE + "] OUT on port " + Integer.toHexString(CONTROL_PORT).toUpperCase() + " received, not handled");
                return;

            case (CONTROL_PORT2):
                logger.log(Level.FINE, "[" + MODULE_TYPE + "] OUT on port " + Integer.toHexString(CONTROL_PORT2).toUpperCase() + " received, not handled");
                return;
            
            default:
                    throw new ModuleUnknownPort("[" + MODULE_TYPE + "] Unknown I/O port requested");
        }
    }

    public byte[] getIOPortWord(int portAddress) throws ModuleException, ModuleUnknownPort, ModuleWriteOnlyPortException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public void setIOPortWord(int portAddress, byte[] dataWord) throws ModuleException, ModuleUnknownPort
    {
        // TODO Auto-generated method stub
        return;
    }

    public byte[] getIOPortDoubleWord(int portAddress) throws ModuleException, ModuleUnknownPort, ModuleWriteOnlyPortException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public void setIOPortDoubleWord(int portAddress, byte[] dataDoubleWord) throws ModuleException, ModuleUnknownPort
    {
        // TODO Auto-generated method stub
        return;
    }

    //******************************************************************************
    // Custom methods
}