/*
 * $Revision$ $Date$ $Author$
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
package nl.kbna.dioscuri.datatransfer;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.Toolkit;
import java.io.*;

import nl.kbna.dioscuri.GUI;

/**
* This class allows data transfer via the clipboard to and from the emulator
*/
public final class TextTransfer implements ClipboardOwner
{

	// Attributes
	GUI gui;
	
	
	// Constructor
	public TextTransfer(GUI parent)
	{
		gui = parent;
	}
	
	
	// Methods
	/**
	* Empty implementation of the ClipboardOwner interface.
	*/
	public void lostOwnership( Clipboard aClipboard, Transferable aContents)
    {
		//do nothing
    }

	/**
	* Set String on clipboard, and make this class the owner of the Clipboard's contents.
    */
	public void setClipboardContents(String text)
	{
		// Wrap String
		StringSelection stringSelection = new StringSelection(text);
		
		// Request system's clipboard and copy text to it
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(stringSelection, this);
	}

	/**
	* Get String in clipboard.
    *
    * @return any text found on the Clipboard; if none found, return an empty String.
    */
	public String getClipboardContents()
	{
		String result = "";
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		
		// odd: the Object param of getContents is not currently used
		Transferable contents = clipboard.getContents(null);
		boolean hasTransferableText = (contents != null) && contents.isDataFlavorSupported(DataFlavor.stringFlavor);
		
		if (hasTransferableText)
		{
		      try
		      {
		    	  result = (String)contents.getTransferData(DataFlavor.stringFlavor);
		      }
		      catch (UnsupportedFlavorException ex)
		      {
		    	  //highly unlikely since we are using a standard DataFlavor
		    	  System.out.println(ex);
		    	  ex.printStackTrace();
		      }
		      catch (IOException ex)
		      {
		    	  System.out.println(ex);
		    	  ex.printStackTrace();
		      }
	    }
		return result;
	}
}
