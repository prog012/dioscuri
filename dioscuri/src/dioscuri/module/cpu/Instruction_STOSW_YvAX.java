/*
 * $Revision: 159 $ $Date: 2009-08-17 12:52:56 +0000 (ma, 17 aug 2009) $ $Author: blohman $
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

package dioscuri.module.cpu;

	/**
	 * Intel opcode AB<BR>
	 * Copy word from register AX to ES:DI; update DI register according to DF.<BR>
	 * Flags modified: none
	 */
public class Instruction_STOSW_YvAX implements Instruction {

	// Attributes
	private CPU cpu;
    byte[] transition = new byte[]{0x00, 0x02};  // Increment/decrement value
	
	// Constructors
	/**
	 * Class constructor
	 * 
	 */
	public Instruction_STOSW_YvAX()	{}
	
	/**
	 * Class constructor specifying processor reference
	 * 
	 * @param processor	Reference to CPU class
	 */
	public Instruction_STOSW_YvAX(CPU processor)
	{
		this();
		
		// Create reference to cpu class
		cpu = processor;
	}

	
	// Methods
	
	/**
	 * Copy word from register AX to ES:DI; update DI register according to flag DF
	 */
	public void execute()
	{
		// Get word at AX and assign to ES:DI; ES segment override is not allowed
		cpu.setWordToExtra(cpu.di, cpu.ax);

        // Update DI according to DF flag
        // Check direction of flag: If DF == 0, DI is incremented; if DF == 1, DI is decremented
        if (cpu.flags[CPU.REGISTER_FLAGS_DF])
        {
            // Decrement the DI register by word size
            cpu.di = Util.subtractWords(cpu.di, transition, 0);
        }
        else
        {
            // Increment the DI register by word size
            cpu.di = Util.addWords(cpu.di, transition, 0);
        }
	}
}
