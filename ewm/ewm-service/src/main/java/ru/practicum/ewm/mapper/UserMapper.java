package ru.practicum.ewm.mapper;

import lombok.experimental.UtilityClass;

import ru.practicum.ewm.model.User;
import ru.practicum.ewm.dto.user.UserDto;
import ru.practicum.ewm.dto.user.UserShortDto;
import ru.practicum.ewm.dto.user.NewUserRequest;

@UtilityClass
public class UserMapper {

    // 22 Добавление нового пользователя
    public User toEntity(NewUserRequest request) {
        if (request == null) return null;

        return User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .build();
    }

    // Entity -> UserDto
    public UserDto toDto(User user) {
        if (user == null) return null;

        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    // Entity -> UserShortDto
    public UserShortDto toShortDto(User user) {
        if (user == null) return null;

        return UserShortDto.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }
}
