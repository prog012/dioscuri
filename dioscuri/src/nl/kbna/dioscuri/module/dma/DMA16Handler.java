/*
 * $Revision: 1.1 $ $Date: 2007-07-02 14:31:41 $ $Author: blohman $
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
package nl.kbna.dioscuri.module.dma;

/**
 * Handler for the slave DMA controller, providing implementations for 16-bit read and write functionality<BR>
 * This handler will be registered with the DMA class to provide device-specific methods for reading (memory -> device) 
 * and writing (device -> memory) a word via DMA. 
 * 
 */
public abstract class DMA16Handler
{
    // Name of the device that provides the methods
    String owner;

    /**
     * Device-specific implementation of the 16-bit DMA read functionality.<BR>
     * This provides a way for DMA to pass a word read from memory (by way of DMA request)
     * to the device for further processing.
     * 
     * @param data  Word from memory that is passed to the device for handling
     */
    public abstract void dma16ReadFromMem(byte[] data);

    /**
     * Device-specific implementation of the 16-bit DMA write functionality.<BR>
     * This provides a way for DMA to write a word to memory (by way of DMA request)
     * passed from the device.
     * 
     * @return Word from device that will be written to memory
     */
    public abstract byte[] dma16WriteToMem();    
}