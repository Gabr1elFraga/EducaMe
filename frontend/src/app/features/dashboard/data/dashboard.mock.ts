import { DashboardSummary } from '../models/dashboard-summary.model';

export const DASHBOARD_MOCK: DashboardSummary = {
  dateLabel: 'Terça-feira, 04 de Junho',
  revenue: 'R$ 8.420',
  metrics: [
    { label: 'Aulas marcadas', value: '18', description: '+4 nesta semana', tone: 'gold' },
    { label: 'Alunos ativos', value: '126', description: '92% recorrência', tone: 'blue' },
    { label: 'Pendências', value: '07', description: '2 pagamentos em aberto', tone: 'orange' },
    { label: 'Disponibilidades', value: '24', description: 'Slots livres hoje', tone: 'green' },
  ],
  lessons: [
    {
      time: '08:00',
      title: 'Matemática com Ana Luísa',
      details: 'Aluno: João Pedro · Modalidade: online · Duração: 50min',
      status: 'Confirmada',
    },
    {
      time: '10:30',
      title: 'Português com Carlos Menezes',
      details: 'Aluno: Larissa Souza · Modalidade: presencial · Duração: 1h',
      status: 'Aguardando',
    },
    {
      time: '14:00',
      title: 'Física com Beatriz Nunes',
      details: 'Aluno: Miguel Santos · Modalidade: online · Duração: 50min',
      status: 'Confirmada',
    },
  ],
  studentUpdates: [
    {
      name: 'Marina Costa',
      details: 'Nova matrícula em reforço escolar',
      status: 'Novo',
    },
    {
      name: 'Thiago Alves',
      details: 'Reagendou a aula de química para sexta-feira',
      status: 'Reagendado',
    },
    {
      name: 'Julia Ferreira',
      details: 'Pagou o plano mensal e liberou novas aulas',
      status: 'Regularizado',
    },
  ],
  teacherAvailability: [
    { name: 'Ana Luísa', subject: 'Matemática', freeSlots: 3 },
    { name: 'Beatriz Nunes', subject: 'Física', freeSlots: 2 },
    { name: 'Carlos Menezes', subject: 'Português', freeSlots: 1 },
  ],
  approvals: 34,
  overduePayments: 2,
  averageTicket: 'R$ 246',
  rating: '4.8 / 5',
  reviewCount: 312,
};
