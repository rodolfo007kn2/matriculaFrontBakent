import { Component, EventEmitter, Output } from '@angular/core';

@Component({
  selector: 'app-tipo-alumno-selector',
  standalone: true,
  templateUrl: './tipo-alumno-selector.component.html',
  styleUrl: './tipo-alumno-selector.component.css'
})
export class TipoAlumnoSelectorComponent {
  @Output() next = new EventEmitter<void>();
  @Output() skipToStep = new EventEmitter<number>();

  seleccionarAlumnoNuevo() {
    this.skipToStep.emit(3);
  }

  seleccionarContinuador() {
    this.next.emit();
  }
}
