import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

import { AlumnoBuscadorComponent } from '../components/alumno-buscador/alumno-buscador.component';
import { AlumnoDatosFormComponent } from '../components/alumno-datos-form/alumno-datos-form.component';
import { DocumentosChecklistComponent } from '../components/documentos-checklist/documentos-checklist.component';
import { SalonAsignadorComponent } from '../components/salon-asignador/salon-asignador.component';
import { MatriculaResumenComponent } from '../components/matricula-resumen/matricula-resumen.component';
import { MatriculaExitoComponent } from '../components/matricula-exito/matricula-exito.component';

import { EstudianteConsultaDTO } from '../../../core/models/estudiante-consulta.dto';
import { AulaDTO } from '../../../core/models/aula.dto';
import { MatriculaRequestDTO } from '../../../core/models/matricula-request.dto';
import { MatriculaResponseDTO } from '../../../core/models/matricula-response.dto';

import { InscripcionRequestDTO } from '../../../core/models/inscripcion-request.dto';
import { MatriculaService } from '../../../core/services/matricula.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-matricula-container',
  standalone: true,
  imports: [
    CommonModule,
    AlumnoBuscadorComponent,
    AlumnoDatosFormComponent,
    DocumentosChecklistComponent,
    SalonAsignadorComponent,
    MatriculaResumenComponent,
    MatriculaExitoComponent
  ],
  templateUrl: './matricula-container.component.html',
  styleUrl: './matricula-container.component.css'
})
export class MatriculaContainerComponent {
  currentStep: number = 1;

  // Estado consolidado del Wizard
  estudianteConsulta: EstudianteConsultaDTO | null = null;
  datosForm: any = null;
  documentosPayload: any = null;
  aulaSeleccionada: AulaDTO | null = null;
  matriculaGenerada: MatriculaResponseDTO | null = null;

  pasos = [
    { num: 1, label: 'Buscar', sub: 'DNI Menor' },
    { num: 2, label: 'Datos', sub: 'Estudiante y Tutor' },
    { num: 3, label: 'Expediente', sub: 'Documentos' },
    { num: 4, label: 'Aula', sub: 'Vacantes' },
    { num: 5, label: 'Resumen', sub: 'Confirmación' },
    { num: 6, label: 'Listo', sub: 'Ficha Oficial' }
  ];

  constructor(private matriculaService: MatriculaService) {}

