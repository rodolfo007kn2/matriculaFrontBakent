import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

import { EstudianteListadoDTO } from '../../../core/models/estudiante-listado.dto';
import { EstudianteService } from '../../../core/services/estudiante.service';

@Component({
  selector: 'app-estudiantes-lista',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './estudiantes-lista.component.html',
  styleUrl: './estudiantes-lista.component.css'
})
export class EstudiantesListaComponent implements OnInit {

  estudiantes: EstudianteListadoDTO[] = [];
  estudiantesFiltrados: EstudianteListadoDTO[] = [];
  cargando: boolean = false;
  error: string | null = null;
  textoBusqueda: string = '';

  constructor(
    private estudianteService: EstudianteService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.cargarEstudiantes();
  }

  cargarEstudiantes(): void {
    this.cargando = true;
    this.error = null;
    this.estudianteService.listarEstudiantes().subscribe({
      next: (lista) => {
        this.estudiantes = lista;
        this.estudiantesFiltrados = lista;
        this.cargando = false;
      },
      error: (err) => {
        console.error('Error al cargar estudiantes:', err);
        this.error = 'No se pudo cargar la lista. Verifique la conexion con el servidor.';
        this.cargando = false;
      }
    });
  }

  filtrar(): void {
    const texto = this.textoBusqueda.toLowerCase().trim();
    if (!texto) {
      this.estudiantesFiltrados = this.estudiantes;
      return;
    }
    this.estudiantesFiltrados = this.estudiantes.filter(e =>
      e.nombres.toLowerCase().includes(texto) ||
      e.apellidos.toLowerCase().includes(texto) ||
      e.nroDoc.includes(texto)
    );
  }

  irAMatricula(): void {
    this.router.navigate(['/matricula/generar']);
  }

  getBadgeClass(condicion: string): string {
    switch (condicion) {
      case 'YA_MATRICULADO': return 'bg-emerald-100 text-emerald-800';
      case 'PRE_INSCRITO':   return 'bg-amber-100 text-amber-800';
      case 'CONTINUADOR':    return 'bg-blue-100 text-blue-800';
      case 'NUEVO':          return 'bg-gray-100 text-gray-700';
      default:               return 'bg-gray-100 text-gray-600';
    }
  }

  getBadgeLabel(condicion: string): string {
    switch (condicion) {
      case 'YA_MATRICULADO': return 'Matriculado';
      case 'PRE_INSCRITO':   return 'Pre-inscrito';
      case 'CONTINUADOR':    return 'Continuador';
      case 'NUEVO':          return 'Nuevo';
      default:               return condicion;
    }
  }
}
