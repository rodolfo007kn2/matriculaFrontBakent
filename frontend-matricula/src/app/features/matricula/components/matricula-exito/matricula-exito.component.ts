import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Router } from '@angular/router';

import { MatriculaResponseDTO } from '../../../../core/models/matricula-response.dto';

@Component({
  selector: 'app-matricula-exito',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './matricula-exito.component.html',
  styleUrl: './matricula-exito.component.css'
})
export class MatriculaExitoComponent {
  @Input() matriculaGenerada: MatriculaResponseDTO | null = null;
  @Output() nuevaMatriculaClick = new EventEmitter<void>();

  constructor(private router: Router) {}

  imprimirFicha() {
    window.print();
  }

  nuevaMatricula() {
    this.nuevaMatriculaClick.emit();
  }

  irAPagos() {
    if (this.matriculaGenerada?.codMatricula) {
      this.router.navigate(['/pagos/generar'], { queryParams: { codigo: this.matriculaGenerada.codMatricula } });
    } else {
      this.router.navigate(['/pagos/generar']);
    }
  }
}

