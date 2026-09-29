import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { EstudianteConsultaDTO } from '../models/estudiante-consulta.dto';
import { EstudianteListadoDTO } from '../models/estudiante-listado.dto';

@Injectable({
  providedIn: 'root'
})
export class EstudianteService {
  private baseUrl = 'http://localhost:8080/api/estudiantes';

  constructor(private http: HttpClient) {}

  /**
   * Consulta un estudiante por numero de documento.
   * Retorna datos completos mas el estado de matricula e inscripcion.
   */
  consultarPorNroDoc(nroDoc: string): Observable<EstudianteConsultaDTO> {
    return this.http.get<EstudianteConsultaDTO>(`${this.baseUrl}/consultar/${nroDoc}`);
  }

  /**
   * Lista todos los estudiantes registrados con su estado calculado
   * en el anio lectivo actual (NUEVO, CONTINUADOR, PRE_INSCRITO, YA_MATRICULADO).
   */
  listarEstudiantes(): Observable<EstudianteListadoDTO[]> {
    return this.http.get<EstudianteListadoDTO[]>(`${this.baseUrl}/listar`);
  }
}
