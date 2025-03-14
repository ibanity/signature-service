package com.ibanity.signatures.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class DigestSignaturePartTest {

    @Test
    public void value_withoutSpecialCharacters() {
        String payload = "test payload";
        DigestSignaturePart digestSignaturePart = new DigestSignaturePart(payload);

        String result = digestSignaturePart.value();

        String expectedHash = "SHA-512=hFbAm884LpT8E1I+sI2hcVojcCcUf+ylT5Yr34U3qRfH//2nAt7G1NFYmqGTDpRPnL1xZnMxIrA9/xuatbuZXQ==";
        assertEquals(expectedHash, result);
    }

    @Test
    public void value_withSpecialCharacters() {
        String payload = 
        """
            {
                "key1": "value1",
                "key2": "value2"
            }
        """;
        DigestSignaturePart digestSignaturePart = new DigestSignaturePart(payload);

        String result = digestSignaturePart.value();

        String expectedHash = "SHA-512=FW3kJ1OJK0Xv7FF8TalJ849OMLXX7DdWtDuIjPLx6NgTAgUewKIDdIXTOf/VNWr+3sqV95qXuNX0yhd3Q+UC6A==";
        assertEquals(expectedHash, result);
    }

}
