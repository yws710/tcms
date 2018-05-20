package com.yws.tcms.string;

import java.util.Date;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yws.tcms.domain.User;

public class StringTest {

	@Test
	public void test1() {
		User user = new User();
		user.setName("zhangsan");
		user.setHireDate(new Date());
		System.out.println(ReflectionToStringBuilder.toString(user, ToStringStyle.MULTI_LINE_STYLE));
		ObjectMapper mapper = new ObjectMapper();
		try {
			String result = mapper.writeValueAsString(user);
			System.out.println(result);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
