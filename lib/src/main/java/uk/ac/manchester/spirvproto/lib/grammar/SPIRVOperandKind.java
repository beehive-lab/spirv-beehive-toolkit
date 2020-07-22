package uk.ac.manchester.spirvproto.lib.grammar;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SPIRVOperandKind {
    @JsonProperty("category")
    public String category;

    @JsonProperty("kind")
    public String kind;

    @JsonProperty("enumerants")
    public SPIRVEnumerant[] enumerants;

    public SPIRVEnumerant getEnumerant(String value) throws InvalidSPIRVEnumerantException {
        for (SPIRVEnumerant enumerant: getEnumerants()) {
            if (enumerant.getValue().equals(value)) {
                return enumerant;
            }
        }

        throw new InvalidSPIRVEnumerantException(getKind(), value);
    }

    public String getCategory() {
        return category;
    }

    public String getKind() {
        return kind;
    }

    public SPIRVEnumerant[] getEnumerants() {
        return enumerants;
    }
}
