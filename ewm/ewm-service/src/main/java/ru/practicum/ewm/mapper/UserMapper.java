package ru.practicum.ewm.mapper;

import ru.practicum.ewm.model.User;
import ru.practicum.ewm.dto.user.UserDto;
import ru.practicum.ewm.dto.user.UserShortDto;
import ru.practicum.ewm.dto.user.NewUserRequest;

public class UserMapper {

    // 22 Добавление нового пользователя
    public static User toEntity(NewUserRequest request) {
        if (request == null) return null;

        return User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .build();
    }

    // Entity -> UserDto
    public static UserDto toDto(User user) {
        if (user == null) return null;

        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    // Entity -> UserShortDto
    public static UserShortDto toShortDto(User user) {
        if (user == null) return null;

        return UserShortDto.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }
}
