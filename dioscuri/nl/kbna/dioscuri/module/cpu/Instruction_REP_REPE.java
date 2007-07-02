/* $Revision: 1.1 $ $Date: 2007-07-02 14:31:37 $ $Author: blohman $
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

package nl.kbna.dioscuri.module.cpu;

import nl.kbna.dioscuri.exception.CPUInstructionException;

/**
 * Intel opcode F3<BR>
 * Repeat execution of string instruction until CX == 0 or ZF is set.<BR>
 * Target string instruction is next instruction.<BR>
 * Flags modified: none; however, the CMPS and SCAS instructions do set status flags
 */
public class Instruction_REP_REPE implements Instruction
{

    // Attributes
    private CPU cpu;

    byte[] word0x01;

    // Constructors
    /**
     * Class constructor
     */
    public Instruction_REP_REPE()
    {
    }

    /**
     * Class constructor specifying processor reference
     * 
     * @param processor Reference to CPU class
     */
    public Instruction_REP_REPE(CPU processor)
    {
        this();

        // Create reference to cpu class
        cpu = processor;

        word0x01 = new byte[] {0x00, 0x01};
    }

    // Methods

    /**
     * Repeat string instruction until CX == 0 or ZF == 0
     */
    public void execute() throws CPUInstructionException
    {
        // TODO: Check the border case CX == 1 is executed the correc number of times.

        // Check if this is the first call to REP
        if (!cpu.repActive)
        {
            // Check the special case CX is zero before entering the REP
            if (cpu.cx[CPU.REGISTER_GENERAL_LOW] == 0 && cpu.cx[CPU.REGISTER_GENERAL_HIGH] == 0)
            {
                // Skip the REP instruction completely; do this by incrementing the IP past the target instruction, including prefixes
                skipInstruction();
                return;
            }

            // Indicate we're a prefix, and indicate it's a REP
            cpu.prefixInstruction = 0xF3;
            cpu.repActive = true;

            // Don't forget to decrement the CX register already, target will be executed straight after this!
            cpu.cx = Util.subtractWords(cpu.cx, word0x01, 0);

            // Now we need to check for the border case where CX == 0 now, then end the loop right here...
            if (cpu.cx[CPU.REGISTER_GENERAL_LOW] == 0 && cpu.cx[CPU.REGISTER_GENERAL_HIGH] == 0)
            {
                finalExecution();
            }
        }
        else
        {
            // We're already in the repeat loop
            // CPU will execute the target instruction; all we need to do here is update and check the conditions

            // Again indicate we're a prefix
            cpu.prefixInstruction = 0xF3;

            // Check if REP or REPE/REPZ should be handled;
            // REPE/REPZ should only prefix CMPS (A6, A7) and SCAS (AE, AF) - but note that not all programs follow this convention!!
            if (cpu.repeRepz)
            {
                // REPE/REPZ
                // CMPS or SCAS has set ZF, so check it as extra terminating condition
                if (!cpu.flags[CPU.REGISTER_FLAGS_ZF])
                {
                    // Zero flag == 0, so terminate immediately by moving until the next instruction

                    skipInstruction();
                    return;
                }
            }

            // Check terminating condition CX == 0 (we check for 1, because the target is executed one last time below
            if (!(cpu.cx[CPU.REGISTER_GENERAL_LOW] == 1 && cpu.cx[CPU.REGISTER_GENERAL_HIGH] == 0))
            {
                cpu.cx = Util.subtractWords(cpu.cx, word0x01, 0);

            }
            else
            {
                // REP finished; execute target one last time, and update all counters as well
                cpu.cx = Util.subtractWords(cpu.cx, word0x01, 0);
                finalExecution();
            }
            
        }
    }

    /**
     * Skip past the REP instruction without executing the instructions.<BR>
     * Reset the prefixInstruction and repActive.
     */
    private void skipInstruction()
    {
        // Move past target, checking for prefixes
        // TODO: Assuming all target instructions are single opcode here!
        cpu.b1 = cpu.getByteFromCode() & 0xFF;
        // TODO: Add lock/address size prefixes
        while (cpu.b1 == 0x26 || cpu.b1 == 0x2E || cpu.b1 == 0x36 || cpu.b1 == 0x3E || cpu.b1 == 0x66)
        {
            cpu.b1 = cpu.getByteFromCode();
        }
        
        // Reset the prefix counter to continue execution in the CPU loop
        cpu.prefixInstruction = 0;
        cpu.repActive = false;
    }

    
    /**
     * Perform the final REP execution<BR>
     * Reset the prefixInstruction and repActive. 
     */
    private void finalExecution() throws CPUInstructionException
    {
        // Execute target instruction the final time, including any prefixes
        cpu.b1 = (cpu.getByteFromCode() & 0xFF);
        while (cpu.b1 == 0x26 || cpu.b1 == 0x2E || cpu.b1 == 0x36 || cpu.b1 == 0x3E || cpu.b1 == 0x66)
        {
            // Push prefix onto stack if not there already
            if (cpu.prefixInstructionStack.search(cpu.b1) == -1)
            {
                cpu.prefixInstructionStack.push(cpu.b1);
            }
            cpu.singleByteInstructions[cpu.b1].execute();
            cpu.b1 = cpu.getByteFromCode() & 0xFF;

        }
        cpu.singleByteInstructions[cpu.b1].execute();
        
        // Reset the prefix counter to continue execution in the CPU loop
        cpu.prefixInstruction = 0;
        cpu.repActive = false;
    }
}