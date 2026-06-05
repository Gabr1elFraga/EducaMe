export interface DashboardMetric {
  label: string;
  value: string;
  description: string;
  tone: 'gold' | 'blue' | 'orange' | 'green';
}

export interface DashboardLesson {
  time: string;
  title: string;
  details: string;
  status: 'Confirmada' | 'Aguardando' | 'Reagendada';
}

export interface DashboardStudentUpdate {
  name: string;
  details: string;
  status: 'Novo' | 'Reagendado' | 'Regularizado';
}

export interface TeacherAvailability {
  name: string;
  subject: string;
  freeSlots: number;
}

export interface DashboardSummary {
  dateLabel: string;
  revenue: string;
  metrics: DashboardMetric[];
  lessons: DashboardLesson[];
  studentUpdates: DashboardStudentUpdate[];
  teacherAvailability: TeacherAvailability[];
  approvals: number;
  overduePayments: number;
  averageTicket: string;
  rating: string;
  reviewCount: number;
}
