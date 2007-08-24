/*
 * $Revision: 1.7 $ $Date: 2007-08-24 15:45:18 $ $Author: blohman $
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

/*
 * Information used in this module was taken from:
 * - http://bochs.sourceforge.net/techspec/CMOS-reference.txt
 * - 
 */
package nl.kbna.dioscuri.module.pit;

import java.util.logging.Level;
import java.util.logging.Logger;

import nl.kbna.dioscuri.Emulator;
import nl.kbna.dioscuri.exception.ModuleException;
import nl.kbna.dioscuri.exception.ModuleUnknownPort;
import nl.kbna.dioscuri.exception.ModuleWriteOnlyPortException;
import nl.kbna.dioscuri.module.Module;
import nl.kbna.dioscuri.module.ModuleCPU;
import nl.kbna.dioscuri.module.ModuleDevice;
import nl.kbna.dioscuri.module.ModuleMotherboard;
import nl.kbna.dioscuri.module.ModulePIC;
import nl.kbna.dioscuri.module.ModulePIT;

/**
 * An implementation of a Programmable Interval Timer (PIT) module based on the Intel 82C54 PIT chip.
 * 
 * @see ModuleDevice
 * @see Module
 * 
 * Metadata module
 * ********************************************
 * general.type                : pit
 * general.name                : Programmable Interval Timer (PIT) based on Intel 82C54 PIT
 * general.architecture        : Von Neumann
 * general.description         : Implements the Intel 82C54 PIT extended with a system clock
 * general.creator             : Tessella Support Services, Koninklijke Bibliotheek, Nationaal Archief of the Netherlands
 * general.version             : 1.0
 * general.keywords            : pit, timer, counter
 * general.relations           : motherboard, cpu
 * general.yearOfIntroduction  : 1994
 * general.yearOfEnding        : 
 * general.ancestor            : Intel 8253
 * general.successor           : 
 * pit.numberOfCounters        : 3
 * pit.numberOfModes           : 6
 * pit.clockFrequency          : 1193181 (ideally, but maybe not feasible in Java)
 * 
 * 
 * Notes:
 * This PIT is implemented to be as close as possible to the functional behaviour of an original Intel 82C54.
 * However, not all logic has to be implemented. Not part of this implementation is:
 * - Signal lines WR, RD, CD, A0 and A1.
 * 
 * Normally counters 0,1,2 are defined to a particular task (RTC, etc.). In this implementation counter zero 
 * generates a IRQ 0 if the count becomes zero. Other counters are not assigned.
 * 
 * In the original PIT, the clock signal is generated by a separate hardware component (crystal).
 * In the emulated PIT, the clock signal is generated by a separate moduleClock (imitates the crystal, but may have a different frequency). 
 * 
 */


public class PIT extends ModulePIT
{

    // Attributes
    
    // Relations
    private Emulator emu;
    private String[] moduleConnections = new String[] {"motherboard", "cpu", "pic"}; 
    protected ModuleMotherboard motherboard;
    private ModuleCPU cpu;
    protected ModulePIC pic;
    private Counter[] counters;
    
    // Toggles
    private boolean isObserved;
    private boolean debugMode;
    
    // IRQ number
    protected int irqNumber;

    // Timing
    private int updateInterval;             // Denotes the update interval for the clock timer
    
    // Logging
    private static Logger logger = Logger.getLogger("nl.kbna.dioscuri.module.pit");
    
    // Constants
    // Module specifics
    public final static int MODULE_ID       = 1;
    public final static String MODULE_TYPE  = "pit";
    public final static String MODULE_NAME  = "Intel 8254 Programmable Interval Timer (PIT)";
    
    // I/O ports PIT
    private final static int PORT_PIT_COUNTER0          = 0x040;    // RW
    private final static int PORT_PIT_COUNTER1          = 0x041;    // RW
    private final static int PORT_PIT_COUNTER2          = 0x042;    // RW
    private final static int PORT_PIT_CONTROLWORD1      = 0x043;    // RW - control word for counters 0-2
    private final static int PORT_PIT_COUNTER3          = 0x044;    // RW - counter 3 (PS/2, EISA), fail-safe timer
    private final static int PORT_PIT_CONTROLWORD2      = 0x047;    //  W - control word for counter 3
    private final static int PORT_PIT_EISA              = 0x048;    // ?? - 
    private final static int PORT_PIT_TIMER2            = 0x049;    // ?? - timer 2 (not used)
    private final static int PORT_PIT_EISA_PIT2A        = 0x04A;    // ?? - EISA PIT 2
    private final static int PORT_PIT_EISA_PIT2B        = 0x04B;    // ?? - EISA PIT 2
    private final static int PORT_KB_CTRL_B             = 0x61;     // Keyboard Controller Port B, in Bochs assigned to PIT (PC speaker??)
    


