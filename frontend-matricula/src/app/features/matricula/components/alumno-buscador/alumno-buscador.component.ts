import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import Swal from 'sweetalert2';

import { AulaDTO } from '../../../../core/models/aula.dto';
import { EstudianteConsultaDTO } from '../../../../core/models/estudiante-consulta.dto';
import { EstudianteService } from '../../../../core/services/estudiante.service';
import { MatriculaService } from '../../../../core/services/matricula.service';

@Component({
  selector: 'app-alumno-buscador',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './alumno-buscador.component.html',
  styleUrl: './alumno-buscador.component.css'
})
export class AlumnoBuscadorComponent {
  @Output() next = new EventEmitter<void>();
  @Output() alumnoSeleccionado = new EventEmitter<EstudianteConsultaDTO>();

  tipoDocumento: string = 'DNI';
  numeroDocumento: string = '';
  estaBuscando: boolean = false;

  estudianteResultado: EstudianteConsultaDTO | null = null;
  busquedaRealizada: boolean = false;

  // Consulta Rápida de Vacantes
  mostrarModalVacantes: boolean = false;
  cargandoVacantes: boolean = false;
  listaAulas: AulaDTO[] = [];

  constructor(
    private estudianteService: EstudianteService,
    private matriculaService: MatriculaService,
    private router: Router
  ) {}

  buscarAlumno() {
    const docLimpio = this.numeroDocumento.trim();
    if (!docLimpio) {
      Swal.fire({
        icon: 'warning',
        title: 'Campo obligatorio',
        text: 'Por favor, ingrese el número de documento del estudiante.',
        confirmButtonColor: '#1E293B'
      });
      return;
    }

    if (this.tipoDocumento === 'DNI' && !/^\d{8}$/.test(docLimpio)) {
      Swal.fire({
        icon: 'warning',
        title: 'DNI Inválido',
        text: 'El DNI debe contener exactamente 8 dígitos numéricos.',
        confirmButtonColor: '#1E293B'
      });
      return;
    }

    this.estaBuscando = true;
    this.estudianteResultado = null;
    this.busquedaRealizada = false;

    this.estudianteService.consultarPorNroDoc(docLimpio).subscribe({
      next: (dto: EstudianteConsultaDTO) => {
        this.estaBuscando = false;
        this.busquedaRealizada = true;
        this.estudianteResultado = dto;

        if (dto.yaMatriculadoAnioActual) {
          Swal.fire({
            icon: 'warning',
            title: 'Alumno Ya Matriculado',
            html: `El estudiante <b>${dto.nombres} ${dto.apellidos}</b> ya cuenta con una matrícula registrada para el año lectivo en curso.<br><br>` +
                  `<b>Código:</b> ${dto.codMatriculaActual}<br>` +
                  `<b>Aula:</b> Inicial ${dto.gradoAulaActual} Años - Sección ${dto.seccionAulaActual}<br>` +
                  `<b>Estado:</b> ${dto.estadoMatriculaActual}`,
            confirmButtonColor: '#1E293B'
          });
        } else if (dto.existe) {
          Swal.fire({
            icon: 'info',
            title: 'Alumno Registrado (Continuador)',
            text: `Se encontró el registro de ${dto.nombres} ${dto.apellidos}. Habilitado para formalizar matrícula del nuevo año escolar.`,
            timer: 2000,
            showConfirmButton: false
          });
        } else {
          Swal.fire({
            icon: 'info',
            title: 'Sin antecedentes previos',
            text: 'El documento no figura en la base de datos escolar. Se procederá con la inscripción como alumno NUEVO.',
            confirmButtonColor: '#1E293B'
          });
        }
      },
      error: (err) => {
        this.estaBuscando = false;
        this.busquedaRealizada = true;
        console.error('Error al consultar estudiante:', err);
        Swal.fire({
          icon: 'error',
          title: 'Error de Conexión',
          text: 'No se pudo consultar el servidor. Verifique que el servicio backend esté activo.',
          confirmButtonColor: '#EF4444'
        });
      }
    });
  }

  seleccionarAlumno() {
    if (!this.estudianteResultado) return;
    if (this.estudianteResultado.yaMatriculadoAnioActual) {
      Swal.fire({
        icon: 'error',
        title: 'Trámite no permitido',
        text: 'No es posible generar una matrícula duplicada para un estudiante que ya se encuentra matriculado en este año escolar.',
        confirmButtonColor: '#1E293B'
      });
      return;
    }
    this.alumnoSeleccionado.emit(this.estudianteResultado);
    this.next.emit();
  }

  irAPagos() {
    this.router.navigate(['/pagos/generar']);
  }

  abrirModalVacantes() {
    this.mostrarModalVacantes = true;
    this.cargandoVacantes = true;
    this.matriculaService.listarAulas().subscribe({
      next: (aulas) => {
        this.cargandoVacantes = false;
        this.listaAulas = aulas;
      },
      error: (err) => {
        this.cargandoVacantes = false;
        console.error('Error al cargar vacantes:', err);
      }
    });
  }

  cerrarModalVacantes() {
    this.mostrarModalVacantes = false;
  }
}
