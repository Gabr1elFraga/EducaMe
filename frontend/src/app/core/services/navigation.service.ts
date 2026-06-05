import { Injectable } from '@angular/core';
import { NavigationItem } from '../models/navigation-item.model';

@Injectable({
  providedIn: 'root',
})
export class NavigationService {
  private readonly sections: NavigationItem[] = [
    { label: 'Dashboard', fragment: 'dashboard' },
    { label: 'Aulas', fragment: 'aulas' },
    { label: 'Alunos', fragment: 'alunos' },
    { label: 'Professores', fragment: 'professores' },
    { label: 'Financeiro', fragment: 'financeiro' },
    { label: 'Avaliações', fragment: 'avaliacoes' },
  ];

  getSections(): NavigationItem[] {
    return [...this.sections];
  }
}
