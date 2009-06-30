/*
 * $Revision: 1.1 $ $Date: 2007-07-02 14:31:38 $ $Author: blohman $
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
 * Intel opcode 3E<BR>
 * Instruction SEG=DS. Overrides the selected segment with DS.<BR>
 * Flags modified: none
 */
public class Instruction_SEG_DS implements Instruction {

	// Attributes
	private CPU cpu;
	
    
	// Constructors
	/**
	 * Construct class
	 */
	public Instruction_SEG_DS()	{}
	
	/**
	 * Construct class
	 * @param processor
	 */
	public Instruction_SEG_DS(CPU processor)
	{
		this();
		
		// Create reference to cpu class
		cpu = processor;
	}

	
	// Methods
	
	/**
	 * Execute instruction
	 */
	public void execute()
	{
        // Let CPU know this is an instruction prefix
        cpu.prefixInstruction = 0x3E;
        
        cpu.segmentOverride = true;
        cpu.segmentOverridePointer = CPU.SEGMENT_OVERRIDE_DS;
	}
}