  nextStep() {
    if (this.currentStep < 6) {
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

  onAlumnoSeleccionado(dto: EstudianteConsultaDTO) {
    this.estudianteConsulta = dto;
    if (dto.tieneInscripcionPrevia || dto.nroExpediente) {
      this.documentosPayload = {
        tieneDniMenor: dto.tieneDniMenor,
        tieneDniTutor: dto.tieneDniTutor,
        tieneCartillaVacunas: dto.tieneCartillaVacunas,
        tieneTamizajeHemog: dto.tieneTamizajeHemog,
        tieneFichaSiagie: dto.tieneFichaSiagie,
        tieneCertifEstudios: dto.tieneCertifEstudios,
        tienePartidaNac: dto.tienePartidaNac,
        detalleObservacion: dto.detalleObservacion
      };
    }
  }

  onDatosFormSubmit(datos: any) {
    this.datosForm = datos;
  }

  onDocumentosSubmit(documentos: any) {
    this.documentosPayload = documentos;
  }

  onGuardarInscripcion(docsPayload: any) {
    this.documentosPayload = docsPayload;

    const payload: InscripcionRequestDTO = {
      tipoEstudiante: this.datosForm?.tipoEstudiante || this.estudianteConsulta?.condicion || 'NUEVO',
      tipoDocEstudiante: this.datosForm?.tipoDocEstudiante || 'DNI',
      nroDocEstudiante: this.datosForm?.nroDocEstudiante || this.estudianteConsulta?.nroDoc || '',
      nombresEstudiante: this.datosForm?.nombresEstudiante || '',
      apellidosEstudiante: this.datosForm?.apellidosEstudiante || '',
      fechaNacimiento: this.datosForm?.fechaNacimiento || '',
      sexo: this.datosForm?.sexo || 'M',
      nroPartidaNac: this.datosForm?.nroPartidaNac || null,
      colegioProcedencia: this.datosForm?.colegioProcedencia || null,
      tieneTraslado: this.datosForm?.tieneTraslado || false,
      tipoDocTutor: this.datosForm?.tipoDocTutor || 'DNI',
      nroDocTutor: this.datosForm?.nroDocTutor || '',
      nombresTutor: this.datosForm?.nombresTutor || '',
      apellidosTutor: this.datosForm?.apellidosTutor || '',
      parentescoTutor: this.datosForm?.parentescoTutor || 'Padre',
      celularTutor: this.datosForm?.celularTutor || '',
      direccionTutor: this.datosForm?.direccionTutor || '',
      correoTutor: this.datosForm?.correoTutor || null,
      tieneDniMenor: docsPayload.tieneDniMenor,
      tieneDniTutor: docsPayload.tieneDniTutor,
      tieneCartillaVacunas: docsPayload.tieneCartillaVacunas,
      tieneTamizajeHemog: docsPayload.tieneTamizajeHemog,
      tieneFichaSiagie: docsPayload.tieneFichaSiagie,
      tieneCertifEstudios: docsPayload.tieneCertifEstudios,
      tienePartidaNac: docsPayload.tienePartidaNac,
      detalleObservacion: docsPayload.detalleObservacion,
      idAula: null
    };

    this.matriculaService.guardarInscripcion(payload).subscribe({
      next: (res) => {
        Swal.fire({
          icon: 'success',
          title: '¡Ficha de Inscripción Guardada!',
          html: `<div class="text-left text-xs space-y-1.5 bg-gray-50 p-3 rounded-lg border border-gray-200 mt-2">
                   <div>Código de Inscripción: <b class="font-mono text-blue-700 font-bold">${res.codInscripcion}</b></div>
                   <div>N° de Expediente: <b class="font-mono text-slate-800">${res.nroExpediente}</b></div>
                   <div>Estado del Expediente: <b class="${res.estadoDoc === 'COMPLETO' ? 'text-emerald-700' : 'text-amber-700'}">${res.estadoDoc}</b></div>
                   <div class="text-slate-500 pt-1 text-[11px] leading-relaxed">
                     La información del estudiante y los documentos recepcionados se han guardado con éxito. Cuando el apoderado regrese, podrá consultar por su DNI para reanudar el trámite y formalizar su matrícula.
                   </div>
                 </div>`,
          confirmButtonColor: '#1E293B',
          confirmButtonText: 'Entendido'
        }).then(() => {
          this.reiniciarWizard();
        });
      },
      error: (err) => {
        console.error('Error al guardar inscripción:', err);
        const msg = err.error?.error || err.error || 'Ocurrió un error al guardar la Ficha de Inscripción en el servidor.';
        Swal.fire({
          icon: 'error',
          title: 'Error al Guardar Inscripción',
          text: msg,
          confirmButtonColor: '#EF4444'
        });
      }
    });
  }

  onSalonAsignado(aula: AulaDTO) {
    this.aulaSeleccionada = aula;
  }

  onMatriculaConfirmada(matricula: MatriculaResponseDTO) {
    this.matriculaGenerada = matricula;
  }

  reiniciarWizard() {
    this.estudianteConsulta = null;
    this.datosForm = null;
    this.documentosPayload = null;
    this.aulaSeleccionada = null;
    this.matriculaGenerada = null;
    this.currentStep = 1;
  }

  get matriculaPayload(): MatriculaRequestDTO {
    return {
      tipoEstudiante: this.datosForm?.tipoEstudiante || this.estudianteConsulta?.condicion || 'NUEVO',
      tipoDocEstudiante: this.datosForm?.tipoDocEstudiante || 'DNI',
      nroDocEstudiante: this.datosForm?.nroDocEstudiante || this.estudianteConsulta?.nroDoc || '',
      nombresEstudiante: this.datosForm?.nombresEstudiante || '',
      apellidosEstudiante: this.datosForm?.apellidosEstudiante || '',
      fechaNacimiento: this.datosForm?.fechaNacimiento || '',
      sexo: this.datosForm?.sexo || 'M',
      nroPartidaNac: this.datosForm?.nroPartidaNac || null,
      colegioProcedencia: this.datosForm?.colegioProcedencia || null,
      tieneTraslado: this.datosForm?.tieneTraslado || false,
      codEstudiantePrevio: this.datosForm?.codEstudiantePrevio || null,
      anioIngreso: this.datosForm?.anioIngreso ? Number(this.datosForm.anioIngreso) : new Date().getFullYear(),
      observacionAcademica: this.datosForm?.observacionAcademica || null,
      tipoDocTutor: this.datosForm?.tipoDocTutor || 'DNI',
      nroDocTutor: this.datosForm?.nroDocTutor || '',
      nombresTutor: this.datosForm?.nombresTutor || '',
      apellidosTutor: this.datosForm?.apellidosTutor || '',
      parentescoTutor: this.datosForm?.parentescoTutor || 'Padre',
      celularTutor: this.datosForm?.celularTutor || '',
      direccionTutor: this.datosForm?.direccionTutor || '',
      correoTutor: this.datosForm?.correoTutor || null,
      tieneDniMenor: this.documentosPayload?.tieneDniMenor ?? true,
      tieneDniTutor: this.documentosPayload?.tieneDniTutor ?? true,
      tieneCartillaVacunas: this.documentosPayload?.tieneCartillaVacunas ?? true,
      tieneTamizajeHemog: this.documentosPayload?.tieneTamizajeHemog ?? true,
      tieneFichaSiagie: this.documentosPayload?.tieneFichaSiagie ?? false,
      tieneCertifEstudios: this.documentosPayload?.tieneCertifEstudios ?? false,
      tienePartidaNac: this.documentosPayload?.tienePartidaNac ?? true,
      idAula: this.aulaSeleccionada?.idAula || 0
    };
  }
}
