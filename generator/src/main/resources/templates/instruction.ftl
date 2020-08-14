package uk.ac.manchester.spirvproto.lib.instructions;

import uk.ac.manchester.spirvproto.lib.disassembler.SPIRVPrintingOptions;
import uk.ac.manchester.spirvproto.lib.instructions.operands.*;

import javax.annotation.Generated;
import java.io.PrintStream;
import java.nio.ByteBuffer;

@Generated("beehive-lab.spirv-proto.generator")
public class SPIRV${name} extends ${superClass} {
    <#if operands??>
    <#list operands as operand>
    <#if operand.quantifier == '*'>
    private final SPIRVMultipleOperands<SPIRV${operand.kind}> ${operand.name};
    <#elseif operand.quantifier == '?'>
    private final SPIRVOptionalOperand<SPIRV${operand.kind}> ${operand.name};
    <#else>
    private final SPIRV${operand.kind} ${operand.name};
    </#if>
    </#list>
    </#if>

    public SPIRV${name}(<#if operands??><#list  operands as operand><#if operand.quantifier == '*'>SPIRVMultipleOperands<<#elseif operand.quantifier == '?'>SPIRVOptionalOperand<</#if>SPIRV${operand.kind}<#if operand.quantifier == '*' || operand.quantifier == '?'>></#if> ${operand.name}<#sep>, </#list></#if>) {
        super(${opCode?string.computer}, <#if operands??><#list  operands as operand>${operand.name}.getWordCount() + </#list></#if>1, "${name}");
        <#if operands??>
        <#list operands as operand>
        this.${operand.name} = ${operand.name};
        </#list>
        </#if>
    }

    @Override
    protected void writeOperands(ByteBuffer output) {
        <#if operands??>
        <#list operands as operand>
        ${operand.name}.write(output);
        </#list>
        </#if>
    }

    @Override
    protected void printOperands(PrintStream output, SPIRVPrintingOptions options) {
        <#if operands??>
        <#list operands as operand> <#if operand.name != "_idResult">
        ${operand.name}.print(output, options);<#sep>
        output.print(" ");</#sep>
        </#if></#list>
        </#if>
    }

    @Override
    protected void printResultAssignment(PrintStream output, SPIRVPrintingOptions options) {
        <#if hasResult>
        _idResult.print(output, options);
        output.print(" = ");
        </#if>
    }

    @Override
    public int getResultAssigmentSize() {
        <#if hasResult>
        return _idResult.nameSize() + 3;
        <#else>
        return 0;
        </#if>
    }
}
