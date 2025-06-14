import { Component } from '@angular/core';
import { ApiService } from '../../services/api.service'; // Adjust path
import { finalize } from 'rxjs/operators';

// Define an interface for the form model for clarity
interface TripFormModel {
  kilometersDriven: number | null;
  fuelUsagePer100Km: number | null;
  fuelType: string; // 'Diesel' or 'Super'
}

@Component({
  selector: 'app-trip-calculator',
  templateUrl: './trip-calculator.component.html',
  styleUrls: ['./trip-calculator.component.css']
})
export class TripCalculatorComponent {

  tripRequest: TripFormModel = {
    kilometersDriven: null,
    fuelUsagePer100Km: null,
    fuelType: 'Diesel' // Default value
  };

  // Available fuel types for the dropdown
  fuelTypes: string[] = ['Diesel', 'Super'];

  calculatedCost: number | null = null;
  isLoading = false;
  error: string | null = null;

  constructor(private apiService: ApiService) { }

  onSubmit(): void {
    if (this.tripRequest.kilometersDriven == null ||
        this.tripRequest.fuelUsagePer100Km == null ||
        !this.tripRequest.fuelType) {
      this.error = 'Please fill in all fields.';
      return;
    }
    if (this.tripRequest.kilometersDriven <= 0 || this.tripRequest.fuelUsagePer100Km <= 0) {
        this.error = 'Kilometers driven and fuel usage must be positive numbers.';
        return;
    }


    this.isLoading = true;
    this.error = null;
    this.calculatedCost = null;

    const requestPayload = {
      fuelType: this.tripRequest.fuelType,
      kilometersDriven: this.tripRequest.kilometersDriven,
      fuelUsagePer100Km: this.tripRequest.fuelUsagePer100Km
    };

    this.apiService.calculateTripCost(requestPayload)
      .pipe(
        finalize(() => this.isLoading = false)
      )
      .subscribe({
        next: (response) => {
          // Assuming response is { totalCost: number } as per Spring Boot DTO
          if (response && typeof response.totalCost === 'number') {
            this.calculatedCost = response.totalCost;
          } else {
            this.error = 'Invalid response from server for trip cost calculation.';
            console.warn('Received invalid trip cost response:', response);
          }
        },
        error: (err) => {
          console.error('Error calculating trip cost:', err);
          if (err.status === 400) { // Bad Request from backend validation or logic
             this.error = err.error?.message || err.error || 'Could not calculate trip cost. Please check your inputs or ensure prices are available.';
          } else {
            this.error = 'Failed to calculate trip cost. Please try again later.';
          }
          this.calculatedCost = null;
        }
      });
  }
}
