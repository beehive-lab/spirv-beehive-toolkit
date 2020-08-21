package uk.ac.manchester.spirvproto.lib.instructions.operands;

import uk.ac.manchester.spirvproto.lib.disassembler.SPIRVPrintingOptions;

import java.io.PrintStream;
import java.nio.ByteBuffer;

public interface SPIRVOperand {
    void write(ByteBuffer output);
    int getWordCount();
    SPIRVCapability[] getCapabilities();

    void print(PrintStream output, SPIRVPrintingOptions options);
}
