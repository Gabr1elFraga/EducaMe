package com.educame.educame_api.application.usecase.dashboard;

import com.educame.educame_api.application.dto.dashboard.DashboardSummaryResponse;
import com.educame.educame_api.infrastructure.persistence.jpa.entity.AlunoEntity;
import com.educame.educame_api.infrastructure.persistence.jpa.entity.AulaEntity;
import com.educame.educame_api.infrastructure.persistence.jpa.entity.ProfessorEntity;
import com.educame.educame_api.infrastructure.persistence.jpa.repository.AlunoJpaRepository;
import com.educame.educame_api.infrastructure.persistence.jpa.repository.AulaJpaRepository;
import com.educame.educame_api.infrastructure.persistence.jpa.repository.AvaliacaoJpaRepository;
import com.educame.educame_api.infrastructure.persistence.jpa.repository.DisponibilidadeJpaRepository;
import com.educame.educame_api.infrastructure.persistence.jpa.repository.PagamentoJpaRepository;
import com.educame.educame_api.infrastructure.persistence.jpa.repository.ProfessorJpaRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Service
public class DashboardSummaryUseCase {

	private final AulaJpaRepository aulaRepository;
	private final AlunoJpaRepository alunoRepository;
	private final ProfessorJpaRepository professorRepository;
	private final DisponibilidadeJpaRepository disponibilidadeRepository;
	private final PagamentoJpaRepository pagamentoRepository;
	private final AvaliacaoJpaRepository avaliacaoRepository;

	public DashboardSummaryUseCase(
		AulaJpaRepository aulaRepository,
		AlunoJpaRepository alunoRepository,
		ProfessorJpaRepository professorRepository,
		DisponibilidadeJpaRepository disponibilidadeRepository,
		PagamentoJpaRepository pagamentoRepository,
		AvaliacaoJpaRepository avaliacaoRepository
	) {
		this.aulaRepository = aulaRepository;
		this.alunoRepository = alunoRepository;
		this.professorRepository = professorRepository;
		this.disponibilidadeRepository = disponibilidadeRepository;
		this.pagamentoRepository = pagamentoRepository;
		this.avaliacaoRepository = avaliacaoRepository;
	}

	public DashboardSummaryResponse execute() {
		var now = OffsetDateTime.now();
		var locale = Locale.forLanguageTag("pt-BR");
		var dateLabel = now.format(DateTimeFormatter.ofPattern("EEEE, dd 'de' MMMM", locale));

		var lessons = aulaRepository.findAll(PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "createdAt")))
			.getContent()
			.stream()
			.map(this::toLesson)
			.toList();

		var studentUpdates = alunoRepository.findAll(PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "createdAt")))
			.getContent()
			.stream()
			.map(this::toStudentUpdate)
			.toList();

		var teacherAvailability = professorRepository.findAll(PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "createdAt")))
			.getContent()
			.stream()
			.map(this::toTeacherAvailability)
			.toList();

		var approvals = (int) pagamentoRepository.countByStatusValue("aprovado");
		var revenueValue = pagamentoRepository.sumValorByStatusValue("aprovado");
		var overduePayments = (int) pagamentoRepository.countOverdueByStatusValue("pendente", LocalDate.now());

		var reviewCount = (int) avaliacaoRepository.count();
		var averageRating = avaliacaoRepository.averageNota();
		var averageRatingValue = averageRating != null ? averageRating : 4.8;

		return new DashboardSummaryResponse(
			dateLabel,
			formatCurrency(revenueValue),
			List.of(
				new DashboardSummaryResponse.Metric("Aulas marcadas", String.valueOf(aulaRepository.count()), "+4 nesta semana", "gold"),
				new DashboardSummaryResponse.Metric("Alunos ativos", String.valueOf(alunoRepository.count()), "Base de usuários atual", "blue"),
				new DashboardSummaryResponse.Metric("Pendências", String.valueOf(overduePayments), "Pagamentos em aberto", "orange"),
				new DashboardSummaryResponse.Metric("Disponibilidades", String.valueOf(disponibilidadeRepository.count()), "Slots cadastrados", "green")
			),
			lessons,
			studentUpdates,
			teacherAvailability,
			approvals,
			overduePayments,
			formatCurrency(revenueValue.divide(BigDecimal.valueOf(Math.max(approvals, 1)), 2, RoundingMode.HALF_UP)),
			String.format(Locale.US, "%.1f / 5", averageRatingValue),
			reviewCount
		);
	}

	private DashboardSummaryResponse.Lesson toLesson(AulaEntity aula) {
		var professor = aula.getProfessor();
		var aluno = aula.getAluno();
		var disciplina = aula.getDisciplina();
		return new DashboardSummaryResponse.Lesson(
			aula.getInicio() != null ? aula.getInicio().toLocalTime().withSecond(0).withNano(0).toString() : "--:--",
			disciplina != null ? disciplina.getNome() + " com " + professor.getNome() : "Aula agendada",
			"Aluno: " + (aluno != null ? aluno.getNome() + " " + aluno.getSobrenome() : "N/D") + " · Duração: " + durationLabel(aula),
			aula.getStatus() != null ? labelize(aula.getStatus().name()) : "Agendada"
		);
	}

	private DashboardSummaryResponse.StudentUpdate toStudentUpdate(AlunoEntity aluno) {
		return new DashboardSummaryResponse.StudentUpdate(
			aluno.getNome() + " " + aluno.getSobrenome(),
			"Cadastro mais recente na base",
			"Novo"
		);
	}

	private DashboardSummaryResponse.TeacherAvailability toTeacherAvailability(ProfessorEntity professor) {
		var freeSlots = (int) disponibilidadeRepository.countByProfessorIdAndStatusValue(professor.getId(), "disponivel");
		return new DashboardSummaryResponse.TeacherAvailability(
			professor.getNome() + " " + professor.getSobrenome(),
			professor.getBio() != null && !professor.getBio().isBlank() ? professor.getBio() : "Professor",
			freeSlots
		);
	}

	private String durationLabel(AulaEntity aula) {
		if (aula.getInicio() == null || aula.getFim() == null) {
			return "50min";
		}
		var duration = java.time.Duration.between(aula.getInicio(), aula.getFim()).toMinutes();
		return duration > 0 ? duration + "min" : "50min";
	}

	private String formatCurrency(BigDecimal amount) {
		return "R$ " + amount.setScale(2, RoundingMode.HALF_UP).toPlainString().replace('.', ',');
	}

	private String labelize(String value) {
		return value.charAt(0) + value.substring(1).toLowerCase(Locale.ROOT);
	}
}
