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
package dioscuri;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;


/**
 * Class StartupCanvas draws a buffered image on a canvas.
 * 
 */
@SuppressWarnings("serial")
public class StartupPanel extends JPanel
{
    // Attributes
    private BufferedImage image = null;
    private boolean paint = false;

    
    // Constructor
    public StartupPanel()
    {
    }

    
    // Methods
    /**
     * Clear the image on canvas
     * 
     */
    public void clearImage()
    {
        // Clear Image Area
        paint = false;
        this.repaint();
    }

    /**
     * Draw an image on the panel
     * This is a standard method used by Graphics
     * 
     * @param Graphics g - standard graphics component
     * 
     */
    public void paintComponent(Graphics g)
    {
        // Paint image on panel
        if (paint && image != null)
        {
            g.drawImage(image, 0, 0, this);
        }
        else
        {
            // Paint panel black
            g.setColor(Color.white);
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }
    

    /**
     * Update the panel
     * This method is called automatically when repaint() is called.
     * It is necesarry to doublebuffer the panel (to prevent it from blinking when updated).
     * 
     * @param Graphics g - the standard graphics component
     */
    public void update(Graphics g)
    {
         paint(g);
    } 
    

    /**
     * Set the given image to current and redraw panel
     * 
     * @param Image i
     */
    public void setImage(BufferedImage i)
    {
        // Paint image object
        paint = true;
        image = i;
        this.repaint();
    }
}