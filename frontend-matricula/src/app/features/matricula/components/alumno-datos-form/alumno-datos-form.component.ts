import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import Swal from 'sweetalert2';

import { EstudianteConsultaDTO } from '../../../../core/models/estudiante-consulta.dto';

@Component({
  selector: 'app-alumno-datos-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './alumno-datos-form.component.html',
  styleUrl: './alumno-datos-form.component.css'
})
export class AlumnoDatosFormComponent implements OnInit, OnChanges {
  @Input() estudianteConsulta: EstudianteConsultaDTO | null = null;

  @Output() next = new EventEmitter<void>();
  @Output() prev = new EventEmitter<void>();
  @Output() datosFormSubmit = new EventEmitter<any>();

  form!: FormGroup;
  edadCalculada: number | null = null;

  constructor(private fb: FormBuilder) {}

  ngOnInit(): void {
    this.initForm();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['estudianteConsulta'] && this.form) {
      this.populateForm();
    }
  }

  get esContinuador(): boolean {
    return this.estudianteConsulta?.condicion === 'CONTINUADOR';
  }

  private initForm() {
    this.form = this.fb.group({
      // Datos Estudiante
      tipoEstudiante: [this.estudianteConsulta?.condicion || 'NUEVO', Validators.required],
      tipoDocEstudiante: ['DNI', Validators.required],
      nroDocEstudiante: [this.estudianteConsulta?.nroDoc || '', [Validators.required, Validators.pattern('^[0-9A-Za-z]{8,15}$')]],
      nombresEstudiante: ['', Validators.required],
      apellidosEstudiante: ['', Validators.required],
      fechaNacimiento: ['', Validators.required],
      sexo: ['M', Validators.required],

      // Específicos Alumno Nuevo
      nroPartidaNac: [''],
      colegioProcedencia: [''],
      tieneTraslado: [false],

      // Específicos Alumno Continuador
      codEstudiantePrevio: [''],
      anioIngreso: [new Date().getFullYear()],
      observacionAcademica: [''],

      // Datos Tutor / Apoderado
      tipoDocTutor: ['DNI', Validators.required],
      nroDocTutor: ['', [Validators.required, Validators.pattern('^[0-9]{8,12}$')]],
      nombresTutor: ['', Validators.required],
      apellidosTutor: ['', Validators.required],
      parentescoTutor: ['Padre', Validators.required],
      celularTutor: ['', [Validators.required, Validators.pattern('^[0-9]{9}$')]],
      direccionTutor: ['', Validators.required],
      correoTutor: ['', [Validators.email]]
    });

    this.populateForm();

    // Escuchar cambios de fecha de nacimiento para calcular edad en tiempo real
    this.form.get('fechaNacimiento')?.valueChanges.subscribe((fecha: string) => {
      this.calcularEdad(fecha);
    });
  }

  private populateForm() {
    if (!this.estudianteConsulta) return;

    const cond = this.estudianteConsulta.condicion || 'NUEVO';
    this.form.patchValue({
      tipoEstudiante: cond,
      nroDocEstudiante: this.estudianteConsulta.nroDoc || '',
      nombresEstudiante: this.estudianteConsulta.nombres || '',
      apellidosEstudiante: this.estudianteConsulta.apellidos || '',
      fechaNacimiento: this.estudianteConsulta.fechaNacimiento || '',
      sexo: this.estudianteConsulta.sexo || 'M',
      nroDocTutor: this.estudianteConsulta.nroDocTutor || '',
      nombresTutor: this.estudianteConsulta.nombresTutor || '',
      apellidosTutor: this.estudianteConsulta.apellidosTutor || '',
      parentescoTutor: this.estudianteConsulta.parentescoTutor || 'Padre',
      celularTutor: this.estudianteConsulta.celularTutor || ''
    });

    if (this.estudianteConsulta.fechaNacimiento) {
      this.calcularEdad(this.estudianteConsulta.fechaNacimiento);
    }
  }

  calcularEdad(fechaStr: string) {
    if (!fechaStr) {
      this.edadCalculada = null;
      return;
    }
    const birthDate = new Date(fechaStr);
    if (isNaN(birthDate.getTime())) {
      this.edadCalculada = null;
      return;
    }
    const today = new Date();
    let age = today.getFullYear() - birthDate.getFullYear();
    const m = today.getMonth() - birthDate.getMonth();
    if (m < 0 || (m === 0 && today.getDate() < birthDate.getDate())) {
      age--;
    }
    this.edadCalculada = age >= 0 ? age : null;
  }

  continuar() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      Swal.fire({
        icon: 'warning',
        title: 'Campos requeridos incompletos',
        text: 'Por favor, revise los campos marcados en rojo tanto del estudiante como del apoderado.',
        confirmButtonColor: '#1E293B'
      });
      return;
    }

    const val = this.form.getRawValue();
    this.datosFormSubmit.emit({
      ...val,
      edadCalculada: this.edadCalculada
    });
    this.next.emit();
  }

  regresar() {
    this.prev.emit();
  }
}
