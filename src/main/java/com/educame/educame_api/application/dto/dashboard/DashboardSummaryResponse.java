package com.educame.educame_api.application.dto.dashboard;

import java.util.List;

public record DashboardSummaryResponse(
	String dateLabel,
	String revenue,
	List<Metric> metrics,
	List<Lesson> lessons,
	List<StudentUpdate> studentUpdates,
	List<TeacherAvailability> teacherAvailability,
	int approvals,
	int overduePayments,
	String averageTicket,
	String rating,
	int reviewCount
) {
	public record Metric(String label, String value, String description, String tone) {
	}

	public record Lesson(String time, String title, String details, String status) {
	}

	public record StudentUpdate(String name, String details, String status) {
	}

	public record TeacherAvailability(String name, String subject, int freeSlots) {
	}
}
