package com.salesnotes.service;

import com.salesnotes.dto.DashboardSummaryDto;
import com.salesnotes.dto.SalesNoteResponse;
import com.salesnotes.entity.NoteStatus;
import com.salesnotes.entity.Priority;
import com.salesnotes.repository.SalesNoteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class DashboardService {

    private final SalesNoteRepository salesNoteRepository;

    public DashboardService(SalesNoteRepository salesNoteRepository) {
        this.salesNoteRepository = salesNoteRepository;
    }

    public DashboardSummaryDto getSummary() {
        long totalNotes = salesNoteRepository.count();
        long openNotes = salesNoteRepository.countByStatus(NoteStatus.OPEN) +
                         salesNoteRepository.countByStatus(NoteStatus.IN_PROGRESS) +
                         salesNoteRepository.countByStatus(NoteStatus.FOLLOW_UP_REQUIRED);
        long wonNotes = salesNoteRepository.countByStatus(NoteStatus.WON);
        long urgentNotes = salesNoteRepository.countByPriority(Priority.URGENT);
        Double totalPipeline = salesNoteRepository.sumTotalDealAmount();
        double pipelineValue = totalPipeline != null ? totalPipeline : 0.0;

        List<SalesNoteResponse> recentNotes = salesNoteRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .limit(5)
                .map(SalesNoteResponse::fromEntity)
                .collect(Collectors.toList());

        return new DashboardSummaryDto(
                totalNotes,
                openNotes,
                wonNotes,
                urgentNotes,
                pipelineValue,
                recentNotes
        );
    }
}
