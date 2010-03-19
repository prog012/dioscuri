//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0-b52-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2010.03.19 at 04:12:06 PM CET 
//


package dioscuri.config;

import javax.xml.bind.annotation.XmlRegistry;
import dioscuri.config.Emulator.Architecture;
import dioscuri.config.Emulator.Architecture.Modules;
import dioscuri.config.Emulator.Architecture.Modules.Ata;
import dioscuri.config.Emulator.Architecture.Modules.Ata.Harddiskdrive;
import dioscuri.config.Emulator.Architecture.Modules.Bios;
import dioscuri.config.Emulator.Architecture.Modules.Bios.Bootdrives;
import dioscuri.config.Emulator.Architecture.Modules.Cpu;
import dioscuri.config.Emulator.Architecture.Modules.Fdc;
import dioscuri.config.Emulator.Architecture.Modules.Fdc.Floppy;
import dioscuri.config.Emulator.Architecture.Modules.Keyboard;
import dioscuri.config.Emulator.Architecture.Modules.Memory;
import dioscuri.config.Emulator.Architecture.Modules.Mouse;
import dioscuri.config.Emulator.Architecture.Modules.Pit;
import dioscuri.config.Emulator.Architecture.Modules.Video;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the dioscuri.config package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: dioscuri.config
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Keyboard }
     * 
     */
    public Keyboard createEmulatorArchitectureModulesKeyboard() {
        return new Keyboard();
    }

    /**
     * Create an instance of {@link Fdc }
     * 
     */
    public Fdc createEmulatorArchitectureModulesFdc() {
        return new Fdc();
    }

    /**
     * Create an instance of {@link Video }
     * 
     */
    public Video createEmulatorArchitectureModulesVideo() {
        return new Video();
    }

    /**
     * Create an instance of {@link Pit }
     * 
     */
    public Pit createEmulatorArchitectureModulesPit() {
        return new Pit();
    }

    /**
     * Create an instance of {@link Bootdrives }
     * 
     */
    public Bootdrives createEmulatorArchitectureModulesBiosBootdrives() {
        return new Bootdrives();
    }

    /**
     * Create an instance of {@link Ata }
     * 
     */
    public Ata createEmulatorArchitectureModulesAta() {
        return new Ata();
    }

    /**
     * Create an instance of {@link Cpu }
     * 
     */
    public Cpu createEmulatorArchitectureModulesCpu() {
        return new Cpu();
    }

    /**
     * Create an instance of {@link Mouse }
     * 
     */
    public Mouse createEmulatorArchitectureModulesMouse() {
        return new Mouse();
    }

    /**
     * Create an instance of {@link Floppy }
     * 
     */
    public Floppy createEmulatorArchitectureModulesFdcFloppy() {
        return new Floppy();
    }

    /**
     * Create an instance of {@link Modules }
     * 
     */
    public Modules createEmulatorArchitectureModules() {
        return new Modules();
    }

    /**
     * Create an instance of {@link Bios }
     * 
     */
    public Bios createEmulatorArchitectureModulesBios() {
        return new Bios();
    }

    /**
     * Create an instance of {@link Harddiskdrive }
     * 
     */
    public Harddiskdrive createEmulatorArchitectureModulesAtaHarddiskdrive() {
        return new Harddiskdrive();
    }

    /**
     * Create an instance of {@link Emulator }
     * 
     */
    public Emulator createEmulator() {
        return new Emulator();
    }

    /**
     * Create an instance of {@link Architecture }
     * 
     */
    public Architecture createEmulatorArchitecture() {
        return new Architecture();
    }

    /**
     * Create an instance of {@link Memory }
     * 
     */
    public Memory createEmulatorArchitectureModulesMemory() {
        return new Memory();
    }

}