    // Constructor

    /**
     * Class constructor
     * 
     */
    public PIT(Emulator owner)
    {
        emu = owner;
        
        // Initialise variables
        isObserved = false;
        debugMode = false;

        // Initialise timing
        updateInterval = -1;
        
        // Create three new individual counters
        counters = new Counter[3];
        for (int c = 0; c < counters.length; c++)
        {
            counters[c] = new Counter(this, c);
        }
        
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
        
        // Set connection for CPU
        if (mod.getType().equalsIgnoreCase("cpu"))
        {
            this.cpu = (ModuleCPU)mod;
            return true;
        }
        
        // Set connection for PIC
        if (mod.getType().equalsIgnoreCase("pic"))
        {
            this.pic = (ModulePIC)mod;
            return true;
        }
        
        // No connection has been established
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
        if (this.motherboard != null && this.cpu != null && this.pic != null)
        {
            return true;
        }
        
        // One or more connections may be missing
        return false;
    }


    /**
     * Reset all parameters of module
     * 
     * @return boolean true if module has been reset successfully, false otherwise
     */
    public boolean reset()
    {
        // Register I/O ports 0x000 - 0x00F in I/O address space
        motherboard.setIOPort(PORT_PIT_COUNTER0, this);
        motherboard.setIOPort(PORT_PIT_COUNTER1, this);
        motherboard.setIOPort(PORT_PIT_COUNTER2, this);
        motherboard.setIOPort(PORT_PIT_CONTROLWORD1, this);
        motherboard.setIOPort(PORT_PIT_COUNTER3, this);
        motherboard.setIOPort(PORT_PIT_CONTROLWORD2, this);
        motherboard.setIOPort(PORT_PIT_EISA, this);
        motherboard.setIOPort(PORT_PIT_TIMER2, this);
        motherboard.setIOPort(PORT_PIT_EISA_PIT2A, this);
        motherboard.setIOPort(PORT_PIT_EISA_PIT2B, this);
        
        // Register IRQ number
        irqNumber = pic.requestIRQNumber(this);
        if (irqNumber > -1)
        {
            logger.log(Level.CONFIG, "[" + MODULE_TYPE + "] IRQ number set to: " + irqNumber);
        }
        else
        {
            logger.log(Level.WARNING, "[" + MODULE_TYPE + "] Request of IRQ number failed.");
        }

        // Request a timer
        if (motherboard.requestTimer(this, updateInterval, true) == true)
        {
            logger.log(Level.CONFIG, "[" + MODULE_TYPE + "]" + " Timer requested successfully.");
        }
        else
        {
            logger.log(Level.WARNING, "[" + MODULE_TYPE + "]" + " Failed to request a timer.");
        }

        // Activate timer
        motherboard.setTimerActiveState(this, true);

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
        // Stop Clock thread
//        clock.setKeepRunning(false);
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
     * @param byte[] data
     * @param Module sender, the sender of the data
     * 
     * @return boolean true if successful, false otherwise
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
        // Show some status information of this module
        String dump = "";
        String ret = "\r\n";
        String tab = "\t";
        
        dump = "PIT dump:" + ret;
        
        dump += "Update interval is " + this.updateInterval + " instructions/update." + ret;
        dump += "In total " + counters.length + " counters exist:" + ret;
        for (int i = 0; i < counters.length; i++)
        {
            if (counters[i].isEnabled() == true)
            {
                dump += "Counter " + i + tab + ": mode " + counters[i].counterMode;
                dump += ", count: start=" +  + (((counters[i].cr[Counter.MSB] & 0x000000FF) << 8) + ((counters[i].cr[Counter.LSB]) & 0x000000FF));
                dump += ", current="+ (((counters[i].ce[Counter.MSB] & 0x000000FF) << 8) + ((counters[i].ce[Counter.LSB]) & 0x000000FF));
                dump += ", R/W-mode=" + counters[i].rwMode;
                dump += ", signals: OUT=" + counters[i].getGateSignal() + " GATE=" + counters[i].getOutSignal();
                dump += ", parity: " + (counters[i].getParity() == true ? "EVEN" : "ODD");
                dump += ", bcd: " + (counters[i].getBCD() == true ? "BCD mode" : "Decimal mode") + ret;
            }
            else
            {
                dump += "Counter " + i + tab + ": mode " + counters[i].counterMode + ", not used" + ret;
            }
        }

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
        return updateInterval;
    }

    /**
     * Defines the interval between subsequent updates
     * 
     * @param int interval in microseconds
     */
    public void setUpdateInterval(int interval)
    {
        // Check if interval is > 0
        if (interval > 0)
        {
            updateInterval = interval;
        }
        else
        {
            updateInterval = 1000; // default is 1 ms
        }
        motherboard.resetTimer(this, updateInterval);
    }


