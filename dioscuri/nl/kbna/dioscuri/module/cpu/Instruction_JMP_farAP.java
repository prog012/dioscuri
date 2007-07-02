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
	 * Intel opcode EA<BR>
	 * Unconditional absolute far jump indicated by immediate two words.<BR>
	 * Displacement is absolute, where addressword1 == IP, addressword 2 == CS.<BR>
	 * Flags modified: none
	 */
public class Instruction_JMP_farAP implements Instruction {

	// Attributes
	private CPU cpu;
	byte[] newIP = new byte[2];
	
	// Constructors
	/**
	 * Class constructor
	 * 
	 */
	public Instruction_JMP_farAP()	{}
	
	/**
	 * Class constructor specifying processor reference
	 * 
	 * @param processor	Reference to CPU class
	 */
	public Instruction_JMP_farAP(CPU processor)
	{
		//this();
		
		// Create reference to cpu class
		cpu = processor;
	}

	
	// Methods
	
	/**
	 * Execute instruction
	 */
	public void execute()
	{
		// Get displacement words (immediate).
        byte[] word_ip = cpu.getWordFromCode();
        byte[] word_cs = cpu.getWordFromCode();
        
        // Assign words to ip and cs
        // NOTE: this cannot be done at once because it will influence the pointer directly
        cpu.ip = word_ip;
		cpu.cs = word_cs;
	}
}