package com.ntrs.ani;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.regex.Pattern;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = { "spring.profiles.active=local,system" })
@SpringBootConfiguration
public class WebApplicationTests {

	private String pattern = "[0-9a-f\\-]*";
	
	@Test
	public void patternTests() {
		assertTrue(Pattern.matches(pattern, "840c2cd2-2fe5-44ca-9173-58d7985f6e81"));
		assertFalse(Pattern.matches(pattern, "/abc"));
		assertFalse(Pattern.matches(pattern, "<abc>"));
		assertFalse(Pattern.matches(pattern, "abc$"));
		assertFalse(Pattern.matches(pattern, "a#f"));
		assertFalse(Pattern.matches(pattern, "&ab;"));
	}

}
