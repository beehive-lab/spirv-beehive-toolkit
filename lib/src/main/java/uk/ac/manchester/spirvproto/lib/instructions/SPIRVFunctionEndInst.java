package uk.ac.manchester.spirvproto.lib.instructions;

public abstract class SPIRVFunctionEndInst extends SPIRVInstruction {
    protected SPIRVFunctionEndInst(int opCode, int wordCount, String name) {
        super(opCode, wordCount, name);
    }
}
