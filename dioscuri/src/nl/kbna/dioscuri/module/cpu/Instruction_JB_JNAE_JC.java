/* $Revision: 1.2 $ $Date: 2007-07-31 09:20:32 $ $Author: blohman $
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
	 * Intel opcode 72<BR>
	 * Conditional short jump on carry.<BR>
	 * Displacement is relative to next instruction.<BR>
	 * Flags modified: none
	 */
public class Instruction_JB_JNAE_JC implements Instruction {

	// Attributes
	private CPU cpu;
	byte displacement;
	
	// Constructors
	/**
	 * Class constructor
	 * 
	 */
	public Instruction_JB_JNAE_JC()	{}
	
	/**
	 * Class constructor specifying processor reference
	 * 
	 * @param processor	Reference to CPU class
	 */
	public Instruction_JB_JNAE_JC(CPU processor)
	{
		//this();
		
		// Create reference to cpu class
		cpu = processor;
	}

	
	// Methods
	
	/**
	 * Execute conditional short jump on carry
	 */
	public void execute()
	{
        // Get displacement byte (immediate)
        // Jump is relative to _next_ instruction, but by the time the byte is added to 
        // the IP, it has already been incremented twice so no extra arithmetic is necessary
        displacement = cpu.getByteFromCode();
 		
        // Jump if carry flag set, otherwise skip instruction
		if (cpu.flags[CPU.REGISTER_FLAGS_CF])
		{
            // Although not explicitly stated, IA-SDM2 p. 3-332 8-byte displacement is sign-extended and added. 
            cpu.ip = Util.addWords(cpu.ip, new byte[]{Util.signExtend(displacement), displacement}, 0);
		}
	}
}