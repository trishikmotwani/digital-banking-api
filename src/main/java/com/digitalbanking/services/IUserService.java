package com.digitalbanking.services;

import java.util.List;

import com.digitalbanking.dtos.UserDto;
import com.digitalbanking.entities.UserEntity;

public interface IUserService {

	public String login(String username, String password);
	public void logout(String username);
	public UserDto registerUser(UserEntity user);
	public String resetPassword(String username);
	public List<UserDto> getAllUsers();
}
