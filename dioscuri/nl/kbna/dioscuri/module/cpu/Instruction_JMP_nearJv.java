/* $Revision: 1.1 $ $Date: 2007-07-02 14:31:33 $ $Author: blohman $
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

	/**
	 * Intel opcode E9<BR>
	 * Unconditional relative near jump indicated by immediate signed word.<BR>
	 * Displacement is relative to next instruction.<BR>
	 * Flags modified: none
	 */
public class Instruction_JMP_nearJv implements Instruction {

	// Attributes
	private CPU cpu;
    byte[] displacement;
    int intermediateResult;

    
	// Constructors
	/**
	 * Class constructor
	 * 
	 */
	public Instruction_JMP_nearJv()
    {
        displacement = new byte[2];
        intermediateResult = 0;
    }
	
	/**
	 * Class constructor specifying processor reference
	 * 
	 * @param processor	Reference to CPU class
	 */
	public Instruction_JMP_nearJv(CPU processor)
	{
		//this();
		
		// Create reference to cpu class
		cpu = processor;
	}

	
	// Methods
	
	/**
	 * Execute unconditional relative near jump indicated by immediate signed word
	 */
	public void execute()
	{
		// Jump is relative to _next_ instruction, but by the time we change 
		// the IP, it has already been incremented thrice, so no extra arithmetic necessary 		
        displacement = cpu.getWordFromCode();
        
		// Update IP with displacement
        intermediateResult = (((int)(cpu.ip[CPU.REGISTER_GENERAL_LOW]))&0xFF) + (((int)displacement[CPU.REGISTER_GENERAL_LOW])&0xFF);
        // Need to check for possible overflow/underflow in IP[low]
        if (intermediateResult > 0xFF)
        {
            // Overflow
            cpu.ip[CPU.REGISTER_GENERAL_HIGH]++;
        }
    
        // Update IP[low] with displacement
        cpu.ip[CPU.REGISTER_GENERAL_LOW] += displacement[CPU.REGISTER_GENERAL_LOW];
        // Update IP[high] with displacement
        cpu.ip[CPU.REGISTER_GENERAL_HIGH] += displacement[CPU.REGISTER_GENERAL_HIGH];

	}
}