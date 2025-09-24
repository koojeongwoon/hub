package com.tinyquest.hub.user.api.assembler;

import com.tinyquest.hub.user.api.controller.UserRestController;
import com.tinyquest.hub.user.api.dto.response.UserDetailResponse;
import com.tinyquest.hub.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Slf4j
@Component
public class UserDetailAssembler implements RepresentationModelAssembler<UserDetailResponse, EntityModel<UserDetailResponse>> {

    @Override
    public EntityModel<UserDetailResponse> toModel(UserDetailResponse entity) throws AccessDeniedException {
        var model = EntityModel.of(entity);
        model.add(
                linkTo(methodOn(UserRestController.class).get()).withSelfRel()
        );
        return model;
    }

    @Override
    public CollectionModel<EntityModel<UserDetailResponse>> toCollectionModel(Iterable<? extends UserDetailResponse> entities) {
        return RepresentationModelAssembler.super.toCollectionModel(entities);
    }
}
