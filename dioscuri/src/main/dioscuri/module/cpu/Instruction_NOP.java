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

/**
 * Intel opcode 90<BR>
 * No operation, does not affect machine content except IP register.<BR>
 * It is an alias for XCHG AX, AX<BR>
 * Flags modified: none
 */
@SuppressWarnings("unused")
public class Instruction_NOP implements Instruction {

    // Attributes
    private CPU cpu;

    // int[] tmp;

    // Constructors

    /**
     * Class constructor
     */
    public Instruction_NOP() {
    }

    /**
     * Class constructor specifying processor reference
     *
     * @param processor Reference to CPU class
     */
    public Instruction_NOP(CPU processor) {
        // this();

        // Create reference to cpu class
        cpu = processor;

        // Initialise variable for temporary storage
        // temp = new int[];
    }

    // Methods

    /**
     * Execute no operation, does not affect machine content except IP register
     */
    public void execute() {
        // Alias for XCHG AX,AX here implemented as a null instruction
        // temp = cpu.ax;
        // cpu.ax =cpu.ax;
        // cpu.ax = temp;
    }
}
