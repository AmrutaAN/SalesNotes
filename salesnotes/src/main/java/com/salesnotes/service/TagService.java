package com.salesnotes.service;

import com.salesnotes.dto.TagRequestDto;
import com.salesnotes.dto.TagResponseDto;
import com.salesnotes.entity.Organisation;
import com.salesnotes.entity.Tag;
import com.salesnotes.repository.OrganisationRepository;
import com.salesnotes.repository.TagRepository;
import com.salesnotes.security.UserPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TagService {

    private final TagRepository tagRepository;
    private final OrganisationRepository organisationRepository;

    public TagService(TagRepository tagRepository, OrganisationRepository organisationRepository) {
        this.tagRepository = tagRepository;
        this.organisationRepository = organisationRepository;
    }

    @Transactional(readOnly = true)
    public List<TagResponseDto> listTags(UserPrincipal currentUser) {
        String orgId = currentUser.getOrganisationId();
        List<Tag> tags;
        if (orgId != null) {
            tags = tagRepository.findByOrganisationIdAndIsActiveTrueOrderBySortOrderAsc(orgId);
        } else {
            tags = tagRepository.findAll().stream()
                    .filter(t -> Boolean.TRUE.equals(t.getIsActive()))
                    .collect(Collectors.toList());
        }
        return tags.stream().map(TagResponseDto::fromEntity).collect(Collectors.toList());
    }

    public TagResponseDto createTag(TagRequestDto request, UserPrincipal currentUser) {
        Organisation org = null;
        if (currentUser.getOrganisationId() != null) {
            org = organisationRepository.findById(currentUser.getOrganisationId()).orElse(null);
        }
        if (org == null) {
            org = organisationRepository.findAll().stream().findFirst()
                    .orElseThrow(() -> new IllegalStateException("Organisation not found"));
        }

        if (tagRepository.existsByOrganisationIdAndCode(org.getId(), request.getCode().trim().toUpperCase())) {
            throw new IllegalArgumentException("Tag with code " + request.getCode() + " already exists in this organisation");
        }

        Tag tag = new Tag(org, request.getCode().trim().toUpperCase(), request.getLabel().trim(), request.getColorHex());
        tag.setDescription(request.getDescription());
        tag.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0);
        tag.setIsSystem(false);
        tag.setIsActive(true);

        return TagResponseDto.fromEntity(tagRepository.save(tag));
    }
}
