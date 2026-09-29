import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import Swal from 'sweetalert2';

export interface DocumentoItem {
  id: string;
  nombre: string;
  descripcion: string;
  obligatorio: boolean;
  entregado: boolean;
  observacion: string;
}

@Component({
  selector: 'app-documentos-checklist',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './documentos-checklist.component.html',
  styleUrl: './documentos-checklist.component.css'
})
export class DocumentosChecklistComponent implements OnInit {
  @Input() tipoEstudiante: string = 'NUEVO';
  @Input() initialData: any = null;
  @Input() tieneInscripcionPrevia: boolean = false;
  @Input() nroExpediente: string = '';
  @Input() estadoDoc: string = '';

  @Output() next = new EventEmitter<void>();
  @Output() prev = new EventEmitter<void>();
  @Output() documentosSubmit = new EventEmitter<any>();
  @Output() guardarInscripcionClick = new EventEmitter<any>();

  documentos: DocumentoItem[] = [];
  observacionesGenerales: string = '';

  ngOnInit(): void {
    const esNuevo = this.tipoEstudiante === 'NUEVO';

    this.documentos = [
      {
        id: 'tieneDniMenor',
        nombre: 'Copia de DNI del Menor',
        descripcion: 'Copia legible y vigente por ambos lados del DNI del estudiante.',
        obligatorio: true,
        entregado: true,
        observacion: ''
      },
      {
        id: 'tieneDniTutor',
        nombre: 'Copia de DNI del Apoderado',
        descripcion: 'Copia legible del DNI del padre, madre o apoderado legal acreditado.',
        obligatorio: true,
        entregado: true,
        observacion: ''
      },
      {
        id: 'tienePartidaNac',
        nombre: 'Partida de Nacimiento',
        descripcion: 'Partida o acta original o copia certificada expedida por RENIEC.',
        obligatorio: esNuevo,
        entregado: true,
        observacion: ''
      },
      {
        id: 'tieneCartillaVacunas',
        nombre: 'Cartilla de Vacunación',
        descripcion: 'Copia de la cartilla completa según el esquema nacional del MINSA.',
        obligatorio: true,
        entregado: true,
        observacion: ''
      },
      {
        id: 'tieneTamizajeHemog',
        nombre: 'Tamizaje de Hemoglobina',
        descripcion: 'Constancia médica emitida en los últimos 6 meses para prevención de anemia.',
        obligatorio: true,
        entregado: true,
        observacion: ''
      },
      {
        id: 'tieneFichaSiagie',
        nombre: 'Ficha SIAGIE de Traslado',
        descripcion: 'Ficha Única de Matrícula emitida por el sistema SIAGIE (aplica traslados).',
        obligatorio: false,
        entregado: false,
        observacion: ''
      },
      {
        id: 'tieneCertifEstudios',
        nombre: 'Certificado Oficial de Estudios',
        descripcion: 'Documento original de estudios si procede de otra institución educativa.',
        obligatorio: false,
        entregado: false,
        observacion: ''
      }
    ];

    if (this.initialData) {
      this.documentos.forEach(doc => {
        if (this.initialData[doc.id] !== undefined && this.initialData[doc.id] !== null) {
          doc.entregado = Boolean(this.initialData[doc.id]);
        }
      });
      if (this.initialData.detalleObservacion) {
        this.observacionesGenerales = this.initialData.detalleObservacion;
      }
    }
  }

  get totalObligatorios(): number {
    return this.documentos.filter(d => d.obligatorio).length;
  }

  get totalEntregadosObligatorios(): number {
    return this.documentos.filter(d => d.obligatorio && d.entregado).length;
  }