    /**
     * Update device
     * 
     */
    public void update()
    {
        // Send pulse to all counters
        for (int c = 0; c < counters.length; c++)
        {
            counters[c].clockPulse();
        }
    }
    

    /**
     * Return a byte from I/O address space at given port
     * 
     * @param int portAddress containing the address of the I/O port
     * 
     * @return byte containing the data at given I/O address port
     * @throws ModuleException, ModuleWriteOnlyPortException
     */
    public byte getIOPortByte(int portAddress) throws ModuleException, ModuleUnknownPort
    {
        logger.log(Level.FINE, "[" + MODULE_TYPE + "]" + " I/O read from address 0x" + Integer.toHexString(portAddress));

        byte returnValue = 0x00;
        
        // Handle data based on portAddress
        switch (portAddress)
        {
            case PORT_PIT_COUNTER0:  // Counter 0
                returnValue = counters[0].getCounterValue();
                break;
                
            case PORT_PIT_COUNTER1:  // Counter 1
                logger.log(Level.WARNING, "[" + MODULE_TYPE + "]" + " Attempted read of Counter 1 [0x41]");
                returnValue = counters[1].getCounterValue();
                break;
                
            case PORT_PIT_COUNTER2:  // Counter 2
                returnValue =  counters[2].getCounterValue();
                break;
                
            case PORT_PIT_CONTROLWORD1:  // Control word
                // Do nothing as reading from control word register is not possible
                logger.log(Level.WARNING, "[" + MODULE_TYPE + "]" + " Attempted read of control word port [0x43]");
                break;

            case PORT_KB_CTRL_B:  // Port 0x61
                // Report reading from port
                logger.log(Level.WARNING, "[" + MODULE_TYPE + "]" + " Attempted read of KB_CTRL_B [0x61]");
                break;
                
            default:
                throw new ModuleUnknownPort("[" + MODULE_TYPE + "] Unknown I/O port requested");
        }
        
        // Return dummy value 0
        return returnValue;
    }


    /**
     * Set a byte in I/O address space at given port
     * 
     * @param int portAddress containing the address of the I/O port
     * @param byte data
     * 
     * @throws ModuleException, ModuleWriteOnlyPortException
     */
    public void setIOPortByte(int portAddress, byte data) throws ModuleException, ModuleUnknownPort
    {
        logger.log(Level.FINE, "[" + MODULE_TYPE + "]" + " I/O write to 0x" + Integer.toHexString(portAddress) + " = 0x" + Integer.toHexString(data));

        // Handle writing data based on portAddress
        switch (portAddress)
        {
            case PORT_PIT_COUNTER0:  // Counter 0
            	logger.log(Level.CONFIG,  "[" + MODULE_TYPE + "] Counter 0: value set to 0x" + Integer.toHexString(data & 0xFF));
                counters[0].setCounterValue(data);
                break;
                
            case PORT_PIT_COUNTER1:  // Counter 1
            	logger.log(Level.CONFIG,  "[" + MODULE_TYPE + "] Counter 1: value set to 0x" + Integer.toHexString(data & 0xFF));
                counters[1].setCounterValue(data);
                break;
                
            case PORT_PIT_COUNTER2:  // Counter 2
            	logger.log(Level.CONFIG,  "[" + MODULE_TYPE + "] Counter 2: value set to 0x" + Integer.toHexString(data & 0xFF));
                counters[2].setCounterValue(data);
                break;
                
            case PORT_PIT_CONTROLWORD1:  // Control word
                // Analyse control word:
                // --------------------------------
                // D7  D6  D5  D4  D3  D2  D1  D0
                // SC1 SC0 RW1 RW0 M2  M1  M0  BCD
                
                // Counter select (SC1/SC0)
                int cNum = (data >> 6);
                // Read/Write mode (RW1/RW0)
                int rwMode = (data >> 4) & 0x03;
                int counterMode = (data >> 1) & 0x07;
                int bcd = data & 0x01;
                
                // Check for valid data
                if ((counterMode > 6) || (rwMode > 4))
                {
                    logger.log(Level.SEVERE, "[" + MODULE_TYPE + "] ControlWord counterMode (" + counterMode + ") / rwMode (" + rwMode + ") out of range");
                    break;
                }
                
                // check if ControlWord is a read-back command
                if (cNum == 0x03)
                {
                    // Read-back command: set appropriate counter in read-back mode
                    // TODO: implement this following Intel 82C54 specs
                    logger.log(Level.WARNING, "[" + MODULE_TYPE + "] Read-Back Command is not implemented");
                    break;
                }

                // Perform counter setup, where cNum denotes counter 0, 1 or 2
                switch (rwMode)
                {
                    case 0x00:  // Counter latch
                        // Read operation: Counter latch command
                        logger.log(Level.CONFIG, "[" + MODULE_TYPE + "] Counter " + cNum + " in latch mode.");
                        // Set specified counter in latch register
                        counters[cNum].latchCounter();
                        break;
                    
                    case 0x01:  // LSB mode
                    case 0x02:  // MSB mode
                        logger.log(Level.WARNING, "[" + MODULE_TYPE + "] LSB/MSB command not implemented");
                        break;
                        
                    case 0x03:  // 16-bit mode
                        logger.log(Level.CONFIG, "[" + MODULE_TYPE + "] Counter " + cNum + " in 16-bit mode.");
                        counters[cNum].setCounterMode(counterMode);
                        counters[cNum].rwMode = rwMode;
                        break;
                        
                    default:
                        logger.log(Level.WARNING, "[" + MODULE_TYPE + "] rwMode [" + rwMode + "] not recognised");
                            break;
                        
                }
                break;
                    
//
//                	// Enable counter
//                	if (counters[cNum].isEnabled() == true)
//                	{
//                		logger.log(Level.WARNING, "[" + MODULE_TYPE + "] Counter " + cNum + " is already in use. Resetting may cause timing issues.");
//                	}
//                	counters[cNum].setEnabled(true);
//                	
//                    counters[cNum].rwMode = rwMode;
//                    
//                    {
//
//                    }
//                    else
//                    {
//                        // Mode of operation (M2/M1/M0)
//                        counters[cNum].setCounterMode((data >> 1) & 0x00000007);
//                        
//                        // Binary or Binary Code Decimal (BCD)
//                        counters[cNum].bcd = (data & 0x00000001) == 0x00000001 ? true : false;
//                    }
//            		logger.log(Level.CONFIG, "[" + MODULE_TYPE + "] Counter " + cNum + " has been set.");
//                }
//                break;
                
            default:
                throw new ModuleUnknownPort("[" + MODULE_TYPE + "] Unknown I/O port requested");
        }
        return;
    }


