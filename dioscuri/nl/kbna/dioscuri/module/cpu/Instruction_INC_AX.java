/* $Revision: 1.1 $ $Date: 2007-07-02 14:31:32 $ $Author: blohman $
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
	 * Intel opcode 40<BR>
	 * Increment general register AX.<BR>
	 * Flags modified: OF, SF, ZF, AF, PF
	 */
public class Instruction_INC_AX implements Instruction {

	// Attributes
	private CPU cpu;
    private byte[] temp;
    private byte[] oldWord;
    private byte[] incWord;
	
	
	// Constructors
	/**
	 * Class constructor
	 * 
	 */
	public Instruction_INC_AX()	{}
	
	/**
	 * Class constructor specifying processor reference
	 * 
	 * @param processor	Reference to CPU class
	 */
	public Instruction_INC_AX(CPU processor)
	{
		//this();
		
		// Create reference to cpu class
		cpu = processor;
        temp = new byte[2];
        oldWord = new byte[2];
        incWord = new byte[] {0x00,0x01};
	}

	
	// Methods
	
	/**
	 * Increment general register AX
	 */
	public void execute()
	{
        // Make copy of old value
        System.arraycopy(cpu.ax, 0, oldWord, 0, cpu.ax.length);
        
        // Increment the ax register
        temp = Util.addWords(cpu.ax, incWord, 0);
        
        // Assign result to ax
        cpu.ax[CPU.REGISTER_GENERAL_HIGH] = temp[CPU.REGISTER_GENERAL_HIGH];
        cpu.ax[CPU.REGISTER_GENERAL_LOW] = temp[CPU.REGISTER_GENERAL_LOW];
        
        // Test AF
        cpu.flags[CPU.REGISTER_FLAGS_AF] = (oldWord[CPU.REGISTER_GENERAL_LOW] & 0x0F) == 0x0F ? true : false;
        // Test OF
        cpu.flags[CPU.REGISTER_FLAGS_OF] = Util.test_OF_ADD(oldWord, incWord, cpu.ax, 0);  
        // Test ZF
        cpu.flags[CPU.REGISTER_FLAGS_ZF] = cpu.ax[CPU.REGISTER_GENERAL_HIGH] == 0x00 && cpu.ax[CPU.REGISTER_GENERAL_LOW] == 0x00 ? true : false;
        // Test SF (set when MSB of BH is 1. In Java can check signed byte)
        cpu.flags[CPU.REGISTER_FLAGS_SF] = cpu.ax[CPU.REGISTER_GENERAL_HIGH] < 0 ? true : false;
        // Set PF, only applies to LSB
        cpu.flags[CPU.REGISTER_FLAGS_PF] = Util.checkParityOfByte(cpu.ax[CPU.REGISTER_GENERAL_LOW]);
	}
}