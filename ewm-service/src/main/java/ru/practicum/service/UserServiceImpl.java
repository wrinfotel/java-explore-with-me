package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.dto.NewUserRequest;
import ru.practicum.dto.UserDto;
import ru.practicum.exception.ConflictException;
import ru.practicum.mapper.UserMapper;
import ru.practicum.model.User;
import ru.practicum.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<UserDto> getAllUsers(List<Long> ids, Integer from, Integer size) {
        Pageable page = PageRequest.of(from / size, size);
        if (ids != null && !ids.isEmpty()) {
            return userRepository.findAllById(ids).stream().map(UserMapper::toUserDto).toList();
        }
        return userRepository.findAll(page).stream().map(UserMapper::toUserDto).toList();

    }

    @Override
    public UserDto createUser(NewUserRequest newUserRequest) {
        boolean checkUser = userRepository.findAll().stream().anyMatch(user1 -> user1.getEmail()
                .equals(newUserRequest.getEmail()));
        if (checkUser) {
            throw new ConflictException("Integrity constraint has been violated.");
        }

        User createdUser = userRepository.save(UserMapper.toUser(newUserRequest));
        return UserMapper.toUserDto(createdUser);
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }
}
