package dioscuri.module.cpu;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;


import dioscuri.*;
import dioscuri.module.memory.*;

import org.junit.*;

import static org.junit.Assert.*;

public class Instruction_INC_BPTest {

    Emulator emu = null;
    CPU cpu = null;
    Memory mem = null;

    int startAddress = 80448;
    String testASMfilename = "test/asm/INC_BP.bin";


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
    * Test method for 'com.tessella.emulator.module.cpu.Instruction_DEC_BP.execute()'
    */
    @Test
    public void testExecute() {
        String BP_ERROR = "BP contains wrong value";
        String OF_ERROR = "OF incorrect";
        String SF_ERROR = "SF incorrect";
        String ZF_ERROR = "ZF incorrect";
        String AF_ERROR = "AF incorrect";
        String PF_ERROR = "PF incorrect";
        String CF_ERROR = "CF incorrect";

        assertFalse(OF_ERROR, cpu.getFlagValue('O'));
        assertFalse(SF_ERROR, cpu.getFlagValue('S'));
        assertFalse(ZF_ERROR, cpu.getFlagValue('Z'));
        assertFalse(AF_ERROR, cpu.getFlagValue('A'));
        assertFalse(PF_ERROR, cpu.getFlagValue('P'));
        assertFalse(CF_ERROR, cpu.getFlagValue('C'));

        // Test INC instruction
        cpu.startDebug(); // MOV al, 0x0F    ; Prepare for AF
        assertEquals(BP_ERROR, cpu.getRegisterValue("BP")[1], (byte) 0x0F);
        cpu.startDebug(); // INC bp          ; Increment BP, test AF
        assertTrue(AF_ERROR, cpu.getFlagValue('A'));

        cpu.startDebug(); // MOV bp, 0x7FFF  ; Prepare for OF
        assertEquals(BP_ERROR, cpu.getRegisterValue("BP")[0], (byte) 0x7F);
        assertEquals(BP_ERROR, cpu.getRegisterValue("BP")[1], (byte) 0xFF);
        cpu.startDebug(); // INC bp          ; Increment BP, test OF, SF
        assertTrue(OF_ERROR, cpu.getFlagValue('O'));
        assertTrue(SF_ERROR, cpu.getFlagValue('S'));
        assertTrue(AF_ERROR, cpu.getFlagValue('A'));
        assertTrue(PF_ERROR, cpu.getFlagValue('P'));

        cpu.startDebug(); // MOV bp, 0xFFFF  ; Prepare for ZF
        assertEquals(BP_ERROR, cpu.getRegisterValue("BP")[0], (byte) 0xFF);
        assertEquals(BP_ERROR, cpu.getRegisterValue("BP")[1], (byte) 0xFF);
        cpu.startDebug(); // INC bp          ; Increment BP, test ZF
        assertFalse(OF_ERROR, cpu.getFlagValue('O'));
        assertFalse(SF_ERROR, cpu.getFlagValue('S'));
        assertTrue(ZF_ERROR, cpu.getFlagValue('Z'));
        assertTrue(AF_ERROR, cpu.getFlagValue('A'));

        cpu.startDebug(); // INC bp          ; Increment BP, test !AF
        assertFalse(ZF_ERROR, cpu.getFlagValue('Z'));
        assertFalse(AF_ERROR, cpu.getFlagValue('A'));

    }

}