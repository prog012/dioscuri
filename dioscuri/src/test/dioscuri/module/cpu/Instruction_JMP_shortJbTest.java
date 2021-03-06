package dioscuri.module.cpu;

import dioscuri.AbstractInstructionTest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * @author Bram Lohman\n@author Bart Kiers
 */
public class Instruction_JMP_shortJbTest extends AbstractInstructionTest {

    /**
     * @throws Exception
     */
    public Instruction_JMP_shortJbTest() throws Exception {
        super(80448, "JMP_shortJb.bin");
    }

    /*
    * Test method for 'com.tessella.emulator.module.cpu.Instruction_JMP_shortJb.execute()'
    */

    /**
     *
     */
    @Test
    public void testExecute() {
        String IP_ERROR = "IP contains wrong value";
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
        assertEquals(IP_ERROR, (byte) 0x01, cpu.getRegisterValue("IP")[0]);
        assertEquals(IP_ERROR, (byte) 0x00, cpu.getRegisterValue("IP")[1]);

        // Test JMP instruction
        cpu.startDebug(); // JMP +4
        assertEquals(IP_ERROR, (byte) 0x01, cpu.getRegisterValue("IP")[0]);
        assertEquals(IP_ERROR, (byte) 0x04, cpu.getRegisterValue("IP")[1]);
        cpu.startDebug(); // JMP -103
        assertEquals(IP_ERROR, (byte) 0x00, cpu.getRegisterValue("IP")[0]);
        assertEquals(IP_ERROR, (byte) 0x99, cpu.getRegisterValue("IP")[1]);
    }

}
