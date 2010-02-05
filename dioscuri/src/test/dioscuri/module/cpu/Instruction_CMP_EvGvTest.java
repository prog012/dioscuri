package dioscuri.module.cpu;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;


import dioscuri.*;
import dioscuri.module.memory.*;

import org.junit.*;

import static org.junit.Assert.*;

public class Instruction_CMP_EvGvTest {

    Emulator emu = null;
    CPU cpu = null;
    Memory mem = null;

    int startAddress = 80448;
    String testASMfilename = "test/asm/CMP_EvGv.bin";


    @Before
    protected void setUp() throws Exception {
        emu = new Emulator(new DummyGUI());
        cpu = new CPU(emu);
        mem = new Memory(emu);
        cpu.setConnection(mem);
        cpu.setDebugMode(true);

        BufferedInputStream bis = new BufferedInputStream(new DataInputStream(new FileInputStream(new File(testASMfilename))));
        byte[] byteArray = new byte[bis.available()];
        bis.read(byteArray, 0, byteArray.length);
        bis.close();

        mem.setBytes(startAddress, byteArray);
    }


    /*
    * Test method for 'com.tessella.emulator.module.cpu.Instruction_CMP_EvGv.execute()'
    */
    @Test
    public void testExecute() {
        String AX_ERROR = "AX contains wrong value";
        String CX_ERROR = "CX contains wrong value";
        String DX_ERROR = "DX contains wrong value";
        String BP_ERROR = "BP contains wrong value";
        String OF_ERROR = "OF incorrect";
        String SF_ERROR = "SF incorrect";
        String ZF_ERROR = "ZF incorrect";
        String AF_ERROR = "AF incorrect";
        String PF_ERROR = "PF incorrect";
        String CF_ERROR = "CF incorrect";

        // Load memory, registers with pre-arranged values (6 instructions)
        cpu.startDebug(); // MOV [0000], AX
        cpu.startDebug(); // MOV AX, 0x8101
        cpu.startDebug(); // MOV [0002], AX
        cpu.startDebug(); // MOV AX, 0x8000
        assertEquals(AX_ERROR, cpu.getRegisterValue("AX")[0], (byte) 0x80);
        assertEquals(AX_ERROR, cpu.getRegisterValue("AX")[1], (byte) 0x00);
        cpu.startDebug(); // MOV CX, 0x0102
        assertEquals(CX_ERROR, cpu.getRegisterValue("CX")[0], (byte) 0x01);
        assertEquals(CX_ERROR, cpu.getRegisterValue("CX")[1], (byte) 0x02);
        cpu.startDebug(); // MOV DX, 0x8102        
        assertEquals(DX_ERROR, cpu.getRegisterValue("DX")[0], (byte) 0x81);
        assertEquals(DX_ERROR, cpu.getRegisterValue("DX")[1], (byte) 0x02);

        assertFalse(OF_ERROR, cpu.getFlagValue('O'));
        assertFalse(SF_ERROR, cpu.getFlagValue('S'));
        assertFalse(ZF_ERROR, cpu.getFlagValue('Z'));
        assertFalse(AF_ERROR, cpu.getFlagValue('A'));
        assertFalse(PF_ERROR, cpu.getFlagValue('P'));
        assertFalse(CF_ERROR, cpu.getFlagValue('C'));

        // Test CMP
        cpu.startDebug(); // CMP [BX+SI], AX ; 0000 - 8000, test OF, SF, CF
        assertTrue(OF_ERROR, cpu.getFlagValue('O'));
        assertTrue(SF_ERROR, cpu.getFlagValue('S'));
        assertTrue(CF_ERROR, cpu.getFlagValue('C'));

        cpu.startDebug(); // CMP [BX+DI+02], CX      ; 8101 - 0102, test OF, SF, AF, PF flags
        assertTrue(OF_ERROR, cpu.getFlagValue('O'));
        assertFalse(SF_ERROR, cpu.getFlagValue('S'));
        assertTrue(AF_ERROR, cpu.getFlagValue('A'));
        assertTrue(PF_ERROR, cpu.getFlagValue('P'));
        assertFalse(CF_ERROR, cpu.getFlagValue('C'));

        cpu.startDebug(); // MOV AX, 0x8101
        assertEquals(AX_ERROR, cpu.getRegisterValue("AX")[0], (byte) 0x81);
        assertEquals(AX_ERROR, cpu.getRegisterValue("AX")[1], (byte) 0x01);
        cpu.startDebug(); // MOV BP, 0x0004          ; Set BP to 4
        assertEquals(BP_ERROR, cpu.getRegisterValue("BP")[1], (byte) 0x04);
        cpu.startDebug(); // CMP [BP+0x0100], AX     ; 8101 - 8101, test ZF
        assertFalse(OF_ERROR, cpu.getFlagValue('O'));
        assertFalse(SF_ERROR, cpu.getFlagValue('S'));
        assertTrue(ZF_ERROR, cpu.getFlagValue('Z'));
        assertFalse(AF_ERROR, cpu.getFlagValue('A'));
        assertTrue(PF_ERROR, cpu.getFlagValue('P'));

        cpu.startDebug(); // CMP [BP+0x0100], DX     ; Test AF, CF, overflow
        assertFalse(OF_ERROR, cpu.getFlagValue('O'));
        assertTrue(AF_ERROR, cpu.getFlagValue('A'));
        assertTrue(CF_ERROR, cpu.getFlagValue('C'));

        cpu.startDebug(); // MOV CX, 0x7F01
        assertEquals(CX_ERROR, cpu.getRegisterValue("CX")[0], (byte) 0x7F);
        assertEquals(CX_ERROR, cpu.getRegisterValue("CX")[1], (byte) 0x01);
        cpu.startDebug(); // MOV DX, 0xFF01
        assertEquals(DX_ERROR, cpu.getRegisterValue("DX")[0], (byte) 0xFF);
        assertEquals(DX_ERROR, cpu.getRegisterValue("DX")[1], (byte) 0x01);

        cpu.startDebug(); // CMP CX, DX      ; 7F01 - FF01, test OF, CF
        assertTrue(OF_ERROR, cpu.getFlagValue('O'));
        assertTrue(CF_ERROR, cpu.getFlagValue('C'));

        cpu.startDebug(); // HLT         
    }
}