    public byte[] getIOPortWord(int portAddress) throws ModuleException, ModuleWriteOnlyPortException
    {
        logger.log(Level.WARNING, "[" + MODULE_TYPE + "] IN command (word) to port " + Integer.toHexString(portAddress).toUpperCase() + " received");
        logger.log(Level.WARNING, "[" + MODULE_TYPE + "] Returned default value 0xFFFF to AX");
        
        // Return dummy value 0xFFFF
        return new byte[] { (byte) 0x0FF, (byte) 0x0FF };
    }


    public void setIOPortWord(int portAddress, byte[] dataWord) throws ModuleException
    {
        logger.log(Level.WARNING, "[" + MODULE_TYPE + "] OUT command (word) to port " + Integer.toHexString(portAddress).toUpperCase() + " received. No action taken.");
        
        // Do nothing and just return
        return;
    }


    public byte[] getIOPortDoubleWord(int portAddress) throws ModuleException, ModuleWriteOnlyPortException
    {
        logger.log(Level.WARNING, "[" + MODULE_TYPE + "] IN command (double word) to port " + Integer.toHexString(portAddress).toUpperCase() + " received");
        logger.log(Level.WARNING, "[" + MODULE_TYPE + "] Returned default value 0xFFFFFFFF to eAX");
        
        // Return dummy value 0xFFFFFFFF
        return new byte[] { (byte) 0x0FF, (byte) 0x0FF, (byte) 0x0FF, (byte) 0x0FF };
    }


    public void setIOPortDoubleWord(int portAddress, byte[] dataDoubleWord) throws ModuleException
    {
        logger.log(Level.WARNING, "[" + MODULE_TYPE + "] OUT command (double word) to port " + Integer.toHexString(portAddress).toUpperCase() + " received. No action taken.");
        
        // Do nothing and just return
        return;
    }


    //******************************************************************************
    // ModulePIT Methods
    
    /**
     * Retrieves the current clockrate of this clock in milliseconds
     * 
     * @return long milliseconds defining how long the clock sleeps before sending a pulse
     */
/*    public long getClockRate()
    {
        // Return the current number of milliseconds the clock is sleeping
        return clock.getClockRate();
    }
*/
    
    /**
     * Sets the clock rate for this PIT
     * 
     * @param long milliseconds, the time between two consequtive clock pulses
     */
/*    public void setClockRate(long milliseconds)
    {
        // Set the clockrate of clock
        clock.setClockRate(milliseconds);
    }
*/

    
    //******************************************************************************
    // Custom Methods
    
	protected void raiseIRQ(Counter counter)
	{
		pic.setIRQ(irqNumber);
	}

    
	protected void lowerIRQ(Counter counter)
	{
		pic.clearIRQ(irqNumber);
	}
    
}
