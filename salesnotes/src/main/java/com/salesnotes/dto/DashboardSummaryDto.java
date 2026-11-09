package com.salesnotes.dto;

import java.util.List;

public class DashboardSummaryDto {
    private long totalNotes;
    private long openNotes;
    private long wonNotes;
    private long urgentNotes;
    private double totalPipelineValue;
    private List<SalesNoteResponse> recentNotes;

    public DashboardSummaryDto() {
    }

    public DashboardSummaryDto(long totalNotes, long openNotes, long wonNotes, long urgentNotes, double totalPipelineValue, List<SalesNoteResponse> recentNotes) {
        this.totalNotes = totalNotes;
        this.openNotes = openNotes;
        this.wonNotes = wonNotes;
        this.urgentNotes = urgentNotes;
        this.totalPipelineValue = totalPipelineValue;
        this.recentNotes = recentNotes;
    }

    public long getTotalNotes() {
        return totalNotes;
    }

    public void setTotalNotes(long totalNotes) {
        this.totalNotes = totalNotes;
    }

    public long getOpenNotes() {
        return openNotes;
    }

    public void setOpenNotes(long openNotes) {
        this.openNotes = openNotes;
    }

    public long getWonNotes() {
        return wonNotes;
    }

    public void setWonNotes(long wonNotes) {
        this.wonNotes = wonNotes;
    }

    public long getUrgentNotes() {
        return urgentNotes;
    }

    public void setUrgentNotes(long urgentNotes) {
        this.urgentNotes = urgentNotes;
    }

    public double getTotalPipelineValue() {
        return totalPipelineValue;
    }

    public void setTotalPipelineValue(double totalPipelineValue) {
        this.totalPipelineValue = totalPipelineValue;
    }

    public List<SalesNoteResponse> getRecentNotes() {
        return recentNotes;
    }

    public void setRecentNotes(List<SalesNoteResponse> recentNotes) {
        this.recentNotes = recentNotes;
    }
}
