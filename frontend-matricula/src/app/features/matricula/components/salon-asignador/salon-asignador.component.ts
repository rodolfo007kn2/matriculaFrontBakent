import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import Swal from 'sweetalert2';

import { AulaDTO } from '../../../../core/models/aula.dto';
import { MatriculaService } from '../../../../core/services/matricula.service';

@Component({
  selector: 'app-salon-asignador',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './salon-asignador.component.html',
  styleUrl: './salon-asignador.component.css'
})
export class SalonAsignadorComponent implements OnInit {
  @Input() edadEstudiante: number | null = null;

  @Output() next = new EventEmitter<void>();
  @Output() prev = new EventEmitter<void>();
  @Output() salonAsignado = new EventEmitter<AulaDTO>();

  cargandoAulas: boolean = false;
  todasLasAulas: AulaDTO[] = [];
  aulasFiltradas: AulaDTO[] = [];

  nivelSeleccionado: string = 'Inicial';
  gradoSeleccionado: number = 3; // 3, 4, 5 años
  turnoSeleccionado: string = 'TODOS'; // 'TODOS', 'MANANA', 'TARDE'

  aulaSeleccionada: AulaDTO | null = null;

  constructor(private matriculaService: MatriculaService) {}

  ngOnInit(): void {
    if (this.edadEstudiante && [3, 4, 5].includes(this.edadEstudiante)) {
      this.gradoSeleccionado = this.edadEstudiante;
    }
    this.cargarAulas();
  }

  cargarAulas() {
    this.cargandoAulas = true;
    this.matriculaService.listarAulas().subscribe({
      next: (aulas) => {
        this.cargandoAulas = false;
        this.todasLasAulas = aulas;
        this.aplicarFiltros();
      },
      error: (err) => {
        this.cargandoAulas = false;
        console.error('Error al cargar aulas:', err);
        Swal.fire({
          icon: 'error',
          title: 'Error de Servidor',
          text: 'No se pudieron recuperar las aulas desde el servidor.',
          confirmButtonColor: '#EF4444'
        });
      }
    });
  }

  aplicarFiltros() {
    this.aulasFiltradas = this.todasLasAulas.filter(aula => {
      const matchGrado = aula.gradoEdad === Number(this.gradoSeleccionado);
      const matchTurno = this.turnoSeleccionado === 'TODOS' || 
                         aula.turno.toUpperCase() === this.turnoSeleccionado.toUpperCase();
      return matchGrado && matchTurno;
    });

    // Si el aula seleccionada ya no está en los filtros, deseleccionar
    if (this.aulaSeleccionada && !this.aulasFiltradas.some(a => a.idAula === this.aulaSeleccionada?.idAula)) {
      this.aulaSeleccionada = null;
    }
  }

  /**
   * Excepción 2 del ECU-CUS-01: Salón Lleno / Sin Vacantes
   * Si el salón tiene cupo cero, muestra la alerta: "No hay vacantes en este salón y turno",
   * y enseguida sugiere otros salones o turnos que aún tengan cupos libres.
   */
  seleccionarAula(aula: AulaDTO) {
    if (aula.vacantesDisp <= 0) {
      // Buscar aulas alternativas con vacantes en el mismo grado/edad
      const alternativas = this.todasLasAulas.filter(
        a => a.idAula !== aula.idAula && a.vacantesDisp > 0 && a.gradoEdad === aula.gradoEdad
      );
      const alternativaPrincipal = alternativas.length > 0 ? alternativas[0] : null;

      let sugerenciaHtml = '';
      if (alternativaPrincipal) {
        const turnoTexto = alternativaPrincipal.turno === 'MANANA' ? 'Mañana (8:00 - 13:00)' : 'Tarde (13:30 - 18:00)';
        sugerenciaHtml = `
          <div class="mt-4 p-3.5 bg-blue-50 border border-blue-200 rounded-xl text-left text-xs text-blue-900 shadow-2xs">
            <p class="font-bold text-blue-800 flex items-center gap-1.5">
              <span>💡 Sugerencia de cupo disponible:</span>
            </p>
            <p class="mt-1 text-slate-700">
              Aula <b>${alternativaPrincipal.nombreSeccion}</b> en turno <b>${turnoTexto}</b> cuenta con 
              <span class="font-bold text-emerald-700">${alternativaPrincipal.vacantesDisp} vacante(s) libre(s)</span>.
            </p>
            <p class="text-[11px] text-blue-600 mt-1">¿Desea cambiar la asignación a este salón sugerido?</p>
          </div>
        `;
      } else {
        sugerenciaHtml = '<p class="text-xs text-red-500 mt-2">No se encontraron aulas alternativas con cupos disponibles para esta edad.</p>';
      }

      Swal.fire({
        icon: 'warning',
        title: 'No hay vacantes en este salón y turno',
        html: `
          <p class="text-sm text-gray-700">
            El aula <b>${aula.nombreSeccion}</b> (Turno ${aula.turno}) ha alcanzado su aforo máximo (Cupo = 0).
          </p>
          ${sugerenciaHtml}
        `,
        showCancelButton: true,
        confirmButtonColor: alternativaPrincipal ? '#2563EB' : '#1E293B',
        cancelButtonColor: '#64748B',
        confirmButtonText: alternativaPrincipal ? `Aceptar Aula ${alternativaPrincipal.nombreSeccion} (${alternativaPrincipal.turno})` : 'Entendido',
        cancelButtonText: 'Cancelar'
      }).then((result) => {
        if (result.isConfirmed && alternativaPrincipal) {
          // Ajustar filtros para que sea visible el aula sugerida
          this.turnoSeleccionado = 'TODOS';
          this.aplicarFiltros();
          this.aulaSeleccionada = alternativaPrincipal;
        }
      });
      return;
    }

    this.aulaSeleccionada = aula;
  }

  continuar() {
    if (!this.aulaSeleccionada) {
      Swal.fire({
        icon: 'warning',
        title: 'Aula no seleccionada',
        text: 'Debe elegir un salón con vacantes disponibles para continuar.',
        confirmButtonColor: '#1E293B'
      });
      return;
    }

    this.salonAsignado.emit(this.aulaSeleccionada);
    this.next.emit();
  }

  regresar() {
    this.prev.emit();
  }
}
