/* $Revision: 159 $ $Date: 2009-08-17 12:52:56 +0000 (ma, 17 aug 2009) $ $Author: blohman $ 
 * 
 * Copyright (C) 2007-2009  National Library of the Netherlands, 
 *                          Nationaal Archief of the Netherlands, 
 *                          Planets
 *                          KEEP
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 *
 * For more information about this project, visit
 * http://dioscuri.sourceforge.net/
 * or contact us via email:
 *   jrvanderhoeven at users.sourceforge.net
 *   blohman at users.sourceforge.net
 *   bkiers at users.sourceforge.net
 * 
 * Developed by:
 *   Nationaal Archief               <www.nationaalarchief.nl>
 *   Koninklijke Bibliotheek         <www.kb.nl>
 *   Tessella Support Services plc   <www.tessella.com>
 *   Planets                         <www.planets-project.eu>
 *   KEEP                            <www.keep-project.eu>
 * 
 * Project Title: DIOSCURI
 */

/*
 * Information used in this module was taken from:
 * - http://en.wikipedia.org/wiki/AT_Attachment
 * - http://bochs.sourceforge.net/techspec/IDE-reference.txt
 */
package dioscuri.module.ata;

/**
 * @author Bram Lohman
 * @author Bart Kiers
 */
public class InterruptReason {
    private int cd = 1;
    private int io = 1;
    private int rel = 1;
    private int tag = 5;

    /**
     * @return -
     */
    public int getCd() {
        return cd;
    }

    /**
     * @param cd
     */
    public void setCd(int cd) {
        this.cd = cd;
    }

    /**
     * @return -
     */
    public int getIo() {
        return io;
    }

    /**
     * @param io
     */
    public void setIo(int io) {
        this.io = io;
    }

    /**
     * @return -
     */
    public int getRel() {
        return rel;
    }

    /**
     * @param rel
     */
    public void setRel(int rel) {
        this.rel = rel;
    }

    /**
     * @return -
     */
    public int getTag() {
        return tag;
    }

    /**
     * @param tag
     */
    public void setTag(int tag) {
        this.tag = tag;
    }

    // Original BOCHS code
    // TODO:
    /**
     * #ifdef BX_LITTLE_ENDIAN unsigned c_d : 1; unsigned i_o : 1; unsigned rel
     * : 1; unsigned tag : 5; #else /* BX_BIG_ENDIAN
     */
    /*
     * unsigned tag : 5; unsigned rel : 1; unsigned i_o : 1; unsigned c_d : 1;
     * #endif
     */

}
