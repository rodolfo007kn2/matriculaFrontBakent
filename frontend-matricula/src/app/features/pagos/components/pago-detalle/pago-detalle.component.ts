import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import Swal from 'sweetalert2';

import { MatriculaResponseDTO } from '../../../../core/models/matricula-response.dto';
import { PagoRequestDTO } from '../../../../core/models/pago-request.dto';
import { PagoResponseDTO } from '../../../../core/models/pago-response.dto';
import { PagoService } from '../../../../core/services/pago.service';

@Component({
  selector: 'app-pago-detalle',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './pago-detalle.component.html',
  styleUrl: './pago-detalle.component.css'
})
export class PagoDetalleComponent implements OnInit {
  @Input() matricula!: MatriculaResponseDTO;

  @Output() next = new EventEmitter<void>();
  @Output() prev = new EventEmitter<void>();
  @Output() pagoExitoso = new EventEmitter<PagoResponseDTO>();

  tipoComprobante: 'BOLETA' | 'RECIBO' = 'BOLETA';
  medioPago: 'EFECTIVO' | 'TRANSFERENCIA' | 'TARJETA' | 'YAPE_PLIN' = 'EFECTIVO';
  montoRecibido: number | null = null;
  nroOperacion: string = '';
  pagoVerificado: boolean = false;
  procesando: boolean = false;

  constructor(private pagoService: PagoService) { }

  ngOnInit(): void {
    if (this.matricula) {
      this.montoRecibido = this.matricula.montoTotal;
    }
  }

  onDatosCobroChange() {
    this.pagoVerificado = false;
  }

  get vuelto(): number {
    if (this.medioPago === 'EFECTIVO' && this.montoRecibido && this.montoRecibido >= this.matricula.montoTotal) {
      return Number((this.montoRecibido - this.matricula.montoTotal).toFixed(2));
    }
    return 0;
  }

  setMontoExacto() {
    this.montoRecibido = this.matricula.montoTotal;
    this.pagoVerificado = false;
  }

  sumarEfectivo(adicional: number) {
    this.montoRecibido = (this.montoRecibido || this.matricula.montoTotal) + adicional;
    this.pagoVerificado = false;
  }

  /**
   * Paso 9 y 10 del ECU-CUS-02:
   * La Tesorera presiona [Verificar Pago]. El sistema comprueba que el dinero recibido
   * sea igual al monto total.
   */
  verificarPago() {
    if (!this.matricula) return;

    if (this.medioPago === 'EFECTIVO') {
      if (!this.montoRecibido || this.montoRecibido < this.matricula.montoTotal) {
        this.pagoVerificado = false;
        Swal.fire({
          icon: 'error',
          title: 'Monto insuficiente para liquidar la matrícula',
          text: `El monto recibido (S/ ${(this.montoRecibido || 0).toFixed(2)}) es menor al costo fijado de S/ ${this.matricula.montoTotal.toFixed(2)}.`,
          confirmButtonColor: '#EF4444'
        });
        return;
      }
    } else {
      if (!this.nroOperacion || !this.nroOperacion.trim()) {
        this.pagoVerificado = false;
        Swal.fire({
          icon: 'warning',
          title: 'Nº de Operación requerido',
          text: 'Para medios bancarios o electrónicos es obligatorio ingresar el número de operación o voucher.',
          confirmButtonColor: '#1E293B'
        });
        return;
      }
    }

    this.pagoVerificado = true;
    Swal.fire({
      icon: 'success',
      title: 'Pago Verificado Conforme',
      html: `<div class="text-sm text-gray-600">
               <p>Importe a liquidar: <b>S/ ${this.matricula.montoTotal.toFixed(2)}</b></p>
               ${this.medioPago === 'EFECTIVO' ? `<p>Vuelto a entregar: <b>S/ ${this.vuelto.toFixed(2)}</b></p>` : `<p>N° Operación: <b>${this.nroOperacion}</b></p>`}
               <p class="text-emerald-600 font-semibold mt-2">Puede proceder a emitir el comprobante institucional.</p>
             </div>`,
      timer: 2500,
      showConfirmButton: true,
      confirmButtonText: 'Entendido',
      confirmButtonColor: '#10B981'
    });
  }

  /**
   * Pasos 11, 12 y 13 del ECU-CUS-02:
   * La Tesorera presiona [Emitir Comprobante]. El sistema genera el correlativo,
   * guarda el comprobante y cambia el estado de la matrícula.
   */
  emitirComprobante() {
    if (!this.matricula) return;

    if (!this.pagoVerificado) {
      Swal.fire({
        icon: 'info',
        title: 'Verificación requerida',
        text: 'Por favor, presione primero el botón [Verificar Pago] antes de emitir el comprobante.',
        confirmButtonColor: '#1E293B'
      });
      return;
    }

    Swal.fire({
      title: '¿Emitir Comprobante de Pago?',
      html: `<div class="text-sm text-gray-600 text-left space-y-1">
               <p><b>Estudiante:</b> ${this.matricula.nombresEstudiante}</p>
               <p><b>Matrícula:</b> ${this.matricula.codMatricula}</p>
               <p><b>Importe:</b> S/ ${this.matricula.montoTotal.toFixed(2)}</p>
               <p><b>Tipo:</b> ${this.tipoComprobante} (${this.medioPago})</p>
             </div>`,
      icon: 'question',
      showCancelButton: true,
      confirmButtonColor: '#10B981',
      cancelButtonColor: '#64748B',
      confirmButtonText: 'Sí, emitir comprobante',
      cancelButtonText: 'Cancelar'
    }).then((result) => {
      if (result.isConfirmed) {
        this.procesando = true;

        const request: PagoRequestDTO = {
          codMatricula: this.matricula.codMatricula,
          tipoComprobante: this.tipoComprobante,
          medioPago: this.medioPago,
          nroOperacion: this.medioPago === 'EFECTIVO' ? undefined : this.nroOperacion.trim(),
          importePagado: this.matricula.montoTotal
        };

        this.pagoService.registrarPago(request).subscribe({
          next: (res: PagoResponseDTO) => {
            this.procesando = false;
            Swal.fire({
              icon: 'success',
              title: 'Pago registrado correctamente',
              text: `Comprobante Nro. ${res.nroComprobante} emitido con éxito.`,
              confirmButtonColor: '#2563EB'
            }).then(() => {
              this.pagoExitoso.emit(res);
              this.next.emit();
            });
          },
          error: (err) => {
            this.procesando = false;
            console.error('Error al registrar pago:', err);
            const msg = err.error?.error || err.error || 'Ocurrió un error al registrar el pago en el servidor.';
            Swal.fire({
              icon: 'error',
              title: 'Error al Registrar Cobro',
              text: msg,
              confirmButtonColor: '#EF4444'
            });
          }
        });
      }
    });
  }

  /**
   * Opción [Anular Cobro] (ECU-CUS-02 Botones de acción)
   */
  anularCobro() {
    Swal.fire({
      title: '¿Anular proceso de cobro?',
      text: 'Se cancelará la operación actual y la matrícula permanecerá en estado Pendiente de Pago.',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#EF4444',
      cancelButtonColor: '#64748B',
      confirmButtonText: 'Sí, anular',
      cancelButtonText: 'Continuar cobro'
    }).then((result) => {
      if (result.isConfirmed) {
        this.regresar();
      }
    });
  }

  regresar() {
    this.prev.emit();
  }
}
