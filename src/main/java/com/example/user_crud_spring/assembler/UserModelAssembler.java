package com.example.user_crud_spring.assembler;

import com.example.user_crud_spring.controller.UserController;
import com.example.user_crud_spring.dtos.UserDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class UserModelAssembler implements
        RepresentationModelAssembler<UserDTO, EntityModel<UserDTO>> {

    @Override
    public EntityModel<UserDTO> toModel(UserDTO dto) {
        return EntityModel.of(dto,
                linkTo(methodOn(UserController.class).getUserById(dto.getId())).withSelfRel(),
                linkTo(methodOn(UserController.class).getAllUsers()).withRel("all-users"));
    }

    public List<EntityModel<UserDTO>> toModelList(List<UserDTO> dtos) {
        return dtos.stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }
}
