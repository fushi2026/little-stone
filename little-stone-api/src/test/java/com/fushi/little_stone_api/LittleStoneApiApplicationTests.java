package com.fushi.little_stone_api;

import com.fushi.entity.SysUser;
import com.fushi.mapper.SysUserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class LittleStoneApiApplicationTests {
	@Autowired
	private SysUserMapper userMapper;

	@Test
	void testMybatisPlus() {
		List<SysUser> users = userMapper.selectList(null);
		users.forEach(System.out::println);
	}

}
