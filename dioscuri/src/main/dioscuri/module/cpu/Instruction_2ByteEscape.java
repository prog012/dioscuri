/* $Revision: 159 $ $Date: 2009-08-17 12:52:56 +0000 (ma, 17 aug 2009) $ $Author: blohman $ 
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

package dioscuri.module.cpu;

import dioscuri.exception.CPUInstructionException;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Intel opcode 0F<BR>
 * Escape character for two-byte opcodes.<BR>
 * References the doubleByteInstructions array in CPU<BR>
 * Flags modified: none
 */
public class Instruction_2ByteEscape implements Instruction {

    // Attributes
    private CPU cpu;

    int instruction;

    // Logging
    private static final Logger logger = Logger.getLogger(Instruction_2ByteEscape.class.getName());

    // Constructors

    /**
     * Class constructor
     */
    public Instruction_2ByteEscape() {
    }

    /**
     * Class constructor specifying processor reference
     *
     * @param processor Reference to CPU class
     */
    public Instruction_2ByteEscape(CPU processor) {
        // this();

        // Create reference to cpu class
        cpu = processor;
    }

    // Methods

    /**
     * Execute doubleByteInstructions[instruction]
     *
     * @throws CPUInstructionException
     */
    public void execute() throws CPUInstructionException {
        // Retrieve instruction number after escape byte
        instruction = cpu.getByteFromCode();

        // Temporary setting for debugging
        // if(cpu.getCpuInstructionDebug())
        cpu.codeByte2 = (instruction & 0xFF);

        if (cpu.doubleWord && !cpu.isDoubleByte32BitSupported()) { // TODO BK Win 3.0 problem
            logger.log(Level.SEVERE, ">> [" + cpu.getType()
                    + "] Instruction problem (opcode "
                    + Integer.toHexString(cpu.codeByte) + " "
                    + Integer.toHexString(cpu.codeByte2)
                    + "h): 32-bit not supported!");
            System.out.println(">> executing cpu.doubleByteInstructions[" + (instruction & 0xFF) + "]");
        }

        // Cast signed byte to integer to avoid invalid array lookup
        // Jump to doubleByte instruction
        cpu.doubleByteInstructions[(instruction & 0xFF)].execute();
    }
}
