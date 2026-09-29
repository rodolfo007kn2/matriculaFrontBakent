import { Routes } from '@angular/router';
import { LayoutComponent } from './core/layout/layout/layout.component';

export const routes: Routes = [
  {
    path: '',
    component: LayoutComponent,
    children: [
      {
        path: 'matricula/generar',
        loadComponent: () => import('./features/matricula/matricula-container/matricula-container.component').then(m => m.MatriculaContainerComponent)
      },
      {
        path: 'pagos/generar',
        loadComponent: () => import('./features/pagos/pagos-container/pagos-container.component').then(m => m.PagosContainerComponent)
      },
      {
        path: 'estudiantes/lista',
        loadComponent: () => import('./features/estudiantes/estudiantes-lista/estudiantes-lista.component').then(m => m.EstudiantesListaComponent)
      },
      {
        path: '',
        redirectTo: 'matricula/generar',
        pathMatch: 'full'
      }
    ]
  },
  {
    path: '**',
    redirectTo: 'matricula/generar'
  }
];
