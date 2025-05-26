package ru.practicum.mapper;

import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.NewCompilationDto;
import ru.practicum.model.Compilation;

import java.util.ArrayList;

public class CompilationMapper {

    public static CompilationDto toCompilationDto(Compilation compilation) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .pinned(compilation.getPinned())
                .title(compilation.getTitle())
                .events(compilation.getEvents() != null ? compilation
                        .getEvents().stream().map(EventMapper::toShortDto).toList() : new ArrayList<>())
                .build();
    }

    public static Compilation toCompilation(NewCompilationDto compilation) {
        return Compilation.builder()
                .pinned(compilation.getPinned() != null ? compilation.getPinned() : false)
                .title(compilation.getTitle())
                .build();
    }
}
