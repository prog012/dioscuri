/* $Revision: 160 $ $Date: 2009-08-17 12:56:40 +0000 (ma, 17 aug 2009) $ $Author: blohman $ 
 * 
 * Copyright (C) 2007-2009  National Library of the Netherlands, 
 *                          Nationaal Archief of the Netherlands, 
 *                          Planets
 *                          KEEP
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 *
 * For more information about this project, visit
 * http://dioscuri.sourceforge.net/
 * or contact us via email:
 *   jrvanderhoeven at users.sourceforge.net
 *   blohman at users.sourceforge.net
 *   bkiers at users.sourceforge.net
 * 
 * Developed by:
 *   Nationaal Archief               <www.nationaalarchief.nl>
 *   Koninklijke Bibliotheek         <www.kb.nl>
 *   Tessella Support Services plc   <www.tessella.com>
 *   Planets                         <www.planets-project.eu>
 *   KEEP                            <www.keep-project.eu>
 * 
 * Project Title: DIOSCURI
 */

package dioscuri.module;

import dioscuri.exception.ModuleException;
import dioscuri.exception.ModuleWriteOnlyPortException;

/**
 * Interface representing a generic hardware module.
 * 
 */

public abstract class ModuleCPU extends Module implements Addressable {

    public ModuleCPU() {
        super(Type.CPU,
                Type.MEMORY, Type.MOTHERBOARD, Type.PIC, Type.CLOCK);
    }

    /**
     * Set the Instructions Per Second (ips) for this CPU.
     * @param ips the Instructions Per Second (ips) for this CPU.
     */
    public abstract void setIPS(int ips);

    /**
     * Get the Instructions Per Second (ips) for this CPU.
     * @return the Instructions Per Second (ips) for this CPU.
     */
    public abstract int getIPS();

    /**
     * Set the Instructions Per Second (ips) for this CPU. Also, define what the
     * smallest period is for sending a clockpulse (in microseconds)
     * 
     * @param ips
     * @param lowestUpdatePeriod the lowest update period in microseconds
     */
    public abstract void setIPS(int ips, int lowestUpdatePeriod);

    /**
     * Retrieve string with information about next instruction to be executed
     * 
     * @return string containing next instruction information
     * 
     */
    public abstract String getNextInstructionInfo();

    /**
     * Retrieve current number of instruction (instructions executed so far)
     * 
     * @return long containing number of instructions
     * 
     */
    public abstract long getCurrentInstructionNumber();

    /**
     * Increment current number of instruction by one
     * 
     */
    protected abstract void incrementInstructionCounter();

    /**
     * Returns a dump of the current registers with their value
     * 
     * @return String containing a register dump
     */
    public abstract String dumpRegisters();

    /**
     * Initialise registers
     * 
     * @return true if initialisation is successful, false otherwise
     * 
     */
    protected abstract boolean initRegisters();

    /**
     * Initialise the single and double byte opcode lookup arrays with
     * instructions corresponding to the Intel hexadecimal machinecode values.
     * @return -
     */
    protected abstract boolean initInstructionTables();

    /**
     * Set the boolean that starts and stops the CPU loop
     * @param status sets the isRunning boolean
     */
    protected abstract void setRunning(boolean status);

    /**
     * Returns the value of a named register.
     * @param registerName
     * @return int[] with value of register, null otherwise
     */
    protected abstract byte[] getRegisterValue(String registerName);

    /**
     * Sets the value of a named register to given value.
     * @param registerName
     * @param value containing the value
     * @return true if set was successful, false otherwise
     */
    protected abstract boolean setRegisterValue(String registerName,
            byte[] value);

    /**
     * Returns the value (byte) in I/O address space at given port address.
     * 
     * @param portAddress
     * @return byte value
     * @throws ModuleException
     * @throws ModuleWriteOnlyPortException
     */
    //protected abstract byte getIOPortByte(int portAddress)
    //        throws ModuleException, ModuleWriteOnlyPortException;

    /**
     * Sets the value (byte) in I/O address space at given port address.
     * @param portAddress
     * @param value
     * @throws ModuleException
     */
    //protected abstract void setIOPortByte(int portAddress, byte value)
    //        throws ModuleException;

    /**
     * Returns the value (word) in I/O address space at given port address.
     * @param portAddress
     * @return byte[] value (word)
     * @throws ModuleException
     * @throws ModuleWriteOnlyPortException
     */
    //protected abstract byte[] getIOPortWord(int portAddress)
    //        throws ModuleException, ModuleWriteOnlyPortException;

    /**
     * Sets the value (word) in I/O address space at given port address.
     * @param portAddress
     * @param value (word)
     * @throws ModuleException
     */
    //protected abstract void setIOPortWord(int portAddress, byte[] value)
    //       throws ModuleException;

    /**
     * Returns the value (double word) in I/O address space at given port
     * address.
     * @param portAddress
     * @return byte[] value (double word)
     * @throws ModuleException
     * @throws ModuleWriteOnlyPortException
     */
    //protected abstract byte[] getIOPortDoubleWord(int portAddress)
    //        throws ModuleException, ModuleWriteOnlyPortException;

    /**
     * Sets the value (double word) in I/O address space at given port address.
     * @param portAddress
     * @param value (double word)
     * @throws ModuleException
     */
    //protected abstract void setIOPortDoubleWord(int portAddress, byte[] value)
    //       throws ModuleException;

    /**
     * Set the interrupt request (IRQ).
     * @param value
     */
    public abstract void interruptRequest(boolean value);

    /**
     *
     * @param value
     * @param origin
     */
    public abstract void setHoldRequest(boolean value, ModuleDevice origin);

    /**
     *
     * @param register
     * @return -
     */
    public abstract String getRegisterHex(int register);

    /**
     * Get CPU instruction debug.
     * @return cpuInstructionDebug.
     */
    public abstract boolean getCpuInstructionDebug();

    /**
     * Set the CPU instruction debug.
     * @param isDebugMode status of instructionDebug (on/off)
     */
    public abstract void setCpuInstructionDebug(boolean isDebugMode);

    /**
     * Returns if CPU halted abnormally or not
     * 
     * @return boolean abnormalTermination true if abnormal, false otherwise
     */
    public abstract boolean isAbnormalTermination();

    /**
     * Returns if CPU halted due to full system shutdown or not
     * 
     * @return boolean shutDown true if emulator should shutdown, false
     *         otherwise
     */
    public abstract boolean isShutdown();
}
