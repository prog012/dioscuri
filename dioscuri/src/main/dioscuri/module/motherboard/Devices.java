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

package dioscuri.module.motherboard;

import java.util.ArrayList;

import dioscuri.module.ModuleDevice;


public class Devices extends ArrayList
{
	// Attributes
	
    
	// Constructors
	/**
     * Class Constructor
	 * 
	 */
	public Devices()
	{
		super();
	}
	
	/**
     * Class Constructor
     * 
	 * @param capacity
	 */
	public Devices(int capacity)
	{
		super(capacity);
	}

	
	// Methods
	/**
     * Add a device to array
     * 
	 * @param ModuleDevice device
     * 
	 * @return boolean true if succesful, false otherwise
	 */
	public boolean addDevice(ModuleDevice device)
	{
		return super.add(device);
	}


	/**
     * Return a device from array based on given type
     * 
	 * @param String type defining the type of the requested device
     * 
	 * @return ModuleDevice
	 */
	public ModuleDevice getDevice(String type)
	{
		for (int i = 0; i < super.size(); i++)
		{
			if ((this.getDevice(i)).getType().equalsIgnoreCase(type))
			{
				return (ModuleDevice)super.get(i);
			}
		}
		return null;
	}


	/**
     * Return a device from array based on given index
     * 
	 * @param int index of the device
     * 
	 * @return ModuleDevice
	 */
	public ModuleDevice getDevice(int index)
	{
		return (ModuleDevice)super.get(index);
	}
}
