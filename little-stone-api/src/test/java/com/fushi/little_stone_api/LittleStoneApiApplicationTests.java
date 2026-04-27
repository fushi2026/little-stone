package com.fushi.little_stone_api;

import com.fushi.entity.User;
import com.fushi.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class LittleStoneApiApplicationTests {
	@Autowired
	private UserMapper userMapper;

	@Test
	void testMybatisPlus() {
		List<User> users = userMapper.selectList(null);
		users.forEach(System.out::println);
	}

}
