import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

import { PagoBuscadorComponent } from '../components/pago-buscador/pago-buscador.component';
import { PagoDetalleComponent } from '../components/pago-detalle/pago-detalle.component';
import { PagoExitoComponent } from '../components/pago-exito/pago-exito.component';

import { MatriculaResponseDTO } from '../../../core/models/matricula-response.dto';
import { PagoResponseDTO } from '../../../core/models/pago-response.dto';

@Component({
  selector: 'app-pagos-container',
  standalone: true,
  imports: [
    CommonModule,
    PagoBuscadorComponent,
    PagoDetalleComponent,
    PagoExitoComponent
  ],
  templateUrl: './pagos-container.component.html',
  styleUrl: './pagos-container.component.css'
})
export class PagosContainerComponent {
  currentStep: number = 1;

  matriculaSeleccionada: MatriculaResponseDTO | null = null;
  comprobanteGenerado: PagoResponseDTO | null = null;

  pasos = [
    { num: 1, label: 'Búsqueda', sub: 'Pendiente' },
    { num: 2, label: 'Cobro', sub: 'Liquidación' },
    { num: 3, label: 'Comprobante', sub: 'Boleta / Recibo' }
  ];

  nextStep() {
    if (this.currentStep < 3) {
      this.currentStep++;
    }
  }

  prevStep() {
    if (this.currentStep > 1) {
      this.currentStep--;
    }
  }

  goToStep(step: number) {
    if (step < this.currentStep) {
      this.currentStep = step;
    }
  }

  onMatriculaSeleccionada(mat: MatriculaResponseDTO) {
    this.matriculaSeleccionada = mat;
  }

  onPagoExitoso(comp: PagoResponseDTO) {
    this.comprobanteGenerado = comp;
  }

  reiniciarWizard() {
    this.matriculaSeleccionada = null;
    this.comprobanteGenerado = null;
    this.currentStep = 1;
  }
}
