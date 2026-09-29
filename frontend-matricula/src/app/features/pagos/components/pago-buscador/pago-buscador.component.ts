import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import Swal from 'sweetalert2';

import { MatriculaResponseDTO } from '../../../../core/models/matricula-response.dto';
import { PagoService } from '../../../../core/services/pago.service';

@Component({
  selector: 'app-pago-buscador',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './pago-buscador.component.html',
  styleUrl: './pago-buscador.component.css'
})
export class PagoBuscadorComponent implements OnInit {
  @Output() next = new EventEmitter<void>();
  @Output() matriculaSeleccionada = new EventEmitter<MatriculaResponseDTO>();

  tipoBusqueda: 'CODIGO' | 'DNI' = 'CODIGO';
  valorBusqueda: string = '';
  estaBuscando: boolean = false;
  busquedaRealizada: boolean = false;
  
  matriculasEncontradas: MatriculaResponseDTO[] = [];

  constructor(
    private pagoService: PagoService,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      if (params['codigo']) {
        this.tipoBusqueda = 'CODIGO';
        this.valorBusqueda = params['codigo'];
        this.buscarMatricula();
      } else if (params['dni']) {
        this.tipoBusqueda = 'DNI';
        this.valorBusqueda = params['dni'];
        this.buscarMatricula();
      }
    });
  }

  buscarMatricula() {
    const valor = this.valorBusqueda.trim();
    if (!valor) {
      Swal.fire({
        icon: 'warning',
        title: 'Campo obligatorio',
        text: `Por favor, ingrese un ${this.tipoBusqueda === 'CODIGO' ? 'código de matrícula' : 'DNI de estudiante'} para buscar.`,
        confirmButtonColor: '#1E293B'
      });
      return;
    }

    this.estaBuscando = true;
    this.matriculasEncontradas = [];
    this.busquedaRealizada = false;

    if (this.tipoBusqueda === 'CODIGO') {
      this.pagoService.buscarMatriculaPorCodigo(valor).subscribe({
        next: (matricula: MatriculaResponseDTO) => {
          this.estaBuscando = false;
          this.busquedaRealizada = true;
          this.matriculasEncontradas = matricula ? [matricula] : [];
        },
        error: (err) => {
          this.estaBuscando = false;
          this.busquedaRealizada = true;
          console.error('Error al buscar matrícula por código:', err);
          Swal.fire({
            icon: 'info',
            title: 'Sin Resultados',
            text: `No se encontró ninguna matrícula registrada con el código "${valor}".`,
            confirmButtonColor: '#1E293B'
          });
        }
      });
    } else {
      this.pagoService.buscarMatriculaPorDni(valor).subscribe({
        next: (lista: MatriculaResponseDTO[]) => {
          this.estaBuscando = false;
          this.busquedaRealizada = true;
          this.matriculasEncontradas = lista || [];
          if (this.matriculasEncontradas.length === 0) {
            Swal.fire({
              icon: 'info',
              title: 'Sin Resultados',
              text: `No se encontraron matrículas para el estudiante con DNI "${valor}".`,
              confirmButtonColor: '#1E293B'
            });
          }
        },
        error: (err) => {
          this.estaBuscando = false;
          this.busquedaRealizada = true;
          console.error('Error al buscar matrícula por DNI:', err);
          Swal.fire({
            icon: 'error',
            title: 'Error de Consulta',
            text: 'Ocurrió un error al buscar por DNI en el servidor.',
            confirmButtonColor: '#EF4444'
          });
        }
      });
    }
  }

  seleccionar(matricula: MatriculaResponseDTO) {
    if (matricula.estadoMatricula === 'CANCELADO') {
      Swal.fire({
        icon: 'info',
        title: 'Matrícula ya Cancelada',
        text: 'Esta matrícula ya se encuentra pagada. Puede consultar el comprobante en el módulo de reportes.',
        confirmButtonColor: '#1E293B'
      });
      return;
    }

    this.matriculaSeleccionada.emit(matricula);
    this.next.emit();
  }
}
