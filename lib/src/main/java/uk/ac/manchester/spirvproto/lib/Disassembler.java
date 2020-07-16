package uk.ac.manchester.spirvproto.lib;

import uk.ac.manchester.spirvproto.lib.grammar.*;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class Disassembler {
	private final BinaryWordStream wordStream;
	private final PrintStream output;
	private final SPIRVHeader header;
	private final SPIRVGrammar grammar;
	private final SPIRVSyntaxHighlighter highlighter;
	private final boolean shouldHighlight;

	public Disassembler(BinaryWordStream wordStream, PrintStream output, boolean shouldHighlight) throws InvalidBinarySPIRVInputException, IOException {
		this.wordStream = wordStream;
		this.output = output;
		this.shouldHighlight = shouldHighlight;
		highlighter = new CLIHighlighter();

		int magicNumber = wordStream.getNextWord();
		if (magicNumber != 0x07230203) {
			if (magicNumber == 0x03022307) {
				wordStream.changeEndianness();
				magicNumber = 0x07230203;
			}
			else {
				throw new InvalidBinarySPIRVInputException(magicNumber);
			}
		}

		header = new SPIRVHeader(magicNumber,
				wordStream.getNextWord(),
				wordStream.getNextWord(),
				wordStream.getNextWord(),
				wordStream.getNextWord());

		grammar = SPIRVSpecification.buildSPIRVGrammar(header.majorVersion, header.minorVersion);
	}

	public void disassemble() throws IOException, InvalidSPIRVOpcodeException, InvalidSPIRVOperandKindException, InvalidSPIRVEnumerantException, InvalidSPIRVWordCountException {
		output.println(this.header);

		int currentWord;
		int opcode;
		int wordcount;
		int operandsLength;
		int currentWordCount;

		int result;
		String op;
		List<String> operands;

		SPIRVInstruction currentInstruction;

		while ((currentWord = wordStream.getNextWord()) != -1) {
			opcode = currentWord & 0xFFFF;
			wordcount = currentWord >> 16;
			currentInstruction = grammar.getInstructionByOpCode(opcode);
            operandsLength = (currentInstruction.operands != null) ? currentInstruction.operands.length : 0;
            op = currentInstruction.toString();



            result = -1;
			currentWordCount = 1;
			operands = new ArrayList<>();
			for (int i = 0; i < operandsLength && currentWordCount < wordcount; i++) {
			    SPIRVOperand currentOperand = currentInstruction.operands[i];
			    int operandCount = 1;
			    // If the quantifier is * that means this is the last operand and there could be 0 or more of it
				// It can be determined by the wordcount how many there is left
			    if (currentOperand.quantifier == '*') {
			    	operandCount = wordcount - currentWordCount;
				}

			    // This needs to be updated to account for optional operands
			    //if (operandCount >= wordcount) throw new InvalidSPIRVWordCountException(currentInstruction, operandsLength, wordcount);

			    if (currentOperand.kind.equals("IdResult")) {
			    	result = wordStream.getNextWord(); currentWordCount++;
				}
			    else {
					for (int j = 0; j < operandCount; j++) {
						currentWordCount += decodeOperand(operands, grammar.getOperandKind(currentOperand.kind));
					}
				}
			}

			//TODO better message and custom exception
            if (wordcount > currentWordCount) throw new RuntimeException("There are operands that were not decoded");

            int boundLength = (int) Math.log10(header.bound) + 1;
            int opStart = 4 + boundLength;

			if (result >= 0) {
				String targetID = "%" + result;
				if (shouldHighlight) targetID = highlighter.highlightID(targetID);
				targetID += " = ";
				output.print(targetID);
				opStart -= (int) Math.log10(result) + 5;
			}
			for (int i = 0; i < opStart; i++) {
				output.print(" ");
			}

			output.print(op);
			for (String operand : operands) {
				output.print(operand);
			}
			output.println();
		}
	}

	private int decodeOperand(List<String> decodedOperands, SPIRVOperandKind operandKind) throws IOException, InvalidSPIRVEnumerantException, InvalidSPIRVOperandKindException {
		int currentWordCount = 0;
		if (operandKind.kind.equals("LiteralString")) {
			StringBuilder sb = new StringBuilder(" \"");
			byte[] word;
			do {
				word = wordStream.getNextWordInBytes(true); currentWordCount++;
				String operandShard = new String(word);
				if (word[word.length - 1] == 0) {
					// Remove trailing zeros
					int index = -1;
					for (int i = word.length - 1; i >= 0 && index < 0; i--) {
						if (word[i] != 0) {
							index = i;
						}
					}
					operandShard = operandShard.substring(0, index + 1);
				}
				sb.append(operandShard);
			} while (word[word.length - 1] != 0);
			sb.append("\" ");
			String result = sb.toString();
			if (shouldHighlight) result = highlighter.highlightString(result);
			decodedOperands.add(result);
		}
		else if (operandKind.kind.startsWith("Id")) {
			String result = " %" + wordStream.getNextWord(); currentWordCount++;
			if (shouldHighlight) result = highlighter.highlightID(result);
			decodedOperands.add(result);
		}
		else if (operandKind.category.endsWith("Enum")) {
			String value;
			if (operandKind.category.startsWith("Value")) {
				value = Integer.toString(wordStream.getNextWord());
			}
			else {
				// For now it can only be a BitEnum
				value = String.format("0x%04x", wordStream.getNextWord());
			}
			currentWordCount++;
			SPIRVEnumerant enumerant = operandKind.getEnumerant(value);
			decodedOperands.add(" " + enumerant.name);
			if (enumerant.parameters != null) {
				for (int j = 0; j < enumerant.parameters.length; j++) {
					SPIRVOperandKind paramKind = grammar.getOperandKind(enumerant.parameters[j].kind);
					currentWordCount += decodeOperand(decodedOperands, paramKind);
				}
			}
		}
		else {
			// By now it can only be a LiteralInteger or a Composite
			// TODO: Composite type category decoding
			String result = " " + wordStream.getNextWord(); currentWordCount++;
			if (shouldHighlight) result = highlighter.highlightInteger(result);
			decodedOperands.add(result);
		}
		return currentWordCount;
	}

	@Override
	public String toString() {
		return "SPIR-V Disassembler";
	}
}