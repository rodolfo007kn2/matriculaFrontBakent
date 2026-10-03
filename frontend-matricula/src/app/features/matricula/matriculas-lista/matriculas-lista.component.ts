import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

import { MatriculaResponseDTO } from '../../../core/models/matricula-response.dto';
import { MatriculaService } from '../../../core/services/matricula.service';

@Component({
  selector: 'app-matriculas-lista',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './matriculas-lista.component.html',
  styleUrl: './matriculas-lista.component.css'
})
export class MatriculasListaComponent implements OnInit {

  matriculas: MatriculaResponseDTO[] = [];
  matriculasFiltradas: MatriculaResponseDTO[] = [];
  cargando: boolean = false;
  error: string | null = null;
  textoBusqueda: string = '';

  constructor(
    private matriculaService: MatriculaService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.cargarMatriculas();
  }

  cargarMatriculas(): void {
    this.cargando = true;
    this.error = null;
    this.matriculaService.listarMatriculas().subscribe({
      next: (lista) => {
        this.matriculas = lista;
        this.matriculasFiltradas = lista;
        this.cargando = false;
      },
      error: (err) => {
        console.error('Error al cargar matrículas:', err);
        this.error = 'No se pudo cargar la lista. Verifique la conexión con el servidor.';
        this.cargando = false;
      }
    });
  }

  filtrar(): void {
    const texto = this.textoBusqueda.toLowerCase().trim();
    if (!texto) {
      this.matriculasFiltradas = this.matriculas;
      return;
    }
    this.matriculasFiltradas = this.matriculas.filter(m =>
      m.codMatricula.toLowerCase().includes(texto) ||
      m.nombresEstudiante.toLowerCase().includes(texto) ||
      m.dniEstudiante.includes(texto)
    );
  }

  irANuevaMatricula(): void {
    this.router.navigate(['/matricula/generar']);
  }

  getBadgeClass(estado: string): string {
    switch (estado) {
      case 'CANCELADO':     return 'bg-emerald-100 text-emerald-800';
      case 'PENDIENTE_PAGO': return 'bg-amber-100 text-amber-800';
      case 'ANULADO':       return 'bg-red-100 text-red-800';
      default:              return 'bg-gray-100 text-gray-600';
    }
  }

  getBadgeLabel(estado: string): string {
    switch (estado) {
      case 'CANCELADO':      return '✓ Pagado';
      case 'PENDIENTE_PAGO': return '⏳ Pendiente';
      case 'ANULADO':        return '✗ Anulado';
      default:               return estado;
    }
  }

  formatearMonto(monto: number): string {
    return `S/ ${Number(monto).toFixed(2)}`;
  }
}
