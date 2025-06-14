import { Component, OnInit } from '@angular/core';
import { ApiService } from '../../services/api.service'; // Adjusted path
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

@Component({
  selector: 'app-fuel-prices',
  templateUrl: './fuel-prices.component.html',
  styleUrls: ['./fuel-prices.component.css']
})
export class FuelPricesComponent implements OnInit {

  prices: Map<string, number> | null = null;
  isLoading = false;
  error: string | null = null;

  // Helper to get price keys for iteration in template
  get priceKeys(): string[] {
    return this.prices ? Array.from(this.prices.keys()) : [];
  }

  constructor(private apiService: ApiService) { }

  ngOnInit(): void {
    this.fetchPrices();
  }

  fetchPrices(): void {
    this.isLoading = true;
    this.error = null;
    this.apiService.getCurrentPrices()
      .pipe(
        finalize(() => this.isLoading = false)
      )
      .subscribe({
        next: (data) => {
          if (data && Object.keys(data).length > 0) {
            this.prices = data;
          } else {
            this.prices = null; // Or new Map();
            this.error = 'Fuel prices are currently unavailable or empty.';
            console.warn('Received null, empty or invalid prices data:', data);
          }
        },
        error: (err) => {
          console.error('Error fetching fuel prices:', err);
          this.error = 'Failed to load fuel prices. Please try again later.';
          this.prices = null; // Clear any stale data
        }
      });
  }
}
