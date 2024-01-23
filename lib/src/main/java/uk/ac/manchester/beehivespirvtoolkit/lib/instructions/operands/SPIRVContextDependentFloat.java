/*
 * MIT License
 *
 * Copyright (c) 2021, APT Group, Department of Computer Science,
 * The University of Manchester.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package uk.ac.manchester.beehivespirvtoolkit.lib.instructions.operands;

import uk.ac.manchester.beehivespirvtoolkit.lib.disassembler.SPIRVPrintingOptions;

import java.io.PrintStream;
import java.nio.ByteBuffer;

public class SPIRVContextDependentFloat extends SPIRVLiteralContextDependentNumber {
    private final float value;
    private boolean isHalf;

    public SPIRVContextDependentFloat(float value) {
        this.value = value;
    }

    public void setHalfFlag() {
        isHalf = true;
    }

    @Override
    public void write(ByteBuffer output) {
        output.putFloat(value);
    }

    @Override
    public int getWordCount() {
        return 1;
    }

    @Override
    public void print(PrintStream output, SPIRVPrintingOptions options) {
        if (isHalf) {
            output.print(Float.toHexString(value));
        } else {
            String number;
            if (value == (int) value) {
                number = String.format("%d", (int) value);
            } else {
                number = String.format("%s", value);
            }
            output.print(number);
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof SPIRVContextDependentFloat) return this.value == ((SPIRVContextDependentFloat) other).value;
        else return super.equals(other);
    }
}
