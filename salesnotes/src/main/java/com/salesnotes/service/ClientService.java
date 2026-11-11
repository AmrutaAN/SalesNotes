package com.salesnotes.service;

import com.salesnotes.dto.ClientRequestDto;
import com.salesnotes.dto.ClientResponseDto;
import com.salesnotes.dto.ClientSummaryDto;
import com.salesnotes.entity.*;
import com.salesnotes.repository.*;
import com.salesnotes.security.UserPrincipal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ClientService {

    private final ClientRepository clientRepository;
    private final ClientNoteRepository clientNoteRepository;
    private final LogEntryRepository logEntryRepository;
    private final UserRepository userRepository;
    private final OrganisationRepository organisationRepository;

    public ClientService(
            ClientRepository clientRepository,
            ClientNoteRepository clientNoteRepository,
            LogEntryRepository logEntryRepository,
            UserRepository userRepository,
            OrganisationRepository organisationRepository
    ) {
        this.clientRepository = clientRepository;
        this.clientNoteRepository = clientNoteRepository;
        this.logEntryRepository = logEntryRepository;
        this.userRepository = userRepository;
        this.organisationRepository = organisationRepository;
    }

    @Transactional(readOnly = true)
    public Page<ClientResponseDto> listClients(UserPrincipal currentUser, String search, Boolean priority, Pageable pageable) {
        List<Client> allClients;
        boolean isEmployee = currentUser.getRole() == UserRole.COMPANYEMPLOYEE;

        if (isEmployee) {
            allClients = clientRepository.findByAssignedRepIdAndDeletedFalse(currentUser.getId());
        } else if (currentUser.getOrganisationId() != null) {
            allClients = clientRepository.findByOrganisationIdAndDeletedFalse(currentUser.getOrganisationId());
        } else {
            allClients = clientRepository.findAll().stream()
                    .filter(c -> !Boolean.TRUE.equals(c.getDeleted()))
                    .collect(Collectors.toList());
        }

        // Apply in-memory search and priority filter
        List<ClientResponseDto> filtered = allClients.stream()
                .filter(c -> {
                    if (search == null || search.trim().isEmpty()) return true;
                    String term = search.trim().toLowerCase();
                    boolean nameMatch = c.getName() != null && c.getName().toLowerCase().contains(term);
                    boolean companyMatch = c.getCompany() != null && c.getCompany().toLowerCase().contains(term);
                    boolean industryMatch = c.getIndustryTag() != null && c.getIndustryTag().toLowerCase().contains(term);
                    return nameMatch || companyMatch || industryMatch;
                })
                .filter(c -> {
                    if (priority == null) return true;
                    return priority.equals(c.getPriorityFlag());
                })
                .map(ClientResponseDto::fromEntity)
                .collect(Collectors.toList());

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), filtered.size());
        List<ClientResponseDto> pageContent = start <= end ? filtered.subList(start, end) : List.of();

        return new PageImpl<>(pageContent, pageable, filtered.size());
    }

    @Transactional(readOnly = true)
    public ClientResponseDto getClientById(String id, UserPrincipal currentUser) {
        Client client = findAndAuthorizeClient(id, currentUser);
        return ClientResponseDto.fromEntity(client);
    }

    public ClientResponseDto createClient(ClientRequestDto request, UserPrincipal currentUser) {
        // Resolve organization
        Organisation organisation = null;
        if (currentUser.getOrganisationId() != null) {
            organisation = organisationRepository.findById(currentUser.getOrganisationId()).orElse(null);
        }
        if (organisation == null) {
            organisation = organisationRepository.findAll().stream().findFirst()
                    .orElseThrow(() -> new IllegalStateException("No default organisation found"));
        }

        // Resolve assigned sales rep
        User assignedRep;
        boolean isAdmin = currentUser.getRole() == UserRole.SYSADMIN || currentUser.getRole() == UserRole.COMPANYADMIN;
        if (isAdmin && request.getAssignedRepId() != null && !request.getAssignedRepId().trim().isEmpty()) {
            assignedRep = userRepository.findById(request.getAssignedRepId())
                    .orElseThrow(() -> new IllegalArgumentException("Assigned rep not found: " + request.getAssignedRepId()));
        } else {
            assignedRep = userRepository.findById(currentUser.getId())
                    .orElseThrow(() -> new IllegalStateException("Current user record not found"));
        }

        Client client = new Client(organisation, assignedRep, request.getName().trim(), request.getCompany());
        client.setPhone(request.getPhone());
        client.setEmail(request.getEmail());
        client.setAddress(request.getAddress());
        client.setIndustryTag(request.getIndustryTag());
        client.setPriorityFlag(Boolean.TRUE.equals(request.getPriorityFlag()));
        client.setNotesCount(0);

        Client savedClient = clientRepository.save(client);

        // Automatically create 1-to-1 persistent ClientNote container
        ClientNote note = new ClientNote(savedClient, organisation);
        savedClient.setClientNote(clientNoteRepository.save(note));

        return ClientResponseDto.fromEntity(savedClient);
    }

    public ClientResponseDto updateClient(String id, ClientRequestDto request, UserPrincipal currentUser) {
        Client client = findAndAuthorizeClient(id, currentUser);

        client.setName(request.getName().trim());
        if (request.getCompany() != null) client.setCompany(request.getCompany());
        if (request.getPhone() != null) client.setPhone(request.getPhone());
        if (request.getEmail() != null) client.setEmail(request.getEmail());
        if (request.getAddress() != null) client.setAddress(request.getAddress());
        if (request.getIndustryTag() != null) client.setIndustryTag(request.getIndustryTag());
        if (request.getPriorityFlag() != null) client.setPriorityFlag(request.getPriorityFlag());

        boolean isAdmin = currentUser.getRole() == UserRole.SYSADMIN || currentUser.getRole() == UserRole.COMPANYADMIN;
        if (isAdmin && request.getAssignedRepId() != null && !request.getAssignedRepId().trim().isEmpty()) {
            User newRep = userRepository.findById(request.getAssignedRepId())
                    .orElseThrow(() -> new IllegalArgumentException("Assigned rep not found: " + request.getAssignedRepId()));
            client.setAssignedRep(newRep);
        }

        return ClientResponseDto.fromEntity(clientRepository.save(client));
    }

    public void deleteClient(String id, UserPrincipal currentUser) {
        Client client = findAndAuthorizeClient(id, currentUser);
        client.setDeleted(true);
        client.setDeletedAt(LocalDateTime.now());
        clientRepository.save(client);
    }

    @Transactional(readOnly = true)
    public ClientSummaryDto getClientSummary(String id, UserPrincipal currentUser) {
        Client client = findAndAuthorizeClient(id, currentUser);
        ClientNote note = client.getClientNote();

        ClientSummaryDto summary = new ClientSummaryDto();
        summary.setClientId(client.getId());
        summary.setClientName(client.getName());
        summary.setCompany(client.getCompany());
        summary.setPriorityFlag(client.getPriorityFlag());
        summary.setLastInteractionAt(client.getLastInteractionAt());
        summary.setTotalEntriesCount(client.getNotesCount());

        if (note != null) {
            summary.setClientNoteId(note.getId());
            List<LogEntry> entries = logEntryRepository.findByClientNoteIdAndDeletedFalseOrderByPriorityDescInteractionDateDescCreatedAtDesc(note.getId());
            long orders = entries.stream().filter(e -> e.getTag() != null && "ORDER".equalsIgnoreCase(e.getTag().getCode())).count();
            long complaints = entries.stream().filter(e -> e.getTag() != null && "COMPLAINT".equalsIgnoreCase(e.getTag().getCode())).count();
            long followups = entries.stream().filter(e -> e.getTag() != null && "FOLLOWUP".equalsIgnoreCase(e.getTag().getCode())).count();

            summary.setOpenOrdersCount(orders);
            summary.setOpenComplaintsCount(complaints);
            summary.setOpenFollowupsCount(followups);
        }

        return summary;
    }

    private Client findAndAuthorizeClient(String id, UserPrincipal currentUser) {
        Client client = clientRepository.findById(id)
                .filter(c -> !Boolean.TRUE.equals(c.getDeleted()))
                .orElseThrow(() -> new IllegalArgumentException("Client not found with id: " + id));

        boolean isEmployee = currentUser.getRole() == UserRole.COMPANYEMPLOYEE;
        if (isEmployee && !client.getAssignedRep().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You do not have permission to access client: " + id);
        }

        return client;
    }
}
