package uk.ac.manchester.spirvproto.lib.instructions;

public abstract class SPIRVAnnotationInst extends SPIRVInstruction {
    protected SPIRVAnnotationInst(int opCode, int wordCount) {
        super(opCode, wordCount);
    }
}