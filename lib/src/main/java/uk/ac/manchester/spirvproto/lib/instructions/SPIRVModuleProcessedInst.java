package uk.ac.manchester.spirvproto.lib.instructions;

public abstract class SPIRVModuleProcessedInst extends SPIRVDebugInst {
        protected SPIRVModuleProcessedInst(int opCode, int wordCount) {
        super(opCode, wordCount);
    }
}