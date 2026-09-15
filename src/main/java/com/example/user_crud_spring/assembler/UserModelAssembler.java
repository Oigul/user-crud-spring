package com.example.user_crud_spring.assembler;

import com.example.user_crud_spring.controller.UserController;
import com.example.user_crud_spring.dtos.UserResponse;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class UserModelAssembler implements
        RepresentationModelAssembler<UserResponse, EntityModel<UserResponse>> {

    @Override
    public EntityModel<UserResponse> toModel(UserResponse response) {
        return EntityModel.of(response,
                linkTo(methodOn(UserController.class).getUserById(response.getId())).withSelfRel(),
                linkTo(methodOn(UserController.class).getAllUsers()).withRel("all-users"));
    }

    public CollectionModel<EntityModel<UserResponse>> toCollectionModel(List<UserResponse> responses) {
        List<EntityModel<UserResponse>> models = responses.stream()
                .map(this::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(models, linkTo(methodOn(UserController.class).getAllUsers()).withSelfRel());
    }
}
