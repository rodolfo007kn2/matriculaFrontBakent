import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { AulaDTO } from '../models/aula.dto';
import { InscripcionRequestDTO } from '../models/inscripcion-request.dto';
import { InscripcionResponseDTO } from '../models/inscripcion-response.dto';
import { MatriculaRequestDTO } from '../models/matricula-request.dto';
import { MatriculaResponseDTO } from '../models/matricula-response.dto';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class MatriculaService {
  private baseUrl = environment.apiUrl;

  constructor(private http: HttpClient) { }


  guardarInscripcion(request: InscripcionRequestDTO): Observable<InscripcionResponseDTO> {
    return this.http.post<InscripcionResponseDTO>(`${this.baseUrl}/matriculas/inscripcion`, request);
  }

  listarAulas(gradoEdad?: number): Observable<AulaDTO[]> {
    const url = gradoEdad ? `${this.baseUrl}/aulas?gradoEdad=${gradoEdad}` : `${this.baseUrl}/aulas`;
    return this.http.get<AulaDTO[]>(url);
  }

  listarAulasPorGrado(gradoEdad: number): Observable<AulaDTO[]> {
    return this.listarAulas(gradoEdad);
  }

  procesarMatricula(request: MatriculaRequestDTO): Observable<MatriculaResponseDTO> {
    return this.http.post<MatriculaResponseDTO>(`${this.baseUrl}/matriculas`, request);
  }

  listarMatriculas(): Observable<MatriculaResponseDTO[]> {
    return this.http.get<MatriculaResponseDTO[]>(`${this.baseUrl}/matriculas`);
  }
}
