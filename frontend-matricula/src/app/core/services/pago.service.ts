import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';
import { MatriculaResponseDTO } from '../models/matricula-response.dto';
import { PagoRequestDTO } from '../models/pago-request.dto';
import { PagoResponseDTO } from '../models/pago-response.dto';

@Injectable({
  providedIn: 'root'
})
export class PagoService {
  private baseUrl = environment.apiUrl;

  constructor(private http: HttpClient) { }

  buscarMatriculaPorCodigo(codMatricula: string): Observable<MatriculaResponseDTO> {
    return this.http.get<MatriculaResponseDTO>(`${this.baseUrl}/matriculas/buscar?codigo=${codMatricula}`);
  }

  buscarMatriculaPorDni(dni: string): Observable<MatriculaResponseDTO[]> {
    return this.http.get<MatriculaResponseDTO[]>(`${this.baseUrl}/matriculas/buscar?dni=${dni}`);
  }

  registrarPago(request: PagoRequestDTO): Observable<PagoResponseDTO> {
    return this.http.post<PagoResponseDTO>(`${this.baseUrl}/pagos`, request);
  }

  listarPagos(): Observable<PagoResponseDTO[]> {
    return this.http.get<PagoResponseDTO[]>(`${this.baseUrl}/pagos`);
  }
}
