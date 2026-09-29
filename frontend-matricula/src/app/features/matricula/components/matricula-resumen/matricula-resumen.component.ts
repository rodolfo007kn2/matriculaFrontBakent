import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import Swal from 'sweetalert2';

import { AulaDTO } from '../../../../core/models/aula.dto';
import { MatriculaRequestDTO } from '../../../../core/models/matricula-request.dto';
import { MatriculaResponseDTO } from '../../../../core/models/matricula-response.dto';
import { MatriculaService } from '../../../../core/services/matricula.service';

@Component({
  selector: 'app-matricula-resumen',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './matricula-resumen.component.html',
  styleUrl: './matricula-resumen.component.css'
})
export class MatriculaResumenComponent {
  @Input() matriculaPayload!: MatriculaRequestDTO;
  @Input() aula!: AulaDTO;
  @Input() estudiantePreview?: any;
  @Input() tutorPreview?: any;
  @Input() documentosPreview?: any;

  @Output() next = new EventEmitter<void>();
  @Output() prev = new EventEmitter<void>();
  @Output() matriculaConfirmada = new EventEmitter<MatriculaResponseDTO>();

  guardando: boolean = false;
  costoMatricula: number = 350.00; // Tarifa oficial

  constructor(private matriculaService: MatriculaService) {}

  imprimirFicha() {
    window.print();
  }

  confirmarMatricula() {
    if (!this.matriculaPayload || !this.aula) {
      Swal.fire({
        icon: 'error',
        title: 'Datos incompletos',
        text: 'Falta información requerida para procesar la matrícula.',
        confirmButtonColor: '#1E293B'
      });
      return;
    }

    Swal.fire({
      title: '¿Formalizar Matrícula?',
      text: `Se registrará la matrícula oficial para el estudiante ${this.estudiantePreview?.nombresEstudiante} ${this.estudiantePreview?.apellidosEstudiante} en el Aula ${this.aula.nombreSeccion}.`,
      icon: 'question',
      showCancelButton: true,
      confirmButtonColor: '#1E293B',
      cancelButtonColor: '#64748B',
      confirmButtonText: 'Sí, formalizar matrícula',
      cancelButtonText: 'Revisar datos'
    }).then((result) => {
      if (result.isConfirmed) {
        this.guardando = true;

        this.matriculaService.procesarMatricula(this.matriculaPayload).subscribe({
          next: (res: MatriculaResponseDTO) => {
            this.guardando = false;
            Swal.fire({
              icon: 'success',
              title: '¡Matrícula Generada!',
              text: `Se ha generado la matrícula con código ${res.codMatricula}. El expediente ha pasado a estado "PENDIENTE DE PAGO".`,
              confirmButtonColor: '#2563EB'
            }).then(() => {
              this.matriculaConfirmada.emit(res);
              this.next.emit();
            });
          },
          error: (err) => {
            this.guardando = false;
            console.error('Error al procesar matrícula:', err);
            const msg = err.error?.error || err.error || 'Ocurrió un error al procesar la matrícula en el servidor.';
            Swal.fire({
              icon: 'error',
              title: 'No se pudo formalizar la matrícula',
              text: msg,
              confirmButtonColor: '#EF4444'
            });
          }
        });
      }
    });
  }

  regresar() {
    this.prev.emit();
  }
}
