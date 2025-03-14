package com.ibanity.signatures;

import java.security.PrivateKey;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@ActiveProfiles("test")
class SignatureServiceApplicationTests {

	@MockitoBean
    PrivateKey privateKey;

	@Test
	void contextLoads() {
	}

}
