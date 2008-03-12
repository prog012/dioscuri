/*
 * $Revision: 1.1 $ $Date: 2008-03-12 14:37:03 $ $Author: blohman $
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
	 * Intel opcode D6<BR>
	 * Set AL on CF (undocumented Intel instruction).<BR>
	 * Set or clear AL depending on carry flag status<BR>
	 * Flags modified: none
	 */
public class Instruction_SALC implements Instruction
{

	// Attributes
	private CPU cpu;

	// Constructors
	/**
	 * Class constructor 
	 * 
	 */
	public Instruction_SALC()	{}
	
	/**
	 * Class constructor specifying processor reference
	 * 
	 * @param processor	Reference to CPU class
	 */
	public Instruction_SALC(CPU processor)
	{
		this();
		
		// Create reference to cpu class
		cpu = processor;
	}

	
	// Methods
	
	/**
	 * Set or clear AL depending on carry flag status<BR>
	 * Information taken from http://www.x86.org/secrets/opcodes/salc.htm
	 */
	public void execute()
	{
		if (cpu.flags[CPU.REGISTER_FLAGS_CF] == true)
		{
			cpu.ax[CPU.REGISTER_GENERAL_LOW] = (byte) 0xFF;
		}
		else
		{
			cpu.ax[CPU.REGISTER_GENERAL_LOW] = (byte) 0x00;
		}
	}
}
