package com.educame.educame_api.presentation.controller;

import com.educame.educame_api.application.dto.dashboard.DashboardSummaryResponse;
import com.educame.educame_api.application.usecase.dashboard.DashboardSummaryUseCase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

	private final DashboardSummaryUseCase dashboardSummaryUseCase;

	public DashboardController(DashboardSummaryUseCase dashboardSummaryUseCase) {
		this.dashboardSummaryUseCase = dashboardSummaryUseCase;
	}

	@GetMapping("/summary")
	public DashboardSummaryResponse summary() {
		return dashboardSummaryUseCase.execute();
	}
}
