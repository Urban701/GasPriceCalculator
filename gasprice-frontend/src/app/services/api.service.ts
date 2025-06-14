import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ApiService {

  // The Spring Boot backend is expected to be running on the same host,
  // typically proxied by the Angular development server in a real dev environment.
  // For this setup, we assume /api is correctly routed to the backend.
  private baseUrl = '/api'; // Adjust if your Spring Boot app has a different context path

  constructor(private http: HttpClient) { }

  getCurrentPrices(): Observable<Map<string, number>> {
    return this.http.get<Map<string, number>>(`${this.baseUrl}/prices`);
  }

  calculateTripCost(data: any): Observable<any> {
    // The 'any' type can be replaced with a specific DTO interface if defined
    // e.g., TripCalculationRequest, TripCalculationResponse
    return this.http.post<any>(`${this.baseUrl}/calculate-cost`, data);
  }
}
