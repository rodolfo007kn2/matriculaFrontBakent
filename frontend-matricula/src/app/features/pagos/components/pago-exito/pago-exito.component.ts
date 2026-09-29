import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';

import { MatriculaResponseDTO } from '../../../../core/models/matricula-response.dto';
import { PagoResponseDTO } from '../../../../core/models/pago-response.dto';

@Component({
  selector: 'app-pago-exito',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './pago-exito.component.html',
  styleUrl: './pago-exito.component.css'
})
export class PagoExitoComponent {
  @Input() comprobante: PagoResponseDTO | null = null;
  @Input() matricula: MatriculaResponseDTO | null = null;

  @Output() nuevoCobroClick = new EventEmitter<void>();

  imprimirComprobante() {
    window.print();
  }

  nuevoCobro() {
    this.nuevoCobroClick.emit();
  }
}