  continuar() {
    const faltantes = this.documentos.filter(doc => doc.obligatorio && !doc.entregado);

    if (faltantes.length > 0) {
      const listaFaltantes = faltantes.map(f => `• ${f.nombre}`).join('\n');
      Swal.fire({
        icon: 'error',
        title: 'Documentación Obligatoria Incompleta',
        html: `<p class="text-xs mb-2 text-gray-600">Faltan documentos indispensables para formalizar:</p>
               <pre class="text-left bg-gray-100 p-3 rounded text-xs font-sans text-red-700">${listaFaltantes}</pre>
               <p class="text-xs text-gray-500 mt-2">Puede usar el botón <b>"Guardar Ficha de Inscripción"</b> para salvar el avance actual y formalizar cuando el padre traiga los documentos.</p>`,
        confirmButtonColor: '#1E293B',
        confirmButtonText: 'Revisar'
      });
      return;
    }

    // Armamos el objeto con las claves exactas requeridas por MatriculaRequestDTO
    const resultadoPayload: any = {
      tieneDniMenor: this.obtenerEstado('tieneDniMenor'),
      tieneDniTutor: this.obtenerEstado('tieneDniTutor'),
      tieneCartillaVacunas: this.obtenerEstado('tieneCartillaVacunas'),
      tieneTamizajeHemog: this.obtenerEstado('tieneTamizajeHemog'),
      tieneFichaSiagie: this.obtenerEstado('tieneFichaSiagie'),
      tieneCertifEstudios: this.obtenerEstado('tieneCertifEstudios'),
      tienePartidaNac: this.obtenerEstado('tienePartidaNac'),
      detalleObservacion: this.observacionesGenerales
    };

    this.documentosSubmit.emit(resultadoPayload);
    this.next.emit();
  }

  guardarSoloInscripcion() {
    const resultadoPayload: any = {
      tieneDniMenor: this.obtenerEstado('tieneDniMenor'),
      tieneDniTutor: this.obtenerEstado('tieneDniTutor'),
      tieneCartillaVacunas: this.obtenerEstado('tieneCartillaVacunas'),
      tieneTamizajeHemog: this.obtenerEstado('tieneTamizajeHemog'),
      tieneFichaSiagie: this.obtenerEstado('tieneFichaSiagie'),
      tieneCertifEstudios: this.obtenerEstado('tieneCertifEstudios'),
      tienePartidaNac: this.obtenerEstado('tienePartidaNac'),
      detalleObservacion: this.observacionesGenerales
    };

    const faltantes = this.documentos.filter(d => !d.entregado);
    const estadoPreview = faltantes.length === 0 ? 'COMPLETO' : 'OBSERVADO';

    Swal.fire({
      title: '¿Guardar Ficha de Inscripción?',
      text: `Se registrará la postulación y el expediente documentario con estado "${estadoPreview}". Los datos quedarán guardados de forma permanente para continuar y formalizar la matrícula cuando el apoderado regrese.`,
      icon: 'question',
      showCancelButton: true,
      confirmButtonColor: '#2563EB',
      cancelButtonColor: '#64748B',
      confirmButtonText: 'Sí, guardar inscripción',
      cancelButtonText: 'Cancelar'
    }).then((result) => {
      if (result.isConfirmed) {
        this.guardarInscripcionClick.emit(resultadoPayload);
      }
    });
  }

  private obtenerEstado(id: string): boolean {
    const doc = this.documentos.find(d => d.id === id);
    return doc ? doc.entregado : false;
  }

  regresar() {
    this.prev.emit();
  }

  rechazarTramite() {
    const faltantes = this.documentos.filter(d => !d.entregado);
    const listaTxt = faltantes.map(f => `• ${f.nombre}`).join('<br>');

    Swal.fire({
      title: '¿Generar Esquela de Observación y Guardar?',
      html: `<p class="text-sm text-gray-600 mb-2">Se registrará la Ficha de Inscripción con expediente <b>OBSERVADO</b> indicando los recaudos pendientes:</p>
             <div class="text-left bg-gray-100 p-3 rounded text-xs font-mono text-gray-800">${listaTxt || 'Ningún documento marcado como faltante'}</div>`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#EF4444',
      cancelButtonColor: '#64748B',
      confirmButtonText: 'Sí, guardar y emitir esquela',
      cancelButtonText: 'Cancelar'
    }).then((result) => {
      if (result.isConfirmed) {
        const resultadoPayload: any = {
          tieneDniMenor: this.obtenerEstado('tieneDniMenor'),
          tieneDniTutor: this.obtenerEstado('tieneDniTutor'),
          tieneCartillaVacunas: this.obtenerEstado('tieneCartillaVacunas'),
          tieneTamizajeHemog: this.obtenerEstado('tieneTamizajeHemog'),
          tieneFichaSiagie: this.obtenerEstado('tieneFichaSiagie'),
          tieneCertifEstudios: this.obtenerEstado('tieneCertifEstudios'),
          tienePartidaNac: this.obtenerEstado('tienePartidaNac'),
          detalleObservacion: this.observacionesGenerales || 'Expediente observado con documentos pendientes por regularizar.'
        };
        this.guardarInscripcionClick.emit(resultadoPayload);
      }
    });
  }
}
