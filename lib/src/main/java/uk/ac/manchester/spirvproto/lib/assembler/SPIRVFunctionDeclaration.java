package uk.ac.manchester.spirvproto.lib.assembler;

import uk.ac.manchester.spirvproto.lib.instructions.*;
import uk.ac.manchester.spirvproto.lib.instructions.operands.SPIRVFunctionControl;
import uk.ac.manchester.spirvproto.lib.instructions.operands.SPIRVId;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

public class SPIRVFunctionDeclaration {
    protected SPIRVFunctionInst functionDeclaration;
    protected List<SPIRVFunctionParameterInst> parameters;
    protected SPIRVFunctionEndInst end;

    public SPIRVFunctionDeclaration(SPIRVId resultType,
                                    SPIRVId funcType,
                                    SPIRVId result,
                                    SPIRVFunctionControl control,
                                    SPIRVFunctionParameterInst... params) {
        functionDeclaration = new SPIRVOpFunction(resultType, result, control, funcType);
        parameters = Arrays.asList(params);
        end = new SPIRVOpFunctionEnd();
    }

    public void write(ByteBuffer output) throws InvalidSPIRVModuleException {
        functionDeclaration.write(output);
        parameters.forEach(p -> p.write(output));
        end.write(output);
    }

    public int getWordCount() {
        int wordCount = functionDeclaration.getWordCount() + end.getWordCount();
        wordCount += parameters.stream().mapToInt(SPIRVInstruction::getWordCount).sum();

        return wordCount;
    }
}
